package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.Rand;

public class LargeMeteor extends Entity implements Pool.Poolable {
    public LargeMeteor(int yPos) {
        this(Rand.nextInt((int)ArcadeSpaceShooter.screenRect.width), yPos);
    }

    public LargeMeteor(float xPos, float yPos) {
        int[] speeds = new int[] { 2, 2, 2, 3, 3, 3, 4, 4, 5, 6 };
        int selectedSpeed = -speeds[Rand.nextInt(speeds.length)];

        this.add(new MeteorComponent(true));
        this.add(new RenderComponent(ArcadeSpaceShooter.bigMeteors.get(Rand.nextInt(ArcadeSpaceShooter.bigMeteors.size())), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(8, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(20, DamageTypeConstants.METEOR));
        this.add(new SpeedComponent(0, selectedSpeed));
        this.add(new PositionComponent(new Vector2(xPos, yPos)));
    }

    public LargeMeteor(float xPos, float yPos, float ySpeed) {
        this.add(new MeteorComponent(true));
        this.add(new RenderComponent(ArcadeSpaceShooter.bigMeteors.get(Rand.nextInt(ArcadeSpaceShooter.bigMeteors.size())), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(8, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(20, DamageTypeConstants.METEOR));
        this.add(new SpeedComponent(0, (int)ySpeed));
        this.add(new PositionComponent(new Vector2(xPos, yPos)));
    }

    /**
     * Default constructor for object pooling - creates components once
     */
    public LargeMeteor() {
        this.add(new MeteorComponent(true));
        this.add(new RenderComponent(ArcadeSpaceShooter.bigMeteors.get(0), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(8, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(20, DamageTypeConstants.METEOR));
        this.add(new SpeedComponent(0, 0));
        this.add(new PositionComponent(0, 0));
    }

    /**
     * Reset for object pooling - update existing components with new values
     */
    @Override
    public void reset() {
        reset(0, 0, -3);
    }

    /**
     * Reset with specific position and speed
     */
    public void reset(float xPos, float yPos, float ySpeed) {
        // Update position
        PositionComponent pc = ComponentMap.positionMapper.get(this);
        if (pc != null) {
            pc.position.set(xPos, yPos);
        }

        // Update speed
        SpeedComponent sc = ComponentMap.speedMapper.get(this);
        if (sc != null) {
            sc.motion.set(0, ySpeed);
        }

        // Randomize texture
        RenderComponent rc = ComponentMap.renderMapper.get(this);
        if (rc != null && rc.textures != null && !rc.textures.isEmpty() &&
            ArcadeSpaceShooter.bigMeteors != null && !ArcadeSpaceShooter.bigMeteors.isEmpty()) {
            rc.textures.set(0, ArcadeSpaceShooter.bigMeteors.get(Rand.nextInt(ArcadeSpaceShooter.bigMeteors.size())));
        }

        // Reset health
        TakesDamageComponent tdc = ComponentMap.takesDamageMapper.get(this);
        if (tdc != null) {
            tdc.health = tdc.maxHealth;
        }
    }
}
