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
    public abstract int iterate();

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
    public final Vector2 acceleration(Vector2 dh, double u) {
        return new Vector2((float) (-g * dh.x - u * g * (vx) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2))),
                (float) (-g * dh.y - u * g * (vy) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2))));
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
