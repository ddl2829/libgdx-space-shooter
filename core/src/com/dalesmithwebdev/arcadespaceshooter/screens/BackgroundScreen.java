package com.dalesmithwebdev.arcadespaceshooter.screens;

import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.entities.BackgroundElement;

import java.util.ArrayList;

public class BackgroundScreen extends BaseScreen {
    ArrayList<BackgroundElement> backgroundObjects;

    public BackgroundScreen()
    {
        backgroundObjects = new ArrayList<BackgroundElement>();
    }

    public void update(float gameTime)
    {
        if (backgroundObjects.size() < 15)
        {
            backgroundObjects.add(new BackgroundElement(ArcadeSpaceShooter.backgroundElements, ArcadeSpaceShooter.screenRect));
        }
        //Update background objects
        for (int i = backgroundObjects.size() - 1; i >= 0; i--)
        {
            backgroundObjects.get(i).update(gameTime);
            if (backgroundObjects.get(i).belowScreen)
            {
                backgroundObjects.remove(i);
            }
        }
    }

    public void draw(float gameTime) {
        ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.background, 0, 0, ArcadeSpaceShooter.screenRect.width, ArcadeSpaceShooter.screenRect.height);
        //Update background objects
        for (int i = backgroundObjects.size() - 1; i >= 0; i--)
        {
            backgroundObjects.get(i).draw(ArcadeSpaceShooter.spriteBatch);
        }
    }
}
