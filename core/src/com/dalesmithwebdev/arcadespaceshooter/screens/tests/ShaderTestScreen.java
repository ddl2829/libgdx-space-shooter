package com.dalesmithwebdev.arcadespaceshooter.screens.tests;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ScreenAdapter;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.PositionComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.RenderComponent;

public class ShaderTestScreen extends ScreenAdapter {

    public ShaderTestScreen() {
        super();
        Entity shaderEntity = new Entity();
        RenderComponent rc = new RenderComponent(ArcadeSpaceShooter.textures.findRegion("enemyShip"), 200, 200, RenderComponent.PLANE_MAIN);
        rc.shader = ArcadeSpaceShooter.outlineShader;
        rc.stickyShader = true;
        shaderEntity.add(rc);
        shaderEntity.add(new PositionComponent(ArcadeSpaceShooter.screenRect.width / 2, ArcadeSpaceShooter.screenRect.height / 2));
        ArcadeSpaceShooter.engine.addEntity(shaderEntity);
    }

    @Override
    public void render(float gameTime) {
        ArcadeSpaceShooter.engine.update(gameTime);
    }
}
