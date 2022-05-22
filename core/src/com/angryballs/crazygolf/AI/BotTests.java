package com.angryballs.crazygolf.AI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;
import com.badlogic.gdx.math.Vector3;

public class BotTests {
    public final static LevelInfo INFO = LevelInfo.exampleInput;

    public static final int AMOUNTOFTEST = 10;
    public static final int AMOUNTOFTREES = 25;
    public static final int AREA = 20;

    private static List<TreeModel> trees = new ArrayList<TreeModel>();

    public static List<Integer> gdShots = new ArrayList<Integer>();
    public static int totalGDShots = 0;

    public static List<Integer> hcShots = new ArrayList<Integer>();
    public static int totalHCShots = 0;

    public static List<Float> SAShots = new ArrayList<Float>();
    public static float totalSAShots = 0;

    public static void main(String[] args) {

        // Bot rbb = new RuleBasedBot(INFO);
        // rbb.run(rbb);

        for (int i = 0; i < AMOUNTOFTEST; i++) {
            System.out.println("test " + (i + 1));
            generateTrees();

            System.out.println("GD");
            Bot gd = new GradientDescent(INFO, trees);
            int temp = gd.run();
            gdShots.add(temp);
            totalGDShots += temp;

            System.out.println("HC");
            Bot hc = new HillClimbing(INFO, trees);
            temp = hc.run();
            hcShots.add(temp);
            totalHCShots += temp;

            System.out.println("SA");
            int total = 0;
            int holeInOnes = 0;
            for (int j = 0; j < 10; ++j) {
                Bot sa = new SimulatedAnnealing(INFO, trees);
                int count = sa.run();

                if (count == 1)
                    ++holeInOnes;

                total += count;
            }

            float avg = total / 10f;
            SAShots.add(avg);
            totalSAShots += avg;
        }


        System.out.println("\nGradient Descent");
        System.out.println(gdShots);
        System.out.println(totalGDShots / AMOUNTOFTEST);

        System.out.println("\nHill Climbing");
        System.out.println(hcShots);
        System.out.println(totalHCShots / AMOUNTOFTEST);

        System.out.println("\nSimulated Annealing");
        System.out.println(SAShots);
        System.out.println(totalSAShots / AMOUNTOFTEST);

    }

    public static void generateTrees() {

        Random rng = new Random();
        trees.clear();
        for (int i = 0; i < AMOUNTOFTREES; ++i) {
            float x = rng.nextFloat() * rng.nextInt(AREA) * (rng.nextBoolean() ? -1 : 1);
            float z = rng.nextFloat() * rng.nextInt(AREA) * (rng.nextBoolean() ? -1 : 1);

            float y = INFO.heightProfile(x, z).floatValue();

            var tree = new TreeModel(true);
            tree.setPosition(new Vector3(x, y, -z));
            trees.add(tree);
        }
    }
}
