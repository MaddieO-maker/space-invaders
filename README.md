# space-invaders
A Space Invaders Game

Prompt 1: I'm building Space Invaders in Java using Swing, split into three files: GameModel.java, GameView.java, and GameController.java. GameView should extend JPanel and be hosted in a JFrame. GameController should have the main method and wire the three classes together. GameModel must have no Swing imports. For now, just create the three class shells with placeholder comments describing what each class will do. The program should compile and open a blank window.
SUCCESS - COMMIT

Prompt 2: Fill in GameModel.java. The model should track: the player's horizontal position, the alien formation (5 rows of 11), the player's bullet (one at a time), alien bullets, the score, and lives remaining (start with 3). Add logic to: move the player left and right, fire a player bullet if one isn't already in flight, advance the player's bullet each tick, move the alien formation right until the edge then down and reverse, fire alien bullets at random intervals, and detect collisions between bullets and aliens or the player. No Swing imports.
SUCCESS - COMMIT

Prompt 3: Fill in GameView.java. It should take a reference to the model and draw everything the player sees: the player, the alien formation, both sets of bullets, the score, and remaining lives. Show a centered game-over message when the game ends. The view should only read from the model — it must never change game state.
SUCCESS - COMMIT

Prompt 4: Fill in GameController.java. Add keyboard controls so the player can move left and right with the arrow keys and fire with the spacebar. Add a game loop using a Swing timer that updates the model each tick and redraws the view. Stop the loop when the game is over.
SUCCESS - COMMIT

Prompt 5: Create a separate file called ModelTester.java with a main method. It should create a GameModel, call its methods directly, and print PASS or FAIL for each check. Write tests for at least five behaviors: the player cannot move past the left or right edge, firing while a bullet is already in flight does nothing, a bullet that reaches the top is removed, destroying an alien increases the score, and losing all lives triggers the game-over state. No testing libraries — just plain Java.
SUCCESS - COMMIT

Prompt 6: Adding Shields: In GameModel.java, add a list of shield rectangles positioned between the player and the alien formation. Reduce a shield's health when hit by a bullet from either side. Remove the shield when health reaches zero. No Swing imports.
SUCCESS - COMMIT

Prompt 7: Drawing Shields: In GameView.java's paintComponent method only, draw the shields from the model's shield list. Use the shield's health value to choose a color from full green to dim red. Do not call any model mutating methods.
SUCCESS - COMMIT

Prompt 8: Increasing Speed: In GameModel.java, increase the alien movement speed each time an alien is destroyed. Expose a method the Controller can call to get the current recommended timer interval. Do not touch the View.
Worked, but made the alien ship too fast to be enjoyable

Prompt 9: In GameModel.java, increase alien movement speed by 1% for each alien destroyed. Do not touch view
SUCCESS - COMMIT

Prompt 10: In GameModel.java, end the game if alien ship is completely destroyed or alien ship reaches player ship. Create a "Success!" end screen if alien ship is destroyed and a "Game Over" end screen if alien ship reaches player ship. Each end screen should have final score and a replay button.
Successfully added "Game Over" and "Success" Screen

Prompt 11: In GameModel.java, call "Game Over" and show Game Over screen when alien ship hits player ship
Prompt 12: "Press R to replay" on final game screen doesn't work. Make sure if "R" is pressed on keyboard on Game Over or Success screen that game restarts
SUCCESS - COMMIT

Prompt 12: In GameView.java, add white dots over black background to look like stars. Have the stars move slowly downward to imply movement of ships.
SUCCESS

Prompt 13: In GameView.Java, instead of red blocks representing aliens, use this image: 👽
SUCCESS - (Not green, but success) - COMMIT
