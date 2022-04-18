package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.PhysicsSystem;
import com.badlogic.gdx.math.Vector2;

public class RuleBasedBot {
    public final PhysicsSystem ps = new PhysicsSystem();

    //Target
    public double xt;
    public double yt;

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

    public double distance = Double.MAX_VALUE;

    public double estDist(double x, double y){return Math.sqrt(Math.pow(x-xt,2)+Math.pow(y-yt,2));}

    public void shoot(){
        distance = estDist(ps.x, ps.y);
        System.out.println("Distance: "+distance);
        System.out.println("Start");
        float vx;
        float vy;

        for(int fvx = -400; fvx < 500; fvx++){
            for(int fvy = -40; fvy < 500; fvy++){

                vx = (fvx*1.0f)/100;        //Reduce speed time step
                vy = (fvy*1.0f/100);

                ps.setStateVector(0,0,0,0);
                ps.performMove(new Vector2(vx,vy));
                while(ps.iteration()==0){
                    ps.iteration();
                    //ps.printStateVector();
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
                    System.out.println("Updated");
                    System.out.println("Best Vx: "+vxb+", Best Vy: "+vyb);
                    System.out.println("New dist: "+locDist);
                }

            }
        }
        System.out.println("Distance = "+distance);
        System.out.println("Best x: "+xb+", Best Y: "+yb);
        System.out.println("Best Vx: "+vxb+", Best Vy: "+vyb);
        System.out.println("Target: ( "+xt+" , "+yt+" )");
    }

    public static void main(String[] args) {
        RuleBasedBot rbb= new RuleBasedBot();
        rbb.shoot();
    }
}
