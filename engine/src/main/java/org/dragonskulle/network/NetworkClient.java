/* (C) 2021 DragonSkulle */
package org.dragonskulle.network;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Hex;
import org.dragonskulle.core.Reference;
import org.dragonskulle.core.Scene;
import org.dragonskulle.network.components.Capital.Capital;
import org.dragonskulle.network.components.NetworkObject;
import org.dragonskulle.network.components.NetworkableComponent;

/**
 * @author Oscar L
 *     <p>This is the client usage, you will create an instance, by providing the correct server to
 *     connect to. ClientListener is the handler for commands that the client receives. {@link
 *     org.dragonskulle.network.ClientListener}**
 */
public class NetworkClient {
    private static final Logger mLogger = Logger.getLogger(NetworkClient.class.getName());

    /** The constant MAX_TRANSMISSION_SIZE. */
    private static final int MAX_TRANSMISSION_SIZE = 512;
    /** The Socket connection to the server. */
    private Socket mSocket;
    /** The byte output stream. */
    private DataOutputStream mDOut;
    /** The byte input stream. */
    private BufferedInputStream mBIn;
    /** The Game Instance. */
    private ClientGameInstance mGame;

    /** The Client listener to notify of important events. */
    private ClientListener mClientListener;
    /** True if the socket is open. */
    private boolean mOpen = true;

    /** The id of the clients capital, only assigned once spawned. */
    private int mCapitalId;
    /** Stores all requests from the server once scheduled. */
    private final ListenableQueue<byte[]> mRequests = new ListenableQueue<>(new LinkedList<>());
    /** The Runnable for @{mClientThread}. */
    private ClientRunner mClientRunner;
    /**
     * The thread that watches @link{dIn} for messages and adds them to the message
     * queue @link{mRequests}.
     */
    private Thread mClientThread;
    /** True if requests are to be automatically processed every time period. */
    private boolean mAutoProcessMessages;
    /**
     * Schedules the processing of received messages @link {mRequests} runs dependant
     * on @link{mAutoProcessMessages}.
     */
    private final Timer mProcessScheduler = new Timer();

    /**
     * Instantiates a new Network client.
     *
     * @param ip the ip
     * @param port the port
     * @param listener the listener
     * @param autoProcessMessages true if should auto process messages
     */
    public NetworkClient(
            String ip, int port, ClientListener listener, boolean autoProcessMessages) {
        this.mAutoProcessMessages = autoProcessMessages;
        mClientListener = listener;
        try {
            mSocket = new Socket(ip, port);
            mBIn = new BufferedInputStream(mSocket.getInputStream());
            mDOut = new DataOutputStream(mSocket.getOutputStream());
            this.mGame = new ClientGameInstance(this::sendBytes);

            mClientRunner = new ClientRunner();
            mClientThread = new Thread(mClientRunner);
            mClientThread.setName("Client Connection");
            mClientThread.setDaemon(true);
            mClientThread.start();
            listener.connectedToServer();
        } catch (UnknownHostException exception) {
            mOpen = false;
            listener.unknownHost();
        } catch (IOException exception) {
            mOpen = false;
            listener.couldNotConnect();
        } catch (Exception exception) {
            mOpen = false;
            exception.printStackTrace();
        }
    }

    /**
     * Instantiates a new Network client.
     *
     * @param ip the ip
     * @param port the port
     * @param listener the listener
     * @param autoProcessMessages true if should auto process messages
     * @param scene the game scene to be linked
     */
    public NetworkClient(
            String ip,
            int port,
            ClientListener listener,
            boolean autoProcessMessages,
            Scene scene) {
        this.mAutoProcessMessages = autoProcessMessages;
        mClientListener = listener;
        try {
            mSocket = new Socket(ip, port);
            mBIn = new BufferedInputStream(mSocket.getInputStream());
            mDOut = new DataOutputStream(mSocket.getOutputStream());
            this.mGame = new ClientGameInstance(this::sendBytes, scene);

            mClientRunner = new ClientRunner();
            mClientThread = new Thread(mClientRunner);
            mClientThread.setName("Client Connection");
            mClientThread.setDaemon(true);
            mClientThread.start();
            listener.connectedToServer();
        } catch (UnknownHostException exception) {
            mOpen = false;
            listener.unknownHost();
        } catch (IOException exception) {
            mOpen = false;
            listener.couldNotConnect();
        } catch (Exception exception) {
            mOpen = false;
            exception.printStackTrace();
        }
    }

    /**
     * Execute bytes after parsing. This is the client version.
     *
     * @param messageType the message type
     * @param payload the payload
     * @return the byteCode of the message processed.
     */
    public byte executeBytes(byte messageType, byte[] payload) {
        mLogger.warning("EXEB - " + messageType);
        switch (messageType) {
            case (byte) 15:
                mLogger.info("Should update requested network object");
                updateNetworkObject(payload);
                break;
            case (byte) 20:
                mLogger.info("Trying to spawn map, need to get the actual map");
                this.mGame.spawnMap(payload);
                mLogger.info("Spawned map");
                break;
            case (byte) 21:
                try {
                    mLogger.info("Trying to spawn capital");
                    Capital capital = deserializeCapitol(payload);
                    this.mCapitalId = this.mGame.spawnCapital(capital.getOwnerId(), capital);
                    if (capital.getId() == mCapitalId) {
                        mLogger.info("Spawned capital");
                    }
                } catch (DecodingException e) {
                    e.printStackTrace();
                }
                break;
            case (byte) 22:
                mLogger.info("Trying to spawn component");
                NetworkableComponent component = NetworkableComponent.createFromBytes(payload);
                assert component != null;
                int componentId = this.mGame.spawnComponent(component.getOwnerId(), component);
                mLogger.info("Spawned Component");
                break;
            default:
                mLogger.warning(
                        "unsure of what to do with message as unknown type byte " + messageType);
                break;
        }
        return messageType;
    }

    /**
     * Deserialize the capital bytes.
     *
     * @param payload the payload
     * @return the capital
     * @throws DecodingException Thrown if any errors occur in deserialization.
     */
    private Capital deserializeCapitol(byte[] payload) throws DecodingException {
        return NetworkableComponent.from(Capital.class, payload);
    }

    /**
     * Update networkable from bytes, this is authored by the server.
     *
     * @param payload the payload
     */
    private void updateNetworkObject(byte[] payload) {
        this.mGame.updateNetworkObject(payload);
    }

    /** Dispose. */
    public void dispose() {
        try {
            if (mOpen) {
                this.sendBytes(new byte[MAX_TRANSMISSION_SIZE]);
                mOpen = false;
                closeAllConnections();
                mClientListener.disconnected();
            }
            mSocket = null;
            mDOut = null;
            mClientListener = null;

            mClientRunner.cancel();
            mClientThread.join();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Send a string message to the server.
     *
     * @param msg the msg
     */
    @Deprecated
    public void send(String msg) {
        this.sendBytes(msg.getBytes());
    }

    /**
     * Sends bytes to the server.
     *
     * @param bytes the bytes
     */
    public void sendBytes(byte[] bytes) {
        if (mOpen) {
            try {
                mLogger.info("sending bytes");
                mDOut.write(bytes);
            } catch (IOException e) {
                mLogger.info("Failed to send bytes");
            }
        }
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    public boolean isConnected() {
        return mOpen;
    }

    /**
     * Gets networkable objects from the game.
     *
     * @return the networkable object references
     */
    public ArrayList<Reference<NetworkObject>> getNetworkableObjects() {
        return this.mGame.getNetworkObjects();
    }

    /**
     * Sets the client to process messages automatically or not.
     *
     * @param toggle the toggle
     * @return true once executed, for testing and can be ignored
     */
    public boolean setProcessMessagesAutomatically(boolean toggle) {
        mLogger.info("Processing Messages Automatically :" + toggle);
        this.mAutoProcessMessages = toggle;
        if (toggle) {
            mProcessScheduler.purge();
            mProcessScheduler.schedule(
                    new ProcessRequestScheduled(),
                    0,
                    30); // bit faster than server game tick rate, // TODO: 11/03/2021 refactor into
            // game object call
        } else {
            mProcessScheduler.purge();
        }
        return true;
    }

    /**
     * Gets the game instance.
     *
     * @return the game
     */
    public ClientGameInstance getGame() {
        return this.mGame;
    }

    /**
     * Sets the linked scene.
     *
     * @param scene the scene
     */
    public void linkToScene(Scene scene) {
        this.mGame.linkToScene(scene);
    }

    /**
     * This is the thread which is created once the connection is achieved. It is used to handle
     * messages received from the server. It also handles the server disconnection.
     */
    private class ClientRunner implements Runnable {

        @Override
        public void run() {
            byte[] bArray;
            byte[] terminateBytes = new byte[MAX_TRANSMISSION_SIZE]; // max flatbuffer size
            if (mAutoProcessMessages) {
                setProcessMessagesAutomatically(true);
            }
            while (mOpen && !Thread.currentThread().isInterrupted()) {
                try {
                    bArray = NetworkMessage.readMessageFromStream(mBIn);
                    if (bArray.length != 0) {
                        if (Arrays.equals(bArray, terminateBytes)) {
                            mClientListener.disconnected();
                            dispose();
                            break;
                        } else {
                            queueRequest(bArray);
                        }
                    }

                } catch (IOException ignore) { // if fails to read from in stream
                    if (mClientListener != null) {
                        mClientListener.error("failed to read from input stream");
                    }
                    if (isConnected()) {
                        dispose();
                    }
                    break;
                }
            }
        }

        /** Cancel. */
        public void cancel() {
            setProcessMessagesAutomatically(false);
        }
    }

    /** The Auto Processing requests TimerTask. */
    private class ProcessRequestScheduled extends TimerTask {
        public void run() {
            processRequests();
        }
    }

    /**
     * Queue a new request for the client to process.
     *
     * @param bArray the bytes
     */
    private void queueRequest(byte[] bArray) {
        mLogger.info("queuing request :: " + Hex.encodeHexString(bArray));
        this.mRequests.addIfUnique(bArray);
    }

    /** Processes all requests. */
    public void processRequests() {
        mLogger.warning("processing all " + this.mRequests.size() + " requests");
        while (!this.mRequests.isEmpty()) {
            byte[] requestBytes = this.mRequests.poll();
            if (requestBytes != null) {
                processBytes(requestBytes);
            }
        }
    }

    /**
     * Checks if the client has requests left to process.
     *
     * @return true if there are requests, false otherwise.
     */
    public boolean hasRequests() {
        return !this.mRequests.isEmpty();
    }

    /** Processes a single request. */
    public void processSingleRequest() {
        if (!this.mRequests.isEmpty()) {
            byte[] requestBytes = this.mRequests.poll();
            if (requestBytes != null) {
                mLogger.info("Processing message with type: " + processBytes(requestBytes));
            }
        }
    }

    /** Clears the pending requests. */
    public void clearPendingRequests() {
        this.mRequests.clear();
    }

    /**
     * Processes bytes from a message.
     *
     * @param bytes the bytes
     */
    private byte processBytes(byte[] bytes) {
        mClientListener.receivedBytes(bytes);
        //        mLogger.info("parsing bytes :: " + Hex.encodeHexString(bytes));
        try {
            return parseBytes(bytes);
        } catch (DecodingException e) {
            mLogger.info(e.getMessage());
            mLogger.info(new String(bytes, StandardCharsets.UTF_8));
            return (byte) -1;
        }
    }

    /**
     * Parses bytes from a message.
     *
     * @param bytes the bytes
     * @throws DecodingException the decoding exception
     */
    private byte parseBytes(byte[] bytes) throws DecodingException {
        try {
            return NetworkMessage.parse(bytes, this);
        } catch (Exception e) {
            mLogger.info("error parsing bytes");
            e.printStackTrace();
            throw new DecodingException("Message is not of valid type");
        }
    }

    /** Closes all connections. */
    private void closeAllConnections() {
        mOpen = false;

        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            if (mDOut != null) {
                mDOut.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /** @return True if has a map, false otherwise. */
    public boolean hasMap() {
        return this.mGame.hasSpawnedMap();
    }

    /** @return True if has a capital, false otherwise. */
    public boolean hasCapital() {
        return this.mGame.hasSpawnedCapital();
    }

    /**
     * Gets the capital id.
     *
     * @return the id
     */
    public int getCapitalId() {
        return this.mCapitalId;
    }

    /**
     * Gets a networkable component by id, null if it doesn't exist.
     *
     * @param networkableId the networkable id
     * @return the networkable component found
     */
    public NetworkableComponent getNetworkableComponent(int networkableId) {
        mLogger.warning("getNetworkableComponent call");
        Reference<NetworkableComponent> networkableComponentReference =
                this.mGame.getNetworkedComponent(networkableId);
        if (networkableComponentReference != null) {
            return networkableComponentReference.get();
        }
        return null;
    }
}
