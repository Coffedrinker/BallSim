import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class main {
	
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Hello world");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Hello world!");
        JPanel mainPanel = new MyPanel();
        frame.getContentPane().add(label);
        frame.setMinimumSize(new Dimension(500,200));
        frame.add(label);
        frame.add(mainPanel);
//        frame.pack();
        
        
        frame.setVisible(true);
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
class MyPanel extends JPanel implements ActionListener{
	int ballX, ballY = 0;
	
	public MyPanel(){
		setMinimumSize(new Dimension(250, 250));
		setBackground(Color.BLACK);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
	}
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.drawOval(ballX, ballY, 50, 50);
	}
}