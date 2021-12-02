package com.dalesmithwebdev.arcadespaceshooter.screens.tests;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.PositionComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.RenderComponent;
import com.dalesmithwebdev.arcadespaceshooter.screens.BaseScreen;

public class ShaderTestScreen extends BaseScreen {

    public ShaderTestScreen() {
        super();
        Entity shaderEntity = new Entity();
        RenderComponent rc = new RenderComponent(ArcadeSpaceShooter.enemyShip, 200, 200, RenderComponent.PLANE_MAIN);
        rc.shader = ArcadeSpaceShooter.outlineShader;
        shaderEntity.add(rc);
        shaderEntity.add(new PositionComponent(ArcadeSpaceShooter.screenRect.width / 2, ArcadeSpaceShooter.screenRect.height / 2));
        ArcadeSpaceShooter.engine.addEntity(shaderEntity);
    }

    @Override
    public void update(float gameTime) {
        ArcadeSpaceShooter.engine.update(gameTime);
    }

    @Override
    public void draw(float gameTime) {

    }
}
