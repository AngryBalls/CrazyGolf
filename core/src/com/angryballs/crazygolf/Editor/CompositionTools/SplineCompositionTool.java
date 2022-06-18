package com.angryballs.crazygolf.Editor.CompositionTools;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.SplineInfo;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SplineCompositionTool extends CompositionTool {

    private SplineInfo currentSplineInfo;

    public SplineCompositionTool(LevelInfo levelInfo) {
        super("Splines", levelInfo);
    }

    @Override
    public boolean handleDrag(Rectangle dragRect) {
        boolean modified;
        modified = levelInfo.splines.remove(currentSplineInfo);

        if (dragRect.area() == 0)
            return modified;

        currentSplineInfo = new SplineInfo((int) dragRect.x, (int) dragRect.y, (int) dragRect.width,
                (int) dragRect.height, levelInfo);

        levelInfo.splines.add(currentSplineInfo);
        return true;
    }

    @Override
    public boolean handleClick(Vector2 cursorPos) {
        currentSplineInfo = null;
        return true;
    }

    @Override
    public boolean handleDelete(Vector2 deleteTarget) {
        for (int i = 0; i < levelInfo.splines.size(); i++) {
            var reverseIndex = levelInfo.splines.size() - 1 - i;
            if (levelInfo.splines.get(reverseIndex).isInModifiableArea(deleteTarget.x, deleteTarget.y)) {
                levelInfo.splines.remove(reverseIndex);
                return true;
            }
        }
        return false;
    }
}
