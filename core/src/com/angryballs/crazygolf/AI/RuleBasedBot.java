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

        for(int vx = -4; vx < 5; vx+= 2.5){
            for(int vy = -4; vy < 5; vy+= 0.5){

                ps.setStateVector(0,0,0,0);
                ps.performMove(new Vector2(vx,vy));
                while(ps.iteration()==0){
                    ps.iteration();
                    //ps.printStateVector();
                }
                System.out.println("Stopped cuz:" + ps.iteration());
                System.out.println("X: "+ps.x +", Y: "+ ps.y);

                double locDist = estDist(ps.x, ps.y);
                if(locDist<distance) {
                    distance = locDist;
                    xb = ps.x;
                    yb = ps.y;
                    vxb = vx;
                    vyb = vy;
                }
                System.out.println(distance);
            }
        }
        System.out.println("Distance = "+distance);
        System.out.println("Best x: "+xb+", Best Y: "+yb);
        System.out.println("Best Vx: "+vxb+", Best Vy: "+vyb);
    }

    public static void main(String[] args) {
        RuleBasedBot rbb= new RuleBasedBot();
        rbb.shoot();
    }
}
