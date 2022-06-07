package com.angryballs.crazygolf.Models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;

public class WallModel extends ModelInstance {
    public WallModel(Rectangle rectangle) {
        super(createWallModel(rectangle));
    }

    private static Model createWallModel(Rectangle rectangle) {
        var mat = new Material(ColorAttribute.createDiffuse(new Color(0, 0, 0, 1)));

        return new ModelBuilder().createBox(rectangle.width, 1, rectangle.height, mat,
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);
    }
}
