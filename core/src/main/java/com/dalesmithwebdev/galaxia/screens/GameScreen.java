package com.dalesmithwebdev.galaxia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.screens.listeners.PauseScreenKeyboardListener;
import com.dalesmithwebdev.galaxia.systems.InputSystem;
import com.dalesmithwebdev.galaxia.systems.LevelSystem;

public class GameScreen extends ScreenAdapter {
    public static float timeStayedAlive = 0;
    private Stage ui;
    private VerticalGroup pauseMenu;
    public VerticalGroup pauseMenuOptions;
    public int selectedMenuItem = 0;
    private InputMultiplexer inputMultiplexer;
    private PauseScreenKeyboardListener pauseScreenKeyboardListener;

    private HorizontalGroup playerLives;
    private VerticalGroup hudTopLeft;

    public GameScreen() {
        ArcadeSpaceShooter.engine.addSystem(new LevelSystem());
        ArcadeSpaceShooter.engine.addSystem(new InputSystem());
        ui = new Stage();
        ui.setDebugUnderMouse(true);
        pauseMenu = new VerticalGroup();
        pauseMenuOptions = new VerticalGroup();

        playerLives = new HorizontalGroup();

        for(int i = 0; i < 4; i++) {
            Image life = new Image(ArcadeSpaceShooter.playerLivesGraphic);
            life.setAlign(Align.topLeft);
            playerLives.addActor(life);
        }

        playerLives.space(10);

        hudTopLeft = new VerticalGroup();
        hudTopLeft.addActor(playerLives);

        ProgressBar healthBar = new ProgressBar(0, 100, 1, false, ArcadeSpaceShooter.uiSkin);
        healthBar.setValue(100);
        healthBar.setColor(Color.RED);
        hudTopLeft.addActor(healthBar);
        //hudTopLeft.setWidth(ArcadeSpaceShooter.playerLivesGraphic.getRegionWidth() * 4 + (10 * 3));;
        //hudTopLeft.setDebug(true);

        hudTopLeft.setPosition(10, Gdx.graphics.getHeight() - 10);
        hudTopLeft.align(Align.topLeft);

        ui.addActor(hudTopLeft);


        Label pausedLabel = new Label("Paused", ArcadeSpaceShooter.uiSkin);
        pausedLabel.setFontScale(3);

        pauseMenu.addActor(pausedLabel);
        pauseMenu.space(Gdx.graphics.getHeight() / 4f);

        final Label resumeLabel = new Label("Resume", ArcadeSpaceShooter.uiSkin);
        resumeLabel.setFontScale(2);
        resumeLabel.setColor(Color.YELLOW);
        resumeLabel.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SnapshotArray<Actor> options =  pauseMenuOptions.getChildren();
                for(Actor option : options) {
                    option.setColor(Color.WHITE);
                }
                resumeLabel.setColor(Color.YELLOW);
                selectedMenuItem = 0;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                resumeGame();
            }
        });
        final Label exitGameLabel = new Label("Exit Game", ArcadeSpaceShooter.uiSkin);
        exitGameLabel.setFontScale(2);
        exitGameLabel.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                SnapshotArray<Actor> options =  pauseMenuOptions.getChildren();
                for(Actor option : options) {
                    option.setColor(Color.WHITE);
                }
                exitGameLabel.setColor(Color.YELLOW);
                selectedMenuItem = 1;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.exit(0);
            }
        });

        pauseMenuOptions.addActor(resumeLabel);
        pauseMenuOptions.addActor(exitGameLabel);

        pauseMenuOptions.space(50);

        pauseMenu.addActor(pauseMenuOptions);

        pauseMenu.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        pauseMenu.align(Align.center);

        pauseScreenKeyboardListener = new PauseScreenKeyboardListener(this);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(ui);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void resumeGame() {
        ArcadeSpaceShooter.paused = false;
        //ArcadeSpaceShooter.backgroundMusic.play();
        pauseMenu.remove();
        inputMultiplexer.removeProcessor(pauseScreenKeyboardListener);
    }

    @Override()
    public void render(float deltaTime)
    {
        timeStayedAlive += deltaTime;

        if (Gdx.input.isKeyJustPressed(Input.Keys.M))
        {
            if(ArcadeSpaceShooter.backgroundMusic.getVolume() == 0.0f) {
                ArcadeSpaceShooter.backgroundMusic.setVolume(1.0f);
            } else {
                ArcadeSpaceShooter.backgroundMusic.setVolume(0.0f);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            if(ArcadeSpaceShooter.paused) {
                resumeGame();
            } else {
                ArcadeSpaceShooter.paused = true;
                ArcadeSpaceShooter.backgroundMusic.pause();
                ui.addActor(pauseMenu);
                inputMultiplexer.addProcessor(pauseScreenKeyboardListener);
            }
        }

        ArcadeSpaceShooter.engine.update(deltaTime);

        ui.act(deltaTime);
        ui.draw();
    }
}
