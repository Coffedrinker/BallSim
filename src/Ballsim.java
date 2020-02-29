import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

public class Ballsim {
	
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Hello world");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Hello world!");
        MyPanel mainPanel = new MyPanel();
        frame.getContentPane().add(label);
        frame.setMinimumSize(new Dimension(300,400));
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
		
		for (int i = 0; i < listY.length; i++) {
			int[] newPoint = rotatePoint(listX[i], listY[i], rotation);
			listX[i] = newPoint[0]+posX;
			listY[i] = newPoint[1]+posY;
		}
		return new Polygon(listX, listY, 4);
	}
	public int[] rotatePoint(int x, int y, double rot){
		int[] coords = {0,0};
		double h = Math.sqrt(x*x+y*y);
		double ang = Math.asin(y/h);
		coords[0] = (int) (h*Math.cos(ang + Math.toRadians(rot)));
		coords[1] = (int) (h*Math.sin(ang + Math.toRadians(rot)));
		return coords;
	}
	public double getRotation(){
		return rotation;
	}
	public void setRotation(double _rotation){
		this.rotation = _rotation;
		this.shape = makeShape();
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public int getPosX() {
		return posX;
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		this.posY = posY;
	}
	public boolean[] getRelativePositionOf(Ball b){		
		boolean north = (int) (b.getPosY() + b.getSize()) <= (int) this.posY;
		boolean south = (int) b.getPosY() > (int) (this.posY + this.height);
		boolean west = (int) (b.getPosX() + b.getSize()) < (int) this.posX;
		boolean east = (int) b.getPosX() > (int) (this.posX + this.width);
		
		boolean[] direction = {north,south,west,east};
		return direction;
	}
}

class Ball {
	private float posX, posY, velocityX, velocityY, forceX, forceY;
	private int size;
	
	public Ball() {
		this.posX = 0;
		this.posY = 0;
		this.size = 20;
		this.velocityX = 100;
		this.velocityY = 0;
	}
	public Ball(float _posX, float _posY, int _size, float _velocityX, float _velocityY){
		this.posX = _posX;
		this.posY = _posY;
		this.size = _size;
		this.velocityX = _velocityX;
		this.velocityY = _velocityY;
		
	}
	
	public float getPosX() {
		return posX;
	}
	public void setPosX(float posX) {
		this.posX = posX;
	}
	public float getPosY() {
		return posY;
	}
	public void setPosY(float posY) {
		this.posY = posY;
	}
	public int getSize(){
		return size;
	}
	public float getVelocityX() {
		return velocityX;
	}
	public void setVelocityX(float velocityX) {
		this.velocityX = velocityX;
	}
	public float getVelocityY() {
		return velocityY;
	}
	public void setVelocityY(float velocityY) {
		this.velocityY = velocityY;
	}
	public float getForceX() {
		return forceX;
	}
	public void setForceX(float forceX) {
		this.forceX = forceX;
	}
	public float getForceY() {
		return forceY;
	}
	public void setForceY(float forceY) {
		this.forceY = forceY;
	}
}

class MyPanel extends JPanel implements Runnable{
	//1000f = 1m
//	float ballX = 1f; 
//	float ballY = 1f;
//	float ballVelocityX = 100f;
//	float ballVelocityY = 0f;
//	int ballSize = 20;
	float gravity = 9800f;
	float studsKoeff = -0.4f;
	float timeScale = 0.2f;
	
	long timer, timeSinceLastFrame, startTime;
	int fps, accumulator = 0;
	
	private boolean running = true;
	
	ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	Ball ball;		
	
	public MyPanel(){
		setMinimumSize(new Dimension(250, 250));
		setBackground(Color.WHITE);
		
		ball = new Ball();
		
		
	}
	
	@Override
	// XXX: Draw all objects
	public void paintComponent(Graphics g){ 
		super.paintComponent(g);
		
		g.setColor(Color.BLUE);
		for (Obstacle obstacle : obstacles) {
			g.fillPolygon(obstacle.getShape());
		}
		g.setColor(Color.GRAY);
		g.fillOval((int) ball.getPosX(), (int) ball.getPosY(), ball.getSize(), ball.getSize());
		g.setColor(Color.RED);
//		g.drawRect((int) (ball.getPosX() + ball.getVelocityX()* timeScale * timeSinceLastFrame / 1000),
//				(int) (ball.getPosY() + ball.getVelocityY()* timeScale * timeSinceLastFrame / 1000),
//						ball.getSize(),
//						ball.getSize());
		fps ++;
	}
	public synchronized void doStop(){
		this.running = false;
	}
	public void run() {
		// XXX: Initialize world
		startTime = System.currentTimeMillis();
		timer = System.currentTimeMillis();
		
		int north = 0, south = 1, west = 2, east = 3 ;
		timeSinceLastFrame = 2;
		
		obstacles.add( new Obstacle(0, this.getHeight()+1, this.getWidth(), 500, 0));
		obstacles.add( new Obstacle(this.getWidth()+1, 0, 20, this.getHeight(), 0));
		obstacles.add( new Obstacle(-30, 0, 30, this.getHeight(), 0));
		obstacles.add( new Obstacle(0, 129, 80, 200, 0));
		obstacles.add( new Obstacle(0, 100, 100, 30, 30));
		
//		Point2D cp = new Point((int) ball.getPosX(), (int) ball.getPosY()); 
		while(this.running) {
			boolean collisionNorth = false;
//			timeSinceLastFrame = System.currentTimeMillis() - timer;
//			timer = System.currentTimeMillis();
//			System.out.println(timer);
//			cp.setLocation((int) ballX + ballSize/2, (int) ballY + ballSize/2);
			
			// XXX: Handle collisions
			for (Obstacle obs : obstacles) { 
				if (obs.getShape().intersects(
						ball.getPosX() + ball.getVelocityX() * timeScale * timeSinceLastFrame / 1000,
						ball.getPosY() + ball.getVelocityY() * timeScale * timeSinceLastFrame / 1000,
						ball.getSize(),
						ball.getSize())) {

					boolean[] direction = obs.getRelativePositionOf(ball); 
					
					if (direction[north] && !direction[west] && !direction[east]) {
						
						collisionNorth = true;
						ball.setPosY(obs.getPosY() - ball.getSize());
						if (ball.getVelocityY() > gravity*timeScale*timeSinceLastFrame /1000) {
							ball.setVelocityY(ball.getVelocityY() * studsKoeff);
						}else{
							ball.setVelocityY(0);
						}
					}
					if (direction[west] && !direction[north] && !direction[south]) {
						ball.setVelocityX(ball.getVelocityX() * studsKoeff);
						ball.setPosX(obs.getPosX() - ball.getSize());
					}
					
				}
			}
			
			// XXX: Handle movement
			ball.setPosX(ball.getPosX() + ball.getVelocityX() * timeScale * timeSinceLastFrame / 1000);
			if (!collisionNorth) {
				ball.setVelocityY(ball.getVelocityY() + gravity * timeSinceLastFrame * timeScale / 1000);
			}
			ball.setPosY(ball.getPosY() + ball.getVelocityY()*timeScale*timeSinceLastFrame/1000);
			
			// XXX: Draw simulation and start next frame
			try {
				accumulator += timeSinceLastFrame;
				if (accumulator >= 1000){
					System.out.println(fps +" | "+ timeSinceLastFrame);
					accumulator -= 1000;
					fps=0;
//					ball.setPosX(0);
//					ball.setPosY(0);
				}
				Thread.sleep(1); // lag simulering, om tid mellan rutor är < 1 ms så blinkar grafiken
				
				revalidate();
				repaint();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Exiting panel thread");
	}
}