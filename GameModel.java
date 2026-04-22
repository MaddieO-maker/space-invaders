import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GameModel.java
 * 
 * The data model for Space Invaders. Contains all game logic and state,
 * including player position, enemies, projectiles, and scoring.
 * This class has NO Swing imports to keep it independent from the UI.
 */
public class GameModel {
    
    // Game dimensions
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    
    // Player constants
    private static final int PLAYER_WIDTH = 40;
    private static final int PLAYER_HEIGHT = 30;
    private static final int PLAYER_Y = 550;
    private static final int PLAYER_SPEED = 5;
    
    // Alien constants
    private static final int ALIEN_COLS = 11;
    private static final int ALIEN_ROWS = 5;
    private static final int ALIEN_WIDTH = 30;
    private static final int ALIEN_HEIGHT = 20;
    private static final int ALIEN_SPEED = 1;
    private static final double ALIEN_SPEED_MULTIPLIER = 1.01; // Increase by 1% per alien destroyed
    
    // Timer constants
    private static final int BASE_TIMER_INTERVAL = 16; // ~60 FPS in milliseconds
    private static final int MIN_TIMER_INTERVAL = 8; // Minimum interval to prevent too-fast gameplay
    private static final int INTERVAL_DECREMENT_PER_ALIEN = 1; // Milliseconds faster per alien
    
    // Bullet constants
    private static final int PLAYER_BULLET_SPEED = 7;
    private static final int ALIEN_BULLET_SPEED = 3;
    private static final int ALIEN_FIRE_RATE = 30; // Fire every N ticks on average
    
    // Shield constants
    private static final int NUM_SHIELDS = 4;
    private static final int SHIELD_WIDTH = 60;
    private static final int SHIELD_HEIGHT = 50;
    private static final int SHIELD_Y = 350;
    private static final int SHIELD_HEALTH = 3;
    
    // Player state
    private int playerX;
    
    // Alien formation state
    private Alien[][] alienFormation;
    private int alienFormationX;
    private int alienFormationY;
    private int alienDirection; // 1 = right, -1 = left
    private int alienTickCount;
    private double currentAlienSpeed;
    
    // Bullet state
    private PlayerBullet playerBullet;
    private List<AlienBullet> alienBullets;
    
    // Shield state
    private List<Shield> shields;
    
    // Game state
    private int score;
    private int lives;
    private Random random;
    private GameState gameState;
    
    // Game state enum and end reason
    public enum GameState {
        PLAYING,
        WON,
        LOST
    }
    
    public enum EndReason {
        ALIENS_DESTROYED,    // Win condition: player destroyed all aliens
        ALIENS_REACHED,      // Lose condition: alien formation reached player
        NO_LIVES_LEFT        // Lose condition: player lost all lives
    }
    
    private EndReason endReason;
    
    /**
     * Initialize the game model with starting values.
     */
    public GameModel() {
        this.random = new Random();
        resetGame();
    }
    
    /**
     * Reset the game to its initial state.
     * Used both during initialization and when restarting after game over.
     */
    public void resetGame() {
        this.playerX = GAME_WIDTH / 2 - PLAYER_WIDTH / 2;
        this.score = 0;
        this.lives = 3;
        this.alienFormationX = 50;
        this.alienFormationY = 50;
        this.alienDirection = 1;
        this.alienTickCount = 0;
        this.currentAlienSpeed = (double) ALIEN_SPEED;
        this.playerBullet = null;
        this.alienBullets = new ArrayList<>();
        this.shields = new ArrayList<>();
        this.gameState = GameState.PLAYING;
        this.endReason = null;
        
        // Initialize alien formation and shields
        initializeAliens();
        initializeShields();
    }
    
    /**
     * Create the alien formation (5 rows of 11 aliens).
     */
    private void initializeAliens() {
        alienFormation = new Alien[ALIEN_ROWS][ALIEN_COLS];
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLS; col++) {
                alienFormation[row][col] = new Alien(col, row);
            }
        }
    }
    
    /**
     * Create the shields positioned between player and aliens.
     */
    private void initializeShields() {
        int spacing = GAME_WIDTH / (NUM_SHIELDS + 1);
        for (int i = 1; i <= NUM_SHIELDS; i++) {
            int shieldX = spacing * i - SHIELD_WIDTH / 2;
            shields.add(new Shield(shieldX, SHIELD_Y, SHIELD_HEALTH));
        }
    }
    
    /**
     * Update game state for the current frame.
     */
    public void update() {
        if (gameState != GameState.PLAYING) {
            return; // Don't update if game is over
        }
        
        updatePlayerBullet();
        updateAlienFormation();
        updateAlienBullets();
        checkCollisions();
        
        // Check win/lose conditions
        checkWinLoseConditions();
    }
    
    /**
     * Move the player left.
     */
    public void movePlayerLeft() {
        playerX = Math.max(0, playerX - PLAYER_SPEED);
    }
    
    /**
     * Move the player right.
     */
    public void movePlayerRight() {
        playerX = Math.min(GAME_WIDTH - PLAYER_WIDTH, playerX + PLAYER_SPEED);
    }
    
    /**
     * Fire a player bullet if one isn't already in flight.
     */
    public void firePlayerBullet() {
        if (playerBullet == null) {
            int bulletX = playerX + PLAYER_WIDTH / 2;
            int bulletY = PLAYER_Y;
            playerBullet = new PlayerBullet(bulletX, bulletY);
        }
    }
    
    /**
     * Update the player bullet position.
     */
    private void updatePlayerBullet() {
        if (playerBullet != null) {
            playerBullet.y -= PLAYER_BULLET_SPEED;
            if (playerBullet.y < 0) {
                playerBullet = null;
            }
        }
    }
    
    /**
     * Update alien formation movement.
     */
    private void updateAlienFormation() {
        alienFormationX += alienDirection * (int) currentAlienSpeed;
        
        // Check if formation hits the edge
        int formationRightEdge = alienFormationX + (ALIEN_COLS * ALIEN_WIDTH);
        int formationLeftEdge = alienFormationX;
        
        if (formationRightEdge >= GAME_WIDTH || formationLeftEdge <= 0) {
            alienFormationY += 30;
            alienDirection *= -1;
        }
    }
    
    /**
     * Update alien bullets and fire new ones.
     */
    private void updateAlienBullets() {
        // Move existing alien bullets down
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            AlienBullet bullet = alienBullets.get(i);
            bullet.y += ALIEN_BULLET_SPEED;
            if (bullet.y > GAME_HEIGHT) {
                alienBullets.remove(i);
            }
        }
        
        // Fire new alien bullets randomly
        alienTickCount++;
        if (alienTickCount >= ALIEN_FIRE_RATE) {
            alienTickCount = 0;
            // Pick a random alien to fire
            Alien randomAlien = getRandomLivingAlien();
            if (randomAlien != null) {
                int bulletX = alienFormationX + randomAlien.col * ALIEN_WIDTH + ALIEN_WIDTH / 2;
                int bulletY = alienFormationY + randomAlien.row * ALIEN_HEIGHT + ALIEN_HEIGHT;
                alienBullets.add(new AlienBullet(bulletX, bulletY));
            }
        }
    }
    
    /**
     * Get a random living alien to fire from.
     */
    private Alien getRandomLivingAlien() {
        List<Alien> livingAliens = new ArrayList<>();
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLS; col++) {
                if (alienFormation[row][col].alive) {
                    livingAliens.add(alienFormation[row][col]);
                }
            }
        }
        if (livingAliens.isEmpty()) {
            return null;
        }
        return livingAliens.get(random.nextInt(livingAliens.size()));
    }
    
    /**
     * Check and handle all collisions.
     */
    private void checkCollisions() {
        // Check player bullet vs aliens
        if (playerBullet != null) {
            for (int row = 0; row < ALIEN_ROWS; row++) {
                for (int col = 0; col < ALIEN_COLS; col++) {
                    Alien alien = alienFormation[row][col];
                    if (alien.alive && checkBulletAlienCollision(playerBullet, alien)) {
                        alien.alive = false;
                        playerBullet = null;
                        score += 10;
                        currentAlienSpeed *= ALIEN_SPEED_MULTIPLIER;
                        return; // One bullet, one hit per tick
                    }
                }
            }
        }
        
        // Check player bullet vs shields
        if (playerBullet != null) {
            for (Shield shield : shields) {
                if (checkBulletShieldCollision(playerBullet, shield)) {
                    shield.health--;
                    playerBullet = null;
                    return;
                }
            }
        }
        
        // Check alien bullets vs shields
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            AlienBullet bullet = alienBullets.get(i);
            for (Shield shield : shields) {
                if (checkAlienBulletShieldCollision(bullet, shield)) {
                    shield.health--;
                    alienBullets.remove(i);
                    return;
                }
            }
        }
        
        // Check alien bullets vs player
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            AlienBullet bullet = alienBullets.get(i);
            if (checkAlienBulletPlayerCollision(bullet)) {
                alienBullets.remove(i);
                lives--;
                if (lives <= 0) {
                    // Game over
                }
            }
        }
        
        // Remove destroyed shields
        shields.removeIf(shield -> shield.health <= 0);
    }
    
    /**
     * Check win/lose conditions and update game state.
     */
    private void checkWinLoseConditions() {
        // Check if all aliens are destroyed (win condition)
        boolean allAliensDestroyed = true;
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLS; col++) {
                if (alienFormation[row][col].alive) {
                    allAliensDestroyed = false;
                    break;
                }
            }
            if (!allAliensDestroyed) break;
        }
        
        if (allAliensDestroyed) {
            gameState = GameState.WON;
            endReason = EndReason.ALIENS_DESTROYED;
            return;
        }
        
        // Check if aliens reached the player (lose condition)
        int alienFormationBottom = alienFormationY + (ALIEN_ROWS * ALIEN_HEIGHT);
        if (alienFormationBottom >= PLAYER_Y) {
            gameState = GameState.LOST;
            endReason = EndReason.ALIENS_REACHED;
            return;
        }
        
        // Check if player has no lives left (lose condition)
        if (lives <= 0) {
            gameState = GameState.LOST;
            endReason = EndReason.NO_LIVES_LEFT;
            return;
        }
    }
    
    /**
     * Check if a player bullet collides with an alien.
     */
    private boolean checkBulletAlienCollision(PlayerBullet bullet, Alien alien) {
        int alienX = alienFormationX + alien.col * ALIEN_WIDTH;
        int alienY = alienFormationY + alien.row * ALIEN_HEIGHT;
        
        return bullet.x >= alienX && bullet.x <= alienX + ALIEN_WIDTH &&
               bullet.y >= alienY && bullet.y <= alienY + ALIEN_HEIGHT;
    }
    
    /**
     * Check if an alien bullet collides with the player.
     */
    private boolean checkAlienBulletPlayerCollision(AlienBullet bullet) {
        return bullet.x >= playerX && bullet.x <= playerX + PLAYER_WIDTH &&
               bullet.y >= PLAYER_Y && bullet.y <= PLAYER_Y + PLAYER_HEIGHT;
    }
    
    /**
     * Check if a player bullet collides with a shield.
     */
    private boolean checkBulletShieldCollision(PlayerBullet bullet, Shield shield) {
        return bullet.x >= shield.x && bullet.x <= shield.x + SHIELD_WIDTH &&
               bullet.y >= shield.y && bullet.y <= shield.y + SHIELD_HEIGHT;
    }
    
    /**
     * Check if an alien bullet collides with a shield.
     */
    private boolean checkAlienBulletShieldCollision(AlienBullet bullet, Shield shield) {
        return bullet.x >= shield.x && bullet.x <= shield.x + SHIELD_WIDTH &&
               bullet.y >= shield.y && bullet.y <= shield.y + SHIELD_HEIGHT;
    }
    
    // Getters for the view
    public int getPlayerX() {
        return playerX;
    }
    
    public int getPlayerY() {
        return PLAYER_Y;
    }
    
    public int getPlayerWidth() {
        return PLAYER_WIDTH;
    }
    
    public int getPlayerHeight() {
        return PLAYER_HEIGHT;
    }
    
    public Alien[][] getAlienFormation() {
        return alienFormation;
    }
    
    public int getAlienFormationX() {
        return alienFormationX;
    }
    
    public int getAlienFormationY() {
        return alienFormationY;
    }
    
    public int getAlienWidth() {
        return ALIEN_WIDTH;
    }
    
    public int getAlienHeight() {
        return ALIEN_HEIGHT;
    }
    
    public PlayerBullet getPlayerBullet() {
        return playerBullet;
    }
    
    public List<AlienBullet> getAlienBullets() {
        return alienBullets;
    }
    
    public List<Shield> getShields() {
        return shields;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getLives() {
        return lives;
    }
    
    public int getGameWidth() {
        return GAME_WIDTH;
    }
    
    public int getGameHeight() {
        return GAME_HEIGHT;
    }
    
    /**
     * Get the recommended timer interval in milliseconds.
     * The interval decreases as more aliens are destroyed, speeding up the game.
     * 
     * @return The recommended timer interval in milliseconds
     */
    public int getRecommendedTimerInterval() {
        int aliensDestroyed = (score / 10); // 10 points per alien
        int recommendedInterval = BASE_TIMER_INTERVAL - (aliensDestroyed * INTERVAL_DECREMENT_PER_ALIEN);
        return Math.max(MIN_TIMER_INTERVAL, recommendedInterval);
    }
    
    /**
     * Get the current game state.
     * 
     * @return The current GameState
     */
    public GameState getGameState() {
        return gameState;
    }
    
    /**
     * Get the reason the game ended.
     * 
     * @return The EndReason, or null if game is still playing
     */
    public EndReason getEndReason() {
        return endReason;
    }
    
    /**
     * Get the end screen message and details.
     * 
     * @return A string describing the end screen (title and final score)
     */
    public String getEndScreenMessage() {
        if (gameState == GameState.WON) {
            return "Success!";
        } else if (gameState == GameState.LOST) {
            return "Game Over";
        }
        return "";
    }
    
    /**
     * Get the final score to display on the end screen.
     * 
     * @return The final score
     */
    public int getFinalScore() {
        return score;
    }
    
    // Inner class for aliens
    public class Alien {
        public int row;
        public int col;
        public boolean alive;
        
        public Alien(int col, int row) {
            this.col = col;
            this.row = row;
            this.alive = true;
        }
    }
    
    // Inner class for player bullets
    public class PlayerBullet {
        public int x;
        public int y;
        private static final int WIDTH = 5;
        private static final int HEIGHT = 15;
        
        public PlayerBullet(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getWidth() {
            return WIDTH;
        }
        
        public int getHeight() {
            return HEIGHT;
        }
    }
    
    // Inner class for alien bullets
    public class AlienBullet {
        public int x;
        public int y;
        private static final int WIDTH = 5;
        private static final int HEIGHT = 10;
        
        public AlienBullet(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getWidth() {
            return WIDTH;
        }
        
        public int getHeight() {
            return HEIGHT;
        }
    }
    
    // Inner class for shields
    public class Shield {
        public int x;
        public int y;
        public int health;
        
        public Shield(int x, int y, int health) {
            this.x = x;
            this.y = y;
            this.health = health;
        }
        
        public int getWidth() {
            return SHIELD_WIDTH;
        }
        
        public int getHeight() {
            return SHIELD_HEIGHT;
        }
    }
}
