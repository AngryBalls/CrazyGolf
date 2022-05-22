package com.angryballs.crazygolf.Models;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

public class TreeModel {

    private Vector3 position;
    private Boolean headless;

    private final LogModel logModel;
    private final LeafModel leafModel;

    public final float treeRadius = 0.25f;

    public TreeModel() {
        this(false);
    }

    public TreeModel(boolean headless) {
        if (!headless) {
            logModel = new LogModel();
            leafModel = new LeafModel();
        } else {
            logModel = null;
            leafModel = null;
        }

        setPosition(new Vector3(0, 0, 0));
        this.headless = headless;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
        if (!headless) {
            logModel.transform.setTranslation(position);
            leafModel.transform.setTranslation(new Vector3(position).add(new Vector3(0, 5, 0)));
        }
    }

    public Vector3 getPosition() {
        return position;
    }

    public void Render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(logModel, environment);
        modelBatch.render(leafModel, environment);
    }
}
