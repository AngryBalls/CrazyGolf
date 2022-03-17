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

public class TerrainModel extends ModelInstance {
    private static final int dimension = 256;
    private static final int divisions = 128;
    private static final float divSize = dimension / (float) divisions;

    public TerrainModel(LevelInfo levelInfo) {
        super(createTerrainModel(levelInfo));
    }

    private static Model createTerrainModel(LevelInfo levelInfo) {
        var grassMaterial = new Material();
        grassMaterial.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture("grass.png")));

        float halfRes = dimension / 2;

        float[][] heightMap = new float[divisions][divisions];

        for (int x = 0; x < divisions; ++x) {
            for (int y = 0; y < divisions; ++y) {
                boolean OOB = x == 0 || x == divisions - 1 || y == 0 || y == divisions - 1;

                heightMap[x][y] = OOB ? -50
                        : levelInfo.heightProfile(x * divSize - halfRes, y * divSize - halfRes)
                                .floatValue();
            }
        }

        var modelbuilder = new ModelBuilder();

        modelbuilder.begin();
        MeshPartBuilder bPartBuilder = modelbuilder.part("rect",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                grassMaterial);
        // NOTE ON TEXTURE REGION, MAY FILL OTHER REGIONS, USE GET region.getU() and so
        // on
        // bPartBuilder.setUVRange(0, 0, 1, 1);

        for (int x = 0; x < divisions - 1; ++x) {
            for (int y = 0; y < divisions - 1; ++y) {
                VertexInfo v00 = new VertexInfo().set(
                        new Vector3(x * divSize - halfRes, heightMap[x][y],
                                y * -divSize + halfRes),
                        null, null, new Vector2(0, 1f));
                VertexInfo v10 = new VertexInfo().set(
                        new Vector3((x + 1) * divSize - halfRes, heightMap[x + 1][y],
                                y * -divSize + halfRes),
                        null, null, new Vector2(1, 1));
                VertexInfo v11 = new VertexInfo().set(
                        new Vector3((x + 1) * divSize - halfRes, heightMap[x + 1][y + 1],
                                (y + 1) * -divSize + halfRes),
                        null, null, new Vector2(1, 0));
                VertexInfo v01 = new VertexInfo().set(
                        new Vector3(x * divSize - halfRes, heightMap[x][y + 1],
                                (y + 1) * -divSize + halfRes),
                        null, null, new Vector2(0, 0));

                bPartBuilder.rect(v00, v10, v11, v01);
            }
        }

        var waterMaterial = new Material();
        var wTex = new Texture("water.jpg");
        wTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        waterMaterial.set(new TextureAttribute(TextureAttribute.Diffuse, wTex));

        bPartBuilder = modelbuilder.part("wrect",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                waterMaterial);

        VertexInfo vw00 = new VertexInfo().set(
                new Vector3(-halfRes, -0.01f, halfRes),
                null, null, new Vector2(0, 1));
        VertexInfo vw10 = new VertexInfo().set(
                new Vector3(halfRes, -0.01f, halfRes),
                null, null, new Vector2(1, 1));
        VertexInfo vw11 = new VertexInfo().set(
                new Vector3(halfRes, -0.01f, -halfRes),
                null, null, new Vector2(1, 0));
        VertexInfo vw01 = new VertexInfo().set(
                new Vector3(-halfRes, -0.01f, -halfRes),
                null, null, new Vector2(0, 0));

        bPartBuilder.rect(vw00, vw10, vw11, vw01);

        var sandMaterial = new Material();
        var sTex = new Texture("sand.png");
        sTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        sandMaterial.set(new TextureAttribute(TextureAttribute.Diffuse, sTex));

        bPartBuilder = modelbuilder.part("srect",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                sandMaterial);

        VertexInfo vs00 = new VertexInfo().set(
                new Vector3(levelInfo.sandPitBounds[0].x,
                        levelInfo.heightProfile(levelInfo.sandPitBounds[0].x,
                                levelInfo.sandPitBounds[0].y)
                                .floatValue() + 0.1f,
                        -levelInfo.sandPitBounds[0].y),
                null, null, new Vector2(0, 1));
        VertexInfo vs10 = new VertexInfo().set(
                new Vector3(levelInfo.sandPitBounds[1].x,
                        levelInfo.heightProfile(levelInfo.sandPitBounds[1].x,
                                levelInfo.sandPitBounds[0].y)
                                .floatValue() + 0.1f,
                        -levelInfo.sandPitBounds[0].y),
                null, null, new Vector2(1, 1));
        VertexInfo vs11 = new VertexInfo().set(
                new Vector3(levelInfo.sandPitBounds[1].x,
                        levelInfo.heightProfile(levelInfo.sandPitBounds[1].x,
                                levelInfo.sandPitBounds[1].y)
                                .floatValue() + 0.1f,
                        -levelInfo.sandPitBounds[1].y),
                null, null, new Vector2(1, 0));
        VertexInfo vs01 = new VertexInfo().set(
                new Vector3(levelInfo.sandPitBounds[0].x,
                        levelInfo.heightProfile(levelInfo.sandPitBounds[0].x,
                                levelInfo.sandPitBounds[0].y)
                                .floatValue() + 0.1f,
                        -levelInfo.sandPitBounds[1].y),
                null, null, new Vector2(0, 0));

        bPartBuilder.rect(vs00, vs10, vs11, vs01);

        var holeMaterial = new Material();
        var hTex = new Texture("hole.png");
        holeMaterial.set(new TextureAttribute(TextureAttribute.Diffuse, hTex));

        bPartBuilder = modelbuilder.part("hrect",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                holeMaterial);

        var hPosX = levelInfo.endPosition.x;
        var hPosY = levelInfo.endPosition.y;
        var r = levelInfo.holeRadius * 10;

        VertexInfo vh00 = new VertexInfo().set(
                new Vector3(hPosX - r, heightAt(hPosX - r, hPosY - r, levelInfo) + 0.1f, -(hPosY - r)),
                null, null, new Vector2(0, 1));
        VertexInfo vh10 = new VertexInfo().set(
                new Vector3(hPosX + r, heightAt(hPosX + r, hPosY - r, levelInfo) + 0.1f, -(hPosY - r)),
                null, null, new Vector2(1, 1));
        VertexInfo vh11 = new VertexInfo().set(
                new Vector3(hPosX + r, heightAt(hPosX + r, hPosY + r, levelInfo) + 0.1f, -(hPosY + r)),
                null, null, new Vector2(1, 0));
        VertexInfo vh01 = new VertexInfo().set(
                new Vector3(hPosX - r, heightAt(hPosX - r, hPosY + r, levelInfo) + 0.1f, -(hPosY + r)),
                null, null, new Vector2(0, 0));

        bPartBuilder.rect(vh00, vh10, vh11, vh01);

        return (modelbuilder.end());
    }

    private static float heightAt(float x, float y, LevelInfo levelInfo) {
        return levelInfo.heightProfile(x, y).floatValue();
    }
}
