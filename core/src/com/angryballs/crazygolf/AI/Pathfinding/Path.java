package com.angryballs.crazygolf.AI.Pathfinding;

import java.util.ArrayList;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.BallModel;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Path {
    public static class NodeInfo {
        public final int index;
        public final float progress;
        public final Vector2 position;
        public final float distanceToEnd;

        private NodeInfo(int index, float progress, Vector2 position, float distanceToEnd) {
            this.index = index;
            this.progress = progress;
            this.position = position;
            this.distanceToEnd = distanceToEnd;
        }
    }

    private int distance;

    private ArrayList<Vector2> path;

    private LevelInfo levelInfo;

    public float distanceToEnd(float progress) {
        return 1 - progress * distance;
    }

    public NodeInfo closestIntersectPoint(Vector2 position) {
        float shortestDistance = Float.MAX_VALUE;
        int intersectPointIndex = 0;
        for (int i = 0; i < path.size(); i++) {
            var pathNode = path.get(i);

            var distance = pathNode.dst2(position);
            if (distance > shortestDistance)
                continue;

            // Do a line of sight check (in case the node is behind a wall)
            var deltaVector = new Vector2(pathNode).sub(position).nor();
            var currentPosition = new Vector2(position);

            boolean intersectingWall = false;

            // This is commented until walls are merged into master
            /*
             * while (!currentPosition.epsilonEquals(pathNode))
             * for (Rectangle wall : levelInfo.walls)
             * if (Intersector.overlaps(new Circle(currentPosition, BallModel.ballRadius),
             * wall)) {
             * intersectingWall = true;
             * break;
             * }
             */

            // This is an optimization step
            // If the current position behind a wall, we assume all future points on the
            // graph is behind the wall.
            if (intersectingWall)
                break;

            shortestDistance = distance;
            intersectPointIndex = i;
        }
        float progress = intersectPointIndex / (float) path.size();

        return new NodeInfo(intersectPointIndex, progress,
                path.get(intersectPointIndex),
                distanceToEnd(progress));
    }
}
