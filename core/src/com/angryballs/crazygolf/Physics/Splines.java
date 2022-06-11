package com.angryballs.crazygolf.Physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Splines extends JFrame{

    int x, y;
    ArrayList<Vector2> list = new ArrayList<>();
    ArrayList<Vector2> splineList = new ArrayList<>();

    public static void main(String[] args) {
        new Splines();

    }
    //reference
    //https://andrewhungblog.wordpress.com/2017/03/03/catmull-rom-splines-in-plain-english/
    public static ArrayList<Vector2> drawSpline(List<Vector2> points){
        int stepsFreq = 3; float tension = 1;
        ArrayList<Vector2> curve = new ArrayList<Vector2>();
        for(int i = 0; i < points.size() - 1; i++){
            Vector2 prev, next, start, end;
            prev = i == 0 ? points.get(i) : points.get(i-1);
            next = i == points.size()-2 ? points.get(i+1) : points.get(i+2);
            start = points.get(i);
            end = points.get(i+1);

            for(int step = 0; step <= stepsFreq; step++){
                float t = (float) step / stepsFreq;
                float tSquared = t * t;
                float tCubed = tSquared * t;

                Vector2 interpolatedPoint =
                        new Vector2(
                                (-.5f * tension * tCubed + tension * tSquared - .5f * tension * t) * prev.x +
                                (1 + .5f * tSquared * (tension - 6) + .5f * tCubed * (4 - tension)) * start.x +
                                (.5f * tCubed * (tension - 4) + .5f * tension * t - (tension - 3) * tSquared) * end.x +
                                (-.5f * tension * tSquared + .5f * tension * tCubed) * next.x,
                                (-.5f * tension * tCubed + tension * tSquared - .5f * tension * t) * prev.y +
                                (1 + .5f * tSquared * (tension - 6) + .5f * tCubed * (4 - tension)) * start.y +
                                (.5f * tCubed * (tension - 4) + .5f * tension * t - (tension - 3) * tSquared) * end.y +
                                (-.5f * tension * tSquared + .5f * tension * tCubed) * next.y);

                curve.add(interpolatedPoint);
            }

        }
        return curve;
    }


    public Splines(){
        setSize(500,500);
        setLayout(null);
        setTitle("Splines");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        Controller c = new Controller();
        addMouseListener(c);
        addKeyListener(c);
    }
    class Controller extends MouseAdapter implements KeyListener {
        Graphics g = getGraphics();
        boolean stop = false;
        Vector2 curPoint;

        public void drawPoints(List<Vector2> list){
            int[] xx = new int[list.size()];
            int[] yy = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                xx[i] = (int) list.get(i).x;
                yy[i] = (int) list.get(i).y;
            }
            g.drawPolyline(xx, yy, list.size());
        }
        public void clean(){
            getContentPane().removeAll();
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(stop)
                return;
            super.mouseClicked(e);
            x = e.getX();
            y = e.getY();

            g.fillRect(x-2,y-2,4,4);
            list.add(new Vector2(x,y));
        }


        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                drawPoints(list);
                splineList = (ArrayList<Vector2>) drawSpline(list);
                drawPoints(splineList);
                stop = true;
            }
            if(e.getKeyChar() == KeyEvent.VK_SPACE){
                clean();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(!stop){
                return;
            }
            Object[] arr = list.toArray();
            if(e.getKeyCode() == KeyEvent.VK_Q){
                curPoint = list.get(list.size()/2);
                System.out.println(curPoint);
            }
            switch (e.getKeyCode()){
                case KeyEvent.VK_W : {curPoint.y -= 50; break;}
                case KeyEvent.VK_A : {curPoint.x -= 50; break;}
                case KeyEvent.VK_S : {curPoint.y += 50; break;}
                case KeyEvent.VK_D : {curPoint.x += 50; break;}
            }
            arr[list.size()/2] = curPoint;
            list.clear();
            for(Object v : arr){
                list.add((Vector2) v);
            }
            System.out.println(list);
            splineList = (ArrayList<Vector2>) drawSpline(list);
            drawPoints(splineList);
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}