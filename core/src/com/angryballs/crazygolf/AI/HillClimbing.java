package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

public class HillClimbing extends Bot {

    public HillClimbing(LevelInfo info) {
        super(info);
    }

    @Override
    public Vector2 computeOptimalMove(double x, double y) {
        var current = new double[] { 0, 0 };
        var stepSize = new double[] { 1.0, 1.0 };
        var acc = 1.2;

        var candidates = new double[] { -acc, -1 / acc, 1 / acc, acc };

        // Lower is better
        var bestScore = distanceSquared(x, y);

        while (true) {
            var prevScore = bestScore;
            for (int i = 0; i < current.length; ++i) {
                var prev = current[i];
                var bestStep = 0.0;
                for (int j = 0; j < candidates.length; ++j) {
                    var step = stepSize[i] * candidates[j];
                    current[i] = prev + step;
                    current[i] = Math.min(Math.max(current[i], -5), 5);
                    var score = evaluate(x, y, current);
                    if (score < bestScore) {
                        bestScore = score;
                        bestStep = step;
                    }
                }
                if (bestStep == 0) {
                    current[i] = prev;
                    stepSize[i] = stepSize[i] / acc;
                } else {
                    current[i] = Math.min(Math.max(prev + bestStep, -5), 5);
                    stepSize[i] = bestStep;
                }
            }
            if (prevScore - bestScore < EPSILON)
                return new Vector2((float) current[0], (float) current[1]);
        }
    }

    private double evaluate(double x, double y, double[] vel) {
        applyPhysicsState((float) x, (float) y, 0, 0);
        performMove(new Vector2((float) vel[0], (float) vel[1]));
        return distanceSquared(ps.x, ps.y);
    }
}
