import javax.swing.JPanel;
import java.awt.Graphics;

/**
 * GameView.java
 * 
 * The view component for Space Invaders. Extends JPanel and handles all rendering.
 * It receives game state from GameModel and draws the game window.
 * This class is hosted in a JFrame by GameController.
 */
public class GameView extends JPanel {
    
    private GameModel model;
    
    /**
     * Initialize the game view with a reference to the game model.
     * 
     * @param model The GameModel instance to render
     */
    public GameView(GameModel model) {
        this.model = model;
        // TODO: Set up rendering settings, event listeners, etc.
    }
    
    /**
     * Paint the game graphics onto the panel.
     * 
     * @param g The Graphics object to draw with
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // TODO: Draw game board, player, enemies, projectiles, etc.
    }
}
