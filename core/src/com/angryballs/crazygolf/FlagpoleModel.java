package com.angryballs.crazygolf;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Vector3;

public class FlagpoleModel extends ModelInstance {
    public FlagpoleModel() {
        super(createFlagPole());
    }

    private static Model createFlagPole() {

        ModelBuilder modelBuilder = new ModelBuilder();

        modelBuilder.begin();

        var rodMat = new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY));
        var rodBuilder = modelBuilder.part("Rod", GL20.GL_TRIANGLES,
                Usage.Position | Usage.Normal | Usage.TextureCoordinates, rodMat);

        CylinderShapeBuilder.build(rodBuilder, 0.019f, 2.1f, 0.019f, 20);

        var flagBuilder = modelBuilder.part("Flag", GL20.GL_TRIANGLES,
                Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(1, 0, 0.39f, 1f)));

        flagBuilder.triangle(new Vector3(0, 1.05f, 0), new Vector3(0, 0.7875f, 0), new Vector3(1.05f / 2, 1.05f, 0));
        flagBuilder.triangle(new Vector3(1.05f / 2, 1.05f, 0), new Vector3(0, 0.7875f, 0), new Vector3(0, 1.05f, 0));

        return modelBuilder.end();
    }
}
