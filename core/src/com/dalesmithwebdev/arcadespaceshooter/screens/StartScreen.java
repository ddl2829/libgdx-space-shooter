package com.dalesmithwebdev.arcadespaceshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;

public class StartScreen extends BaseScreen {
    double timeSinceLastFlash = 0;
    double flashInterval = 500;
    boolean flashing = false;
    GlyphLayout glyphLayout = new GlyphLayout();

    public void update(float gameTime)
    {
        timeSinceLastFlash += gameTime;
        if (timeSinceLastFlash > flashInterval)
        {
            flashing = !flashing;
            timeSinceLastFlash = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
        {
            ArcadeSpaceShooter.PopScreen();
            ArcadeSpaceShooter.PushScreen(new GameScreen());
            //ArcadeSpaceShooter.backgroundMusic.play();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            Gdx.app.exit();
        }
    }

    public void draw(float gameTime)
    {
        glyphLayout.setText(ArcadeSpaceShooter.bitmapFont, "Arcade Space Shooter");
        ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, "Arcade Space Shooter", ArcadeSpaceShooter.screenRect.width / 2 - glyphLayout.width / 2, ArcadeSpaceShooter.screenRect.height / 4);

        Color flashColor = flashing ? Color.WHITE : Color.YELLOW;

        ArcadeSpaceShooter.bitmapFont.setColor(flashColor);
        glyphLayout.setText(ArcadeSpaceShooter.bitmapFont, "Press Enter to Play");
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, "Press Enter to Play", ArcadeSpaceShooter.screenRect.width / 2 - glyphLayout.width / 2, ArcadeSpaceShooter.screenRect.height / 3 * 2);

        ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);
        glyphLayout.setText(ArcadeSpaceShooter.bitmapFont, "Press Escape to Quit");
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, "Press Escape to Quit", ArcadeSpaceShooter.screenRect.width / 2 - glyphLayout.width / 2, ArcadeSpaceShooter.screenRect.height / 4 * 3);
    }
}
