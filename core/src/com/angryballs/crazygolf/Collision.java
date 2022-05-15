package com.angryballs.crazygolf;

import com.badlogic.gdx.math.Vector2;

public class Collision {

    public final static float ballDiameter = 0.04267f;
    public final static float ballRadius = ballDiameter/2;

    public final static float logDiameter = 0.5f;
    public final static float logRadius = logDiameter/2;

    public static boolean collisionDetector(Vector2 treePos, Vector2 ballPos) {


        float sumOfRadius = ballRadius + logRadius;
        System.out.println(sumOfRadius);

        float xDist = Math.abs(treePos.x - ballPos.x);
        float yDist = Math.abs(treePos.y - ballPos.y);
        float trueDist = (float) Math.sqrt(xDist*xDist + yDist*yDist);
        System.out.println(trueDist);

            if (sumOfRadius >= trueDist){
                return true;
            }

            else return false;
    }

    public static void main(String[] args) {

        Vector2 logCoords = new Vector2(1, 2);
        Vector2 ballCoords  = new Vector2(2,3);

        System.out.println(collisionDetector(logCoords, ballCoords));

    }
}

