package com.angryballs.crazygolf.Models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class LogModel extends ModelInstance {
    public LogModel() {
        super(createTreeModel());
    }

    private static Model createTreeModel() {
        ModelBuilder modelBuilder = new ModelBuilder();

        modelBuilder.begin();

        var barkMat = new Material(ColorAttribute.createDiffuse(new Color(0.22f, 0.11f, 0.03f, 1f)));
        var logBuilder = modelBuilder.part("Rod", GL20.GL_TRIANGLES,
                Usage.Position | Usage.Normal | Usage.TextureCoordinates, barkMat);

        SphereShapeBuilder.build(logBuilder, 0.5f, 10f, 0.5f, 20, 20);

        return modelBuilder.end();
    }
}
