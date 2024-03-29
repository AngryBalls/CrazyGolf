package com.angryballs.crazygolf;

import java.util.ArrayList;
import java.util.List;

import com.angryballs.crazygolf.AI.Bot;
import com.angryballs.crazygolf.AI.GradientDescent;
import com.angryballs.crazygolf.AI.HillClimbing;
import com.angryballs.crazygolf.AI.SimulatedAnnealing;
import com.angryballs.crazygolf.AI.Pathfinding.Path;
import com.angryballs.crazygolf.AI.Pathfinding.Pathfinder;
import com.angryballs.crazygolf.Editor.EditorOverlay;
import com.angryballs.crazygolf.Models.BallModel;
import com.angryballs.crazygolf.Models.FlagpoleModel;
import com.angryballs.crazygolf.Models.Skybox;
import com.angryballs.crazygolf.Models.TerrainModel;
import com.angryballs.crazygolf.Models.TreeModel;
import com.angryballs.crazygolf.Models.WallModel;
import com.angryballs.crazygolf.Physics.GRK2PhysicsEngine;
import com.angryballs.crazygolf.Physics.PhysicsEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
    private TerrainModel terrainModel;
    private final BallModel ballModel;
    private final FlagpoleModel poleModel;
    private final Skybox skybox;

    private PerspectiveCamera cam;
    private final Environment environment;
    private FirstPersonCameraController2 camControls;

    private PhysicsEngine physicsSystem;

    private LevelInfo levelInfo;
    private InputAdapter inputAdapter;
    private State state = State.RUN;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;

    private MenuOverlay menuOverlay;
    private final Sound swingSound;
    private final Music winMusic;

    // USERINPUT:
    private float pressedTime;

    private static float timeForOne = 0.1f;// power 1 = timeForOne secodns
    private static final int maxPower = 5;

    private boolean spacePressed;

    private WallModel[] wallModels;
    private EditorOverlay editorOverlay;

    private boolean win = false;

    public GameScreen3D(LevelInfo levelInfo, final GrazyGolf game) {
        levelInfo.reload();

        this.levelInfo = levelInfo;

        loadLevel();
        swingSound = Gdx.audio.newSound(Gdx.files.internal("whoosh.wav"));
        winMusic = Gdx.audio.newMusic(Gdx.files.internal("win.ogg"));

        winMusic.setVolume(0.1f);

        editorOverlay = new EditorOverlay(levelInfo, () -> loadLevel());
        spacePressed = false;

        physicsSystem = new GRK2PhysicsEngine(levelInfo);
        terrainModel = new TerrainModel(levelInfo);
        ballModel = new BallModel();
        poleModel = new FlagpoleModel();
        skybox = new Skybox();

        inputAdapter = new GameScreenInputAdapter();

        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 3, 0);
        cam.near = 1f;
        cam.far = 256f;
        cam.update();

        camControls = new FirstPersonCameraController2(cam, levelInfo);
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

    public void loadLevel() {
        terrainModel = new TerrainModel(levelInfo);
        generateTrees();

        // Generate walls
        wallModels = new WallModel[levelInfo.walls.size()];
        for (int i = 0; i < levelInfo.walls.size(); ++i) {
            var rect = levelInfo.walls.get(i);
            wallModels[i] = new WallModel(rect);
            wallModels[i].transform.setTranslation(rect.getX() + rect.width / 2, 0,
                    -(rect.getY() + rect.height / 2));
        }

        Path path = Pathfinder.findPath(levelInfo);

        levelInfo.optimalPath = path;

        gdBot = new GradientDescent(levelInfo, path);
        hcBot = new HillClimbing(levelInfo, path);
        saBot = currentBot = new SimulatedAnnealing(levelInfo, path);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        editorOverlay.update(cam);

        camControls.noclip = editorOverlay.isEnabled();

        modelBatch.begin(cam);
        modelBatch.render(skybox);
        modelBatch.render(terrainModel, environment);
        modelBatch.render(ballModel, environment);
        modelBatch.render(poleModel, environment);
        editorOverlay.draw(modelBatch);
        for (var tree : trees)
            tree.Render(modelBatch, environment);

        for (var wall : wallModels)
            modelBatch.render(wall, environment);

        modelBatch.end();

        if (state == State.PAUSE) {
            menuOverlay.act();
            menuOverlay.draw();
        } else if (state == State.RUN) {
            for (int i = 0; i < 50; ++i) {
                int result = physicsSystem.iterate();

                if (result == 3 && !win) {
                    winMusic.play();
                    win = true;
                }

                if (result != 0)
                    break;
            }

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

        if (spacePressed)
            font.draw(spriteBatch, "Power = " + String.format("%.2f", updatePower(delta)), 10,
                    Gdx.graphics.getHeight() - 120);

        if (editorOverlay.isEnabled())
            font.draw(spriteBatch, String.format("Editor mode = %s (Tab to change)", editorOverlay.getCurrentMode()),
                    10,
                    Gdx.graphics.getHeight() - 140);

        String currBotText = "";

        if (currentBot == gdBot)
            currBotText = "Gradient Descent";
        else if (currentBot == hcBot)
            currBotText = "Hill Climbing";
        else if (currentBot == saBot)
            currBotText = "Simulated Annealing";

        font.draw(spriteBatch, "Active Bot = " + currBotText + (addNoise ? " (noise enabled)" : ""), 10,
                Gdx.graphics.getHeight() - 90);

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
        menuOverlay.resize(width, height);
        super.resize(width, height);
    }

    private int initialVelocitiesInd = 0;

    private int getRemainingSwings() {
        return VelocityReader.initialVelocities.size() - initialVelocitiesInd;
    }

    private void performSwing() {
        if (getRemainingSwings() <= 0)
            return;

        physicsSystem.performMove(VelocityReader.initialVelocities.get(initialVelocitiesInd++));
        swingSound.play(0.1f);
    }

    private Bot gdBot;
    private Bot hcBot;
    private Bot saBot;
    private Bot currentBot;
    private boolean addNoise;

    private void botPerformSwing() {
        currentBot.applyPhysicsState(physicsSystem);
        var optimalMove = currentBot.computeMove(physicsSystem.x, physicsSystem.y);

        physicsSystem.performMove(optimalMove);
        swingSound.play(0.1f);
    }

    private void shootBall() {
        var power = updatePower(0);
        physicsSystem.performMove(new Vector2(power * cam.direction.x, power * -cam.direction.z));
        swingSound.play(0.1f);
    }

    private float updatePower(float delta) {
        pressedTime += delta;
        var power = Math.min(maxPower, (double) pressedTime / timeForOne);
        return (float) power;
    }

    private void resetGame() {
        physicsSystem = new GRK2PhysicsEngine(levelInfo);
        initialVelocitiesInd = 0;
        win = false;
    }

    private List<TreeModel> trees = new ArrayList<TreeModel>();

    private void generateTrees() {
        trees.clear();
        for (var tree : levelInfo.trees) {
            var treeModel = new TreeModel();
            treeModel.setPosition(new Vector3(tree.x, levelInfo.heightProfile(tree.x, tree.y).floatValue(), -tree.y));
            trees.add(treeModel);
        }
    }

    private void findBall() {
        var pPos = levelInfo.endPosition;

        cam.position.set(new Vector3((float) physicsSystem.x, 0, -(float) physicsSystem.y));
        var camDir = new Vector3((float) (pPos.x - physicsSystem.x), 0, (float) -(pPos.y - physicsSystem.y)).nor();

        var reverseAngle = new Vector3(camDir).scl(-4);

        cam.direction.set(camDir);

        cam.position.add(reverseAngle);
    }

    private class GameScreenInputAdapter extends InputAdapter {
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (editorOverlay.onMouseDown())
                return true;

            pressedTime = 0;
            spacePressed = true;

            return true;
        }

        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (editorOverlay.onMouseUp())
                return true;

            shootBall();
            spacePressed = false;
            return true;
        }

        @Override
        public boolean keyDown(int keycode) {
            camControls.keyDown(keycode);
            if (editorOverlay.handleKeyPress(keycode))
                return true;

            if (keycode == Input.Keys.ESCAPE) {
                if (state == State.RUN) {
                    showMenu();
                } else if (state == State.PAUSE) {
                    hideMenu();
                }
            } else if (keycode == Input.Keys.SPACE)
                performSwing();
            else if (keycode == Input.Keys.B)
                botPerformSwing();
            else if (keycode == Input.Keys.TAB)
                findBall();
            else if (keycode == Input.Keys.NUM_1)
                currentBot = gdBot;
            else if (keycode == Input.Keys.NUM_2)
                currentBot = hcBot;
            else if (keycode == Input.Keys.NUM_3)
                currentBot = saBot;
            else if (keycode == Input.Keys.R)
                resetGame();
            else if (keycode == Input.Keys.E)
                addNoise = !addNoise;

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

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (state == State.RUN)
                camControls.touchDragged(screenX, screenY, 0);
            return true;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            if (amountY == 0)
                return false;

            return editorOverlay.onScroll(amountY);
        }
    }
}
