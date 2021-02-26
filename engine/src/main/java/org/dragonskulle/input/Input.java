/* (C) 2021 DragonSkulle */
package org.dragonskulle.input;

import java.util.logging.Logger;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Manages all user input a window receives.
 *
 * <p>Provides:
 *
 * <ul>
 *   <li>Access to whether {@link Action}s are active.
 *   <li>Access to the cursor.
 *   <li>Access to the raw scrolling value (via {@link #mScroll}).
 * </ul>
 *
 * @author Craig Wilboure
 */
@Accessors(prefix = "m")
public class Input {

    /** Used to log messages. */
    public static final Logger LOGGER = Logger.getLogger("input");

    /** Stores the bindings between buttons and actions. */
    private Bindings mBindings;

    /** Stores which actions are active. */
    private Actions mActions;

    /** Allows button input to be detected. */
    @Getter private Buttons mButtons;

    /** Allows cursor position to be detected. */
    @Getter private Cursor mCursor;

    /** Allows scrolling to be detected. */
    @Getter private Scroll mScroll;

    /**
     * Create a new input manager.
     *
     * @param window A {@link Long} GLFW window id, or {@code null} if there is no window.
     */
    public Input(Long window) {
        mActions = new Actions();
        mBindings = new Bindings();

        mCursor = new Cursor(mActions);
        mButtons = new Buttons(mActions, mBindings);
        mScroll = new Scroll(mButtons);

        // If a window is provided, attach the event listeners.
        if (window != null) {
            mCursor.attachToWindow(window);
            mButtons.attachToWindow(window);
            mScroll.attachToWindow(window);
        }

        // For infinite mouse movement.
        // GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        // For hiding the cursor.
        // GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }

    /**
     * Query whether an {@link Action} is activated.
     *
     * @param action The action to be tested.
     * @return {@code true} if the action is activated, otherwise {@code false}.
     */
    public boolean isActivated(Action action) {
        return mActions.isActivated(action);
    }

    /**
     * Resets any recorded values ready for their new values.
     *
     * <p>Currently only used for {@link Scroll}.
     */
    public void reset() {
        mScroll.reset();
    }
}
