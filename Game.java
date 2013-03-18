import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Game implements Runnable {
   public void run() {
      // Top-level frame
      final JFrame frame = new JFrame("Tetris");
      frame.setLocation(1000, 150);
      
      // Main playing area
      final Menu menu = new Menu(frame);
      frame.add(menu, BorderLayout.CENTER);
      frame.setResizable(false);

      // Put the frame on the screen
      frame.pack();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);

      }

   /*
    * Get the game started!
    */
   public static void main(String[] args) {
       SwingUtilities.invokeLater(new Game());
   }

}
