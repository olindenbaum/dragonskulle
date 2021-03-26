/* (C) 2021 DragonSkulle */
package org.dragonskulle.assets;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.dragonskulle.components.Component;
import org.dragonskulle.components.TransformHex;
import org.dragonskulle.core.GameObject;
import org.dragonskulle.core.Reference;
import org.dragonskulle.core.Resource;
import org.dragonskulle.core.Scene;
import org.dragonskulle.network.components.NetworkHexTransform;
import org.dragonskulle.ui.UIText;
import org.joml.Vector3f;
import org.junit.Test;

/** Unit tests for {@link GLTF} files. */
public class GLTFTest {
    @Test
    public void loadGLTF() {
        try (Resource<GLTF> res = GLTF.getResource("testin")) {
            assertNotNull(res);
        }
    }

    @Test
    public void correctSceneName() {
        try (Resource<GLTF> res = GLTF.getResource("testin")) {
            assert (res.get().getDefaultScene().getName().equals("TestScene"));
        }
    }

    @Test
    public void correctRootObjects() {
        try (Resource<GLTF> res = GLTF.getResource("testin")) {
            Scene scene = res.get().getDefaultScene();

            String[] targetRootObjects = {"Camera", "Cube", "Light", "Suzanne", "Torus.001"};
            Set<String> targetRootObjectSet = new HashSet<>();
            for (String s : targetRootObjects) targetRootObjectSet.add(s);

            Set<String> actualRootObjectSet = new HashSet<>();
            for (GameObject go : scene.getGameObjects()) actualRootObjectSet.add(go.getName());

            assertEquals(targetRootObjectSet, actualRootObjectSet);
        }
    }

    @Test
    public void cubeHasSphere() {
        try (Resource<GLTF> res = GLTF.getResource("testin")) {
            Scene scene = res.get().getDefaultScene();
            GameObject cube =
                    scene.getGameObjects().stream()
                            .filter(go -> go.getName().equals("Cube"))
                            .findFirst()
                            .orElse(null);
            GameObject sphere =
                    cube.getChildren().stream()
                            .filter(go -> go.getName().equals("Sphere"))
                            .findFirst()
                            .orElse(null);
            assertNotNull(sphere);
            assertEquals(new Vector3f(0f, 0f, 2f), sphere.getTransform().getPosition());
        }
    }

    @Test
    public void cubeHasUIText() {
        try (Resource<GLTF> res = GLTF.getResource("testin")) {
            Scene scene = res.get().getDefaultScene();
            GameObject cube =
                    scene.getGameObjects().stream()
                            .filter(go -> go.getName().equals("Cube"))
                            .findFirst()
                            .orElse(null);
            ArrayList<Reference<Component>> outList = new ArrayList<>();
            cube.getComponents(Component.class, outList);
            for (Reference<Component> comp : outList) {
                System.out.println(comp.get());
            }
            Reference<UIText> uiText = cube.getComponent(UIText.class);
            assertNotNull(uiText);
            assert (uiText.get().getVerticalAlignment() == 1337f);
            assertEquals("Did you know that 3 billion devices run Java?", uiText.get().getText());
        }
    }

    @Test
    public void sphereHasHexTransform() {
        try (Resource<GLTF> res = GLTF.getResource("testin")) {
            Scene scene = res.get().getDefaultScene();
            GameObject cube =
                    scene.getGameObjects().stream()
                            .filter(go -> go.getName().equals("Cube"))
                            .findFirst()
                            .orElse(null);
            GameObject sphere =
                    cube.getChildren().stream()
                            .filter(go -> go.getName().equals("Sphere"))
                            .findFirst()
                            .orElse(null);
            assertNotNull(sphere.getTransform(TransformHex.class));
        }
    }

    @Test
    public void sphereHasNetworkHexTransform() {
        try (Resource<GLTF> res = GLTF.getResource("testin")) {
            Scene scene = res.get().getDefaultScene();
            GameObject cube =
                    scene.getGameObjects().stream()
                            .filter(go -> go.getName().equals("Cube"))
                            .findFirst()
                            .orElse(null);
            GameObject sphere =
                    cube.getChildren().stream()
                            .filter(go -> go.getName().equals("Sphere"))
                            .findFirst()
                            .orElse(null);
            Reference<NetworkHexTransform> networkHexTransform =
                    sphere.getComponent(NetworkHexTransform.class);
            assertNotNull(networkHexTransform);
            assertEquals(
                    new Vector3f(1f, 2f, 3f), networkHexTransform.get().mAxialCoordinate.get());
        }
    }
}
