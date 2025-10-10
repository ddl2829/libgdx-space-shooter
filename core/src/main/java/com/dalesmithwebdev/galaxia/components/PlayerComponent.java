package com.dalesmithwebdev.galaxia.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public int lives;
    public int maxLives = 4;

    public PlayerComponent()
    {
        this.lives = this.maxLives;
    }
}
