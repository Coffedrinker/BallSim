import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import javax.swing.*;

public class Ballsim {
	
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Hello world");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Hello world!");
        MyPanel mainPanel = new MyPanel();
        frame.getContentPane().add(label);
        frame.setMinimumSize(new Dimension(300,1020));
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
	private int[] listX, listY;
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
		listX = new int[] {0,width, width,0}; 
		listY = new int[] {0,0,height, height};
		
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
		if (this.rotation != 0){
			north = rotatePoint((int) b.getPosX(),(int) b.getPosY(), -this.rotation)[1] + b.getSize()<= (int) this.posY;
		}
		
		boolean[] direction = {north,south,west,east};
		return direction;
	}
	public int[][] getPoints(){
		int[][] points = new int[][] {listX,listY};
		return points;
	}
}

class Ball {
	private float posX, posY, velocityX, velocityY, forceX, forceY;
	private int size;
	
	public Ball() {
		this.posX = 1;
		this.posY = 1;
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
	float gravity = 9800f;
	float studsKoeff = -1f;
	float timeScale = 1f;
	
	long timer, timeSinceLastFrame, startTime;
	int fps, accumulator = 0;
	
	private boolean running = true;
	
	private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private Ball ball;		
	
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
		g.drawLine((int) ball.getPosX(), (int) ball.getPosY(),(int)  ball.getPosX() + (int) ball.getVelocityX(), (int) ball.getVelocityY());
		fps ++;
	}
	public synchronized void doStop(){
		this.running = false;
	}
	// Beräkna skärande punkt hos 2 linjer utifrån 2 punkter från dessa linjer
	// a -> b och c -> d
	// TODO: Byt namn!
	public double[] getIntersectingPoint(double ax, double ay, double bx, double by, double cx, double cy, double dx, double dy){
		double fakAToB = (by-ay)/(bx-ax);
		double fakCToD = (dy-cy)/(dx-cx);
		
		double konstAToB = ay-fakAToB*ax;
		double konstCToD = cy-fakCToD*cx;
		
		double pointX = (konstCToD-konstAToB)/(fakAToB-fakCToD); // ?!
		
		// Räta linjens ekvation f= k*x + m
		double pointY = fakAToB*pointX + konstAToB;
				
		double newAngle = Math.atan(fakAToB) - Math.atan(fakCToD);
		double origLength = Math.sqrt((dx-cx)*(dx-cx)+(dy-cy)*(dy-cy));
//		double clipLength = origLength - Math.sqrt((pointX-cy)*(pointX-cy)+(pointY-cy)*(pointY-cy));

		double newPointX = pointX + origLength * Math.cos(newAngle);
		double newPointY = pointY + origLength * Math.sin(newAngle);
		double newVelocityX = origLength * Math.cos(newAngle);
		double newVelocityY = origLength * Math.sin(newAngle);
		return new double[] {newPointX,newPointY,newVelocityX, newVelocityY};
	}
	
	public void run() {
		// XXX: Initialize world
		startTime = System.currentTimeMillis();
		timer = System.currentTimeMillis();
		timeSinceLastFrame = 2;
		
		int north = 0, south = 1, west = 2, east = 3 ;
		boolean collisionNorth;
		double[] newPosAndAngle;
		boolean[] direction;
		
		obstacles.add( new Obstacle(0, this.getHeight()+1, this.getWidth(), 500, 0));
		obstacles.add( new Obstacle(this.getWidth()+1, 0, 20, this.getHeight(), 0));
		obstacles.add( new Obstacle(-30, 0, 30, this.getHeight(), 0));
//		obstacles.add( new Obstacle(0, 129, 80, 200, 0));
		obstacles.add( new Obstacle(0, 130, 150, 30, 30));
		
		
//		Point2D cp = new Point((int) ball.getPosX(), (int) ball.getPosY()); 
		while(this.running) {
//			timeSinceLastFrame = System.currentTimeMillis() - timer;
//			timer = System.currentTimeMillis();
//			System.out.println(timer);
//			cp.setLocation((int) ballX + ballSize/2, (int) ballY + ballSize/2);
			
			collisionNorth = false;
			// XXX: Handle collisions
			for (Obstacle obs : obstacles) { 
				if (obs.getShape().intersects(
						ball.getPosX() + ball.getVelocityX() * timeScale * timeSinceLastFrame / 1000,
						ball.getPosY() + ball.getVelocityY() * timeScale * timeSinceLastFrame / 1000,
						ball.getSize(),
						ball.getSize())) {
					
					direction = obs.getRelativePositionOf(ball); 
					if (direction[north] && !direction[west] && !direction[east]) {
						newPosAndAngle = getIntersectingPoint(
								obs.getPoints()[0][0], 
								obs.getPoints()[1][0], 
								obs.getPoints()[0][1], 
								obs.getPoints()[1][1], 
								ball.getPosX() , 
								ball.getPosY() + ball.getSize(),
								ball.getPosX() + ball.getVelocityX() * timeScale * timeSinceLastFrame /1000, 
								ball.getPosY() + ball.getSize() + ball.getVelocityY() * timeScale * timeSinceLastFrame /1000);
						System.out.println(ball.getVelocityY() + "mm/s");
						collisionNorth = true;
						ball.setPosX((int) newPosAndAngle[0]);
						ball.setPosY((int) newPosAndAngle[1] - ball.getSize());
						
						ball.setVelocityX((int) newPosAndAngle[2]/(timeScale * timeSinceLastFrame / 1000));
						ball.setVelocityY((int) newPosAndAngle[3]/(timeScale * timeSinceLastFrame / 1000));
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
			accumulator += timeSinceLastFrame * timeScale;
			if (accumulator >= 1000){
				System.out.println(fps +" | "+ timeSinceLastFrame);
				accumulator -= 1000;
				fps=0;
				System.out.println(ball.getVelocityY() + "mm/s");
//				ball.setPosX(0);
//				ball.setPosY(0);
			}
			revalidate();
			repaint();
			try {
				Thread.sleep(2); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Exiting panel thread");
	}
}