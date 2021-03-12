/* (C) 2021 DragonSkulle */
package org.dragonskulle.network;

/** @author Oscar L Thrown if a network object does not have the child */
public class NetworkObjectDoesNotHaveChildError extends Exception {
    /**
     * @param errorMessage the error message to be displayed
     * @param componentId the component id which was not found
     */
    public NetworkObjectDoesNotHaveChildError(String errorMessage, int componentId) {
        super(errorMessage);
        this.invalidComponentId = componentId;
    }

    public final int invalidComponentId;
}
