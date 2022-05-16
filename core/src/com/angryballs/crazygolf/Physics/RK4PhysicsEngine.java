package com.angryballs.crazygolf.Physics;

import java.util.List;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.badlogic.gdx.math.Vector2;

/**
 * A physics system using a Runge-Kutta 4th order method to
 * approximate the results
 */
public class RK4PhysicsEngine extends PhysicsEngine {
    public RK4PhysicsEngine(LevelInfo levelInfo, List<TreeModel> trees) {
        super(levelInfo, trees);
    }

    @Override
    protected void performCalculations(Vector2 derivative, float h) {
        var res1 = rkKForPos(h);
        this.x += res1.x;
        this.y += res1.y;

        var res2 = rkKforVel(derivative, h);
        this.vx += res2.x;
        this.vy += res2.y;
    }

    private Vector2 rkKForPos(float h) {
        double x = 0;
        double y = 0;

        var k1_x = h * vx;
        var k1_y = h * vy;

        var k2_x = h * (vx + accelerationX(0, derivativeX(x + 0.5 * k1_x, y + 0.5 * k1_y)) * h / 2);
        var k2_y = h * (vy + accelerationY(0, derivativeY(x + 0.5 * k1_x, y + 0.5 * k1_y)) * h / 2);

        var k3_x = h * (vx + accelerationX(0, derivativeX(x + 0.5 * k2_x, y + 0.5 * k2_y)) * h / 2);
        var k3_y = h * (vy + accelerationY(0, derivativeY(x + 0.5 * k2_x, y + 0.5 * k2_y)) * h / 2);

        var k4_x = h * (vx + accelerationX(0, derivativeX(x + k3_x, y + k3_y)) * h);
        var k4_y = h * (vy + accelerationY(0, derivativeY(x + k3_x, y + k3_y)) * h);

        x = 1f / 6 * (k1_x + 2 * k2_x + 2 * k3_x + k4_x);
        y = 1f / 6 * (k1_y + 2 * k2_y + 2 * k3_y + k4_y);


        return new Vector2((float) x, (float) y);
    }

    private Vector2 rkKforVel(Vector2 derivative, float h) {
        double x = 0;
        double y = 0;

        var k1_x = h * accelerationX(0, derivative.x);
        var k1_y = h * accelerationY(0, derivative.y);


        var k2_x = h * accelerationX(0.5 * k1_x, derivative.x);
        var k2_y = h * accelerationY(0.5 * k1_y, derivative.y);


        var k3_x = h * accelerationX(0.5 * k2_x, derivative.x);
        var k3_y = h * accelerationY(0.5 * k2_y, derivative.y);


        var k4_x = h * accelerationX(k3_x, derivative.x);
        var k4_y = h * accelerationY(k3_y, derivative.y);

        x = 1f / 6 * (k1_x + 2 * k2_x + 2 * k3_x + k4_x);
        y = 1f / 6 * (k1_y + 2 * k2_y + 2 * k3_y + k4_y);

        return new Vector2((float) x, (float) y);
    }

}
