package com.dalesmithwebdev.arcadespaceshooter.screens;


public abstract class BaseScreen {
    public boolean pausesBelow = false;

    public abstract void update(float gameTime);

    public abstract void draw(float gameTime);
}
