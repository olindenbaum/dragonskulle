/* (C) 2021 DragonSkulle */
package org.dragonskulle.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Stores the bindings between buttons and the {@link Action}s they trigger.
 *
 * @author Craig Wilbourne
 */
class Bindings {

    /** Used to log messages. */
    private static final Logger LOGGER = Logger.getLogger("bindings");

    /** Stores all potential bindings. */
    private final ArrayList<Binding> mBindings = new ArrayList<Binding>();
    
    /**
     * Key: Button <br>
     * Value: {@link Action}s the Button activates.
     */
    private final HashMap<Integer, ArrayList<Action>> mButtonToActions =
            new HashMap<Integer, ArrayList<Action>>();

    /**
     * Key: {@link Action} <br>
     * Value: Buttons that activate the Action.
     */
    private final HashMap<Action, ArrayList<Integer>> mActionToButtons =
            new HashMap<Action, ArrayList<Integer>>();
    
    public Bindings(BindingsTemplate bindings) {
    	// Store each custom binding.
    	for (Binding binding : bindings.getBindings()) {
			add(binding);
		}

        rebind();
    }

    /**
     * @param button The button being targeted.
     * @return An {@code ArrayList} of {@link Action}s associated with the button, or an empty
     *     {@code ArrayList}.
     */
    public ArrayList<Action> getActions(Integer button) {
        if (!mButtonToActions.containsKey(button)) {
            return new ArrayList<Action>();
        }
        return mButtonToActions.get(button);
    }

    /**
     * *
     *
     * @param action The action being targeted.
     * @return An {@code ArrayList} of buttons associated with the {@link Action}, or an empty
     *     {@code ArrayList}.
     */
    public ArrayList<Integer> getButtons(Action action) {
        if (!mActionToButtons.containsKey(action)) {
            return new ArrayList<Integer>();
        }
        return mActionToButtons.get(action);
    }

    /**
     * Add a {@link Binding} to the list of {@link #mBindings}.
     *
     * <p>Will not take effect until {@link #rebind()} is called.
     *
     * <p>Bindings added later will overwrite old bindings.
     *
     * @param binding The binding to be added.
     */
    private void add(Binding binding) {
        mBindings.add(binding);
    }

    /**
     * Add all of the bindings stored by {@link #mBindings}.
     *
     * <p>This temporarily resets {@link #mButtonToActions} and {@link #mActionToButtons}, and then
     * repopulates them with the latest bindings.
     */
    private void rebind() {
        mButtonToActions.clear();
        mActionToButtons.clear();

        for (Binding binding : mBindings) {
            mButtonToActions.put(binding.getButton(), binding.getActions());
        }
        generateActionToButtons();

        LOGGER.info(
                String.format(
                        "Rebinded.\nButton to Actions: %s\nAction to Buttons: %s",
                        mButtonToActions.toString(), mActionToButtons.toString()));
    }

    /** Use {@link #buttonToAction} to generate the contents of {@link #actionToButton}. */
    private void generateActionToButtons() {
        // Get each button and action combination in mButtonToActions.
        for (Entry<Integer, ArrayList<Action>> entry : mButtonToActions.entrySet()) {
            // For each action, store a list of the buttons that trigger it.
            for (Action action : entry.getValue()) {
                ArrayList<Integer> buttonsList =
                        new ArrayList<
                                Integer>(); // Store a list of buttons that trigger the action.
                buttonsList.add(entry.getKey()); // Add the current button to the list.

                // If the action already has buttons assigned to it, add those buttons to the list.
                if (mActionToButtons.containsKey(action)) {
                    buttonsList.addAll(mActionToButtons.get(action));
                }

                // Store the results.
                mActionToButtons.put(action, buttonsList);
            }
        }
    }
}
