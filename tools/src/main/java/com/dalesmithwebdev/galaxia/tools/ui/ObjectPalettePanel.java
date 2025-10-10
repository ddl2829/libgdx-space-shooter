package com.dalesmithwebdev.galaxia.tools.ui;

import com.dalesmithwebdev.galaxia.tools.model.ObjectType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Palette of placeable objects (meteors, enemies, power-ups)
 */
public class ObjectPalettePanel extends ScrollPane {
    private final Consumer<ObjectType> onSelectCallback;
    private ToggleGroup toggleGroup;

    public ObjectPalettePanel(Consumer<ObjectType> onSelectCallback) {
        this.onSelectCallback = onSelectCallback;

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        toggleGroup = new ToggleGroup();

        // Meteors section
        content.getChildren().add(createSectionLabel("Meteors"));
        content.getChildren().add(createObjectButton(ObjectType.METEOR_LARGE));
        content.getChildren().add(createObjectButton(ObjectType.METEOR_SMALL));

        content.getChildren().add(new Separator());

        // Enemies section
        content.getChildren().add(createSectionLabel("Enemies"));
        content.getChildren().add(createObjectButton(ObjectType.ENEMY_FIGHTER));
        content.getChildren().add(createObjectButton(ObjectType.ENEMY_UFO));
        content.getChildren().add(createObjectButton(ObjectType.ENEMY_BOSS));

        content.getChildren().add(new Separator());

        // Power-ups section
        content.getChildren().add(createSectionLabel("Power-ups"));
        content.getChildren().add(createObjectButton(ObjectType.POWERUP_LASER_STRENGTH));
        content.getChildren().add(createObjectButton(ObjectType.POWERUP_DUAL_LASER));
        content.getChildren().add(createObjectButton(ObjectType.POWERUP_DIAGONAL_LASER));
        content.getChildren().add(createObjectButton(ObjectType.POWERUP_MISSILE));
        content.getChildren().add(createObjectButton(ObjectType.POWERUP_BOMB));
        content.getChildren().add(createObjectButton(ObjectType.POWERUP_EMP));
        content.getChildren().add(createObjectButton(ObjectType.POWERUP_SHIELD));

        setContent(content);
        setFitToWidth(true);
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        return label;
    }

    private ToggleButton createObjectButton(ObjectType type) {
        ToggleButton button = new ToggleButton(type.getDisplayName());
        button.setToggleGroup(toggleGroup);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);

        button.setOnAction(e -> {
            if (button.isSelected()) {
                onSelectCallback.accept(type);
            } else {
                onSelectCallback.accept(null);
            }
        });

        return button;
    }

    public void clearSelection() {
        toggleGroup.selectToggle(null);
    }
}
