package com.angryballs.crazygolf.Editor;

import java.util.HashMap;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.BallModel;
import com.angryballs.crazygolf.Models.TerrainModel;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class EditorOverlay {
    private static int resolutionX;
    private static int resolutionY;

    private boolean enabled = false;

    private Runnable terrainModifiedEvent;

    private LevelInfo levelInfo;

    private TerrainModel gridModel;
    private BallModel ballModel;

    public EditorOverlay(LevelInfo levelInfo) {
        this.levelInfo = levelInfo;
        gridModel = new TerrainModel(levelInfo, true);
        gridModel.transform.trn(0, 0.5f, 0);

        ballModel = new BallModel();
        ballModel.transform.scl(5);
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

        float x = Math.round(currentPosition.x / 2) * 2;
        float y = -Math.round(currentPosition.z / 2) * 2;

        return new Vector2(x, y);
    }

    public void update(Camera cam) {
        var pos = currentlyTargetedNode(cam.position, cam.direction);

        var height = levelInfo.heightProfile(pos.x, pos.y);

        var markerPos = new Vector3(pos.x, (float) (height + 0.5), -pos.y);

        ballModel.transform.setTranslation(markerPos);
    }

    public void draw(ModelBatch modelBatch) {
        modelBatch.render(gridModel);
        modelBatch.render(ballModel);
    }

    public boolean handleKey(int keycode) {
        // Stuff to raise or lower a node
        return false;
    }

}
