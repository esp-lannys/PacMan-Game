package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import back.Display;
import back.Game;
import pacman.PacmanGame;

public class Main {

    public static void main(String[] args) {
    	 Game game = new PacmanGame();
         Display view = new Display(game);
         JFrame frame = new JFrame();
         frame.setTitle("Pacman");
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.getContentPane().add(view);
         frame.pack();
         frame.setLocationRelativeTo(null);
         frame.setVisible(true);
         view.requestFocus();
         view.start();
       
    }
    
}
