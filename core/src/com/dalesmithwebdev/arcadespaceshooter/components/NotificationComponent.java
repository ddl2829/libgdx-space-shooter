package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class NotificationComponent implements Component {
    static int colorSelect = 0;
    public String text;
    public int elapsedTime = 0;
    public int maxLife = 200;
    public Color color;
    public boolean centerText = false;

    public NotificationComponent(String t, int lifeSpan, boolean centered)
    {
        centerText = centered;
        maxLife = lifeSpan;
        text = t;
        colorSelect++;
        if(colorSelect > 4 || centered)
        {
            colorSelect = 0;
        }
        switch (colorSelect)
        {
            case 0:
                color = Color.WHITE;
                break;
            case 1:
                color = Color.BLUE;
                break;
            case 2:
                color = Color.GREEN;
                break;
            case 3:
                color = Color.RED;
                break;
            case 4:
                color = Color.PURPLE;
                break;
        }
    }
}
