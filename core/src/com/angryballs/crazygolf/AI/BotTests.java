package com.angryballs.crazygolf.AI;

import java.util.ArrayList;

import com.angryballs.crazygolf.LevelInfo;

public class BotTests {
    public final static LevelInfo INFO = LevelInfo.exampleInput;

    public static void main(String[] args) {
        // Bot rbb = new RuleBasedBot(INFO);
        // rbb.run(rbb);
        System.out.println("\nGradient Descent");
        Bot gd = new GradientDescent(INFO, new ArrayList<>());
        // gd.run();

        System.out.println("\nNewton Raphson");
        Bot nr = new NewtonRaphson(INFO, new ArrayList<>());
        // nr.run();

        System.out.println("\nHill Climbing");
        Bot hc = new HillClimbing(INFO, new ArrayList<>());
        hc.run();

        System.out.println("\nSimulated Annealing");
        Bot sa = new SimulatedAnnealing(INFO, new ArrayList<>());
        sa.run();
    }
}
