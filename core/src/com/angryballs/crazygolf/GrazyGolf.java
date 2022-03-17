package com.angryballs.crazygolf;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Game;

public class GrazyGolf extends Game {
	private GameScreen3D gameScreen;
	SpriteBatch batch;
	public static final int MENU_SCREEN_WIDTH = 480;
	public static final int MENU_SCREEN_HEIGHT = 720;
	public static final int SCREEN_WIDTH = 32;
	public static final int SCREEN_HEIGHT = 32;
	private Screen currentScreen;

	public void Switch_Menu() {
		var oldScreen = currentScreen;
		if (oldScreen != null) {
			oldScreen.dispose();
		}
		setScreen(currentScreen = new MenuScreen(this));

	}
	public void Start_Game() {
		var oldScreen = currentScreen;
		if (oldScreen != null) {
			oldScreen.dispose();
		}
		setScreen(currentScreen = new GameScreen3D(LevelInfo.exampleInput, this));

	}

	@Override
	public void create() {
		batch = new SpriteBatch();
		Switch_Menu();
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
