package com.angryballs.crazygolf;

public class Ball {
    public double Xlocation;
    public double Ylocation;
    public double Zlocation;

    public Ball(){
        Xlocation = 0;
        Ylocation = 0;
        Zlocation = 0;
    }
    public Ball(double x, double y, double z){
        Xlocation = x;
        Ylocation = y;
        Zlocation = z;
    }
}
