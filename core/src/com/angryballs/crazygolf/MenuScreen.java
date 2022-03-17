package com.angryballs.crazygolf;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen implements Screen {

    private static final int EXIT_BUTTON_WIDTH = 250;
    private static final int EXIT_BUTTON_HEIGHT = 100;
    private static final int PLAY_BUTTON_WIDTH = 280;
    private static final int PLAY_BUTTON_HEIGHT = 100;
    private static final int LOGO_WIDTH = 300;
    private static final int LOGO_HEIGHT = 150;
    private static final int EXIT_BUTTON_Y = 70;
    private static final int PLAY_BUTTON_Y = 220;
    private static final int LOGO_Y = 530;

    GrazyGolf game;
    OrthographicCamera camera;
    Texture exitButtonActive;
    Texture exitButtonInactive;
    Texture playButtonActive;
    Texture playButtonInactive;
    Texture logo;

    public MenuScreen(GrazyGolf game){
        this.game = game;
        playButtonActive = new Texture("play_button_active.png");
        playButtonInactive = new Texture("play_button_inactive.png");
        exitButtonActive = new Texture("exit_button_active.png");
        exitButtonInactive = new Texture("exit_button_inactive.png");
        logo = new Texture("logo.png");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GrazyGolf.MENU_SCREEN_WIDTH, GrazyGolf.MENU_SCREEN_HEIGHT);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        int x_E = GrazyGolf.MENU_SCREEN_WIDTH/2 - EXIT_BUTTON_WIDTH/2;
        int x_P =GrazyGolf.MENU_SCREEN_WIDTH/2 - PLAY_BUTTON_WIDTH/2;
        int x_L =GrazyGolf.MENU_SCREEN_WIDTH/2 - LOGO_WIDTH/2;
        game.font.draw(game.batch, "Welcome to GG!!! ", 100, 490);
        game.batch.draw(logo, x_L , LOGO_Y, LOGO_WIDTH, LOGO_HEIGHT);
        /* Changes the color of the buttons when the mouse is on top of the buttons and makes the buttons work
         * */
        if(Gdx.input.getX() < x_E + EXIT_BUTTON_WIDTH && Gdx.input.getX() > x_E && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() < EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() > EXIT_BUTTON_Y){
            game.batch.draw(exitButtonActive, x_E, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
            if(Gdx.input.isTouched()){
                Gdx.app.exit();
            }
        }
        else{
            game.batch.draw(exitButtonInactive, x_E, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        }

        if(Gdx.input.getX() < x_P + PLAY_BUTTON_WIDTH && Gdx.input.getX() > x_P && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT && GrazyGolf.MENU_SCREEN_HEIGHT - Gdx.input.getY() > PLAY_BUTTON_Y){
            game.batch.draw(playButtonActive, x_P, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
            if (Gdx.input.isTouched()) {
                game.setScreen(new GameScreen(game, 50, 50, 32, 32));
                dispose();
            }
        }
        else{
            game.batch.draw(playButtonInactive, x_P, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        }

        game.batch.end();


    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}