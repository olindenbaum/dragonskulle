/* (C) 2021 DragonSkulle */
package org.dragonskulle.network.components.Capital;

import org.dragonskulle.components.IFrameUpdate;
import org.dragonskulle.network.components.NetworkableComponent;
import org.dragonskulle.network.components.sync.SyncVector3;
import org.joml.Vector3f;

/**
 * @author Oscar L A simple version of a networked transform, it moves the x value of the component
 *     forward and backwards
 */
public class NetworkedTransform extends NetworkableComponent implements IFrameUpdate {
    public SyncVector3 mPosition = new SyncVector3(new Vector3f(0, 0, 0));
    private int mShouldFlipDirection = 1;

    @Override
    protected void onDestroy() {}

    @Override
    public void frameUpdate(float deltaTime) {
        if (getNetworkObject().isServer()) {
            float oldX = mPosition.get().x();
            if (oldX > 0.5) {
                mShouldFlipDirection = -1;
            } else if (oldX < -0.3) {
                mShouldFlipDirection = 1;
            }
            getGameObject().getTransform().translate(mShouldFlipDirection * (deltaTime), 0, 0);
            mPosition.set(getGameObject().getTransform().getLocalPosition());
        } else {
            getGameObject().getTransform().setPosition((Vector3f) mPosition.get());
        }
    }

    @Override
    public String toString() {
        return "NetworkedTransform{"
                + "position="
                + mPosition
                + ", shouldFlipDirection="
                + mShouldFlipDirection
                + '}';
    }
}
