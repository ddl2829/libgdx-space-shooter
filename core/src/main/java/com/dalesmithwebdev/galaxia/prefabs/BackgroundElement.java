package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.BackgroundObjectComponent;
import com.dalesmithwebdev.galaxia.components.PositionComponent;
import com.dalesmithwebdev.galaxia.components.RenderComponent;
import com.dalesmithwebdev.galaxia.components.SpeedComponent;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.Rand;

public class BackgroundElement extends Entity implements Pool.Poolable {
    public BackgroundElement() {
        TextureRegion chosenTexture = ArcadeSpaceShooter.backgroundElements.get(Rand.nextInt(ArcadeSpaceShooter.backgroundElements.size()));
        this.add(new RenderComponent(chosenTexture, RenderComponent.PLANE_BACKGROUND_OBJECTS));
        this.add(new PositionComponent(Rand.nextInt(0, (int)ArcadeSpaceShooter.screenRect.width), ArcadeSpaceShooter.screenRect.height + chosenTexture.getRegionHeight() + Rand.nextInt(300)));
        this.add(new SpeedComponent(0, -Rand.nextInt(6) - 3));
        this.add(new BackgroundObjectComponent());
    }

    @Override
    public void reset() {
        PositionComponent pc = ComponentMap.positionComponentComponentMapper.get(this);
        pc.position = new Vector2(Rand.nextInt(0, (int)ArcadeSpaceShooter.screenRect.width), ArcadeSpaceShooter.screenRect.height + 30 + Rand.nextInt(300));
    }
}
