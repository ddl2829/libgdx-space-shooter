package com.dalesmithwebdev.arcadespaceshooter.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.utility.ComponentMap;

public class RenderSystem extends EntitySystem {

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> sprites = this.getEngine().getEntitiesFor(Family.all(RenderComponent.class, PositionComponent.class).get());

        for(Entity drawable : sprites)
        {
            PositionComponent pc = ComponentMap.positionComponentComponentMapper.get(drawable);
            RenderComponent rc = ComponentMap.renderComponentComponentMapper.get(drawable);
            if (rc.visible)
            {
                ArcadeSpaceShooter.spriteBatch.draw(rc.CurrentTexture(), (int)pc.position.x - (rc.CurrentTexture().getWidth() / 2.0f), (int)pc.position.y - (rc.CurrentTexture().getHeight() / 2.0f), rc.CurrentTexture().getWidth(), rc.CurrentTexture().getHeight());

                if (ComponentMap.playerComponentComponentMapper.has(drawable))
                {
                    if(ComponentMap.shieldedComponentComponentMapper.has(drawable))
                    {
                        Texture shield = ArcadeSpaceShooter.playerShield;
                        ArcadeSpaceShooter.spriteBatch.draw(shield, (int)(pc.position.x - rc.CurrentTexture().getWidth() / 2) - 25, (int)(pc.position.y - rc.CurrentTexture().getHeight() / 2) - 30, shield.getWidth(), shield.getHeight());
                    }
                }
            }
        }

        ImmutableArray<Entity> players = this.getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        if (players.size() > 0)
        {
            Entity player = players.first();

            PlayerComponent pc = ComponentMap.playerComponentComponentMapper.get(player);
            TakesDamageComponent ptdc = ComponentMap.takesDamageComponentComponentMapper.get(player);
            int livesY = (int)ArcadeSpaceShooter.screenRect.height - ArcadeSpaceShooter.playerLivesGraphic.getHeight() - 10;
            for (int i = 0; i < pc.lives; i++)
            {
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.playerLivesGraphic, 40 * i + 10, livesY, ArcadeSpaceShooter.playerLivesGraphic.getWidth(), ArcadeSpaceShooter.playerLivesGraphic.getHeight());
            }

            String scoreText = "" + Math.floor(ArcadeSpaceShooter.playerScore);
            ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);
            ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, scoreText, ArcadeSpaceShooter.screenRect.width - ArcadeSpaceShooter.measureText(scoreText) - 10, ArcadeSpaceShooter.screenRect.height - 50);

            // debug
//            String killcount = "" +ArcadeSpaceShooter.kills;
//            ArcadeSpaceShooter.bitmapFont.setColor(Color.WHITE);
//            ArcadeSpaceShooter.bitmapFont.draw(ArcadeSpaceShooter.spriteBatch, killcount, ArcadeSpaceShooter.screenRect.width - ArcadeSpaceShooter.measureText(scoreText) - 10, ArcadeSpaceShooter.screenRect.height - 80);

            ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
            ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 8, livesY - 13, 150, 12);
            ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
            ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 12, 148, 10);
            ArcadeSpaceShooter.spriteBatch.setColor(Color.RED);
            ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 12, (int)(((double)ptdc.health / ptdc.maxHealth) * 148), 10);

            if(ComponentMap.hasShieldComponentComponentMapper.has(player))
            {
                HasShieldComponent hasShieldComponent = ComponentMap.hasShieldComponentComponentMapper.get(player);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.BLACK);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 8, livesY - 30, 150, 12);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 29, 148, 10);
                ArcadeSpaceShooter.spriteBatch.setColor(Color.BLUE);
                ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 29, (int)((hasShieldComponent.shieldPower / hasShieldComponent.maxShieldPower) * 148), 10);

                if (hasShieldComponent.shieldCooldown)
                {
                    ArcadeSpaceShooter.spriteBatch.setColor(Color.PURPLE);
                    ArcadeSpaceShooter.spriteBatch.draw(ArcadeSpaceShooter.blank, 9, livesY - 31, (int)(Math.min((hasShieldComponent.shieldPower / hasShieldComponent.maxShieldPower), 1) * 148), 10);
                }
            }
            ArcadeSpaceShooter.spriteBatch.setColor(Color.WHITE);
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
    }
}
