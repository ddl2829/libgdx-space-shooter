package com.dalesmithwebdev.arcadespaceshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.StringBuilder;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;

public class StartScreen extends BaseScreen {
    double timeSinceLastFlash = 0;
    double flashInterval = 500;
    boolean flashing = false;
    GlyphLayout glyphLayout = new GlyphLayout();
    StringBuilder sb = new StringBuilder();

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

        ArcadeSpaceShooter.engine.update(gameTime);
    }

    public void draw(float gameTime)
    {
        sb.clear();
        sb.append("Arcade Space Shooter");
        glyphLayout.setText(ArcadeSpaceShooter.bitmapFont, sb);
        ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, sb, ArcadeSpaceShooter.screenRect.width / 2 - glyphLayout.width / 2, ArcadeSpaceShooter.screenRect.height / 4);

        Color flashColor = flashing ? Color.WHITE : Color.YELLOW;

        sb.clear();
        sb.append("Press Enter to Play");
        ArcadeSpaceShooter.bitmapFont.setColor(flashColor);
        glyphLayout.setText(ArcadeSpaceShooter.bitmapFont, sb);
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, sb, ArcadeSpaceShooter.screenRect.width / 2 - glyphLayout.width / 2, ArcadeSpaceShooter.screenRect.height / 3 * 2);

        sb.clear();
        sb.append("Press Escape to Quit");
        ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);
        glyphLayout.setText(ArcadeSpaceShooter.bitmapFont, sb);
        ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, sb, ArcadeSpaceShooter.screenRect.width / 2 - glyphLayout.width / 2, ArcadeSpaceShooter.screenRect.height / 4 * 3);
    }
}
