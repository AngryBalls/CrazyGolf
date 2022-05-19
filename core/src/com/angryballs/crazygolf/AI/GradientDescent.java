package com.angryballs.crazygolf.AI;

import java.lang.ref.SoftReference;
import java.util.List;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.angryballs.crazygolf.VelocityReader;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Gradient Descent based bot
 * Update formula:
 * Vnew = Vcurrent - A * F'(Vcurrent)
 *
 * - Learning rate
 * - Derivative of a fitness function
 *
 * - Fintess Function - distance from end position to target point
 * after applying speed from current position
 */
public class GradientDescent extends Bot {

    protected ArrayList<Double> fitness = new ArrayList<>();
    protected ArrayList<Double> speed = new ArrayList<>();

    // Specific GD variables
    private final double dv = 0.0001; // derivative step
    private float A = 0.01f; // step size of descent
    private final int ITERATION_LIMIT = 269;
    private final int UPDATE_LIMIT = 50;
    private final double DELTA_DISTANCE = 0.0001;
    private final double RADIUS;

    // Best data
    private double bestScore = Double.MAX_VALUE;
    private Vector2 bestSpeed = new Vector2();

    // Stat info
    private int lastupd = 0;
    private int iterator = 0;

    public GradientDescent(LevelInfo info, List<TreeModel> trees) {
        super(info, trees);
        RADIUS = ps.getRadius();
    }

    @Override
    public Vector2 computeOptimalMove(double x, double y) {
        return predict(x, y);
    }

    public double derivative(Vector2 coords, Vector2 speed, boolean isX) {
        double distV;
        double distVnew;
        int sign = 1;

        // Calculate the f(V) = dist(point, target)
        ps.setStateVector(coords.x, coords.y, 0, 0);
        performMove(speed);
        distV = distance(ps.x, ps.y);

        // Calculate the f(V+dV) = dist(new point, target)
        ps.setStateVector(coords.x, coords.y, 0, 0);
        Vector2 newSpeed;

        if (isX) {
            if (speed.x > 0) {
                newSpeed = new Vector2((float) (speed.x - dv), speed.y);
                sign = -1;
            } else
                newSpeed = new Vector2((float) (speed.x + dv), speed.y);
        } else {
            if (speed.y > 0) {
                newSpeed = new Vector2(speed.x, (float) (speed.y - dv));
                sign = -1;
            } else
                newSpeed = new Vector2(speed.x, (float) (speed.y + dv));
        }
        performMove(newSpeed);
        distVnew = distance(ps.x, ps.y);

        // Calculate the derivative
        return sign * (distVnew - distV) / dv;
    }

    /**
     * Performs a prediction of the next (vx,vy) vector
     *
     * @param x starting X coordinate
     * @param y starting Y coordinate
     * @return vector( vx, vy )
     */
    public Vector2 predict(double x, double y) {
        Vector2 coords = new Vector2((float) x, (float) y);

        // take target coords as direction + approximate values
        Vector2 curSpeed = new Vector2((float) Math.max(Math.min(this.xt - x, 5), -5),
                (float) Math.max(Math.min(this.yt - y, 5), -5));
        Vector2 predictedSpeed = new Vector2();

        double curScore = evaluate(coords, curSpeed);
        while (true) {
            double dvx = derivative(coords, curSpeed, true);
            double dvy = derivative(coords, curSpeed, false);
            // System.out.println("dVx: "+dvx+", dVy: "+dvy);
            predictedSpeed.x = curSpeed.x - A * (float) dvx;
            predictedSpeed.y = curSpeed.y - A * (float) dvy;
            predictedSpeed.x = Math.max(Math.min(predictedSpeed.x, 5), -5);
            predictedSpeed.y = Math.max(Math.min(predictedSpeed.y, 5), -5);
            curScore = evaluate(coords, predictedSpeed);
            // System.out.println("Speed: "+predictedSpeed+", Distance:
            // "+Math.sqrt(curScore));

            boolean stop = false;

            if (bestScore - curScore > DELTA_DISTANCE)
                lastupd = iterator;

            if (bestScore > curScore)
                bestScore = curScore;

            if (iterator - lastupd > UPDATE_LIMIT)
                stop = true;

            curSpeed = predictedSpeed;
            iterator++;

            if (iterator > ITERATION_LIMIT || curScore <= RADIUS * RADIUS || stop) {

                // System.out.println("X: "+ps.x+", Y: "+ps.y);
                // System.out.println("Counter: "+iterator);
                // System.out.println("Distance: "+bestScore);
                // System.out.println("Speed: "+predictedSpeed);
                bestScore = Double.MAX_VALUE;
                iterator = 0;
                lastupd = 0;
                return predictedSpeed;
            }
        }
    }

    private double evaluate(Vector2 coords, Vector2 speed) {
        ps.setStateVector(coords.x, coords.y, 0, 0);
        performMove(speed);
        return distanceSquared(ps.x, ps.y);
    }
}
