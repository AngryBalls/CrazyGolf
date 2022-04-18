package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.PhysicsSystem;
import com.badlogic.gdx.math.Vector2;

public class RuleBasedBot {
    public final PhysicsSystem ps = new PhysicsSystem();

    private final double EPSILON = 0.01;

    //Target
    private double xt;
    private double yt;

    //Best coords
    private double xb;
    private double yb;

    //Best speeds
    private double vxb;
    private double vyb;

    public RuleBasedBot(){
        xt = ps.getXt();
        yt = ps.getYt();
    }

    public RuleBasedBot(double xt, double yt){
        this.xt = xt;
        this.yt = yt;
    }

    public double distance = Double.MAX_VALUE;

    public double estDist(double x, double y){return Math.sqrt(Math.pow(x-xt,2)+Math.pow(y-yt,2));}

    public void shoot(){
        distance = estDist(ps.x, ps.y);

        double xs = ps.x;
        double ys = ps.y;

//        System.out.println("Distance: "+distance);
//        System.out.println("Start");
        float vx;
        float vy;


        for(int fvx = -400; fvx < 500; fvx++){
            for(int fvy = -400; fvy < 500; fvy++){

                if(fvx == 0 || fvy == 0)
                    continue;

                vx = (fvx*1.0f/100);        //Reduce speed time step by dividing
                vy = (fvy*1.0f/100);

                ps.setStateVector(xs,ys,0,0);
                ps.performMove(new Vector2(vx,vy));
                while(ps.iteration()==0){
                    ps.iteration();
                }
                //System.out.println("Stopped cuz:" + ps.iteration());
                //System.out.println("X: "+ps.x +", Y: "+ ps.y);

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
        System.out.println("Distance:           "+distance);
        System.out.println("The speed found:    ( "+vxb+" , "+vyb+" )");
        ps.printStateVector();

//        System.out.println("Distance = "+distance);
//        System.out.println("Best x: "+xb+", Best Y: "+yb);
//        System.out.println("Best Vx: "+vxb+", Best Vy: "+vyb);
    }
    public void run(){
        System.out.println("Target:             ( "+xt+" , "+yt+" )");
        int i = 0;
        while(distance>EPSILON){
            shoot();
            i++;
            System.out.println("ShotNr: "+i);
        }
    }

    public static void main(String[] args) {
        RuleBasedBot rbb= new RuleBasedBot(40,10);
        rbb.run();
    }
}
