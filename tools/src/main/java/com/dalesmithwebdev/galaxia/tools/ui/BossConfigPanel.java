package com.dalesmithwebdev.galaxia.tools.ui;

import com.dalesmithwebdev.galaxia.tools.model.BossConfiguration;
import com.dalesmithwebdev.galaxia.tools.model.MovementPattern;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Configuration panel for boss enemies
 */
public class BossConfigPanel extends ScrollPane {
    private final BossConfiguration bossConfig;
    private final Runnable onUpdateCallback;

    private VBox content;

    // Basic properties
    private TextField healthField;
    private TextField fireRateField;
    private TextField speedField;
    private ComboBox<MovementPattern> movementCombo;

    // Abilities
    private CheckBox hasLasersCheck;
    private CheckBox hasUpgradedLasersCheck;
    private CheckBox hasDualLasersCheck;
    private CheckBox hasDiagonalLasersCheck;
    private CheckBox hasMissilesCheck;
    private CheckBox hasShieldCheck;
    private CheckBox hasEmpCheck;

    // Phases
    private CheckBox hasMultiplePhasesCheck;
    private TextField phaseThresholdField;

    public BossConfigPanel(BossConfiguration bossConfig, Runnable onUpdateCallback) {
        this.bossConfig = bossConfig;
        this.onUpdateCallback = onUpdateCallback;

        content = new VBox(10);
        content.setPadding(new Insets(10));

        buildUI();

        setContent(content);
        setFitToWidth(true);
    }

    private void buildUI() {
        content.getChildren().clear();

        Label titleLabel = new Label("Boss Configuration");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        content.getChildren().add(titleLabel);
        content.getChildren().add(new Separator());

        // Basic properties
        buildBasicSection();

        // Abilities
        buildAbilitiesSection();

        // Phases
        buildPhasesSection();

        // Apply button
        Button applyBtn = new Button("Apply Changes");
        applyBtn.setMaxWidth(Double.MAX_VALUE);
        applyBtn.setOnAction(e -> applyChanges());
        content.getChildren().add(applyBtn);
    }

    private void buildBasicSection() {
        content.getChildren().add(new Label("Basic Properties"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        healthField = createNumberField(bossConfig.getHealth());
        fireRateField = createNumberField(bossConfig.getFireRate());
        speedField = createNumberField(bossConfig.getSpeed());

        movementCombo = new ComboBox<>();
        movementCombo.getItems().addAll(MovementPattern.values());
        movementCombo.setValue(bossConfig.getMovementPattern());
        movementCombo.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("Health:"), 0, 0);
        grid.add(healthField, 1, 0);
        grid.add(new Label("Fire Rate (ms):"), 0, 1);
        grid.add(fireRateField, 1, 1);
        grid.add(new Label("Speed:"), 0, 2);
        grid.add(speedField, 1, 2);
        grid.add(new Label("Movement:"), 0, 3);
        grid.add(movementCombo, 1, 3);

        content.getChildren().add(grid);
        content.getChildren().add(new Separator());
    }

    private void buildAbilitiesSection() {
        content.getChildren().add(new Label("Abilities"));

        hasLasersCheck = new CheckBox("Has Lasers");
        hasLasersCheck.setSelected(bossConfig.isHasLasers());

        hasUpgradedLasersCheck = new CheckBox("Upgraded Lasers");
        hasUpgradedLasersCheck.setSelected(bossConfig.isHasUpgradedLasers());

        hasDualLasersCheck = new CheckBox("Dual Lasers");
        hasDualLasersCheck.setSelected(bossConfig.isHasDualLasers());

        hasDiagonalLasersCheck = new CheckBox("Diagonal Lasers");
        hasDiagonalLasersCheck.setSelected(bossConfig.isHasDiagonalLasers());

        hasMissilesCheck = new CheckBox("Has Missiles");
        hasMissilesCheck.setSelected(bossConfig.isHasMissiles());

        hasShieldCheck = new CheckBox("Has Shield");
        hasShieldCheck.setSelected(bossConfig.isHasShield());

        hasEmpCheck = new CheckBox("Has EMP");
        hasEmpCheck.setSelected(bossConfig.isHasEmp());

        content.getChildren().addAll(
            hasLasersCheck,
            hasUpgradedLasersCheck,
            hasDualLasersCheck,
            hasDiagonalLasersCheck,
            hasMissilesCheck,
            hasShieldCheck,
            hasEmpCheck
        );
        content.getChildren().add(new Separator());
    }

    private void buildPhasesSection() {
        content.getChildren().add(new Label("Phase Configuration"));

        hasMultiplePhasesCheck = new CheckBox("Multiple Phases");
        hasMultiplePhasesCheck.setSelected(bossConfig.isHasMultiplePhases());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        phaseThresholdField = createNumberField(bossConfig.getPhaseHealthThreshold());
        phaseThresholdField.disableProperty().bind(hasMultiplePhasesCheck.selectedProperty().not());

        grid.add(new Label("Phase 2 at Health %:"), 0, 0);
        grid.add(phaseThresholdField, 1, 0);

        content.getChildren().add(hasMultiplePhasesCheck);
        content.getChildren().add(grid);
        content.getChildren().add(new Separator());
    }

    private TextField createNumberField(Number value) {
        TextField field = new TextField(value.toString());
        field.setPrefWidth(100);
        return field;
    }

    private void applyChanges() {
        try {
            // Apply basic properties
            bossConfig.setHealth(Integer.parseInt(healthField.getText()));
            bossConfig.setFireRate(Integer.parseInt(fireRateField.getText()));
            bossConfig.setSpeed(Float.parseFloat(speedField.getText()));
            bossConfig.setMovementPattern(movementCombo.getValue());

            // Apply abilities
            bossConfig.setHasLasers(hasLasersCheck.isSelected());
            bossConfig.setHasUpgradedLasers(hasUpgradedLasersCheck.isSelected());
            bossConfig.setHasDualLasers(hasDualLasersCheck.isSelected());
            bossConfig.setHasDiagonalLasers(hasDiagonalLasersCheck.isSelected());
            bossConfig.setHasMissiles(hasMissilesCheck.isSelected());
            bossConfig.setHasShield(hasShieldCheck.isSelected());
            bossConfig.setHasEmp(hasEmpCheck.isSelected());

            // Apply phases
            bossConfig.setHasMultiplePhases(hasMultiplePhasesCheck.isSelected());
            bossConfig.setPhaseHealthThreshold(Integer.parseInt(phaseThresholdField.getText()));

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
        alert.setContentText("Boss configuration updated successfully");
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
