package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.PhysicsSystem;
import com.badlogic.gdx.math.Vector2;

public class RuleBasedBot {
    public final PhysicsSystem ps;
    private final double EPSILON;

    //Target
    private double xt;
    private double yt;

    //Best coords
    private double xb;
    private double yb;

    //Best speeds
    private double vxb;
    private double vyb;

    public RuleBasedBot(double xt, double yt){
        this.ps = new PhysicsSystem();
        this.EPSILON=0.1;
        this.xt = xt;
        this.yt = yt;
    }
    public RuleBasedBot(LevelInfo info){
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

    /**
     * Estimates and selects the best shot among the neighbourhood of the starting point
     * @param x     staring X coordinate
     * @param y     starting Y coordinate
     * @return      vector( vx, vy )
     */
    public Vector2 shoot(double x, double y){
        distance = estDist(x, y);

        double xs = x;
        double ys = y;

//        System.out.println("Distance: "+distance);
//        System.out.println("Start");
        float vx;
        float vy;


        for(int fvx =  -50; fvx < 50; fvx++){
            for(int fvy = -50; fvy < 50; fvy++){

                if(fvx == 0 || fvy == 0)
                    continue;

                vx = (fvx*1.0f/10);        //Reduce speed time step by dividing
                vy = (fvy*1.0f/10);

                ps.setStateVector(xs,ys,0,0);
                ps.performMove(new Vector2(vx,vy));
                while(ps.iteration()==0){
                    ps.iteration();
                }
//                System.out.println("Stopped cuz:" + ps.iteration());
//                System.out.println("X: "+ps.x +", Y: "+ ps.y);

                double locDist = estDist(ps.x, ps.y);
                if(locDist<distance) {
                    distance = locDist;
                    xb = ps.x;
                    yb = ps.y;
                    vxb = vx;
                    vyb = vy;
//                    System.out.println("Updated");
//                    System.out.println("Best Vx: "+vxb+", Best Vy: "+vyb);
//                    System.out.println("New dist: "+locDist);
                }
            }
        }

//        System.out.println("Distance:           "+distance);
//        System.out.println("The speed found:    ( "+vxb+" , "+vyb+" )");
//        System.out.println("Distance = "+distance);
//        System.out.println("Best x: "+xb+", Best Y: "+yb);
//        System.out.println("Best Vx: "+vxb+", Best Vy: "+vyb);

        return new Vector2((float)vxb,(float)vyb);
    }

    /**
     * Performs shoot directly towards the hole
     * @param x     starting X coordinate
     * @param y     starting Y coordinate
     * @return      vector( vx, vy )
     */
    public Vector2 swing(double x, double y){
        distance = estDist(ps.x, ps.y);

//        System.out.println("Distance: "+distance);
//        System.out.println("Start");

        Vector2 startCoords = new Vector2((float)x, (float)y);



        //take target coords as direction + approximate values
        Vector2 targetSpeed = new Vector2((float) Math.max(Math.min(xt-x,5),-5),(float)Math.max(Math.min(yt-y,5),-5));
//        System.out.println("Target speed: "+targetSpeed);

        float speedStep = 0.5f;
        float speedDistribution = 3.0f/2;   // defines how far we can go from target speed

        Vector2 speedLowerPos = new Vector2(Math.max(targetSpeed.x-speedDistribution,-5), Math.max(targetSpeed.y - speedDistribution,-5));
        Vector2 speedUpperPos = new Vector2(Math.min(targetSpeed.x+speedDistribution,5), Math.min(targetSpeed.y + speedDistribution,5));

        Vector2 curSpeed = new Vector2(speedLowerPos.x,speedLowerPos.y);

        while(curSpeed.x<speedUpperPos.x){
            while (curSpeed.y<speedUpperPos.y) {

                if (curSpeed.x == 0 || curSpeed.y == 0)
                    continue;

//                System.out.println("curSpeed:"+curSpeed);

                ps.setStateVector(startCoords.x, startCoords.y, 0, 0);
                ps.performMove(new Vector2(curSpeed.x, curSpeed.y));
                while (ps.iteration() == 0) {
                    ps.iteration();
                }

                double locDist = estDist(ps.x, ps.y);
                //System.out.println(locDist);
                if (locDist < distance) {
                    distance = locDist;
                    xb = ps.x;
                    yb = ps.y;
                    vxb = curSpeed.x;
                    vyb = curSpeed.y;
//                    System.out.println("Updated");
//                    System.out.println("Best Vx: " + vxb + ", Best Vy: " + vyb);
//                    System.out.println("New dist: " + locDist);
                }

                curSpeed.y += speedStep;
            }
            curSpeed.x += speedStep;
            curSpeed.y = speedLowerPos.y;
        }
//        System.out.println("Distance = "+Math.sqrt(distance));
//        System.out.println("Best x: "+xb+", Best Y: "+yb);
//        System.out.println("Best Vx: "+vxb+", Best Vy: "+vyb);
        return new Vector2((float) vxb, (float) vyb);
    }

    public void run(){
        long start = System.currentTimeMillis();
        System.out.println("Target:             ( "+xt+" , "+yt+" )");
        Vector2 coords = new Vector2((float) ps.x, (float) ps.y);
        Vector2 speeds = new Vector2();
        int i = 0;

        while(Math.abs(Math.sqrt(distance))>EPSILON){

            //speeds = shoot(coords.x, coords.y);
            speeds = swing(coords.x, coords.y);

            ps.setStateVector(coords.x, coords.y, 0, 0);
            System.out.println("The state vector: "+coords+" "+speeds);
            ps.performMove(speeds);
            while(ps.iteration()==0){
                ps.iteration();
            }
            coords.x = (float) ps.x;
            coords.y = (float) ps.y;
            i++;
            System.out.println("ShotNr: "+i);
        }

        System.out.println("The state vector: "+coords+" "+speeds);
        long end = System.currentTimeMillis();
        System.out.println("Ran in : "+(end-start)*0.01+" s");

    }

    public static void main(String[] args) {
        RuleBasedBot rbb= new RuleBasedBot(LevelInfo.exampleInput);
        System.out.println(rbb.EPSILON);
        rbb.run();
        //rbb.swing(0,0);
    }
}
