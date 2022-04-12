package com.angryballs.crazygolf;

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

    private static final int EXIT_BUTTON_WIDTH = 250;
    private static final int EXIT_BUTTON_HEIGHT = 100;
    private static final int PLAY_BUTTON_WIDTH = 280;
    private static final int PLAY_BUTTON_HEIGHT = 100;
    private static final int EXIT_BUTTON_Y = 70;
    private static final int PLAY_BUTTON_Y = 220;

    private static final int LOGO_WIDTH = 300;
    private static final int LOGO_HEIGHT = 150;
    private static final int LOGO_Y = 530;

    private Texture logo;

    private BitmapFont font;

    private SpriteBatch spriteBatch;

    private final Runnable exitCallback;
    private final Runnable playCallback;

    private final boolean showLogo;

    private final ImageButton playButton;
    private final ImageButton exitButton;

    public MenuOverlay(boolean showLogo, Runnable playAction, Runnable exitAction) {

        this.showLogo = showLogo;

        playCallback = playAction;
        exitCallback = exitAction;

        var playButtonActive = new TextureRegionDrawable(new Texture("play_button_active.png"));
        var playButtonInactive = new TextureRegionDrawable(new Texture("play_button_inactive.png"));
        var exitButtonActive = new TextureRegionDrawable(new Texture("exit_button_active.png"));
        var exitButtonInactive = new TextureRegionDrawable(new Texture("exit_button_inactive.png"));

        int x = GrazyGolf.MENU_SCREEN_WIDTH / 2 - EXIT_BUTTON_WIDTH / 2;

        playButton = new ImageButton(createButtonStyleWith(playButtonInactive, playButtonActive, playButtonActive));
        playButton.setBounds(x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playCallback.run();
            }
        });

        exitButton = new ImageButton(createButtonStyleWith(exitButtonInactive, exitButtonActive, exitButtonActive));
        exitButton.setBounds(x, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
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
            int x_L = GrazyGolf.MENU_SCREEN_WIDTH / 2 - LOGO_WIDTH / 2;
            font.draw(batch, "Welcome to GG!!! ", 100, 490);
            batch.draw(logo, x_L, LOGO_Y, LOGO_WIDTH, LOGO_HEIGHT);
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
}
