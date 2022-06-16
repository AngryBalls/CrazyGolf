package com.angryballs.crazygolf.Editor.CompositionTools;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class WallCompositionTool extends CompositionTool {

    private Rectangle currentWallRect;

    public WallCompositionTool(LevelInfo levelInfo) {
        super("Walls", levelInfo);
    }

    @Override
    public boolean handleDelete(Vector2 deleteTarget) {
        for (int i = 0; i < levelInfo.walls.size(); i++) {
            var reverseIndex = levelInfo.walls.size() - 1 - i;
            if (levelInfo.walls.get(reverseIndex).contains(deleteTarget)) {
                levelInfo.walls.remove(reverseIndex);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleDrag(Rectangle dragRect) {
        boolean modified;
        modified = levelInfo.walls.remove(currentWallRect);

        if (dragRect.area() == 0)
            return modified;

        levelInfo.walls.add(dragRect);
        return true;
    }

}
