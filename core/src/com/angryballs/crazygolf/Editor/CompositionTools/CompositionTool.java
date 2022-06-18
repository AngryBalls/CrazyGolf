package com.angryballs.crazygolf.Editor.CompositionTools;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class CompositionTool {

    public final String name;

    protected final LevelInfo levelInfo;

    public CompositionTool(String name, LevelInfo levelInfo) {
        this.name = name;
        this.levelInfo = levelInfo;
    }

    public boolean handleDelete(Vector2 deleteTarget) {
        return false;
    };

    public boolean handleClick(Vector2 clickOrigin) {
        return false;
    }

    public boolean handleDrag(Rectangle dragRect) {
        return false;
    }

    public Vector2 applyCursorOffset(Vector2 cursorPos) {
        return cursorPos;
    }
}
