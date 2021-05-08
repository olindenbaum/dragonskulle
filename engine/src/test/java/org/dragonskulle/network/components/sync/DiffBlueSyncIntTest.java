/* (C) 2021 DragonSkulle */
package org.dragonskulle.network.components.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.junit.Test;

// The below tests were generated by https://www.diffblue.com/

public class DiffBlueSyncIntTest {
    @Test
    public void testSet() {
        SyncInt syncInt = new SyncInt(42);
        syncInt.set(1);
        assertEquals(1, syncInt.get());
        assertTrue(syncInt.mDirty);
    }

    @Test
    public void testAdd() {
        SyncInt syncInt = new SyncInt(42);
        syncInt.add(42);
        assertEquals(84, syncInt.get());
        assertTrue(syncInt.mDirty);
    }

    @Test
    public void testSubtract() {
        SyncInt syncInt = new SyncInt(42);
        syncInt.subtract(42);
        assertEquals(0, syncInt.get());
        assertTrue(syncInt.mDirty);
    }

    @Test
    public void testSerialize() throws IOException {
        SyncInt syncInt = new SyncInt(42);
        syncInt.serialize(new DataOutputStream(new ByteArrayOutputStream(1)), 123);
        assertEquals(42, syncInt.get());
        assertFalse(syncInt.mDirty);
    }

    @Test
    public void testDeserialize() throws IOException {
        SyncInt syncInt = new SyncInt(42);
        syncInt.deserialize(
                new DataInputStream(
                        new ByteArrayInputStream("AAAAAAAAAAAAAAAAAAAAAAAA".getBytes("UTF-8"))));
        assertEquals(1094795585, syncInt.get());
    }

    @Test
    public void testEquals() {
        assertFalse((new SyncInt(42)).equals("42"));
        assertFalse((new SyncInt(42)).equals(null));
    }

    @Test
    public void testHashCode() {
        assertEquals(42, (new SyncInt(42)).hashCode());
    }
}
