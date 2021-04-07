/* (C) 2021 DragonSkulle */
package org.dragonskulle.input;

/**
 * An action that can either be activated or deactivated.
 *
 * <p>An action is triggered by buttons, as defined in {@link Bindings}.
 *
 * @author Craig Wilbourne
 */
public class Action {

    /** A name used for display purposes only. */
    private String mName;

    /** Whether the action is currently activated. */
    private Boolean mActivated = false;

    /** Whether the action has been activated this frame. */
    private Boolean mJustActivated = false;

    /** Whether the action has been <b>de</b>activated this frame. */
    private Boolean mJustDeactivated = false;

    /** Create a new (unnamed) action. */
    public Action() {}

    /**
     * Create an action and give it a display name.
     *
     * @param name The name of the action.
     */
    public Action(String name) {
        this();
        this.mName = name;
    }

    /**
     * Get if the action is currently activated.
     *
     * @return Whether the action is currently activated.
     */
    public boolean isActivated() {
        return mActivated;
    }

    /**
     * Set the action to either activated ({@code true}) or not activated ({@code false}).
     *
     * @param activated Whether the action should be activated.
     */
    void setActivated(boolean activated) {
        if (mActivated == false && activated == true) {
            // If the action is currently false and it will become true, this shows the action has
            // just been activated.
            setJustActivated(true);
        } else if (mActivated == true && activated == false) {
            // If the action is currently true and it will become false, this shows the action has
            // just been deactivated.
            setJustDeactivated(true);
        }

        mActivated = activated;
    }

    /**
     * Get whether the action has been activated this frame.
     *
     * @return {@code true} if the action was activated this frame, otherwise {@code false}.
     */
    public boolean isJustActivated() {
        return mJustActivated;
    }

    /**
     * Set whether the action has just been activated this frame.
     *
     * <p>If the action has just been activated- so {@code activated} is {@code true}- the {@link
     * Action} will be added to {@link Actions}' list of just activated actions.
     *
     * @param activated Whether the action has been activated this frame.
     */
    void setJustActivated(boolean activated) {
        if (activated == true) {
            Actions.addJustActivated(this);
        }
        mJustActivated = activated;
    }

    /**
     * Get whether the action has been deactivated this frame.
     *
     * @return {@code true} if the action was deactivated this frame, otherwise {@code false}.
     */
    public boolean isJustDeactivated() {
        return mJustDeactivated;
    }

    /**
     * Set whether the action has just been deactivated this frame.
     *
     * <p>If the action has just been deactivated- so {@code deactivated} is {@code true}- the
     * {@link Action} will be added to {@link Actions}' list of just deactivated actions.
     *
     * @param deactivated Whether the action has been deactivated this frame.
     */
    void setJustDeactivated(boolean deactivated) {
        if (deactivated == true) {
            Actions.addJustDeactivated(this);
        }
        mJustDeactivated = deactivated;
    }

    @Override
    public String toString() {
        // If no name is available, display the action name as blank.
        String name = mName != null ? mName : "---";

        return String.format("Action{name:%s}", name);
    }
}
