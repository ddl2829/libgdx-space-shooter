package com.dalesmithwebdev.arcadespaceshooter.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.BackgroundObjectComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.PositionComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.RenderComponent;
import com.dalesmithwebdev.arcadespaceshooter.prefabs.BackgroundElement;

public class BackgroundScreen extends BaseScreen {

    public BackgroundScreen()
    {
        Entity background = new Entity();
        background.add(new PositionComponent(ArcadeSpaceShooter.screenRect.width / 2, ArcadeSpaceShooter.screenRect.height / 2));
        background.add(new RenderComponent(ArcadeSpaceShooter.background, (int)ArcadeSpaceShooter.screenRect.width, (int)ArcadeSpaceShooter.screenRect.height, RenderComponent.PLANE_BACKGROUND_IMAGE));
        ArcadeSpaceShooter.engine.addEntity(background);
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
