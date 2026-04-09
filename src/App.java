import javax.swing.*;

public class App {
    public static void main(String[] args) {
        int rows = 21;
        int columns = 19;
        int tileSize = 32;
        int boardWidth = columns * tileSize;
        int boardHeight = rows * tileSize;

        JFrame frame = new JFrame("Pacman");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Pacman pacmanGame = new Pacman();
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }
}