package com.angryballs.crazygolf;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GrazyGolf extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render() {
		ScreenUtils.clear(1, 1, 1, 1);
		batch.begin();
		//changed 0 to 10
		batch.draw(img, 60, 60);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}
}
