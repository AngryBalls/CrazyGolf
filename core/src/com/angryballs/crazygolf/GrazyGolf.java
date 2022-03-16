package com.angryballs.crazygolf;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class GrazyGolf extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	ModelBatch modelBatch;
	TerrainModel terMod;

	public PerspectiveCamera cam;

	public Environment environment;

	@Override
	public void create() {
		terMod = new TerrainModel(LevelInfo.exampleInput);
		modelBatch = new ModelBatch();
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		cam = new PerspectiveCamera(120, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 20, 0);
		cam.near = 0;
		cam.far = 300f;
		cam.update();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0f, -1f, -0f));
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(terMod.modelInstance, environment);
		modelBatch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}
}
