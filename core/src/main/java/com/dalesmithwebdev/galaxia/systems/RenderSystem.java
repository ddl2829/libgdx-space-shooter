package com.dalesmithwebdev.galaxia.systems;

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
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.services.GameStateService;
import com.dalesmithwebdev.galaxia.services.ServiceLocator;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.FontManager;

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
            PositionComponent pc = ComponentMap.positionMapper.get(drawable);
            RenderComponent rc = ComponentMap.renderMapper.get(drawable);

            if(ComponentMap.recentlyDamagedMapper.has(drawable)) {
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
                if(ComponentMap.explosionMapper.has(drawable)) {
                    ExplosionComponent ec = ComponentMap.explosionMapper.get(drawable);
                    ArcadeSpaceShooter.spriteBatch.draw(rc.CurrentTexture(), (int) pc.position.x - ec.radius, (int) pc.position.y - ec.radius, ec.radius * 2, ec.radius * 2);
                } else {
                    ArcadeSpaceShooter.spriteBatch.draw(rc.CurrentTexture(), (int) pc.position.x - (rc.width / 2.0f), (int) pc.position.y - (rc.height / 2.0f), rc.width, rc.height);
                }

                if (ComponentMap.playerMapper.has(drawable))
                {
                    if(ComponentMap.shieldedMapper.has(drawable))
                    {
                        ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.playerShield, (int)(pc.position.x - rc.width / 2) - 25, (int)(pc.position.y - rc.height / 2) - 10, rc.width, rc.height);
                    }
                }

                // Put a little flame trail behind missiles that have entered their second phase
                if(ComponentMap.missileMapper.has(drawable)) {
                    MissileComponent mc = ComponentMap.missileMapper.get(drawable);
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
            RenderComponent rc = ComponentMap.renderMapper.get(e);
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

            if(ComponentMap.hasBombsMapper.has(player)) {
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.bomb, 5, 5, ArcadeSpaceShooter.bomb.getRegionWidth(), ArcadeSpaceShooter.bomb.getRegionHeight());
            }

            if(ComponentMap.hasMissilesMapper.has(player)) {
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.missile, 30, 5, ArcadeSpaceShooter.missile.getRegionWidth(), ArcadeSpaceShooter.missile.getRegionHeight());
            }

            PlayerComponent pc = ComponentMap.playerMapper.get(player);
            TakesDamageComponent ptdc = ComponentMap.takesDamageMapper.get(player);
            int livesY = (int)ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.playerLivesGraphic.getRegionHeight() - 10;
//            for (int i = 0; i < pc.lives; i++)
//            {
//                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.playerLivesGraphic, 40 * i + 10, livesY, ArcadeSpaceShooter.playerLivesGraphic.getRegionWidth(), ArcadeSpaceShooter.playerLivesGraphic.getRegionHeight());
//            }

            // Score display with custom monospace font
            GameStateService gameState = ServiceLocator.getInstance().getGameState();
            sb.clear();
            sb.append((int)Math.floor(gameState.getPlayerScore()));
            String scoreText = sb.toString();
            float scoreWidth = FontManager.getTextWidth(scoreText, "score");
            FontManager.getLabelStyle("score").font.setColor(Color.YELLOW);
            FontManager.getLabelStyle("score").font.draw(
                ArcadeSpaceShooter.spriteBatch,
                scoreText,
                ArcadeSpaceShooter.screenRect.width - scoreWidth - 10,
                ArcadeSpaceShooter.screenRect.height - 10
            );

            // FPS counter with HUD font
            int frames = Gdx.graphics.getFramesPerSecond();
            sb.clear();
            sb.append(frames);
            String fpsText = sb.toString();
            float fpsWidth = FontManager.getTextWidth(fpsText, "hud_text");
            FontManager.getLabelStyle("hud_text").font.setColor(Color.WHITE);
            FontManager.getLabelStyle("hud_text").font.draw(
                ArcadeSpaceShooter.spriteBatch,
                fpsText,
                ArcadeSpaceShooter.screenRect.width - fpsWidth - 10,
                ArcadeSpaceShooter.screenRect.height - 50
            );

            if(ComponentMap.renderMapper.has(player)) {
                RenderComponent player_rc = ComponentMap.renderMapper.get(player);
                if (player_rc.visible) {
//                    ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
//                    ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 8, livesY - 13, 100, 12);
//                    ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
//                    ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 12, 98, 10);
//                    ArcadeSpaceShooter.spriteBatch.setColor(Color.RED);
//                    ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 12, (int) (((double) ptdc.health / ptdc.maxHealth) * 98), 10);

                    if (ComponentMap.hasShieldMapper.has(player)) {
                        HasShieldComponent hasShieldComponent = ComponentMap.hasShieldMapper.get(player);
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
        }

        ImmutableArray<Entity> boss = this.getEngine().getEntitiesFor(Family.all(BossEnemyComponent.class).get());
        if(boss.size() > 0) {
            // Check if the boss is on screen
            PositionComponent bossPos = ComponentMap.positionMapper.get(boss.first());
            if(bossPos.position.y < ArcadeSpaceShooter.screenRect.height) {
                // boss is on screen
                // draw a health bar for them in the center top

                TakesDamageComponent bossTdc = ComponentMap.takesDamageMapper.get(boss.first());

                // Center the boss HP bar at the top of the screen
                float barWidth = 400;
                float barHeight = 30;
                float barX = (ArcadeSpaceShooter.screenRect.width - barWidth) / 2;
                float barY = ArcadeSpaceShooter.screenRect.height - barHeight - 10;

                // Boss label
                String bossLabel = "BOSS";
                float labelWidth = FontManager.getTextWidth(bossLabel, "hud_text");
                FontManager.getLabelStyle("hud_text").font.setColor(Color.RED);
                FontManager.getLabelStyle("hud_text").font.draw(
                    ArcadeSpaceShooter.spriteBatch,
                    bossLabel,
                    barX - labelWidth - 10,
                    barY + 20
                );

                // Draw boss health bar
                ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, barX, barY, barWidth, barHeight);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.DARK_GRAY);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, barX + 2, barY + 2, barWidth - 4, barHeight - 4);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.RED);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, barX + 2, barY + 2, (int)(((double)bossTdc.health / bossTdc.maxHealth) * (barWidth - 4)), barHeight - 4);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
            }
        }

        // Notifications with custom HUD font
        GameStateService gameState = ServiceLocator.getInstance().getGameState();
        if(!gameState.isPaused()) {
            ImmutableArray<Entity> notifications = this.getEngine().getEntitiesFor(Family.all(NotificationComponent.class).get());
            for (Entity notification : notifications) {
                NotificationComponent notificationComponent = ComponentMap.notificationMapper.get(notification);
                notificationComponent.elapsedTime += deltaTime;
                if (notificationComponent.elapsedTime > notificationComponent.maxLife) {
                    this.getEngine().removeEntity(notification);
                    continue;
                }

                FontManager.getLabelStyle("hud_text").font.setColor(notificationComponent.color);
                if (notificationComponent.centerText) {
                    float textWidth = FontManager.getTextWidth(notificationComponent.text, "hud_text");
                    FontManager.getLabelStyle("hud_text").font.draw(
                            ArcadeSpaceShooter.spriteBatch,
                            notificationComponent.text,
                            ArcadeSpaceShooter.screenRect.width / 2 - textWidth / 2,
                            ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.screenRect.height / 3
                    );
                } else {
                    if (ComponentMap.positionMapper.has(notification)) {
                        PositionComponent pc = ComponentMap.positionMapper.get(notification);
                        FontManager.getLabelStyle("hud_text").font.draw(
                            ArcadeSpaceShooter.spriteBatch,
                            notificationComponent.text,
                            pc.position.x,
                            pc.position.y
                        );
                    }
                }
            }
        }

        ArcadeSpaceShooter.spriteBatch.end();
    }
}
