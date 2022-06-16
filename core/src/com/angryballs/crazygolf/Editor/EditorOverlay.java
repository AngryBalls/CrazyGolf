package com.angryballs.crazygolf.Editor;

import java.util.Stack;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.AI.Pathfinding.Pathfinder;
import com.angryballs.crazygolf.Models.BallModel;
import com.angryballs.crazygolf.Models.TerrainModel;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class EditorOverlay {

    private enum EditorMode {
        tree,
        wall,
        terrain
    };

    private EditorMode currentMode = EditorMode.tree;

    private boolean enabled = false;

    private Runnable terrainModifiedEvent;

    private LevelInfo levelInfo;

    private TerrainModel gridModel;
    private BallModel ballModel;

    private Vector2 cursorPos = new Vector2();

    private Vector2 cursorDragStart = new Vector2();
    private Rectangle currentWallRect;

    private BallModel[] pathFindIndicator = new BallModel[0];

    private Stack<BallModel> ballModelPool = new Stack<BallModel>();

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
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getCurrentMode() {
        return currentMode.toString();
    }

    private Vector2 currentlyTargetedNode(Vector3 origin, Vector3 direction) {
        Vector3 sclDirection = new Vector3(direction).scl(0.5f);
        Vector3 currentPosition = new Vector3(origin);

        // double lastHeight = currentPosition.y;

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

        float x = 0;
        float y = 0;

        if (currentMode == EditorMode.terrain) {
            x = Math.round(currentPosition.x / 2) * 2;
            y = -Math.round(currentPosition.z / 2) * 2;
        } else {
            x = Math.round(currentPosition.x);
            y = -Math.round(currentPosition.z);
        }

        return new Vector2(x, y);
    }

    public void update(Camera cam) {
        var lastCursorPos = cursorPos;
        var pos = cursorPos = currentlyTargetedNode(cam.position, cam.direction);

        var height = levelInfo.heightProfile(pos.x, pos.y);

        var markerPos = new Vector3(pos.x, (float) (height + 0.5), -pos.y);

        ballModel.transform.setTranslation(markerPos);

        if (!lastCursorPos.equals(cursorPos)) {
            var gridDelta = new Vector2(cursorPos).sub(cursorDragStart);

            if (currentMode == EditorMode.wall && isHoldingMouse) {
                var tlX = Math.min(cursorPos.x, cursorDragStart.x);
                var tlY = Math.min(cursorPos.y, cursorDragStart.y);

                Vector2 tlC = new Vector2(tlX, tlY);

                var newRect = new Rectangle(tlC.x, tlC.y, Math.abs(gridDelta.x),
                        Math.abs(gridDelta.y));

                levelInfo.walls.remove(currentWallRect);

                if (gridDelta.x == 0 || gridDelta.y == 0) {
                    currentWallRect = null;
                } else {
                    levelInfo.walls.add(newRect);
                    currentWallRect = newRect;
                }
                refreshLevel();
            }
        }
    }

    public void draw(ModelBatch modelBatch) {
        if (!enabled)
            return;

        modelBatch.render(gridModel);
        modelBatch.render(ballModel);

        for (BallModel ballModel : pathFindIndicator) {
            modelBatch.render(ballModel);
        }
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
            terrainModifiedEvent.run();
            return true;
        }
        if (keycode == Keys.FORWARD_DEL) {
            if (currentMode == EditorMode.tree) {
                if (levelInfo.trees.remove(cursorPos))
                    refreshLevel();
            }

            if (currentMode == EditorMode.wall) {
                for (int i = 0; i < levelInfo.walls.size(); i++) {
                    var reverseIndex = levelInfo.walls.size() - 1 - i;
                    if (levelInfo.walls.get(reverseIndex).contains(cursorPos)) {
                        levelInfo.walls.remove(reverseIndex);
                        refreshLevel();
                        break;
                    }
                }
            }
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

        if (currentMode == EditorMode.tree) {
            levelInfo.trees.add(cursorPos);
            refreshLevel();
        }

        if (currentMode == EditorMode.wall) {
            cursorDragStart = cursorPos;
            isHoldingMouse = true;
        }

        return true;
    }

    public boolean onMouseUp() {
        if (!enabled)
            return false;

        if (currentMode == EditorMode.wall) {
            isHoldingMouse = false;
            currentWallRect = null;
        }

        return true;
    }

    private void switchMode() {
        switch (currentMode) {
            case tree:
                currentMode = EditorMode.wall;
                break;
            case wall:
                currentMode = EditorMode.terrain;
                break;
            case terrain:
                currentMode = EditorMode.tree;
                break;
        }
    }

    private void refreshLevel() {
        terrainModifiedEvent.run();
        updatePathIndicators();

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

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPathBall() {
        var ball = new BallModel();
        ball.transform.scl(2, 2, 5);
        ballModelPool.add(ball);
    }
}
