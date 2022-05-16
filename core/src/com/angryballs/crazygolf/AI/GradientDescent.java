package com.angryballs.crazygolf.AI;

import java.util.List;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

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

    protected  ArrayList<Double> fitness = new ArrayList<>();
    protected  ArrayList<Double> speed = new ArrayList<>();

    // Specific GD variables
    private final double dv = 0.1; // derivative step
    private  float A = 0.1f; // step size of descent
    private final double ZERO = 0.001;

    public GradientDescent(LevelInfo info, List<TreeModel> trees) {
        super(info, trees);
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
//        if(isX)
//            System.out.println("initial:     "+ps.x+", "+ps.y);
        distV = distance(ps.x, ps.y);

        // Calculate the f(V+dV) = dist(new point, target)
        ps.setStateVector(coords.x, coords.y, 0, 0);
//        if(isX)
//            System.out.println("Previous:    "+speed);
        Vector2 newSpeed = Vector2.Zero;


         if(isX){
             if(speed.x > 4.5) {
                 newSpeed = new Vector2((float) (speed.x - dv), speed.y);
                 sign = -1;
             }else
                 newSpeed = new Vector2((float) (speed.x + dv), speed.y);
        }else{
             if (speed.y>4.5) {
                 newSpeed = new Vector2(speed.x, (float) (speed.y - dv));
                 sign = -1;
             }else
                 newSpeed = new Vector2(speed.x, (float) (speed.y + dv));
        }
        performMove(newSpeed);
//        if(isX)
//            System.out.println("final:       "+ps.x+", "+ps.y);
        distVnew = distance(ps.x, ps.y);


        // Calculate the derivative
//        if(isX){
//        System.out.println("Predicted:   "+newSpeed);
//        System.out.println("distVnew: " +distVnew+", dist: "+distV);
//        System.out.println("ditVnew-distV "+(distVnew - distV));
          //System.out.println("f': "+(distVnew - distV) / dv);
//        System.out.println("coords = (" + ps.x+", "+ps.y+")");
//        }
        return sign*(distVnew - distV) / dv;
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
//        Vector2 curSpeed = new Vector2((float) Math.max(Math.min(this.xt - x, 5), -5),
//                (float) Math.max(Math.min(this.yt - y, 5), -5));
        Vector2 curSpeed = new Vector2(3,2);
        Vector2 predictedSpeed = new Vector2();

        double curScore = evaluate(coords,curSpeed);
        double previousScore = curScore;
        while(true){
            double dvx = derivative(coords, curSpeed, true);
            double dvy = derivative(coords, curSpeed, false);
            //System.out.println("dVx: "+dvx+", dVy: "+dvy);
            predictedSpeed.x = curSpeed.x - A*(float)dvx;
            predictedSpeed.y = curSpeed.y - A*(float)dvy;
            predictedSpeed.x = Math.max(Math.min(predictedSpeed.x, 5), -5);
            predictedSpeed.y = Math.max(Math.min(predictedSpeed.y, 5), -5);
            curScore = evaluate(coords,predictedSpeed);
            System.out.println("Speed:      "+predictedSpeed+", Distance: "+Math.sqrt(curScore));

            curSpeed = predictedSpeed;
            previousScore = curScore;
            fitness.add(curScore);
            speed.add((double)(curSpeed.y+curSpeed.x));

            if (distance(ps.x,ps.y)<=0.15){
                System.out.println(speed);
                System.out.println(fitness);
                System.out.println("X: "+ps.x+", Y: "+ps.y);
                return predictedSpeed;
            }
            //break;
        }
        //return Vector2.Zero;
    }
    private double evaluate(Vector2 coords, Vector2 speed){
        ps.setStateVector(coords.x, coords.y, 0, 0);
        performMove(speed);
        return distanceSquared(ps.x, ps.y);
    }
}
