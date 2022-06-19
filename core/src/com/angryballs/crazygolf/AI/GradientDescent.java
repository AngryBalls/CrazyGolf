package com.angryballs.crazygolf.AI;

import java.util.ArrayList;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.AI.Pathfinding.Path;
import com.badlogic.gdx.math.Vector2;

/**
 * Gradient Descent based bot
 * Update formula:
 * Vnew = Vcurrent - A * F'(Vcurrent)
 *
 * A - Learning rate
 * F'(Vcurrent) - Derivative of a fitness function
 *
 * - Fintess Function - distance from end position to target point
 * after applying speed from current position
 */
public class GradientDescent extends Bot {

    protected ArrayList<Double> fitness = new ArrayList<>();
    protected ArrayList<Double> speed = new ArrayList<>();

    // Specific GD variables
    private final double dv = 0.0001; // derivative step
    private final float A = 0.01f; // step size of descent
    private final int ITERATION_LIMIT = 269;
    private final int UPDATE_LIMIT = 100;
    private final double DELTA_DISTANCE = 0.00001;
    private final double RADIUS;
    private boolean stop = false;

    // Best data
    private double bestScore = Double.MAX_VALUE;
    private Vector2 bestSpeed = new Vector2();

    // Stat info
    private int lastupd = 0;
    private int iterator = 0;

    public GradientDescent(LevelInfo info, Path optimalPath) {
        super(info, optimalPath);
        RADIUS = ps.getRadius();
    }

    @Override
    protected Vector2 computeOptimalMove(double x, double y) {
        return predict(x, y);
    }

    private double derivative(Vector2 coords, Vector2 speed, boolean isX, double curScore) {
        double distV;
        double distVnew;
        int sign = 1;

        // Calculate the f(V) = dist(point, target)
        distV = curScore;

        // Calculate the f(V+dV) = dist(new point, target)
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
        distVnew = evaluate(coords, newSpeed);

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
    private Vector2 predict(double x, double y) {
        Vector2 coords = new Vector2((float) x, (float) y);

        // Starting Speeds
        Vector2 curSpeed = new Vector2((float) Math.max(Math.min(this.xt - x, 5), -5),
                (float) Math.max(Math.min(this.yt - y, 5), -5));

        double curScore = evaluate(coords, curSpeed);
        bestSpeed = new Vector2(curSpeed.x, curSpeed.y);

        while (true) {
            double dvx = derivative(coords, curSpeed, true, curScore);
            double dvy = derivative(coords, curSpeed, false, curScore);

            curSpeed.x = curSpeed.x - A * (float) dvx;
            curSpeed.y = curSpeed.y - A * (float) dvy;

            curSpeed.x = Math.max(Math.min(curSpeed.x, 5), -5);
            curSpeed.y = Math.max(Math.min(curSpeed.y, 5), -5);

            curScore = evaluate(coords, curSpeed);

            double difference = bestScore - curScore;
            if (difference > DELTA_DISTANCE) {
                lastupd = iterator;
                bestSpeed = new Vector2(curSpeed.x, curSpeed.y);
            }

            if (curScore < bestScore) {
                bestScore = curScore;
                bestSpeed = new Vector2(curSpeed.x, curSpeed.y);
            }
            if (iterator - lastupd > UPDATE_LIMIT)
                stop = true;

            iterator++;

            if (bestScore <= RADIUS || iterator > ITERATION_LIMIT || stop) {
                System.out.println("\nGradient Descent");
                System.out.println("================");
                System.out.println("Iterations: " + iterator);
                System.out.println("Distance: " + bestScore);
                System.out.println("Speed: " + bestSpeed);

                if (iterator > ITERATION_LIMIT) {
                    System.out.println("ITERATION_LIMIT");
                } else if (bestScore <= RADIUS) {
                    System.out.println("CurScore <= R*R");
                } else {
                    System.out.println("UPDATE_LIMIT");
                }

                bestScore = Double.MAX_VALUE;
                iterator = 0;
                lastupd = 0;
                stop = false;
                return bestSpeed;
            }
        }
    }

    private double evaluate(Vector2 coords, Vector2 speed) {
        ps.setStateVector(coords.x, coords.y, 0, 0);
        int result = performMove(speed);
        if (result == 3)
            return 0;
        return distanceSquared(ps.x, ps.y);
    }
}
