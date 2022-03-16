package com.angryballs.crazygolf;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GrazyGolf extends Game {
	SpriteBatch batch;
	final int SCREENWIDTH = 32;
	final int SCREENHEIGHT = 32;
	final int RESOLUTION = 100;

	@Override
	public void create() {
		batch = new SpriteBatch();
		this.setScreen(new GameScreen(this, 50, 50, SCREENWIDTH, SCREENHEIGHT, RESOLUTION));
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
