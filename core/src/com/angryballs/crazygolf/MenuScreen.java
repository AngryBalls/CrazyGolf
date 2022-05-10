package com.angryballs.crazygolf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen extends ScreenAdapter {
    private MenuOverlay menuOverlay;

    public MenuScreen(GrazyGolf game) {
        menuOverlay = new MenuOverlay(true, () -> {
            game.Start_Game();
        }, () -> {
            Gdx.app.exit();
        });

        Gdx.input.setInputProcessor(menuOverlay);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        menuOverlay.act();
        menuOverlay.draw();
    }

    @Override
    public void dispose() {
        menuOverlay.dispose();
    }
}
