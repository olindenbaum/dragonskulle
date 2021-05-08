/* (C) 2021 DragonSkulle */
package org.dragonskulle.renderer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

// The below tests were generated by https://www.diffblue.com/

public class DiffBlueShaderKindTest {
    @Test
    public void testToString() {
        assertEquals("vert", ShaderKind.VERTEX_SHADER.toString());
        assertEquals("geom", ShaderKind.GEOMETRY_SHADER.toString());
        assertEquals("frag", ShaderKind.FRAGMENT_SHADER.toString());
    }
}
