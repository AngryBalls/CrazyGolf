package com.angryballs.crazygolf;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TerrainModel {
    private static final int dimension = 256;
    private static final int divisions = 64;
    private static final float divSize = dimension / (float) divisions;

    public final ModelInstance modelInstance;

    private final LevelInfo levelInfo;

    public TerrainModel(LevelInfo levelInfo) {

        this.levelInfo = levelInfo;

        var mat = new Material();
        mat.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture("grass.jpg")));
        modelInstance = new ModelInstance(
                createTerrainModel(mat, 0, 0, 1, 1));
    }

    private Model createTerrainModel(final Material material,
            final float u1, final float v1, final float u2, final float v2) {

        float halfRes = dimension / 2;

        float[][] heightMap = new float[divisions][divisions];

        for (int x = 0; x < divisions; ++x) {
            for (int y = 0; y < divisions; ++y) {
                heightMap[x][y] = levelInfo.heightProfile(new Vector2(x * divSize, y * divSize)) * 10;
            }
        }

        var modelbuilder = new ModelBuilder();

        modelbuilder.begin();
        MeshPartBuilder bPartBuilder = modelbuilder.part("rect",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                material);
        // NOTE ON TEXTURE REGION, MAY FILL OTHER REGIONS, USE GET region.getU() and so
        // on
        bPartBuilder.setUVRange(u1, v1, u2, v2);

        for (int x = 0; x < divisions - 1; ++x) {
            for (int y = 0; y < divisions - 1; ++y) {
                VertexInfo v00 = new VertexInfo().set(
                        new Vector3(x * divSize - halfRes, heightMap[x][y], y * -divSize + halfRes),
                        null, null, new Vector2(0, 1));
                VertexInfo v10 = new VertexInfo().set(
                        new Vector3((x + 1) * divSize - halfRes, heightMap[x + 1][y], y * -divSize + halfRes),
                        null, null, new Vector2(1, 1));
                VertexInfo v11 = new VertexInfo().set(
                        new Vector3((x + 1) * divSize - halfRes, heightMap[x + 1][y + 1], (y + 1) * -divSize + halfRes),
                        null, null, new Vector2(1, 0));
                VertexInfo v01 = new VertexInfo().set(
                        new Vector3(x * divSize - halfRes, heightMap[x][y + 1], (y + 1) * -divSize + halfRes),
                        null, null, new Vector2(0, 0));

                bPartBuilder.rect(v00, v10, v11, v01);
            }
        }
        return (modelbuilder.end());
    }
}
