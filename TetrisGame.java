import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

public class TetrisGame extends JPanel implements ActionListener {  // Main class extends JPanel for the game and implements ActionListener for timer events
    private static final int ROWS = 20, COLS = 10, BLOCK_SIZE = 30;  // Constants for rows, columns, and block size
    private int[][] board = new int[ROWS][COLS];  // 2D array to represent the game board
    private Timer timer;  // Timer to handle the game loop
    private Queue<int[][]> blockQueue = new LinkedList<>();  // Queue to hold upcoming blocks
    private int[][] currentBlock;  // 2D array to represent the current block
    private int blockRow = 0, blockCol = COLS / 2;  // Starting position of the current block
    private Random random = new Random();  // Random number generator for block selection
    private boolean gameOver = false;  // Flag to track if the game is over
    private int score = 0;  // Player's score
    private int speed = 500;  // Initial speed of the game in milliseconds

    public TetrisGame() {  // Constructor for the Tetris game
        setPreferredSize(new Dimension(COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE));  // Set the panel size
        setBackground(Color.BLACK);  // Set the background color to black
        setFocusable(true);  // Make the panel focusable to capture key events
        addKeyListener(new KeyAdapter() {  // Add a key listener to capture player input
            public void keyPressed(KeyEvent e) {  // Handle key press events
                handleInput(e.getKeyCode());  // Call handleInput() when a key is pressed
            }
        });
        generateBlock();  // Generate the first block
        timer = new Timer(speed, this);  // Create a timer to trigger the game loop
        timer.start();  // Start the timer
    }

    private void generateBlock() {  // Method to generate a new block
        int[][] block = {{1, 1, 1}, {0, 1, 0}};  // Default "T" block
        blockQueue.add(block);  // Add the block to the block queue
        currentBlock = blockQueue.poll();  // Set the current block to the first block in the queue
        blockRow = 0;  // Set the block row to 0 (starting at the top)
        blockCol = COLS / 2;  // Set the block column to the middle of the board
        if (!canMove(blockRow, blockCol)) {  // Check if the block can move to the starting position
            gameOver = true;  // End the game if the block can't be placed
            timer.stop();  // Stop the game timer
            repaint();  // Repaint the game panel
        }
    }

    private boolean canMove(int newRow, int newCol) {  // Method to check if a block can move to a new position
        if (gameOver) return false;  // If the game is over, prevent movement
        for (int r = 0; r < currentBlock.length; r++) {  // Loop through the rows of the block
            for (int c = 0; c < currentBlock[0].length; c++) {  // Loop through the columns of the block
                if (currentBlock[r][c] == 1) {  // If the block is part of the current block
                    int boardRow = newRow + r, boardCol = newCol + c;  // Calculate the new position on the board
                    if (boardRow >= ROWS || boardCol < 0 || boardCol >= COLS ||  // Check if the position is out of bounds
                        (boardRow >= 0 && board[boardRow][boardCol] == 1)) {  // Check if the position is occupied
                        return false;  // If the block can't move, return false
                    }
                }
            }
        }
        return true;  // Return true if the block can move
    }

    private void placeBlock() {  // Method to place the current block on the board
        for (int r = 0; r < currentBlock.length; r++) {  // Loop through the rows of the current block
            for (int c = 0; c < currentBlock[0].length; c++) {  // Loop through the columns of the current block
                if (currentBlock[r][c] == 1) {  // If the block is part of the current block
                    int boardRow = blockRow + r;  // Calculate the row on the board
                    if (boardRow < 0) {  // If the block reaches the top row, end the game
                        gameOver = true;  // Set the game over flag
                        timer.stop();  // Stop the game timer
                        repaint();  // Repaint the game panel
                        return;  // Exit the method
                    }
                    board[boardRow][blockCol + c] = 1;  // Place the block on the board
                }
            }
        }
        checkRows();  // Check for completed rows after placing the block
        generateBlock();  // Generate a new block
    }

    private void checkRows() {  // Method to check for full rows and clear them
        int rowsCleared = 0;  // Counter for the number of rows cleared

        // Identify and clear full rows
        for (int r = ROWS - 1; r >= 0; r--) {  // Loop through rows from bottom to top
            boolean full = true;  // Flag to check if the row is full
            for (int c = 0; c < COLS; c++) {  // Loop through columns
                if (board[r][c] == 0) {  // If any block is empty, the row is not full
                    full = false;  // Set full flag to false
                    break;  // Exit the loop
                }
            }

            // Clear the row and shift rows above it down
            if (full) {  // If the row is full
                rowsCleared++;  // Increment the rows cleared counter
                for (int row = r; row > 0; row--) {  // Loop through rows above the cleared row
                    System.arraycopy(board[row - 1], 0, board[row], 0, COLS);  // Shift rows down
                }
                board[0] = new int[COLS];  // Clear the top row
                r++;  // Re-check the current row after shifting
            }
        }

        // Update score and adjust speed if rows were cleared
        if (rowsCleared > 0) {  // If rows were cleared
            score += rowsCleared * 100;  // Increase score
            adjustSpeed();  // Adjust speed based on score
        }
    }

    private void adjustSpeed() {  // Method to adjust the speed of the game
        int newSpeed = Math.max(100, speed - 50 * (score / 500));  // Decrease speed as the score increases
        if (newSpeed != speed) {  // If the speed has changed
            speed = newSpeed;  // Update the speed
            timer.setDelay(speed);  // Set the new delay for the timer
        }
    }

    private void handleInput(int key) {  // Method to handle player input
        if (gameOver) {  // If the game is over
            if (key == KeyEvent.VK_R) {  // If 'R' key is pressed, restart the game
                resetGame();  // Call resetGame() to restart
                return;  // Exit the method
            }
            return;  // Do nothing if another key is pressed
        }
        if (key == KeyEvent.VK_LEFT && canMove(blockRow, blockCol - 1)) blockCol--;  // Move left if possible
        if (key == KeyEvent.VK_RIGHT && canMove(blockRow, blockCol + 1)) blockCol++;  // Move right if possible
        if (key == KeyEvent.VK_DOWN && canMove(blockRow + 1, blockCol)) blockRow++;  // Move down if possible
        if (key == KeyEvent.VK_SPACE) {  // If space bar is pressed
            while (canMove(blockRow + 1, blockCol)) {  // Move the block down as far as possible
                blockRow++;
            }
            placeBlock();  // Place the block when it reaches the bottom
        }
        repaint();  // Repaint the game panel
    }

    private void resetGame() {  // Method to reset the game
        board = new int[ROWS][COLS];  // Reset the board
        blockQueue.clear();  // Clear the block queue
        gameOver = false;  // Set game over flag to false
        score = 0;  // Reset score
        speed = 500;  // Reset speed
        blockRow = 0;  // Reset block row
        blockCol = COLS / 2;  // Reset block column to the middle
        generateBlock();  // Generate the first block
        timer.setDelay(speed);  // Set the timer delay to the initial speed
        timer.start();  // Start the timer
        repaint();  // Repaint the game panel
    }

    @Override
    public void actionPerformed(ActionEvent e) {  // ActionPerformed method to handle timer events
        if (gameOver) return;  // If the game is over, do nothing
        if (canMove(blockRow + 1, blockCol)) {  // If the block can move down
            blockRow++;  // Move the block down
        } else {  // If the block can't move down
            placeBlock();  // Place the block
        }
        repaint();  // Repaint the game panel
    }

    @Override
    protected void paintComponent(Graphics g) {  // Method to draw the game
        super.paintComponent(g);  // Call the superclass method

        // Draw the board
        g.setColor(Color.WHITE);  // Set the color to white
        for (int r = 0; r < ROWS; r++) {  // Loop through rows
            for (int c = 0; c < COLS; c++) {  // Loop through columns
                if (board[r][c] == 1) {  // If the cell is filled
                    g.fillRect(c * BLOCK_SIZE, r * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);  // Draw the block
                }
            }
        }

        // Draw the current block
        if (!gameOver) {  // If the game is not over
            g.setColor(Color.BLUE);  // Set the color to blue
            for (int r = 0; r < currentBlock.length; r++) {  // Loop through the rows of the current block
                for (int c = 0; c < currentBlock[0].length; c++) {  // Loop through the columns of the current block
                    if (currentBlock[r][c] == 1) {  // If the block is part of the current block
                        g.fillRect((blockCol + c) * BLOCK_SIZE, (blockRow + r) * BLOCK_SIZE,  // Draw the current block
                                BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                    }
                }
            }
        }

        // Display score and game over message
        g.setColor(Color.WHITE);  // Set color to white for text
        g.setFont(new Font("Arial", Font.BOLD, 20));  // Set the font for the score
        g.drawString("Score: " + score, 10, 25);  // Display score
        if (gameOver) {  // If the game is over
            g.setColor(Color.RED);  // Set color to red for game over message
            g.setFont(new Font("Arial", Font.BOLD, 30));  // Set the font for the game over message
            String gameOverMsg = "GAME OVER";  // Game over message
            String scoreMsg = "Final Score: " + score;  // Final score message
            String restartMsg = "Press 'R' to Restart";  // Restart message

            int gameOverX = (COLS * BLOCK_SIZE - g.getFontMetrics().stringWidth(gameOverMsg)) / 2;  // Calculate position to center game over message
            int scoreX = (COLS * BLOCK_SIZE - g.getFontMetrics().stringWidth(scoreMsg)) / 2;  // Calculate position to center score message
            int restartX = (COLS * BLOCK_SIZE - g.getFontMetrics().stringWidth(restartMsg)) / 2;  // Calculate position to center restart message

            int centerY = (ROWS * BLOCK_SIZE) / 2;  // Calculate the vertical center of the screen
            g.drawString(gameOverMsg, gameOverX, centerY - 40);  // Draw the game over message
            g.drawString(scoreMsg, scoreX, centerY);  // Draw the final score
            g.drawString(restartMsg, restartX, centerY + 40);  // Draw the restart message
        }
    }

    public static void main(String[] args) {  // Main method to run the game
        JFrame frame = new JFrame("Tetris Game");  // Create a new frame for the game window
        TetrisGame game = new TetrisGame();  // Create a new instance of the Tetris game
        frame.add(game);  // Add the game panel to the frame
        frame.pack();  // Pack the frame to fit the game panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Set the default close operation
        frame.setLocationRelativeTo(null);  // Center the window on the screen
        frame.setVisible(true);  // Make the frame visible
    }
}
