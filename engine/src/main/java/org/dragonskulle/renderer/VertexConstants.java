/* (C) 2021 DragonSkulle */
package org.dragonskulle.renderer;

import java.nio.ByteBuffer;
import org.joml.*;

/**
 * Constant properties vertex shaders receive
 *
 * <p>These should essentially be universal properties for all shaders, like camera transformation
 *
 * @author Aurimas Blažulionis
 */
class VertexConstants {
    public static int SIZEOF = 4 * 4 * 4 * 2;
    public static int VIEW_OFFSET = 0;
    public static int PROJ_OFFSET = 4 * 4 * 4;

    public Matrix4fc view = new Matrix4f();
    public Matrix4fc proj = new Matrix4f();

    public void copyTo(ByteBuffer buffer, int offset) {
        view.get(VIEW_OFFSET + offset, buffer);
        proj.get(PROJ_OFFSET + offset, buffer);
    }

    public void copyTo(ByteBuffer buffer) {
        copyTo(buffer, 0);
    }
}