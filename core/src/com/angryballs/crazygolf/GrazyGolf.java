package com.angryballs.crazygolf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class GrazyGolf extends Game {
	SpriteBatch batch;
	public static final int MENU_SCREEN_WIDTH = 480;
	public static final int MENU_SCREEN_HEIGHT = 720;
	public static final int SCREEN_WIDTH = 32;
	public static final int SCREEN_HEIGHT = 32;
	BitmapFont font;

	@Override
	public void create() {
		batch = new SpriteBatch();
		this.setScreen(new MenuScreen(this));
		font = new BitmapFont();
	}
	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
