package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;

public class BotTests {
    public final static LevelInfo INFO = LevelInfo.exampleInput;

    public static void main(String[] args) {
        // Bot rbb = new RuleBasedBot(INFO);
        // rbb.run(rbb);
        System.out.println("\nGradient Descent");
        Bot gd = new GradientDescent(INFO);
        gd.ps.setStateVector(-1, -1, 0, 0);
        gd.run();

        System.out.println("\nNewton Raphson");
        Bot nr = new NewtonRaphson(INFO);
        nr.ps.setStateVector(-1, -1, 0, 0);
        nr.run();

        System.out.println("\nHill Climbing");
        Bot hc = new HillClimbing(INFO);
        hc.applyPhysicsState(-1, -1, 0, 0);
        hc.run();
    }
}
