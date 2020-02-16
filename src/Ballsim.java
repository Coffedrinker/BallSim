import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.Vector;

import javax.swing.*;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class Ballsim {
	
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Hello world");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Hello world!");
        MyPanel mainPanel = new MyPanel();
        frame.getContentPane().add(label);
        frame.setMinimumSize(new Dimension(300,200));
        frame.add(label);
        frame.add(mainPanel);
//        frame.pack();
        
        
        frame.setVisible(true);
        Thread physics = new Thread(mainPanel);
        System.out.println("Starting thread");
        physics.start();
        
    } 
    public static void main(String[] args) {
        //Entry point
        SwingUtilities.invokeLater(new Runnable(){
        
            public void run() {
                createAndShowGUI();
            };
        });
    }
}
class Obstacle {
	private int posX, posY, width, height;
	private double rotation;
	private Polygon shape;
	public Obstacle() {	
		// TODO Auto-generated constructor stub
		this.posX = 0;
		this.posY = 0;
		this.width = 60;
		this.height = 20;
		this.rotation = 0;
		this.shape = makeShape();
	}
	public Obstacle(int _posX, int _posY, int _width, int _height , double _rotation){
		this.posX = _posX;
		this.posY = _posY;
		this.width = _width;
		this.height = _height;	
		this.rotation = _rotation;
		this.shape = makeShape();
		System.out.println(getShape().npoints);
		for (int i = 0; i < 4; i++) {
			System.out.println(getShape().xpoints[i]+" | "+ getShape().ypoints[i]);
		}
	}
	public Polygon getShape(){
		return this.shape;
	}
	
	private Polygon makeShape(){
		int[] listX = {0,width, width,0}; 
		int[] listY = {0,0,height, height};
		if (rotation == 17) {
			return new Polygon(listX,listY,4);
		} else {
			for (int i = 0; i < listY.length; i++) {
				int[] newPoint = rotatePoint(listX[i], listY[i], rotation);
				listX[i] = newPoint[0]+posX;
				listY[i] = newPoint[1]+posY;
			}
			return new Polygon(listX, listY, 4);
		}
	}
	
	private int[] rotatePoint(int x, int y, double rot){
		int[] coords = {0,0};
		double h = Math.sqrt(x*x+y*y);
		double ang = Math.asin(y/h);
		coords[0] = (int) (h*Math.cos(ang + Math.toRadians(rot)));
		coords[1] = (int) (h*Math.sin(ang + Math.toRadians(rot)));
		return coords;
	}
	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}
}
class MyPanel extends JPanel implements Runnable{
	//1000f = 1m
	float ballX, ballY = 1f;
	float ballVelocityX = 100f;//TODO: implementera som m/s | just nu pixlar/s
	float ballVelocityY = 0;
	float gravity = 9800f;
	float studsKoeff = 1f;
	
	long timer, timeSinceLastFrame, startTime;
	int fps, accumulator = 0;
	int ballSize = 20;
	private boolean running = true;
	Obstacle[] obstacles = new Obstacle[1];
			
	public MyPanel(){
		setMinimumSize(new Dimension(250, 250));
		setBackground(Color.WHITE);
		
		obstacles[0] = new Obstacle(0, 100, 60, 20, 60);
		
	}
	
	@Override
	public void paintComponent(Graphics g){ 
		super.paintComponent(g);
//		System.out.println("Drawing");
		g.fillOval((int) ballX, (int) ballY, ballSize, ballSize);
		g.setColor(Color.YELLOW);
		for (Obstacle obstacle : obstacles) {
			g.fillPolygon(obstacle.getShape());
//			System.out.println(obstacle.getShape().xpoints + " | "+obstacle.getShape().ypoints);
		}
		fps ++;
	}
	public synchronized void doStop(){
		this.running = false;
	}
	public void run() {
		// XXX: Physics thread
		startTime = System.currentTimeMillis();
		timer = System.currentTimeMillis();
		while(this.running) {
//			System.out.println("doing thread");
			timeSinceLastFrame = System.currentTimeMillis() - timer;
			timer = System.currentTimeMillis();
			
			ballVelocityY += (gravity*timeSinceLastFrame/1000);
			
			ballX += (ballVelocityX*timeSinceLastFrame/1000);
			ballY += (ballVelocityY*timeSinceLastFrame/1000);
			
			
//			System.out.println(timeSinceLastFrame+"ms");
//			paintComponent(getGraphics());
			revalidate();
			repaint();
			
			if (ballX > this.getWidth()-ballSize || ballX < 0) {
				if (ballX > ballSize) {
					ballX = this.getWidth() - ballSize;
				} else {
					ballX = 0f;
				}
				ballVelocityX *= -1;
				System.out.println(System.currentTimeMillis() - startTime +"ms");
			}
			if (ballY > this.getHeight()-ballSize) {
				ballVelocityY *= -studsKoeff;
				ballY = this.getHeight()-ballSize;
			}
			try {
				accumulator += timeSinceLastFrame;
				if (accumulator > 1000){
					System.out.println(fps +" | "+ timeSinceLastFrame);
					accumulator = 0;
					fps=0;
				}
				Thread.sleep(1); // lag simulering, om tid mellan rutor är < 1 ms så blinkar grafiken
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Exiting panel thread");
	}
}