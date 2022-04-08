package com.angryballs.crazygolf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class BallModel extends ModelInstance {
    public BallModel() {
        super(createBallModel());
    }

    private static Model createBallModel() {
        var mat = new Material();
        // mat.set(TextureAttribute.createDiffuse(new Texture("ball_Normal.png")));
        mat.set(TextureAttribute.createNormal(new Texture("ball_Normal.png")));

        return new ModelBuilder().createSphere(0.04267f, 0.04267f, 0.04267f, 50, 50, mat,
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
    }
}
