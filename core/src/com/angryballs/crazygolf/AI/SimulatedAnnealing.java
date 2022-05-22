package com.angryballs.crazygolf.AI;

import java.util.List;
import java.util.Random;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.badlogic.gdx.math.Vector2;

public class SimulatedAnnealing extends Bot {
    public SimulatedAnnealing(LevelInfo info, List<TreeModel> trees) {
        super(info, trees);
    }

    private Random rng = new Random();

    // initial temperature
    private double temperature = maxTemp;

    private final static double maxTemp = 20;

    // cooling step size
    private double coolingRate = 0.1;

    private double bestDistance = Double.MAX_VALUE;
    private Vector2 bestMove = new Vector2();

    private void cooldown() {
        temperature -= coolingRate;
    }

    private Vector2 generateRandomNeighbour(double x, double y) {
        if (temperature == maxTemp)
            return new Vector2((float) Math.min(Math.max(xt - x, -5), 5), (float) Math.min(Math.max(yt - y, -5), 5));

        // Generates a new vector with each axis being between -5 and 5
        float xDelta = (rng.nextFloat() * 10) - (5 + bestMove.x);
        float yDelta = (rng.nextFloat() * 10) - (5 + bestMove.y);

        // This is used to reduce the rate of change as temperature goes down
        float coeff = 1 - easeOutBounce((float) (1 - temperature / maxTemp));

        return new Vector2(bestMove.x + xDelta * coeff, bestMove.y + yDelta * coeff);
    }

    @Override
    public Vector2 computeOptimalMove(double x, double y) {
        temperature = maxTemp;
        // bestDistance = Double.MAX_VALUE;
        bestMove = new Vector2();
        int ite = 0;

        while (temperature > 0) {
            ++ite;
            Vector2 newMove = generateRandomNeighbour(x, y);

            applyPhysicsState((float) x, (float) y, 0, 0);

            int moveResult = performMove(newMove);

            cooldown();

            // We hit a tree/body of water, don't even consider the move
            if (moveResult == 2)
                continue;

            // We've putted, no need to consider any other options
            if (moveResult == 3) {
                System.out.println("\nSimulated Annealing");
                System.out.println("===================");
                System.out.println("Iterations: " + ite);
                System.out.println("Distace: " + bestDistance);
                System.out.println("Speed: " + bestMove);
                return newMove;
            }

            var newDist = distanceSquared(ps.x, ps.y);

            if (newDist < bestDistance) {
                bestDistance = newDist;
                bestMove = newMove;
            }
        }

        System.out.println("\nSimulated Annealing");
        System.out.println("===================");
        System.out.println("Iterations: " + ite);
        System.out.println("Distace: " + bestDistance);
        System.out.println("Speed: " + bestMove);
        return bestMove;
    }

    // MaxT 10 => 6, // MaxT = 20 => 10
    // Viable on higher MaxT
    private float easeOutPow2(float t) {
        return t * t;
    }

    // MaxT 10 => 12, // MaxT = 20 => 44
    // Viable on higher MaxT
    private float easeOutCubic(float t) {
        return (float) (1 - Math.pow(1 - t, 3));
    }

    // MaxT 10 => 8, // MaxT = 20 => 42
    // Viable on higher MaxT, not good at all
    // with lower T's
    private float easeOutCirc(float t) {
        return (float) Math.sqrt(1 - Math.pow(t - 1, 2));
    }

    // MaxT 10 => 20, // MaxT = 20 => 57
    // Best of the bunch
    private float easeOutBounce(float t) {
        final float n1 = 7.5625f;
        final float d1 = 2.75f;

        if (t < 1 / d1) {
            return n1 * t * t;
        } else if (t < 2 / d1) {
            return n1 * (t -= 1.5f / d1) * t + 0.75f;
        } else if (t < 2.5 / d1) {
            return n1 * (t -= 2.25f / d1) * t + 0.9375f;
        } else {
            return n1 * (t -= 2.625f / d1) * t + 0.984375f;
        }
    }

    // MaxT 10 => 6, // MaxT = 20 => 23
    // Crap
    private float stepToEaseOutCirc(float t) {
        if (t < 0.5f)
            return 0;

        return easeOutCirc((t - 0.5f) * 2);
    }

    // MaxT 10 => 6, // MaxT = 20 => 26
    // Pretty good with higher maxT, crap at low maxT
    private float stepToStep(float t) {
        if (t < 0.25f)
            return 0;

        if (t < 0.5f)
            return 0.5f;

        if (t < 0.75f)
            return 0.75f;

        return 0.95f;
    }
}
