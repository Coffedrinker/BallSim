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

        MyPanel mainPanel = new MyPanel();
//        mainPanel.setSize(800, 1000);
        frame.add(mainPanel);
        frame.pack();
        frame.revalidate();
        frame.repaint();
        
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
	}	
	public int getPosY() {
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
	private double posX, posY, velocityX, velocityY, forceX, forceY;
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
	
	public double getPosX() {
		return posX;
	}
	public void setPosX(double posX) {
		this.posX = posX;
	}
	public double getPosY() {
		return posY;
	}
	public void setPosY(double posY) {
		this.posY = posY;
	}
	public int getSize(){
		return size;
	}
	public double getVelocityX() {
		return velocityX;
	}
	public void setVelocityX(double velocityX) {
		this.velocityX = velocityX;
	}
	public double getVelocityY() {
		return velocityY;
	}
	public void setVelocityY(double velocityY) {
		this.velocityY = velocityY;
	}
	public double getForceX() {
		return forceX;
	}
	public void setForceX(double forceX) {
		this.forceX = forceX;
	}
	public double getForceY() {
		return forceY;
	}
	public void setForceY(double forceY) {
		this.forceY = forceY;
	}
}

class MyPanel extends JPanel implements Runnable{
	//1000f = 1m
	double gravity = 9800;
//	double studsKoeff = 0.33; 
	double timeScale = 1;
	
	double timeSinceLastFrame = 0.001d, startTime, accumulator = 0;
	int fps;
	
	private boolean running = true;
	
	private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private Ball ball;		
	
	public MyPanel(){
		this.setPreferredSize(new Dimension(800, 630));
		setBackground(Color.WHITE);
		
		

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

		//Accelerationslinje
//		g.drawLine((int) ball.getPosX(), (int) ball.getPosY(),(int)  (ball.getPosX() + ball.getVelocityX()*timeSinceLastFrame), (int) (ball.getPosY() + ball.getVelocityY()*timeSinceLastFrame));
		
		fps ++;
	}
	public synchronized void doStop(){
		this.running = false;
	}

	// Beräkna skärande punkt hos 2 linjer utifrån 2 punkter från dessa linjer
	// Beräknar bollens nya vektor
	// a -> b och c -> d
	public double[] calculateCollision(double ax, double ay, double bx, double by, double cx, double cy, double dx, double dy){
		double fakAToB = (by-ay)/(bx-ax);
		double fakCToD = (dy-cy)/(dx-cx);
		
		double konstAToB = ay-fakAToB*ax;
		double konstCToD = cy-fakCToD*cx;
		
		
		double pointX = (konstCToD-konstAToB)/(fakAToB-fakCToD);
		
		if (dx == cx || bx == ax) {
			pointX = cx;
		}
		// Räta linjens ekvation f= k*x + m
		
		System.out.println("Infallsvinkel: " + Math.toDegrees(Math.atan(fakCToD)));
		double pointY = fakAToB*pointX + konstAToB;
		double newAngle = Math.atan(fakAToB) - Math.atan(fakCToD);
		double origLength = Math.sqrt((dx-cx)*(dx-cx)+(dy-cy)*(dy-cy));
		
		double newPointX = pointX + origLength * Math.cos(newAngle);
		double newPointY = pointY + origLength * Math.sin(newAngle);
		double newVelocityX = origLength * Math.cos(newAngle) * 0.75;
		double newVelocityY = origLength * Math.sin(newAngle) * 0.33;
		return new double[] {newPointX,newPointY,newVelocityX, newVelocityY};
	}
	
	public void run() {
		// XXX: Initialize world
		startTime = System.currentTimeMillis();
		
		
		int north = 0, south = 1, west = 2, east = 3 ;
		boolean collisionNorth;
		double[] newPosAndAngle;
		boolean[] direction;

		
		// XXX: Set up scene
		ball = new Ball(0,-35,35,1010,0);
		
		obstacles.add( new Obstacle(0, this.getHeight()-2, this.getWidth(), 500, 0));
		obstacles.add( new Obstacle(this.getWidth()+1, 0, 500, this.getHeight(), 0));
//		obstacles.add( new Obstacle(-30, 0, 30, this.getHeight(), 0));
		obstacles.add( new Obstacle(0, 130, 150, 30, 30));
		
		System.out.println(this.getHeight());
		revalidate();
		repaint();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		while(this.running) {
			collisionNorth = false;
			
			
			// XXX: Handle collisions
			for (Obstacle obs : obstacles) { 
				if (obs.getShape().intersects(
						ball.getPosX() + ball.getVelocityX() * timeScale * timeSinceLastFrame,
						ball.getPosY() + ball.getVelocityY() * timeScale * timeSinceLastFrame,
						ball.getSize(),
						ball.getSize())) {
					
					direction = obs.getRelativePositionOf(ball); 
					if (direction[north] && !direction[west] && !direction[east]) {
						newPosAndAngle = calculateCollision(
								obs.getPoints()[0][0], 
								obs.getPoints()[1][0], 
								obs.getPoints()[0][1], 
								obs.getPoints()[1][1], 
								ball.getPosX() , 
								ball.getPosY() + ball.getSize(),
								ball.getPosX() + ball.getVelocityX() * timeScale * timeSinceLastFrame, 
								ball.getPosY() + ball.getSize() + ball.getVelocityY() * timeScale * timeSinceLastFrame);
						System.out.println(ball.getVelocityX() + "mm/s xled"); 
						System.out.println(ball.getVelocityY() + "mm/s yled");
						System.out.println("x: " + ball.getPosX() + " | y: " + (int) (ball.getPosY()- this.getHeight()));
//						if (collisionNorth) {
//							ball.setPosX((int) newPosAndAngle[0]);
//							ball.setPosY((int) newPosAndAngle[1] - ball.getSize());
//							ball.setVelocityY(0);
//						}else{
//							ball.setPosX((int) newPosAndAngle[0]);
//							ball.setPosY((int) newPosAndAngle[1] - ball.getSize());
//							ball.setVelocityX(newPosAndAngle[2]/(timeScale * timeSinceLastFrame));
//							ball.setVelocityY(newPosAndAngle[3]/(timeScale * timeSinceLastFrame));
//						}
						ball.setPosX((int) newPosAndAngle[0]);
						ball.setPosY((int) newPosAndAngle[1] - ball.getSize());
						ball.setVelocityX(newPosAndAngle[2]/(timeScale * timeSinceLastFrame));
						ball.setVelocityY(newPosAndAngle[3]/(timeScale * timeSinceLastFrame));
						
						if (Math.abs(ball.getVelocityY()) < 50) {
							ball.setVelocityY(0);
						}
						collisionNorth = true;
					}
					if (direction[west] && !direction[north] && !direction[south]) {
						ball.setVelocityX(ball.getVelocityX()  * -1);
						ball.setPosX(obs.getPosX() - ball.getSize());
					}
					
				}
				
				//XXX: end of collision check
			}
			
			// XXX: Handle movement
			
			ball.setPosX(ball.getPosX() + ball.getVelocityX() * timeScale * timeSinceLastFrame);
			if (collisionNorth == false) {
				ball.setVelocityY(ball.getVelocityY() + gravity * timeSinceLastFrame * timeScale);
			}
			ball.setPosY(ball.getPosY() + ball.getVelocityY() * timeSinceLastFrame);
			
			// XXX: Draw simulation and start next frame
			accumulator += timeSinceLastFrame * timeScale;
			
			if (accumulator >= 1){
				accumulator -= 1;
				
				System.out.println(ball.getVelocityY() + "mm/s");
//				ball.setPosX(0);
//				ball.setPosY(0);
			}
			revalidate();
			repaint();
			try {
				Thread.sleep(8); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Exiting panel thread");
	}
}