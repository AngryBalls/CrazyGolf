package com.angryballs.crazygolf.Editor;

import java.util.Stack;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Editor.CompositionTools.*;
import com.angryballs.crazygolf.Models.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.*;

public class EditorOverlay {
    private final CompositionTool[] compositionTools;

    private int compositionToolIndex = 0;

    private CompositionTool currentCompositionTool = null;

    private boolean enabled = false;

    private final Runnable terrainModifiedEvent;

    private final LevelInfo levelInfo;

    private TerrainModel gridModel;

    private final BallModel ballModel;

    private Vector2 cursorPos = new Vector2();

    private Vector2 cursorDragStart = new Vector2();

    private BallModel[] pathFindIndicator = new BallModel[0];

    private final Stack<BallModel> ballModelPool = new Stack<BallModel>();

    public EditorOverlay(LevelInfo levelInfo, Runnable updateAction) {
        this.levelInfo = levelInfo;
        gridModel = new TerrainModel(levelInfo, true);
        gridModel.transform.trn(0, 0.5f, 0);

        ballModel = new BallModel();
        ballModel.transform.scl(5);
        terrainModifiedEvent = updateAction;

        for (int i = 0; i < 100; ++i)
            createPathBall();

        updatePathIndicators();

        compositionTools = createCompositionTools();
        currentCompositionTool = compositionTools[compositionToolIndex];
    }

    private CompositionTool[] createCompositionTools() {
        return new CompositionTool[] {
                new TreeCompositionTool(levelInfo),
                new WallCompositionTool(levelInfo),
                new SplineCompositionTool(levelInfo)
        };
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getCurrentMode() {
        return currentCompositionTool.name;
    }

    private Vector2 currentlyTargetedNode(Vector3 origin, Vector3 direction) {
        Vector3 sclDirection = new Vector3(direction).scl(0.5f);
        Vector3 currentPosition = new Vector3(origin);

        boolean found = false;
        // Find intersect point
        for (int i = 0; i < 100; ++i) {
            currentPosition.add(sclDirection);

            var height = levelInfo.heightProfile(currentPosition.x, -currentPosition.z);
            if (currentPosition.y <= height) {
                found = true;
                break;
            }
        }

        if (!found)
            return new Vector2();

        return currentCompositionTool
                .applyCursorOffset(new Vector2(Math.round(currentPosition.x), -Math.round(currentPosition.z)));
    }

    public void update(Camera cam) {
        var lastCursorPos = cursorPos;
        var pos = cursorPos = currentlyTargetedNode(cam.position, cam.direction);

        if (!lastCursorPos.equals(cursorPos)) {
            var height = levelInfo.heightProfile(pos.x, pos.y);

            var markerPos = new Vector3(pos.x, (float) (height + 0.5), -pos.y);

            ballModel.transform.setTranslation(markerPos);

            var gridDelta = new Vector2(cursorPos).sub(cursorDragStart);

            if (isHoldingMouse) {
                var tlX = Math.min(cursorPos.x, cursorDragStart.x);
                var tlY = Math.min(cursorPos.y, cursorDragStart.y);

                Vector2 tlC = new Vector2(tlX, tlY);

                var normalizedRectangle = new Rectangle(tlC.x, tlC.y, Math.abs(gridDelta.x),
                        Math.abs(gridDelta.y));

                if (currentCompositionTool.handleDrag(normalizedRectangle))
                    refreshLevel();
            }
        }
    }

    public void draw(ModelBatch modelBatch) {
        if (!enabled)
            return;

        modelBatch.render(gridModel);
        modelBatch.render(ballModel);

        for (BallModel ballModel : pathFindIndicator)
            modelBatch.render(ballModel);
    }

    public boolean handleKeyPress(int keycode) {
        if (keycode == Keys.GRAVE) {
            enabled = !enabled;
            return true;
        }

        if (!enabled)
            return false;

        // Switch editor modes
        if (keycode == Keys.TAB) {
            switchMode();
            return true;
        }

        if (keycode == Keys.R) {
            levelInfo.reload();
            refreshLevel();
            return true;
        }
        if (keycode == Keys.FORWARD_DEL) {
            if (currentCompositionTool.handleDelete(cursorPos))
                refreshLevel();
            return true;
        }

        if (keycode == Keys.SPACE) {
            levelInfo.save();
            return true;
        }

        return false;
    }

    private boolean isHoldingMouse = false;

    public boolean onMouseDown() {
        if (!enabled)
            return false;

        isHoldingMouse = true;
        cursorDragStart = cursorPos;
        return true;
    }

    public boolean onMouseUp() {
        isHoldingMouse = false;

        if (!enabled)
            return false;

        if (currentCompositionTool.handleClick(cursorPos))
            refreshLevel();

        return true;
    }

    public boolean onScroll(float amount) {
        if (!enabled)
            return false;

        if (currentCompositionTool.handleScroll(cursorPos, amount))
            refreshLevel();

        return true;
    }

    private void switchMode() {
        currentCompositionTool = compositionTools[(++compositionToolIndex) % compositionTools.length];
    }

    private void refreshLevel() {
        terrainModifiedEvent.run();
        updatePathIndicators();

        gridModel = new TerrainModel(levelInfo, true);
        gridModel.transform.trn(0, 0.5f, 0);
    }

    private void updatePathIndicators() {
        for (BallModel ballModel : pathFindIndicator) {
            ballModelPool.add(ballModel);
        }

        var thing = levelInfo.optimalPath;
        if (thing == null) {
            pathFindIndicator = new BallModel[0];
            return;
        }

        pathFindIndicator = new BallModel[thing.path.size()];

        for (int i = 0; i < thing.path.size(); ++i) {
            var pos = thing.path.get(i);

            if (ballModelPool.empty())
                createPathBall();

            var ballModel = ballModelPool.pop();
            ballModel.transform.setTranslation(pos.x - 64 + 0.5f,
                    levelInfo.heightProfile(pos.x - 64 + 0.5f, pos.y - 64 + 0.5f).floatValue(),
                    -(pos.y - 64 + 0.5f));

            pathFindIndicator[i] = ballModel;
        }
    }

    private void createPathBall() {
        var ball = new BallModel();
        ball.transform.scl(2, 2, 5);
        ballModelPool.add(ball);
    }
}
