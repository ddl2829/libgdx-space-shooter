package com.dalesmithwebdev.arcadespaceshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;

public class GameScreen extends BaseScreen {
    public static float timeStayedAlive = 0;

    public void update(float gameTime)
    {
        timeStayedAlive += gameTime;

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
            ArcadeSpaceShooter.backgroundMusic.pause();
            ArcadeSpaceShooter.PushScreen(new PauseScreen());
            return;
        }
        ArcadeSpaceShooter.engine.update(gameTime);
    }

    public void draw(float gameTime) {
    }
}
