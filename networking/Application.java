package networking;

import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTextField;


public class Application extends JFrame {
    
    public Application() throws IOException {
        initUI();
    }

    private void initUI() throws IOException {	// function to initialise the user interface
        add(new Board());	
        setSize(1366, 768);	//setting the size of the board
        setTitle("Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//enabling the exit button
        setLocationRelativeTo(null);	//setting the game at the centre of the screen
        
    }    
    
    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	//Thread.sleep()
            	
                Application ex;
				try {
					ex = new Application();
					ex.setVisible(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            }
        });
        
        
    }
}
