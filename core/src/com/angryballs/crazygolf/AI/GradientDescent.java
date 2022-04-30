package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.PhysicsSystem;
import com.badlogic.gdx.math.Vector2;

public class GradientDescent {
    public  final PhysicsSystem ps;
    private final double EPSILON;

    private final double dv = 0.01;        //derivative step
    private final double A = 0.5;          //step size of descent

    //Target
    private double xt;
    private double yt;

    //Best coords
    private double xb;
    private double yb;

    //Best speeds
    private double vxb;
    private double vyb;

    public GradientDescent(double xt, double yt){
        this.ps = new PhysicsSystem();
        this.EPSILON=0.1;
        this.xt = xt;
        this.yt = yt;
    }
    public GradientDescent(LevelInfo info){
        this.ps = new PhysicsSystem(info);
        this.EPSILON = ps.getRadius();
        this.xt = ps.getXt();
        this.yt = ps.getYt();
    }

    public double distance = Double.MAX_VALUE;


    /**
     * Estimates the distance between point ( x,y ) and target point
     * ! does NOT calculate the actual distance between 2 points
     * @param x     x coordinate of the point ( x,y )
     * @param y     y coordinate of the point ( x,y )
     * @return      estimated distance
     */
    public double estDist(double x, double y){return (x-xt)*(x-xt)+(y-yt)*(y-yt);}

    /**
     * Calculates the distance between point ( x,y ) and target point
     * @param x     x coordinate of the point ( x,y )
     * @param y     y coordinate of the point ( x,y )
     * @return      estimated distance
     */
    public double getDist(double x, double y){return Math.sqrt((x-xt)*(x-xt)+(y-yt)*(y-yt));}

    public double derivative(Vector2 coords, Vector2 speed, boolean isX){
        double distV;
        double distVnew;

        // Calculate the f(V) = dist(point, target)
        ps.setStateVector(coords.x, coords.y, 0,0);
        ps.performMove(speed);
        while(ps.iteration()==0){
            ps.iteration();
        }
        distV = getDist(ps.x, ps.y);

        // Calculate the f(V+dV) = dist(new point, target)
        ps.setStateVector(coords.x, coords.y, 0,0);
        Vector2 newSpeed;
        if (isX)
            newSpeed = new Vector2((float)(speed.x+dv), speed.y);
        else
            newSpeed = new Vector2(speed.x,(float)(speed.y+dv));
        ps.performMove(newSpeed);
        while(ps.iteration()==0){
            ps.iteration();
        }
        distVnew = getDist(ps.x, ps.y);

        // Calculate the derivative
        return (distVnew-distV)/dv;
    }
    /**
     * Performs a prediction of the next (vx,vy) vector
     * @param x     starting X coordinate
     * @param y     starting Y coordinate
     * @return      vector( vx, vy )
     */
    public Vector2 predict(double x, double y){
        //System.out.println("Target: ( "+xt+", "+yt+" )");
        Vector2 coords = new Vector2((float)x, (float)y);

        //take target coords as direction + approximate values
        Vector2 targetSpeed = new Vector2((float) Math.max(Math.min(xt-x,5),-5),(float)Math.max(Math.min(yt-y,5),-5));
        //System.out.println("Target speed: "+targetSpeed);

        Vector2 predictedSpeed = new Vector2();

        predictedSpeed.x = targetSpeed.x - (float)(A*derivative(coords, targetSpeed, true));
        predictedSpeed.y = targetSpeed.y - (float)(A*derivative(coords, targetSpeed, false));

        predictedSpeed.x = Math.max(Math.min(predictedSpeed.x,5),-5);
        predictedSpeed.y = Math.max(Math.min(predictedSpeed.y,5),-5);
        System.out.println("Predicted speed: "+predictedSpeed);

        return predictedSpeed;
    }
    public void run(){
        long start = System.currentTimeMillis();
        System.out.println("Target:             ( "+xt+" , "+yt+" )");
        Vector2 coords = new Vector2((float) ps.x, (float) ps.y);
        Vector2 speeds = new Vector2();
        int i = 0;

        while(Math.abs(Math.sqrt(distance))>EPSILON){

            //speeds = shoot(coords.x, coords.y);
            speeds = predict(coords.x, coords.y);

            ps.setStateVector(coords.x, coords.y, 0, 0);
            ps.performMove(speeds);
            while(ps.iteration()==0){
                ps.iteration();
            }
            coords.x = (float) ps.x;
            coords.y = (float) ps.y;
            i++;
            System.out.println("ShotNr: "+i);
            System.out.println("The state vector: "+coords+" "+speeds);
            distance = estDist(coords.x,coords.y);
        }

        System.out.println("The state vector: "+coords+" "+speeds);
        long end = System.currentTimeMillis();
        System.out.println("Ran in : "+(end-start)*0.01+" s");

    }

    public static void main(String[] args) {
        GradientDescent gd = new GradientDescent(LevelInfo.exampleInput);
        gd.run();
    }

}
