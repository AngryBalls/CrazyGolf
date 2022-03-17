package com.angryballs.crazygolf;

import com.badlogic.gdx.Game;

public class GrazyGolf extends Game {
	private GameScreen3D gameScreen;

	@Override
	public void create() {
		gameScreen = new GameScreen3D(LevelInfo.exampleInput);
		setScreen(gameScreen);
	}

	@Override
	public void dispose() {
		if (gameScreen != null)
			gameScreen.dispose();
	}
}
