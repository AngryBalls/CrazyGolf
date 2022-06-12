package com.angryballs.crazygolf;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SplineInfo {
    public LevelInfo levelInfo = LevelInfo.exampleInput;

    // main point properties
    public int x;
    public int y;

    public int w;
    public int h;

    Vector2[] bounds = new Vector2[2];

    public int unit = 1;

    public  double[][] nodes;

    private static final int tension = 1;

    public SplineInfo(int x, int y, int w, int h) {
        nodes = new double[w][h];
        this.x = x;
        this.y = y;
        this.w = w;
        this.h=h;
        nodes[w/2][h/2] = levelInfo.heightProfile(x,y);

        bounds[0] = new Vector2(this.x - w/2*unit,this.y - h/2*unit);
        bounds[1] = new Vector2(this.x + w/2*unit,this.y + h/2*unit);


        fill();
    }

    public double heightAt(double x, double y) {
        if(!isInSpline(x,y)){
            //TODO: merge with height profile
            return levelInfo.heightProfile(x,y);}

        double result;
        float ty, tx, shift;
        int ix, iy;
        double[] point = new double[4];

        double prev, next, start, end;

        ty = (float) (y-y%unit==0? y *1f/unit:y%(y-y%unit));
        tx = (float) (x-x%unit==0? x *1f/unit:x%(x-x%unit));

        // start/end nodes
        Vector2 startNode = new Vector2((int)(x-x%unit),(int) (y-y%unit));
        if((x-x%unit)%unit==0&&((y-y%unit)%unit==0))
            return nodes[(int)((x-x%unit)-bounds[0].x)/unit][(int)((y-y%unit)-bounds[0].y)/unit];
        Vector2 endNode = new Vector2((int)(x-x%unit+unit),(int) (y-y%unit+unit));

        //calculate 4 horizontal points using 4 points for each

        for(int i = 0; i < 4; i++){
            shift = unit*(i-1);
            if(startNode.x - bounds[0].x+shift<0)
                ix = (int)((startNode.x - bounds[0].x)/unit);
            else if(startNode.x+shift>bounds[1].x)
                ix = (int)((endNode.x - bounds[0].x)/unit);
            else
                ix = (int)((startNode.x - bounds[0].x+shift)/unit);
            iy = (startNode.y-unit-bounds[0].y)/unit>=0?
                    (int)(startNode.y-unit-bounds[0].y)/unit:
                    (int)(startNode.y-bounds[0].y)/unit;
            prev = nodes[ix][iy];
            iy = (int)(startNode.y-bounds[0].y)/unit;
            start = nodes[ix][iy];
            iy = (int)(endNode.y-bounds[0].y)/unit;
            end = nodes[ix][iy];
            iy = (endNode.y+unit)<=bounds[1].y?
                    (int)(endNode.y+unit-bounds[0].y)/unit:
                    (int)(endNode.y-bounds[0].y)/unit;
            next = nodes [ix][iy];
            point[i] = splineAt(prev,start,end,next,ty);
        }
        result = splineAt(point[0],point[1],point[2],point[3],tx);
        return result;
    }

    /**
     * Catmull splines
     * @param prev
     * @param start
     * @param end
     * @param next
     * @param t - how far the point from the start
     * @return height at the point between start and end
     */
    private double splineAt(double prev, double start, double end, double next, float t){
        float tSquared = t * t;
        float tCubed = tSquared * t;

        double result = (-.5f * tension * tCubed + tension * tSquared - .5f * tension * t) * prev +
                        (1 + .5f * tSquared * (tension - 6) + .5f * tCubed * (4 - tension)) * start +
                        (.5f * tCubed * (tension - 4) + .5f * tension * t - (tension - 3) * tSquared) * end +
                        (-.5f * tension * tSquared + .5f * tension * tCubed) * next;

        return result;
    }

    private void fill(){

        for(int xx = 0; xx <= (bounds[1].x-bounds[0].x)/unit;xx++){
            for(int yy = 0; yy <= (bounds[1].y-bounds[0].y)/unit;yy++){
                nodes[xx][yy] = height(bounds[0].x+unit*xx,bounds[0].y+unit*yy);
            }
        }
    }

    private void print(){

        for(int xx = 0; xx <= (bounds[1].x-bounds[0].x)/unit;xx++){
            for(int yy = 0; yy <= (bounds[1].y-bounds[0].y)/unit;yy++){
                System.out.print(nodes[xx][yy]+" ");
            }
            System.out.println();
        }
    }

    private double height(double x,double y){
        return levelInfo.heightProfile(x,y);
    }

    public boolean isInSpline(double x, double y) {
        return bounds[0].x<=x && bounds[1].x>=x && bounds[0].y<=y && bounds[1].y>=y;
    }

    public void setZ(double height){
        nodes[w/2][h/2] = height;
    }

    public void test(){
        nodes = new double[][]
                       {{0.1, 0.1, 0.1, 0.1, 0.1},
                        {0.1, 0.5, 0.5, 0.5, 0.1},
                        {0.1, 0.5, 1  , 0.5, 0.1},
                        {0.1, 0.5, 0.5, 0.5, 0.1},
                        {0.1, 0.1, 0.1, 0.1, 0.1}};
    }

    public static void main(String[] args) {
        SplineInfo s = new SplineInfo(0,0,5,5);
        s.setZ(10);
        System.out.println("Matrix:");
        s.print();
        System.out.println(s.heightAt(3.3,-3));
    }
}