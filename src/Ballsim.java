import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
        frame.setMinimumSize(new Dimension(500,200));
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
class MyPanel extends JPanel implements Runnable{
	float ballX, ballY = 1f;
	float ballVelocityX = 100f;//TODO: implementera som m/s |just nu pixlar/s
	float ballVelocityY = -100f;
	float gravity = 400f;
	double timer, timeSinceLastFrame, startTime;
	
	private boolean running = true;
	
	public MyPanel(){
		setMinimumSize(new Dimension(250, 250));
		setBackground(Color.WHITE);
	}
	
	@Override
	public void paintComponents(Graphics g){
		super.paintComponent(g);
//		System.out.println("Drawing");

		g.drawOval((int) ballX, (int) ballY, 50, 50);
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
			paintComponents(getGraphics());
			
			if (ballX > this.getWidth()-50 || ballX < 0) {
				if (ballX > 50) {
					ballX = this.getWidth() - 50;
				} else {
					ballX = 0f;
				}
				ballVelocityX *= -1;
				System.out.println(System.currentTimeMillis() - startTime +"ms");
			}
			if (ballY > this.getHeight()-50) {
				ballVelocityY *= -0.99;
				ballY = this.getHeight()-50;
			}
			try {
				Thread.sleep(2L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Exiting panel thread");
	}
}