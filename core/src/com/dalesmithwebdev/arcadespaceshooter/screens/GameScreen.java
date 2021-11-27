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
            //MediaPlayer.Volume = 0.0f;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            //MediaPlayer.Pause();
            ArcadeSpaceShooter.backgroundMusic.pause();
            ArcadeSpaceShooter.PushScreen(new PauseScreen());
            return;
        }
        ArcadeSpaceShooter.engine.update(gameTime);
        //ArcadeSpaceShooter.instance.world.Draw(spriteBatch, spriteFont);
    }

    public void draw(float gameTime) {
        //ArcadeSpaceShooter.engine.update(gameTime);
    }
}
