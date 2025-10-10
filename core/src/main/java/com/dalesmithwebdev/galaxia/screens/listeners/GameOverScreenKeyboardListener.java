package com.dalesmithwebdev.galaxia.screens.listeners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.screens.GameOverScreen;
import com.dalesmithwebdev.galaxia.screens.GameScreen;

public class GameOverScreenKeyboardListener implements InputProcessor {
    private final GameOverScreen gameOverScreen;

    public GameOverScreenKeyboardListener(GameOverScreen gameOverScreen) {
        this.gameOverScreen = gameOverScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ENTER) {
            if(gameOverScreen.selectedMenuItem == 0) {
                ArcadeSpaceShooter.playerScore = 0;
                ArcadeSpaceShooter.kills = 0;
                ArcadeSpaceShooter.instance.setScreen(new GameScreen());
            } else if(gameOverScreen.selectedMenuItem == 1) {
                System.exit(0);
            }
            return false;
        }
        if(keycode == Input.Keys.UP) {
            gameOverScreen.selectedMenuItem--;
        }
        if(keycode == Input.Keys.DOWN) {
            gameOverScreen.selectedMenuItem++;
        }
        if(gameOverScreen.selectedMenuItem > 1) {
            gameOverScreen.selectedMenuItem = 0;
        } else if(gameOverScreen.selectedMenuItem < 0) {
            gameOverScreen.selectedMenuItem = 1;
        }
        SnapshotArray<Actor> options =  gameOverScreen.gameOverMenuOptions.getChildren();
        for(Actor option : options) {
            option.setColor(Color.WHITE);
        }
        options.get(gameOverScreen.selectedMenuItem).setColor(Color.YELLOW);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
