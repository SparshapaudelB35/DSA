/*b) 
A Game of Tetris 
Functionality: 
Queue: Use a queue to store the sequence of falling blocks. 
Stack: Use a stack to represent the current state of the game board. 
GUI: 
A game board with grid cells. 
A preview area to show the next block. 
Buttons for left, right, and rotate. 
Implementation: 
Initialization: 
 Create an empty queue to store the sequence of falling blocks. 
 Create an empty stack to represent the game board. 
 Initialize the game board with empty cells. 
 Generate a random block and enqueue it. 
Game Loop: 
While the game is not over: 
 Check for game over: If the top row of the game board is filled, the game is over. 
 Display the game state: Draw the current state of the game board and the next block in the 
preview area. 
Handle user input: 
 If the left or right button is clicked, move the current block horizontally if possible. 
 If the rotate button is clicked, rotate the current block if possible. 
 Move the block: If the current block can move down without colliding, move it down. Otherwise: 
 Push the current block onto the stack, representing its placement on the game board. 
 Check for completed rows: If a row is filled, pop it from the stack and add a new empty row at the 
top. 
 Generate a new random block and enqueue it. 
Game Over: 
 Display a game over message and the final score. 
Data Structures: 
Block: A class or struct to represent a Tetris block, including its shape, color, and current position. 
GameBoard: A 2D array or matrix to represent the game board, where each cell can be empty or filled 
with a block. 
Queue: A queue to store the sequence of falling blocks. 
Stack: A stack to represent the current state of the game board. 
Additional Considerations: 
Collision detection: Implement a function to check if a block can move or rotate without colliding with 
other blocks or the game board boundaries. 
Scoring: Implement a scoring system based on factors like completed rows, number of blocks placed, and 
other game-specific rules. 
Leveling: Increase the speed of the falling blocks as the player's score increases. 
Power-ups: Add power-ups like clearing lines, adding extra rows, or changing the shape of the current 
block. */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;  

public class TetrisGame extends JPanel implements ActionListener {
    private static final int ROWS = 20, COLS = 10, BLOCK_SIZE = 30;
    private int[][] board = new int[ROWS][COLS];
    private Timer timer;
    private Queue<int[][]> blockQueue = new LinkedList<>();
    private int[][] currentBlock;
    private int blockRow = 0, blockCol = COLS / 2;
    private Random random = new Random();
    private boolean gameOver = false;
    private int score = 0;
    private int speed = 500;

    public TetrisGame() {
        setPreferredSize(new Dimension(COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                handleInput(e.getKeyCode());
            }
        });
        generateBlock();
        timer = new Timer(speed, this);
        timer.start();
    }

    private void generateBlock() {
        int[][] block = {{1, 1, 1}, {0, 1, 0}};
        blockQueue.add(block);
        currentBlock = blockQueue.poll();
        blockRow = 0;
        blockCol = COLS / 2 - 1;
        if (!canMove(blockRow, blockCol)) {
            gameOver = true;
            timer.stop();
            repaint();
        }
    }

    private boolean canMove(int newRow, int newCol) {
        if (gameOver) return false;
        for (int r = 0; r < currentBlock.length; r++) {
            for (int c = 0; c < currentBlock[0].length; c++) {
                if (currentBlock[r][c] == 1) {
                    int boardRow = newRow + r, boardCol = newCol + c;
                    if (boardRow >= ROWS || boardCol < 0 || boardCol >= COLS ||
                        (boardRow >= 0 && board[boardRow][boardCol] == 1)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placeBlock() {
        for (int r = 0; r < currentBlock.length; r++) {
            for (int c = 0; c < currentBlock[0].length; c++) {
                if (currentBlock[r][c] == 1) {
                    int boardRow = blockRow + r;
                    if (boardRow < 0) {
                        gameOver = true;
                        timer.stop();
                        repaint();
                        return;
                    }
                    board[boardRow][blockCol + c] = 1;
                }
            }
        }
        checkRows();
        generateBlock();
    }

    private void checkRows() {
        int rowsCleared = 0;
        for (int r = ROWS - 1; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                rowsCleared++;
                for (int row = r; row > 0; row--) {
                    System.arraycopy(board[row - 1], 0, board[row], 0, COLS);
                }
                board[0] = new int[COLS];
                r++;
            }
        }
        if (rowsCleared > 0) {
            score += rowsCleared * 100;
            adjustSpeed();
        }
    }

    private void adjustSpeed() {
        int newSpeed = Math.max(100, speed - 50 * (score / 500));
        if (newSpeed != speed) {
            speed = newSpeed;
            timer.setDelay(speed);
        }
    }

    private void handleInput(int key) {
        if (gameOver) {
            if (key == KeyEvent.VK_R) {
                resetGame();
                return;
            }
            return;
        }
        if (key == KeyEvent.VK_LEFT && canMove(blockRow, blockCol - 1)) blockCol--;
        if (key == KeyEvent.VK_RIGHT && canMove(blockRow, blockCol + 1)) blockCol++;
        if (key == KeyEvent.VK_DOWN && canMove(blockRow + 1, blockCol)) blockRow++;
        if (key == KeyEvent.VK_SPACE) {
            while (canMove(blockRow + 1, blockCol)) {
                blockRow++;
            }
            placeBlock();
        }
        repaint();
    }

    private void resetGame() {
        board = new int[ROWS][COLS];
        blockQueue.clear();
        gameOver = false;
        score = 0;
        speed = 500;
        blockRow = 0;
        blockCol = COLS / 2;
        generateBlock();
        timer.setDelay(speed);
        timer.start();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;
        if (canMove(blockRow + 1, blockCol)) {
            blockRow++;
        } else {
            placeBlock();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] == 1) {
                    g.fillRect(c * BLOCK_SIZE, r * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }
        g.setColor(Color.RED);
        for (int r = 0; r < currentBlock.length; r++) {
            for (int c = 0; c < currentBlock[0].length; c++) {
                if (currentBlock[r][c] == 1) {
                    g.fillRect((blockCol + c) * BLOCK_SIZE, (blockRow + r) * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Game");
        TetrisGame game = new TetrisGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
