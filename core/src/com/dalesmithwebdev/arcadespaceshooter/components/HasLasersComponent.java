package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;

public class HasLasersComponent implements Component {
    public static int SINGLE = 1;
    public static int DUAL = 2;
    public static int DIAGONAL = 4;
    public static int UPGRADED = 8;
    public static int UPGRADED_AGAIN = 16;

    public double shotInterval;
    public double timeSinceLastShot = 0;
    public int typeMask;

    public HasLasersComponent(double shotInterval, int typeMask) {
        this.shotInterval = shotInterval;
        this.typeMask = typeMask;
    }
}
