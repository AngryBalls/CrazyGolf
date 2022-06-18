package com.angryballs.crazygolf;

import com.badlogic.gdx.math.Vector2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SplineInfo {
    public final LevelInfo levelInfo;

    // Top left corner point
    public int x;
    public int y;

    // Spline width/height
    public int w;
    public int h;

    // Bounds of Modifiable area
    Vector2[] bounds = new Vector2[2];

    // How much space is between 2 nodes
    public int unit = 1;

    // Modifiable points
    public double[][] nodes;
    private static int code = 0;

    // Coordinates of modified points
    public ArrayList<Vector2> modifiedNodes = new ArrayList<>();

    private static final int tension = 1;
    private static final int HEIGHT_LIMIT = 3;
    private static final double DELTA = 0.5;

    public SplineInfo(int x, int y, int w, int h, LevelInfo levelInfo) {
        this.levelInfo = levelInfo;

        nodes = new double[w][h];
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        bounds[0] = new Vector2(this.x, this.y);
        bounds[1] = new Vector2(this.x + (w - 1) * unit, this.y + (h - 1) * unit);

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
        bounds[1] = new Vector2(this.x + (w - 1) * unit, this.y + (h - 1) * unit);

        fill();
    }

    /**
     * Height at (x,y) within a spline
     *
     * @param x coordinate X
     * @param y coordinate Y
     * @return height at (x,y)
     */
    public double heightAt(double x, double y) {
        double result;
        float ty, tx;
        Vector2 shift = new Vector2();
        int ix, iy;
        double[] point = new double[4];
        double[] prepDots = new double[4];
        boolean external = false;
        boolean influenced = false;

        // start/end nodes
        Vector2 sign = new Vector2(x < 0 ? -1f : 1f, y < 0 ? -1f : 1f);
        Vector2 startNode = new Vector2((int) (x - x % unit), (int) (y - y % unit));
        Vector2 endNode = new Vector2((int) (startNode.x + sign.x * unit), (int) (startNode.y + sign.y * unit));

        ty = (float) (y - y % unit == 0 ? y * 1f / unit : y % (y - y % unit));
        tx = (float) (x - x % unit == 0 ? x * 1f / unit : x % (x - x % unit));

        ty = Math.abs(ty);
        tx = Math.abs(tx);

        // Check surrounding points to see if the calcs are influenced
        for (int xx = 0; xx < 2; xx++) {
            for (int yy = 0; yy < 2; yy++) {
                ix = (int) ((x - x % unit) - bounds[0].x) / unit;
                iy = (int) ((y - y % unit) - bounds[0].y) / unit;
                for (Vector2 p : modifiedNodes) {
                    if (ix == (int) p.x && iy == (int) p.y) {
                        influenced = true;
                        break;
                    }
                }
            }
        }
        if (!influenced) {
            return height(x, y);
        }

        // use splines to get 4 points along Y axis, then calculate spline in X axis
        for (int xx = 0; xx < 4; xx++) {
            shift.x = sign.x * unit * (xx - 1);
            if (!isInBounds(startNode.x + shift.x, true))
                external = true;
            for (int yy = 0; yy < 4; yy++) {
                shift.y = sign.y * unit * (yy - 1);
                if (external || !isInBounds(startNode.y + shift.y, false))
                    prepDots[yy] = height(startNode.x + shift.x, startNode.y + shift.y);
                else {
                    ix = (int) ((startNode.x - bounds[0].x + shift.x) / unit);
                    iy = (int) ((startNode.y - bounds[0].y + shift.y) / unit);
                    prepDots[yy] = nodes[ix][iy];
                }
            }
            point[xx] = formula(prepDots[0], prepDots[1], prepDots[2], prepDots[3], ty);
            external = false;
        }

        result = formula(point[0], point[1], point[2], point[3], tx);
        return result;
    }

    private double formula(double prev, double start, double end, double next, float t) {
        switch (code) {
            case 0:
                return splineAt(prev, start, end, next, t);
            case 1:
                return easeInOutQuad(t, start, end - start, 1);
            case 2:
                return easeInOutSine(t, start, end - start, 1);
        }
        return 0;
    }

    private double easeInOutQuad(double t, double start, double change, double speed) {
        if ((t /= speed / 2) < 1)
            return change / 2 * t * t + start;
        return -change / 2 * ((--t) * (t - 2) - 1) + start;

    }

    private double easeInOutSine(double t, double start, double change, double speed) {
        return -change / 2 * (Math.cos(
                Math.PI * t / speed) - 1) + start;

    }

    /**
     * Catmull-Rom Splines
     * Based on cubic interpolation
     *
     * @param prev  ghost point for start
     * @param start height at start node
     * @param end   height at end node
     * @param next  ghost point for next
     * @param t     how far desired point from start
     * @return height of the desired point
     */
    private double splineAt(double prev, double start, double end, double next, float t) {
        float tSquared = t * t;
        float tCubed = tSquared * t;

        return (-.5f * tension * tCubed + tension * tSquared - .5f * tension * t) * prev +
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
        return levelInfo.evaluateHeight(x, y);
    }

    public boolean isInModifiableArea(double x, double y) {
        return bounds[0].x <= x && bounds[1].x >= x && bounds[0].y <= y && bounds[1].y >= y;
    }

    public boolean isInSpline(double x, double y) {
        return bounds[0].x - 2 * unit <= x && bounds[1].x + 2 * unit >= x && bounds[0].y - 2 * unit <= y
                && bounds[1].y + 2 * unit >= y;
    }

    private boolean isInBounds(double value, boolean isX) {
        if (isX)
            return value <= bounds[1].x && value >= bounds[0].x;
        else
            return value <= bounds[1].y && value >= bounds[0].y;

    }

    public boolean moveUp(double x, double y) {
        return move(x, y, DELTA);
    }

    public boolean moveDown(double x, double y) {
        return move(x, y, -DELTA);
    }

    public boolean move(double x, double y, double amount) {
        if (!isInModifiableArea(x, y))
            return false;

        int ix = (int) ((x - x % unit) - bounds[0].x) / unit;
        int iy = (int) ((y - y % unit) - bounds[0].y) / unit;

        double height = nodes[ix][iy];
        if (!modifiedNodes.contains(new Vector2(ix, iy)))
            modifiedNodes.add(new Vector2(ix, iy));

        if (Math.abs(height) >= HEIGHT_LIMIT)
            return false;
        else {
            nodes[ix][iy] = height + amount;
            return true;
        }
    }

    public void test() {
        nodes = new double[][] { { 0.1, 0.1, 0.1, 0.1, 0.1 },
                { 0.1, -1.0, 0.5, -1.0, 0.1 },
                { 0.1, 0.5, 1.0, 0.5, 0.1 },
                { 0.1, -1.0, 0.5, -1.0, 0.1 },
                { 0.1, 0.1, 0.1, 0.1, 0.1 } };
        nodes = new double[5][5];
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
        var ss = new SplineInfo(-5, -5, 11, 11, LevelInfo.exampleInput);
        ss.moveUp(-5, -5);

        float t = 0;
        for (int c = 0; c < 3; c++) {
            code = c;
            System.out.println("FORMULA: " + code);
            System.out.println(ss.heightAt(-5.170328, -5.995397));
        }
    }

    public String serialize() {
        String output = String.format(Locale.US, "%d,%d,%d,%d", x, y, w, h);

        for (var modifiedNode : modifiedNodes) {
            output += String.format(Locale.US, ",%f,%f,%f", modifiedNode.x, modifiedNode.y,
                    nodes[(int) modifiedNode.x][(int) modifiedNode.y]);
        }

        return output;
    }

    public static SplineInfo deserializeFromString(String input, LevelInfo levelInfo) {
        var trimmed = input.substring(1, input.length() - 1);

        String[] splittedVarStr = trimmed.split(",");

        int x = Integer.parseInt(splittedVarStr[0]);
        int y = Integer.parseInt(splittedVarStr[1]);
        int w = Integer.parseInt(splittedVarStr[2]);
        int h = Integer.parseInt(splittedVarStr[3]);

        int current = 4;

        var spline = new SplineInfo(x, y, w, h, levelInfo);

        while (current < splittedVarStr.length) {
            var ix = (int) Float.parseFloat(splittedVarStr[current++]);
            var iy = (int) Float.parseFloat(splittedVarStr[current++]);
            var v = Float.parseFloat(splittedVarStr[current++]);

            spline.nodes[ix][iy] = v;
            spline.modifiedNodes.add(new Vector2(ix, iy));
        }

        return spline;
    }
}
