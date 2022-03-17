package com.angryballs.crazygolf;

import java.awt.*;
import java.util.Random;

import javax.swing.text.StyledEditorKit.BoldAction;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.Texture;

public class GameScreen3D extends ScreenAdapter {
    private final ModelBatch modelBatch = new ModelBatch();
    private final TerrainModel terrainModel;
    private final BallModel ballModel;
    private PerspectiveCamera cam;

    private final Environment environment;

    private FirstPersonCameraController2 camControls;

    private PhysicsSystem physicsSystem;

    private LevelInfo levelInfo;

    private State state = State.RUN;
    final SpriteBatch spriteBatch;
    final BitmapFont font;

    private final Texture exitButtonActive;
    private final Texture exitButtonInactive;
    private final Texture playButtonActive;
    private final Texture playButtonInactive;

    final private GrazyGolf game;

    private static final int EXIT_BUTTON_WIDTH = 125;
    private static final int EXIT_BUTTON_HEIGHT = 50;
    private static final int PLAY_BUTTON_WIDTH = 140;
    private static final int PLAY_BUTTON_HEIGHT = 50;
    private static final int EXIT_BUTTON_Y = 35;
    private static final int PLAY_BUTTON_Y = 110;

    public GameScreen3D(LevelInfo levelInfo,final GrazyGolf game) {
        this.game = game;
        this.levelInfo = levelInfo;
        physicsSystem = new PhysicsSystem(levelInfo);
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

        spriteBatch = new SpriteBatch();

        font = new BitmapFont();

        playButtonActive = new Texture("play_button_active.png");
        playButtonInactive = new Texture("play_button_inactive.png");
        exitButtonActive = new Texture("exit_button_active.png");
        exitButtonInactive = new Texture("exit_button_inactive.png");


    }

    @Override
    public void render(float delta) {
        switch (state) {
            case PAUSE:

                Gdx.input.setCursorCatched(false);
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
                Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);


                modelBatch.begin(cam);
                modelBatch.render(terrainModel, environment);
                modelBatch.render(ballModel, environment);
                modelBatch.end();

                spriteBatch.begin();
                int x = GrazyGolf.MENU_SCREEN_WIDTH/2 - EXIT_BUTTON_WIDTH/2;

                boolean queueExit = false;
                if(Gdx.input.getX() < x + EXIT_BUTTON_WIDTH && Gdx.input.getX() > x && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() < EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() > EXIT_BUTTON_Y){
                    spriteBatch.draw(exitButtonActive, x, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
                    if(Gdx.input.isTouched()){
                        queueExit = true;
                    }
                }
                else{
                    spriteBatch.draw(exitButtonInactive, x, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
                }

                if(Gdx.input.getX() < x + PLAY_BUTTON_WIDTH && Gdx.input.getX() > x && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() > PLAY_BUTTON_Y){
                    spriteBatch.draw(playButtonActive, x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
                    if (Gdx.input.isTouched()) {
                        state = State.RESUME;
                    }
                }
                else{
                    spriteBatch.draw(playButtonInactive, x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
                }
                font.draw(spriteBatch, "# of shots", 10, Gdx.graphics.getHeight() - 10);
                font.draw(spriteBatch, "X position = " + (float) Math.round(physicsSystem.x*100)/100 , 10, Gdx.graphics.getHeight() - 30);
                Vector2 vec2 = new Vector2(physicsSystem.x, physicsSystem.y);
                float f2 = LevelInfo.exampleInput.heightProfile(vec2);
                font.draw(spriteBatch, "Y position = " + (float) Math.round(f2*100)/100, 10, Gdx.graphics.getHeight() - 50);
                font.draw(spriteBatch, "Z position = " + (float) Math.round(physicsSystem.y*100)/100, 10, Gdx.graphics.getHeight() - 70);

                spriteBatch.end();
                if(queueExit)
                    game.Switch_Menu();

                break;
            case RUN:
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
                Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

                physicsSystem.iteration();
                camControls.update(delta);
                updateBallPos();

                modelBatch.begin(cam);
                modelBatch.render(terrainModel, environment);
                modelBatch.render(ballModel, environment);
                modelBatch.end();

                spriteBatch.begin();
                font.draw(spriteBatch, "# of shots", 10, Gdx.graphics.getHeight() - 10);
                font.draw(spriteBatch, "X position = " + (float) Math.round(physicsSystem.x*100)/100 , 10, Gdx.graphics.getHeight() - 30);
                Vector2 vec = new Vector2(physicsSystem.x, physicsSystem.y);
                float f = LevelInfo.exampleInput.heightProfile(vec);
                font.draw(spriteBatch, "Y position = " + (float) Math.round(f*100)/100, 10, Gdx.graphics.getHeight() - 50);
                font.draw(spriteBatch, "Z position = " + (float) Math.round(physicsSystem.y*100)/100, 10, Gdx.graphics.getHeight() - 70);
                spriteBatch.end();



                break;
            case RESUME:
                Gdx.input.setCursorCatched(true);
                this.state = State.RUN;
                break;
            case STOPPED:

                break;
            default:
                break;
        }



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
        spriteBatch.dispose();
        exitButtonActive.dispose();
        exitButtonInactive.dispose();
        playButtonActive.dispose();
        playButtonInactive.dispose();
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

            if (keycode == 44) {
                if (state == State.RUN) {
                    state = State.PAUSE;
                }
                else if (state == State.PAUSE) {
                    state = State.RESUME;
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
            camControls.touchDragged(screenX, screenY, 0);
            return true;
        }
    }

    private void updateBallPos() {
        float x, y, z;
        x = physicsSystem.x;
        z = physicsSystem.y;

        y = levelInfo.heightProfile(new Vector2(x, z));

        ballModel.transform.setTranslation(new Vector3(x - 128, y, -z + 128));
    }
}
