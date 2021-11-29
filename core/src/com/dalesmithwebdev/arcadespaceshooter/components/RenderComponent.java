package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.Arrays;

public class RenderComponent implements Component {
    public int currentTexture = 0;
    public ArrayList<Texture> textures;
    public boolean visible = true;
    public int width = 0;
    public int height = 0;
    public int zIndex = 0;

    public static int PLANE_BACKGROUND_IMAGE = -2;
    public static int PLANE_BACKGROUND_OBJECTS = -1;
    public static int PLANE_MAIN = 0;
    public static int PLANE_ABOVE = 1;

    public Texture CurrentTexture()
    {
        return textures.get(currentTexture);
    }

    public RenderComponent(Texture t, int zPlane)
    {
        textures = new ArrayList<>();
        textures.add(t);
        this.width = t.getWidth();
        this.height = t.getHeight();
        zIndex = zPlane;
    }

    public RenderComponent(Texture t, int width, int height, int zPlane) {
        textures = new ArrayList<>();
        textures.add(t);
        this.width = width;
        this.height = height;
        zIndex = zPlane;
    }

    public RenderComponent(Texture[] inTextures, int zPlane)
    {
        textures = new ArrayList<>();
        textures.addAll(Arrays.asList(inTextures));
        zIndex = zPlane;
    }
}
