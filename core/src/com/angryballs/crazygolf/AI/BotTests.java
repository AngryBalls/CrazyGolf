package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;

public class BotTests {
    public final static LevelInfo INFO = LevelInfo.exampleInput;

    public static void main(String[] args) {
        // Bot rbb = new RuleBasedBot(INFO);
        // rbb.run(rbb);
        Bot gd = new GradientDescent(INFO);
        gd.ps.setStateVector(-1, -1, 0, 0);
        // gd.run();
        Bot nr = new NewtonRaphson(INFO);
        nr.ps.setStateVector(-1, -1, 0, 0);
        nr.run();
    }
}
