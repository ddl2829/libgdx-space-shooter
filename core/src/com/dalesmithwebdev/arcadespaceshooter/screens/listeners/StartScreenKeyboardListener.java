package com.dalesmithwebdev.arcadespaceshooter.screens.listeners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.SnapshotArray;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.screens.GameScreen;
import com.dalesmithwebdev.arcadespaceshooter.screens.StartScreen;

public class StartScreenKeyboardListener implements InputProcessor {
    private StartScreen startScreen;

    public StartScreenKeyboardListener(StartScreen titleScreen) {
        this.startScreen = titleScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ENTER) {
            if(startScreen.selectedMenuItem == 0) {
                ArcadeSpaceShooter.instance.setScreen(new GameScreen());
            } else if(startScreen.selectedMenuItem == 1) {
                // options picked
            } else if(startScreen.selectedMenuItem == 2) {
                System.exit(0);
            }
            return false;
        }
        if(keycode == Input.Keys.UP) {
            startScreen.selectedMenuItem--;
        }
        if(keycode == Input.Keys.DOWN) {
            startScreen.selectedMenuItem++;
        }
        if(startScreen.selectedMenuItem > 2) {
            startScreen.selectedMenuItem = 0;
        } else if(startScreen.selectedMenuItem < 0) {
            startScreen.selectedMenuItem = 2;
        }
        SnapshotArray<Actor> options =  startScreen.startMenuOptions.getChildren();
        for(Actor option : options) {
            option.setColor(Color.WHITE);
        }
        options.get(startScreen.selectedMenuItem).setColor(Color.YELLOW);
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
