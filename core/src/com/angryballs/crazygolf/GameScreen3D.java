package com.angryballs.crazygolf;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GameScreen3D extends ScreenAdapter {
    private final ModelBatch modelBatch = new ModelBatch();
    private final TerrainModel terrainModel;
    private final BallModel ballModel;
    private final FlagpoleModel poleModel;
    private final Skybox skybox;
    private PerspectiveCamera cam;

    private final Environment environment;

    private FirstPersonCameraController2 camControls;

    private PhysicsSystem physicsSystem;

    private LevelInfo levelInfo;

    public GameScreen3D(LevelInfo levelInfo) {
        this.levelInfo = levelInfo;
        physicsSystem = new PhysicsSystem(levelInfo);
        terrainModel = new TerrainModel(LevelInfo.exampleInput);
        ballModel = new BallModel();
        poleModel = new FlagpoleModel();
        skybox = new Skybox();

        cam = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 20, 0);
        cam.near = 1;
        cam.far = 5000f;
        cam.update();

        camControls = new FirstPersonCameraController2(cam);
        camControls.setDegreesPerPixel(0.5f);
        camControls.setVelocity(50);

        ballModel.transform.setTranslation(new Vector3(0, 50, 100));

        var pPos = levelInfo.endPosition;
        poleModel.transform
                .setTranslation(
                        new Vector3(pPos.x, levelInfo.heightProfile(pPos.x, pPos.y).floatValue() + 10, -pPos.y));

        skybox.transform.rotateRad(new Vector3(1, 0, 0), 3.14f);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0f, -1f, -0f));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        physicsSystem.iteration();
        camControls.update(delta);
        updateBallPos();

        modelBatch.begin(cam);
        modelBatch.render(skybox);
        modelBatch.render(terrainModel, environment);
        modelBatch.render(ballModel, environment);
        modelBatch.render(poleModel, environment);
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
        Random rng = new Random();

        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            physicsSystem.performMove(new Vector2(rng.nextInt(100) * (rng.nextBoolean() ? -1 : 1),
                    rng.nextInt(100) * (rng.nextBoolean() ? -1 : 1)));
            return true;
        }

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

    private void updateBallPos() {
        float x, y, z;
        x = (float) physicsSystem.x;
        z = (float) physicsSystem.y;

        y = levelInfo.heightProfile(x, z).floatValue();

        ballModel.transform.setTranslation(new Vector3(x, y, -z));
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
    }
}
