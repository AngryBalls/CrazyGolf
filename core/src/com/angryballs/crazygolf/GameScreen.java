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
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;

public class GameScreen implements  Screen{

    final GrazyGolf game;
    int screenWidth;
    int screenHeight;
    int differentColors;
    int resolution;
    Texture golfBall;
    Texture water;
    Rectangle ball;
    OrthographicCamera camera;
    Pixmap map;
    int XChanged;
    int YChanged;
    int spriteDraw;
    ArrayList<Sprite> mapStorage;
    ArrayList<Integer> xCoordinate;
    ArrayList<Integer> yCoordinate;

    public GameScreen(final GrazyGolf game, int Xstart, int Ystart, int screenWidth, int screenHeight, int resolution) {
        mapStorage = new ArrayList<>();
        xCoordinate = new ArrayList<>();
        yCoordinate = new ArrayList<>();
        this.game = game;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.resolution = resolution;
        this.spriteDraw = 0;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth * this.resolution, screenHeight * this.resolution);
        ball = new Rectangle();
        golfBall = new Texture(Gdx.files.internal("ball.png"));
        water = new Texture(Gdx.files.internal("water.jpg"));
        ball.width = (float) screenWidth/15;
        ball.height = (float) screenHeight/15;
        ball.x = Xstart * resolution;
        ball.y = Ystart * resolution;
        this.differentColors =100;
        this.XChanged = 0;
        this.YChanged = 0;
        this.map = new Pixmap(screenWidth * this.resolution, screenHeight * this.resolution, Pixmap.Format.RGBA8888);
        Create_Background(0,0, -1);
    }

    public int Check_Background(int x, int y) {
        for (int i = 0; i < mapStorage.size(); i++) {
            if (xCoordinate.get(i) == x && yCoordinate.get(i) == y) {
                System.out.println(x + ", " + y + ", " + i);
                return i;
            }
        }
        return -1;
    }

    public void Create_Background(int Xpos, int Ypos, int arrayPos) {
        map.setColor(Color.CLEAR);
        map.fill();
        if (arrayPos == -1) {
            double max = find_max();
            double step = Determine_Step(max, this.differentColors);
            int half = differentColors / 2;
            for (int i = 0; i < this.screenWidth; i++) {
                for (int j = 0; j < this.screenHeight; j++) {
                    for (int k = 0; k < this.resolution; k++) {
                        for (int l = 0; l < this.resolution; l++) {
                            double x = (i + ( (double) k/ this.resolution)) + (XChanged * (screenWidth));
                            double y = (j + ( (double) l / this.resolution) + (YChanged * screenHeight));
                            int XPixel = (this.resolution * i) + k;
                            int YPixel = (this.resolution * j) + l;
                            double d = Calculate(x, y);
                            if (d > 0) {
                                int colorPointer = (int) (d / step);
                                if (colorPointer == half) {
                                    this.map.setColor(0, 1, 0, 1);
                                    this.map.drawPixel(XPixel, YPixel);
                                } else if (colorPointer < half) {
                                    float c = (float) (55 + (200 / half * colorPointer)) / 255;
                                    this.map.setColor(0, c, 0, 1);
                                    this.map.drawPixel(XPixel, YPixel);
                                } else  {
                                    float c = (float) (200 / half * (colorPointer - half)) / 255;
                                    this.map.setColor(c, 1, c, 1);
                                    this.map.drawPixel(XPixel, YPixel);
                                }
                                // darkest color: 0,55,0,1
                                // middle color: 0,255,0,1
                                // lightest color: 200,255,200,1
                            }
                        }
                    }
                }
            }
            this.yCoordinate.add(Ypos);
            this.xCoordinate.add(Xpos);
            Texture temp = new Texture(map);
            this.spriteDraw = mapStorage.size();
            this.mapStorage.add(new Sprite(temp));

        }
        else
            this.spriteDraw = arrayPos;
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

    @Override
    public void show() {
        // set the background once:
    }

    @Override
    public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        Sprite sprite = new Sprite(water);
        sprite.setSize(this.screenWidth * this.resolution, this.screenHeight*this.resolution);
        ScreenUtils.clear(0,0,0,1);
        game.batch.draw(sprite.getTexture(), 0,0, sprite.getWidth(), sprite.getHeight());
        game.batch.draw(this.mapStorage.get(spriteDraw), 0, 0);
        game.batch.draw(golfBall, ball.x,ball.y, ball.width * this.resolution, ball.height * this.resolution);
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            int x = (int) ball.x - 10;
            int y = (int) ball.y;
            ball_Update(x,y);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            int x = (int) ball.x;
            int y = (int) ball.y + 10;
            ball_Update(x,y);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            int x = (int) ball.x;
            int y = (int) ball.y - 10;
            ball_Update(x,y);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            int x = (int) ball.x + 10;
            int y = (int) ball.y;
            ball_Update(x,y);
        }

        check_out_of_bounds();
    }

    public void check_out_of_bounds() {
        int y = (int) ball.y;
        int x = (int) ball.x;
        // bal moves down
        if (y < (-this.ball.height * this.resolution)) {
            this.YChanged++;
            int UX = (int) this.ball.x;
            int UY = (int) this.ball.y + (this.screenHeight * this.resolution);
            ball_Update(UX,UY);
            int a = Check_Background(this.XChanged, this.YChanged);
            Create_Background(this.XChanged, this.YChanged, a);
        }
        // ball moves up
        else if(y > (this.screenHeight * this.resolution)- this.ball.height) {
            this.YChanged--;
            int UX = (int) this.ball.x;
            int UY = (int) this.ball.y - (this.screenHeight * this.resolution);
            ball_Update(UX,UY);
            int a = Check_Background(this.XChanged, this.YChanged);
            Create_Background(this.XChanged, this.YChanged, a);

        }
        // ball moves to the left
        else if (x < (-this.ball.width * this.resolution)) {
            this.XChanged--;
            int UX = (int) this.ball.x + (this.screenWidth * this.resolution);
            int UY = (int) this.ball.y;
            ball_Update(UX,UY);
            int a = Check_Background(this.XChanged, this.YChanged);
            Create_Background(this.XChanged, this.YChanged, a);
        }
        // ball moves to the right
        else if (x > (this.screenWidth * this.resolution) - this.ball.width) {
            this.XChanged++;
            int UX = (int) this.ball.x - (this.screenWidth * this.resolution);
            int UY = (int) this.ball.y;
            ball_Update(UX,UY);
            int a = Check_Background(this.XChanged, this.YChanged);
            Create_Background(this.XChanged, this.YChanged, a);
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
        water.dispose();
    }
}