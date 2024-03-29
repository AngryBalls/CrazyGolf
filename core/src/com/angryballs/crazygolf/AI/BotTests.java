package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;

public class BotTests {
    public final static LevelInfo INFO = LevelInfo.exampleInput;

    public static void main(String[] args) {
        // Bot rbb = new RuleBasedBot(INFO);
        // rbb.run(rbb);
        System.out.println("\nGradient Descent");
        Bot gd = new GradientDescent(INFO, null);
        gd.run();

        System.out.println("\nHill Climbing");

        Bot hc = new HillClimbing(INFO, null);
        hc.run();

        System.out.println("\nSimulated Annealing");
        int total = 0;
        int holeInOnes = 0;
        for (int i = 0; i < 1; ++i) {
            Bot sa = new SimulatedAnnealing(INFO, null);
            int count = sa.run();

            if (count == 1)
                ++holeInOnes;

            total += count;
        }

        float avg = total / 1;
        System.out.println("Stats for Simulated Annealing over 100 games");
        System.out.printf("Average shots per game: %.8f\n", avg);
        System.out.printf("Number of hole in ones: %d\n", holeInOnes);

    }
}
