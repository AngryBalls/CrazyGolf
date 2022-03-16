package com.angryballs.crazygolf;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.math.Vector2;

public class VelocityReader {
    public static ArrayList<Vector2> initialVelocities = new ArrayList<>();

    public static void scan() {
        File folder = new File("swings");
        if (!folder.exists())
            return;

        for (var file : folder.listFiles()) {
            try (Scanner input = new Scanner(file)) {
                Vector2 velocity = new Vector2();

                velocity.x = Float.parseFloat(input.next());
                velocity.y = Float.parseFloat(input.next());
                initialVelocities.add(velocity);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Malformed input. Aborting scan.");
                return;
            }
        }
    }

    static {
        scan();
    }

    public static void main(String[] args) {
        System.out.println(initialVelocities);
    }
}
