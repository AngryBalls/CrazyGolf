package com.angryballs.crazygolf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.badlogic.gdx.math.Vector2;

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

    private final String heightProfile;

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
            return ((Double) expression.eval());
        } catch (Exception e) {
            System.out.println(e);
        }

        return 0.0;
    }

    public Double heightProfileNative(double x, double y) {
        return 0.5 * (Math.sin((x - y) / 7) + 0.9);
    }

    private static ScriptEngineManager mgr = new ScriptEngineManager();
    private static ScriptEngine engine = mgr.getEngineByName("JavaScript");

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

            heightProfile = props.getProperty("heightProfile", "0");
            expression = ((Compilable) engine).compile(heightProfile);
            bindings = engine.createBindings();

            return;
        } catch (Exception e) {
            throw new IOException("Malformed input file");
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
            var result = lvlInfo.heightProfileOld(0, 0);
            System.out.println(result);
        }
        {
            var result = lvlInfo.heightProfile(0, 0);
            System.out.println(result);
        }
        {
            double res = 0;
            long duration = 0;
            for (int i = 0; i < 100000; i++) {
                long startTime = System.currentTimeMillis();
                res += lvlInfo.heightProfileOld(0, 1);
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
                res += lvlInfo.heightProfile(0, 1);

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
                res += lvlInfo.heightProfileNative(0, 1);

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
