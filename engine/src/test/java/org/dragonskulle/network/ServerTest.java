/* (C) 2021 DragonSkulle */
package org.dragonskulle.network;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

import java.util.logging.Logger;
import org.dragonskulle.network.components.Capital;
import org.junit.*;

/** @author Oscar L */
public class ServerTest {
    private static final Logger mLogger = Logger.getLogger(ServerTest.class.getName());
    private static final long TIMEOUT = 8;
    private static StartServer mServerInstance;
    private static ServerEars mServerListener;
    private static NetworkClient mNetworkClient;
    private static ClientEars mClientListener;

    @BeforeClass
    public static void setUp() {
        mServerInstance = new StartServer(true);
        mClientListener = new ClientEars();
    }

    @AfterClass
    public static void tearDown() {
        mServerInstance.dispose();
    }

    @Test
    public void testSpawnMap() {
        mServerInstance.clearPendingRequests();
        mNetworkClient = new NetworkClient("127.0.0.1", 7000, mClientListener, false);
        testMapClient();
        mNetworkClient.dispose();
    }

    private void testMapClient() {
        await().atMost(6, SECONDS).until(() -> mNetworkClient.hasRequests());
        mNetworkClient.processSingleRequest();
        await().atMost(1, SECONDS).until(() -> mNetworkClient.hasMap());
    }

    @Test
    public void testCapitalSpawnedServer() {
        mServerInstance.clearPendingRequests();

        mNetworkClient = new NetworkClient("127.0.0.1", 7000, mClientListener, false);
        testMapClient();
        testCapitalSpawnDefaultServer();
        mNetworkClient.dispose();
    }

    private Capital testCapitalSpawnDefaultServer() {
        await().atMost(6, SECONDS).until(() -> mNetworkClient.hasRequests());
        mNetworkClient.processSingleRequest();
        await().atMost(1, SECONDS).until(() -> mNetworkClient.hasMap());
        await().atMost(TIMEOUT * 2, SECONDS).until(() -> mNetworkClient.hasCapital());
        assertFalse(mServerInstance.server.networkObjects.isEmpty());
        int capitalId = mNetworkClient.getCapitalId();
        assertNotNull(capitalId);
        final Capital nc = (Capital) mServerInstance.server.findComponent(capitalId);
        assertNotNull(nc);
        mLogger.info("\t-----> " + capitalId);
        assert (nc.getSyncMe().get() == false);
        assert (nc.getSyncMeAlso().get().equals("Hello World"));
        return nc;
    }

    @Test
    public void testCapitalUpdatedServer() {
        mServerInstance.clearPendingRequests();

        mNetworkClient = new NetworkClient("127.0.0.1", 7000, mClientListener, false);
        testMapClient();
        Capital nc = testCapitalSpawnDefaultServer();

        await().atMost(6, SECONDS).until(() -> mNetworkClient.hasRequests());
        mNetworkClient.processSingleRequest();
        await().atMost(TIMEOUT, SECONDS).until(() -> nc.getSyncMe().get() == true);
        assert (nc.getSyncMe().get() == true);
        await().atMost(TIMEOUT, SECONDS)
                .until(() -> nc.getSyncMeAlso().get().equals("Goodbye World"));
        assert (nc.getSyncMeAlso().get().equals("Goodbye World"));
        mNetworkClient.dispose();
    }

    @Test
    public void testCapitalSpawnedClient() {
        mServerInstance.clearPendingRequests();

        mNetworkClient = new NetworkClient("127.0.0.1", 7000, mClientListener, false);
        testMapClient();
        testCapitalSpawnDefaultClient();
        mNetworkClient.dispose();
    }

    private Capital testCapitalSpawnDefaultClient() {
        await().atMost(6, SECONDS).until(() -> mNetworkClient.hasRequests());
        mNetworkClient.processSingleRequest();
        await().atMost(1, SECONDS).until(() -> mNetworkClient.hasMap());
        await().atMost(TIMEOUT * 2, SECONDS).until(() -> mNetworkClient.hasCapital());
        assertFalse(mServerInstance.server.networkObjects.isEmpty());
        int capitalId = mNetworkClient.getCapitalId();
        assertNotNull(capitalId);
        mLogger.info("Capital ID : " + capitalId);
        mLogger.info("mClient has these objects : " + mNetworkClient.getNetworkableObjects());
        final Capital nc = (Capital) mNetworkClient.getNetworkableComponent(capitalId);
        assertNotNull(nc);
        mLogger.info("\t-----> " + capitalId);
        assert (nc.getSyncMe().get() == false);
        assert (nc.getSyncMeAlso().get().equals("Hello World"));
        return nc;
    }

    @Test
    public void
            testCapitalUpdatedClient() { // need to write a better test, this is not processing all
        // requests it catches
        mNetworkClient = new NetworkClient("127.0.0.1", 7000, mClientListener, false);
        testMapClient();
        Capital nc = testCapitalSpawnDefaultClient();

        await().atLeast(30, SECONDS).until(() -> false);
        await().atMost(1, SECONDS).until(() -> mNetworkClient.hasRequests());
        mNetworkClient.processRequests();
        await().atMost(3, SECONDS).until(() -> !mNetworkClient.hasRequests());
        await().atMost(3, SECONDS).until(() -> nc.getSyncMe().get() == true);
        assert (nc.getSyncMe().get() == true);
        await().atMost(TIMEOUT, SECONDS)
                .until(() -> finalNc.getSyncMeAlso().get().equals("Goodbye World"));
        assert (nc.getSyncMeAlso().get().equals("Goodbye World"));
        mNetworkClient.dispose();
    }
}
