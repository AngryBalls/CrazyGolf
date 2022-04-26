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
     * ! does NOT calculate the actual distance
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
    public double gatDist(double x, double y){return Math.sqrt((x-xt)*(x-xt)+(y-yt)*(y-yt));}

    public void shoot(){
        distance = estDist(ps.x, ps.y);

        double xs = ps.x;
        double ys = ps.y;

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

        ps.setStateVector(xb,yb,0,0);
//        System.out.println("Distance:           "+distance);
//        System.out.println("The speed found:    ( "+vxb+" , "+vyb+" )");
        ps.printStateVector();

//        System.out.println("Distance = "+distance);
//        System.out.println("Best x: "+xb+", Best Y: "+yb);
//        System.out.println("Best Vx: "+vxb+", Best Vy: "+vyb);
    }

    public void swing(){
        distance = estDist(ps.x, ps.y);

//        System.out.println("Distance: "+distance);
//        System.out.println("Start");

        Vector2 startCoords = new Vector2((float)ps.x, (float)ps.y);

        //take target coords as direction + approximate values
        Vector2 targetSpeed = new Vector2((float) Math.max(Math.min(xt,5),-5),(float)Math.max(Math.min(yt,5),-5));
//        System.out.println("Target speed: "+targetSpeed);

        float speedStep = 0.1f;
        float speedDistribution = 3.0f/2;   // defines how far we can go from target speed

        Vector2 speedLowerPos = new Vector2(Math.max(targetSpeed.x-speedDistribution,-5), Math.max(targetSpeed.y - speedDistribution,-5));
        Vector2 speedUpperPos = new Vector2(Math.min(targetSpeed.x+speedDistribution,5), Math.min(targetSpeed.y + speedDistribution,5));

        Vector2 curSpeed = new Vector2(speedLowerPos.x,speedLowerPos.y);

        while(curSpeed.x<speedUpperPos.x){
            while (curSpeed.y<speedUpperPos.y) {

                if (curSpeed.x == 0 || curSpeed.y == 0)
                    continue;

                //System.out.println("curSpeed:"+curSpeed);

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
    }

    public void run(){
        long start = System.currentTimeMillis();
        System.out.println("Target:             ( "+xt+" , "+yt+" )");
        int i = 0;
        while(Math.sqrt(distance)>EPSILON){
            shoot();
            //swing();
            i++;
            System.out.println("ShotNr: "+i);
        }
        long end = System.currentTimeMillis();
        System.out.println("Ran in : "+(end-start)*0.01+" s");
    }

    public static void main(String[] args) {
        RuleBasedBot rbb= new RuleBasedBot(LevelInfo.exampleInput);
        System.out.println(rbb.EPSILON);
        rbb.run();
    }
}
