package com.dalesmithwebdev.arcadespaceshooter.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.BackgroundObjectComponent;
import com.dalesmithwebdev.arcadespaceshooter.prefabs.BackgroundElement;

import java.util.ArrayList;

public class BackgroundScreen extends BaseScreen {

    public BackgroundScreen()
    {
    }

    public void update(float gameTime)
    {
        ImmutableArray<Entity> backgroundObjects = ArcadeSpaceShooter.engine.getEntitiesFor(Family.all(BackgroundObjectComponent.class).get());

        if (backgroundObjects.size() < 15)
        {
            ArcadeSpaceShooter.engine.addEntity(new BackgroundElement());
        }
    }

    public void draw(float gameTime) {
    }
}
