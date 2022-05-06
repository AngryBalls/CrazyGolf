package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.PhysicsSystem;
import com.badlogic.gdx.math.Vector2;

public abstract class Bot {
    public final PhysicsSystem ps;
    public final double EPSILON;

    //Target
    public double xt;
    public double yt;

    //Best coords
    public double xb;
    public double yb;

    //Best speeds
    public double vxb;
    public double vyb;

    public double distance = Double.MAX_VALUE;

    public Bot(double xt, double yt){
        this.ps = new PhysicsSystem();
        this.EPSILON=0.1;
        this.xt = xt;
        this.yt = yt;
    }
    public Bot(LevelInfo info){
        this.ps = new PhysicsSystem(info);
        this.EPSILON = ps.getRadius();
        this.xt = ps.getXt();
        this.yt = ps.getYt();
    }

    public abstract Vector2 getSpeedVector(double x,double y);

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

    public void run(Bot bot){
        long start = System.currentTimeMillis();
        System.out.println("Target:             ( "+xt+" , "+yt+" )");
        Vector2 coords = new Vector2((float) ps.x, (float) ps.y);
        Vector2 speeds = new Vector2();
        int i = 0;
        while(Math.abs(Math.sqrt(bot.distance))>EPSILON){

            speeds = bot.getSpeedVector(coords.x,coords.y);
            ps.setStateVector(coords.x, coords.y, 0, 0);
            System.out.println("The state vector: "+coords+" "+speeds);
            ps.performMove(speeds);
            while(ps.iteration()==0){
                ps.iteration();
            }
            coords.x = (float) ps.x;
            coords.y = (float) ps.y;
            bot.distance = estDist(coords.x,coords.y);
            i++;
            System.out.println("ShotNr: "+i);
        }

        System.out.println("The state vector: "+coords+" "+speeds);
        long end = System.currentTimeMillis();
        System.out.println("Ran in : "+(end-start)*0.001+" s");

    }
}
