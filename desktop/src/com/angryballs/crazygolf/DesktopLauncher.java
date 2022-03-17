package com.angryballs.crazygolf;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.angryballs.crazygolf.GrazyGolf;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(GrazyGolf.MENU_SCREEN_WIDTH, GrazyGolf.MENU_SCREEN_HEIGHT);
		config.setResizable(false);
		new Lwjgl3Application(new GrazyGolf(), config);
	}
}
