import javax.swing.*;

public class main {
    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Hello world");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Hello world!");
        frame.getContentPane().add(label);

        frame.pack();
        frame.setVisible(true);
    } 
    public static void main(String[] args) {
        //Entry point
        SwingUtilities.invokeLater(new Runnable(){
        
            @Override
            public void run() {
                createAndShowGUI();
            };
        });
    }
}