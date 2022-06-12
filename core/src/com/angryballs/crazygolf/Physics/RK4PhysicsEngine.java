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

        var k1x = h * vx;
        var k1y = h * vy;

        var k2x = h * (vx + accelerationX(0, derivative(x + 0.5 * k1x, y + 0.5 * k1y)) * h / 2);
        var k2y = h * (vy + accelerationY(0, derivative(x + 0.5 * k1x, y + 0.5 * k1y)) * h / 2);

        var k3x = h * (vx + accelerationX(0, derivative(x + 0.5 * k2x, y + 0.5 * k2y)) * h / 2);
        var k3y = h * (vy + accelerationY(0, derivative(x + 0.5 * k2x, y + 0.5 * k2y)) * h / 2);

        var k4x = h * (vx + accelerationX(0, derivative(x + k3x, y + k3y)) * h);
        var k4y = h * (vy + accelerationY(0, derivative(x + k3x, y + k3y)) * h);

        x = 1f / 6 * (k1x + 2 * k2x + 2 * k3x + k4x);
        y = 1f / 6 * (k1y + 2 * k2y + 2 * k3y + k4y);


        return new Vector2((float) x, (float) y);
    }

    private Vector2 rkKforVel(Vector2 derivative, float h) {
        double x = 0;
        {
            var k1 = h * accelerationX(0, derivative);
            var k2 = h * accelerationX(0.5 * k1, derivative);
            var k3 = h * accelerationX(0.5 * k2, derivative);
            var k4 = h * accelerationX(k3, derivative);

            x = 1f / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
        }

        double y = 0;
        {
            var k1 = h * accelerationY(0, derivative);
            var k2 = h * accelerationY(0.5 * k1, derivative);
            var k3 = h * accelerationY(0.5 * k2, derivative);
            var k4 = h * accelerationY(k3, derivative);

            y = 1f / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
        }

        return new Vector2((float) x, (float) y);
    }

}
