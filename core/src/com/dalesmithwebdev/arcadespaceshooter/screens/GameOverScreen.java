package com.dalesmithwebdev.arcadespaceshooter.screens;

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
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.screens.listeners.GameOverScreenKeyboardListener;
import com.dalesmithwebdev.arcadespaceshooter.systems.LevelSystem;

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

        Label title = new Label("Game Over", ArcadeSpaceShooter.uiSkin);
        title.setFontScale(3);

        Label scoreLabel = new Label("Score: " + ArcadeSpaceShooter.playerScore, ArcadeSpaceShooter.uiSkin);
        scoreLabel.setFontScale(2);

        Label levelLabel = new Label("Level: " + LevelSystem.levelNumber, ArcadeSpaceShooter.uiSkin);
        levelLabel.setFontScale(2);

        gameOverMenuListing.addActor(title);
        gameOverMenuListing.addActor(scoreLabel);
        gameOverMenuListing.addActor(levelLabel);

        final Label startLabel = new Label("Restart", ArcadeSpaceShooter.uiSkin);
        startLabel.setColor(Color.YELLOW);
        startLabel.setFontScale(2);
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
                ArcadeSpaceShooter.playerScore = 0;
                ArcadeSpaceShooter.kills = 0;
                ArcadeSpaceShooter.instance.setScreen(new GameScreen());
            }
        });

        final Label quitLabel = new Label("Quit", ArcadeSpaceShooter.uiSkin);
        quitLabel.setFontScale(2);
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
