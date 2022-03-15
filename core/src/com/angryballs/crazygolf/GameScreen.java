package com.angryballs.crazygolf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.*;
import java.text.Format;

public class GameScreen implements  Screen{

    final GrazyGolf game;
    int Xstart;
    int Ystart;
    int screenWidth;
    int screenHeight;
    int differentColors;
    int resolution;
    Texture golfBall;
    Texture backgroundImage;
    Rectangle ball;
    OrthographicCamera camera;
    Pixmap map;

    public GameScreen(final GrazyGolf game, int Xstart, int Ystart, int screenWidth, int screenHeight) {
        this.game = game;
        this.Xstart = Xstart;
        this.Ystart = Ystart;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.resolution = 10;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth * this.resolution, screenHeight * this.resolution);
        //this.viewport = new
        ball = new Rectangle();
        golfBall = new Texture(Gdx.files.internal("ball.png"));
        ball.width = screenWidth/10;
        ball.height = screenHeight/10;
        ball.x = screenWidth/2;
        ball.y = screenHeight/2;
        this.differentColors =100;
        System.out.println("pixels: " + screenWidth * this.resolution);
        this.map = new Pixmap(screenWidth * this.resolution, screenHeight * this.resolution, Pixmap.Format.RGBA8888);
        Create_Background();
    }

    public void setSize(int w, int h) {

    }

    /**
     *  Color for water:
     *  0-0-244
     *
     *  testing formula: 0.5*(Math.sin((x-y)/7)+0.9)
     *
     */
    public void Create_Background() {
        int biggerCount = 0;
        int smallerCount = 0;
        int equalCount = 0;
        int waterCount = 0;
        map.setColor(Color.WHITE);
        map.fill();
        Color watercolor = new Color(0,0,1,1);
        double max = find_max();
        double step = Determine_Step(max, this.differentColors);
        int half = (int) differentColors/2;
        System.out.println("half = " + half);
        for (int i = 0; i < this.screenWidth; i++) {
            for (int j = 0; j < this.screenHeight; j++) {
                for (double k = 0; k < 1 - 0.1; k = k + 0.1) {
                    for (double l = 0; l < 1 - 0.1; l = l + 0.1) {
                        double x = i+k;
                        double y = j+l;
                        int XPixel = (int) (this.resolution * i + (k * this.resolution) + 0.5);
                        int YPixel = (int) (this.resolution * j + (l * this.resolution) + 0.5);
                        double d = Calculate(x,y);
                        if (d < 0) {
                            this.map.setColor(watercolor);
                            this.map.drawPixel(XPixel, YPixel);
                            waterCount++;
                        }
                        else {
                            int colorPointer = (int) (d/step);
                            if (colorPointer == half) {
                                this.map.setColor(0,1,0,1);
                                this.map.drawPixel(XPixel, YPixel);
                                equalCount++;
                            }
                            else if (colorPointer < half) {
                                double a = half;
                                double b = colorPointer;
                                float c = (float) (55 + (200/a * b)) / 255;
                                this.map.setColor(0,c,0,1);
                                this.map.drawPixel(XPixel, YPixel);
                                smallerCount++;
                            }
                            else if (colorPointer > half) {
                                double a = half;
                                double b = colorPointer;
                                float c = (float) (200/a * (b-a)) / 255;
                                //this.map.setColor(Color.YELLOW);
                                this.map.setColor(c,1,c,1);
                                this.map.drawPixel(XPixel, YPixel);
                                biggerCount++;
                            }
                            // darkest color: 0,55,0,1
                            // middle color: 0,255,0,1
                            // lightest color: 200,255,200,1
                        }
                    }
                }
//                double d = Calculate(i, j);
//                if (d < 0) {
//                    this.map.setColor(watercolor);
//                    this.map.drawPixel(i,j);
//                }
//                else {
//                    int colorPointer = (int) (d/step);
//                    if (colorPointer == half) {
//                        this.map.setColor(0,1,0,1);
//                        this.map.drawPixel(i,j);
//                        equalCount++;
//                    }
//                    else if (colorPointer < half) {
//                        double a = half;
//                        double b = colorPointer;
//                        float c = (float) (55 + (200/a * b)) / 255;
//                        this.map.setColor(0,c,0,1);
//                        this.map.drawPixel(i,j);
//                        smallerCount++;
//                    }
//                    else if (colorPointer > half) {
//                        double a = half;
//                        double b = colorPointer;
//                        float c = (float) (200/a * (b-a)) / 255;
//                        this.map.setColor(c,1,c,1);
//                        this.map.drawPixel(i,j);
//                        biggerCount++;
//                    }
//                    // darkest color: 0,55,0,1
//                    // middle color: 0,255,0,1
//                    // lightest color: 200,255,200,1
//                }
            }
        }

        System.out.println("equal: " + equalCount);
        System.out.println("smaller: " + smallerCount);
        System.out.println("bigger: " + biggerCount);
        System.out.println("water: " + waterCount);
        backgroundImage = new Texture(map);

    }

    public double Determine_Step(double max, int amount) {
        double x = max/amount;
        x = (int) (x*1000);
        x = x / 1000;
        return x;
    }

    public double find_max() {
        double max = 0;
        for (int i = 0; i < this.screenWidth; i++) {
            for (int j = 0; j < this.screenHeight; j++) {
                double d = Calculate(i,j);
                if (d > max)
                    max = d;
            }
        }
        return max;
    }

    public double Calculate(double x, double y){
        return (0.5*(Math.sin((x-y)/7)+0.8));
    }


    public void ball_Update(int new_X, int new_Y) {
        ball.x = new_X;
        ball.y = new_Y;
    }
    public void show() {
        // set the background once:
    }

    @Override
    public void render(float delta) {

        //ScreenUtils.clear(0, 1, 0, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(backgroundImage, 0, 0);
        game.batch.draw(golfBall, ball.x,ball.y, ball.width, ball.height);
        game.batch.end();

        if (ball.x < this.screenWidth - (this.ball.width/2) && ball.y < this.screenHeight - (this.ball.height/2)) {
            ball_Update(0,0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            //resize(800,800);
        }
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }
}
