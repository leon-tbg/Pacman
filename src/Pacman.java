import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class Pacman extends JPanel implements ActionListener,KeyListener{
    class Block {
        Image image;
        int x;
        int y;
        int width;
        int height;

        int startX;
        int startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for(Block wall : walls) {
                if(collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if(this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            }
            else if (this.direction =='D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            }
            else if (this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rows = 21;
    private int columns = 19;
    private int tileSize = 32;
    private int boardWidth = columns * tileSize;
    private int boardHeight = rows * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    private int difficulty = 3; //1 = easy, 2 = middle, 3 = hard

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    Pacman() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("assets/wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("assets/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("assets/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("assets/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("assets/redGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("assets/pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("assets/pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("assets/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("assets/pacmanRight.png")).getImage();

        loadMap();
        for(Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for(int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if(tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanUpImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' ') {
                    Block food = new Block(null, x+14, y+14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for(Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
        for(Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.white);
        for(Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        if(gameOver) {
            g.setColor(Color.black);
            g.fillRect(0, tileSize * 4, tileSize * 17, tileSize * 8);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER: ", tileSize, boardHeight/3);
            g.drawString(score + " P", tileSize, boardHeight/2 - 2 * tileSize);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press a button to continue", tileSize, boardHeight/2);
        }
        else {
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Lives: " + lives + "   Score: " + score + " P", tileSize/2, tileSize/2);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for(Block wall : walls) {
            if(collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        if(pacman.x <= -pacman.width && pacman.direction == 'L') {
            pacman.x = boardWidth + pacman.width;
        }

        if(pacman.x >= boardWidth + pacman.width && pacman.direction == 'R') {
            pacman.x = -pacman.width;
        }

        for(Block ghost : ghosts) {
            if(collision(pacman, ghost)) {
                lives -= 1;
                if(lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y  == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                char newDirection = directions[random.nextInt(2)];
                ghost.updateDirection(newDirection);
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            //ghost update position
            for(Block wall : walls) {
                if(collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;

                    char newDirection = directions[random.nextInt(4)];

                    if(difficulty == 1) {
                        int rnd = random.nextInt(5);
                        if(rnd == 0) newDirection = getIntelligentGhostDirection(ghost);
                    }
                    else if(difficulty == 2) {
                        int rnd = random.nextInt(5);
                        if(rnd == 0 || rnd == 1 || rnd == 2) newDirection = getIntelligentGhostDirection(ghost);
                    }
                    else if(difficulty == 3) {
                        int rnd = random.nextInt(5);
                        if(rnd != 4) newDirection = getIntelligentGhostDirection(ghost);
                    }
                    ghost.updateDirection(newDirection);
                    break;
                }
            }
        }

        Block foodEaten = null;
        for(Block food : foods) {
            if(collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if(foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for(Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    public char getCharAt(int c, int r) {
        if (r < 0 || r >= rows || c < 0 || c >= columns) {
            return 'X';
        }

        String row = tileMap[r];
        return row.charAt(c);
    }

    public char getIntelligentGhostDirection(Block ghost) {
        char newDirection = directions[random.nextInt(4)];

        int dx = ghost.x - pacman.x;
        int dy = ghost.y - pacman.y;

        char horizontal = dx > 0 ? 'L' : 'R';
        char vertical   = dy > 0 ? 'U' : 'D';

        if (Math.abs(dx) >= Math.abs(dy)) {
            if (canMove(ghost, horizontal)) newDirection = horizontal;
            else if (canMove(ghost, vertical)) newDirection = vertical;
        } else {
            if (canMove(ghost, vertical)) newDirection = vertical;
            else if (canMove(ghost, horizontal)) newDirection = horizontal;
        }

        return newDirection;
    }

    private boolean canMove(Block ghost, char dir) {
        if (ghost.x % tileSize != 0 || ghost.y % tileSize != 0) {
            return false;
        }

        int tx = ghost.x / tileSize;
        int ty = ghost.y / tileSize;
        return switch (dir) {
            case 'L' -> getCharAt(tx - 1, ty) != 'X';
            case 'R' -> getCharAt(tx + 1, ty) != 'X';
            case 'U' -> getCharAt(tx,ty - 1) != 'X';
            case 'D' -> getCharAt(tx,ty + 1) != 'X';
            default  -> false;
        };
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if(gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        if(e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if(pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        }
        else if(pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if(pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
        else if(pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
    }
}