package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Physics.*;
import com.badlogic.gdx.math.Vector2;

public abstract class Bot {
    protected final PhysicsEngine ps;
    protected final double EPSILON;

    // Target
    protected double xt;
    protected double yt;

    // Best coords
    protected double xb;
    protected double yb;

    // Best speeds
    protected double vxb;
    protected double vyb;

    protected double distance = Double.MAX_VALUE;

    public Bot(LevelInfo info) {
        this.ps = new EulersPhysicsEngine(info);
        this.EPSILON = ps.getRadius();
        this.xt = ps.getXt();
        this.yt = ps.getYt();
    }

    public abstract Vector2 getSpeedVector(double x, double y);

    /**
     * Estimates the distance between point ( x,y ) and target point
     * ! does NOT calculate the actual distance between 2 points
     *
     * @param x x coordinate of the point ( x,y )
     * @param y y coordinate of the point ( x,y )
     * @return estimated distance
     */
    public double estDist(double x, double y) {
        return (x - xt) * (x - xt) + (y - yt) * (y - yt);
    }

    /**
     * Calculates the distance between point ( x,y ) and target point
     *
     * @param x x coordinate of the point ( x,y )
     * @param y y coordinate of the point ( x,y )
     * @return estimated distance
     */
    public double getDist(double x, double y) {
        return Math.sqrt((x - xt) * (x - xt) + (y - yt) * (y - yt));
    }

    public void run(Bot bot) {
        long start = System.currentTimeMillis();
        System.out.println("Target:             ( " + xt + " , " + yt + " )");
        Vector2 coords = new Vector2((float) ps.x, (float) ps.y);
        Vector2 speeds = new Vector2();
        int i = 0;

        while (Math.abs(Math.sqrt(bot.distance)) > EPSILON) {
            speeds = bot.getSpeedVector(coords.x, coords.y);
            ps.setStateVector(coords.x, coords.y, 0, 0);
            System.out.println("The state vector: " + coords + " " + speeds);
            ps.performMove(speeds);
            while (ps.iterate() == 0) {
                ps.iterate();
            }
            coords.x = (float) ps.x;
            coords.y = (float) ps.y;
            bot.distance = estDist(coords.x, coords.y);
            i++;
            System.out.println("ShotNr: " + i);
        }

        System.out.println("The state vector: " + coords + " " + speeds);
        long end = System.currentTimeMillis();
        System.out.println("Ran in : " + (end - start) * 0.001 + " s");
    }
}
