package com.angryballs.crazygolf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.badlogic.gdx.math.Vector2;

public class LevelInfo {
    public final Vector2 startPosition;

    public final Vector2 endPosition;

    public final float holeRadius; // ( ͡° ͜ʖ ͡°)

    public final Vector2[] sandPitBounds = new Vector2[2];

    public final float grassKineticFrictionCoeff;
    public final float grassStaticFrictionCoeff;

    public final float sandKineticFrictionCoeff;
    public final float sandStaticFrictionCoeff;

    private final String heightProfile;

    public float heightProfile(Vector2 coord) {
        engine.put("x", coord.x);
        engine.put("y", coord.y);

        try {
            return ((Double) engine.eval(heightProfile)).floatValue();
        } catch (Exception e) {
            System.out.println(e);
        }

        return 0;
    }

    private static ScriptEngineManager mgr = new ScriptEngineManager();
    private static ScriptEngine engine = mgr.getEngineByName("JavaScript");

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

            return;
        } catch (Exception e) {
            throw new IOException("Malformed input file");
        }
    }
}
