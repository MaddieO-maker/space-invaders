import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;

/**
 * GameView.java
 * 
 * The view component for Space Invaders. Extends JPanel and handles all rendering.
 * It receives game state from GameModel and draws the game window.
 * This class is hosted in a JFrame by GameController.
 */
public class GameView extends JPanel {
    
    private GameModel model;
    
    // Colors
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color PLAYER_COLOR = Color.GREEN;
    private static final Color ALIEN_COLOR = Color.RED;
    private static final Color PLAYER_BULLET_COLOR = Color.YELLOW;
    private static final Color ALIEN_BULLET_COLOR = Color.CYAN;
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color GAME_OVER_COLOR = Color.RED;
    
    /**
     * Initialize the game view with a reference to the game model.
     * 
     * @param model The GameModel instance to render
     */
    public GameView(GameModel model) {
        this.model = model;
        setBackground(BACKGROUND_COLOR);
        setDoubleBuffered(true);
    }
    
    /**
     * Paint the game graphics onto the panel.
     * 
     * @param g The Graphics object to draw with
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw all game elements
        drawAlienFormation(g2d);
        drawShields(g2d);
        drawPlayer(g2d);
        drawPlayerBullet(g2d);
        drawAlienBullets(g2d);
        drawUI(g2d);
        
        // Draw game over message if lives are depleted
        if (model.getLives() <= 0) {
            drawGameOver(g2d);
        }
    }
    
    /**
     * Draw the player ship.
     */
    private void drawPlayer(Graphics2D g) {
        g.setColor(PLAYER_COLOR);
        g.fillRect(model.getPlayerX(), model.getPlayerY(), 
                   model.getPlayerWidth(), model.getPlayerHeight());
        
        // Draw a simple triangle on top to make it look like a ship
        g.setColor(PLAYER_COLOR);
        int[] xPoints = {
            model.getPlayerX() + model.getPlayerWidth() / 2,
            model.getPlayerX(),
            model.getPlayerX() + model.getPlayerWidth()
        };
        int[] yPoints = {
            model.getPlayerY() - 10,
            model.getPlayerY(),
            model.getPlayerY()
        };
        g.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Draw the alien formation.
     */
    private void drawAlienFormation(Graphics2D g) {
        GameModel.Alien[][] aliens = model.getAlienFormation();
        int formationX = model.getAlienFormationX();
        int formationY = model.getAlienFormationY();
        int alienWidth = model.getAlienWidth();
        int alienHeight = model.getAlienHeight();
        
        g.setColor(ALIEN_COLOR);
        for (int row = 0; row < aliens.length; row++) {
            for (int col = 0; col < aliens[row].length; col++) {
                if (aliens[row][col].alive) {
                    int x = formationX + col * alienWidth;
                    int y = formationY + row * alienHeight;
                    g.fillRect(x, y, alienWidth, alienHeight);
                    
                    // Draw simple eyes
                    g.setColor(Color.BLACK);
                    g.fillRect(x + 5, y + 5, 3, 3);
                    g.fillRect(x + alienWidth - 8, y + 5, 3, 3);
                    g.setColor(ALIEN_COLOR);
                }
            }
        }
    }
    
    /**
     * Draw the shields.
     */
    private void drawShields(Graphics2D g) {
        g.setColor(new Color(0, 200, 0)); // Green shields
        for (GameModel.Shield shield : model.getShields()) {
            g.fillRect(shield.x, shield.y, shield.getWidth(), shield.getHeight());
            
            // Draw health indicator - decrease opacity based on damage
            int healthOpacity = (shield.health * 85); // 0-255 based on health
            g.setColor(new Color(0, 0, 0, 255 - healthOpacity));
            g.fillRect(shield.x, shield.y, shield.getWidth(), shield.getHeight());
            g.setColor(new Color(0, 200, 0));
            
            // Draw text showing health
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            String healthText = String.valueOf(shield.health);
            int textWidth = g.getFontMetrics().stringWidth(healthText);
            int textX = shield.x + (shield.getWidth() - textWidth) / 2;
            int textY = shield.y + shield.getHeight() / 2 + 5;
            g.drawString(healthText, textX, textY);
        }
    }
    
    /**
     * Draw the player's bullet.
     */
    private void drawPlayerBullet(Graphics2D g) {
        GameModel.PlayerBullet bullet = model.getPlayerBullet();
        if (bullet != null) {
            g.setColor(PLAYER_BULLET_COLOR);
            g.fillRect(bullet.x - bullet.getWidth() / 2, bullet.y, 
                       bullet.getWidth(), bullet.getHeight());
        }
    }
    
    /**
     * Draw all alien bullets.
     */
    private void drawAlienBullets(Graphics2D g) {
        g.setColor(ALIEN_BULLET_COLOR);
        for (GameModel.AlienBullet bullet : model.getAlienBullets()) {
            g.fillRect(bullet.x - bullet.getWidth() / 2, bullet.y, 
                       bullet.getWidth(), bullet.getHeight());
        }
    }
    
    /**
     * Draw the UI (score and lives).
     */
    private void drawUI(Graphics2D g) {
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Draw score
        String scoreText = "Score: " + model.getScore();
        g.drawString(scoreText, 20, 30);
        
        // Draw lives
        String livesText = "Lives: " + model.getLives();
        g.drawString(livesText, model.getGameWidth() - 150, 30);
    }
    
    /**
     * Draw the centered game-over message.
     */
    private void drawGameOver(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, model.getGameWidth(), model.getGameHeight());
        
        // Game over text
        g.setColor(GAME_OVER_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 64));
        String gameOverText = "GAME OVER";
        int textWidth = g.getFontMetrics().stringWidth(gameOverText);
        int x = (model.getGameWidth() - textWidth) / 2;
        int y = (model.getGameHeight() / 2) - 40;
        g.drawString(gameOverText, x, y);
        
        // Final score
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        String finalScoreText = "Final Score: " + model.getScore();
        int scoreWidth = g.getFontMetrics().stringWidth(finalScoreText);
        int scoreX = (model.getGameWidth() - scoreWidth) / 2;
        int scoreY = y + 60;
        g.drawString(finalScoreText, scoreX, scoreY);
    }
}
