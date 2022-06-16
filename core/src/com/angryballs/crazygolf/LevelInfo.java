package com.angryballs.crazygolf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;

import com.angryballs.crazygolf.AI.Pathfinding.Path;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.w3c.dom.css.Rect;

public class LevelInfo {
    public static final LevelInfo exampleInput;

    public final Vector2 startPosition;

    public final Vector2 endPosition;

    public final float holeRadius; // ( ͡° ͜ʖ ͡°)

    public final Vector2[] sandPitBounds = new Vector2[2];

    public final float grassKineticFrictionCoeff;
    public final float grassStaticFrictionCoeff;

    public final float sandKineticFrictionCoeff;
    public final float sandStaticFrictionCoeff;

    public final ArrayList<Rectangle> walls = new ArrayList<>();
    public final ArrayList<Rectangle> originalWalls = new ArrayList<>();

    private final String heightProfile;

    public final ArrayList<Vector2> trees = new ArrayList<>();
    public final ArrayList<Vector2> originalTrees = new ArrayList<>();

    public Path optimalPath = null;

    // Older usage of the script engine, which always interpreted the heightProfile
    // Discontinued in favor of the new method, which uses a precompiled profile
    private Double heightProfileOld(double x, double y) {
        engine.put("x", x);
        engine.put("y", y);

        try {
            return ((Double) engine.eval(heightProfile));
        } catch (Exception e) {
            System.out.println(e);
        }

        return 0.0;
    }

    // Storage to avoid updating bindings for the same (x,y) pair
    private Double lastX = null;
    private Double lastY = null;

    public Double heightProfile(double x, double y) {
        if (lastX == null || lastX != x) {
            bindings.put("x", x);
            lastX = x;
        }

        if (lastY == null || lastY != y) {
            bindings.put("y", y);
            lastY = y;
        }

        try {
            return ((Double) expression.eval(bindings));
        } catch (Exception e) {
            System.out.println(e);
        }

        return 0.0;
    }

    public Double heightProfileNative(double x, double y) {
        return 0.5 * (Math.sin((x - y) / 7) + 0.9);
    }

    private static ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();

    private final CompiledScript expression;
    private final Bindings bindings;

    public LevelInfo(File file) throws FileNotFoundException, IOException {
        Properties props = new Properties();

        props.load(new FileInputStream(file));

        try {
            startPosition = new Vector2(Float.parseFloat(props.getProperty("x0", "0")),
                    Float.parseFloat(props.getProperty("y0", "0")));

            endPosition = new Vector2(Float.parseFloat(props.getProperty("xt", "0")),
                    Float.parseFloat(props.getProperty("yt", "0")));

            holeRadius = Float.parseFloat(props.getProperty("r", "0"));

            grassKineticFrictionCoeff = Float.parseFloat(props.getProperty("muk", "0"));
            grassStaticFrictionCoeff = Float.parseFloat(props.getProperty("mus", "0"));

            sandKineticFrictionCoeff = Float.parseFloat(props.getProperty("muks", "0"));
            sandStaticFrictionCoeff = Float.parseFloat(props.getProperty("muss", "0"));

            var sandPitX = props.getProperty("sandPitX").split("<x<");
            var sandPitY = props.getProperty("sandPitY").split("<y<");
            sandPitBounds[0] = new Vector2(Float.parseFloat(sandPitX[0]), Float.parseFloat(sandPitY[0]));
            sandPitBounds[1] = new Vector2(Float.parseFloat(sandPitX[1]), Float.parseFloat(sandPitY[1]));

            var treeString = props.getProperty("trees", "");

            if (!treeString.equals("")) {
                var treesSplit = treeString.split(";");

                for (String treePos : treesSplit) {
                    var posArr = treePos.substring(1, treePos.length() - 1).split(",");

                    var treeVec = new Vector2(Float.parseFloat(posArr[0]), Float.parseFloat(posArr[1]));

                    trees.add(treeVec);
                    originalTrees.add(treeVec);
                }
            }

            var wallString = props.getProperty("walls", "");

            if (!wallString.equals("")) {
                var wallsSplit = wallString.split(";");

                for (String wallRectString : wallsSplit) {
                    var rectInfo = wallRectString.substring(1, wallRectString.length() - 1).split(",");

                    float x = Float.parseFloat(rectInfo[0]);
                    float y = Float.parseFloat(rectInfo[1]);
                    float w = Float.parseFloat(rectInfo[2]);
                    float h = Float.parseFloat(rectInfo[3]);

                    var wallRect = new Rectangle(x, y, w, h);
                    walls.add(wallRect);
                    originalWalls.add(wallRect);
                }
            }

            heightProfile = props.getProperty("heightProfile", "0");
            expression = ((Compilable) engine).compile(heightProfile);
            bindings = engine.createBindings();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Malformed input file");
        }
    }

    public void reload() {
        trees.clear();
        for (var tree : originalTrees)
            trees.add(tree);

        walls.clear();
        for (var wall : originalWalls)
            walls.add(wall);
    }

    public void save() {
        Properties props = new Properties();

        props.put("x0", String.format(Locale.US, "%f", startPosition.x));
        props.put("y0", String.format(Locale.US, "%f", startPosition.y));

        props.put("xt", String.format(Locale.US, "%f", endPosition.x));
        props.put("yt", String.format(Locale.US, "%f", endPosition.y));

        props.put("r", String.format(Locale.US, "%f", holeRadius));

        props.put("muk", String.format(Locale.US, "%f", grassKineticFrictionCoeff));
        props.put("mus", String.format(Locale.US, "%f", grassStaticFrictionCoeff));

        props.put("muks", String.format(Locale.US, "%f", sandKineticFrictionCoeff));
        props.put("muss", String.format(Locale.US, "%f", sandStaticFrictionCoeff));

        props.put("sandPitX", String.format(Locale.US, "%f<x<%f", sandPitBounds[0].x, sandPitBounds[1].x));
        props.put("sandPitY", String.format(Locale.US, "%f<y<%f", sandPitBounds[0].y, sandPitBounds[1].y));

        props.put("heightProfile", heightProfile);

        String treeString = "";
        for (Vector2 tree : trees) {
            treeString = String.format(Locale.US, "%s(%f,%f);", treeString, tree.x, tree.y);
        }

        String wallString = "";
        for (Rectangle wall : walls) {
            wallString = String.format(Locale.US, "%s(%f,%f,%f,%f);", wallString, wall.x, wall.y, wall.width,
                    wall.height);
        }
        props.put("walls", wallString);

        try {
            var time = new Timestamp(System.currentTimeMillis());
            var writer = new FileWriter(
                    new File(String.format("LevelInfoSave %s.txt",
                            time.toString().replace(':', ' ').substring(0, 16))));

            props.store(writer, "This is automatically generated by CrazyGolf. Manual modification may cause problems");
        } catch (Exception e) {
            System.out
                    .println("[ERROR] Can't save levelinfo. This is likely due to not being able to create the file.");
            e.printStackTrace();
        }
    }

    static {
        LevelInfo tmp = null;
        try {
            tmp = new LevelInfo(new File("example_inputfile.txt"));
        } catch (Exception e) {
            System.err.println("example file is either malformed or nonexistent");
        }
        exampleInput = tmp;
    }

    public static void main(String[] args) {
        var lvlInfo = LevelInfo.exampleInput;

        double avgA = 0;
        double avgB = 0;
        double avgNative = 0;
        {
            var result = lvlInfo.heightProfileOld(0, 1);
            System.out.println(result);
        }
        {
            var result = lvlInfo.heightProfile(0, 1);
            System.out.println(result);
        }
        {
            double res = 0;
            long duration = 0;
            for (int i = 0; i < 100000; i++) {
                long startTime = System.currentTimeMillis();
                res += lvlInfo.heightProfileOld(i, 1);
                long endTime = System.currentTimeMillis();
                duration += endTime - startTime;
            }
            avgA = duration / (float) 100000;
            System.out.println("Total time: " + String.format("%.8f", (duration / 1000.0)));
            System.out.println(res);
        }

        {
            double res = 0;
            long duration = 0;
            for (int i = 0; i < 100000; i++) {
                long startTime = System.currentTimeMillis();
                res += lvlInfo.heightProfile(0, i);

                long endTime = System.currentTimeMillis();
                duration += endTime - startTime;
            }
            avgB = duration / (float) 100000;
            System.out.println("Total time: " + String.format("%.8f", (duration / 1000.0)));
            System.out.println(res);
        }

        {
            double res = 0;
            long duration = 0;
            for (int i = 0; i < 100000; i++) {
                long startTime = System.currentTimeMillis();
                res += lvlInfo.heightProfileNative(i, i);

                long endTime = System.currentTimeMillis();
                duration += endTime - startTime;

            }
            System.out.println(res);
            System.out.println("Total time: " + String.format("%.8f", (duration / 1000.0)));
            avgNative = duration / (float) 100000;
        }

        System.out.println(
                "(Original) Avg Running time over " + 100000 + " runs : "
                        + String.format("%.8f", avgA / 1000) + "s");
        System.out.println(
                "(Improved) Avg Running time over " + 100000 + " runs : "
                        + String.format("%.8f", avgB / 1000) + "s");
        System.out.println(
                "(Native)   Avg Running time over " + 100000 + " runs : "
                        + String.format("%.8f", avgNative / 1000) + "s");
    }
}
