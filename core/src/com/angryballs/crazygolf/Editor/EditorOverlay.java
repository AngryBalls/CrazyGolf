package com.angryballs.crazygolf.Editor;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.SplineInfo;
import com.angryballs.crazygolf.Models.BallModel;
import com.angryballs.crazygolf.Models.TerrainModel;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
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

    private SplineInfo currentSplineInfo;

    public EditorOverlay(LevelInfo levelInfo, Runnable updateAction) {
        this.levelInfo = levelInfo;
        gridModel = new TerrainModel(levelInfo, true);
        gridModel.transform.trn(0, 0.5f, 0);

        ballModel = new BallModel();
        ballModel.transform.scl(5);
        terrainModifiedEvent = updateAction;
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

        if (currentMode == EditorMode.terrain) {
            var spline = levelInfo.getContainingSpline(new Vector2(currentPosition.x, -currentPosition.z));
            if (spline != null) {
                float x = Math.round((currentPosition.x - spline.x) / 2) * 2 + spline.x;
                float y = Math.round((-currentPosition.z - spline.y) / 2) * 2 + spline.y;
                return new Vector2(x, y);
            }
        }
        float x = Math.round(currentPosition.x);
        float y = -Math.round(currentPosition.z);

        return new Vector2(x, y);
    }

    Vector2 lastCursorPos = null;

    public void update(Camera cam) {
        lastCursorPos = cursorPos;
        var pos = cursorPos = currentlyTargetedNode(cam.position, cam.direction);

        var height = levelInfo.heightProfile(pos.x, pos.y);

        var markerPos = new Vector3(pos.x, (float) (height + 0.5), -pos.y);

        ballModel.transform.setTranslation(markerPos);

        if (!lastCursorPos.equals(cursorPos)) {
            var gridDelta = new Vector2(cursorPos).sub(cursorDragStart);

            if (currentMode == EditorMode.terrain && isHoldingMouse) {
                var splineCentre = new Vector2(cursorDragStart).add(cursorPos).scl(0.5f);

                levelInfo.splines.remove(currentSplineInfo);

                var newSpline = new SplineInfo((int) splineCentre.x, (int) splineCentre.y, (int) Math.abs(gridDelta.x),
                        (int) Math.abs(gridDelta.y),
                        levelInfo);

                if (gridDelta.x == 0 || gridDelta.y == 0) {
                    currentSplineInfo = null;
                } else {
                    levelInfo.splines.add(newSpline);
                    currentSplineInfo = newSpline;
                }
                terrainModifiedEvent.run();
            }
        }
    }

    public void draw(ModelBatch modelBatch) {
        if (!enabled)
            return;

        modelBatch.render(gridModel);
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
            terrainModifiedEvent.run();
            return true;
        }
        if (keycode == Keys.FORWARD_DEL) {
            if (currentMode == EditorMode.tree) {
                if (levelInfo.trees.remove(cursorPos))
                    terrainModifiedEvent.run();
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
            terrainModifiedEvent.run();
        }

        if (currentMode == EditorMode.terrain) {
            isHoldingMouse = true;

        }

        return true;
    }

    public boolean onMouseUp() {
        if (!enabled)
            return false;

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

}
