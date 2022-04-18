package com.angryballs.crazygolf;

import com.badlogic.gdx.math.Vector2;

public class RuleBasedBot {
    public final PhysicsSystem ps = new PhysicsSystem();

    public double xt;
    public double yt;

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

        for(int vx = -4; vx < 5; vx+= 1){
            for(int vy = -4; vy < 5; vy+= 1){

                ps.setStateVector(0,0,0,0);
                ps.performMove(new Vector2(vx,vy));
                while(ps.iteration()==0){
                    ps.iteration();
                }
                System.out.println("Stopped cuz:" + ps.iteration());
                System.out.println("X: "+ps.x +", Y: "+ ps.y);

                double locDist = estDist(ps.x, ps.y);
                if(locDist<distance)
                    distance = locDist;
                System.out.println(distance);
            }
        }
        System.out.println("Distance = "+distance);
    }

    public static void main(String[] args) {
        RuleBasedBot rbb= new RuleBasedBot();
        rbb.shoot();
    }
}
