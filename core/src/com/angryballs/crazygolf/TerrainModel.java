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
        private static final int dimension = 1024;
        private static final int divisions = 128;
        private static final float divSize = dimension / (float) divisions;

        public TerrainModel(LevelInfo levelInfo) {
                super(createTerrainModel(levelInfo));
        }

        private static Model createTerrainModel(LevelInfo levelInfo) {

                var grassMaterial = new Material();
                grassMaterial.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture("grass.jpg")));

                float halfRes = dimension / 2;

                float[][] heightMap = new float[divisions][divisions];

                for (int x = 0; x < divisions; ++x) {
                        for (int y = 0; y < divisions; ++y) {
                                boolean OOB = x == 0 || x == divisions - 1 || y == 0 || y == divisions - 1;

                                heightMap[x][y] = OOB ? -50
                                                : levelInfo.heightProfile(x * divSize, y * divSize).floatValue();
                        }
                }

                var modelbuilder = new ModelBuilder();

                modelbuilder.begin();
                MeshPartBuilder bPartBuilder = modelbuilder.part("rect",
                                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                                grassMaterial);
                // NOTE ON TEXTURE REGION, MAY FILL OTHER REGIONS, USE GET region.getU() and so
                // on
                bPartBuilder.setUVRange(0, 0, 1, 1);

                for (int x = 0; x < divisions - 1; ++x) {
                        for (int y = 0; y < divisions - 1; ++y) {
                                VertexInfo v00 = new VertexInfo().set(
                                                new Vector3(x * divSize - halfRes, heightMap[x][y],
                                                                y * -divSize + halfRes),
                                                null, null, new Vector2(0, 1));
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
                return (modelbuilder.end());
        }
}
