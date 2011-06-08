package fr.loria.score;

import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.server.ClientServerCorrespondents;
import fr.loria.score.server.CommunicationServiceImpl;
import fr.loria.score.server.ServerJupiterAlg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * @author: Bogdan Flueras (email: Bogdan.Flueras@inria.fr)
 */
public class CommunicationServiceTest {
    private static final Log LOG = LogFactory.getLog(CommunicationServiceTest.class);
    
    private CommunicationService communicationService = new CommunicationServiceImpl();
    public static final int NR_CLIENTS = 6;
    public static final int NR_MESSAGES = 25;
    public static final int NR_SESSIONS = 1;

    @Before
    public void setup() {
        Map<Integer, List<Integer>> editingSessions = ClientServerCorrespondents.getInstance().getEditingSessions();
        assertEquals("Invalid initial size for editing sessions", 0, editingSessions.size());
        Map<Integer, ServerJupiterAlg> correspondents = ClientServerCorrespondents.getInstance().getCorrespondents();
        assertEquals("Invalid initial size for client-server correspondents sessions", 0, correspondents.size());
    }

    @After
    public void tearDown() {
        ClientServerCorrespondents.getInstance().getEditingSessions().clear();
        ClientServerCorrespondents.getInstance().getCorrespondents().clear();
    }

    @Test
    public void generateClientId() {
        int expected = 0;

        int id = communicationService.generateClientId();
        assertEquals("Invalid id generated", expected, id);

        id=communicationService.generateClientId();
        assertEquals("Invalid id generated", ++expected, id);
    }

    @Test
    public void generateClientIdFromMultipleThreads() {
        /* Run the test only if explicitly requested on the command line by passing a system property */
        if (System.getProperty(TestUtils.RUN_STRESS_TESTS_FLAG) == null) {
            TestUtils.warnSkipped(LOG, TestUtils.RUN_STRESS_TESTS_FLAG);
            return;
        }
        
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch endSignal = new CountDownLatch(NR_CLIENTS);
        Executor executor = Executors.newFixedThreadPool(NR_CLIENTS);

        final List<Integer> clientIds = Collections.synchronizedList(new ArrayList<Integer>());
        for (int i = 0; i < NR_CLIENTS; i++) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        startSignal.await();
                        clientIds.add(communicationService.generateClientId());
                    } catch (InterruptedException e) {
                        fail(e.getMessage());
                    } finally {
                        endSignal.countDown();
                    }
                }
            });
        }

        startSignal.countDown();
        try {
            endSignal.await();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        assertEquals("Invalid nr of clients", NR_CLIENTS, clientIds.size());

        for (int i = 0; i < NR_CLIENTS; i++) {
            assertTrue("Invalid id generated", clientIds.contains(i));
        }
    }

    @Test
    public void createServerPairForClient() {
        int siteId = 1;
        int sessionId = 47;

        ClientJupiterAlg client = new ClientJupiterAlg("foo", siteId);
        client.setEditingSessionId(sessionId);

        String actualData = communicationService.createServerPairForClient(client);
        assertEquals("Wrong data received when creating server pair for client", "foo", actualData);

        Map<Integer, ServerJupiterAlg> correspondents = ClientServerCorrespondents.getInstance().getCorrespondents();
        assertNotNull(correspondents);
        assertEquals("Invalid correspondents size", 1, correspondents.size());
        assertEquals("Invalid data for server correspondent", "foo", correspondents.get(siteId).getData());
        assertEquals("Invalid siteId for server correspondent", siteId, correspondents.get(siteId).getSiteId());

        Map<Integer, List<Integer>> sessions = ClientServerCorrespondents.getInstance().getEditingSessions();
        assertNotNull(sessions);
        assertEquals("Invalid editing session size", 1, sessions.size());
        assertEquals("Invalid siteId for sessionId:" + sessionId, (Object) siteId, sessions.get(sessionId).get(0));
    }

    @Test
    public void createServerPairForClientMultipleThreads() {
        /* Run the test only if explicitly requested on the command line by passing a system property */
        if (System.getProperty(TestUtils.RUN_STRESS_TESTS_FLAG) == null) {
            TestUtils.warnSkipped(LOG, TestUtils.RUN_STRESS_TESTS_FLAG);
            return;
        }
        
        //for each client it's corresponding server is created and is correctly mapped to editing session
        TestUtils.createServerPairs(NR_CLIENTS, 1, communicationService);
        Map<Integer, ServerJupiterAlg> correspondents = ClientServerCorrespondents.getInstance().getCorrespondents();
        assertEquals("Invalid nr of server pairs", NR_CLIENTS, correspondents.size());

        for (Iterator<ServerJupiterAlg> it = correspondents.values().iterator(); it.hasNext();) {
            ServerJupiterAlg server = it.next();
            assertNotNull("Server cannot be null", server);
            assertEquals("Invalid server data", "", server.getData());
        }

        Map<Integer, List<Integer>> sessions = ClientServerCorrespondents.getInstance().getEditingSessions();
        assertEquals(NR_CLIENTS, sessions.get(0).size());
    }

    @Test
    public void removeServerPairForClient() {
        Map<Integer, List<Integer>> editingSessions;
        Map<Integer, ServerJupiterAlg> correspondents;

        ClientJupiterAlg client = new ClientJupiterAlg("", 10);
        client.setEditingSessionId(47);
        communicationService.createServerPairForClient(client);

        //remove it
        communicationService.removeServerPairForClient(client);
        editingSessions = ClientServerCorrespondents.getInstance().getEditingSessions();
        assertTrue("Invalid editing session id", editingSessions.containsKey(47));
        assertEquals("Site id exists for editing session:", 0, editingSessions.get(47).size());

        correspondents = ClientServerCorrespondents.getInstance().getCorrespondents();
        assertEquals("Invalid correspondents size", 0, correspondents.size());
    }

    //Ensures that server correctly receives and handles all client messages
    @Test
    public void serverReceive() {
        /* Run the test only if explicitly requested on the command line by passing a system property */
        if (System.getProperty(TestUtils.RUN_STRESS_TESTS_FLAG) == null) {
            TestUtils.warnSkipped(LOG, TestUtils.RUN_STRESS_TESTS_FLAG);
            return;
        }
        
        //create servers
        TestUtils.createServerPairs(NR_CLIENTS, 1, communicationService);
        TestUtils.sendMessagesToServer(NR_CLIENTS, NR_MESSAGES, NR_SESSIONS, communicationService);

        // now check if servers have the same data & state at quiescence
        Collection<ServerJupiterAlg> servers = ClientServerCorrespondents.getInstance().getCorrespondents().values();
        ServerJupiterAlg previous = null;

        for (Iterator<ServerJupiterAlg> it = servers.iterator(); it.hasNext(); ) {
            ServerJupiterAlg s = it.next();

            if (previous != null) {
                assertEquals("Inconsistent final data among servers. Server1:" + previous + ", Server2:" + s, previous.getData(), s.getData());
                assertEquals("Inconsistent final state among servers", previous.getCurrentState(), s.getCurrentState());
            }
            previous = s;
        }

        //a new client joins and should receive the existing content
        Integer id = communicationService.generateClientId();
        String clientContent = communicationService.createServerPairForClient(new ClientJupiterAlg("", id));
        String serverContent = ClientServerCorrespondents.getInstance().getCorrespondents().get(id).getData();
        assertEquals("Invalid client content joining an editing session", previous.getData(), clientContent);
        assertEquals("Invalid server content joining an editing session", previous.getData(), serverContent);
    }
}