package com.angryballs.crazygolf.Editor;

import java.util.HashMap;

import com.angryballs.crazygolf.LevelInfo;
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

    public EditorOverlay(LevelInfo levelInfo) {
        this.levelInfo = levelInfo;
    }

    private Vector2 currentlyTargetedNode(Vector3 origin, Vector3 direction) {
        Vector3 sclDirection = new Vector3(direction).scl(0.01f);
        Vector3 currentPosition = new Vector3(origin);

        // Find intersect point
        for (int i = 0; i < 100; ++i) {
            currentPosition.add(sclDirection);

            var height = levelInfo.heightProfile(currentPosition.x, -currentPosition.z);
            if (origin.y < height) {
                if (currentPosition.y >= height)
                    break;
            } else if (currentPosition.y <= height)
                break;
        }

        float x = Math.round(currentPosition.x);
        float y = Math.round(-currentPosition.z);

        return new Vector2(x, y);
    }

    public void update(Camera cam) {

    }

    public void draw(ModelBatch modelBatch) {

    }

    public boolean handleKey(int keycode) {
        // Stuff to raise or lower a node
        return false;
    }

}
