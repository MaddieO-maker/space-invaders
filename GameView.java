import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GameView.java
 * 
 * The view component for Space Invaders. Extends JPanel and handles all rendering.
 * It receives game state from GameModel and draws the game window.
 * This class is hosted in a JFrame by GameController.
 */
public class GameView extends JPanel {
    
    private GameModel model;
    private List<Star> stars;
    private Random random;
    
    // Star constants
    private static final int NUM_STARS = 50;
    private static final int STAR_SPEED = 1; // Pixels per frame
    private static final int STAR_SIZE = 2; // Pixel diameter
    
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
        this.random = new Random();
        setBackground(BACKGROUND_COLOR);
        setDoubleBuffered(true);
        initializeStars();
    }
    
    /**
     * Initialize the star field with random stars.
     */
    private void initializeStars() {
        stars = new ArrayList<>();
        for (int i = 0; i < NUM_STARS; i++) {
            int x = random.nextInt(model.getGameWidth());
            int y = random.nextInt(model.getGameHeight());
            stars.add(new Star(x, y));
        }
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
        
        // Update and draw stars first (background)
        updateStars();
        drawStars(g2d);
        
        // Draw all game elements
        drawAlienFormation(g2d);
        
        // Draw shields with health-based color (green to red)
        for (GameModel.Shield shield : model.getShields()) {
            // Color gradient: health 3 = green, health 2 = yellow, health 1 = red
            Color shieldColor;
            if (shield.health >= 3) {
                shieldColor = Color.GREEN;
            } else if (shield.health == 2) {
                shieldColor = Color.YELLOW;
            } else {
                shieldColor = Color.RED;
            }
            
            g2d.setColor(shieldColor);
            g2d.fillRect(shield.x, shield.y, shield.getWidth(), shield.getHeight());
            
            // Draw border
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new java.awt.BasicStroke(2));
            g2d.drawRect(shield.x, shield.y, shield.getWidth(), shield.getHeight());
        }
        
        drawPlayer(g2d);
        drawPlayerBullet(g2d);
        drawAlienBullets(g2d);
        drawUI(g2d);
        
        // Draw end screen if game has ended
        if (model.getGameState() != GameModel.GameState.PLAYING) {
            drawEndScreen(g2d);
        }
    }
    
    /**
     * Update star positions each frame.
     */
    private void updateStars() {
        for (Star star : stars) {
            star.y += STAR_SPEED;
            // Wrap around when star reaches bottom
            if (star.y > model.getGameHeight()) {
                star.y = 0;
                star.x = random.nextInt(model.getGameWidth());
            }
        }
    }
    
    /**
     * Draw all stars in the field.
     */
    private void drawStars(Graphics2D g) {
        g.setColor(Color.WHITE);
        for (Star star : stars) {
            g.fillOval(star.x, star.y, STAR_SIZE, STAR_SIZE);
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
        
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        for (int row = 0; row < aliens.length; row++) {
            for (int col = 0; col < aliens[row].length; col++) {
                if (aliens[row][col].alive) {
                    int x = formationX + col * alienWidth;
                    int y = formationY + row * alienHeight;
                    // Draw alien emoji
                    g.drawString("👽", x + 5, y + 20);
                }
            }
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
     * Draw the end screen (win or lose) with appropriate message and final score.
     */
    private void drawEndScreen(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, model.getGameWidth(), model.getGameHeight());
        
        // Determine message and color based on game state
        String endMessage;
        Color messageColor;
        
        if (model.getGameState() == GameModel.GameState.WON) {
            endMessage = "Success!";
            messageColor = Color.GREEN;
        } else {
            endMessage = "Game Over";
            messageColor = GAME_OVER_COLOR;
        }
        
        // End screen title text
        g.setColor(messageColor);
        g.setFont(new Font("Arial", Font.BOLD, 64));
        int textWidth = g.getFontMetrics().stringWidth(endMessage);
        int x = (model.getGameWidth() - textWidth) / 2;
        int y = (model.getGameHeight() / 2) - 40;
        g.drawString(endMessage, x, y);
        
        // Final score
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        String finalScoreText = "Final Score: " + model.getFinalScore();
        int scoreWidth = g.getFontMetrics().stringWidth(finalScoreText);
        int scoreX = (model.getGameWidth() - scoreWidth) / 2;
        int scoreY = y + 60;
        g.drawString(finalScoreText, scoreX, scoreY);
        
        // Replay button hint
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String replayText = "Press R to replay";
        int replayWidth = g.getFontMetrics().stringWidth(replayText);
        int replayX = (model.getGameWidth() - replayWidth) / 2;
        int replayY = y + 120;
        g.drawString(replayText, replayX, replayY);
    }
    
    /**
     * Inner class representing a star in the background.
     */
    private static class Star {
        int x;
        int y;
        
        Star(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
