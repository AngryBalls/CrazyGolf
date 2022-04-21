package com.angryballs.crazygolf.Physics;

import com.angryballs.crazygolf.*;
import com.badlogic.gdx.math.*;

/**
 * The base PhysicsEngine class which contains components that will be used by
 * all deriving engines.
 */
public abstract class PhysicsEngine {

    // Current State
    public double x; // x coordinate
    public double y; // y coordinate
    public double vx; // speed in x
    public double vy; // speed in y

    // Physics properties
    protected static final float h = 0.001f; // a single time step of length h
    protected static final float g = 9.81f;
    protected static final float dh = 0.000000001f; // derivative step

    protected float uk;
    protected float us;

    protected float usk;
    protected float uss;

    protected float xt;
    protected float yt;
    protected float r;

    protected Vector2 sandBoundsX;
    protected Vector2 sandBoundsY;

    protected final LevelInfo levelInfo;

    protected boolean sandFlag = false;

    public PhysicsEngine(LevelInfo info) {
        levelInfo = info;

        applyPhysicsProperties();
        reset();
    }

    private void applyPhysicsProperties() {
        uk = LevelInfo.exampleInput.grassKineticFrictionCoeff;
        us = LevelInfo.exampleInput.grassStaticFrictionCoeff;

        usk = LevelInfo.exampleInput.sandKineticFrictionCoeff;
        uss = LevelInfo.exampleInput.sandStaticFrictionCoeff;

        xt = LevelInfo.exampleInput.endPosition.x;
        yt = LevelInfo.exampleInput.endPosition.y;
        r = LevelInfo.exampleInput.holeRadius;

        sandBoundsX = LevelInfo.exampleInput.sandPitBounds[0];
        sandBoundsY = LevelInfo.exampleInput.sandPitBounds[1];
    }

    private void reset() {
        vx = vy = 0;
        x = levelInfo.startPosition.x;
        y = levelInfo.startPosition.y;
    }

    protected boolean ballMoving = false;

    /**
     * Performs a move, applying the input velocity to the ball
     *
     * @param velocity the velocity to be applied to the ball
     */
    public final void performMove(Vector2 velocity) {
        if (velocity.epsilonEquals(Vector2.Zero)) {
            System.out.println("Didn't actually hit the ball.");
            return;
        }
        vx = velocity.x;
        vy = velocity.y;
        ballMoving = true;
    }

    /**
     * Method to update the state vector
     *
     * @return the reason why the ball should stop or not
     *         0: no stop
     *         1: the ball has no speed and acceleration
     *         2: the ball is in the water (or tree)
     *         3: the ball is in the hole
     */

    public final int iterate() {
        if (!ballMoving) {
            // System.out.println("Iteration is called on stationary ball, returning
            // early.");
            return 1;
        }

        sandFlag = false;

        if ((x >= sandBoundsX.x && x <= sandBoundsY.x) && (y >= sandBoundsX.y && y <= sandBoundsY.y))
            sandFlag = true;

        var derivative = derivative(x, y);

        performCalculations(derivative);

        // TODO: add the range of trees
        if (getHeight(this.x, this.y) < 0) {
            ballMoving = false;
            return 2;
        }

        // (x-targetX)^2 + (y-targetY)^2 <= radius^2
        if (isInCircle(this.x, this.xt, this.y, this.yt, this.r)) {
            ballMoving = false;
            return 3;
        }

        // Check if the ball is in the final position (will not move)
        if (Math.abs(vx) <= 0.1 && Math.abs(vy) <= 0.1) { // 0.09
            if (Math.abs(derivative.x) < Float.MIN_VALUE && Math.abs(derivative.y) < Float.MIN_VALUE) {
                ballMoving = false;
                return 1;
            } else if (!sandFlag && us > Math.sqrt(Math.pow(derivative.x, 2) + Math.pow(derivative.y, 2))) {
                ballMoving = false;
                return 1;
            } else if (sandFlag && uss > Math.sqrt(Math.pow(derivative.x, 2) + Math.pow(derivative.y, 2))) {
                ballMoving = false;
                return 1;
            } else
                return 0;
        } else
            return 0;
    }

    protected abstract void performCalculations(Vector2 derivative);

    /**
     * Method to check if the ball inside the hole's radius
     *
     * @param x       current X coordinate
     * @param centerX the target X coordinate
     * @param y       current Y coordinate
     * @param centerY the target Y coordinate
     * @param r       the radius around the hole
     * @return Is the ball inside the target's radius?
     */
    public final boolean isInCircle(double x, double centerX, double y, double centerY, double r) {
        return Math.pow((x - centerX), 2) + Math.pow((y - centerY), 2) <= r * r;
    }

    public final double getHeight(double x, double y) {
        return levelInfo.heightProfile(x, y);
    }

    /**
     * Method to calculate the partial derivative of Height function with respect to
     * X or Y
     *
     * @param v1 current X coordinate
     * @param v2 current Y coordinate
     * @return dh/dx AND dh/dy
     */
    public final Vector2 derivative(double v1, double v2) {
        return new Vector2(
                (float) ((getHeight(v1 + dh, v2) - getHeight(v1, v2)) / dh),
                (float) ((getHeight(v1, v2 + dh) - getHeight(v1, v2)) / dh));
    }

    /**
     * Method to calculate the acceleration for a specific state vector in X or Y
     * axis
     *
     * @param u friction coefficient
     * @return acceleration w.r.t. X AND Y
     */
    public final Vector2 acceleration(double offset, Vector2 dh, double u) {
        double sqrt1 = Math.sqrt(Math.pow(vx + offset, 2) + Math.pow(vy, 2));
        double sqrt2 = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy + offset, 2));

        var x = -g * dh.x - u * g * (vx) / sqrt1;
        var y = -g * dh.y - u * g * (vy) / sqrt2;

        return new Vector2((float) x, (float) y);
    }

    public final double getX() {
        return x;
    }

    public final double getY() {
        return y;
    }

    public final double getVx() {
        return vx;
    }

    public final double getVy() {
        return vy;
    }
}
