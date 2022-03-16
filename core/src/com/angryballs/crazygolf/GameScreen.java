package com.angryballs.crazygolf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;

public class GameScreen implements  Screen{

    final GrazyGolf game;

    int screenWidth;
    int screenHeight;
    int differentColors;
    int resolution;
    Texture golfBall;
    Texture backgroundImage;
    Texture water;
    Rectangle ball;
    OrthographicCamera camera;
    Pixmap map;

    public GameScreen(final GrazyGolf game, int Xstart, int Ystart, int screenWidth, int screenHeight, int resolution) {
        this.game = game;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.resolution = resolution;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth * this.resolution, screenHeight * this.resolution);
        ball = new Rectangle();
        golfBall = new Texture(Gdx.files.internal("ball.png"));
        water = new Texture(Gdx.files.internal("water.jpg"));
        ball.width = screenWidth/10;
        ball.height = screenHeight/10;
        ball.x = screenWidth/2 * resolution;
        ball.y = screenHeight/2 * resolution;
//        ball.x = Xstart * resolution;
//        ball.y = Ystart * resolution;
        // set the amount of shades of green that will appear on the screen
        this.differentColors =100;
        this.map = new Pixmap(screenWidth * this.resolution, screenHeight * this.resolution, Pixmap.Format.RGBA8888);
        Create_Background();
    }

    public void Create_Background() {
        int biggerCount = 0;
        int smallerCount = 0;
        int equalCount = 0;
        int waterCount = 0;
        double max = find_max();
        double step = Determine_Step(max, this.differentColors);
        int half = (int) differentColors/2;
        System.out.println("half = " + half);
        for (int i = 0; i < this.screenWidth; i++) {
            for (int j = 0; j < this.screenHeight; j++) {
                for (int k = 0; k < this.resolution; k++) {
                    for (int l = 0; l < this.resolution; l++){
                        double tx = k;
                        double ty = l;
                        double x = i + (tx/this.resolution);
                        double y = j + (ty/this.resolution);
                        int XPixel = (this.resolution * i) + k;
                        int YPixel = (this.resolution * j) + l;
                        double d = Calculate(x,y);
                        if (d > 0) {
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
        return (0.5*(Math.sin((x-y)/7)+0.5));
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
        Sprite sprite = new Sprite(water);
        sprite.setSize(this.screenWidth * this.resolution, this.screenHeight*this.resolution);
        game.batch.draw(sprite.getTexture(), 0,0, sprite.getWidth(), sprite.getHeight());
        game.batch.draw(backgroundImage, 0, 0);
        game.batch.draw(golfBall, ball.x,ball.y, ball.width * this.resolution, ball.height * this.resolution);
        game.batch.end();

        if (ball.x < this.screenWidth - (this.ball.width/2) && ball.y < this.screenHeight - (this.ball.height/2)) {
            ball_Update(0,0);
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
        golfBall.dispose();
        map.dispose();
        backgroundImage.dispose();
    }
}
