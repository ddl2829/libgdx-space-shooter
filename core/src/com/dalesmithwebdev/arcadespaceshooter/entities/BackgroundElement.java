package com.dalesmithwebdev.arcadespaceshooter.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dalesmithwebdev.arcadespaceshooter.ArcadeSpaceShooter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class BackgroundElement {
    Rectangle screenBounds;
    Vector2 position;
    Vector2 motion;
    ArrayList<Texture> textures;
    public boolean belowScreen = false;
    static int textureID = 1;
    int useTexture;

    public BackgroundElement(ArrayList<Texture> textures, Rectangle screenBounds)
    {
        this.textures = textures;
        this.screenBounds = screenBounds;
        Random rand = new Random();
        position = new Vector2(rand.nextInt((int)screenBounds.width), rand.nextInt(500) + (int)ArcadeSpaceShooter.screenRect.height);
        motion = new Vector2(0, -3);
        //motion = new Vector2(0, 0);
        //motion.y = -3;
        textureID += 1;
        if (textureID > textures.size() - 1)
        {
            textureID = 0;
        }
        useTexture = textureID;
    }

    public void update(float gameTime)
    {
        position.add(motion.cpy().setLength(gameTime));
        if (position.y < -100)
        {
            belowScreen = true;
        }
    }

    public void draw(SpriteBatch spriteBatch)
    {
        spriteBatch.draw(textures.get(useTexture), (int)position.x, (int)position.y, textures.get(useTexture).getWidth(), textures.get(useTexture).getHeight());
    }
}
