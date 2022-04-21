package com.angryballs.crazygolf.Physics;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

/**
 * A physics system using a general second-order Runge-Kutta method to
 * approximate the results
 */
public class RK4PhysicsEngine extends PhysicsEngine {
    public RK4PhysicsEngine(LevelInfo levelInfo) {
        super(levelInfo);
    }

    @Override
    protected void performCalculations(Vector2 derivative) {
        var res1 = rkKForPos();
        this.x += res1.x;
        this.y += res1.y;

        var res2 = rkKforVel(derivative);
        this.vx += res2.x;
        this.vy += res2.y;
    }

    private Vector2 rkKForPos() {
        double x = 0;
        {
            var k1 = h * vx;

            var k2 = h * (vx + accelerationX(0, derivativeX(x + 0.5 * k1, y), this.uk) * h / 2);
            var k3 = h * (vx + accelerationX(0, derivativeX(x + 0.5 * k2, y), this.uk) * h / 2);
            var k4 = h * (vx + accelerationX(0, derivativeX(x + k3, y), this.uk) * h);
            x = h / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
        }

        double y = 0;
        {
            var k1 = h * vy;

            var k2 = h * (vy + accelerationY(0, derivativeY(x, y + 0.5 * k1), this.uk) * h / 2);
            var k3 = h * (vy + accelerationY(0, derivativeY(x, y + 0.5 * k2), this.uk) * h / 2);
            var k4 = h * (vy + accelerationY(0, derivativeY(x, y + k3), this.uk) * h);
            y = h / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
        }

        return new Vector2((float) x, (float) y);
    }

    private Vector2 rkKforVel(Vector2 derivative) {
        double x = 0;
        {
            var k1 = h * accelerationX(0, derivative.x, this.uk);
            var k2 = h * accelerationX(0.5 * k1, derivative.x, this.uk);
            var k3 = h * accelerationX(0.5 * k2, derivative.x, this.uk);
            var k4 = h * accelerationX(k3, derivative.x, this.uk);

            x = h / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
        }

        double y = 0;
        {
            var k1 = h * accelerationY(0, derivative.y, this.uk);
            var k2 = h * accelerationY(0.5 * k1, derivative.y, this.uk);
            var k3 = h * accelerationY(0.5 * k2, derivative.y, this.uk);
            var k4 = h * accelerationY(k3, derivative.y, this.uk);

            y = h / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
        }

        return new Vector2((float) x, (float) y);
    }

}
