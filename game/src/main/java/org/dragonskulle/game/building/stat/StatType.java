/* (C) 2021 DragonSkulle */
package org.dragonskulle.game.building.stat;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.dragonskulle.game.building.stat.SyncStat.IValueCalculator;

/**
 * Stores different types of stats.
 *
 * <p>Each stat has:
 *
 * <ul>
 *   <li>An ID (its index in {@link #values()}).
 *   <li>A method to calculate its value for a given level.
 * </ul>
 *
 * @author Craig Wilbourne
 */
@Log
@Accessors(prefix = "m")
public enum StatType {
    ATTACK((level) -> level), // The attack value is identical to the current level number.
    ATTACK_DISTANCE(3), // Regardless of the level, the attack distance will always be the same.
    BUILD_DISTANCE(3), // Regardless of the level, the build distance will always be the same.
    CLAIM_DISTANCE(1), // Regardless of the level, the claim distance will always be the same.
    DEFENCE((level) -> level), // The defence value is identical to the current level number.
    TOKEN_GENERATION((level) -> {
        return Math.max(level - 1, 0);
    }), // The number of tokens to generate is identical to the current level number minus
    VIEW_DISTANCE(3); // Regardless of the level, the view distance will always be the same.


    /* Set the IDs of the Stats. */
    static {
        int current = 0;
        for (StatType statType : values()) {
            statType.mID = current;
            current++;
        }
    }

    /**
     * The index of the specific StatType in {@link #values()}.
     */
    private int mID;

    /**
     * The method used to turn a level ({@code int}) into a value ({@code int}).
     */
    @Getter
    private final IValueCalculator mValueCalculator;

    /**
     * Whether the stat always returns a fixed value.
     */
    @Getter
    private final boolean mFixedValue;

    /**
     * Create a new type of stat.
     *
     * @param valueCalculator The method used to turn a level into a value.
     */
    StatType(IValueCalculator valueCalculator) {
        mFixedValue = false;
        mValueCalculator = valueCalculator;
    }

    /**
     * Create a new type of stat that is permanently one value.
     *
     * @param value The value of the stat, regardless of level.
     */
    StatType(int value) {
        mFixedValue = true;
        mValueCalculator =
                (__) -> {
                    return value;
                };
    }

    /**
     * Get the ID of the StatType.
     *
     * @return The ID.
     */
    public int getID() {
        return mID;
    }

    /**
     * Get a {@link StatType} from its ID.
     *
     * @param id The ID of the desired StatType.
     * @return The desired StatType, or {@code null}.
     */
    public static StatType getFromID(int id) {
        StatType[] values = values();
        if (id < 0 || id > values.length) {
            log.warning("StatType ID out of range: " + id);
            return null;
        }

        return values[id];
    }
}
