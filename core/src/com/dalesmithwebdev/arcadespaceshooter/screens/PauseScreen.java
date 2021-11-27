package com.dalesmithwebdev.arcadespaceshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;

public class PauseScreen extends BaseScreen {
    GlyphLayout glyphLayout = new GlyphLayout();

    public PauseScreen()
    {
        pausesBelow = true;
    }

    public void update(float gameTime)
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            ArcadeSpaceShooter.backgroundMusic.pause();
            ArcadeSpaceShooter.PopScreen();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
        {
            Gdx.app.exit();
            return;
        }
    }

    public void draw(float gameTime)
    {
        ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);

        glyphLayout.setText(ArcadeSpaceShooter.bitmapFont, "Paused");
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, "Paused", ArcadeSpaceShooter.screenRect.width / 2 - glyphLayout.width / 2, ArcadeSpaceShooter.screenRect.height / 3);

        glyphLayout.setText(ArcadeSpaceShooter.bitmapFont, "Press Enter to End Game");
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, "Press Enter to End Game", ArcadeSpaceShooter.screenRect.width / 2 - glyphLayout.width / 2, ArcadeSpaceShooter.screenRect.height / 2);
    }
}
