package com.dalesmithwebdev.galaxia.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.ArrayList;
import java.util.Arrays;

public class RenderComponent implements Component {
    public int currentTexture = 0;
    public ArrayList<TextureRegion> textures;
    public boolean visible = true;
    public int width = 0;
    public int height = 0;
    public int zIndex = 0;
    public ShaderProgram shader = null;
    public int shaderTime = 0;
    public boolean stickyShader = false;

    public static int PLANE_BACKGROUND_IMAGE = -2;
    public static int PLANE_BACKGROUND_OBJECTS = -1;
    public static int PLANE_MAIN = 0;
    public static int PLANE_ABOVE = 1;

    public TextureRegion CurrentTexture()
    {
        return textures.get(currentTexture);
    }

    public RenderComponent(TextureRegion t, int zPlane)
    {
        textures = new ArrayList<>();
        textures.add(t);
        this.width = t.getRegionWidth();
        this.height = t.getRegionHeight();
        zIndex = zPlane;
    }

    public RenderComponent(TextureRegion t, int width, int height, int zPlane) {
        textures = new ArrayList<>();
        textures.add(t);
        this.width = width;
        this.height = height;
        zIndex = zPlane;
    }

    public RenderComponent(TextureRegion[] inTextures, int zPlane)
    {
        textures = new ArrayList<>();
        textures.addAll(Arrays.asList(inTextures));
        width = inTextures[0].getRegionWidth();
        height = inTextures[0].getRegionHeight();
        zIndex = zPlane;
    }
}
