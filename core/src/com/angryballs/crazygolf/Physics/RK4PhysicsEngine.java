package com.angryballs.crazygolf.Physics;

import java.util.List;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.angryballs.crazygolf.Models.WallModel;
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
        {
            var k1 = h * vx;

            var k2 = h * (vx + accelerationX(0, derivativeX(x + 0.5 * k1, y)) * h / 2);
            var k3 = h * (vx + accelerationX(0, derivativeX(x + 0.5 * k2, y)) * h / 2);
            var k4 = h * (vx + accelerationX(0, derivativeX(x + k3, y)) * h);
            x = 1f / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
        }

        double y = 0;
        {
            var k1 = h * vy;

            var k2 = h * (vy + accelerationY(0, derivativeY(x, y + 0.5 * k1)) * h / 2);
            var k3 = h * (vy + accelerationY(0, derivativeY(x, y + 0.5 * k2)) * h / 2);
            var k4 = h * (vy + accelerationY(0, derivativeY(x, y + k3)) * h);
            y = 1f / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
        }

        return new Vector2((float) x, (float) y);
    }

    private Vector2 rkKforVel(Vector2 derivative, float h) {
        double x = 0;
        {
            var k1 = h * accelerationX(0, derivative.x);
            var k2 = h * accelerationX(0.5 * k1, derivative.x);
            var k3 = h * accelerationX(0.5 * k2, derivative.x);
            var k4 = h * accelerationX(k3, derivative.x);

            x = 1f / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
        }

        double y = 0;
        {
            var k1 = h * accelerationY(0, derivative.y);
            var k2 = h * accelerationY(0.5 * k1, derivative.y);
            var k3 = h * accelerationY(0.5 * k2, derivative.y);
            var k4 = h * accelerationY(k3, derivative.y);

            y = 1f / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
        }

        return new Vector2((float) x, (float) y);
    }

}
