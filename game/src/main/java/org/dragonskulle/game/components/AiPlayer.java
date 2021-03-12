package org.dragonskulle.game.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.dragonskulle.components.IFixedUpdate;
import org.dragonskulle.core.Reference;
import org.dragonskulle.game.map.HexagonMap;
import org.dragonskulle.game.map.HexagonTile;

/**
 * This base class will allow AI players to be created and used throughout the game.
 * @author Oscar L
 */
public class AiPlayer extends Player implements IFixedUpdate {
    
	protected float timeSinceStart;
	protected int lowerBoundTime;
	protected int upperBoundTime;
	protected int timeToWait;
	
	protected Random random = new Random();
	
	protected float tileProbability = (float)0.5;
	protected float buildingProabilty = 1 - tileProbability;
	
	protected float upgradeBuilding = (float) 0.2;  // These three probabilities summed must == 1
	protected float attackBuilding = (float) 0.7;
	protected float sellBuilding = (float) 0.1;
	
	//TODO to choose where to attack, which building to use, what stat to upgrade.  Do we want these to be uniform or not?  I would say it's easier to be uniform HOWEVER we can play around more easily if they're not uniform
	
	/**
	 * A Constructor for an AI Player 
	 * @param lowerBound the lower bound in time for how often you want the AI player to play
	 * @param upperBound the upper bound in time for how often you want the AI player to play
	 * @param map the map being used for this game
	 * @param capital the capital used by the player
	 * 
	 * <p> So if you set the lowerBound to 2 and upperBound the AI Player will have a go every 2 - 5 seconds (Depending on the random number picked) <\p>
	 */
	public AiPlayer(int lowerBound, int upperBound, Reference<HexagonMap> map, Building capital) {
		
		super(map, capital);
		lowerBoundTime = lowerBound;
		upperBoundTime = upperBound;
		timeSinceStart = 0;
		
		createNewRandomTime();
		
		
	}
	
	/**
	 * This will check to see whether the AI Player can actually play or not
	 * @param deltaTime The time since the last fixed update
	 * @return A boolean to say whether the AI player can play
	 */
	protected boolean playGame(float deltaTime) {
		timeSinceStart += deltaTime;
		
		//Checks to see how long since last time AI player played and if longer than how long they have to wait
		if (timeSinceStart >= timeToWait) {
			timeSinceStart = 0;
			createNewRandomTime();  //Creates new Random Number
			return true;
		}
		
		return false;
	}
	
	/**
	 * This will set how long the AI player has to wait until they can play
	 */
	protected void createNewRandomTime() {
		do {
			
			timeToWait = random.nextInt(upperBoundTime+1);
		} while (timeToWait < lowerBoundTime);
	}
	
	@Override
    protected void onDestroy() {

    }

    @Override
    public void fixedUpdate(float deltaTime) {
    	
    	updateTokens(deltaTime);
    	if (playGame(deltaTime)) {
    		simulateInput();
    		triggerEvent();
    		
    	}
    }

    /**
     * This will simulate the action to be done by the AI player.  For the base class this will be done using probability
     */
    private void simulateInput(){
    	
    	//TODO Need to know how we are interacting with triggerEvent().  Cos here you can choose exact command to do (Much Much easier)
    	//TODO ATM have the set up probabilties to choose which event to do.  Need to add which building/tile to use
    	
    	if (ownedBuildings.size() == 1) {
    		//TODO Choose which tile to use;
    		List<HexagonTile> tilesToUse = hexTilesToExpand();
    		if (tilesToUse.size() != 0) {
    			int randomIndex = random.nextInt(tilesToUse.size());
    			HexagonTile tileToExpandTo = tilesToUse.get(randomIndex);
    			//now have Hexagon tile to expand to 
    		}
    		else {
    			return; //end
    		}
			
    		
    	}
    	else {
    		float randomNumber = random.nextFloat();
    		
    		if (randomNumber <= tileProbability) {
    			//TODO Choose which tile to use
    			//Need way to choose which tile to use -- Guessing best way is to use Building again
    			List<HexagonTile> tilesToUse = hexTilesToExpand();
        		if (tilesToUse.size() != 0) {
        			int randomIndex = random.nextInt(tilesToUse.size());
        			HexagonTile tileToExpandTo = tilesToUse.get(randomIndex);
        			//now have Hexagon tile to expand to 
        		}
        		else {
        			return; //end
        		}
    		}
    		else {
    			randomNumber = random.nextFloat();
    			Building building = ownedBuildings.get(random.nextInt(ownedBuildings.size()));
    			
    			if (randomNumber <= upgradeBuilding) {
    				
    				Building buildingToUpgrade = ownedBuildings.get(random.nextInt(ownedBuildings.size));
    				//How to choose what stat to improve -- need to wait to se how building is doing it
    				//TODO Choose which building to upgrade & which stat to upgrade
    				return;
    			}
    			else if (randomNumber > upgradeBuilding && randomNumber <= attackBuilding + upgradeBuilding){
    				//TODO Choose which building to attack
    				ArrayList<Building[]> buildingsToAttack = new ArrayList<Building[]>();
    				for (Building building: ownedBuildings) {
    					
    					List<Building> attackableBuildings = building.attackableBuildings();
    					for (Building buildingWhichCouldBeAttacked : attackableBuildings) {
    						Building[] listToAdd = {building, buildingWhichCouldBeAttacked};
    					
    						buildingsToAttack.add(listToAdd);
    					}
    				}
    				
    				if (buildingsToAttack.size() != 0) {
    					Building[] buildingToAttack = buildingsToAttack.get(random.nextInt(buildingsToAttack.size()));
    					//Chosen building to attack in form [buildingToAttackFrom, buildingToAttack]
    					return;
    				}
    				else {
    					return;
    				}
    				
    				
    			}
    			else {
    				//TODO Choose which building to sell
    				if (ownedBuildings > 1) {
    					Building buildingToSell = ownedBuildings.get(random.nextInt(ownedBuildings.size()));	
    					//Now have building to sell
        				return;
    				}
    				else {
    					return;
    				}
    				
    				
    			}
    		}
    	}
    }
    
    /**
     * A private method which will return all the hex tiles which can be used to place a building in
     * @return A list of hexagon tiles which can be expanded into.
     */
    private List<HexagonTile> hexTilesToExpand(){
    	List<HexagonTile> hexTilesToExpand = new ArrayList<HexagonTile>();
    	for (Building building: ownedBuildings) {
    		List<HexagonTile> hexTilesWhichCanBeSeen = building.getHexTiles();
    		
    		int r_pos = building.getR();
    		int q_pos = building.getS();
    		
    		for (HexagonTile hexTile: hexTilesWhichCanBeSeen) {
    			   			
    			if (mapComponent.get(hexTile.getmR(), hexTile.getmQ()) != null) {
    				; //Ignore cos theres already a building there
    			}
    			else if (!checkCloseBuildings(hexTile)) {  // Rework to check for every building.
    				;//IGNORE TILE IT'S WITHIN 1 HEX	
    			}
    			
    			
    			// Can add extra checks here.
    			else {
    				hexTilesToExpand.add(hexTile);
    			}
    		}
    	}
    	return hexTilesToExpand;
    }
    
    /**
     * This will check if the hex tile chosen to build in is within 1 place of any other building.
     * @param hexTile The hex tile to build in
     * @return {@code true} if that hextile is valid to build in or {@code false} if it's not valid 
     */
    private boolean checkCloseBuildings(HexagonTile hexTile) {
    	int r_value = hexTile.getmR();
		int q_value = hexTile.getmQ();
    	int index = 0;
    	boolean validPlace = true;
    	while (validPlace && index < ownedBuildings.size()) { 
			Builidng buildingToCheck = ownedBuildings.get(index);
			if ((Math.abs(Math.abs(r_value) - Math.abs(buildingToCheck.getmR())) <= 1) && (Math.abs(Math.abs(q_value) - Math.abs(building.getmQ())) <= 1)){
				return false;
			}
			index++;
		}
    	return true;
    }
}
