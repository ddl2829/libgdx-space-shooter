package com.dalesmithwebdev.galaxia.tools.ui;

import com.dalesmithwebdev.galaxia.tools.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Panel for managing timed events in the level editor
 */
public class TimedEventsPanel extends BorderPane {
    private final Level level;
    private final Runnable onEventUpdatedCallback;

    private TableView<TimedEvent> eventsTable;
    private ObservableList<TimedEvent> eventsList;
    private VBox editorForm;

    private TimedEvent currentEvent = null;
    private boolean isEditMode = false;

    // Form controls
    private TextField triggerTimeField;
    private ComboBox<TriggerType> triggerTypeCombo;
    private ComboBox<EventType> eventTypeCombo;
    private VBox eventDataContainer;

    public TimedEventsPanel(Level level, Runnable onEventUpdatedCallback) {
        this.level = level;
        this.onEventUpdatedCallback = onEventUpdatedCallback;

        // Initialize UI
        VBox mainContent = new VBox(10);
        mainContent.setPadding(new Insets(10));

        // Events table (must be created before toolbar)
        eventsTable = createEventsTable();
        VBox.setVgrow(eventsTable, Priority.ALWAYS);

        // Top toolbar (created after eventsTable is initialized)
        ToolBar toolbar = createToolbar();
        mainContent.getChildren().add(toolbar);

        // Add table to content
        mainContent.getChildren().add(eventsTable);

        // Event editor form
        editorForm = createEditorForm();
        editorForm.setVisible(false);
        editorForm.setManaged(false);
        mainContent.getChildren().add(editorForm);

        setCenter(mainContent);
        refreshEventsList();
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();

        Button addBtn = new Button("Add Event");
        addBtn.setOnAction(e -> startAddEvent());

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.disableProperty().bind(eventsTable.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.setOnAction(e -> deleteSelectedEvent());

        Button sortBtn = new Button("Sort by Time");
        sortBtn.setOnAction(e -> sortEvents());

        toolbar.getItems().addAll(addBtn, deleteBtn, new Separator(), sortBtn);
        return toolbar;
    }

    private TableView<TimedEvent> createEventsTable() {
        TableView<TimedEvent> table = new TableView<>();
        table.setPrefHeight(300);

        // Trigger time column
        TableColumn<TimedEvent, String> triggerCol = new TableColumn<>("Trigger");
        triggerCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTriggerDisplay()));
        triggerCol.setPrefWidth(80);

        // Event type column
        TableColumn<TimedEvent, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getEventType().getIcon() + " " +
                data.getValue().getEventType().getDisplayName()));
        typeCol.setPrefWidth(120);

        // Description column
        TableColumn<TimedEvent, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getSummary()));
        descCol.setPrefWidth(250);

        table.getColumns().addAll(triggerCol, typeCol, descCol);

        // Row selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                editEvent(newVal);
            }
        });

        eventsList = FXCollections.observableArrayList();
        table.setItems(eventsList);

        return table;
    }

    private VBox createEditorForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("Event Editor");
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        // Trigger configuration
        HBox triggerBox = new HBox(10);
        triggerBox.setAlignment(Pos.CENTER_LEFT);

        Label triggerLabel = new Label("Trigger:");
        triggerTimeField = new TextField("0");
        triggerTimeField.setPrefWidth(100);

        triggerTypeCombo = new ComboBox<>();
        triggerTypeCombo.getItems().addAll(TriggerType.values());
        triggerTypeCombo.setValue(TriggerType.TIME_BASED);
        triggerTypeCombo.setOnAction(e -> updateTriggerLabel());

        triggerBox.getChildren().addAll(triggerLabel, triggerTimeField, triggerTypeCombo);

        // Event type selection
        HBox eventTypeBox = new HBox(10);
        eventTypeBox.setAlignment(Pos.CENTER_LEFT);

        Label eventTypeLabel = new Label("Event Type:");
        eventTypeCombo = new ComboBox<>();
        eventTypeCombo.getItems().addAll(EventType.values());
        eventTypeCombo.setValue(EventType.NOTIFICATION);
        eventTypeCombo.setOnAction(e -> updateEventDataForm());

        eventTypeBox.getChildren().addAll(eventTypeLabel, eventTypeCombo);

        // Event data container (dynamic based on event type)
        eventDataContainer = new VBox(10);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = new Button(isEditMode ? "Update" : "Create");
        saveBtn.setOnAction(e -> saveEvent());

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> cancelEdit());

        buttonBox.getChildren().addAll(saveBtn, cancelBtn);

        form.getChildren().addAll(
            titleLabel,
            new Separator(),
            triggerBox,
            eventTypeBox,
            new Separator(),
            new Label("Event Data:"),
            eventDataContainer,
            buttonBox
        );

        return form;
    }

    private void updateTriggerLabel() {
        TriggerType type = triggerTypeCombo.getValue();
        if (type == TriggerType.TIME_BASED) {
            triggerTimeField.setPromptText("Seconds");
        } else {
            triggerTimeField.setPromptText("Y Position");
        }
    }

    private void updateEventDataForm() {
        eventDataContainer.getChildren().clear();
        EventType type = eventTypeCombo.getValue();

        switch (type) {
            case NOTIFICATION:
                createNotificationForm();
                break;
            case ITEM_SPAWN:
                createItemSpawnForm();
                break;
            case ENEMY_WAVE:
                createEnemyWaveForm();
                break;
            case ENVIRONMENTAL_CHANGE:
                createEnvironmentalForm();
                break;
        }
    }

    private void createNotificationForm() {
        Label messageLabel = new Label("Message:");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter notification message...");
        messageArea.setPrefRowCount(3);
        messageArea.setId("message");

        HBox durationBox = new HBox(10);
        durationBox.setAlignment(Pos.CENTER_LEFT);
        Label durationLabel = new Label("Duration (seconds):");
        TextField durationField = new TextField("3.0");
        durationField.setPrefWidth(100);
        durationField.setId("duration");
        durationBox.getChildren().addAll(durationLabel, durationField);

        HBox styleBox = new HBox(10);
        styleBox.setAlignment(Pos.CENTER_LEFT);
        Label styleLabel = new Label("Style:");
        ComboBox<String> styleCombo = new ComboBox<>();
        styleCombo.getItems().addAll("INFO", "WARNING", "URGENT");
        styleCombo.setValue("INFO");
        styleCombo.setId("style");
        styleBox.getChildren().addAll(styleLabel, styleCombo);

        eventDataContainer.getChildren().addAll(messageLabel, messageArea, durationBox, styleBox);

        // Load existing data if editing
        if (currentEvent != null && currentEvent.getEventType() == EventType.NOTIFICATION) {
            messageArea.setText(currentEvent.getData("message", ""));
            durationField.setText(String.valueOf(currentEvent.getData("duration", 3.0)));
            styleCombo.setValue(currentEvent.getData("style", "INFO"));
        }
    }

    private void createItemSpawnForm() {
        HBox objectTypeBox = new HBox(10);
        objectTypeBox.setAlignment(Pos.CENTER_LEFT);
        Label typeLabel = new Label("Object Type:");
        ComboBox<ObjectType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(ObjectType.values());
        typeCombo.getItems().removeIf(t -> !t.isPowerup()); // Only powerups for item spawn
        if (!typeCombo.getItems().isEmpty()) {
            typeCombo.setValue(typeCombo.getItems().get(0));
        }
        typeCombo.setId("objectType");
        objectTypeBox.getChildren().addAll(typeLabel, typeCombo);

        HBox xBox = new HBox(10);
        xBox.setAlignment(Pos.CENTER_LEFT);
        Label xLabel = new Label("X Position:");
        TextField xField = new TextField("300");
        xField.setPrefWidth(100);
        xField.setId("x");
        xBox.getChildren().addAll(xLabel, xField);

        HBox yBox = new HBox(10);
        yBox.setAlignment(Pos.CENTER_LEFT);
        Label yLabel = new Label("Y Position:");
        TextField yField = new TextField("0");
        yField.setPrefWidth(100);
        yField.setId("y");
        yBox.getChildren().addAll(yLabel, yField);

        eventDataContainer.getChildren().addAll(objectTypeBox, xBox, yBox);

        // Load existing data if editing
        if (currentEvent != null && currentEvent.getEventType() == EventType.ITEM_SPAWN) {
            String objTypeStr = currentEvent.getData("objectType", "");
            if (!objTypeStr.isEmpty()) {
                try {
                    typeCombo.setValue(ObjectType.valueOf(objTypeStr));
                } catch (IllegalArgumentException e) {
                    // Invalid type, keep default
                }
            }
            xField.setText(String.valueOf(currentEvent.getData("x", 300.0)));
            yField.setText(String.valueOf(currentEvent.getData("y", 0.0)));
        }
    }

    private void createEnemyWaveForm() {
        HBox enemyTypeBox = new HBox(10);
        enemyTypeBox.setAlignment(Pos.CENTER_LEFT);
        Label typeLabel = new Label("Enemy Type:");
        ComboBox<ObjectType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(ObjectType.values());
        typeCombo.getItems().removeIf(t -> !t.isEnemy()); // Only enemies
        if (!typeCombo.getItems().isEmpty()) {
            typeCombo.setValue(typeCombo.getItems().get(0));
        }
        typeCombo.setId("enemyType");
        enemyTypeBox.getChildren().addAll(typeLabel, typeCombo);

        HBox countBox = new HBox(10);
        countBox.setAlignment(Pos.CENTER_LEFT);
        Label countLabel = new Label("Count:");
        TextField countField = new TextField("5");
        countField.setPrefWidth(100);
        countField.setId("count");
        countBox.getChildren().addAll(countLabel, countField);

        HBox patternBox = new HBox(10);
        patternBox.setAlignment(Pos.CENTER_LEFT);
        Label patternLabel = new Label("Formation:");
        ComboBox<String> patternCombo = new ComboBox<>();
        patternCombo.getItems().addAll("LINE", "V_SHAPE", "SCATTERED", "CIRCLE");
        patternCombo.setValue("LINE");
        patternCombo.setId("pattern");
        patternBox.getChildren().addAll(patternLabel, patternCombo);

        eventDataContainer.getChildren().addAll(enemyTypeBox, countBox, patternBox);

        // Load existing data if editing
        if (currentEvent != null && currentEvent.getEventType() == EventType.ENEMY_WAVE) {
            String enemyTypeStr = currentEvent.getData("enemyType", "");
            if (!enemyTypeStr.isEmpty()) {
                try {
                    typeCombo.setValue(ObjectType.valueOf(enemyTypeStr));
                } catch (IllegalArgumentException e) {
                    // Invalid type, keep default
                }
            }
            countField.setText(String.valueOf(currentEvent.getData("count", 5)));
            patternCombo.setValue(currentEvent.getData("pattern", "LINE"));
        }
    }

    private void createEnvironmentalForm() {
        HBox changeTypeBox = new HBox(10);
        changeTypeBox.setAlignment(Pos.CENTER_LEFT);
        Label typeLabel = new Label("Change Type:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("BACKGROUND", "MUSIC", "LIGHTING");
        typeCombo.setValue("BACKGROUND");
        typeCombo.setId("changeType");
        changeTypeBox.getChildren().addAll(typeLabel, typeCombo);

        HBox valueBox = new HBox(10);
        valueBox.setAlignment(Pos.CENTER_LEFT);
        Label valueLabel = new Label("Value:");
        TextField valueField = new TextField("");
        valueField.setPromptText("Asset name or identifier");
        valueField.setId("value");
        valueBox.getChildren().addAll(valueLabel, valueField);

        eventDataContainer.getChildren().addAll(changeTypeBox, valueBox);

        // Load existing data if editing
        if (currentEvent != null && currentEvent.getEventType() == EventType.ENVIRONMENTAL_CHANGE) {
            typeCombo.setValue(currentEvent.getData("changeType", "BACKGROUND"));
            valueField.setText(currentEvent.getData("value", ""));
        }
    }

    private void startAddEvent() {
        isEditMode = false;
        currentEvent = new TimedEvent();
        showEditorForm();
        updateEventDataForm();
    }

    private void editEvent(TimedEvent event) {
        isEditMode = true;
        currentEvent = event;
        showEditorForm();

        // Load event data into form
        triggerTimeField.setText(String.valueOf(event.getTriggerTime()));
        triggerTypeCombo.setValue(event.getTriggerType());
        eventTypeCombo.setValue(event.getEventType());
        updateEventDataForm();
    }

    private void saveEvent() {
        try {
            // Parse trigger time
            float triggerTime = Float.parseFloat(triggerTimeField.getText());
            currentEvent.setTriggerTime(triggerTime);
            currentEvent.setTriggerType(triggerTypeCombo.getValue());
            currentEvent.setEventType(eventTypeCombo.getValue());

            // Extract event data from form controls
            currentEvent.getEventData().clear();
            for (javafx.scene.Node node : eventDataContainer.getChildren()) {
                extractEventData(node, currentEvent);
            }

            // Add to level if new
            if (!isEditMode) {
                level.addTimedEvent(currentEvent);
            } else {
                level.sortTimedEvents();
            }

            hideEditorForm();
            refreshEventsList();

            if (onEventUpdatedCallback != null) {
                onEventUpdatedCallback.run();
            }
        } catch (NumberFormatException e) {
            showError("Invalid input", "Please check numeric values.");
        }
    }

    private void extractEventData(javafx.scene.Node node, TimedEvent event) {
        if (node instanceof Control) {
            Control control = (Control) node;
            String id = control.getId();
            if (id != null) {
                if (control instanceof TextField) {
                    TextField field = (TextField) control;
                    try {
                        // Try to parse as number
                        if (field.getText().contains(".")) {
                            event.setData(id, Double.parseDouble(field.getText()));
                        } else {
                            event.setData(id, Integer.parseInt(field.getText()));
                        }
                    } catch (NumberFormatException e) {
                        // Store as string
                        event.setData(id, field.getText());
                    }
                } else if (control instanceof TextArea) {
                    event.setData(id, ((TextArea) control).getText());
                } else if (control instanceof ComboBox) {
                    ComboBox<?> combo = (ComboBox<?>) control;
                    Object value = combo.getValue();
                    if (value instanceof ObjectType) {
                        event.setData(id, ((ObjectType) value).name());
                    } else {
                        event.setData(id, String.valueOf(value));
                    }
                }
            }
        } else if (node instanceof Pane) {
            for (javafx.scene.Node child : ((Pane) node).getChildren()) {
                extractEventData(child, event);
            }
        }
    }

    private void cancelEdit() {
        hideEditorForm();
        eventsTable.getSelectionModel().clearSelection();
    }

    private void deleteSelectedEvent() {
        TimedEvent selected = eventsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Event");
            confirm.setHeaderText("Delete this event?");
            confirm.setContentText(selected.getSummary());

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    level.removeTimedEvent(selected);
                    refreshEventsList();
                    if (onEventUpdatedCallback != null) {
                        onEventUpdatedCallback.run();
                    }
                }
            });
        }
    }

    private void sortEvents() {
        level.sortTimedEvents();
        refreshEventsList();
    }

    private void showEditorForm() {
        editorForm.setVisible(true);
        editorForm.setManaged(true);
    }

    private void hideEditorForm() {
        editorForm.setVisible(false);
        editorForm.setManaged(false);
        currentEvent = null;
    }

    private void refreshEventsList() {
        eventsList.clear();
        eventsList.addAll(level.getTimedEvents());
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void refresh() {
        refreshEventsList();
    }
}
