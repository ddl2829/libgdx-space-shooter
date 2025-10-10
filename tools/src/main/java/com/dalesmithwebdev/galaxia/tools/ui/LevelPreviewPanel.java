package com.dalesmithwebdev.galaxia.tools.ui;

import com.dalesmithwebdev.galaxia.tools.model.Level;
import com.dalesmithwebdev.galaxia.tools.model.ObjectType;
import com.dalesmithwebdev.galaxia.tools.model.PlacedObject;
import com.dalesmithwebdev.galaxia.tools.model.TimedEvent;
import com.dalesmithwebdev.galaxia.tools.model.TriggerType;
import com.dalesmithwebdev.galaxia.tools.model.EventType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

/**
 * Level preview panel with libGDX sprite rendering
 * Uses libGDX TextureAtlas loaded and converted to JavaFX Images for rendering
 */
public class LevelPreviewPanel extends StackPane {
    private final Level level;
    private final Consumer<PlacedObject> onSelectCallback;
    private final ObjectProperty<PlacedObject> selectedObjectProperty = new SimpleObjectProperty<>();

    private boolean gridVisible = true;
    private boolean snapToGrid = true;
    private static final int GRID_SIZE = 20;
    private static final double VIEWPORT_WIDTH = 1000;
    private static final double VIEWPORT_HEIGHT = 800;

    private ObjectType placementMode = null;
    private Canvas canvas;
    private GraphicsContext gc;
    private double scrollOffsetY = 0;
    private PlacedObject hoveredObject = null;

    // Multi-selection state
    private final Set<PlacedObject> selectedObjects = new HashSet<>();
    private Rectangle2D selectionBox = null;
    private Point2D dragStart = null;
    private Point2D dragCurrent = null;
    private boolean isDraggingSelection = false;
    private boolean isDrawingSelectionBox = false;
    private boolean isTiledPlacement = false;
    private final Map<PlacedObject, Point2D> dragOffsets = new HashMap<>();
    private List<Point2D> tiledPreviewPositions = new ArrayList<>();

    // Copy/paste state
    private final List<PlacedObject> clipboard = new ArrayList<>();
    private Point2D clipboardOrigin = null; // Center point of copied objects
    private Point2D lastMousePosition = null; // Track mouse for paste target

    // Timed event interaction state
    private TimedEvent hoveredEvent = null;
    private TimedEvent draggedEvent = null;
    private boolean isDraggingEvent = false;
    private float draggedEventInitialY = 0;

    public LevelPreviewPanel(Level level, Consumer<PlacedObject> onSelectCallback) {
        this.level = level;
        this.onSelectCallback = onSelectCallback;

        // Initialize texture atlas
        initializeTextureAtlas();

        // Create JavaFX canvas for rendering
        canvas = new Canvas(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Add info label
        Label infoLabel = new Label("Level Preview (Game Sprites)\nClick to place objects, scroll to navigate");
        infoLabel.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-text-fill: white; -fx-padding: 5;");
        StackPane.setAlignment(infoLabel, Pos.TOP_LEFT);
        StackPane.setMargin(infoLabel, new Insets(10));

        getChildren().addAll(canvas, infoLabel);

        // Set up input handlers
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnMouseMoved(this::handleMouseMove);
        canvas.setOnScroll(this::handleScroll);

        // Enable keyboard input
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::handleKeyPressed);
        canvas.setOnMouseClicked(e -> canvas.requestFocus());

        // Initial render
        render();
    }

    private void initializeTextureAtlas() {
        try {
            // Use absolute path to atlas file
            String atlasAbsolutePath = "/Users/dale/games/galaxia/assets/ArcadeShooter.atlas";
            File atlasFile = new File(atlasAbsolutePath);

            if (atlasFile.exists()) {
                // Initialize TextureLoader with atlas path
                TextureLoader.initialize(atlasAbsolutePath);
                System.out.println("Texture atlas loaded successfully from: " + atlasAbsolutePath);
            } else {
                System.err.println("Atlas file not found at: " + atlasAbsolutePath);
                System.err.println("Sprites will not be displayed.");
            }
        } catch (Exception e) {
            System.err.println("Error loading texture atlas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setGridVisible(boolean visible) {
        this.gridVisible = visible;
        render();
    }

    public void setSnapToGrid(boolean snap) {
        this.snapToGrid = snap;
    }

    public void setPlacementMode(ObjectType type) {
        this.placementMode = type;
    }

    public ObjectProperty<PlacedObject> selectedObjectProperty() {
        return selectedObjectProperty;
    }

    public PlacedObject getSelectedObject() {
        return selectedObjectProperty.get();
    }

    public void setSelectedObject(PlacedObject obj) {
        selectedObjectProperty.set(obj);
        selectedObjects.clear();
        if (obj != null) {
            selectedObjects.add(obj);
        }
    }

    public Set<PlacedObject> getSelectedObjects() {
        return new HashSet<>(selectedObjects);
    }

    public void clearSelection() {
        selectedObjects.clear();
        selectedObjectProperty.set(null);
        render();
    }

    public void refresh() {
        render();
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        double mouseX = event.getX();
        double mouseY = event.getY() + scrollOffsetY;
        lastMousePosition = new Point2D(mouseX, mouseY);
        dragStart = new Point2D(mouseX, mouseY);
        dragCurrent = dragStart;

        // Check if clicking on a timed event marker first
        TimedEvent clickedEvent = findEventAt((float) event.getX(), (float) mouseY);
        if (clickedEvent != null && placementMode == null) {
            // Start dragging the event
            draggedEvent = clickedEvent;
            isDraggingEvent = true;
            draggedEventInitialY = getEventYPosition(clickedEvent);
            return;
        }

        PlacedObject clickedObject = findObjectAt((float) mouseX, (float) mouseY);

        if (placementMode != null) {
            // Start tiled placement mode if shift is held
            if (event.isShiftDown()) {
                isTiledPlacement = true;
                tiledPreviewPositions.clear();
            } else {
                // Single placement mode - place immediately on press
                float x = (float) (snapToGrid ? Math.round(mouseX / GRID_SIZE) * GRID_SIZE : mouseX);
                float y = (float) (snapToGrid ? Math.round(mouseY / GRID_SIZE) * GRID_SIZE : mouseY);
                PlacedObject newObj = new PlacedObject(placementMode, x, y);
                level.getObjects().add(newObj);
                render();
            }
        } else {
            // Selection/move mode
            if (clickedObject != null) {
                if (selectedObjects.contains(clickedObject)) {
                    // Start move mode for all selected objects
                    isDraggingSelection = true;
                    dragOffsets.clear();
                    for (PlacedObject obj : selectedObjects) {
                        dragOffsets.put(obj, new Point2D(obj.getX() - mouseX, obj.getY() - mouseY));
                    }
                } else {
                    // Select this object (add to selection if shift held)
                    if (!event.isShiftDown()) {
                        selectedObjects.clear();
                    }
                    selectedObjects.add(clickedObject);
                    selectedObjectProperty.set(clickedObject);
                    if (onSelectCallback != null) {
                        onSelectCallback.accept(clickedObject);
                    }

                    // Start move mode for this object
                    isDraggingSelection = true;
                    dragOffsets.clear();
                    for (PlacedObject obj : selectedObjects) {
                        dragOffsets.put(obj, new Point2D(obj.getX() - mouseX, obj.getY() - mouseY));
                    }
                }
            } else {
                // Clicked empty space - start selection box
                if (!event.isShiftDown()) {
                    selectedObjects.clear();
                    selectedObjectProperty.set(null);
                }
                isDrawingSelectionBox = true;
            }
            render();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY() + scrollOffsetY;
        dragCurrent = new Point2D(mouseX, mouseY);

        if (isDraggingEvent && draggedEvent != null) {
            // Update event position preview
            render();
        } else if (isTiledPlacement && placementMode != null) {
            // Update tiled placement preview
            tiledPreviewPositions = calculateTiledPositions(dragStart, dragCurrent, placementMode);
            render();
        } else if (isDraggingSelection) {
            // Update preview positions for selected objects (don't modify actual positions yet)
            render();
        } else if (isDrawingSelectionBox) {
            // Update selection box
            double minX = Math.min(dragStart.getX(), dragCurrent.getX());
            double minY = Math.min(dragStart.getY(), dragCurrent.getY());
            double maxX = Math.max(dragStart.getX(), dragCurrent.getX());
            double maxY = Math.max(dragStart.getY(), dragCurrent.getY());
            selectionBox = new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
            render();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        double mouseX = event.getX();
        double mouseY = event.getY() + scrollOffsetY;

        if (isDraggingEvent && draggedEvent != null) {
            // Calculate new trigger time based on Y position
            float newY = (float) mouseY;

            if (draggedEvent.getTriggerType() == TriggerType.POSITION_BASED) {
                // For position-based, Y is direct
                draggedEvent.setTriggerTime(newY);
            } else {
                // For time-based, convert Y to time
                float scrollSpeed = level.getLength() / level.getEstimatedTimeSeconds();
                float newTime = newY / scrollSpeed;
                draggedEvent.setTriggerTime(Math.max(0, newTime));
            }

            // Resort events
            level.sortTimedEvents();

            // Clear drag state
            isDraggingEvent = false;
            draggedEvent = null;
            render();
        } else if (isTiledPlacement && placementMode != null) {
            // Create all tiled objects
            for (Point2D pos : tiledPreviewPositions) {
                float x = (float) (snapToGrid ? Math.round(pos.getX() / GRID_SIZE) * GRID_SIZE : pos.getX());
                float y = (float) (snapToGrid ? Math.round(pos.getY() / GRID_SIZE) * GRID_SIZE : pos.getY());
                PlacedObject newObj = new PlacedObject(placementMode, x, y);
                level.getObjects().add(newObj);
            }
            tiledPreviewPositions.clear();
            isTiledPlacement = false;
        } else if (isDraggingSelection) {
            // Commit object positions
            for (PlacedObject obj : selectedObjects) {
                Point2D offset = dragOffsets.get(obj);
                if (offset != null) {
                    float newX = (float) (mouseX + offset.getX());
                    float newY = (float) (mouseY + offset.getY());
                    if (snapToGrid) {
                        newX = Math.round(newX / GRID_SIZE) * GRID_SIZE;
                        newY = Math.round(newY / GRID_SIZE) * GRID_SIZE;
                    }
                    obj.setX(newX);
                    obj.setY(newY);
                }
            }
            dragOffsets.clear();
            isDraggingSelection = false;
        } else if (isDrawingSelectionBox) {
            // Select all objects in box
            if (selectionBox != null) {
                for (PlacedObject obj : level.getObjects()) {
                    if (objectIntersectsBox(obj, selectionBox)) {
                        selectedObjects.add(obj);
                    }
                }
                // Update selected object property to first selected
                if (!selectedObjects.isEmpty()) {
                    selectedObjectProperty.set(selectedObjects.iterator().next());
                }
            }
            selectionBox = null;
            isDrawingSelectionBox = false;
        }

        dragStart = null;
        dragCurrent = null;
        render();
    }

    private void handleMouseMove(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY() + scrollOffsetY;
        lastMousePosition = new Point2D(mouseX, mouseY);
        hoveredObject = findObjectAt((float) mouseX, (float) mouseY);
        hoveredEvent = findEventAt((float) event.getX(), (float) mouseY);
        render();
    }

    private void handleScroll(ScrollEvent event) {
        scrollOffsetY -= event.getDeltaY();
        scrollOffsetY = Math.max(0, Math.min(level.getLength() - VIEWPORT_HEIGHT, scrollOffsetY));
        render();
    }

    private void handleKeyPressed(KeyEvent event) {
        boolean isCtrlOrCmd = event.isControlDown() || event.isMetaDown();

        if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
            deleteSelectedObjects();
            event.consume();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            // Cancel current operation
            if (isDraggingSelection || isDrawingSelectionBox || isTiledPlacement) {
                isDraggingSelection = false;
                isDrawingSelectionBox = false;
                isTiledPlacement = false;
                selectionBox = null;
                dragOffsets.clear();
                tiledPreviewPositions.clear();
                render();
            } else {
                clearSelection();
            }
            event.consume();
        } else if (isCtrlOrCmd && event.getCode() == KeyCode.C) {
            copySelectedObjects();
            event.consume();
        } else if (isCtrlOrCmd && event.getCode() == KeyCode.V) {
            pasteObjects();
            event.consume();
        } else if (isCtrlOrCmd && event.getCode() == KeyCode.X) {
            cutSelectedObjects();
            event.consume();
        } else if (isCtrlOrCmd && event.getCode() == KeyCode.D) {
            duplicateSelectedObjects();
            event.consume();
        }
    }

    private void deleteSelectedObjects() {
        if (selectedObjects.isEmpty()) {
            return;
        }

        // Confirmation for large selections
        if (selectedObjects.size() > 5) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Objects");
            alert.setHeaderText("Delete " + selectedObjects.size() + " objects?");
            alert.setContentText("This action cannot be undone.");

            Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != javafx.scene.control.ButtonType.OK) {
                return;
            }
        }

        // Remove all selected objects
        level.getObjects().removeAll(selectedObjects);
        selectedObjects.clear();
        selectedObjectProperty.set(null);
        render();
    }

    /**
     * Copy selected objects to clipboard
     */
    private void copySelectedObjects() {
        if (selectedObjects.isEmpty()) {
            return;
        }

        clipboard.clear();

        // Calculate center point of selection
        float sumX = 0, sumY = 0;
        for (PlacedObject obj : selectedObjects) {
            sumX += obj.getX();
            sumY += obj.getY();
        }
        clipboardOrigin = new Point2D(sumX / selectedObjects.size(),
                                       sumY / selectedObjects.size());

        // Deep copy all selected objects
        for (PlacedObject obj : selectedObjects) {
            PlacedObject copy = createDeepCopy(obj);
            clipboard.add(copy);
        }

        System.out.println("Copied " + clipboard.size() + " object(s)");
    }

    /**
     * Paste clipboard objects at mouse cursor position
     */
    private void pasteObjects() {
        if (clipboard.isEmpty()) {
            return;
        }

        // Get paste target (mouse position or viewport center)
        Point2D pasteTarget = lastMousePosition;
        if (pasteTarget == null) {
            // Default to center of viewport if no mouse position
            pasteTarget = new Point2D(VIEWPORT_WIDTH / 2,
                                      VIEWPORT_HEIGHT / 2 + scrollOffsetY);
        }

        // Calculate offset from clipboard origin to paste target
        double offsetX = pasteTarget.getX() - clipboardOrigin.getX();
        double offsetY = pasteTarget.getY() - clipboardOrigin.getY();

        // Clear current selection
        selectedObjects.clear();

        // Paste all objects with offset
        for (PlacedObject clipObj : clipboard) {
            PlacedObject pasted = createDeepCopy(clipObj);
            float newX = (float) (pasted.getX() + offsetX);
            float newY = (float) (pasted.getY() + offsetY);

            if (snapToGrid) {
                newX = Math.round(newX / GRID_SIZE) * GRID_SIZE;
                newY = Math.round(newY / GRID_SIZE) * GRID_SIZE;
            }

            pasted.setX(newX);
            pasted.setY(newY);

            level.getObjects().add(pasted);
            selectedObjects.add(pasted);
        }

        // Update selected object property
        if (!selectedObjects.isEmpty()) {
            selectedObjectProperty.set(selectedObjects.iterator().next());
        }

        System.out.println("Pasted " + clipboard.size() + " object(s) at (" +
                          (int)pasteTarget.getX() + ", " + (int)pasteTarget.getY() + ")");
        render();
    }

    /**
     * Cut selected objects (copy + delete)
     */
    private void cutSelectedObjects() {
        if (selectedObjects.isEmpty()) {
            return;
        }

        int count = selectedObjects.size();

        // Copy to clipboard
        copySelectedObjects();

        // Delete originals
        level.getObjects().removeAll(selectedObjects);
        selectedObjects.clear();
        selectedObjectProperty.set(null);

        System.out.println("Cut " + count + " object(s)");
        render();
    }

    /**
     * Duplicate selected objects with small offset
     */
    private void duplicateSelectedObjects() {
        if (selectedObjects.isEmpty()) {
            return;
        }

        // Copy to clipboard
        copySelectedObjects();

        // Save original clipboard origin
        Point2D originalOrigin = clipboardOrigin;

        // Paste with small offset (20 pixels right and down)
        clipboardOrigin = new Point2D(
            clipboardOrigin.getX() + 20,
            clipboardOrigin.getY() + 20
        );

        pasteObjects();

        // Restore clipboard origin for potential future pastes
        clipboardOrigin = originalOrigin;

        System.out.println("Duplicated " + clipboard.size() + " object(s)");
    }

    /**
     * Create a deep copy of a PlacedObject
     */
    private PlacedObject createDeepCopy(PlacedObject original) {
        PlacedObject copy = new PlacedObject(
            original.getType(),
            original.getX(),
            original.getY()
        );

        // Copy all properties
        copy.setScale(original.getScale());
        copy.setSpeed(original.getSpeed());
        copy.setMovementPattern(original.getMovementPattern());
        copy.setDirectionX(original.getDirectionX());
        copy.setDirectionY(original.getDirectionY());
        copy.setRotationSpeed(original.getRotationSpeed());
        copy.setHealth(original.getHealth());
        copy.setFireRate(original.getFireRate());
        copy.setHasLasers(original.isHasLasers());
        copy.setHasMissiles(original.isHasMissiles());
        copy.setHasShield(original.isHasShield());
        copy.setSpawnDelay(original.getSpawnDelay());

        return copy;
    }

    private PlacedObject findObjectAt(float x, float y) {
        for (int i = level.getObjects().size() - 1; i >= 0; i--) {
            PlacedObject obj = level.getObjects().get(i);
            float width = obj.getType().getWidth() * obj.getScale();
            float height = obj.getType().getHeight() * obj.getScale();

            if (x >= obj.getX() - width/2 && x <= obj.getX() + width/2 &&
                y >= obj.getY() - height/2 && y <= obj.getY() + height/2) {
                return obj;
            }
        }
        return null;
    }

    private TimedEvent findEventAt(float x, float y) {
        if (level.getTimedEvents() == null || level.getTimedEvents().isEmpty()) {
            return null;
        }

        double markerSize = 30;
        double markerX = 10;

        for (TimedEvent event : level.getTimedEvents()) {
            float eventY = getEventYPosition(event);
            float adjustedY = (float) (eventY - scrollOffsetY);

            // Check if click is within the event marker circle
            if (x >= markerX && x <= markerX + markerSize &&
                y >= adjustedY - markerSize / 2 && y <= adjustedY + markerSize / 2) {
                return event;
            }
        }
        return null;
    }

    private float getEventYPosition(TimedEvent event) {
        if (event.getTriggerType() == TriggerType.POSITION_BASED) {
            return event.getTriggerTime();
        } else {
            // For time-based events, estimate Y position based on level progression
            float scrollSpeed = level.getLength() / level.getEstimatedTimeSeconds();
            return event.getTriggerTime() * scrollSpeed;
        }
    }

    private boolean objectIntersectsBox(PlacedObject obj, Rectangle2D box) {
        float width = obj.getType().getWidth() * obj.getScale();
        float height = obj.getType().getHeight() * obj.getScale();
        float objMinX = obj.getX() - width / 2;
        float objMinY = obj.getY() - height / 2;
        float objMaxX = obj.getX() + width / 2;
        float objMaxY = obj.getY() + height / 2;

        return !(objMaxX < box.getMinX() || objMinX > box.getMaxX() ||
                 objMaxY < box.getMinY() || objMinY > box.getMaxY());
    }

    private List<Point2D> calculateTiledPositions(Point2D start, Point2D end, ObjectType type) {
        List<Point2D> positions = new ArrayList<>();

        if (start == null || end == null) {
            return positions;
        }

        float spacing = type.getWidth() * 1.5f;

        // Calculate distance and direction
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Need at least some distance to create a line
        if (distance < spacing / 2) {
            positions.add(start);
            return positions;
        }

        // Place objects along the line
        int count = Math.max(1, (int)(distance / spacing));
        for (int i = 0; i <= count; i++) {
            double t = count > 0 ? (double)i / count : 0;
            positions.add(new Point2D(
                start.getX() + dx * t,
                start.getY() + dy * t
            ));
        }

        return positions;
    }

    private void render() {
        // Clear canvas
        gc.setFill(Color.rgb(26, 26, 38));
        gc.fillRect(0, 0, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        // Draw grid
        if (gridVisible) {
            drawGrid();
        }

        // Draw level boundary
        drawLevelBoundary();

        // Draw placed objects
        for (PlacedObject obj : level.getObjects()) {
            boolean isSelected = selectedObjects.contains(obj);

            // If dragging, draw at preview position
            if (isDraggingSelection && isSelected && dragCurrent != null) {
                Point2D offset = dragOffsets.get(obj);
                if (offset != null) {
                    float previewX = (float) (dragCurrent.getX() + offset.getX());
                    float previewY = (float) (dragCurrent.getY() + offset.getY());
                    drawObjectAtPosition(obj, previewX, previewY, true, 0.5);
                }
            } else {
                drawObject(obj, isSelected);
            }
        }

        // Draw timed event markers
        drawTimedEventMarkers();

        // Draw multi-selection highlights
        for (PlacedObject obj : selectedObjects) {
            drawSelectionHighlight(obj);
        }

        // Draw selection box
        if (isDrawingSelectionBox && selectionBox != null) {
            drawSelectionBox(selectionBox);
        }

        // Draw tiled placement preview
        if (isTiledPlacement && !tiledPreviewPositions.isEmpty() && placementMode != null) {
            drawTiledPlacementPreview(tiledPreviewPositions, placementMode);
        }

        // Draw hover highlight
        if (hoveredObject != null && !selectedObjects.contains(hoveredObject)) {
            drawHoverHighlight(hoveredObject);
        }
    }

    private void drawGrid() {
        gc.setStroke(Color.rgb(77, 77, 89, 0.5));
        gc.setLineWidth(0.5);

        // Vertical lines
        for (int x = 0; x < VIEWPORT_WIDTH; x += GRID_SIZE) {
            gc.strokeLine(x, 0, x, VIEWPORT_HEIGHT);
        }

        // Horizontal lines
        double startY = Math.floor(scrollOffsetY / GRID_SIZE) * GRID_SIZE - scrollOffsetY;
        for (double y = startY; y < VIEWPORT_HEIGHT; y += GRID_SIZE) {
            gc.strokeLine(0, y, VIEWPORT_WIDTH, y);
        }
    }

    private void drawLevelBoundary() {
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.strokeRect(0, -scrollOffsetY, VIEWPORT_WIDTH, level.getLength());
    }

    private void drawObject(PlacedObject obj, boolean selected) {
        float adjustedY = (float) (obj.getY() - scrollOffsetY);
        float width = obj.getType().getWidth() * obj.getScale();
        float height = obj.getType().getHeight() * obj.getScale();

        // Try to load sprite image
        Image sprite = TextureLoader.getImage(obj.getType().getTextureRegionName());

        if (sprite != null) {
            // Draw actual sprite
            if (selected) {
                // Apply cyan tint for selection
                gc.setGlobalAlpha(0.7);
                gc.setFill(Color.CYAN);
                gc.fillRect(obj.getX() - width/2, adjustedY - height/2, width, height);
                gc.setGlobalAlpha(1.0);
            }

            gc.drawImage(sprite,
                obj.getX() - width/2,
                adjustedY - height/2,
                width,
                height);
        } else {
            // Fallback to colored rectangle if sprite not found
            Color color = getObjectColor(obj.getType());
            if (selected) {
                color = Color.CYAN;
            }

            gc.setFill(color);
            gc.fillRect(obj.getX() - width/2, adjustedY - height/2, width, height);

            // Draw type label
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(8));
            String label = obj.getType().name().substring(0, Math.min(3, obj.getType().name().length()));
            gc.fillText(label, obj.getX() - width/4, adjustedY);
        }
    }

    private Color getObjectColor(ObjectType type) {
        if (type.isMeteor()) {
            return Color.BROWN;
        } else if (type.isEnemy()) {
            return type.isBoss() ? Color.RED : Color.ORANGE;
        } else if (type.isPowerup()) {
            return Color.GREEN;
        }
        return Color.GRAY;
    }

    private void drawSelectionHighlight(PlacedObject obj) {
        float adjustedY = (float) (obj.getY() - scrollOffsetY);
        float width = obj.getType().getWidth() * obj.getScale();
        float height = obj.getType().getHeight() * obj.getScale();

        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokeRect(obj.getX() - width/2, adjustedY - height/2, width, height);
    }

    private void drawHoverHighlight(PlacedObject obj) {
        float adjustedY = (float) (obj.getY() - scrollOffsetY);
        float width = obj.getType().getWidth() * obj.getScale();
        float height = obj.getType().getHeight() * obj.getScale();

        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(1);
        gc.strokeRect(obj.getX() - width/2, adjustedY - height/2, width, height);
    }

    private void drawTimedEventMarkers() {
        if (level.getTimedEvents() == null || level.getTimedEvents().isEmpty()) {
            return;
        }

        for (TimedEvent event : level.getTimedEvents()) {
            // If this event is being dragged, use the current drag position
            float yPosition;
            if (isDraggingEvent && event == draggedEvent && dragCurrent != null) {
                yPosition = (float) dragCurrent.getY();
            } else {
                yPosition = getEventYPosition(event);
            }

            float adjustedY = (float) (yPosition - scrollOffsetY);

            // Only draw if visible in viewport
            if (adjustedY < -50 || adjustedY > VIEWPORT_HEIGHT + 50) {
                continue;
            }

            // Get color based on event type
            Color markerColor = getEventColor(event.getEventType());

            // Draw event marker (circle with icon)
            double markerSize = 30;
            double markerX = 10; // Left side of viewport

            // Apply visual feedback for hover or drag
            boolean isHovered = (event == hoveredEvent);
            boolean isDragged = (event == draggedEvent);

            if (isDragged) {
                // Draw larger semi-transparent shadow for dragged event
                gc.setGlobalAlpha(0.3);
                gc.setFill(markerColor);
                gc.fillOval(markerX - 5, adjustedY - markerSize / 2 - 5, markerSize + 10, markerSize + 10);
                gc.setGlobalAlpha(1.0);
            } else if (isHovered) {
                // Draw glow effect for hovered event
                gc.setGlobalAlpha(0.5);
                gc.setFill(Color.YELLOW);
                gc.fillOval(markerX - 3, adjustedY - markerSize / 2 - 3, markerSize + 6, markerSize + 6);
                gc.setGlobalAlpha(1.0);
            }

            // Draw background circle
            gc.setFill(markerColor);
            gc.fillOval(markerX, adjustedY - markerSize / 2, markerSize, markerSize);

            // Draw white border for dragged or hovered events
            if (isDragged || isHovered) {
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeOval(markerX, adjustedY - markerSize / 2, markerSize, markerSize);
            }

            // Draw icon text
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(16));
            gc.fillText(event.getEventType().getIcon(), markerX + 7, adjustedY + 5);

            // Draw connecting line to center
            gc.setStroke(isDragged ? Color.YELLOW : markerColor);
            gc.setLineWidth(isDragged ? 2 : 1);
            gc.setLineDashes(5, 5);
            gc.strokeLine(markerX + markerSize, adjustedY, VIEWPORT_WIDTH / 4, adjustedY);
            gc.setLineDashes(); // Reset to solid line

            // Draw trigger info label
            gc.setFill(isDragged ? Color.YELLOW : Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(isDragged ? 11 : 10));
            String triggerLabel = event.getTriggerDisplay();
            gc.fillText(triggerLabel, markerX + markerSize + 5, adjustedY - 5);

            // Draw event summary on right side if space allows
            if (adjustedY > 20 && adjustedY < VIEWPORT_HEIGHT - 20) {
                gc.setFill(isDragged ? Color.YELLOW : Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font(isDragged ? 10 : 9));
                String summary = event.getSummary();
                if (summary.length() > 40) {
                    summary = summary.substring(0, 37) + "...";
                }
                gc.fillText(summary, VIEWPORT_WIDTH / 4 + 5, adjustedY + 4);
            }

            // Draw helper text for dragged events
            if (isDragged) {
                gc.setFill(Color.YELLOW);
                gc.setFont(javafx.scene.text.Font.font(10));
                gc.fillText("Drag to adjust timing", VIEWPORT_WIDTH / 2 - 50, adjustedY - 20);
            } else if (isHovered) {
                gc.setFill(Color.YELLOW);
                gc.setFont(javafx.scene.text.Font.font(9));
                gc.fillText("Click and drag to move", VIEWPORT_WIDTH / 2 - 50, adjustedY - 15);
            }
        }
    }

    private Color getEventColor(EventType type) {
        switch (type) {
            case NOTIFICATION:
                return Color.rgb(52, 152, 219); // Blue
            case ITEM_SPAWN:
                return Color.rgb(46, 204, 113); // Green
            case ENEMY_WAVE:
                return Color.rgb(231, 76, 60); // Red
            case ENVIRONMENTAL_CHANGE:
                return Color.rgb(155, 89, 182); // Purple
            default:
                return Color.GRAY;
        }
    }

    private void drawObjectAtPosition(PlacedObject obj, float x, float y, boolean selected, double alpha) {
        float adjustedY = (float) (y - scrollOffsetY);
        float width = obj.getType().getWidth() * obj.getScale();
        float height = obj.getType().getHeight() * obj.getScale();

        // Save current alpha
        double originalAlpha = gc.getGlobalAlpha();
        gc.setGlobalAlpha(alpha);

        // Try to load sprite image
        Image sprite = TextureLoader.getImage(obj.getType().getTextureRegionName());

        if (sprite != null) {
            // Draw actual sprite
            if (selected) {
                // Apply cyan tint for selection
                gc.setFill(Color.CYAN);
                gc.fillRect(x - width/2, adjustedY - height/2, width, height);
            }

            gc.drawImage(sprite,
                x - width/2,
                adjustedY - height/2,
                width,
                height);
        } else {
            // Fallback to colored rectangle if sprite not found
            Color color = getObjectColor(obj.getType());
            if (selected) {
                color = Color.CYAN;
            }

            gc.setFill(color);
            gc.fillRect(x - width/2, adjustedY - height/2, width, height);

            // Draw type label
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(8));
            String label = obj.getType().name().substring(0, Math.min(3, obj.getType().name().length()));
            gc.fillText(label, x - width/4, adjustedY);
        }

        // Restore alpha
        gc.setGlobalAlpha(originalAlpha);
    }

    private void drawSelectionBox(Rectangle2D box) {
        double adjustedY = box.getMinY() - scrollOffsetY;
        double adjustedHeight = box.getHeight();

        // Semi-transparent fill
        gc.setFill(Color.rgb(100, 200, 255, 0.2));
        gc.fillRect(box.getMinX(), adjustedY, box.getWidth(), adjustedHeight);

        // Dashed border
        gc.setStroke(Color.rgb(100, 200, 255));
        gc.setLineWidth(2);
        gc.setLineDashes(5, 5);
        gc.strokeRect(box.getMinX(), adjustedY, box.getWidth(), adjustedHeight);
        gc.setLineDashes(); // Reset to solid
    }

    private void drawTiledPlacementPreview(List<Point2D> positions, ObjectType type) {
        double originalAlpha = gc.getGlobalAlpha();
        gc.setGlobalAlpha(0.3);

        for (Point2D pos : positions) {
            float adjustedY = (float) (pos.getY() - scrollOffsetY);
            float width = type.getWidth();
            float height = type.getHeight();

            // Try to load sprite image
            Image sprite = TextureLoader.getImage(type.getTextureRegionName());

            if (sprite != null) {
                gc.drawImage(sprite,
                    pos.getX() - width/2,
                    adjustedY - height/2,
                    width,
                    height);
            } else {
                // Fallback to colored rectangle
                gc.setFill(getObjectColor(type));
                gc.fillRect(pos.getX() - width/2, adjustedY - height/2, width, height);
            }

            // Draw small indicator at center
            gc.setFill(Color.WHITE);
            gc.fillOval(pos.getX() - 2, adjustedY - 2, 4, 4);
        }

        gc.setGlobalAlpha(originalAlpha);
    }
}
