/* (C) 2021 DragonSkulle */
package org.dragonskulle.network.components;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.dragonskulle.components.Component;
import org.dragonskulle.core.Reference;
import org.dragonskulle.network.components.requests.ClientRequest;
import org.dragonskulle.network.components.requests.ServerEvent;
import org.dragonskulle.network.components.sync.ISyncVar;

/**
 * @author Oscar L Any component that extends this, its syncvars will be updated with the server.
 */
@Accessors(prefix = "m")
public abstract class NetworkableComponent extends Component {

    /** A reference to itself. */
    private final Reference<NetworkableComponent> mReference = new Reference<>(this);

    @Getter private NetworkObject mNetworkObject = null;

    public static final int MASK_LENGTH_OFFSET = 0;
    public static final int MASK_OFFSET = MASK_LENGTH_OFFSET + 1;

    /** Instantiates a new Networkable component. */
    public NetworkableComponent() {}

    /** The Fields. */
    private Field[] mFields;

    /**
     * Init fields. @param networkObject the network object
     *
     * @param outRequests the requests it can deal with
     * @param outEvents the events it can deal with
     */
    public void initialize(
            NetworkObject networkObject,
            List<ClientRequest<?>> outRequests,
            List<ServerEvent<?>> outEvents) {

        mNetworkObject = networkObject;

        onNetworkInitialize();

        mFields =
                Arrays.stream(this.getClass().getDeclaredFields())
                        .filter(field -> ISyncVar.class.isAssignableFrom(field.getType()))
                        .toArray(Field[]::new);

        for (Field f : mFields) {
            f.setAccessible(true);
        }

        Field[] requestFields =
                Arrays.stream(this.getClass().getDeclaredFields())
                        .filter(field -> ClientRequest.class.isAssignableFrom(field.getType()))
                        .toArray(Field[]::new);

        try {
            for (Field f : requestFields) {
                f.setAccessible(true);
                ClientRequest<?> req = (ClientRequest<?>) f.get(this);
                outRequests.add(req);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Field[] eventFields =
                Arrays.stream(this.getClass().getDeclaredFields())
                        .filter(field -> ServerEvent.class.isAssignableFrom(field.getType()))
                        .toArray(Field[]::new);

        try {
            for (Field f : eventFields) {
                f.setAccessible(true);
                ServerEvent<?> req = (ServerEvent<?>) f.get(this);
                outEvents.add(req);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        onConnectedSyncvars();
    }

    protected void onNetworkInitialize() {}

    protected void onConnectedSyncvars() {}

    void beforeNetSerialize() {}

    protected void afterNetUpdate() {}

    protected void onOwnerIdChange(int newId) {}

    Stream<ISyncVar> getSyncVars() {
        return Arrays.stream(mFields)
                .map(
                        f -> {
                            try {
                                return (ISyncVar) f.get(this);
                            } catch (IllegalAccessException e) {
                                return null;
                            }
                        });
    }

    @Override
    public String toString() {
        StringBuilder fieldsString = new StringBuilder("Field{\n");
        for (Field field : mFields) {
            try {
                fieldsString.append(field.get(this).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        fieldsString.append("\n}");

        return "NetworkableComponent{" + ", fields=" + fieldsString + '}';
    }

    /**
     * Gets a reference to this component.
     *
     * @return the reference
     */
    public Reference<NetworkableComponent> getNetReference() {
        return this.mReference;
    }

    /**
     * Get the {@link NetworkObject}'s {@link NetworkManager}, if the NetworkObject exists.
     *
     * @return The NetworkManager, or {@code null}.
     */
    public NetworkManager getNetworkManager() {
        if (mNetworkObject == null) {
            return null;
        }
        return mNetworkObject.getNetworkManager();
    }
}
