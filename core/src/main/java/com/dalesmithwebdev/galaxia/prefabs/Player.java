package com.dalesmithwebdev.galaxia.prefabs;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.galaxia.components.*;
import com.dalesmithwebdev.galaxia.systems.DamageSystem;

public class Player extends Entity {
    public Player() {
        super();

        this.add(new PlayerComponent());
        this.add(new HasLasersComponent(150, HasLasersComponent.SINGLE));
        this.add(new SpeedComponent(0, 0));
        this.add(new TakesDamageComponent(50, DamageSystem.ENEMY ^ DamageSystem.ENEMY_LASER ^ DamageSystem.METEOR));
        this.add(new DealsDamageComponent(5, DamageSystem.PLAYER));

        // Debug upgrades
//        this.add(new HasMissilesComponent(1000));
//        this.add(new HasBombsComponent(500));
//        this.add(new HasShieldComponent());
//        this.add(new HasEmpComponent(10000));
    }
}
