package com.angryballs.crazygolf.Editor.CompositionTools;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

public class TreeCompositionTool extends CompositionTool {
    public TreeCompositionTool(LevelInfo levelInfo) {
        super("Trees", levelInfo);
    }

    @Override
    public boolean handleDelete(Vector2 deleteTarget) {
        return levelInfo.trees.remove(deleteTarget);
    }

    @Override
    public boolean handleClick(Vector2 clickOrigin) {
        levelInfo.trees.add(clickOrigin);
        return true;
    }
}
