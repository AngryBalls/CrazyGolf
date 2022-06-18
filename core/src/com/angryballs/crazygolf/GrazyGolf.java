package com.angryballs.crazygolf;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;

public class GrazyGolf extends Game {
	public static final int MENU_SCREEN_WIDTH = 480;
	public static final int MENU_SCREEN_HEIGHT = 720;
	public static final int SCREEN_WIDTH = 32;
	public static final int SCREEN_HEIGHT = 32;

	private Screen currentScreen;

	public void Switch_Menu() {
		var oldScreen = currentScreen;
		setScreen(currentScreen = new MenuScreen(this));
		if (oldScreen != null) {
			oldScreen.dispose();
		}
	}

	public void Start_Game() {
		var oldScreen = currentScreen;
		setScreen(currentScreen = new GameScreen3D(LevelInfo.exampleInput, this));
		if (oldScreen != null) {
			oldScreen.dispose();
		}
	}

	@Override
	public void create() {
		Switch_Menu();
	}

	@Override
	public void dispose() {
		if (currentScreen != null)
			currentScreen.dispose();
	}
}
