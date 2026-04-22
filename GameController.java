import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * GameController.java
 * 
 * The controller and main entry point for Space Invaders. Wires together
 * the GameModel and GameView, creates the JFrame window, and manages
 * the game loop and event handling.
 */
public class GameController implements KeyListener, ActionListener {
    
    private GameModel model;
    private GameView view;
    private JFrame frame;
    private Timer gameLoop;
    
    // Key states for smooth movement
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean spacePressed;
    
    // Game loop timing
    private static final int TICK_DELAY = 16; // ~60 FPS
    
    /**
     * Initialize the controller and set up the game components.
     */
    public GameController() {
        // Create the model and view
        model = new GameModel();
        view = new GameView(model);
        
        // Create and configure the main window
        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        
        // Set up keyboard input
        view.setFocusable(true);
        view.addKeyListener(this);
        
        // Initialize key states
        leftPressed = false;
        rightPressed = false;
        spacePressed = false;
        
        // Start the game loop
        gameLoop = new Timer(TICK_DELAY, this);
        gameLoop.start();
    }
    
    /**
     * Handle key press events.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spacePressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            // Restart the game if R is pressed and game is not playing
            if (model.getGameState() != GameModel.GameState.PLAYING) {
                model.resetGame();
                gameLoop.start();
            }
        }
    }
    
    /**
     * Handle key release events.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spacePressed = false;
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            // R key release - not needed for functionality
        }
    }
    
    /**
     * Handle key type events (not used).
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed
    }
    
    /**
     * Handle game loop tick events.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle player input
        if (leftPressed) {
            model.movePlayerLeft();
        }
        if (rightPressed) {
            model.movePlayerRight();
        }
        if (spacePressed) {
            model.firePlayerBullet();
        }
        
        // Update game state
        model.update();
           
        // Adjust timer speed based on difficulty
        int recommendedInterval = model.getRecommendedTimerInterval();
        gameLoop.setDelay(recommendedInterval);
        
        // Redraw the view
        view.repaint();
        
        // Stop the game loop if game is over
        if (model.getGameState() != GameModel.GameState.PLAYING) {
            gameLoop.stop();
        }
    }
    
    /**
     * Main method - entry point for the application.
     */
    public static void main(String[] args) {
        new GameController();
    }
}
