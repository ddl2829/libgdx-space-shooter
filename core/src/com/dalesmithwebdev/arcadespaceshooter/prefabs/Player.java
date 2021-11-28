package com.dalesmithwebdev.arcadespaceshooter.prefabs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.components.HasShieldComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.PlayerComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.SpeedComponent;
import com.dalesmithwebdev.arcadespaceshooter.components.TakesDamageComponent;
import com.dalesmithwebdev.arcadespaceshooter.systems.DamageSystem;

public class Player extends Entity {
    public Player() {
        super();

        PlayerComponent ppc = new PlayerComponent();
        this.add(ppc);
        this.add(new HasShieldComponent());
        this.add(new SpeedComponent(new Vector2(0, 0)));
        this.add(new TakesDamageComponent(50, DamageSystem.ENEMY ^ DamageSystem.ENEMY_LASER ^ DamageSystem.METEOR));
    }
}
