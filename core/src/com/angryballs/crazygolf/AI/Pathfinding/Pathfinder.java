package com.angryballs.crazygolf.AI.Pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.angryballs.crazygolf.LevelInfo;
import com.badlogic.gdx.math.Vector2;

public class Pathfinder {

    private static boolean[][] createSearchSpace(LevelInfo levelInfo) {
        var grid = new boolean[128][128];

        for (int y = 0; y < 128; ++y) {
            int y2 = y - 64;
            for (int x = 0; x < 128; ++x) {
                int x2 = x - 64;

                var topLeft = levelInfo.heightProfile(x2, y2) < 0 ? 1 : 0;
                var topRight = levelInfo.heightProfile(x2 + 1, y2) < 0 ? 1 : 0;
                var bottomLeft = levelInfo.heightProfile(x2, y2 + 1) < 0 ? 1 : 0;
                var bottomRight = levelInfo.heightProfile(x2 + 1, y2 + 1) < 0 ? 1 : 0;
                var centre = levelInfo.heightProfile(x2 + 0.5, y2 + 0.5) < 0 ? 1 : 0;

                var mostlyWater = (topLeft + topRight + bottomLeft + bottomRight + centre) > 2;
                if (mostlyWater) {
                    grid[y][x] = true;
                    continue;
                }

                for (var wall : levelInfo.walls) {
                    if (wall.contains(new Vector2((float) (x2 + 0.5), (float) (y2 + 0.5)))) {
                        grid[y][x] = true;
                        continue;
                    }
                }
            }
        }

        return grid;
    }

    public static Path findPath(LevelInfo levelInfo) {
        var searchSpace = createSearchSpace(levelInfo);
        int xs = (int) (levelInfo.startPosition.x);
        int ys = (int) (levelInfo.startPosition.y);
        var start = new Vector2(xs + 64, ys + 64);

        int xt = (int) (levelInfo.endPosition.x);
        int yt = (int) (levelInfo.endPosition.y);
        var goal = new Vector2(xt + 64, yt + 64);

        var frontier = new LinkedList<Vector2>();
        frontier.push(start);

        var arrivalOrigin = new HashMap<Vector2, Vector2>();
        arrivalOrigin.put(start, null);

        var reached = new HashSet<Vector2>();
        reached.add(start);

        // Do a full traversal first
        while (!frontier.isEmpty()) {
            var current = frontier.poll();
            var neighbours = new Vector2[] {
                    new Vector2(current).add(1, 0),
                    new Vector2(current).add(0, 1),
                    new Vector2(current).add(-1, 0),
                    new Vector2(current).add(0, -1),
            };

            for (Vector2 neighbour : neighbours) {
                if (neighbour.x < 0 || neighbour.x > 127 || neighbour.y < 0 || neighbour.y > 127)
                    continue;

                if (searchSpace[(int) neighbour.y][(int) neighbour.x])
                    continue;

                if (arrivalOrigin.containsKey(neighbour))
                    continue;

                frontier.add(neighbour);
                arrivalOrigin.put(neighbour, current);
            }
        }

        // Reconstruct the path
        var current = goal;
        var pathNodes = new ArrayList<Vector2>();

        while (!current.equals(start)) {
            pathNodes.add(current);
            var next = arrivalOrigin.get(current);
            if (next == null)
                return null;
            current = arrivalOrigin.get(current);
        }

        pathNodes.add(start);
        Collections.reverse(pathNodes);

        var path = new Path(pathNodes, levelInfo);

        return path;
    }

}
