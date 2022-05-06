package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

/**
 * Newton-Raphson based bot
 *  Update formula:
 *  Vnew = Vcurrent - A * F(Vcurrent)/F'(Vcurrent)
 *                    ^      ^             ^
 *           learning rate   |             |
 *                  fitness function       |
 *                            erivative of the fitness function
 *
 *   Fintess Function - distance from end position to target point
 *   after applying speed from current position
 *
 *   Learning rate by default is 1
 *
 */
public class NewtonRaphson extends Bot{

    //Specific NR variables
    private final double dv = 0.00001;        //derivative step
    private final double A = 1;          //step size of descent

    public NewtonRaphson(double xt, double yt){
        super(xt,yt);
    }
    public NewtonRaphson(LevelInfo info){
        super(info);
    }

    @Override
    public Vector2 getSpeedVector(double x, double y) {
        return predict(x,y);
    }

    public double derivative(Vector2 coords, Vector2 speed, boolean isX){
        //Calculate f(V) = dist(point, target)
        double distV = fitnessFun(coords, speed);

        // Calculate the f(V+dV) = dist(new point, target)
        double distVnew;
        Vector2 newSpeed;
        if (isX)
            newSpeed = new Vector2((float)(speed.x+dv), speed.y);
        else
            newSpeed = new Vector2(speed.x,(float)(speed.y+dv));
        distVnew = fitnessFun(coords,newSpeed);

        // Calculate the derivative
        return (distVnew-distV)/dv;
    }
    public double fitnessFun(Vector2 coords, Vector2 speed){
        double distV;

        // Calculate the f(V) = dist(point, target)
        ps.setStateVector(coords.x, coords.y, 0,0);
        ps.performMove(speed);
        while(ps.iteration()==0){
            ps.iteration();
        }
        distV = getDist(ps.x, ps.y);

        return distV;
    }

    /**
     * Performs a prediction of the next (vx,vy) vector
     * @param x     starting X coordinate
     * @param y     starting Y coordinate
     * @return      vector( vx, vy )
     */
    public Vector2 predict(double x, double y){
        Vector2 coords = new Vector2((float)x, (float)y);

        //take target coords as direction + approximate values
        Vector2 targetSpeed = new Vector2((float) Math.max(Math.min(this.xt-x,5),-5),(float)Math.max(Math.min(this.yt-y,5),-5));

        Vector2 predictedSpeed = new Vector2();
        predictedSpeed.x = targetSpeed.x - (float)(A*fitnessFun(coords,targetSpeed)/derivative(coords, targetSpeed, true));
        predictedSpeed.y = targetSpeed.y - (float)(A*fitnessFun(coords,targetSpeed)/derivative(coords, targetSpeed, false));
        predictedSpeed.x = Math.max(Math.min(predictedSpeed.x,5),-5);
        predictedSpeed.y = Math.max(Math.min(predictedSpeed.y,5),-5);

        if(predictedSpeed.x==predictedSpeed.y &&predictedSpeed.x == 0){
            predictedSpeed.x = targetSpeed.x;
            predictedSpeed.y = targetSpeed.y;
        }
        return predictedSpeed;
    }
}
