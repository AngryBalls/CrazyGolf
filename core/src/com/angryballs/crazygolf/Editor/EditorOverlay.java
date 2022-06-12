package com.angryballs.crazygolf.Editor;

import com.angryballs.crazygolf.LevelInfo;
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
        var pos = cursorPos = currentlyTargetedNode(cam.position, cam.direction);

        var height = levelInfo.heightProfile(pos.x, pos.y);

        var markerPos = new Vector3(pos.x, (float) (height + 0.5), -pos.y);

        ballModel.transform.setTranslation(markerPos);
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
