package com.dalesmithwebdev.arcadespaceshooter.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class RenderComponent implements Component {
    public int currentTexture = 0;
    public ArrayList<Texture> textures;
    public boolean visible = true;

    public Texture CurrentTexture()
    {
        return textures.get(currentTexture);
    }

    public RenderComponent(Texture t)
    {
        textures = new ArrayList<Texture>();
        textures.add(t);
    }

    public RenderComponent(Texture[] inTextures)
    {
        textures = new ArrayList<Texture>();
        for(Texture t : inTextures) {
            textures.add(t);
        }
    }
}
