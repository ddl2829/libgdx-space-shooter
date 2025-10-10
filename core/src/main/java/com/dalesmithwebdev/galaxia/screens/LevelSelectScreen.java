package com.dalesmithwebdev.galaxia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.level.LevelLoader;
import com.dalesmithwebdev.galaxia.systems.LevelSystem;
import com.dalesmithwebdev.galaxia.utility.FontManager;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

import java.util.List;

/**
 * Screen for selecting which level to play
 */
public class LevelSelectScreen extends ScreenAdapter {
    private Stage ui;
    private VerticalGroup levelList;
    private int selectedIndex = 0;
    private List<LevelLoader.LevelInfo> levels;
    private float inputCooldown = 0;
    private static final float INPUT_DELAY = 200; // 200ms delay before accepting input

    public LevelSelectScreen() {
        ui = new Stage();
        Gdx.input.setInputProcessor(ui);

        // Load available levels
        levels = LevelLoader.getAvailableLevels();

        // Main container
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        ui.addActor(mainTable);

        // Title
        Label titleLabel = new Label("SELECT LEVEL", FontManager.getLabelStyle("title"));
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).padBottom(50).row();

        // Level list
        levelList = new VerticalGroup();
        levelList.space(20);
        levelList.align(Align.center);

        if (levels.isEmpty()) {
            // No levels found
            Label noLevelsLabel = new Label("No levels found!", FontManager.getLabelStyle("menu_item"));
            noLevelsLabel.setColor(Color.RED);
            levelList.addActor(noLevelsLabel);

            Label backLabel = new Label("Back to Menu", FontManager.getLabelStyle("menu_item"));
            backLabel.setColor(Color.YELLOW);
            backLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    SoundManager.playSelect();
                    ArcadeSpaceShooter.instance.setScreen(new StartScreen());
                }
            });
            levelList.addActor(backLabel);
        } else {
            // Add level options
            for (int i = 0; i < levels.size(); i++) {
                final LevelLoader.LevelInfo level = levels.get(i);
                final int index = i;

                Label levelLabel = new Label(
                    level.getName() + " (Difficulty: " + String.format("%.1f", level.getDifficulty()) + ")",
                    FontManager.getLabelStyle("menu_item")
                );

                if (i == selectedIndex) {
                    levelLabel.setColor(Color.YELLOW);
                }

                levelLabel.addListener(new ClickListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        updateSelection(index);
                    }

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        SoundManager.playSelect();
                        startLevel(level.getId());
                    }
                });

                levelList.addActor(levelLabel);
            }

            // Add back button
            Label backLabel = new Label("Back to Menu", FontManager.getLabelStyle("menu_item"));
            backLabel.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    updateSelection(levels.size());
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    SoundManager.playSelect();
                    ArcadeSpaceShooter.instance.setScreen(new StartScreen());
                }
            });
            levelList.addActor(backLabel);
        }

        // Wrap in scroll pane
        ScrollPane scrollPane = new ScrollPane(levelList, ArcadeSpaceShooter.uiSkin);
        scrollPane.setFadeScrollBars(false);
        mainTable.add(scrollPane).expand().fill().pad(20);
    }

    private void updateSelection(int newIndex) {
        int maxIndex = levels.isEmpty() ? 1 : levels.size();
        selectedIndex = newIndex;

        // Update colors
        for (int i = 0; i < levelList.getChildren().size; i++) {
            Actor actor = levelList.getChildren().get(i);
            if (actor instanceof Label) {
                ((Label) actor).setColor(i == selectedIndex ? Color.YELLOW : Color.WHITE);
            }
        }
    }

    private void startLevel(String levelId) {
        // Set the selected level and start the game
        System.out.println(">>> LevelSelectScreen: Starting level with ID: " + levelId);
        LevelSystem.setCurrentLevelId(levelId);
        System.out.println(">>> LevelSelectScreen: Set level ID, now setting level number to 1");
        LevelSystem.levelNumber = 1;
        System.out.println(">>> LevelSelectScreen: Creating new GameScreen");
        ArcadeSpaceShooter.instance.setScreen(new GameScreen());
        System.out.println(">>> LevelSelectScreen: GameScreen created and set");
    }

    @Override
    public void render(float deltaTime) {
        // Update input cooldown timer
        if (inputCooldown < INPUT_DELAY) {
            inputCooldown += deltaTime;
        }

        // Handle keyboard input only after cooldown
        if (inputCooldown >= INPUT_DELAY) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                int maxIndex = levels.isEmpty() ? 1 : levels.size();
                updateSelection(Math.max(0, selectedIndex - 1));
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                int maxIndex = levels.isEmpty() ? 1 : levels.size();
                updateSelection(Math.min(maxIndex, selectedIndex + 1));
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                SoundManager.playSelect();
                if (levels.isEmpty() || selectedIndex == levels.size()) {
                    // Back to menu
                    ArcadeSpaceShooter.instance.setScreen(new StartScreen());
                } else {
                    // Start selected level
                    startLevel(levels.get(selectedIndex).getId());
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                SoundManager.playSelect();
                ArcadeSpaceShooter.instance.setScreen(new StartScreen());
            }
        }

        ArcadeSpaceShooter.engine.update(deltaTime);
        ui.act(deltaTime);
        ui.draw();
    }
}
