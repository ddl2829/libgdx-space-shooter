package com.dalesmithwebdev.galaxia.tools.ui;

import com.dalesmithwebdev.galaxia.tools.model.Level;
import com.dalesmithwebdev.galaxia.tools.model.ObjectType;
import com.dalesmithwebdev.galaxia.tools.model.PlacedObject;
import com.dalesmithwebdev.galaxia.tools.service.LevelService;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * Level editor window with palette, preview, and configuration panels
 */
public class LevelEditorWindow extends Stage {
    private final Level level;
    private final LevelService levelService;
    private final Runnable onSaveCallback;

    // UI components
    private LevelPreviewPanel previewPanel;
    private ObjectPalettePanel palettePanel;
    private ObjectConfigPanel configPanel;
    private BossConfigPanel bossConfigPanel;
    private LevelMetadataPanel metadataPanel;
    private TimedEventsPanel eventsPanel;

    private PlacedObject selectedObject = null;

    public LevelEditorWindow(Level level, LevelService levelService, Runnable onSaveCallback) {
        this.level = level;
        this.levelService = levelService;
        this.onSaveCallback = onSaveCallback;

        setTitle("Level Editor - " + level.getName());
        setWidth(1400);
        setHeight(900);

        BorderPane root = new BorderPane();

        // Center: split between palette and preview
        SplitPane centerSplit = new SplitPane();
        centerSplit.setOrientation(Orientation.HORIZONTAL);

        // Left: Object palette
        palettePanel = new ObjectPalettePanel(this::onPaletteItemSelected);
        centerSplit.getItems().add(palettePanel);

        // Center: Level preview
        previewPanel = new LevelPreviewPanel(level, this::onObjectSelected);
        centerSplit.getItems().add(previewPanel);

        centerSplit.setDividerPositions(0.15);
        root.setCenter(centerSplit);

        // Right: Configuration panel
        TabPane rightPanel = new TabPane();
        rightPanel.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        rightPanel.setPrefWidth(300);

        // Object config tab
        configPanel = new ObjectConfigPanel(this::onObjectUpdated);
        Tab objectTab = new Tab("Object", configPanel);

        // Boss config tab
        bossConfigPanel = new BossConfigPanel(level.getBossConfig(), this::updateLevel);
        Tab bossTab = new Tab("Boss", bossConfigPanel);

        // Level metadata tab
        metadataPanel = new LevelMetadataPanel(level, this::updateLevel);
        Tab metadataTab = new Tab("Level", metadataPanel);

        // Timed events tab
        eventsPanel = new TimedEventsPanel(level, this::updateLevel);
        Tab eventsTab = new Tab("Events", eventsPanel);

        rightPanel.getTabs().addAll(metadataTab, objectTab, bossTab, eventsTab);
        root.setRight(rightPanel);

        // Top toolbar (created after previewPanel is initialized)
        root.setTop(createToolbar());

        Scene scene = new Scene(root);
        setScene(scene);

        // Initial update
        updateLevel();
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> saveLevel());

        Button validateBtn = new Button("Validate");
        validateBtn.setOnAction(e -> validateLevel());

        Button clearBtn = new Button("Clear Level");
        clearBtn.setOnAction(e -> clearLevel());

        CheckBox gridToggle = new CheckBox("Show Grid");
        gridToggle.setSelected(true);
        gridToggle.selectedProperty().addListener((obs, old, newVal) ->
            previewPanel.setGridVisible(newVal));

        CheckBox snapToggle = new CheckBox("Snap to Grid");
        snapToggle.setSelected(true);
        snapToggle.selectedProperty().addListener((obs, old, newVal) ->
            previewPanel.setSnapToGrid(newVal));

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setOnAction(e -> deleteSelectedObjects());
        deleteBtn.disableProperty().bind(previewPanel.selectedObjectProperty().isNull());

        // Meteor generation tools
        Button randomMeteorsBtn = new Button("Random Meteors");
        randomMeteorsBtn.setOnAction(e -> showRandomMeteorsDialog());

        CheckBox guardRailsToggle = new CheckBox("Guard Rails");
        guardRailsToggle.setSelected(false);
        guardRailsToggle.selectedProperty().addListener((obs, old, newVal) ->
            toggleGuardRails(newVal));

        toolbar.getItems().addAll(
            saveBtn,
            validateBtn,
            clearBtn,
            new Separator(),
            gridToggle,
            snapToggle,
            new Separator(),
            deleteBtn,
            new Separator(),
            randomMeteorsBtn,
            guardRailsToggle
        );

        return toolbar;
    }

    private void onPaletteItemSelected(ObjectType type) {
        previewPanel.setPlacementMode(type);
    }

    private void onObjectSelected(PlacedObject obj) {
        selectedObject = obj;
        configPanel.setObject(obj);
    }

    private void onObjectUpdated() {
        updateLevel();
        previewPanel.refresh();
    }

    private void deleteSelectedObjects() {
        var selectedObjects = previewPanel.getSelectedObjects();
        if (!selectedObjects.isEmpty()) {
            level.getObjects().removeAll(selectedObjects);
            previewPanel.clearSelection();
            updateLevel();
            previewPanel.refresh();
        }
    }

    private void clearLevel() {
        int objectCount = level.getObjects().size();
        if (objectCount == 0) {
            showInfo("Level Empty", "Level has no objects to clear.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Level");
        alert.setHeaderText("Remove all " + objectCount + " objects from level?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                level.getObjects().clear();
                previewPanel.clearSelection();
                updateLevel();
                previewPanel.refresh();
                showInfo("Level Cleared", "All objects removed from level.");
            }
        });
    }

    private void updateLevel() {
        level.calculateDifficulty();
        metadataPanel.refresh();
        eventsPanel.refresh();
        previewPanel.refresh();
    }

    private void saveLevel() {
        try {
            List<String> errors = levelService.validateLevel(level);
            if (!errors.isEmpty()) {
                showError("Validation Failed", String.join("\n", errors));
                return;
            }

            levelService.saveLevel(level);
            showInfo("Level Saved", "Level saved successfully: " + level.getName());
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
        } catch (Exception e) {
            showError("Save Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    private void validateLevel() {
        List<String> errors = levelService.validateLevel(level);
        if (errors.isEmpty()) {
            showInfo("Validation Passed", "Level is valid and ready to save.");
        } else {
            showError("Validation Failed", String.join("\n", errors));
        }
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showRandomMeteorsDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Generate Random Meteors");
        dialog.setHeaderText("Configure random meteor placement");

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField densityField = new TextField("15");
        TextField largePctField = new TextField("30");
        TextField minYField = new TextField("0");
        TextField maxYField = new TextField(String.valueOf((int) level.getLength()));

        grid.add(new Label("Fill Density (%):"), 0, 0);
        grid.add(densityField, 1, 0);
        grid.add(new Label("Large Meteor % (0-100):"), 0, 1);
        grid.add(largePctField, 1, 1);
        grid.add(new Label("Min Y Position:"), 0, 2);
        grid.add(minYField, 1, 2);
        grid.add(new Label("Max Y Position:"), 0, 3);
        grid.add(maxYField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    float density = Float.parseFloat(densityField.getText());
                    float largePct = Float.parseFloat(largePctField.getText());
                    float minY = Float.parseFloat(minYField.getText());
                    float maxY = Float.parseFloat(maxYField.getText());

                    generateRandomMeteors(density, largePct, minY, maxY);
                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Please enter valid numbers.");
                }
            }
        });
    }

    private void generateRandomMeteors(float densityPct, float largeMeteorPct, float minY, float maxY) {
        // Calculate number of meteors based on density
        float levelArea = 1000 * (maxY - minY); // viewport width * level section height
        float meteorArea = 28 * 28; // average meteor size
        int maxMeteors = (int) (levelArea / meteorArea);
        int meteorCount = (int) (maxMeteors * (densityPct / 100f));

        // Clamp large meteor percentage
        largeMeteorPct = Math.max(0, Math.min(100, largeMeteorPct));

        int addedCount = 0;
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < meteorCount; i++) {
            // Randomly choose meteor type
            ObjectType meteorType = (random.nextFloat() * 100 < largeMeteorPct) ?
                ObjectType.METEOR_LARGE : ObjectType.METEOR_SMALL;

            // Random position within bounds
            float x = 50 + random.nextFloat() * 900; // Keep within 50-950 to avoid edges
            float y = minY + random.nextFloat() * (maxY - minY);

            // Random scale variation (0.8 to 1.2)
            float scale = 0.8f + random.nextFloat() * 0.4f;

            // Random rotation speed
            float rotationSpeed = -30 + random.nextFloat() * 60;

            PlacedObject meteor = new PlacedObject(meteorType, x, y);
            meteor.setScale(scale);
            meteor.setRotationSpeed(rotationSpeed);

            level.getObjects().add(meteor);
            addedCount++;
        }

        updateLevel();
        previewPanel.refresh();
        showInfo("Meteors Generated", "Added " + addedCount + " random meteors to level.");
    }

    private void toggleGuardRails(boolean enabled) {
        if (enabled) {
            generateGuardRails();
        } else {
            removeGuardRails();
        }
    }

    private void generateGuardRails() {
        // Remove existing guard rails first
        removeGuardRails();

        int spacing = 40; // Spacing between meteors
        float leftX = 15; // Left edge
        float rightX = 985; // Right edge

        // Generate small meteors along both edges
        for (float y = 0; y < level.getLength(); y += spacing) {
            // Left guard rail
            PlacedObject leftMeteor = new PlacedObject(ObjectType.METEOR_SMALL, leftX, y);
            leftMeteor.setScale(0.8f);
            leftMeteor.setSpawnDelay(-1); // Mark as guard rail with negative spawn delay
            level.getObjects().add(leftMeteor);

            // Right guard rail
            PlacedObject rightMeteor = new PlacedObject(ObjectType.METEOR_SMALL, rightX, y);
            rightMeteor.setScale(0.8f);
            rightMeteor.setSpawnDelay(-1); // Mark as guard rail
            level.getObjects().add(rightMeteor);
        }

        updateLevel();
        previewPanel.refresh();
        showInfo("Guard Rails Generated", "Added guard rails along level edges.");
    }

    private void removeGuardRails() {
        // Remove all objects marked as guard rails (spawn delay = -1)
        List<PlacedObject> toRemove = new java.util.ArrayList<>();
        for (PlacedObject obj : level.getObjects()) {
            if (obj.getSpawnDelay() == -1) {
                toRemove.add(obj);
            }
        }

        if (!toRemove.isEmpty()) {
            level.getObjects().removeAll(toRemove);
            updateLevel();
            previewPanel.refresh();
        }
    }
}
