package com.angryballs.crazygolf.Physics;

import java.util.List;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.BallModel;
import com.angryballs.crazygolf.Models.TreeModel;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

/**
 * The base PhysicsEngine class which contains components that will be used by
 * all deriving engines.
 */
public abstract class PhysicsEngine {

    // 0 -> Off
    // 1 -> Always on
    // 2 -> Adaptive
    private static int useNewPhysics = 2;

    // Current State
    public double x; // x coordinate
    public double y; // y coordinate
    public double vx; // speed in x
    public double vy; // speed in y

    // Physics properties
    private static final float defaultH = 0.001f; // a single time step of length h
    protected static final float g = 9.81f;
    protected static final float dh = 0.000000001f; // derivative step
    protected static final float threshold = 0.01f;

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

    private final List<Vector2> trees;

    public PhysicsEngine(LevelInfo info) {
        levelInfo = info;
        this.trees = info.trees;

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

    protected void reset() {
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
        vx = Math.max(-5, Math.min(velocity.x, 5));
        vy = Math.max(-5, Math.min(velocity.y, 5));
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
        return iterate(defaultH);
    }

    public final int iterate(float h) {
        if (!ballMoving) {
            // System.out.println("Iteration is called on stationary ball, returning
            // early.");
            return 1;
        }

        var derivative = derivative(x, y);

        performCalculations(derivative, h);

        if (getHeight(this.x, this.y) < 0) {
            ballMoving = false;
            return 2;
        }

        if (collidesWithTree()) {
            ballMoving = false;
            return 2;
        }

        collidesWithWall();
        // (x-targetX)^2 + (y-targetY)^2 <= radius^2
        if (isInCircle(this.x, this.xt, this.y, this.yt, this.r)) {
            ballMoving = false;
            return 3;
        }

        // Check if the ball is in the final position (will not move)
        if (Math.abs(vx) <= threshold && Math.abs(vy) <= threshold) { // 0.09
            if (Math.abs(derivative.x) < Float.MIN_VALUE && Math.abs(derivative.y) < Float.MIN_VALUE) {
                ballMoving = false;
                return 1;
            } else if (!isInSand() && us > Math.sqrt(Math.pow(derivative.x, 2) + Math.pow(derivative.y, 2))) {
                ballMoving = false;
                return 1;
            } else if (isInSand() && uss > Math.sqrt(Math.pow(derivative.x, 2) + Math.pow(derivative.y, 2))) {
                ballMoving = false;
                return 1;
            } else
                return 0;
        } else
            return 0;
    }

    protected abstract void performCalculations(Vector2 derivative, float h);

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
    private final boolean isInCircle(double x, double centerX, double y, double centerY, double r) {
        return Math.pow((x - centerX), 2) + Math.pow((y - centerY), 2) <= r * r;
    }

    private final double getHeight(double x, double y) {
        return levelInfo.heightProfile(x, y);
    }

    private final boolean isInSand() {
        return (x >= sandBoundsX.x && x <= sandBoundsY.x) && (y >= sandBoundsX.y && y <= sandBoundsY.y);
    }

    /**
     * Method to calculate the partial derivative of Height function with respect to
     * X or Y
     *
     * @param v1 current X coordinate
     * @param v2 current Y coordinate
     * @return dh/dx AND dh/dy
     */
    protected final Vector2 derivative(double v1, double v2) {
        return new Vector2(
                (float) ((getHeight(v1 + dh, v2) - getHeight(v1, v2)) / dh),
                (float) ((getHeight(v1, v2 + dh) - getHeight(v1, v2)) / dh));
    }

    protected final double accelerationX(double offset, Vector2 dh) {
        return accelerationX(offset, dh.x, dh.y);
    }

    protected final double accelerationX(double offset, double dx, double dy) {
        var u = isInSand() ? usk : uk;

        if (isSteep(dx, dy)) {
            double vx = this.vx + offset;
            return -g * dx / (1 + dx * dx + dy * dy)
                    - u * g * (vx) / Math.sqrt(vx * vx + vy * vy + (dx * vx + dx * vy) * (dx * vx + dy * vy));
        } else {
            double sqrt = Math.sqrt(Math.pow(vx + offset, 2) + Math.pow(vy, 2));
            return -g * dx - u * g * (vx + offset) / sqrt;
        }
    }

    protected final double accelerationY(double offset, Vector2 dh) {
        return accelerationY(offset, dh.x, dh.y);
    }

    protected final double accelerationY(double offset, double dx, double dy) {
        var u = isInSand() ? usk : uk;
        if (isSteep(dx, dy)) {
            double vy = this.vy + offset;
            return -g * dy / (1 + dx * dx + dy * dy)
                    - u * g * (vy) / Math.sqrt(vx * vx + vy * vy + (dx * vx + dy * vy) * (dx * vx + dy * vy));
        } else {
            double sqrt = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy + offset, 2));
            return -g * dy - u * g * (vy + offset) / sqrt;
        }
    }

    /**
     * Method to calculate the acceleration for a specific state vector in X or Y
     * axis
     *
     * @return acceleration w.r.t. X AND Y
     */
    protected final Vector2 acceleration(Vector2 dh) {
        var u = isInSand() ? usk : uk;

        if (isSteep(dh.x, dh.y)) {
            var x = -g * dh.x / (1 + dh.x * dh.x + dh.y * dh.y) -
                    u * g * (vx) / Math.sqrt(vx * vx + vy * vy + (dh.x * vx + dh.x * vy) * (dh.x * vx + dh.y * vy));
            var y = -g * dh.y / (1 + dh.x * dh.x + dh.y * dh.y) -
                    u * g * (vy) / Math.sqrt(vx * vx + vy * vy + (dh.x * vx + dh.y * vy) * (dh.x * vx + dh.y * vy));
            return new Vector2((float) x, (float) y);
        } else {
            double sqrt = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
            var x = -g * dh.x - u * g * (vx) / sqrt;
            var y = -g * dh.y - u * g * (vy) / sqrt;
            return new Vector2((float) x, (float) y);
        }
    }

    private final boolean isSteep(double dx, double dy) {
        if (PhysicsEngine.useNewPhysics == 0)
            return false;
        else if (PhysicsEngine.useNewPhysics == 1)
            return true;

        return (Math.abs(1 - dx * dx) < PhysicsEngine.dh || Math.abs(1 - dy * dy) < PhysicsEngine.dh);
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

    public double getXt() {
        return xt;
    }

    public double getYt() {
        return yt;
    }

    public void setStateVector(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;

        this.vx = vx;
        this.vy = vy;
    }

    public void printStateVector() {
        System.out.println("X: " + this.x + ", Y:" + this.y + ", Vx: " + vx + ", Vy: " + vy);
    }

    public double getRadius() {
        return (double) r;
    }

    private boolean isIntersectingTree(Vector2 position) {
        var ballRadius = BallModel.ballRadius;

        var distanceSquared = Vector2.dst((float) x, (float) y, position.x, position.y);

        if (distanceSquared < ballRadius + TreeModel.treeRadius)
            return true;
        return false;
    }

    private boolean collidesWithTree() {
        for (Vector2 tree : trees)
            if (isIntersectingTree(tree))
                return true;

        return false;
    }

    private boolean collidesWithWall() {
        for (int i = 0; i < levelInfo.walls.size(); i++) {
            if (isIntersectingWall(levelInfo.walls.get(i)))
                return true;
        }
        return false;
    }

    private boolean isIntersectingWall(Rectangle rectangle) {
        var ball = new Circle((float) x, (float) y, BallModel.ballRadius);

        // Ball doesn't intersect wall
        if (!Intersector.overlaps(ball, rectangle))
            return false;

        int roundedX = (int) Math.round(x);
        int roundedY = (int) Math.round(y);

        float xAbs = (float) Math.abs(x - roundedX);
        float yAbs = (float) Math.abs(y - roundedY);

        if (xAbs < yAbs) {
            vx = -vx;
        } else {
            vy = -vy;
        }
        return true;
    }

}
