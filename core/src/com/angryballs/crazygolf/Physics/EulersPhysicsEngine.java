package com.angryballs.crazygolf.Physics;

import java.util.ArrayList;
import java.util.List;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.badlogic.gdx.math.Vector2;

/**
 * A physics system using Euler's method to approximate the results
 */
public class EulersPhysicsEngine extends PhysicsEngine {
    public EulersPhysicsEngine(LevelInfo levelInfo, List<TreeModel> trees) {
        super(levelInfo, trees);
    }

    @Override
    protected void performCalculations(Vector2 derivative, float h) {
        var a = acceleration(derivative);

        this.x += h * vx;
        this.y += h * vy;

        this.vx += h * a.x;
        this.vy += h * a.y;
    }

    public static void main(String[] args) {
        EulersPhysicsEngine sv = new EulersPhysicsEngine(LevelInfo.exampleInput, new ArrayList<>());
        sv.performMove(new Vector2(3, 0));
        int code = sv.iterate();
        while (code == 0) {
            System.out.println("X: " + sv.getX() + ", Y: " + sv.getY() + ", Vx: " + sv.getVx() + ", Vy: " + sv.getVy());
            code = sv.iterate();
        }
    }
}
