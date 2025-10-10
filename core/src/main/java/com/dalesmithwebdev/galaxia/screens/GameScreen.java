package com.dalesmithwebdev.galaxia.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
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
import com.dalesmithwebdev.galaxia.components.HasShieldComponent;
import com.dalesmithwebdev.galaxia.components.PlayerComponent;
import com.dalesmithwebdev.galaxia.components.TakesDamageComponent;
import com.dalesmithwebdev.galaxia.screens.listeners.PauseScreenKeyboardListener;
import com.dalesmithwebdev.galaxia.systems.LevelSystem;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.FontManager;
import com.dalesmithwebdev.galaxia.utility.SoundManager;

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
    private ProgressBar healthBar;
    private ProgressBar shieldBar;

    public GameScreen() {
        System.out.println(">>> GameScreen: Constructor called!");
        System.out.println(">>> GameScreen: Adding LevelSystem to engine");
        LevelSystem levelSystem = new LevelSystem();
        ArcadeSpaceShooter.engine.addSystem(levelSystem);
        System.out.println(">>> GameScreen: Creating UI stage");
        ui = new Stage();
        ui.setDebugUnderMouse(true);
        pauseMenu = new VerticalGroup();
        pauseMenuOptions = new VerticalGroup();

        // Calculate consistent width based on lives graphic
        float hudWidth = ArcadeSpaceShooter.playerLivesGraphic.getRegionWidth() * 4 + (10 * 3);

        // Player lives
        playerLives = new HorizontalGroup();
        for(int i = 0; i < 4; i++) {
            Image life = new Image(ArcadeSpaceShooter.playerLivesGraphic);
            life.setAlign(Align.topLeft);
            playerLives.addActor(life);
        }
        playerLives.space(10);

        // Health bar
        healthBar = new ProgressBar(0, 100, 1, false, ArcadeSpaceShooter.uiSkin);
        healthBar.setValue(100);
        healthBar.setColor(Color.RED);
        healthBar.setSize(hudWidth, ArcadeSpaceShooter.playerLivesGraphic.getRegionHeight());

        // Shield bar
        shieldBar = new ProgressBar(0, 100, 1, false, ArcadeSpaceShooter.uiSkin);
        shieldBar.setValue(100);
        shieldBar.setColor(Color.CYAN);
        shieldBar.setSize(hudWidth, ArcadeSpaceShooter.playerLivesGraphic.getRegionHeight());
        shieldBar.setVisible(false); // Hidden until player has shield

        // Assemble HUD
        hudTopLeft = new VerticalGroup();
        hudTopLeft.space(5);
        hudTopLeft.addActor(playerLives);
        hudTopLeft.addActor(healthBar);
        hudTopLeft.addActor(shieldBar);

        hudTopLeft.setPosition(10, Gdx.graphics.getHeight() - 10);
        hudTopLeft.align(Align.topLeft);

        ui.addActor(hudTopLeft);

        // Pause menu with custom fonts
        Label pausedLabel = new Label("PAUSED", FontManager.getLabelStyle("title"));
        pausedLabel.setAlignment(Align.center);

        pauseMenu.addActor(pausedLabel);
        pauseMenu.space(Gdx.graphics.getHeight() / 4f);

        final Label resumeLabel = new Label("Resume", FontManager.getLabelStyle("menu_item"));
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
                SoundManager.playSelect();
                resumeGame();
            }
        });
        final Label exitGameLabel = new Label("Exit Game", FontManager.getLabelStyle("menu_item"));
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
                SoundManager.playSelect();
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

        // Trigger initial level build
        System.out.println(">>> GameScreen: Triggering initial level build");
        levelSystem.startInitialLevel();
        System.out.println(">>> GameScreen: Constructor complete");
    }

    public void resumeGame() {
        ArcadeSpaceShooter.paused = false;
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
                SoundManager.playPause();
                ui.addActor(pauseMenu);
                inputMultiplexer.addProcessor(pauseScreenKeyboardListener);
            }
        }

        // Update HUD bars and lives
        ImmutableArray<Entity> playerEntities = ArcadeSpaceShooter.engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
        if (playerEntities.size() > 0) {
            Entity player = playerEntities.first();

            // Update lives display
            PlayerComponent playerComp = ComponentMap.playerMapper.get(player);
            if (playerComp != null) {
                // Clear existing life icons
                playerLives.clearChildren();
                // Add correct number of life icons
                for (int i = 0; i < playerComp.lives; i++) {
                    Image life = new Image(ArcadeSpaceShooter.playerLivesGraphic);
                    life.setAlign(Align.topLeft);
                    playerLives.addActor(life);
                }
            }

            // Update health bar
            TakesDamageComponent health = ComponentMap.takesDamageMapper.get(player);
            if (health != null) {
                float healthPercent = (float) health.health / health.maxHealth * 100f;
                healthBar.setValue(healthPercent);
            }

            // Update shield bar
            HasShieldComponent shield = ComponentMap.hasShieldMapper.get(player);
            if (shield != null) {
                shieldBar.setVisible(true);
                float shieldPercent = (float) (shield.shieldPower / shield.maxShieldPower * 100f);
                shieldBar.setValue(shieldPercent);
            } else {
                shieldBar.setVisible(false);
            }
        }

        ArcadeSpaceShooter.engine.update(deltaTime);

        ui.act(deltaTime);
        ui.draw();
    }
}
