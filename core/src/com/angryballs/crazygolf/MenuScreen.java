package com.angryballs.crazygolf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.awt.*;

public class MenuScreen implements Screen {

    final GrazyGolf game;
    OrthographicCamera camera;
    int Xstart;
    int Ystart;
    int screenwidth;
    int screenheight;
    int resolution;

    public MenuScreen(final GrazyGolf game, int Xstart, int Ystart, int screenWidth, int screenHeight, int resolution) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth * resolution, screenHeight * resolution);
        this.Xstart = Xstart;
        this.Ystart = Ystart;
        this.screenwidth = screenWidth;
        this.screenheight = screenHeight;
        this.resolution = resolution;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game, this.Xstart, this.Ystart, this.screenwidth, this.screenheight, this.resolution));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
