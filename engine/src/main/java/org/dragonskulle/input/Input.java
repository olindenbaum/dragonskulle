package org.dragonskulle.input;

import java.util.logging.Logger;

import org.dragonskulle.input.listeners.KeyboardListener;
import org.dragonskulle.input.listeners.MouseButtonListener;
import org.dragonskulle.input.listeners.MousePositionListener;
import org.dragonskulle.input.listeners.MouseScrollListener;
import org.dragonskulle.input.storage.Actions;
import org.dragonskulle.input.storage.Buttons;
import org.dragonskulle.input.storage.Converter;
import org.dragonskulle.input.storage.MousePosition;
import org.dragonskulle.input.storage.Scroll;
import org.joml.Vector2d;

/**
 * Manages all user input.
 * @author Craig Wilboure
 */
public class Input {
	
	public static final Logger LOGGER = Logger.getLogger("input");		
	
	/** Convert between buttons and actions. */
	private Converter converter = new Converter();
	/** Store triggered actions. */
	private Actions actions = new Actions();
	/** Store triggered buttons. */
	private Buttons buttons = new Buttons(converter, actions);
	/** Store mouse scroll movement. */
	private Scroll scroll = new Scroll();
	/** Store mouse position. */
	private MousePosition mousePosition = new MousePosition();
	
	public Input(long window) {
		new KeyboardListener(window, buttons);
		new MouseButtonListener(window, buttons);
		new MouseScrollListener(window, buttons, scroll);
		new MousePositionListener(window, mousePosition, actions);
		
		
		/*
		GLFWCursorPosCallback mousePosition = new GLFWCursorPosCallback() {
			
			@Override
			public void invoke(long window, double xpos, double ypos) {
				//System.out.println("Mouse position.");
				//System.out.println(String.format("xpos: %f\nypos: %f", xpos, ypos));
			}
		};
		
		GLFW.glfwSetCursorPosCallback(window, mousePosition);
		*/
		
		// For infinite mouse movement.
		// GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		// For hiding the cursor.
		// GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}
	
	/**
	 * Query whether an {@link Action} is activated.
	 * @param action The action to be tested.
	 * @return {@code true} if the action is activated, otherwise {@code false}.
	 */
	public boolean isActivated(Action action) {
		return actions.isActivated(action);
	}
	
	/**
	 * Resets the {@link #scroll} detection for the next input polling cycle.
	 */
	public void resetScroll() {
		scroll.reset();
		buttons.released(Scroll.UP);
		buttons.released(Scroll.DOWN);
	}
	
	/**
	 * 
	 * @return The amount of scrolling done since {@link Scroll#reset} was called.
	 */
	public double getScroll() {
		return scroll.getAmount();
	}
	
	public Vector2d getMousePosition() {
		return mousePosition.getPosition();
	}
	
	public MousePosition getMouse() {
		return mousePosition;
	}
	
	public void mouseTEST() {
		//LOGGER.info(String.format("%b", mousePosition.isDragInProgress()));
		if(mousePosition.isDragInProgress()) {
			double distance = mousePosition.getDragDistance();
			double angle = mousePosition.getDragAngle();
			LOGGER.info(String.format("Distance of drag: %f\nAt angle: %f", distance, angle));
			
			//double test = mousePosition.getPosition().distance(mousePosition.getDragStart());
			//LOGGER.info(String.format("Value: %f", test));
		}
	}
	
}
