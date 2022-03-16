package com.angryballs.crazygolf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;


public class chunck {

    int x;
    int y;
    Pixmap pixmap;
    Texture text;

    public chunck(int x, int y, int screenWidth, int screenHeight, int resolution, Pixmap map) {
        this.x = x;
        this.y = y;
        pixmap = new Pixmap(screenWidth * resolution, screenHeight * resolution, Pixmap.Format.RGBA8888);
        this.text = new Texture(map);
    }

    public int get_X() {
        return this.x;
    }

    public int get_Y() {
        return this.y;
    }

    public Pixmap get_Pixmap() {
        return this.pixmap;
    }

    public void set_Pixmap(Pixmap map) {
        this.pixmap = map;
    }

    public Texture get_Texture() {
        return this.text;
    }

}
