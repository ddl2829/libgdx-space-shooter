package com.dalesmithwebdev.galaxia.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.prefabs.Player;
import com.dalesmithwebdev.galaxia.screens.GameOverScreen;
import com.dalesmithwebdev.galaxia.services.GameStateService;
import com.dalesmithwebdev.galaxia.services.ServiceLocator;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;

/**
 * RespawnSystem - Handles player death and respawn logic
 * Single Responsibility: Player lifecycle management (spawn, death, respawn)
 */
public class RespawnSystem extends EntitySystem {
    private float respawnTimer = 0;
    private boolean waitingToRespawn = false;
    private static final float RESPAWN_DELAY = 2000; // 2 seconds in milliseconds

    @Override
    public void update(float gameTime) {
        GameStateService gameState = ServiceLocator.getInstance().getGameState();
        if (gameState.isPaused()) {
            return;
        }

        // Handle respawn timer
        if (waitingToRespawn) {
            respawnTimer += gameTime;
            if (respawnTimer < RESPAWN_DELAY) {
                return; // Still waiting to respawn
            }
            // Timer complete, continue to respawn code
        }

        // Get or create player
        ImmutableArray<Entity> playerEntities = getEngine().getEntitiesFor(
            Family.all(PlayerComponent.class).get()
        );

        Entity player;
        if (playerEntities.size() == 0) {
            // Don't create a new player if game over is scheduled
            if (gameState.isGameOverScheduled()) {
                return;
            }
            player = new Player();
            getEngine().addEntity(player);
        } else {
            player = playerEntities.get(0);
        }

        // Check if player needs respawning (no render component)
        if (!ComponentMap.renderMapper.has(player)) {
            handleRespawn(player);
        }
    }

    /**
     * Handle player respawn logic
     */
    private void handleRespawn(Entity player) {
        GameStateService gameState = ServiceLocator.getInstance().getGameState();
        PlayerComponent ppc = ComponentMap.playerMapper.get(player);

        // Check if player is out of lives
        if (ppc.lives <= 0) {
            if (gameState.isGameOverScheduled()) {
                return;
            }
            // Remove player entity immediately to prevent any respawn
            getEngine().removeEntity(player);

            // Schedule game over screen
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    ArcadeSpaceShooter.instance.setScreen(new GameOverScreen());
                }
            }, 3);
            gameState.setGameOverScheduled(true);
            return;
        }

        // Check if this is initial spawn (no PositionComponent) or respawn (has PositionComponent)
        boolean isInitialSpawn = !ComponentMap.positionMapper.has(player);

        // Player has lives remaining - start respawn timer (but not for initial spawn)
        if (!isInitialSpawn && !waitingToRespawn) {
            waitingToRespawn = true;
            respawnTimer = 0;
            return;
        }

        // Respawn timer complete or initial spawn - restore/setup player
        respawnPlayer(player, isInitialSpawn);
    }

    /**
     * Respawn the player with reset state
     */
    private void respawnPlayer(Entity player, boolean isInitialSpawn) {
        GameStateService gameState = ServiceLocator.getInstance().getGameState();
        waitingToRespawn = false;
        respawnTimer = 0;

        // Reset kill counter
        gameState.setKills(0);

        // Reset laser upgrades
        HasLasersComponent hasLasersComponent = ComponentMap.hasLasersMapper.get(player);
        hasLasersComponent.typeMask = HasLasersComponent.SINGLE;

        // Restore health
        TakesDamageComponent ptdc = ComponentMap.takesDamageMapper.get(player);
        ptdc.health = ptdc.maxHealth;

        // Restore render component
        RenderComponent prc = new RenderComponent(
            ArcadeSpaceShooter.shipTextures.toArray(new TextureRegion[ArcadeSpaceShooter.shipTextures.size()]),
            RenderComponent.PLANE_MAIN
        );
        player.add(prc);

        // Set spawn position
        Vector2 spawnPosition = new Vector2(
            ArcadeSpaceShooter.screenRect.width / 2,
            prc.height + 20
        );

        if (isInitialSpawn) {
            player.add(new PositionComponent(spawnPosition));
        } else {
            PositionComponent playerPositionComp = ComponentMap.positionMapper.get(player);
            playerPositionComp.position = spawnPosition;
        }
    }
}
