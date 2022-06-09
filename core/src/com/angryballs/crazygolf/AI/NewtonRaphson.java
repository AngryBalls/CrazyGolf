package com.angryballs.crazygolf.AI;

import java.util.List;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.angryballs.crazygolf.Models.WallModel;
import com.badlogic.gdx.math.Vector2;

/**
 * Newton-Raphson based bot
 * Update formula:
 * Vnew = Vcurrent - A * F(Vcurrent)/F'(Vcurrent)
 *
 * A            - learning rate
 * F(Vcurrent)  - fitness function
 * F'(Vcurrent) - derivative of the fitness function
 *
 * Fintess Function - distance from end position to target point
 * after applying speed from current position
 *
 * Learning rate by default is 1
 *
 */
public class NewtonRaphson extends Bot {

    // Specific NR variables
    private final double dv = 0.01; // derivative step
    private final float A = 0.01f; // step size of descent
    private final int ITERATION_LIMIT = 569;
    private final int UPDATE_LIMIT = 300;
    private final double DELTA_DISTANCE = 0.00001;
    private final double RADIUS;
    private boolean stop = false;

    // Best data
    private double bestScore = Double.MAX_VALUE;
    private Vector2 bestSpeed = new Vector2();

    // Stat info
    private int lastupd = 0;
    private int iterator = 0;

    public NewtonRaphson(LevelInfo info, List<TreeModel> trees) {
        super(info, trees);
        RADIUS = ps.getRadius();
    }

    @Override
    public Vector2 computeOptimalMove(double x, double y) {
        return predict(x, y);
    }

    public double derivative(Vector2 coords, Vector2 speed, boolean isX) {
        // Calculate f(V) = dist(point, target)
        double distV = fitnessFun(coords, speed);

        // Calculate the f(V+dV) = dist(new point, target)
        double distVnew;
        Vector2 newSpeed;
        double sign = 1;
        if (isX) {
            if (speed.x > 0) {
                newSpeed = new Vector2((float) (speed.x - dv), speed.y);
                sign = -1;
            } else
                newSpeed = new Vector2((float) (speed.x + dv), speed.y);
        }else {
            if (speed.y > 0) {
                newSpeed = new Vector2(speed.x, (float) (speed.y - dv));
                sign = -1;
            } else
                newSpeed = new Vector2(speed.x, (float) (speed.y + dv));
        }
        distVnew = fitnessFun(coords, newSpeed);

        // Calculate the derivative
        return sign*(distVnew - distV) / dv;
    }

    public double fitnessFun(Vector2 coords, Vector2 speed) {
        double distV;

        // Calculate the f(V) = dist(point, target)
        ps.setStateVector(coords.x, coords.y, 0, 0);
        performMove(speed);
        distV = distance(ps.x, ps.y);

        return distV;
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
        Vector2 curSpeed = new Vector2((float) Math.max(Math.min(xt - x, 5), -5),
                (float) Math.max(Math.min(yt - y, 5), -5));

        bestSpeed = new Vector2(curSpeed.x, curSpeed.y);
        double curScore;

        while(true){
            curSpeed.x = curSpeed.x
                    - A*(float) (fitnessFun(coords, curSpeed) / derivative(coords, curSpeed, true));
            curSpeed.y = curSpeed.y
                    - A*(float) (fitnessFun(coords, curSpeed) / derivative(coords, curSpeed, false));
            curSpeed.x = Math.max(Math.min(curSpeed.x, 5), -5);
            curSpeed.y = Math.max(Math.min(curSpeed.y, 5), -5);

            //System.out.println(curSpeed);
            curScore = fitnessFun(coords, curSpeed);

            double difference = bestScore - curScore;
            if (difference > DELTA_DISTANCE){
                lastupd = iterator;
                bestSpeed = new Vector2(curSpeed.x, curSpeed.y);
            }
            if(curScore < bestScore){
                bestScore = curScore;
                bestSpeed = new Vector2(curSpeed.x, curSpeed.y);
            }
            if(iterator-lastupd>UPDATE_LIMIT)
                stop = true;
            iterator++;

            //System.out.println("Best Score: "+bestScore);

            if (bestScore <= RADIUS || iterator > ITERATION_LIMIT || stop) {

                System.out.println("Iterations: "+iterator);
                System.out.println("Distance: "+bestScore);
                System.out.println("Speed: "+bestSpeed);

                if(iterator > ITERATION_LIMIT){
                    System.out.println("ITERATION_LIMIT");
                }else if(bestScore <= RADIUS){
                    System.out.println("CurScore <= R*R");
                }else{
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
}
