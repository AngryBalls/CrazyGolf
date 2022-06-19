package com.angryballs.crazygolf.Physics;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

/**
 * A physics system using Euler's method to approximate the results
 */
public class EulersPhysicsEngine extends PhysicsEngine {
    public EulersPhysicsEngine(LevelInfo levelInfo) {
        super(levelInfo);
    }

    @Override
    protected void performCalculations(Vector2 derivative, float h) {
        var a = acceleration(derivative);

        this.x += h * vx;
        this.y += h * vy;

        this.vx += h * a.x;
        this.vy += h * a.y;
    }

}
