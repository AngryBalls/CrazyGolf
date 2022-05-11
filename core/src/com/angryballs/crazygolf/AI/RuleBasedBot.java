package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

public class RuleBasedBot extends Bot {
    public RuleBasedBot(LevelInfo info) {
        super(info);
    }

    /**
     * Brute-Force aka Hill climbing Algo
     * Estimates and selects the best shot among the neighbourhood of the starting
     * point
     *
     * @param x staring X coordinate
     * @param y starting Y coordinate
     * @return vector( vx, vy )
     */
    public Vector2 shoot(double x, double y) {
        double distance = distanceSquared(x, y);

        double xs = x;
        double ys = y;

        Vector2 speed = new Vector2();

        for (int fvx = -5; fvx < 5; fvx++) {
            for (int fvy = -5; fvy < 5; fvy++) {

                if (fvx == 0 && fvy == 0)
                    continue;
                // Get right speed by dividing
                speed.x = (fvx * 1.0f);
                speed.y = (fvy * 1.0f);

                ps.setStateVector(xs, ys, 0, 0);
                performMove(new Vector2(speed.x, speed.y));

                double locDist = distanceSquared(ps.x, ps.y);
                if (locDist < distance) {
                    distance = locDist;
                    xb = ps.x;
                    yb = ps.y;
                    vxb = speed.x;
                    vyb = speed.y;
                }
            }
        }
        return new Vector2((float) vxb, (float) vyb);
    }

    /**
     * Performs shoot directly towards the hole
     *
     * @param x starting X coordinate
     * @param y starting Y coordinate
     * @return vector( vx, vy )
     */
    public Vector2 swing(double x, double y) {
        double distance = distanceSquared(ps.x, ps.y);
        Vector2 startCoords = new Vector2((float) x, (float) y);

        // take target coords as direction + approximate values
        Vector2 targetSpeed = new Vector2((float) Math.max(Math.min(xt - x, 5), -5),
                (float) Math.max(Math.min(yt - y, 5), -5));
        this.vxb = targetSpeed.x;
        this.vyb = targetSpeed.y;

        float speedStep = 0.5f;
        float speedDistribution = 1.0f / 2; // defines how far we can go from target speed

        Vector2 speedLowerPos = new Vector2(Math.max(targetSpeed.x - speedDistribution, -5),
                Math.max(targetSpeed.y - speedDistribution, -5));
        Vector2 speedUpperPos = new Vector2(Math.min(targetSpeed.x + speedDistribution, 5),
                Math.min(targetSpeed.y + speedDistribution, 5));

        Vector2 curSpeed = new Vector2(speedLowerPos.x, speedLowerPos.y);

        while (curSpeed.x < speedUpperPos.x) {
            while (curSpeed.y < speedUpperPos.y) {
                if (curSpeed.x == 0 && curSpeed.y == 0)
                    continue;

                ps.setStateVector(startCoords.x, startCoords.y, 0, 0);
                performMove(curSpeed);

                double locDist = distanceSquared(ps.x, ps.y);
                if (locDist < distance) {
                    distance = locDist;
                    xb = ps.x;
                    yb = ps.y;
                    vxb = curSpeed.x;
                    vyb = curSpeed.y;
                }

                curSpeed.y += speedStep;
            }
            curSpeed.x += speedStep;
            curSpeed.y = speedLowerPos.y;
        }
        return new Vector2((float) vxb, (float) vyb);
    }

    @Override
    public Vector2 computeOptimalMove(double x, double y) {
        // Try every possible shot
        // return shoot(x,y);
        // Swing towards the target
        return swing(x, y);
    }
}
