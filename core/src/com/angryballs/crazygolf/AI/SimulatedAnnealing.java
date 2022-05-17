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

    private final static double maxTemp = 10;

    // cooling step size
    private double coolingRate = 0.1;

    private double bestDistance = Double.MAX_VALUE;
    private Vector2 bestMove = new Vector2();

    private double currentDistance = Double.MAX_VALUE;

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
        float coeff = (float) (temperature / maxTemp);

        return new Vector2(bestMove.x + xDelta * coeff, bestMove.y + yDelta * coeff);
    }

    // generates an acceptance possibility for the new distance
    private boolean shouldAccept(double newDist) {
        var probability = Math.exp(-(newDist - currentDistance) / temperature);

        return rng.nextFloat() < probability;
    }

    @Override
    public Vector2 computeOptimalMove(double x, double y) {
        temperature = maxTemp;

        while (temperature > 0) {
            Vector2 newMove = generateRandomNeighbour(x, y);

            applyPhysicsState((float) x, (float) y, 0, 0);

            int moveResult = performMove(newMove);

            cooldown();

            // We hit a tree/body of water, don't even consider the move
            if (moveResult == 2)
                continue;

            // We've putted, no need to consider any other options
            if (moveResult == 3)
                return newMove;

            var newDist = distanceSquared(ps.x, ps.y);

            if (newDist < bestDistance) {
                bestDistance = currentDistance = newDist;
                bestMove = newMove;
            } else if (shouldAccept(newDist)) {
                currentDistance = newDist;
            }
        }
        return bestMove;
    }
}
