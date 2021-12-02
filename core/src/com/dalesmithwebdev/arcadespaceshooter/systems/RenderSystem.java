package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.StringBuilder;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class RenderSystem extends EntitySystem {

    private StringBuilder sb = new StringBuilder();
    private FrameBuffer fbo = new FrameBuffer(
            Pixmap.Format.RGBA8888,
            (int)ArcadeSpaceShooter.screenRect.width,
            (int)ArcadeSpaceShooter.screenRect.height,
            false
    );

    public void RenderList(Iterable<Entity> sprites) {
        for(Entity drawable : sprites)
        {
            PositionComponent pc = ComponentMap.positionComponentComponentMapper.get(drawable);
            RenderComponent rc = ComponentMap.renderComponentComponentMapper.get(drawable);

            if(ComponentMap.recentlyDamagedComponentComponentMapper.has(drawable)) {
                rc.shader = ArcadeSpaceShooter.outlineShader;
            } else {
                if(!rc.stickyShader) {
                    rc.shader = null;
                }
            }

            if (rc.visible)
            {
                if(rc.shader != null) {
                    if(rc.shader.hasUniform("iResolution")) {
                        rc.shader.setUniformf("iResolution", new Vector2(ArcadeSpaceShooter.screenRect.width, ArcadeSpaceShooter.screenRect.height));
                    }
                    if(rc.shader.hasUniform("texSize")) {
                        rc.shader.setUniformf("texSize", new Vector2(rc.CurrentTexture().getTexture().getWidth(), rc.CurrentTexture().getTexture().getHeight()));
                    }
                }
                if(ComponentMap.explosionComponentComponentMapper.has(drawable)) {
                    ExplosionComponent ec = ComponentMap.explosionComponentComponentMapper.get(drawable);
                    ArcadeSpaceShooter.spriteBatch.draw(rc.CurrentTexture(), (int) pc.position.x - ec.radius, (int) pc.position.y - ec.radius, ec.radius * 2, ec.radius * 2);
                } else {
                    ArcadeSpaceShooter.spriteBatch.draw(rc.CurrentTexture(), (int) pc.position.x - (rc.width / 2.0f), (int) pc.position.y - (rc.height / 2.0f), rc.width, rc.height);
                }

                if (ComponentMap.playerComponentComponentMapper.has(drawable))
                {
                    if(ComponentMap.shieldedComponentComponentMapper.has(drawable))
                    {
                        ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.playerShield, (int)(pc.position.x - rc.width / 2) - 25, (int)(pc.position.y - rc.height / 2) - 10, rc.width, rc.height);
                    }
                }

                // Put a little flame trail behind missiles that have entered their second phase
                if(ComponentMap.missileComponentComponentMapper.has(drawable)) {
                    MissileComponent mc = ComponentMap.missileComponentComponentMapper.get(drawable);
                    if(mc.speedBoosted) {
                        ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.fireEffect, pc.position.x - (ArcadeSpaceShooter.fireEffect.getRegionWidth() / 2.0f) - 1, pc.position.y - ArcadeSpaceShooter.fireEffect.getRegionHeight() - rc.height / 2.0f - 3);
                    }
                }
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> sprites = this.getEngine().getEntitiesFor(Family.all(RenderComponent.class, PositionComponent.class).get());
        TreeMap<Integer, HashMap<ShaderProgram, List<Entity>>> layers = new TreeMap<>();
        for(Entity e : sprites) {
            RenderComponent rc = ComponentMap.renderComponentComponentMapper.get(e);
            if(!layers.containsKey(rc.zIndex)) {
                layers.put(rc.zIndex, new HashMap<ShaderProgram, List<Entity>>());
            }
            HashMap<ShaderProgram, List<Entity>> thisLayer = layers.get(rc.zIndex);
            if(!thisLayer.containsKey(rc.shader)) {
                thisLayer.put(rc.shader, new ArrayList<Entity>());
            }
            if(rc.shader != null) {
                rc.shaderTime += deltaTime;
            }
            List<Entity> thisShader = thisLayer.get(rc.shader);
            thisShader.add(e);
        }

        fbo.begin();
        Gdx.gl.glClearColor(149.0f/255.0f, 50.0f/255.0f, 168.0f/255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for(Integer layer : layers.keySet()) {
            HashMap<ShaderProgram, List<Entity>> thisLayer = layers.get(layer);
            for(ShaderProgram shader : thisLayer.keySet()) {
                if(shader == ArcadeSpaceShooter.outlineShader) {
                    ArcadeSpaceShooter.spriteBatch.begin();
                    ArcadeSpaceShooter.spriteBatch.setShader(null);
                    RenderList(thisLayer.get(shader));
                    ArcadeSpaceShooter.spriteBatch.setShader(shader);
                    RenderList(thisLayer.get(shader));
                    ArcadeSpaceShooter.spriteBatch.end();
                } else {
                    ArcadeSpaceShooter.spriteBatch.begin();
                    ArcadeSpaceShooter.spriteBatch.setShader(shader);
                    RenderList(thisLayer.get(shader));
                    ArcadeSpaceShooter.spriteBatch.end();
                }
            }
        }
        fbo.end();

        Texture texture = fbo.getColorBufferTexture();
        Sprite sprite = new Sprite(texture);
        sprite.flip(false, true);

        ArcadeSpaceShooter.spriteBatch.begin();
        ArcadeSpaceShooter.spriteBatch.setShader(ArcadeSpaceShooter.vignetteShader);
        ArcadeSpaceShooter.spriteBatch.draw(sprite, 0, 0, ArcadeSpaceShooter.screenRect.width, ArcadeSpaceShooter.screenRect.height);
        ArcadeSpaceShooter.spriteBatch.end();

        ArcadeSpaceShooter.spriteBatch.setShader(null);

        ArcadeSpaceShooter.spriteBatch.begin();
        ImmutableArray<Entity> players = this.getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        if (players.size() > 0)
        {
            Entity player = players.first();

            if(ComponentMap.hasBombsComponentComponentMapper.has(player)) {
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.bomb, 5, 5, ArcadeSpaceShooter.bomb.getRegionWidth(), ArcadeSpaceShooter.bomb.getRegionHeight());
            }

            if(ComponentMap.hasMissilesComponentComponentMapper.has(player)) {
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.missile, 30, 5, ArcadeSpaceShooter.missile.getRegionWidth(), ArcadeSpaceShooter.missile.getRegionHeight());
            }

            PlayerComponent pc = ComponentMap.playerComponentComponentMapper.get(player);
            TakesDamageComponent ptdc = ComponentMap.takesDamageComponentComponentMapper.get(player);
            int livesY = (int)ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.playerLivesGraphic.getRegionHeight() - 10;
            for (int i = 0; i < pc.lives; i++)
            {
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.playerLivesGraphic, 40 * i + 10, livesY, ArcadeSpaceShooter.playerLivesGraphic.getRegionWidth(), ArcadeSpaceShooter.playerLivesGraphic.getRegionHeight());
            }

            //String scoreText = "" + Math.floor(ArcadeSpaceShooter.playerScore);
            sb.clear();
            sb.append((int)Math.floor(ArcadeSpaceShooter.playerScore));
            ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);
            ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, sb, ArcadeSpaceShooter.screenRect.width - ArcadeSpaceShooter.measureText(sb) - 10, ArcadeSpaceShooter.screenRect.height - 50);

            // debug
            int frames = Gdx.graphics.getFramesPerSecond();
            //String fps = "" +frames;
            sb.clear();
            sb.append(frames);
            ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);
            ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, sb, ArcadeSpaceShooter.screenRect.width - ArcadeSpaceShooter.measureText(sb) - 10, ArcadeSpaceShooter.screenRect.height - 80);

            RenderComponent player_rc = ComponentMap.renderComponentComponentMapper.get(player);
            if(player_rc.visible) {
                ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 8, livesY - 13, 100, 12);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 12, 98, 10);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.RED);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 12, (int) (((double) ptdc.health / ptdc.maxHealth) * 98), 10);

                if (ComponentMap.hasShieldComponentComponentMapper.has(player)) {
                    HasShieldComponent hasShieldComponent = ComponentMap.hasShieldComponentComponentMapper.get(player);
                    ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
                    ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 8, livesY - 30, 100, 12);
                    ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
                    ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 29, 98, 10);
                    ArcadeSpaceShooter.spriteBatch.setColor(Color.BLUE);
                    ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 29, (int) ((hasShieldComponent.shieldPower / hasShieldComponent.maxShieldPower) * 98), 10);

                    if (hasShieldComponent.shieldCooldown) {
                        ArcadeSpaceShooter.spriteBatch.setColor(Color.PURPLE);
                        ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 31, (int) (Math.min((hasShieldComponent.shieldPower / hasShieldComponent.maxShieldPower), 1) * 98), 10);
                    }
                }
                ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
            }
        }

        ImmutableArray<Entity> boss = this.getEngine().getEntitiesFor(Family.all(BossEnemyComponent.class).get());
        if(boss.size() > 0) {
            // Check if the boss is on screen
            PositionComponent bossPos = ComponentMap.positionComponentComponentMapper.get(boss.first());
            if(bossPos.position.y < ArcadeSpaceShooter.screenRect.height) {
                // boss is on screen
                // draw a health bar for them

                TakesDamageComponent bossTdc = ComponentMap.takesDamageComponentComponentMapper.get(boss.first());

                ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, ArcadeSpaceShooter.screenRect.width - 200, ArcadeSpaceShooter.screenRect.height - 30, 190, 20);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, ArcadeSpaceShooter.screenRect.width - 199, ArcadeSpaceShooter.screenRect.height - 29, 188, 18);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.RED);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, ArcadeSpaceShooter.screenRect.width - 199, ArcadeSpaceShooter.screenRect.height - 29, (int)(((double)bossTdc.health / bossTdc.maxHealth) * 188), 18);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
            }
        }


        ImmutableArray<Entity> notifications = this.getEngine().getEntitiesFor(Family.all(NotificationComponent.class).get());
        for (Entity notification : notifications)
        {
            NotificationComponent notificationComponent = ComponentMap.notificationComponentComponentMapper.get(notification);
            notificationComponent.elapsedTime += deltaTime;
            if (notificationComponent.elapsedTime > notificationComponent.maxLife)
            {
                this.getEngine().removeEntity(notification);
                continue;
            }

            ArcadeSpaceShooter.bitmapFont.setColor(notificationComponent.color);
            if (notificationComponent.centerText)
            {
                ArcadeSpaceShooter.bitmapFont.draw(
                        ArcadeSpaceShooter.spriteBatch,
                        notificationComponent.text,
                        ArcadeSpaceShooter.screenRect.width / 2 - ArcadeSpaceShooter.measureText(notificationComponent.text) / 2,
                        ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.screenRect.height / 3
                );
            }
            else
            {
                if(ComponentMap.positionComponentComponentMapper.has(notification))
                {
                    PositionComponent pc = ComponentMap.positionComponentComponentMapper.get(notification);
                    ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, notificationComponent.text, pc.position.x, pc.position.y);
                }
            }
        }

        ArcadeSpaceShooter.spriteBatch.end();
    }
}
