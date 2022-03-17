package com.angryballs.crazygolf;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;

public class GrazyGolf extends Game {
	private GameScreen3D gameScreen;
	SpriteBatch batch;
	public static final int MENU_SCREEN_WIDTH = 480;
	public static final int MENU_SCREEN_HEIGHT = 720;
	public static final int SCREEN_WIDTH = 32;
	public static final int SCREEN_HEIGHT = 32;


	@Override
	public void create() {
		gameScreen = new GameScreen3D(LevelInfo.exampleInput);
		//setScreen(gameScreen);
		batch = new SpriteBatch();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		if (gameScreen != null)
			gameScreen.dispose();
	}
}
