package com.angryballs.crazygolf.Models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;

public class LeafModel extends ModelInstance {
    public LeafModel() {
        super(createTreeModel());
    }

    private static Model createTreeModel() {
        ModelBuilder modelBuilder = new ModelBuilder();

        modelBuilder.begin();
        var leaveBuilder = modelBuilder.part("Flag", GL20.GL_TRIANGLES,
                Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(new Color(0f, 0.5f, 0f, 1f))));

        SphereShapeBuilder.build(leaveBuilder, 5f, 3f, 5f, 20, 20);

        return modelBuilder.end();
    }
}
