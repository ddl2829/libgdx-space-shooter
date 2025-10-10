package com.dalesmithwebdev.galaxia.tools.ui;

import com.dalesmithwebdev.galaxia.tools.model.Level;
import com.dalesmithwebdev.galaxia.tools.service.LevelService;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Main window displaying a list of all levels
 */
public class LevelListView extends Stage {
    private final LevelService levelService;
    private final TableView<Level> levelTable;
    private final ObservableList<Level> levels;

    public LevelListView() {
        this.levelService = new LevelService();
        this.levels = FXCollections.observableArrayList();
        this.levelTable = createLevelTable();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Toolbar
        HBox toolbar = createToolbar();
        root.setTop(toolbar);

        // Table
        root.setCenter(levelTable);

        Scene scene = new Scene(root, 1000, 600);
        setTitle("Galaxia Level Editor - Levels");
        setScene(scene);

        // Load levels
        refreshLevels();
    }

    private TableView<Level> createLevelTable() {
        TableView<Level> table = new TableView<>();

        // ID column
        TableColumn<Level, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        idCol.setPrefWidth(150);

        // Name column
        TableColumn<Level, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(200);

        // Enemy columns
        TableColumn<Level, Number> fightersCol = new TableColumn<>("Fighters");
        fightersCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getFighterCount()));
        fightersCol.setPrefWidth(80);

        TableColumn<Level, Number> ufosCol = new TableColumn<>("UFOs");
        ufosCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getUfoCount()));
        ufosCol.setPrefWidth(80);

        TableColumn<Level, Number> meteorsCol = new TableColumn<>("Meteors");
        meteorsCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMeteorCount()));
        meteorsCol.setPrefWidth(80);

        TableColumn<Level, Number> powerupsCol = new TableColumn<>("Power-ups");
        powerupsCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPowerupCount()));
        powerupsCol.setPrefWidth(80);

        // Boss column
        TableColumn<Level, String> bossCol = new TableColumn<>("Boss");
        bossCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isHasBoss() ? "Yes" : "No"));
        bossCol.setPrefWidth(60);

        // Length column
        TableColumn<Level, Number> lengthCol = new TableColumn<>("Length (s)");
        lengthCol.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getEstimatedTimeSeconds()));
        lengthCol.setPrefWidth(90);

        // Difficulty column
        TableColumn<Level, Number> difficultyCol = new TableColumn<>("Difficulty");
        difficultyCol.setCellValueFactory(data -> {
            float diff = data.getValue().getDifficultyRating();
            return new SimpleFloatProperty(Math.round(diff * 10) / 10.0f);
        });
        difficultyCol.setPrefWidth(90);

        table.getColumns().addAll(idCol, nameCol, fightersCol, ufosCol, meteorsCol,
            powerupsCol, bossCol, lengthCol, difficultyCol);

        // Double-click to edit
        table.setRowFactory(tv -> {
            TableRow<Level> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openLevelEditor(row.getItem());
                }
            });
            return row;
        });

        table.setItems(levels);
        return table;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(0, 0, 10, 0));

        Button newBtn = new Button("New Level");
        newBtn.setOnAction(e -> createNewLevel());

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            Level selected = levelTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openLevelEditor(selected);
            }
        });
        editBtn.disableProperty().bind(levelTable.getSelectionModel().selectedItemProperty().isNull());

        Button duplicateBtn = new Button("Duplicate");
        duplicateBtn.setOnAction(e -> duplicateLevel());
        duplicateBtn.disableProperty().bind(levelTable.getSelectionModel().selectedItemProperty().isNull());

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> deleteLevel());
        deleteBtn.disableProperty().bind(levelTable.getSelectionModel().selectedItemProperty().isNull());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshLevels());

        toolbar.getChildren().addAll(newBtn, editBtn, duplicateBtn, deleteBtn, new Separator(), refreshBtn);
        return toolbar;
    }

    private void createNewLevel() {
        Level newLevel = levelService.createNewLevel();
        openLevelEditor(newLevel);
    }

    private void duplicateLevel() {
        Level selected = levelTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Level duplicate = levelService.duplicateLevel(selected);
            try {
                levelService.saveLevel(duplicate);
                refreshLevels();
                showInfo("Level duplicated successfully", "Created: " + duplicate.getName());
            } catch (Exception e) {
                showError("Failed to duplicate level", e.getMessage());
            }
        }
    }

    private void deleteLevel() {
        Level selected = levelTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete level: " + selected.getName());
            confirm.setContentText("Are you sure? This cannot be undone.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    levelService.deleteLevel(selected);
                    refreshLevels();
                    showInfo("Level deleted", selected.getName() + " has been deleted.");
                } catch (Exception e) {
                    showError("Failed to delete level", e.getMessage());
                }
            }
        }
    }

    private void openLevelEditor(Level level) {
        try {
            LevelEditorWindow editor = new LevelEditorWindow(level, levelService, this::refreshLevels);
            editor.show();
        } catch (Exception e) {
            showError("Failed to open level editor", e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshLevels() {
        levels.clear();
        levels.addAll(levelService.loadAllLevels());
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
}
