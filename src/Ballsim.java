import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Ballsim {
	
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Hello world");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Hello world!");
        MyPanel mainPanel = new MyPanel();
        frame.getContentPane().add(label);
        frame.setMinimumSize(new Dimension(900,600));
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
class Obstacle extends Rectangle{
	public Obstacle() {
		// TODO Auto-generated constructor stub
		
	}
}
class MyPanel extends JPanel implements Runnable{
	//1000f = 1m
	float ballX, ballY = 1f;
	float ballVelocityX = 100f;//TODO: implementera som m/s | just nu pixlar/s
	float ballVelocityY = 0;
	float gravity = 9800f;
	float studsKoeff = 0.6f;
	
	long timer, timeSinceLastFrame, startTime;
	int fps, accumulator = 0;
	int ballSize = 20;
	private boolean running = true;
	
	public MyPanel(){
		setMinimumSize(new Dimension(250, 250));
		setBackground(Color.WHITE);
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
//		System.out.println("Drawing");
		g.drawOval((int) ballX, (int) ballY, ballSize, ballSize);
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
					System.out.println(fps +" | "+ 1000/timeSinceLastFrame);
					accumulator = 0;
					fps=0;
				}
				Thread.sleep(15); // lag simulering, om tid mellan rutor är < 1 ms så blinkar grafiken
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Exiting panel thread");
	}
}