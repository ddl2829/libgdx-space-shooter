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
import com.dalesmithwebdev.arcadespaceshooter.screens.listeners.StartScreenKeyboardListener;

public class StartScreen extends ScreenAdapter {
    private final Stage ui;
    public VerticalGroup startMenuOptions;
    public int selectedMenuItem = 0;

    public StartScreen() {
        ui = new Stage();
        VerticalGroup startMenu = new VerticalGroup();
        startMenu.align(Align.center);
        startMenu.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        startMenu.space(Gdx.graphics.getHeight() / 4f);
        startMenuOptions = new VerticalGroup();

        Label title = new Label("Arcade Space Shooter", ArcadeSpaceShooter.uiSkin);
        title.setFontScale(3);

        final Label startLabel = new Label("Start Game", ArcadeSpaceShooter.uiSkin);
        startLabel.setColor(Color.YELLOW);
        startLabel.setFontScale(2);
        startLabel.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SnapshotArray<Actor> options =  startMenuOptions.getChildren();
                for(Actor option : options) {
                    option.setColor(Color.WHITE);
                }
                startLabel.setColor(Color.YELLOW);
                selectedMenuItem = 0;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArcadeSpaceShooter.instance.setScreen(new GameScreen());
                //ArcadeSpaceShooter.instance.setScreen(new GameOverScreen());
            }
        });

        final Label optionsLabel = new Label("Options", ArcadeSpaceShooter.uiSkin);
        optionsLabel.setFontScale(2);
        optionsLabel.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SnapshotArray<Actor> options =  startMenuOptions.getChildren();
                for(Actor option : options) {
                    option.setColor(Color.WHITE);
                }
                optionsLabel.setColor(Color.YELLOW);
                selectedMenuItem = 0;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                // do options stuff;
            }
        });

        final Label exitLabel = new Label("Exit", ArcadeSpaceShooter.uiSkin);
        exitLabel.setFontScale(2);
        exitLabel.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SnapshotArray<Actor> options =  startMenuOptions.getChildren();
                for(Actor option : options) {
                    option.setColor(Color.WHITE);
                }
                exitLabel.setColor(Color.YELLOW);
                selectedMenuItem = 0;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.exit(0);
            }
        });

        startMenuOptions.addActor(startLabel);
        startMenuOptions.addActor(optionsLabel);
        startMenuOptions.addActor(exitLabel);
        startMenuOptions.space(50);

        startMenu.addActor(title);
        startMenu.addActor(startMenuOptions);
        ui.addActor(startMenu);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(ui);
        multiplexer.addProcessor(new StartScreenKeyboardListener(this));
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override()
    public void render(float deltaTime)
    {
        ArcadeSpaceShooter.engine.update(deltaTime);

        ui.act(deltaTime);
        ui.draw();
    }
}
