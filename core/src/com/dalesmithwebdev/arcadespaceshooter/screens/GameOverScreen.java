package com.dalesmithwebdev.arcadespaceshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.systems.LevelSystem;

public class GameOverScreen extends ScreenAdapter {
    boolean flashing = false;
    double timeSinceLastFlash = 0;
    double flashInterval = 500;

    @Override
    public void render(float gameTime)
    {
        timeSinceLastFlash += gameTime;
        if (timeSinceLastFlash > flashInterval)
        {
            flashing = !flashing;
            timeSinceLastFlash = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
        {
            ArcadeSpaceShooter.playerScore = 0;
            ArcadeSpaceShooter.kills = 0;
            ArcadeSpaceShooter.instance.setScreen(new GameScreen());
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            Gdx.app.exit();
        }

        ArcadeSpaceShooter.engine.update(gameTime);

        ArcadeSpaceShooter.spriteBatch.begin();
        ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, "Game Over", (int)ArcadeSpaceShooter.screenRect.width / 2.0f - ArcadeSpaceShooter.measureText("Game Over") / 2,  ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.screenRect.height / 3);
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch,  "Score: " + ((int)ArcadeSpaceShooter.playerScore), (int)ArcadeSpaceShooter.screenRect.width / 2.0f - ArcadeSpaceShooter.measureText("Score: " + ((int)ArcadeSpaceShooter.playerScore)) / 2, ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.screenRect.height / 3 - 20);
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, "Level: " + LevelSystem.levelNumber, (int)ArcadeSpaceShooter.screenRect.width / 2.0f - ArcadeSpaceShooter.measureText("Level: " + LevelSystem.levelNumber) / 2, ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.screenRect.height / 3 - 40);
        Color flashColor = flashing ? Color.WHITE : Color.YELLOW;
        ArcadeSpaceShooter.bitmapFont.setColor(flashColor);
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, "Press Enter to Play Again", (int)ArcadeSpaceShooter.screenRect.width / 2.0f - ArcadeSpaceShooter.measureText("Press Enter to Play Again") / 2, ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.screenRect.height / 2);
        ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, "Press Escape to Quit", (int)ArcadeSpaceShooter.screenRect.width / 2.0f - ArcadeSpaceShooter.measureText("Press Escape to Quit") / 2, ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.screenRect.height / 2 - 20);
        ArcadeSpaceShooter.spriteBatch.end();
    }
}
