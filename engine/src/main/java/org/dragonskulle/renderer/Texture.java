/* (C) 2021 DragonSkulle */
package org.dragonskulle.renderer;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.dragonskulle.core.Resource;
import org.dragonskulle.core.ResourceManager;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;

@Accessors(prefix = "m")
@Getter
public class Texture implements NativeResource {
    private int mWidth;
    private int mHeight;
    private int mChannels;
    private ByteBuffer mBuffer;

    public static Resource<Texture> getResource(String name) {
        name = String.format("textures/%s", name);

        return ResourceManager.getResource(
                Texture.class,
                (buffer) -> {
                    Texture ret = new Texture();
                    ByteBuffer buf = MemoryUtil.memAlloc(buffer.length);
                    buf.put(buffer);
                    buf.rewind();
                    try (MemoryStack stack = stackPush()) {
                        IntBuffer pX = stack.ints(0);
                        IntBuffer pY = stack.ints(0);
                        IntBuffer pC = stack.ints(0);
                        ret.mBuffer = stbi_load_from_memory(buf, pX, pY, pC, STBI_rgb_alpha);
                        ret.mWidth = pX.get(0);
                        ret.mHeight = pY.get(0);
                        ret.mChannels = pC.get(0);
                    }
                    return ret;
                },
                name);
    }

    @Override
    public final void free() {
        if (mBuffer != null) {
            stbi_image_free(mBuffer);
            mBuffer = null;
        }
    }

    public int size() {
        return mBuffer.capacity();
    }
}