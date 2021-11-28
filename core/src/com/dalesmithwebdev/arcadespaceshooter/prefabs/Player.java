package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.dalesmithwebdev.arcadespaceshooter.components.*;
import com.dalesmithwebdev.arcadespaceshooter.systems.DamageSystem;

public class Player extends Entity {
    public Player() {
        super();

        PlayerComponent ppc = new PlayerComponent();
        this.add(ppc);
        this.add(new HasShieldComponent());
        this.add(new HasLasersComponent(150, HasLasersComponent.SINGLE));
        this.add(new SpeedComponent(0, 0));
        this.add(new TakesDamageComponent(50, DamageSystem.ENEMY ^ DamageSystem.ENEMY_LASER ^ DamageSystem.METEOR));
    }
}
