package com.angryballs.crazygolf;

import javax.swing.text.StyledEditorKit.BoldAction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector3;

public class GameScreen3D extends ScreenAdapter {
    private final ModelBatch modelBatch = new ModelBatch();
    private final TerrainModel terrainModel;
    private final BallModel ballModel;
    private PerspectiveCamera cam;

    private final Environment environment;

    private FirstPersonCameraController2 camControls;

    public GameScreen3D(LevelInfo levelInfo) {
        terrainModel = new TerrainModel(LevelInfo.exampleInput);
        ballModel = new BallModel();

        cam = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 20, 0);
        cam.near = 1;
        cam.far = 300f;
        cam.update();

        camControls = new FirstPersonCameraController2(cam);
        camControls.setDegreesPerPixel(0.5f);
        camControls.setVelocity(50);

        ballModel.transform.setTranslation(new Vector3(0, 50, 100));

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0f, -1f, -0f));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        camControls.update(delta);

        modelBatch.begin(cam);
        modelBatch.render(terrainModel, environment);
        modelBatch.render(ballModel, environment);
        modelBatch.end();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GameScreenInputAdapter());
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }

    private class GameScreenInputAdapter extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            camControls.keyDown(keycode);
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            camControls.keyUp(keycode);
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            camControls.touchDragged(screenX, screenY, 0);
            return true;
        }
    }
}
