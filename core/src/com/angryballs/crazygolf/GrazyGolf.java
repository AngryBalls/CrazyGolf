package com.angryballs.crazygolf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.awt.*;

public class GrazyGolf extends Game {
	SpriteBatch batch;
	public BitmapFont font;
	final int SCREENWIDTH = 32;
	final int SCREENHEIGHT = 32;
	final int RESOLUTION = 50;

	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		this.setScreen(new MenuScreen(this, 50, 50, SCREENWIDTH, SCREENHEIGHT, RESOLUTION));
	}
	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
