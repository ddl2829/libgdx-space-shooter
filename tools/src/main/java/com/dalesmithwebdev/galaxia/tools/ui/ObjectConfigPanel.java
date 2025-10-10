package com.dalesmithwebdev.galaxia.tools.ui;

import com.dalesmithwebdev.galaxia.tools.model.MovementPattern;
import com.dalesmithwebdev.galaxia.tools.model.PlacedObject;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Configuration panel for selected objects
 */
public class ObjectConfigPanel extends ScrollPane {
    private final Runnable onUpdateCallback;

    private VBox content;
    private PlacedObject currentObject = null;

    // Common fields
    private TextField xField;
    private TextField yField;
    private Slider scaleSlider;
    private Label scaleLabel;

    // Movement fields
    private ComboBox<MovementPattern> movementCombo;
    private TextField speedField;
    private TextField directionXField;
    private TextField directionYField;
    private TextField rotationSpeedField;

    // Enemy fields
    private TextField healthField;
    private TextField fireRateField;
    private CheckBox hasLasersCheck;
    private CheckBox hasMissilesCheck;
    private CheckBox hasShieldCheck;

    // Timing fields
    private TextField spawnDelayField;

    public ObjectConfigPanel(Runnable onUpdateCallback) {
        this.onUpdateCallback = onUpdateCallback;

        content = new VBox(10);
        content.setPadding(new Insets(10));

        setContent(content);
        setFitToWidth(true);

        showNoSelectionMessage();
    }

    public void setObject(PlacedObject obj) {
        this.currentObject = obj;
        if (obj == null) {
            showNoSelectionMessage();
        } else {
            buildConfigUI();
        }
    }

    private void showNoSelectionMessage() {
        content.getChildren().clear();
        Label label = new Label("No object selected");
        label.setStyle("-fx-text-fill: gray;");
        content.getChildren().add(label);
    }

    private void buildConfigUI() {
        content.getChildren().clear();

        // Object type label
        Label typeLabel = new Label("Type: " + currentObject.getType().getDisplayName());
        typeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        content.getChildren().add(typeLabel);
        content.getChildren().add(new Separator());

        // Position section
        GridPane posGrid = new GridPane();
        posGrid.setHgap(10);
        posGrid.setVgap(5);

        xField = createNumberField(currentObject.getX());
        yField = createNumberField(currentObject.getY());

        posGrid.add(new Label("X:"), 0, 0);
        posGrid.add(xField, 1, 0);
        posGrid.add(new Label("Y:"), 0, 1);
        posGrid.add(yField, 1, 1);

        content.getChildren().add(new Label("Position"));
        content.getChildren().add(posGrid);
        content.getChildren().add(new Separator());

        // Scale section
        scaleSlider = new Slider(0.1, 3.0, currentObject.getScale());
        scaleSlider.setShowTickLabels(false);
        scaleSlider.setShowTickMarks(false);
        scaleLabel = new Label(String.format("%.2f", currentObject.getScale()));
        scaleSlider.valueProperty().addListener((obs, old, newVal) -> {
            scaleLabel.setText(String.format("%.2f", newVal.doubleValue()));
        });

        content.getChildren().add(new Label("Scale"));
        content.getChildren().add(scaleSlider);
        content.getChildren().add(scaleLabel);
        content.getChildren().add(new Separator());

        // Movement section (for meteors and enemies)
        if (currentObject.getType().isMeteor() || currentObject.getType().isEnemy()) {
            buildMovementSection();
        }

        // Enemy-specific section
        if (currentObject.getType().isEnemy()) {
            buildEnemySection();
        }

        // Timing section
        buildTimingSection();

        // Apply button
        Button applyBtn = new Button("Apply Changes");
        applyBtn.setMaxWidth(Double.MAX_VALUE);
        applyBtn.setOnAction(e -> applyChanges());
        content.getChildren().add(applyBtn);
    }

    private void buildMovementSection() {
        content.getChildren().add(new Label("Movement"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        movementCombo = new ComboBox<>();
        movementCombo.getItems().addAll(MovementPattern.values());
        movementCombo.setValue(currentObject.getMovementPattern());
        movementCombo.setMaxWidth(Double.MAX_VALUE);

        speedField = createNumberField(currentObject.getSpeed());
        directionXField = createNumberField(currentObject.getDirectionX());
        directionYField = createNumberField(currentObject.getDirectionY());
        rotationSpeedField = createNumberField(currentObject.getRotationSpeed());

        grid.add(new Label("Pattern:"), 0, 0);
        grid.add(movementCombo, 1, 0);
        grid.add(new Label("Speed:"), 0, 1);
        grid.add(speedField, 1, 1);
        grid.add(new Label("Dir X:"), 0, 2);
        grid.add(directionXField, 1, 2);
        grid.add(new Label("Dir Y:"), 0, 3);
        grid.add(directionYField, 1, 3);
        grid.add(new Label("Rotation:"), 0, 4);
        grid.add(rotationSpeedField, 1, 4);

        content.getChildren().add(grid);
        content.getChildren().add(new Separator());
    }

    private void buildEnemySection() {
        content.getChildren().add(new Label("Enemy Properties"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        healthField = createNumberField(currentObject.getHealth());
        fireRateField = createNumberField(currentObject.getFireRate());

        grid.add(new Label("Health:"), 0, 0);
        grid.add(healthField, 1, 0);
        grid.add(new Label("Fire Rate (ms):"), 0, 1);
        grid.add(fireRateField, 1, 1);

        content.getChildren().add(grid);

        hasLasersCheck = new CheckBox("Has Lasers");
        hasLasersCheck.setSelected(currentObject.isHasLasers());

        hasMissilesCheck = new CheckBox("Has Missiles");
        hasMissilesCheck.setSelected(currentObject.isHasMissiles());

        hasShieldCheck = new CheckBox("Has Shield");
        hasShieldCheck.setSelected(currentObject.isHasShield());

        content.getChildren().addAll(hasLasersCheck, hasMissilesCheck, hasShieldCheck);
        content.getChildren().add(new Separator());
    }

    private void buildTimingSection() {
        content.getChildren().add(new Label("Timing"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        spawnDelayField = createNumberField(currentObject.getSpawnDelay());

        grid.add(new Label("Spawn Delay (s):"), 0, 0);
        grid.add(spawnDelayField, 1, 0);

        content.getChildren().add(grid);
        content.getChildren().add(new Separator());
    }

    private TextField createNumberField(Number value) {
        TextField field = new TextField(value.toString());
        field.setPrefWidth(100);
        return field;
    }

    private void applyChanges() {
        if (currentObject == null) return;

        try {
            // Apply position
            currentObject.setX(Float.parseFloat(xField.getText()));
            currentObject.setY(Float.parseFloat(yField.getText()));

            // Apply scale
            currentObject.setScale((float) scaleSlider.getValue());

            // Apply movement (if applicable)
            if (movementCombo != null) {
                currentObject.setMovementPattern(movementCombo.getValue());
                currentObject.setSpeed(Float.parseFloat(speedField.getText()));
                currentObject.setDirectionX(Float.parseFloat(directionXField.getText()));
                currentObject.setDirectionY(Float.parseFloat(directionYField.getText()));
                currentObject.setRotationSpeed(Float.parseFloat(rotationSpeedField.getText()));
            }

            // Apply enemy properties (if applicable)
            if (healthField != null) {
                currentObject.setHealth(Integer.parseInt(healthField.getText()));
                currentObject.setFireRate(Integer.parseInt(fireRateField.getText()));
                currentObject.setHasLasers(hasLasersCheck.isSelected());
                currentObject.setHasMissiles(hasMissilesCheck.isSelected());
                currentObject.setHasShield(hasShieldCheck.isSelected());
            }

            // Apply timing
            currentObject.setSpawnDelay(Float.parseFloat(spawnDelayField.getText()));

            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }

            showSuccess();
        } catch (NumberFormatException e) {
            showError("Invalid number format: " + e.getMessage());
        }
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Changes Applied");
        alert.setContentText("Object configuration updated successfully");
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to Apply Changes");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
