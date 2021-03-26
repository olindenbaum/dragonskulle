/* (C) 2021 DragonSkulle */
package org.dragonskulle.game;

import static org.dragonskulle.utils.Env.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import org.dragonskulle.assets.GLTF;
import org.dragonskulle.audio.AudioManager;
import org.dragonskulle.audio.AudioSource;
import org.dragonskulle.audio.SoundType;
import org.dragonskulle.components.*;
import org.dragonskulle.core.Engine;
import org.dragonskulle.core.GameObject;
import org.dragonskulle.core.Reference;
import org.dragonskulle.core.Resource;
import org.dragonskulle.core.Scene;
import org.dragonskulle.core.TemplateManager;
import org.dragonskulle.game.building.Building;
import org.dragonskulle.game.camera.KeyboardMovement;
import org.dragonskulle.game.camera.ScrollTranslate;
import org.dragonskulle.game.camera.ZoomTilt;
import org.dragonskulle.game.input.GameBindings;
import org.dragonskulle.game.map.HexagonMap;
import org.dragonskulle.game.map.MapEffects;
import org.dragonskulle.game.player.AiPlayer;
import org.dragonskulle.game.player.HumanPlayer;
import org.dragonskulle.game.player.Player;
import org.dragonskulle.network.ServerClient;
import org.dragonskulle.network.components.NetworkHexTransform;
import org.dragonskulle.network.components.NetworkManager;
import org.dragonskulle.renderer.Font;
import org.dragonskulle.renderer.Mesh;
import org.dragonskulle.renderer.SampledTexture;
import org.dragonskulle.renderer.components.*;
import org.dragonskulle.renderer.materials.UnlitMaterial;
import org.dragonskulle.ui.*;
import org.joml.*;
import org.lwjgl.system.NativeResource;

public class App implements NativeResource {

    private static String sIP = "127.0.0.1";
    private static int sPort = 7000;

    private final Resource<GLTF> mMainMenuGLTF = GLTF.getResource("main_menu");

    private Scene createMainScene() {
        // Create a scene
        Scene mainScene = new Scene("game");

        GameObject cameraRig =
                new GameObject(
                        "mainCamera",
                        (rig) -> {
                            KeyboardMovement keyboardMovement = new KeyboardMovement();
                            rig.addComponent(keyboardMovement);

                            rig.getTransform(Transform3D.class).setPosition(0, -4, 1.5f);

                            rig.buildChild(
                                    "rotationRig",
                                    (pitchRig) -> {
                                        ZoomTilt zoomTilt = new ZoomTilt();
                                        pitchRig.addComponent(zoomTilt);
                                        pitchRig.buildChild(
                                                "camera",
                                                (camera) -> {
                                                    ScrollTranslate scroll =
                                                            new ScrollTranslate(
                                                                    keyboardMovement, zoomTilt);
                                                    scroll.getStartPos().set(0f, -5f, 0f);
                                                    scroll.getEndPos().set(0f, -100f, 0f);
                                                    camera.addComponent(scroll);

                                                    // Make sure it's an actual camera
                                                    Camera cam = new Camera();
                                                    cam.farPlane = 200;
                                                    camera.addComponent(cam);

                                                    camera.addComponent(new MapEffects());
                                                });
                                    });
                        });

        mainScene.addRootObject(GameObject.instantiate(cameraRig));

        GameObject audioObject =
                new GameObject(
                        "audioObject",
                        new TransformUI(true),
                        (root) -> {
                            root.addComponent(new AudioSource());

                            TransformUI t = root.getTransform(TransformUI.class);
                            t.setParentAnchor(0.78f, 0.75f, 1f, 0.75f);
                            t.setMargin(0f, 0.1f, 0f, 0.2f);

                            root.addComponent(
                                    new UIButton(
                                            new UIText(
                                                    new Vector3f(0f, 0f, 0f),
                                                    Font.getFontResource("Rise of Kingdom.ttf"),
                                                    "Mute/Unmute"),
                                            (uiButton, __) -> {
                                                AudioManager.getInstance()
                                                        .toggleMute(SoundType.BACKGROUND);
                                                AudioManager.getInstance()
                                                        .toggleMute(SoundType.SFX);
                                            }));
                        });
        GameObject audioButtonEffect =
                new GameObject(
                        "audioObject",
                        (root) -> {
                            root.addComponent(new AudioSource());
                        });

        Reference<AudioSource> refAudio = audioObject.getComponent(AudioSource.class);
        Reference<AudioSource> refAudioButtonEffect =
                audioButtonEffect.getComponent(AudioSource.class);

        if (refAudio.isValid()) {
            AudioManager.getInstance().setVolume(SoundType.BACKGROUND, 70);
            AudioManager.getInstance().setVolume(SoundType.SFX, 60);
            refAudio.get().loadAudio("game_background.wav", SoundType.BACKGROUND);
            refAudioButtonEffect.get().loadAudio("button-10.wav", SoundType.SFX);
            // refAudio.get().play();
        }

        mainScene.addRootObject(audioObject);

        GameObject hexagonMap =
                new GameObject(
                        "hexagon map",
                        new Transform3D(),
                        (map) -> {
                            map.addComponent(new HexagonMap(51));
                        });

        mainScene.addRootObject(hexagonMap);

        return mainScene;
    }

    private Scene createMainMenu(Scene mainScene) {
        Scene mainMenu = mMainMenuGLTF.get().getDefaultScene();

        TemplateManager templates = new TemplateManager();

        templates.addAllObjects(
                new GameObject(
                        "cube",
                        (handle) -> {
                            UnlitMaterial mat = new UnlitMaterial();
                            mat.getFragmentTextures()[0] = new SampledTexture("cat_material.jpg");
                            handle.addComponent(new Renderable(Mesh.CUBE, mat));
                        }),
                new GameObject(
                        "capital",
                        (handle) -> {
                            UnlitMaterial mat = new UnlitMaterial();

                            mat.getFragmentTextures()[0] = new SampledTexture("cat_material.jpg");
                            handle.addComponent(new Renderable(Mesh.HEXAGON, mat));
                        }),
                new GameObject(
                        "building",
                        new TransformHex(0, 0, 1),
                        (handle) -> {
                            UnlitMaterial mat = new UnlitMaterial();
                            mat.getColour().set(1, 0, 0, 1);
                            handle.addComponent(new Renderable(Mesh.CUBE, mat));
                            handle.addComponent(new Building());
                            handle.addComponent(new NetworkHexTransform());
                        }),
                new GameObject(
                        "player",
                        new TransformHex(0, 0, 1),
                        (handle) -> {
                            handle.addComponent(new Player());
                        }),
                new GameObject(
                        "aiPlayer",
                        new TransformHex(0, 0, 1),
                        (handle) -> {
                            handle.addComponent(new AiPlayer());
                            handle.addComponent(new Player());
                        }));

        Reference<NetworkManager> networkManager =
                new NetworkManager(templates, mainScene).getReference(NetworkManager.class);

        GameObject networkManagerObject =
                new GameObject(
                        "client network manager",
                        (handle) -> {
                            handle.addComponent(networkManager.get());
                        });

        GameObject audioObject =
                new GameObject(
                        "audioObject",
                        new TransformUI(true),
                        (root) -> {
                            root.addComponent(new AudioSource());

                            TransformUI t = root.getTransform(TransformUI.class);
                            t.setParentAnchor(0.78f, 0.75f, 1f, 0.75f);
                            t.setMargin(0f, 0.1f, 0f, 0.2f);

                            root.addComponent(
                                    new UIButton(
                                            new UIText(
                                                    new Vector3f(0f, 0f, 0f),
                                                    Font.getFontResource("Rise of Kingdom.ttf"),
                                                    "Mute/Unmute"),
                                            (uiButton, __) -> {
                                                AudioManager.getInstance()
                                                        .toggleMute(SoundType.BACKGROUND);
                                                AudioManager.getInstance()
                                                        .toggleMute(SoundType.SFX);
                                            }));
                        });

        GameObject audioButtonEffect =
                new GameObject(
                        "audioObject",
                        (root) -> {
                            root.addComponent(new AudioSource());
                        });

        Reference<AudioSource> refAudio = audioObject.getComponent(AudioSource.class);
        Reference<AudioSource> refAudioButtonEffect =
                audioButtonEffect.getComponent(AudioSource.class);
        if (refAudio.isValid()) {
            AudioManager.getInstance().setVolume(SoundType.BACKGROUND, 70);
            AudioManager.getInstance().setVolume(SoundType.SFX, 60);
            refAudio.get().loadAudio("game_background.wav", SoundType.BACKGROUND);
            refAudioButtonEffect.get().loadAudio("button-10.wav", SoundType.SFX);
            // refAudio.get().play();
        }

        GameObject gameTitle =
                new GameObject(
                        "title",
                        new TransformUI(true),
                        (title) -> {
                            TransformUI t = title.getTransform(TransformUI.class);
                            t.setParentAnchor(0.4f, 0.05f, 0.8f, 0.05f);
                            t.setMargin(0f, 0f, 0f, 0.2f);

                            title.addComponent(
                                    new UIText(
                                            new Vector3f(1f, 1f, 1f),
                                            Font.getFontResource("Rise of Kingdom.ttf"),
                                            "Hex Wars"));
                        });

        mainMenu.addRootObject(gameTitle);

        GameObject mainUI =
                new GameObject(
                        "mainUI",
                        new TransformUI(false),
                        (root) -> {
                            root.addComponent(new UIRenderable(new Vector4f(1f, 1f, 1f, 0.1f)));
                            root.getTransform(TransformUI.class).setParentAnchor(0f);
                        });

        GameObject joinUI =
                new GameObject(
                        "joinUI",
                        new TransformUI(false),
                        (root) -> {
                            root.addComponent(new UIRenderable(new Vector4f(1f, 1f, 1f, 0.1f)));
                            root.getTransform(TransformUI.class).setParentAnchor(0f);
                        });

        GameObject hostUI =
                new GameObject(
                        "hostUI",
                        new TransformUI(false),
                        (root) -> {
                            root.addComponent(new UIRenderable(new Vector4f(1f, 1f, 1f, 0.1f)));
                            root.getTransform(TransformUI.class).setParentAnchor(0f);
                        });

        GameObject hostGameUI =
                new GameObject(
                        "hostGameUI",
                        new TransformUI(false),
                        (root) -> {
                            root.addComponent(new UIRenderable(new Vector4f(1f, 1f, 1f, 0.1f)));
                            root.getTransform(TransformUI.class).setParentAnchor(0f);
                            root.buildChild(
                                    "populate_with_ai",
                                    new TransformUI(true),
                                    (box) -> {
                                        box.getTransform(TransformUI.class)
                                                .setParentAnchor(0.3f, 0.93f, 1f, 0.93f);
                                        box.getTransform(TransformUI.class)
                                                .setMargin(0f, 0f, 0f, 0.07f);
                                        box.addComponent(
                                                new UIRenderable(
                                                        new SampledTexture("ui/wide_button.png")));
                                        box.addComponent(
                                                new UIButton(
                                                        new UIText(
                                                                new Vector3f(0f, 0f, 0f),
                                                                Font.getFontResource(
                                                                        "Rise of Kingdom.ttf"),
                                                                "Fill game with AI"),
                                                        (a, b) -> {
                                                            System.out.println(
                                                                    "should fill with ai");
                                                            networkManager
                                                                    .get()
                                                                    .getServerManager()
                                                                    .spawnNetworkObject(
                                                                            -1,
                                                                            networkManager
                                                                                    .get()
                                                                                    .findTemplateByName(
                                                                                            "aiPlayer"));
                                                        }));
                                    });
                        });

        mainUI.buildChild(
                "bg",
                new TransformUI(false),
                (bg) -> {
                    bg.addComponent(new UIRenderable(new Vector4f(0.1f, 0.1f, 0.1f, 0f)));

                    bg.getTransform(TransformUI.class).setParentAnchor(0f, 0f, 0.5f, 1.f);

                    bg.buildChild(
                            "joinButton",
                            new TransformUI(true),
                            (button) -> {
                                button.getTransform(TransformUI.class)
                                        .setParentAnchor(0f, 0.05f, 0.5f, 0.05f);
                                button.getTransform(TransformUI.class).setMargin(0f, 0f, 0f, 0.07f);

                                UIButton newButton =
                                        new UIButton(
                                                new UIText(
                                                        new Vector3f(0f, 0f, 0f),
                                                        Font.getFontResource("Rise of Kingdom.ttf"),
                                                        "Join Game"),
                                                refAudioButtonEffect
                                                        .get()
                                                        .audibleClick(
                                                                (uiButton, __) -> {
                                                                    mainUI.setEnabled(false);
                                                                    joinUI.setEnabled(true);
                                                                    hostGameUI.setEnabled(false);
                                                                }));
                                button.addComponent(newButton);
                            });

                    bg.buildChild(
                            "hostButton",
                            new TransformUI(true),
                            (button) -> {
                                button.getTransform(TransformUI.class)
                                        .setParentAnchor(0f, 0.15f, 0.5f, 0.15f);
                                button.getTransform(TransformUI.class).setMargin(0f, 0f, 0f, 0.07f);

                                UIButton newButton =
                                        new UIButton(
                                                new UIText(
                                                        new Vector3f(0f, 0f, 0f),
                                                        Font.getFontResource("Rise of Kingdom.ttf"),
                                                        "Host Game"),
                                                refAudioButtonEffect
                                                        .get()
                                                        .audibleClick(
                                                                (uiButton, __) -> {
                                                                    mainUI.setEnabled(false);
                                                                    hostUI.setEnabled(true);
                                                                    hostGameUI.setEnabled(true);
                                                                }));
                                button.addComponent(newButton);
                            });

                    bg.buildChild(
                            "settingsButton",
                            new TransformUI(true),
                            (button) -> {
                                button.getTransform(TransformUI.class)
                                        .setParentAnchor(0f, 0.25f, 0.5f, 0.25f);
                                button.getTransform(TransformUI.class).setMargin(0f, 0f, 0f, 0.07f);

                                UIButton newButton =
                                        new UIButton(
                                                new UIText(
                                                        new Vector3f(0f, 0f, 0f),
                                                        Font.getFontResource("Rise of Kingdom.ttf"),
                                                        "Settings"),
                                                refAudioButtonEffect
                                                        .get()
                                                        .audibleClick((uiButton, __) -> {}));

                                button.addComponent(newButton);
                            });

                    bg.buildChild(
                            "quitButton",
                            new TransformUI(true),
                            (button) -> {
                                button.getTransform(TransformUI.class)
                                        .setParentAnchor(0f, 0.35f, 0.5f, 0.35f);
                                button.getTransform(TransformUI.class).setMargin(0f, 0f, 0f, 0.07f);

                                UIButton newButton =
                                        new UIButton(
                                                new UIText(
                                                        new Vector3f(0f, 0f, 0f),
                                                        Font.getFontResource("Rise of Kingdom.ttf"),
                                                        "Quit"),
                                                (uiButton, __) -> {
                                                    sReload = false;
                                                    Engine.getInstance().stop();
                                                });

                                button.addComponent(newButton);
                            });
                });
        joinUI.buildChild(
                "bg",
                new TransformUI(false),
                (bg) -> {
                    bg.addComponent(new UIRenderable(new Vector4f(0.1f, 0.1f, 0.1f, 0f)));

                    bg.getTransform(TransformUI.class).setParentAnchor(0f, 0f, 0.5f, 1.f);

                    final Reference<GameObject> connectingRef =
                            bg.buildChild(
                                    "connecting",
                                    false,
                                    new TransformUI(true),
                                    (text) -> {
                                        text.getTransform(TransformUI.class)
                                                .setParentAnchor(0f, 0.12f, 0.5f, 0.12f);
                                        text.getTransform(TransformUI.class)
                                                .setMargin(0f, 0f, 0f, 0.07f);
                                        text.addComponent(
                                                new UIText(
                                                        new Vector3f(0f),
                                                        Font.getFontResource("Rise of Kingdom.ttf"),
                                                        "Connecting..."));
                                    });
                    final Reference<UIText> connectingTextRef =
                            connectingRef.get().getComponent(UIText.class);

                    connectingTextRef.get().setEnabled(false);

                    bg.buildChild(
                            "joinButton",
                            new TransformUI(true),
                            (button) -> {
                                button.getTransform(TransformUI.class)
                                        .setParentAnchor(0f, 0.05f, 0.5f, 0.05f);
                                button.getTransform(TransformUI.class).setMargin(0f, 0f, 0f, 0.07f);

                                UIButton newButton =
                                        new UIButton(
                                                new UIText(
                                                        new Vector3f(0f, 0f, 0f),
                                                        Font.getFontResource("Rise of Kingdom.ttf"),
                                                        "Join (Temporary)"),
                                                refAudioButtonEffect
                                                        .get()
                                                        .audibleClick(
                                                                (uiButton, __) -> {
                                                                    networkManager
                                                                            .get()
                                                                            .createClient(
                                                                                    sIP,
                                                                                    sPort,
                                                                                    (manager,
                                                                                            netID) -> {
                                                                                        if (netID
                                                                                                >= 0) {
                                                                                            onConnectedClient(
                                                                                                    mainScene,
                                                                                                    manager,
                                                                                                    netID);
                                                                                        } else if (connectingTextRef
                                                                                                .isValid()) {
                                                                                            connectingTextRef
                                                                                                    .get()
                                                                                                    .setEnabled(
                                                                                                            false);
                                                                                        }
                                                                                    });
                                                                    if (connectingTextRef.isValid())
                                                                        connectingTextRef
                                                                                .get()
                                                                                .setEnabled(true);
                                                                }));
                                button.addComponent(newButton);
                            });

                    bg.buildChild(
                            "cancelButton",
                            new TransformUI(true),
                            (button) -> {
                                button.getTransform(TransformUI.class)
                                        .setParentAnchor(0f, 0.35f, 0.5f, 0.35f);
                                button.getTransform(TransformUI.class).setMargin(0f, 0f, 0f, 0.07f);

                                UIButton newButton =
                                        new UIButton(
                                                new UIText(
                                                        new Vector3f(0f, 0f, 0f),
                                                        Font.getFontResource("Rise of Kingdom.ttf"),
                                                        "Cancel"),
                                                refAudioButtonEffect
                                                        .get()
                                                        .audibleClick(
                                                                (uiButton, __) -> {
                                                                    joinUI.setEnabled(false);
                                                                    mainUI.setEnabled(true);
                                                                }));

                                button.addComponent(newButton);
                            });
                });

        hostUI.buildChild(
                "bg",
                new TransformUI(false),
                (bg) -> {
                    bg.addComponent(new UIRenderable(new Vector4f(0.1f, 0.1f, 0.1f, 0f)));

                    bg.getTransform(TransformUI.class).setParentAnchor(0f, 0f, 0.5f, 1.f);

                    bg.buildChild(
                            "joinButton",
                            new TransformUI(true),
                            (button) -> {
                                button.getTransform(TransformUI.class)
                                        .setParentAnchor(0f, 0.05f, 0.5f, 0.05f);
                                button.getTransform(TransformUI.class).setMargin(0f, 0f, 0f, 0.07f);

                                UIButton newButton =
                                        new UIButton(
                                                new UIText(
                                                        new Vector3f(0f, 0f, 0f),
                                                        Font.getFontResource("Rise of Kingdom.ttf"),
                                                        "Host (Temporary)"),
                                                refAudioButtonEffect
                                                        .get()
                                                        .audibleClick(
                                                                (uiButton, __) -> {
                                                                    networkManager
                                                                            .get()
                                                                            .createServer(
                                                                                    sPort,
                                                                                    this
                                                                                            ::onClientConnected);
                                                                }));
                                button.addComponent(newButton);
                            });

                    bg.buildChild(
                            "cancelButton",
                            new TransformUI(true),
                            (button) -> {
                                button.getTransform(TransformUI.class)
                                        .setParentAnchor(0f, 0.35f, 0.5f, 0.35f);
                                button.getTransform(TransformUI.class).setMargin(0f, 0f, 0f, 0.07f);

                                UIButton newButton =
                                        new UIButton(
                                                new UIText(
                                                        new Vector3f(0f, 0f, 0f),
                                                        Font.getFontResource("Rise of Kingdom.ttf"),
                                                        "Cancel"),
                                                refAudioButtonEffect
                                                        .get()
                                                        .audibleClick(
                                                                (uiButton, __) -> {
                                                                    hostUI.setEnabled(false);
                                                                    mainUI.setEnabled(true);
                                                                }));

                                button.addComponent(newButton);
                            });
                });

        joinUI.setEnabled(false);
        hostUI.setEnabled(false);
        hostGameUI.setEnabled(false);
        mainScene.addRootObject(hostGameUI);

        mainMenu.addRootObject(networkManagerObject);

        mainMenu.addRootObject(hostUI);
        mainMenu.addRootObject(joinUI);
        mainMenu.addRootObject(mainUI);
        mainMenu.addRootObject(audioObject);

        return mainMenu;
    }

    public static boolean sReload = true;

    /**
     * Entrypoint of the program. Creates and runs one app instance
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {

        do {
            try (App app = new App()) {
                app.run();
            }
        } while (sReload);

        Map<Thread, StackTraceElement[]> activeThreads = Thread.getAllStackTraces();

        for (Map.Entry<Thread, StackTraceElement[]> t : activeThreads.entrySet()) {
            if (t.getKey() != Thread.currentThread()) {
                System.out.println("THREAD:");
                System.out.println(t.getKey().getName());
                System.out.println(t.getKey().getId());
                System.out.println(Arrays.toString(t.getValue()));
            }
        }

        System.exit(0);
    }

    private void run() {
        // Create a scene
        Scene mainScene = createMainScene();

        // Create the main menu
        Scene mainMenu = createMainMenu(mainScene);

        // Load the mainScene as an inactive scene
        Engine.getInstance().loadScene(mainScene, false);

        // Load the mainMenu as the presentation scene
        Engine.getInstance().loadPresentationScene(mainMenu);

        // Load dev console
        // TODO: actually make a fully fledged console
        // TODO: join it at the end
        new Thread(
                        () -> {
                            Scanner in = new Scanner(System.in);

                            String line;

                            while ((line = in.nextLine()) != null) {
                                try {
                                    sPort = in.nextInt();
                                    sIP = line.trim();
                                    System.out.println("Address set successfully!");
                                } catch (Exception e) {
                                    System.out.println("Failed to set IP and port!");
                                }
                            }
                        })
                .start();

        // Run the game
        Engine.getInstance().start("Hex Wars", new GameBindings());
    }

    private void onConnectedClient(Scene mainScene, NetworkManager manager, int netID) {
        System.out.println("CONNECTED ID " + netID);

        GameObject humanPlayer =
                new GameObject(
                        "human player",
                        (handle) -> {
                            handle.addComponent(
                                    new HumanPlayer(
                                            manager.getReference(NetworkManager.class), netID));
                        });

        mainScene.addRootObject(humanPlayer);
    }

    private void onClientConnected(NetworkManager manager, ServerClient networkClient) {
        int id = networkClient.getNetworkID();
        manager.getServerManager().spawnNetworkObject(id, manager.findTemplateByName("cube"));
        manager.getServerManager().spawnNetworkObject(id, manager.findTemplateByName("capital"));
        manager.getServerManager().spawnNetworkObject(id, manager.findTemplateByName("player"));
    }

    @Override
    public void free() {
        mMainMenuGLTF.free();
    }
}

//        // Create a cube. This syntax is slightly different
//        // This here, will allow you to "build" the cube in one go
//        GameObject cube =
//                new GameObject(
//                        "cube",
//                        new Transform3D(0f, 0f, 1.5f),
//                        (go) -> {
//                            go.addComponent(new Renderable(Mesh.CUBE, new UnlitMaterial()));
//                            go.getComponent(Renderable.class)
//                                    .get()
//                                    .getMaterial(IColouredMaterial.class)
//                                    .setAlpha(1f);
//                            // You spin me right round...
//                            go.addComponent(new Spinner(-180.f, 1000.f, 0.1f));
//                        });
//
//        mainMenu.addRootObject(cube);
//
//        mainMenu.addRootObject(GameObject.instantiate(cube));
