package com.angryballs.crazygolf;

import com.angryballs.crazygolf.Models.BallModel;
import com.angryballs.crazygolf.Models.FlagpoleModel;
import com.angryballs.crazygolf.Models.LeafModel;
import com.angryballs.crazygolf.Models.LogModel;
import com.angryballs.crazygolf.Models.Skybox;
import com.angryballs.crazygolf.Models.TerrainModel;
import com.angryballs.crazygolf.Models.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private final LogModel treeModel;
    private final LeafModel leafModel;

    private PerspectiveCamera cam;

    private final Environment environment;

    private FirstPersonCameraController2 camControls;

    private PhysicsSystem physicsSystem;

    private LevelInfo levelInfo;

    private InputAdapter inputAdapter;

    private State state = State.RUN;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;

    private MenuOverlay menuOverlay;

    public GameScreen3D(LevelInfo levelInfo, final GrazyGolf game) {
        this.levelInfo = levelInfo;
        physicsSystem = new PhysicsSystem(levelInfo);
        terrainModel = new TerrainModel(LevelInfo.exampleInput);
        ballModel = new BallModel();
        poleModel = new FlagpoleModel();
        skybox = new Skybox();
        treeModel = new LogModel();
        treeModel.transform.setTranslation(new Vector3(-5, 0, -5));

        leafModel = new LeafModel();
        leafModel.transform.setTranslation(new Vector3(-5, 5f, -5));

        inputAdapter = new GameScreenInputAdapter();

        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 3, 0);
        cam.near = 1f;
        cam.far = 128f;
        cam.update();

        camControls = new FirstPersonCameraController2(cam, levelInfo, false);
        camControls.setDegreesPerPixel(0.5f);
        camControls.setVelocity(5);

        ballModel.transform.setTranslation(new Vector3(0, 50, 100));

        var pPos = levelInfo.endPosition;
        poleModel.transform
                .setTranslation(
                        new Vector3(pPos.x, levelInfo.heightProfile(pPos.x, pPos.y).floatValue() + 1.05f, -pPos.y));

        skybox.transform.rotateRad(new Vector3(1, 0, 0), 3.14f);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0, -1f, -0f));

        spriteBatch = new SpriteBatch();

        font = new BitmapFont();

        menuOverlay = new MenuOverlay(false, () -> {
            hideMenu();
        }, () -> {
            game.Switch_Menu();
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        modelBatch.begin(cam);
        modelBatch.render(skybox);
        modelBatch.render(terrainModel, environment);
        modelBatch.render(ballModel, environment);
        modelBatch.render(poleModel, environment);
        modelBatch.render(treeModel, environment);
        modelBatch.render(leafModel, environment);
        modelBatch.end();

        if (state == State.PAUSE) {
            menuOverlay.act();
            menuOverlay.draw();
        } else if (state == State.RUN) {
            for (int i = 0; i < 50; ++i)
                physicsSystem.iteration();
            camControls.update(delta);
            skybox.transform.setTranslation(new Vector3(cam.position).add(new Vector3(0, 20, 0)));
            skybox.transform.rotate(new Vector3(0, 1, 0), 0.04f);
            poleModel.transform.rotate(new Vector3(0, 1, 0), 0.25f);
            updateBallPos();
        }

        spriteBatch.begin();

        font.draw(spriteBatch, Integer.toString(getRemainingSwings()) + " shot(s) remaining", 10,
                Gdx.graphics.getHeight() - 10);
        font.draw(spriteBatch, "X position = " + (float) Math.round(physicsSystem.x * 100) / 100, 10,
                Gdx.graphics.getHeight() - 30);
        Vector2 vec = new Vector2((float) physicsSystem.x, (float) physicsSystem.y);
        float f = levelInfo.heightProfile(vec.x, vec.y).floatValue();
        font.draw(spriteBatch, "Y position = " + (float) Math.round(f * 100) / 100, 10,
                Gdx.graphics.getHeight() - 50);
        font.draw(spriteBatch, "Z position = " + (float) Math.round(physicsSystem.y * 100) / 100, 10,
                Gdx.graphics.getHeight() - 70);

        spriteBatch.end();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputAdapter);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void hide() {
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        spriteBatch.dispose();
    }

    private void showMenu() {
        state = State.PAUSE;
        Gdx.input.setCursorCatched(false);
        Gdx.input.setInputProcessor(menuOverlay);
    }

    private void hideMenu() {
        state = State.RUN;
        Gdx.input.setCursorCatched(true);
        Gdx.input.setInputProcessor(inputAdapter);
    }

    private class GameScreenInputAdapter extends InputAdapter {
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (state == State.RUN)
                performSwing();
            return true;
        }

        @Override
        public boolean keyDown(int keycode) {
            camControls.keyDown(keycode);

            if (keycode == 44) {
                if (state == State.RUN) {
                    showMenu();
                } else if (state == State.PAUSE) {
                    hideMenu();
                }
            }
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {

            camControls.keyUp(keycode);
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            if (state == State.RUN)
                camControls.touchDragged(screenX, screenY, 0);
            return true;
        }
    }

    private void updateBallPos() {
        float x, y, z;
        x = (float) physicsSystem.x;
        z = (float) physicsSystem.y;

        y = levelInfo.heightProfile(x, z).floatValue();

        ballModel.transform.setTranslation(new Vector3(x, y + (BallModel.ballDiameter / 2), -z));
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
    }

    private int initialVelocitiesInd = 0;

    private int getRemainingSwings() {
        return VelocityReader.initialVelocities.size() - initialVelocitiesInd;
    }

    private void performSwing() {
        if (getRemainingSwings() <= 0)
            return;

        physicsSystem.performMove(VelocityReader.initialVelocities.get(initialVelocitiesInd++));
    }

}
