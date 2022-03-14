package com.angryballs.crazygolf;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.xpath.XPathConstants;

public class BallLocationUpdateLoop {
    public double Xcoordinate;
    public double Ycoordinate;
    public double heightCoordinate;
    public double Xaccelleration;
    public double Yaccelleration;
    public double heightAccelleration;
    
    public BallLocationUpdateLoop(){
        Xcoordinate = Ball.Xlocation;
        Ycoordinate = Ball.Ylocation;
        heightCoordinate = Ball.Zlocation;
        Xaccelleration = 0.0;
        Yaccelleration = 0.0;
        heightAccelleration = 0.0;
    }
    public BallLocationUpdateLoop(double x, double y){
        Xcoordinate = Ball.Xlocation;
        Ycoordinate = Ball.Ylocation;
        heightCoordinate = Ball.Zlocation;
        Xaccelleration = x;
        Yaccelleration = y;
        heightAccelleration = 0.0;
    }
    public BallLocationUpdateLoop(double x, double y, double height){
        Xcoordinate = Ball.Xlocation;
        Ycoordinate = Ball.Ylocation;
        heightCoordinate = Ball.Zlocation;
        Xaccelleration = x;
        Yaccelleration = y;
        heightAccelleration = height;
    }

    public void main(String[] args){
        
        boolean continueLoop = true;
        double Xvelocity = 0;
        double Yvelocity = 0;
        double heightVelocity = 0;
        while(continueLoop){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Xvelocity += Xaccelleration * 0.01;
            Yvelocity += Yaccelleration * 0.01;
            heightVelocity += heightAccelleration * 0.01;
            Xcoordinate += Xvelocity * 0.01;
            Yvelocity += Yvelocity * 0.01;
            heightCoordinate += heightVelocity * 0.01;
            Ball.Xlocation = Xcoordinate;
            Ball.Ylocation = Ycoordinate;
            Ball.Zlocation = heightCoordinate;
        }
    }
}
