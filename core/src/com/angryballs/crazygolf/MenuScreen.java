package com.angryballs.crazygolf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen extends ScreenAdapter {

    private static final int EXIT_BUTTON_WIDTH = 250;
    private static final int EXIT_BUTTON_HEIGHT = 100;
    private static final int PLAY_BUTTON_WIDTH = 280;
    private static final int PLAY_BUTTON_HEIGHT = 100;
    private static final int EXIT_BUTTON_Y = 70;
    private static final int PLAY_BUTTON_Y = 220;

    GrazyGolf game;
    OrthographicCamera camera;
    Texture exitButtonActive;
    Texture exitButtonInactive;
    Texture playButtonActive;
    Texture playButtonInactive;

    SpriteBatch spriteBatch;

    public MenuScreen(GrazyGolf game) {
        this.game = game;
        playButtonActive = new Texture("play_button_active.png");
        playButtonInactive = new Texture("play_button_inactive.png");
        exitButtonActive = new Texture("exit_button_active.png");
        exitButtonInactive = new Texture("exit_button_inactive.png");

        spriteBatch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        spriteBatch.begin();

        int x = GrazyGolf.MENU_SCREEN_WIDTH / 2 - EXIT_BUTTON_WIDTH / 2;
        /*
         * Changes the color of the buttons when the mouse is on top of the buttons and
         * makes the buttons work
         */
        if (Gdx.input.getX() < x + EXIT_BUTTON_WIDTH && Gdx.input.getX() > x
                && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() < EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT
                && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() > EXIT_BUTTON_Y) {
            spriteBatch.draw(exitButtonActive, x, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
            if (Gdx.input.isTouched()) {
                Gdx.app.exit();
            }
        } else {
            spriteBatch.draw(exitButtonInactive, x, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        }

        if (Gdx.input.getX() < x + PLAY_BUTTON_WIDTH && Gdx.input.getX() > x
                && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT
                && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() > PLAY_BUTTON_Y) {
            spriteBatch.draw(playButtonActive, x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
            if (Gdx.input.isTouched()) {
                game.Start_Game();
            }
        } else {
            spriteBatch.draw(playButtonInactive, x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        }

        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
