package com.dalesmithwebdev.galaxia.tools.ui;

import com.dalesmithwebdev.galaxia.tools.model.Level;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Panel for editing level metadata and viewing statistics
 */
public class LevelMetadataPanel extends ScrollPane {
    private final Level level;
    private final Runnable onUpdateCallback;

    private VBox content;

    // Editable fields
    private TextField nameField;
    private TextField lengthField;
    private TextField estimatedTimeField;
    private CheckBox hasBossCheck;

    // Calculated/read-only fields
    private Label difficultyLabel;
    private Label totalObjectsLabel;
    private Label fightersLabel;
    private Label ufosLabel;
    private Label meteorsLabel;
    private Label powerupsLabel;

    public LevelMetadataPanel(Level level, Runnable onUpdateCallback) {
        this.level = level;
        this.onUpdateCallback = onUpdateCallback;

        content = new VBox(10);
        content.setPadding(new Insets(10));

        buildUI();

        setContent(content);
        setFitToWidth(true);
    }

    private void buildUI() {
        content.getChildren().clear();

        Label titleLabel = new Label("Level Metadata");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        content.getChildren().add(titleLabel);
        content.getChildren().add(new Separator());

        // Editable section
        buildEditableSection();

        // Statistics section
        buildStatisticsSection();

        // Apply button
        Button applyBtn = new Button("Apply Changes");
        applyBtn.setMaxWidth(Double.MAX_VALUE);
        applyBtn.setOnAction(e -> applyChanges());
        content.getChildren().add(applyBtn);
    }

    private void buildEditableSection() {
        content.getChildren().add(new Label("Basic Info"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        nameField = new TextField(level.getName());
        nameField.setPrefWidth(200);

        lengthField = new TextField(String.valueOf(level.getLength()));
        lengthField.setPrefWidth(100);

        estimatedTimeField = new TextField(String.valueOf(level.getEstimatedTimeSeconds()));
        estimatedTimeField.setPrefWidth(100);

        hasBossCheck = new CheckBox("Has Boss");
        hasBossCheck.setSelected(level.isHasBoss());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Length (pixels):"), 0, 1);
        grid.add(lengthField, 1, 1);
        grid.add(new Label("Est. Time (s):"), 0, 2);
        grid.add(estimatedTimeField, 1, 2);

        content.getChildren().add(grid);
        content.getChildren().add(hasBossCheck);
        content.getChildren().add(new Separator());
    }

    private void buildStatisticsSection() {
        content.getChildren().add(new Label("Statistics (Auto-calculated)"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        difficultyLabel = new Label(String.format("%.1f / 10.0", level.getDifficultyRating()));
        totalObjectsLabel = new Label(String.valueOf(level.getObjects().size()));
        fightersLabel = new Label(String.valueOf(level.getFighterCount()));
        ufosLabel = new Label(String.valueOf(level.getUfoCount()));
        meteorsLabel = new Label(String.valueOf(level.getMeteorCount()));
        powerupsLabel = new Label(String.valueOf(level.getPowerupCount()));

        grid.add(new Label("Difficulty:"), 0, 0);
        grid.add(difficultyLabel, 1, 0);
        grid.add(new Label("Total Objects:"), 0, 1);
        grid.add(totalObjectsLabel, 1, 1);
        grid.add(new Label("Fighters:"), 0, 2);
        grid.add(fightersLabel, 1, 2);
        grid.add(new Label("UFOs:"), 0, 3);
        grid.add(ufosLabel, 1, 3);
        grid.add(new Label("Meteors:"), 0, 4);
        grid.add(meteorsLabel, 1, 4);
        grid.add(new Label("Power-ups:"), 0, 5);
        grid.add(powerupsLabel, 1, 5);

        content.getChildren().add(grid);
        content.getChildren().add(new Separator());
    }

    public void refresh() {
        // Update calculated fields
        difficultyLabel.setText(String.format("%.1f / 10.0", level.getDifficultyRating()));
        totalObjectsLabel.setText(String.valueOf(level.getObjects().size()));
        fightersLabel.setText(String.valueOf(level.getFighterCount()));
        ufosLabel.setText(String.valueOf(level.getUfoCount()));
        meteorsLabel.setText(String.valueOf(level.getMeteorCount()));
        powerupsLabel.setText(String.valueOf(level.getPowerupCount()));
    }

    private void applyChanges() {
        try {
            level.setName(nameField.getText());
            level.setLength(Float.parseFloat(lengthField.getText()));
            level.setEstimatedTimeSeconds(Float.parseFloat(estimatedTimeField.getText()));
            level.setHasBoss(hasBossCheck.isSelected());

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
        alert.setContentText("Level metadata updated successfully");
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
