package com.dalesmithwebdev.galaxia.listeners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dalesmithwebdev.galaxia.screens.GameScreen;

public class PauseScreenKeyboardListener implements InputProcessor {
    private GameScreen gameScreen;

    public PauseScreenKeyboardListener(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ENTER) {
            if(gameScreen.selectedMenuItem == 0) {
                gameScreen.resumeGame();
            } else if(gameScreen.selectedMenuItem == 1) {
                System.exit(0);
            }
            return false;
        }
        if(keycode == Input.Keys.UP) {
            gameScreen.selectedMenuItem--;
        }
        if(keycode == Input.Keys.DOWN) {
            gameScreen.selectedMenuItem++;
        }
        if(gameScreen.selectedMenuItem > 1) {
            gameScreen.selectedMenuItem = 0;
        } else if(gameScreen.selectedMenuItem < 0) {
            gameScreen.selectedMenuItem = 1;
        }
        SnapshotArray<Actor> options =  gameScreen.pauseMenuOptions.getChildren();
        for(Actor option : options) {
            option.setColor(Color.WHITE);
        }
        options.get(gameScreen.selectedMenuItem).setColor(Color.YELLOW);
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
