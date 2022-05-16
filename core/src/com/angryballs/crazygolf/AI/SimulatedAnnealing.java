package com.angryballs.crazygolf.AI;

import java.util.ArrayList;
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
    private double temperature = 20;

    // cooling step size
    private double coolingRate = 0.1;

    private LevelInfo levelInfo;

    private double bestDistance = Double.MAX_VALUE;
    private Vector2 bestMove = new Vector2();

    private double currentDistance = Double.MAX_VALUE;
    private Vector2 currentMove = new Vector2();

    private void cooldown() {
        temperature -= coolingRate;
    }

    private Vector2 generateRandomNeighbour() {
        // Generates a new vector with each axis being between -5 and 5
        float x = (rng.nextFloat() * 10) - 5;
        float y = (rng.nextFloat() * 10) - 5;

        return new Vector2(x, y);
    }

    // generates an acceptance possibility for the new distance
    private boolean shouldAccept(double newDist) {
        var probability = Math.exp(-(newDist - currentDistance) / temperature);

        return rng.nextFloat() < probability;
    }

    @Override
    public Vector2 computeOptimalMove(double x, double y) {
        temperature = 20;
        while (temperature > 0) {
            Vector2 newMove = generateRandomNeighbour();

            applyPhysicsState((float) x, (float) y, 0, 0);
            performMove(newMove);

            var newDist = distanceSquared(ps.x, ps.y);

            if (newDist < bestDistance) {
                bestDistance = currentDistance = newDist;
                bestMove = currentMove = newMove;
            } else if (shouldAccept(newDist)) {
                currentDistance = newDist;
                currentMove = newMove;
            }
            cooldown();
        }
        return bestMove;
    }
}
