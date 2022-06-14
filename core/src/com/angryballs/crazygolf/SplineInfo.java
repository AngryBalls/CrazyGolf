package com.angryballs.crazygolf;

import com.badlogic.gdx.math.Vector2;

import java.text.DecimalFormat;

public class SplineInfo {
    public final LevelInfo levelInfo;

    //Top left corner point
    public int x;
    public int y;

    //Spline width/height
    public int w;
    public int h;

    Vector2[] bounds = new Vector2[2];

    // How much space is between 2 nodes
    public int unit =  1;

    //Modifiable points
    public double[][] nodes;

    private static final int tension = 1;
    private static final int HEIGHT_LIMIT = 5;

    public SplineInfo(int x, int y, int w, int h, LevelInfo levelInfo) {
        this.levelInfo = levelInfo;

        nodes = new double[w][h];
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        bounds[0] = new Vector2(this.x, this.y);
        bounds[1] = new Vector2(this.x + (w-1) * unit, this.y + (h-1) * unit);

        fill();
    }

    public SplineInfo(int x, int y, int w, int h, int unit, LevelInfo levelInfo) {
        this.levelInfo = levelInfo;
        nodes = new double[w][h];
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.unit = unit;

        bounds[0] = new Vector2(this.x, this.y);
        bounds[1] = new Vector2(this.x + (w-1) * unit, this.y + (h-1) * unit);
        fill();
    }

    public double heightAt(double x, double y) {
        double result;
        float ty, tx;
        Vector2 shift = new Vector2();
        int ix, iy;
        double[] point = new double[4];
        double[] prepDots = new double[4];
        boolean external = false;


        ty = (float) (y - y % unit == 0 ? y * 1f / unit : y % (y - y % unit));
        tx = (float) (x - x % unit == 0 ? x * 1f / unit : x % (x - x % unit));

        // start/end nodes
        Vector2 startNode = new Vector2((int) (x - x % unit), (int) (y - y % unit));
        Vector2 endNode = new Vector2((int) (x - x % unit + unit), (int) (y - y % unit + unit));

        // use splines to get 4 points along Y axis, then calculate spline in X axis
        for (int xx = 0; xx < 4; xx++) {
            shift.x =  unit * (xx - 1);
            if(!isInBounds(startNode.x+shift.x,true))
                external = true;
            for (int yy = 0; yy < 4; yy++){
                shift.y = unit * (yy-1);
                if( external || !isInBounds(startNode.y+shift.y,false))
                    prepDots[yy] = height(startNode.x+shift.x,startNode.y+shift.y);
                else{
                    ix = (int) ((startNode.x - bounds[0].x + shift.x) / unit);
                    iy = (int) ((startNode.y - bounds[0].y + shift.y) / unit);
                    prepDots[yy] = nodes[ix][iy];
                }
            }
            point[xx] = splineAt(prepDots[0],prepDots[1],prepDots[2],prepDots[3],ty);
            external = false;
        }

        result = splineAt(point[0], point[1], point[2], point[3], tx);
        return result;
    }

    /**
     * Catmull splines
     *
     * @param prev
     * @param start
     * @param end
     * @param next
     * @param t     - how far the point from the start
     * @return height at the point between start and end
     */
    private double splineAt(double prev, double start, double end, double next, float t) {
        float tSquared = t * t;
        float tCubed = tSquared * t;

        return  (-.5f * tension * tCubed + tension * tSquared - .5f * tension * t) * prev +
                (1 + .5f * tSquared * (tension - 6) + .5f * tCubed * (4 - tension)) * start +
                (.5f * tCubed * (tension - 4) + .5f * tension * t - (tension - 3) * tSquared) * end +
                (-.5f * tension * tSquared + .5f * tension * tCubed) * next;
    }

    private void fill() {

        for (int xx = 0; xx <= (bounds[1].x - bounds[0].x) / unit; xx++) {
            for (int yy = 0; yy <= (bounds[1].y - bounds[0].y) / unit; yy++) {
                nodes[xx][yy] = height(bounds[0].x + unit * xx, bounds[0].y + unit * yy);
            }
        }
    }

    private void print() {

        for (int xx = 0; xx <= (bounds[1].x - bounds[0].x) / unit; xx++) {
            for (int yy = 0; yy <= (bounds[1].y - bounds[0].y) / unit; yy++) {
                System.out.print(nodes[xx][yy] + " ");
            }
            System.out.println();
        }
    }

    private double height(double x, double y) {
        return levelInfo.heightProfile(x, y);
    }

    public boolean isInModifiableArea(double x, double y) {
        return bounds[0].x <= x && bounds[1].x >= x && bounds[0].y <= y && bounds[1].y >= y;
    }
    public boolean isInSpline(double x, double y){
        return bounds[0].x - 2*unit <= x && bounds[1].x +2*unit>= x && bounds[0].y -2*unit<= y && bounds[1].y+2*unit >= y;
    }

    public void setZ(double height) {
        nodes[w / 2][h / 2] = height;
    }

    private boolean isInBounds(double value, boolean isX){
        if(isX)
            return value<=bounds[1].x&&value>=bounds[0].x;
        else
            return value<=bounds[1].y&&value>=bounds[0].y;

    }

    public boolean movetUp(double x,double y){
        if(!isInModifiableArea(x,y))
            return false;
        Vector2 node = new Vector2((int) (x - x % unit), (int) (y - y % unit));
        int ix = (int) ((x - x % unit)-bounds[0].x)/unit;
        int iy = (int) ((y - y % unit)-bounds[0].y)/unit;

        double height = nodes[ix][iy];
        if(Math.abs(height)>=HEIGHT_LIMIT)
            return false;
        else{
            nodes[ix][iy] = ++height;
            return true;
        }
    }
    public boolean moveDown(double x,double y){
        if(!isInModifiableArea(x,y)){
            System.out.println("NOT IN THE AREA");
            System.out.println(bounds[0]);
            System.out.println(bounds[1]);
            return false;
        }
        Vector2 node = new Vector2((int) (x - x % unit), (int) (y - y % unit));
        int ix = (int) ((x - x % unit)-bounds[0].x)/unit;
        int iy = (int) ((y - y % unit)-bounds[0].y)/unit;
        System.out.println(node);

        double height = nodes[ix][iy];
        if(Math.abs(height)>=HEIGHT_LIMIT)
            return false;
        else{
            nodes[ix][iy] = --height;
            return true;
        }
    }

    public void test() {
        nodes = new double[][] { { 0.1, 0.1, 0.1, 0.1, 0.1 },
                { 0.1, -1.0, 0.5, -1.0, 0.1 },
                { 0.1, 0.5, 1.0, 0.5, 0.1 },
                { 0.1, -1.0, 0.5, -1.0, 0.1 },
                { 0.1, 0.1, 0.1, 0.1, 0.1 } };
        System.out.println("Map:");
        print();
        double[][] map = new double[2 * w - 1][2 * h - 1];
        double step = unit * 1f / 2;
        System.out.println("Detailed");
        for (int xx = 0; xx < map.length; xx++) {
            for (int yy = 0; yy < map[0].length; yy++) {
                map[xx][yy] = heightAt(bounds[0].x + xx * unit * step, bounds[0].y + yy * unit * step);

                DecimalFormat df = new DecimalFormat("#.####");
                System.out.print(map[xx][yy] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        SplineInfo s = new SplineInfo(-2, -2, 5, 5, LevelInfo.exampleInput);
        s.setZ(10);
        System.out.println("Matrix:");
        s.print();

        System.out.println(s.heightAt(10.8,-2.2));

    }
}
