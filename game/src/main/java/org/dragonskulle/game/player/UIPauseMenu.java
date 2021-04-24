/* (C) 2021 DragonSkulle */
package org.dragonskulle.game.player;

import lombok.extern.java.Log;
import org.dragonskulle.components.Component;
import org.dragonskulle.components.IFixedUpdate;
import org.dragonskulle.components.IFrameUpdate;
import org.dragonskulle.components.IOnAwake;
import org.dragonskulle.components.IOnStart;
import org.dragonskulle.core.GameObject;
import org.dragonskulle.core.Reference;
import org.dragonskulle.game.input.GameActions;
import org.dragonskulle.network.components.NetworkManager;
import org.dragonskulle.ui.TransformUI;
import org.dragonskulle.ui.UIButton;
import org.dragonskulle.ui.UIManager;

@Log
public class UIPauseMenu extends Component
        implements IOnAwake, IFrameUpdate {

    private Reference<NetworkManager> mNetworkManager;

    private GameObject mMenuContainer;
    private GameObject mSettingsContainer;

    public UIPauseMenu(NetworkManager networkManager) {
        mNetworkManager = networkManager.getReference(NetworkManager.class);
    }

    private void generateMenu() {
    	UIButton resume =
                new UIButton(
                        "Resume",
                        (__, ___) -> {
                        	mMenuContainer.setEnabled(false);
                        });

        UIButton settings =
                new UIButton(
                        "Settings",
                        (__, ___) -> {
                            System.out.println("Settings.");
                            mSettingsContainer.setEnabled(true);
                            mMenuContainer.setEnabled(false);
                        });

        UIButton exit =
                new UIButton(
                        "Exit",
                        (__, ___) -> {
                            if (Reference.isValid(mNetworkManager)) {
                                NetworkManager networkManager = mNetworkManager.get();

                                if (networkManager.getClientManager() != null) {
                                    networkManager.getClientManager().disconnect();
                                } else if (networkManager.getServerManager() != null) {
                                    networkManager.getServerManager().destroy();
                                }
                            } else {
                                log.severe("Unable to get network manager.");
                            }
                        });

        final UIManager uiManager = UIManager.getInstance();
        uiManager.buildVerticalUi(mMenuContainer, 0.3f, 0, 1f, resume, settings, exit);
    }

    @Override
    public void onAwake() {
        mMenuContainer = new GameObject("pause_container", false, new TransformUI());
        getGameObject().addChild(mMenuContainer);
        mMenuContainer.setEnabled(false);

        mSettingsContainer = new GameObject("settings_container", false, new TransformUI(),
        		(settings) -> {
        			settings.addComponent(new UISettingsMenu(() -> {
        				mMenuContainer.setEnabled(true);
        				mSettingsContainer.setEnabled(false);
        			}));
        		}
        		);
        getGameObject().addChild(mSettingsContainer);
        mSettingsContainer.setEnabled(false);
        
        generateMenu();
    }

    @Override
    public void frameUpdate(float deltaTime) {
        if (GameActions.TOGGLE_PAUSE.isJustActivated()) {
        	mMenuContainer.setEnabled(!mMenuContainer.isEnabled());
        }
    }
    
    @Override
    protected void onDestroy() {}
}
