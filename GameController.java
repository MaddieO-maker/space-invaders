import javax.swing.JFrame;

/**
 * GameController.java
 * 
 * The controller and main entry point for Space Invaders. Wires together
 * the GameModel and GameView, creates the JFrame window, and manages
 * the game loop and event handling.
 */
public class GameController {
    
    private GameModel model;
    private GameView view;
    private JFrame frame;
    
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
        
        // TODO: Start game loop, set up input handling, etc.
    }
    
    /**
     * Main method - entry point for the application.
     */
    public static void main(String[] args) {
        new GameController();
    }
}
