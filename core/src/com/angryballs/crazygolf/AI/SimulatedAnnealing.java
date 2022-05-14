package com.angryballs.crazygolf.AI;

import java.util.ArrayList;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

public class SimulatedAnnealing {

    // initial temperature
    private double temperature = 10000;

    // cooling step size
    private double coolingRate = 0.005;

    private LevelInfo levelInfo;

    public SimulatedAnnealing(LevelInfo info) {
        this.levelInfo = info;
    }

    // generates an acceptance possibility for the new distance
    private double acceptanceChance(double minDist, double curDist, double temperature) {
        // accept better distance in 100%
        if (minDist > curDist)
            return 1.0;

        return Math.exp(curDist - minDist) / temperature;
    }

    public void run() {
        RuleBasedBot rbb = new RuleBasedBot(levelInfo, new ArrayList<>());

        while (temperature > 1) {

        }
    }

}
