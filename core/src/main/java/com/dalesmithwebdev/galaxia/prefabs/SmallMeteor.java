package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.dalesmithwebdev.galaxia.ArcadeSpaceShooter;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.constants.DamageTypeConstants;
import com.dalesmithwebdev.galaxia.utility.ComponentMap;
import com.dalesmithwebdev.galaxia.utility.Rand;

public class SmallMeteor extends Entity implements Pool.Poolable {
    public SmallMeteor(int yPos) {
        this(Rand.nextInt((int)ArcadeSpaceShooter.screenRect.width), yPos);
    }

    public SmallMeteor(float xPos, float yPos) {
        int[] speeds = new int[] { 3, 3, 3, 4, 4, 4, 5, 5, 6, 6 };
        int selectedSpeed = -speeds[Rand.nextInt(speeds.length)];

        this.add(new MeteorComponent(false));
        this.add(new RenderComponent(ArcadeSpaceShooter.smallMeteors.get(Rand.nextInt(ArcadeSpaceShooter.smallMeteors.size())), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(2, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(5, DamageTypeConstants.METEOR));
        this.add(new SpeedComponent(0, selectedSpeed));
        this.add(new PositionComponent(new Vector2(xPos, yPos)));
    }

    public SmallMeteor(float xPos, float yPos, float ySpeed) {
        this.add(new MeteorComponent(false));
        this.add(new RenderComponent(ArcadeSpaceShooter.smallMeteors.get(Rand.nextInt(ArcadeSpaceShooter.smallMeteors.size())), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(2, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(5, DamageTypeConstants.METEOR));
        this.add(new SpeedComponent(0, (int)ySpeed));
        this.add(new PositionComponent(new Vector2(xPos, yPos)));
    }

    /**
     * Default constructor for object pooling - creates components once
     */
    public SmallMeteor() {
        this.add(new MeteorComponent(false));
        this.add(new RenderComponent(ArcadeSpaceShooter.smallMeteors.get(0), RenderComponent.PLANE_MAIN));
        this.add(new TakesDamageComponent(2, DamageTypeConstants.LASER ^ DamageTypeConstants.MISSILE ^ DamageTypeConstants.BOMB));
        this.add(new DealsDamageComponent(5, DamageTypeConstants.METEOR));
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
            ArcadeSpaceShooter.smallMeteors != null && !ArcadeSpaceShooter.smallMeteors.isEmpty()) {
            rc.textures.set(0, ArcadeSpaceShooter.smallMeteors.get(Rand.nextInt(ArcadeSpaceShooter.smallMeteors.size())));
        }

        // Reset health
        TakesDamageComponent tdc = ComponentMap.takesDamageMapper.get(this);
        if (tdc != null) {
            tdc.health = tdc.maxHealth;
        }
    }
}
