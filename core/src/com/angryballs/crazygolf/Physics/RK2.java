package com.angryballs.crazygolf.Physics;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class RK2 extends PhysicsEngine {
    private static float alpha = 0.5f;

    public RK2(LevelInfo levelInfo, List<TreeModel> trees) {
        super(levelInfo, trees);
    }

    @Override
    protected void performCalculations(Vector2 derivative, float h) {

        double[] stateVector = new double[4];
        stateVector[0] = x;
        stateVector[1] = y;
        stateVector[2] = vx;
        stateVector[3] = vy;
        double[] upd = updateStateVector(stateVector, h);
        x = upd[0];
        y = upd[1];
        vx = upd[2];
        vy = upd[3];
    }

    private double[] updateStateVector(double[] initialVector, float h) {
        double[] stateVector = new double[4];

        stateVector[0] = initialVector[0];
        stateVector[1] = initialVector[1];
        stateVector[2] = initialVector[2];
        stateVector[3] = initialVector[3];

        double[] k1 = new double[4];
        double[] k2 = new double[4];

        k1[0] = h * stateVector[2];
        k1[1] = h * stateVector[3];
        k1[2] = h * acceleration(stateVector).x;
        k1[3] = h * acceleration(stateVector).y;


        for ( int i = 0; i < stateVector.length; i++ ) {
            stateVector[i] += k1[i];
            //System.out.println("k1["+i+"] "+k1[i]);
        }
        //System.out.println();

        k2[0] = h * ( initialVector[2] + acceleration(stateVector).x * h / 2 );
        k2[1] = h * ( initialVector[3] + acceleration(stateVector).y * h / 2 );
        double[] v = new double[4];
        for ( int i = 0; i < stateVector.length; i++ ) {
            v[i] += stateVector[i];
        }
        v[2] = initialVector[2] + h / 2 * k1[2];
        v[3] = initialVector[3] + h / 2 * k1[3];
//        for ( int i = 0; i < stateVector.length; i++ ) {
//            System.out.println(v[i]);
//        }
        //System.out.println();
        k2[2] = h * ( acceleration(v).x * h / 2 );
        k2[3] = h * ( acceleration(v).y * h / 2 );

        for ( int i = 0; i < stateVector.length; i++ ) {
            stateVector[i] = initialVector[i] + k2[i];
            //System.out.println("("+initialVector[i]+" : "+stateVector[i]+") ");
        }
        //System.out.println();
        return stateVector;

    }
}
