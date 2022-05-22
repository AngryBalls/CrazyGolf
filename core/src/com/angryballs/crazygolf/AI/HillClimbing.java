package com.angryballs.crazygolf.AI;

import java.util.List;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.badlogic.gdx.math.Vector2;

public class HillClimbing extends Bot {

    private static double EPSILON = Double.MIN_VALUE;

    public HillClimbing(LevelInfo info, List<TreeModel> trees) {
        super(info, trees);
    }

    @Override
    public Vector2 computeOptimalMove(double x, double y) {
        var current = new double[] { Math.min(Math.max(xt - x, -5), 5), Math.min(Math.max(yt - y, -5), 5) };
        var stepSize = new double[] { 1.0, 1.0 };
        var acc = 0.1;

        var candidates = new double[] { -acc, -1 / acc, 1 / acc, acc };

        // Lower is better
        var bestScore = evaluate(x, y, current);
        int ite = 0;

        while (true) {
            var prevScore = bestScore;
            for (int i = 0; i < current.length; ++i) {
                ++ite;
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
            if (prevScore - bestScore < EPSILON) {
//                System.out.println("\nHill Climbing");
//                System.out.println("=============");
//                System.out.println("Iterations: " + ite);
                //System.out.println("Distance: " + bestScore);
                //System.out.println("Speed: " + new Vector2((float) current[0], (float) current[1]));
                return new Vector2((float) current[0], (float) current[1]);
            }
        }
    }

    private double evaluate(double x, double y, double[] vel) {
        applyPhysicsState((float) x, (float) y, 0, 0);

        var moveResult = performMove(new Vector2((float) vel[0], (float) vel[1]));

        double fitness = distanceSquared(ps.x, ps.y);

        return fitness;
    }
}
