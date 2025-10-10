package com.dalesmithwebdev.galaxia.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.BackgroundObjectComponent;
import com.dalesmithwebdev.galaxia.screens.listeners.GameOverScreenKeyboardListener;
import com.dalesmithwebdev.galaxia.systems.LevelSystem;
import com.dalesmithwebdev.galaxia.utility.FontManager;

public class GameOverScreen extends ScreenAdapter {
    private final Stage ui;
    public VerticalGroup gameOverMenuOptions;
    public int selectedMenuItem = 0;

    public GameOverScreen() {
        ui = new Stage();
        VerticalGroup gameOverMenu = new VerticalGroup();
        gameOverMenu.align(Align.center);
        gameOverMenu.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        gameOverMenu.space(Gdx.graphics.getHeight() / 4f);
        gameOverMenuOptions = new VerticalGroup();

        VerticalGroup gameOverMenuListing = new VerticalGroup();
        gameOverMenuListing.space(30);

        // Game over title with sci-fi font
        Label title = new Label("GAME OVER", FontManager.getLabelStyle("title"));
        title.setAlignment(Align.center);

        // Score display with monospace font
        Label scoreLabel = new Label("SCORE: " + (int)ArcadeSpaceShooter.playerScore, FontManager.getLabelStyle("score"));
        scoreLabel.setAlignment(Align.center);

        // Level display with HUD font
        Label levelLabel = new Label("LEVEL: " + LevelSystem.levelNumber, FontManager.getLabelStyle("hud_text"));
        levelLabel.setAlignment(Align.center);

        gameOverMenuListing.addActor(title);
        gameOverMenuListing.addActor(scoreLabel);
        gameOverMenuListing.addActor(levelLabel);

        final Label startLabel = new Label("Restart", FontManager.getLabelStyle("menu_item"));
        startLabel.setColor(Color.YELLOW);
        startLabel.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SnapshotArray<Actor> options =  gameOverMenuOptions.getChildren();
                for(Actor option : options) {
                    option.setColor(Color.WHITE);
                }
                startLabel.setColor(Color.YELLOW);
                selectedMenuItem = 0;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Reset game state
                ArcadeSpaceShooter.playerScore = 0;
                ArcadeSpaceShooter.kills = 0;
                LevelSystem.levelNumber = 0;
                GameScreen.timeStayedAlive = 0;
                ArcadeSpaceShooter.gameOverScheduled = false;

                // Remove LevelSystem that will be re-added by GameScreen
                ArcadeSpaceShooter.engine.removeSystem(ArcadeSpaceShooter.engine.getSystem(LevelSystem.class));

                // Remove all entities except background image
                ImmutableArray<Entity> allEntities = ArcadeSpaceShooter.engine.getEntities();
                for(int i = allEntities.size() - 1; i >= 0; i--) {
                    Entity entity = allEntities.get(i);
                    com.dalesmithwebdev.galaxia.components.RenderComponent rc = entity.getComponent(com.dalesmithwebdev.galaxia.components.RenderComponent.class);

                    // Keep only the background image entity (PLANE_BACKGROUND_IMAGE)
                    boolean isBackgroundImage = rc != null && rc.zIndex == com.dalesmithwebdev.galaxia.components.RenderComponent.PLANE_BACKGROUND_IMAGE;

                    if(!isBackgroundImage) {
                        ArcadeSpaceShooter.engine.removeEntity(entity);
                    }
                }

                ArcadeSpaceShooter.instance.setScreen(new GameScreen());
            }
        });

        final Label quitLabel = new Label("Quit", FontManager.getLabelStyle("menu_item"));
        quitLabel.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SnapshotArray<Actor> options =  gameOverMenuOptions.getChildren();
                for(Actor option : options) {
                    option.setColor(Color.WHITE);
                }
                quitLabel.setColor(Color.YELLOW);
                selectedMenuItem = 1;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.exit(0);
            }
        });

        gameOverMenuOptions.addActor(startLabel);
        gameOverMenuOptions.addActor(quitLabel);
        gameOverMenuOptions.space(50);

        gameOverMenu.addActor(gameOverMenuListing);
        gameOverMenu.addActor(gameOverMenuOptions);
        ui.addActor(gameOverMenu);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(ui);
        multiplexer.addProcessor(new GameOverScreenKeyboardListener(this));
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float deltaTime)
    {
        ArcadeSpaceShooter.engine.update(deltaTime);

        ui.act(deltaTime);
        ui.draw();
    }
}
