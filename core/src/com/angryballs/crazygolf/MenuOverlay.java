package com.angryballs.crazygolf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MenuOverlay extends Stage {
    public static int MENU_SCREEN_WIDTH = 240;
    public static int MENU_SCREEN_HEIGHT = 200;

    private static int EXIT_BUTTON_WIDTH = 250;
    private static int EXIT_BUTTON_HEIGHT = 100;
    private static int PLAY_BUTTON_WIDTH = 280;
    private static int PLAY_BUTTON_HEIGHT = 100;
    private static int EXIT_BUTTON_Y = 70;
    private static int PLAY_BUTTON_Y = 220;

    private static int LOGO_WIDTH = 300;
    private static int LOGO_HEIGHT = 150;
    private static int LOGO_Y = 550;

    private Texture logo;

    private BitmapFont font;

    private SpriteBatch spriteBatch;

    private final Runnable exitCallback;
    private final Runnable playCallback;

    private final boolean showLogo;

    private final ImageButton playButton;
    private final ImageButton exitButton;

    private final Sound tapSound;

    public MenuOverlay(boolean showLogo, Runnable playAction, Runnable exitAction) {

        this.showLogo = showLogo;

        playCallback = playAction;
        exitCallback = exitAction;

        var playButtonActive = new TextureRegionDrawable(new Texture("play_button_active.png"));
        var playButtonInactive = new TextureRegionDrawable(new Texture("play_button_inactive.png"));
        var exitButtonActive = new TextureRegionDrawable(new Texture("exit_button_active.png"));
        var exitButtonInactive = new TextureRegionDrawable(new Texture("exit_button_inactive.png"));

        int x = MENU_SCREEN_WIDTH / 2 - EXIT_BUTTON_WIDTH / 2;
        tapSound = Gdx.audio.newSound(Gdx.files.internal("tap.wav"));

        playButton = new ImageButton(createButtonStyleWith(playButtonInactive, playButtonActive, playButtonActive));
        playButton.setBounds(x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tapSound.play();
                playCallback.run();
            }
        });

        exitButton = new ImageButton(createButtonStyleWith(exitButtonInactive, exitButtonActive, exitButtonActive));
        exitButton.setBounds(x, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tapSound.play();
                exitCallback.run();
            }
        });

        addActor(playButton);
        addActor(exitButton);

        if (showLogo) {
            logo = new Texture("logo.png");
            font = new BitmapFont();
        }

        spriteBatch = new SpriteBatch();
    }

    @Override
    public void draw() {
        var batch = getBatch();
        batch.begin();

        if (showLogo) {
            int x_L = MENU_SCREEN_WIDTH / 2 - LOGO_WIDTH / 2;
            font.draw(batch, "Welcome to GG!!! ", 100, 500);
            batch.draw(logo, 10, LOGO_Y, LOGO_WIDTH, LOGO_HEIGHT);
        }

        batch.end();
        super.draw();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        if (showLogo) {
            font.dispose();
            logo.dispose();
        }
        super.dispose();
    }

    private ImageButtonStyle createButtonStyleWith(TextureRegionDrawable up, TextureRegionDrawable down,
            TextureRegionDrawable over) {
        var ButtonStyle = new ImageButtonStyle();
        ButtonStyle.imageUp = up;
        ButtonStyle.imageOver = down;
        ButtonStyle.imageDown = down;
        return ButtonStyle;
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
        MENU_SCREEN_WIDTH = width;
        MENU_SCREEN_HEIGHT = height;

        playButton.setBounds(10, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        exitButton.setBounds(10, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
    }
}
