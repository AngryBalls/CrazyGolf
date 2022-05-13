package com.angryballs.crazygolf.AI;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Physics.EulersPhysicsEngine;
import com.angryballs.crazygolf.Physics.GRK2PhysicsEngine;
import com.angryballs.crazygolf.Physics.PhysicsEngine;
import com.angryballs.crazygolf.Physics.RK4PhysicsEngine;
import com.badlogic.gdx.math.Vector2;

public class BotTests {
    public final static LevelInfo INFO = LevelInfo.exampleInput;

    public static void main(String[] args) {
        // Bot rbb = new RuleBasedBot(INFO);
        // rbb.run(rbb);
        System.out.println("\nGradient Descent");
        Bot gd = new GradientDescent(INFO);
        //gd.computeOptimalMove(-3,0);

//        gd.ps.setStateVector(-3,0,0,0);
//        gd.performMove(new Vector2(3.78f, 1.43f));
//        gd.ps.printStateVector();

//        System.out.println("\nNewton Raphson");
//        Bot nr = new NewtonRaphson(INFO);
//        nr.run();

//        System.out.println("\nHill Climbing");
//        Bot hc = new HillClimbing(INFO);
//        hc.run();

        PhysicsEngine ps = new RK4PhysicsEngine(LevelInfo.exampleInput);
        ps.performMove(new Vector2(1,0));
        while (ps.iterate()==0){
            ps.iterate();
        }
        ps.printStateVector();


    }
}
