package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.BackgroundObjectComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.PositionComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.RenderComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.SpeedComponent;
import com.dalesmithwebdev.arcadespaceshooter.utility.Rand;

public class BackgroundElement extends Entity {
    public BackgroundElement() {
        Texture chosenTexture = ArcadeSpaceShooter.backgroundElements.get(Rand.nextInt(ArcadeSpaceShooter.backgroundElements.size()));
        this.add(new RenderComponent(chosenTexture, RenderComponent.PLANE_BACKGROUND_OBJECTS));
        this.add(new PositionComponent(Rand.nextInt(0, (int)ArcadeSpaceShooter.screenRect.width), ArcadeSpaceShooter.screenRect.height + chosenTexture.getHeight() + Rand.nextInt(300)));
        this.add(new SpeedComponent(0, -Rand.nextInt(6) - 3));
        this.add(new BackgroundObjectComponent());
    }
}
