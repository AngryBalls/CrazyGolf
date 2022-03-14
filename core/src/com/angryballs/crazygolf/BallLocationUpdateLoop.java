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
    public Ball bol;
    
    public BallLocationUpdateLoop(Ball bol){
        this(bol,0,0,0);
    }
    public BallLocationUpdateLoop(Ball bol, double x, double y){
        this(bol,x,y,0);
    }
    public BallLocationUpdateLoop(Ball bol, double x, double y, double height){
        this.bol = bol; 
        Xcoordinate = bol.Xlocation;
        Ycoordinate = bol.Ylocation;
        heightCoordinate = bol.Zlocation;
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
            bol.Xlocation = Xcoordinate;
            bol.Ylocation = Ycoordinate;
            bol.Zlocation = heightCoordinate;
        }
    }
}
