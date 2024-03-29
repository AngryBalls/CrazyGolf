package com.angryballs.crazygolf.AI.Pathfinding;

import java.util.List;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.BallModel;
import com.badlogic.gdx.math.Circle;
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

    public final List<Vector2> path;

    private final LevelInfo levelInfo;

    public Path(List<Vector2> path, LevelInfo levelInfo) {
        this.levelInfo = levelInfo;
        this.path = path;
        distance = path.size();
        distance *= distance;
    }

    public float distanceToEnd(float progress) {
        // System.out.println("Distance to end: " + ((1 - progress) * distance));
        return (1 - progress) * distance;
    }

    public NodeInfo closestIntersectPoint(Vector2 position) {
        float shortestDistance = Float.MAX_VALUE;
        int intersectPointIndex = 0;
        for (int i = 0; i < path.size(); i++) {
            var pathNode = path.get(i);
            var pathNodeCorrectedPos = new Vector2(pathNode.x - 64 + .5f, pathNode.y - 64 + .5f);

            var distance = pathNodeCorrectedPos.dst2(position);

            // System.out.println("Corrected distance: " + distance);
            if (distance > shortestDistance)
                continue;

            // Do a line of sight check (in case the node is behind a wall)
            var deltaVector = new Vector2(pathNodeCorrectedPos).sub(position).scl(0.01f);
            var currentPosition = new Vector2(position);

            boolean intersectingWall = false;

            for (int j = 0; j < 100; ++j) {
                currentPosition.add(deltaVector);
                var ballCirc = new Circle(currentPosition, BallModel.ballRadius);
                for (Rectangle wall : levelInfo.walls)
                    if (Intersector.overlaps(ballCirc, wall)) {
                        intersectingWall = true;
                        break;
                    }
                if (intersectingWall)
                    break;
            }

            // This is an optimization step
            // If the current position behind a wall, we assume all future points on the
            // graph is behind the wall.
            if (intersectingWall)
                continue;

            shortestDistance = distance;
            intersectPointIndex = i;
        }
        float progress = intersectPointIndex / (float) (path.size() - 1);

        return new NodeInfo(intersectPointIndex, progress,
                new Vector2(path.get(intersectPointIndex)).sub(63.5f, 63.5f),
                distanceToEnd(progress));
    }
}
