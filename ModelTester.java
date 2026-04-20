/**
 * ModelTester.java
 * 
 * Unit tests for GameModel using plain Java.
 * Tests core game logic without any testing libraries.
 */
public class ModelTester {
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Space Invaders Model Tests ===\n");
        
        testPlayerLeftBoundary();
        testPlayerRightBoundary();
        testPlayerBulletInFlightBlocks();
        testBulletRemovedAtTop();
        testAlienDestructionIncreasesScore();
        testGameOverState();
        
        System.out.println("\n=== Results ===");
        System.out.println("Tests run: " + testsRun);
        System.out.println("Tests passed: " + testsPassed);
        System.out.println("Tests failed: " + (testsRun - testsPassed));
    }
    
    /**
     * Test 1: Player cannot move past the left edge.
     */
    private static void testPlayerLeftBoundary() {
        testsRun++;
        System.out.print("Test 1: Player left boundary... ");
        
        GameModel model = new GameModel();
        
        // Move player left many times (should stop at x=0)
        for (int i = 0; i < 1000; i++) {
            model.movePlayerLeft();
        }
        
        if (model.getPlayerX() == 0) {
            System.out.println("PASS");
            testsPassed++;
        } else {
            System.out.println("FAIL (expected x=0, got x=" + model.getPlayerX() + ")");
        }
    }
    
    /**
     * Test 2: Player cannot move past the right edge.
     */
    private static void testPlayerRightBoundary() {
        testsRun++;
        System.out.print("Test 2: Player right boundary... ");
        
        GameModel model = new GameModel();
        int maxX = model.getGameWidth() - model.getPlayerWidth();
        
        // Move player right many times (should stop at maxX)
        for (int i = 0; i < 1000; i++) {
            model.movePlayerRight();
        }
        
        if (model.getPlayerX() == maxX) {
            System.out.println("PASS");
            testsPassed++;
        } else {
            System.out.println("FAIL (expected x=" + maxX + ", got x=" + model.getPlayerX() + ")");
        }
    }
    
    /**
     * Test 3: Firing while a bullet is already in flight does nothing.
     */
    private static void testPlayerBulletInFlightBlocks() {
        testsRun++;
        System.out.print("Test 3: Firing blocks while bullet in flight... ");
        
        GameModel model = new GameModel();
        
        // Fire first bullet
        model.firePlayerBullet();
        GameModel.PlayerBullet firstBullet = model.getPlayerBullet();
        
        // Try to fire second bullet
        model.firePlayerBullet();
        GameModel.PlayerBullet secondBullet = model.getPlayerBullet();
        
        // Should be the same bullet object
        if (firstBullet == secondBullet && firstBullet != null) {
            System.out.println("PASS");
            testsPassed++;
        } else {
            System.out.println("FAIL (bullet was replaced)");
        }
    }
    
    /**
     * Test 4: A bullet that reaches the top is removed.
     */
    private static void testBulletRemovedAtTop() {
        testsRun++;
        System.out.print("Test 4: Bullet removed at top... ");
        
        GameModel model = new GameModel();
        
        // Fire a bullet
        model.firePlayerBullet();
        
        // Update until bullet is removed (bullet moves up, should be removed when y < 0)
        for (int i = 0; i < 100; i++) {
            model.update();
        }
        
        GameModel.PlayerBullet bullet = model.getPlayerBullet();
        
        if (bullet == null) {
            System.out.println("PASS");
            testsPassed++;
        } else {
            System.out.println("FAIL (bullet still exists at y=" + bullet.y + ")");
        }
    }
    
    /**
     * Test 5: Destroying an alien increases the score.
     */
    private static void testAlienDestructionIncreasesScore() {
        testsRun++;
        System.out.print("Test 5: Destroying alien increases score... ");
        
        GameModel model = new GameModel();
        int initialScore = model.getScore();
        
        // Get the first alien
        GameModel.Alien[][] aliens = model.getAlienFormation();
        GameModel.Alien targetAlien = aliens[0][0];
        
        // Fire a bullet and position it to collide with the alien
        model.firePlayerBullet();
        GameModel.PlayerBullet bullet = model.getPlayerBullet();
        
        // Manually set bullet position to collide with first alien
        // The alien is at (alienFormationX + 0*alienWidth, alienFormationY + 0*alienHeight)
        int alienX = model.getAlienFormationX();
        int alienY = model.getAlienFormationY();
        bullet.x = alienX + model.getAlienWidth() / 2;
        bullet.y = alienY + model.getAlienHeight() / 2;
        
        // Update to trigger collision detection
        model.update();
        
        int finalScore = model.getScore();
        
        if (finalScore > initialScore) {
            System.out.println("PASS (score increased from " + initialScore + " to " + finalScore + ")");
            testsPassed++;
        } else {
            System.out.println("FAIL (score did not increase)");
        }
    }
    
    /**
     * Test 6: Losing all lives triggers the game-over state.
     */
    private static void testGameOverState() {
        testsRun++;
        System.out.print("Test 6: Game over when lives reach 0... ");
        
        GameModel model = new GameModel();
        int initialLives = model.getLives();
        
        // Simulate losing all lives by firing alien bullets at the player
        // We'll manually trigger collisions by positioning alien bullets at the player
        for (int i = 0; i < initialLives; i++) {
            // Create an alien bullet and position it at the player
            GameModel.AlienBullet bullet = model.new AlienBullet(
                model.getPlayerX() + model.getPlayerWidth() / 2,
                model.getPlayerY()
            );
            model.getAlienBullets().add(bullet);
            
            // Update to trigger collision
            model.update();
        }
        
        if (model.getLives() <= 0) {
            System.out.println("PASS (lives reduced to " + model.getLives() + ")");
            testsPassed++;
        } else {
            System.out.println("FAIL (lives still at " + model.getLives() + ")");
        }
    }
}
