package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public int laserLevel = 0;
    public double lastFireTime = 0;
    public int currentTexture = 0;

    public float shipSpeed = 5.0f;

    public int lives;
    public int maxLives = 5;

    //public bool shielded = false;
    //public bool shieldCooldown = false;

    public double timeSinceRespawn = 0;

    public PlayerComponent()
    {
    }
}
