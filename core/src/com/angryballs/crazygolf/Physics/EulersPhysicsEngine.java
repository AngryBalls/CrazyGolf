package com.angryballs.crazygolf.Physics;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

/**
 * A physics system using Euler's method to approximate the results
 */
public class EulersPhysicsEngine extends PhysicsEngine {
    public EulersPhysicsEngine(LevelInfo levelInfo) {
        super(levelInfo);
    }

    @Override
    protected void performCalculations(Vector2 derivative) {
        var a = acceleration(derivative);

        this.x += h * vx;
        this.y += h * vy;

        this.vx = Math.max(-5, Math.min(vx + h * a.x, 5));
        this.vy = Math.max(-5, Math.min(vy + h * a.y, 5));
    }

    public static void main(String[] args) {
        EulersPhysicsEngine sv = new EulersPhysicsEngine(LevelInfo.exampleInput);
        sv.performMove(new Vector2(3, 0));
        int code = sv.iterate();
        while (code == 0) {
            System.out.println("X: " + sv.getX() + ", Y: " + sv.getY() + ", Vx: " + sv.getVx() + ", Vy: " + sv.getVy());
            code = sv.iterate();
        }
    }
}
