/* (C) 2021 DragonSkulle */
package org.dragonskulle.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

// The below tests were generated by https://www.diffblue.com/

public class DiffBlueActionTest {

    @Test
    public void testSetActivated2() {
        Action action = new Action("Name");
        action.setActivated(false);
        assertFalse(action.isActivated());
    }

    @Test
    public void testSetJustActivated2() {
        Action action = new Action("Name");
        action.setJustActivated(false);
        assertFalse(action.isJustActivated());
    }

    @Test
    public void testSetJustDeactivated() {
        Action action = new Action("Name");
        action.setJustDeactivated(true);
        assertTrue(action.isJustDeactivated());
    }

    @Test
    public void testSetJustDeactivated2() {
        Action action = new Action("Name");
        action.setJustDeactivated(false);
        assertFalse(action.isJustDeactivated());
    }

    @Test
    public void testToString() {
        assertEquals("Action{name:Name; ignore:true}", (new Action("Name")).toString());
        assertEquals("Action{name:---; ignore:true}", (new Action(null)).toString());
    }
}
