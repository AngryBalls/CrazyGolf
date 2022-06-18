package com.angryballs.crazygolf.Physics;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Tests {
    public static void main(String[] args) {
        testRK();

    }
    /**
     * Test data: height function: 0.1*x+1
     * x0 = 0, y0 = 0, muk = 0.05
     */
    public static void testAnkie(){
        Vector2 initV = new Vector2(2,0);
        int iteCount = 1;
        long totalMS = 0;
        double exact = 1.35915732;

        for (int q = 0; q < iteCount; ++q) {
            PhysicsEngine sv5 = new RK4PhysicsEngine(LevelInfo.exampleInput, new ArrayList<>());
            long startTime = System.currentTimeMillis();
            sv5.performMove(initV);
            int code5 = sv5.iterate();
            while (code5 == 0) {
                code5 = sv5.iterate();
            }
            long endTime = System.currentTimeMillis();
            totalMS += (endTime - startTime);
            if (q == iteCount - 1) {
                System.out.println("RK4: X: " + sv5.getX() + ", Y: " + sv5.getY());
                System.out.print("Absolute error: " + Math.abs(sv5.getX() - exact));
            }
        }

        double avgMs = totalMS / (float) iteCount;
        System.out.println(", Running time: " + String.format("%.6f", avgMs) + "ms ");

        totalMS = 0;
        for (int q = 0; q < iteCount; ++q) {
            PhysicsEngine sv = new EulersPhysicsEngine(LevelInfo.exampleInput, new ArrayList<>());
            long startTime = System.currentTimeMillis();
            sv.performMove(initV);
            int code = sv.iterate();
            while (code == 0) {
                code = sv.iterate();
            }
            long endTime = System.currentTimeMillis();
            totalMS += (endTime - startTime);
            if (q == iteCount - 1) {
                System.out.println("Euler: X: " + sv.getX() + ", Y: " + sv.getY());
                System.out.print("Absolute error: " + Math.abs(sv.getX() - exact));
            }
        }
        avgMs = totalMS / (float) iteCount;
        System.out.println(", Running time: " + String.format("%.6f", avgMs) + "ms ");


        totalMS = 0;
        for (int q = 0; q < iteCount; ++q) {
            PhysicsEngine sv2 = new GRK2PhysicsEngine(LevelInfo.exampleInput, new ArrayList<>());
            long startTime = System.currentTimeMillis();
            sv2.performMove(initV);
            int code2 = sv2.iterate();
            while (code2 == 0) {
                code2 = sv2.iterate();
            }
            long endTime = System.currentTimeMillis();
            totalMS += (endTime - startTime);

            if (q == iteCount - 1) {
                System.out.println("RK2-Mid:X: " + sv2.getX() + ", Y: " + sv2.getY());
                System.out.print("Absolute error: " + Math.abs(sv2.getX() - exact));
            }
        }
        avgMs = totalMS / (float) iteCount;
        System.out.println(", Running time: " + String.format("%.6f", avgMs) + "ms ");

        totalMS = 0;
        for (int q = 0; q < iteCount; ++q) {
            PhysicsEngine sv2 = new GRK2PhysicsEngine(LevelInfo.exampleInput, new ArrayList<>());
            long startTime = System.currentTimeMillis();
            sv2.performMove(initV);
            int code2 = sv2.iterate();
            while (code2 == 0) {
                code2 = sv2.iterate();
            }
//            for(int i = 0;i<5;i++){
//                code2 = sv2.iterate();
//            }
            long endTime = System.currentTimeMillis();
            totalMS += (endTime - startTime);

            if (q == iteCount - 1) {
                System.out.println("AB:X: " + sv2.getX() + ", Y: " + sv2.getY());
                System.out.print("Absolute error: " + Math.abs(sv2.getX() - exact));
            }
        }
        avgMs = totalMS / (float) iteCount;
        System.out.println(", Running time: " + String.format("%.6f", avgMs) + "ms ");

    }

    public static void testRK(){
        PhysicsEngine e1 = new GRK2PhysicsEngine(LevelInfo.exampleInput,new ArrayList<>());
        PhysicsEngine e2 = new RK2(LevelInfo.exampleInput,new ArrayList<>());
        e1.setStateVector(0,-1,0,0);
        e2.setStateVector(0,-1,0,0);
        e1.performMove(new Vector2(1,0));
        e2.performMove(new Vector2(1,0));

        double t = 0.0;
        while(e1.iterate()==0){
            e1.iterate();
            t+=0.001f;
        }
        System.out.println("Current PE: ");
        e1.printStateVector();
        System.out.println(t);
        System.out.println();
        System.out.println("New PE:");
        t = 0.0;
        while(e2.iterate()==0){
            e2.iterate();
            t+=0.001f;
        }
        e2.printStateVector();
        System.out.println(t);

    }
}
