package com.angryballs.crazygolf.Physics;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

/**
 * A physics system using a general second-order Runge-Kutta method to
 * approximate the results
 */
public class GRK2PhysicsEngine extends PhysicsEngine {

    private static float alpha = 0.5f;

    public GRK2PhysicsEngine(LevelInfo levelInfo) {
        super(levelInfo);
    }

    @Override
    protected void performCalculations(Vector2 derivative) {
        var res1 = grk2ForPos();
        this.x += h * res1.x;
        this.y += h * res1.y;

        var res2 = grk2ForVel(derivative);
        this.vx += h * res2.x;
        this.vy += h * res2.y;
    }

    private Vector2 grk2ForPos() {
        Vector2 derivative = derivative(x + alpha * h * vx, y + alpha * h * vy);

        Vector2 acceleration = acceleration(0, derivative, uk);

        double x = (1 - 1 / (2 * alpha)) * vx + 1 / (2 * alpha) * (vx + acceleration.x * alpha * h);
        double y = (1 - 1 / (2 * alpha)) * vy + 1 / (2 * alpha) * (vy + acceleration.y * alpha * h); // vy + acce *
                                                                                                     // (0+ah)

        return new Vector2((float) x, (float) y);
    }

    private Vector2 grk2ForVel(Vector2 derivative) {
        var w = acceleration(0, derivative, uk);

        // This needs cleaning
        var accelerationX = acceleration(alpha * h * w.x, derivative, uk).x;
        var accelerationY = acceleration(alpha * h * w.y, derivative, uk).y;

        double x = (1 - 1 / (2 * alpha)) * w.x + 1 / (2 * alpha) * accelerationX;
        double y = (1 - 1 / (2 * alpha)) * w.y + 1 / (2 * alpha) * accelerationY;

        return new Vector2((float) x, (float) y);
    }
}
