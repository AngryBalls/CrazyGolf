package com.angryballs.crazygolf.Physics;

import com.angryballs.crazygolf.LevelInfo;
import com.angryballs.crazygolf.Models.TreeModel;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class MultiStepEngine extends PhysicsEngine {
    private int iterationNo;
    private Vector2 preVel;
    private Vector2 prePos;

    public MultiStepEngine(LevelInfo info, List<TreeModel> trees) {
        super(info, trees);
        preVel = new Vector2(0, 0);
        prePos = info.startPosition;
    }

    @Override
    protected void performCalculations(Vector2 derivative, float h) {
        iterationNo++;
        if (iterationNo == 1) {
            preVel = new Vector2((float) this.vx, (float) this.vy);
            var res1 = rk2ForPos(h);
            this.x += h * res1.x;
            this.y += h * res1.y;

            var res2 = rk2ForVel(derivative, h);
            this.vx += h * res2.x;
            this.vy += h * res2.y;
        } else {
            var res = ab(derivative, h);
            prePos = new Vector2((float) this.x, (float) this.y);
            preVel = new Vector2((float) this.vx, (float) this.vy);

            this.x += h * res[0].x;
            this.y += h * res[0].y;

            this.vx += h * res[1].x;
            this.vy += h * res[1].y;
        }
    }

    private Vector2 rk2ForPos(float h) {
        Vector2 derivative = derivative(x + 2 / 3 * h * vx, y + 2 / 3 * h * vy);

        Vector2 acceleration = acceleration(derivative);

        double x = 0.25 * vx + 0.75 * (vx + acceleration.x * 2 / 3 * h);
        double y = 0.25 * vy + 0.75 * (vy + acceleration.y * 2 / 3 * h);
        // vy + acce * (0+ah)

        return new Vector2((float) x, (float) y);
    }

    private Vector2 rk2ForVel(Vector2 derivative, float h) {
        var w = acceleration(derivative);

        // This needs cleaning
        var accelerationX = accelerationX(2 / 3 * h * w.x, derivative);
        var accelerationY = accelerationY(2 / 3 * h * w.y, derivative);

        double x = 0.25 * w.x + 0.75 * accelerationX;
        double y = 0.25 * w.y + 0.75 * accelerationY;

        return new Vector2((float) x, (float) y);
    }

    private Vector2[] ab(Vector2 derivative, float h) {
        double vx = (h / 2) * (3 * accelerationX(0, derivative) - accelerationX(0, preVel));
        double vy = (h / 2) * (3 * accelerationY(0, derivative) - accelerationY(0, preVel));

        double x = (h / 12) * (5 * (this.vx + vx) + 8 * this.vx - preVel.x);
        double y = (h / 12) * (5 * (this.vy + vy) + 8 * this.vy - preVel.y);

        vx = (h / 12) * (5 * accelerationX(0, derivative(x, y)) + 8 * accelerationX(0, derivative)
                - accelerationX(0, derivative(prePos.x, prePos.y)));
        vy = (h / 12) * (5 * accelerationY(0, derivative(x, y)) + 8 * accelerationY(0, derivative)
                - accelerationY(0, derivative(prePos.x, prePos.y)));


        x = (h / 12) * (5 * (this.vx + vx) + 8 * this.vx - preVel.x);
        y = (h / 12) * (5 * (this.vy + vy) + 8 * this.vy - preVel.y);

        return new Vector2[]{new Vector2((float) x, (float) y), new Vector2((float) vx, (float) vy)};
    }

    @Override
    protected void reset() {
        super.reset();
        iterationNo = 0;
    }


}
