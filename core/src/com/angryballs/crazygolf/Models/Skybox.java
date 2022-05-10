package com.angryballs.crazygolf.Models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Skybox extends ModelInstance {
    public Skybox() {
        super(createBallModel());
    }

    private static Model createBallModel() {
        var mat = new Material();
        // mat.set(TextureAttribute.createDiffuse(new Texture("ball_Normal.png")));
        mat.set(TextureAttribute.createDiffuse(new Texture("Source_Gray.png")));

        return new ModelBuilder().createSphere(-256, -256, -256, 128, 128, mat,
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
    }
}
