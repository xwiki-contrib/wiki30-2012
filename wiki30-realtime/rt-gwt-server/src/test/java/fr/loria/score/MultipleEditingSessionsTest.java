package fr.loria.score;

import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.server.ClientServerCorrespondents;
import fr.loria.score.server.CommunicationServiceImpl;
import fr.loria.score.server.ServerJupiterAlg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: Bogdan Flueras (email: Bogdan.Flueras@inria.fr)
 */
public class MultipleEditingSessionsTest {
    private static final Logger LOG = LoggerFactory.getLogger(MultipleEditingSessionsTest.class);
    
    private static final int NR_CLIENTS = 14;
    private static final int NR_SESSIONS = 7;
    private static final int NR_MESSAGES = 10;

    private CommunicationService commService;

    @Before
    public void setUp() throws Exception {
        commService = new CommunicationServiceImpl();

        TestUtils.createServerPairs(NR_CLIENTS, NR_SESSIONS, commService);
        TestUtils.sendMessagesToServer(NR_CLIENTS, NR_MESSAGES, NR_SESSIONS, commService);
    }

    @After
    public void tearDown() throws Exception {
        ClientServerCorrespondents.getInstance().getEditingSessions().clear();
        ClientServerCorrespondents.getInstance().getCorrespondents().clear();
    }

    /**
     * Test that:
     * 1) servers in same editing session have same data
     * 2) servers in different editing sessions have different data
     */
    @Test
    public void testEditingSessions() throws Exception {
        /* Run the test only if explicitly requested on the command line by passing a system property */
        if (System.getProperty(TestUtils.RUN_STRESS_TESTS_FLAG) == null) {
            TestUtils.warnSkipped(LOG, TestUtils.RUN_STRESS_TESTS_FLAG);
            return;
        }
        
        List<ServerJupiterAlg> serversInDifferentSessions = new ArrayList<ServerJupiterAlg>();
        ServerJupiterAlg previous = null;

        // now check if servers in same editing session have the same data & state at quiescence
        for (Map.Entry<Integer, List<Integer>> e : ClientServerCorrespondents.getInstance().getEditingSessions().entrySet()) {
            List<Integer> serverIds = e.getValue();
            List<ServerJupiterAlg> servers = new ArrayList<ServerJupiterAlg>();
            for (Integer serverId : serverIds) {
                servers.add(ClientServerCorrespondents.getInstance().getCorrespondents().get(serverId));
            }

            serversInDifferentSessions.add(servers.get(0));

            previous = null;
            for (Iterator<ServerJupiterAlg> it = servers.iterator(); it.hasNext(); ) {
                ServerJupiterAlg s = it.next();
                if (previous != null) {
                    assertEquals("Inconsistent data among servers in same editing session. Server1:" + previous + ", Server2:" + s, previous.getData(), s.getData());
                    assertEquals("Inconsistent state among servers in same editing sesson", previous.getCurrentState(), s.getCurrentState());
                }
                previous = s;
            }
        }

        //check if servers in different editing sessions have different data
        previous = null;
        for (Iterator<ServerJupiterAlg> it = serversInDifferentSessions.iterator(); it.hasNext(); ) {
            ServerJupiterAlg s = it.next();
            if (previous != null) {
                assertTrue("Servers in different editing session have same data!. Server1:" + previous + ", Server2:" + s, !previous.getData().equals(s.getData()));
            }
            previous = s;
        }
    }

    /**
     * Scenario: There is a current editing session. A new client joins the same session and he receives the existing content
     */
    @Test
    public void testNewClientJoinsSameEditingSession() throws Exception {
        /* Run the test only if explicitly requested on the command line by passing a system property */
        if (System.getProperty(TestUtils.RUN_STRESS_TESTS_FLAG) == null) {
            TestUtils.warnSkipped(LOG, TestUtils.RUN_STRESS_TESTS_FLAG);
            return;
        }
        
        ClientJupiterAlg client = new ClientJupiterAlg("", NR_CLIENTS + 1);
        int esid = client.getSiteId() % NR_SESSIONS;
        client.setEditingSessionId(esid);
        String content = commService.createServerPairForClient(client);

        String expected = ClientServerCorrespondents.getInstance().getCorrespondents().get(
                        ClientServerCorrespondents.getInstance().getEditingSessions().get(esid).get(0)
        ).getData();

        assertNotNull("Received content should not be null for client joining existing editing session", content);
        assertEquals("Client received wrong content when joining existing editing session", content, expected);
    }

    /**
     * Scenario: There is a current editing session. A new client joins new session and he receives empty string
     */
    @Test
    public void testNewClientJoinsInDifferentEditingSession() throws Exception {
        /* Run the test only if explicitly requested on the command line by passing a system property */
        if (System.getProperty(TestUtils.RUN_STRESS_TESTS_FLAG) == null) {
            TestUtils.warnSkipped(LOG, TestUtils.RUN_STRESS_TESTS_FLAG);
            return;
        }
        
        ClientJupiterAlg client = new ClientJupiterAlg("", NR_CLIENTS + 1);
        int esid = Integer.MAX_VALUE;
        client.setEditingSessionId(esid);
        String expected = client.getData();

        String actual = commService.createServerPairForClient(client);
        assertNotNull("Received content should not be null for client joining new editing session", actual);
        assertEquals("Client received wrong content when joining new editing session", expected, actual);
    }
}