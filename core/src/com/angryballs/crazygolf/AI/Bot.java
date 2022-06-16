package com.angryballs.crazygolf.AI;

import java.util.List;
import java.util.Random;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.angryballs.crazygolf.Physics.GRK2PhysicsEngine;
import com.angryballs.crazygolf.Physics.PhysicsEngine;
import com.badlogic.gdx.math.Vector2;

public abstract class Bot {
    protected final PhysicsEngine ps;
    private final double EPSILON;

    // Target
    protected double xt;
    protected double yt;

    // Best coords
    protected double xb;
    protected double yb;

    // Best speeds
    protected double vxb;
    protected double vyb;

    public Bot(LevelInfo info, List<TreeModel> trees) {
        this.ps = new GRK2PhysicsEngine(info, trees);
        this.EPSILON = ps.getRadius();
        this.xt = ps.getXt();
        this.yt = ps.getYt();
    }

    public static final float noiseMagnitude = 0.1f;

    public static final Random noiseRNG = new Random();

    public Vector2 computeMove(double x, double y) {
        var move = computeOptimalMove(x, y);

        var shotMagnitude = move.len();
        var shotDirection = new Vector2(move).nor();

        var directionNoise = (-20 + noiseRNG.nextFloat() * 40) * noiseMagnitude;
        var magnitudeNoise = (-shotMagnitude + shotMagnitude * 2 * noiseRNG.nextFloat()) * noiseMagnitude;

        var noisyShot = shotDirection.rotateDeg(directionNoise).scl(shotMagnitude + magnitudeNoise);

        return noisyShot;
    }

    protected abstract Vector2 computeOptimalMove(double x, double y);

    /**
     * Estimates the distance between point ( x,y ) and target point
     * ! does NOT calculate the actual distance between 2 points
     *
     * @param x x coordinate of the point ( x,y )
     * @param y y coordinate of the point ( x,y )
     * @return estimated distance
     */
    public double distanceSquared(double x, double y) {
        return (x - xt) * (x - xt) + (y - yt) * (y - yt);
    }

    /**
     * Calculates the distance between point ( x,y ) and target point
     *
     * @param x x coordinate of the point ( x,y )
     * @param y y coordinate of the point ( x,y )
     * @return estimated distance
     */
    public double distance(double x, double y) {
        return Math.sqrt((x - xt) * (x - xt) + (y - yt) * (y - yt));
    }

    public int run() {
        long start = System.currentTimeMillis();
        System.out.println("Target:             ( " + xt + " , " + yt + " )");
        Vector2 coords = new Vector2((float) ps.x, (float) ps.y);
        Vector2 speeds = new Vector2();
        int i = 0;

        double distance = Double.MAX_VALUE;

        while (Math.abs(Math.sqrt(distance)) > EPSILON) {
            speeds = computeOptimalMove(coords.x, coords.y);
            ps.setStateVector(coords.x, coords.y, 0, 0);
            System.out.println("The state vector: " + coords + " " + speeds);
            performMove(speeds);

            coords.x = (float) ps.x;
            coords.y = (float) ps.y;
            distance = distanceSquared(coords.x, coords.y);
            i++;
            System.out.println("ShotNr: " + i);
        }

        System.out.println("The state vector: " + coords + " " + speeds);
        long end = System.currentTimeMillis();
        System.out.println("Ran in : " + (end - start) * 0.001 + " s");
        return i;
    }

    // Used for higher precision simulations
    protected final int performMove(Vector2 speed) {
        ps.performMove(speed);

        int result = 0;
        while (result == 0)
            result = ps.iterate();

        return result;
    }

    // Used for lower precision simulations
    protected final int performMoveFast(Vector2 speed) {
        ps.performMove(speed);

        int result = 0;
        while (result == 0)
            result = ps.iterate();

        return result;
    }

    public void applyPhysicsState(float x, float y, float vx, float vy) {
        ps.setStateVector(x, y, vx, vy);
    }

    public void applyPhysicsState(PhysicsEngine engine) {
        ps.setStateVector(engine.x, engine.y, engine.vx, engine.vy);
    }
}
