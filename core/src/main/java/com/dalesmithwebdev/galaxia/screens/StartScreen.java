package com.dalesmithwebdev.galaxia.screens;

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
import com.dalesmithwebdev.galaxia.screens.listeners.StartScreenKeyboardListener;
import com.dalesmithwebdev.galaxia.utility.FontManager;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

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

        // Use custom sci-fi title font
        Label title = new Label("GALAXIA", FontManager.getLabelStyle("title"));
        title.setAlignment(Align.center);

        // Use custom menu item fonts
        final Label startLabel = new Label("Select Level", FontManager.getLabelStyle("menu_item"));
        startLabel.setColor(Color.YELLOW);
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
                SoundManager.playSelect();
                ArcadeSpaceShooter.instance.setScreen(new LevelSelectScreen());
            }
        });

        final Label optionsLabel = new Label("Options", FontManager.getLabelStyle("menu_item"));
        optionsLabel.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SnapshotArray<Actor> options =  startMenuOptions.getChildren();
                for(Actor option : options) {
                    option.setColor(Color.WHITE);
                }
                optionsLabel.setColor(Color.YELLOW);
                selectedMenuItem = 1;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.playSelect();
                // do options stuff;
            }
        });

        final Label exitLabel = new Label("Exit", FontManager.getLabelStyle("menu_item"));
        exitLabel.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SnapshotArray<Actor> options =  startMenuOptions.getChildren();
                for(Actor option : options) {
                    option.setColor(Color.WHITE);
                }
                exitLabel.setColor(Color.YELLOW);
                selectedMenuItem = 2;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.playSelect();
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
