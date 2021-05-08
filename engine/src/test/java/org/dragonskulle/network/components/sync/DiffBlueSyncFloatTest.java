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

public class DiffBlueSyncFloatTest {
    @Test
    public void testSet() {
        SyncFloat syncFloat = new SyncFloat(10.0f);
        syncFloat.set(10.0f);
        assertEquals(10.0f, syncFloat.get(), 0.0f);
        assertTrue(syncFloat.mDirty);
    }

    @Test
    public void testSerialize() throws IOException {
        SyncFloat syncFloat = new SyncFloat(10.0f);
        syncFloat.serialize(new DataOutputStream(new ByteArrayOutputStream(1)), 123);
        assertEquals(10.0f, syncFloat.get(), 0.0f);
        assertFalse(syncFloat.mDirty);
    }

    @Test
    public void testDeserialize() throws IOException {
        SyncFloat syncFloat = new SyncFloat(10.0f);
        syncFloat.deserialize(
                new DataInputStream(
                        new ByteArrayInputStream("AAAAAAAAAAAAAAAAAAAAAAAA".getBytes("UTF-8"))));
        assertEquals(12.078431f, syncFloat.get(), 0.0f);
    }

    @Test
    public void testEquals() {
        assertFalse((new SyncFloat(10.0f)).equals("42"));
        assertFalse((new SyncFloat(10.0f)).equals(null));
    }

    @Test
    public void testHashCode() {
        assertEquals(1092616223, (new SyncFloat(10.0f)).hashCode());
    }
}
