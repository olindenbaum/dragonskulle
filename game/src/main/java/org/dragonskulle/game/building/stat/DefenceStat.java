/* (C) 2021 DragonSkulle */
package org.dragonskulle.game.building.stat;

public class DefenceStat extends Stat<Double> {

	private final double mValueMin = 0.5;
	private final double mValueMax = 20.0;

    private double mapLevel() {    	
        return map(mValueMin, mValueMax, Double.valueOf(mLevel), Double.valueOf(LEVEL_MIN), Double.valueOf(LEVEL_MAX));
    }
    
    @Override
    protected Double levelToValue() {
        return mapLevel();
    }
}
