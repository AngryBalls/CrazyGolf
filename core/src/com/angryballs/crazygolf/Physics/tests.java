package com.angryballs.crazygolf.Physics;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;


public class tests {
    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            PhysicsEngine.tan = 0 + i * 0.1; //0, 0.1, 0.2 ... 5
            System.out.println(test());
        }

        PhysicsEngine.tan = 1000000000;
        System.out.println(test());
    }

    public static double test() {
        Vector2 initV = new Vector2(5, 0);
        double time = 0.1;
        double a = -9.81 * Math.sin(Math.atan(PhysicsEngine.tan)) - 0.1 * 9.81 * Math.cos(Math.atan(PhysicsEngine.tan));
        double exact = 0.5 + 0.005 * a;

        PhysicsEngine.useNewPhysics = 1;
        PhysicsEngine sv1 = new GRK2PhysicsEngine(LevelInfo.exampleInput);
        sv1.performMove(initV);
        for (int i = 0; i < time / 0.001; i++) {
            sv1.iterate();
        }
        double newError = Math.abs(sv1.getX() - exact);

        PhysicsEngine.useNewPhysics = 0;
        PhysicsEngine sv2 = new GRK2PhysicsEngine(LevelInfo.exampleInput);
        sv2.performMove(initV);
        for (int i = 0; i < time / 0.001; i++) {
            sv2.iterate();
        }
        double oldError = Math.abs(sv2.getX() - exact);

        System.out.println("Tan: " + PhysicsEngine.tan + " New error: " + newError + " Old Error: " + oldError + " Exact: " + exact);
        return newError - oldError;
    }
}
