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
    public int iterate() {
        if (!ballMoving) {
            // System.out.println("Iteration is called on stationary ball, returning
            // early.");
            return 1;
        }
        Vector2 dh = derivative(x, y);
        Vector2 a;
        boolean sandFlag = false;

        if ((x >= sandBoundsX.x && x <= sandBoundsY.x)
                && (y >= sandBoundsX.y && y <= sandBoundsY.y)) {
            a = acceleration(dh, usk);
            sandFlag = true;
        } else {
            a = acceleration(dh, uk);
        }

        this.x += h * vx;
        this.y += h * vy;

        this.vx = Math.max(-5, Math.min(vx + h * a.x, 5));
        this.vy = Math.max(-5, Math.min(vy + h * a.y, 5));

        // TODO: add the range of trees
        if (getHeight(this.x, this.y) < 0) {
            ballMoving = false;
            return 2;
        }

        // (x-targetX)^2 + (y-targetY)^2 <= radius^2
        if (isInCircle(this.x, this.xt, this.y, this.yt, this.r)) {
            ballMoving = false;
            return 3;
        }

        // Check if the ball is in the final position (will not move)
        if (Math.abs(vx) <= 0.1 && Math.abs(vy) <= 0.1) { // 0.09
            if (Math.abs(dh.x) < Float.MIN_VALUE && Math.abs(dh.y) < Float.MIN_VALUE) {
                ballMoving = false;
                return 1;
            } else if (!sandFlag && us > Math.sqrt(Math.pow(dh.x, 2) + Math.pow(dh.y, 2))) {
                ballMoving = false;
                return 1;
            } else if (sandFlag && uss > Math.sqrt(Math.pow(dh.x, 2) + Math.pow(dh.y, 2))) {
                ballMoving = false;
                return 1;
            } else
                return 0;
        } else
            return 0;
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
