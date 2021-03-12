package org.dragonskulle.game.components;

import org.dragonskulle.components.IFrameUpdate;
import org.dragonskulle.core.Reference;
import org.dragonskulle.game.input.GameActions;
import org.joml.Vector2d;

/**
 * This class will allow a user to interact with game.
 * @author Harry Stoltz, Oscar Lindenbaum and Nathaniel Lowis
 */
public class HumanPlayer extends Player implements IFrameUpdate {

	private Screen screenOn = Screen.MAP_SCREEN;

	/**
	 * The constructor for the human player
	 * @param map the map being used for this game
	 * @param capital the capital used by the player
	 */
	public HumanPlayer(Reference<HexagonMap> map, Building capital) {
		
		super(map, capital);
		
	}
    @Override
    protected void onDestroy() {
    	
    }

    @Override
    public void frameUpdate(float deltaTime) {
    	updateTokens(deltaTime);
    	processInput();
    	triggerEvent();
    	
    }
    
    /**
     * This will take an input from the User and will then change this into a way for the class to understand what the player wants to do
     */
    private void processInput() {
    	if (screenOn == Screen.MAP_SCREEN) {
    		mapScreen();
    	}
    	else if (screenOn == Screen.BUILDING_SCREEN){
    		buildingScreen();
    	}
    	else if (screenOn == Screen.TILE_SCREEN) {
    		expansionScreen();
    	}
    }
    
    /**
     * This will choose what to do when the user can see the full map
     */
    private void mapScreen() {
    	if (GameActions.LEFT_CLICK.isActivated()) {
    		Vector2d cursorPosition = GameActions.getCursor().getPosistion();
    		// Check to see whether the user has pressed a tile.  And then send that to server 
    		
    	}
    	
    }
    
    /**
     * This will choose what to do when the user can see what to do with a building
     */
    private void buildingScreen() {
    	if (GameActions.LEFT_CLICK.isActivated()) {
    		Vector2d cursorPosition = GameActions.getCursor().getPosistion();
    		// Check to see if user has pressed a button.  If it has then send to server and all time change perspective
    		
    	}
    }

    /**
     * This will choose what to do when the user can see what to do with a building
     */
    private void expansionScreen() {
    	if (GameActions.LEFT_CLICK.isActivated()) {
    		Vector2d cursorPosition = GameActions.getCursor().getPosistion();
    		
    		// Check to see if user has pressed a button.  If it has then send to server and all time change perspective
    		
    	}
    }
}
