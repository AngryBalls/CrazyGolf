package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

/**
 * Gradient Descent based bot
 * Update formula:
 * Vnew = Vcurrent - A * F'(Vcurrent)
 * ^ ^
 * learning rate |
 * Derivative of a fitness function
 *
 * Fintess Function - distance from end position to target point
 * after applying speed from current position
 */
public class GradientDescent extends Bot {

    // Specific GD variables
    private final double dv = 0.00001; // derivative step
    private final double A = 0.5; // step size of descent

    public GradientDescent(LevelInfo info) {
        super(info);
    }

    @Override
    public Vector2 getSpeedVector(double x, double y) {
        return predict(x, y);
    }

    public double derivative(Vector2 coords, Vector2 speed, boolean isX) {
        double distV;
        double distVnew;

        // Calculate the f(V) = dist(point, target)
        ps.setStateVector(coords.x, coords.y, 0, 0);
        ps.performMove(speed);
        while (ps.iterate() == 0) {
            ps.iterate();
        }
        distV = getDist(ps.x, ps.y);

        // Calculate the f(V+dV) = dist(new point, target)
        ps.setStateVector(coords.x, coords.y, 0, 0);
        Vector2 newSpeed;
        if (isX)
            newSpeed = new Vector2((float) (speed.x + dv), speed.y);
        else
            newSpeed = new Vector2(speed.x, (float) (speed.y + dv));
        ps.performMove(newSpeed);
        while (ps.iterate() == 0) {
            ps.iterate();
        }
        distVnew = getDist(ps.x, ps.y);

        // Calculate the derivative
        return (distVnew - distV) / dv;
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
        Vector2 targetSpeed = new Vector2((float) Math.max(Math.min(this.xt - x, 5), -5),
                (float) Math.max(Math.min(this.yt - y, 5), -5));

        Vector2 predictedSpeed = new Vector2();
        predictedSpeed.x = targetSpeed.x - (float) (A * derivative(coords, targetSpeed, true));
        predictedSpeed.y = targetSpeed.y - (float) (A * derivative(coords, targetSpeed, false));
        predictedSpeed.x = Math.max(Math.min(predictedSpeed.x, 5), -5);
        predictedSpeed.y = Math.max(Math.min(predictedSpeed.y, 5), -5);

        if (predictedSpeed.x == predictedSpeed.y && predictedSpeed.x == 0) {
            predictedSpeed.x = targetSpeed.x;
            predictedSpeed.y = targetSpeed.y;
        }
        return predictedSpeed;
    }

}
