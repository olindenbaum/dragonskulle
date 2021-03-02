/* (C) 2021 DragonSkulle */
package org.dragonskulle.renderer;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.ByteBuffer;
import lombok.Builder;
import org.dragonskulle.renderer.VulkanPipeline.AttributeDescription;
import org.dragonskulle.renderer.VulkanPipeline.BindingDescription;
import org.joml.*;

/**
 * Describes a single vertex
 *
 * @author Aurimas Blažulionis
 */
@Builder
public class Vertex {
    public static int SIZEOF = (3 + 3 + 2) * 4;
    public static int OFFSETOF_POS = 0;
    public static int OFFSETOF_COL = 3 * 4;
    public static int OFFSETOF_UV = OFFSETOF_COL + 3 * 4;

    public static final BindingDescription BINDING_DESCRIPTION =
            new BindingDescription(0, SIZEOF, VK_VERTEX_INPUT_RATE_VERTEX);

    public static final AttributeDescription[] ATTRIBUTE_DESCRIPTIONS = {
        new AttributeDescription(0, 0, VK_FORMAT_R32G32B32_SFLOAT, OFFSETOF_POS),
        new AttributeDescription(0, 1, VK_FORMAT_R32G32B32_SFLOAT, OFFSETOF_COL),
        new AttributeDescription(0, 2, VK_FORMAT_R32G32_SFLOAT, OFFSETOF_UV),
    };

    private Vector3fc pos;
    private Vector3fc color;
    private Vector2fc uv;

    /** Copy the vertice to a byte buffer */
    public void copyTo(ByteBuffer buffer) {
        buffer.putFloat(pos.x());
        buffer.putFloat(pos.y());
        buffer.putFloat(pos.z());

        buffer.putFloat(color.x());
        buffer.putFloat(color.y());
        buffer.putFloat(color.z());

        buffer.putFloat(uv.x());
        buffer.putFloat(uv.y());
    }
}
