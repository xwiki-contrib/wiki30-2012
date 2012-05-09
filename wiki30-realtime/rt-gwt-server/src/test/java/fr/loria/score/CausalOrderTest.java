package fr.loria.score;

import fr.loria.score.client.ClientDTO;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.jupiter.model.AbstractOperation;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.plain.PlainDocument;
import fr.loria.score.jupiter.plain.operation.InsertOperation;
import fr.loria.score.server.ClientServerCorrespondents;
import fr.loria.score.server.CommunicationServiceImpl;
import fr.loria.score.server.ServerJupiterAlg;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Test that messages received by a Jupiter server are causal ordered - that is they hold a specific invariant
 * @author Bogdan.Flueras@inria.fr
 */
public class CausalOrderTest {
    private static final Logger LOG = LoggerFactory.getLogger(CausalOrderTest.class);
    
    private static final String HELLO = "hello";
    private static final String RIEN_DE_RIEN =
            "Non, rien de rien\n" +
            "Non, je ne regrette rien\n" +
            "Ni le bien qu'on m'a fait\n" +
            "Ni le mal, tout ça m'est bien égal\n" +
            "Non, rien de rien\n" +
            "Non, je ne regrette rien\n" +
            "C'est payé, balayé, oublié\n" +
            "Je me fous du passé\n" +
            "\n" +
            "Avec mes souvenirs, j'ai allumé le feu\n" +
            "Mes chagrins, mes plaisirs, je n'ai plus besoin d'eux\n" +
            "Balayées les amours, avec leurs trémolos\n" +
            "Balayées pour toujours, je repars à zéro\n" +
            "\n" +
            "Non, rien de rien\n" +
            "Non, je ne regrette rien\n" +
            "Ni le bien qu'on m'a fait\n" +
            "Ni le mal, tout ça m'est bien égal\n" +
            "Non, rien de rien\n" +
            "Non, je ne regrette rien\n" +
            "Car ma vie car mes joies\n" +
            "Aujourd'hui, ça commence avec toi";

    private final int esid = 0;
    private final int siteId = 42;
    private CommunicationService commService;

    @Before
    public void setUp() throws Exception {
        commService = new CommunicationServiceImpl();
        ClientDTO dto = new ClientDTO().setEditingSessionId(esid).setSiteId(siteId).setDocument(new PlainDocument(""));
        commService.createServerPairForClient(dto);
    }

    @After
    public void afterTest() {
        ClientServerCorrespondents.getInstance().getCorrespondents().clear();
        ClientServerCorrespondents.getInstance().getEditingSessions().clear();
    }

    @Test
    public void testReceiveBySingleThread() { //causal order
        List<Message> messages = createMessages(HELLO, esid);
        ServerJupiterAlg server = ClientServerCorrespondents.getInstance().getCorrespondents().get(siteId);

        int received = 1;
        for (ListIterator<Message> it = messages.listIterator(messages.size()); it.hasPrevious(); received++) {
            commService.serverReceive(it.previous());
            if (!it.hasPrevious()) {
                assertEquals(0, server.getCausalOrderedMessages().size());
                break;
            }
            assertEquals(received, server.getCausalOrderedMessages().size());
        }
        assertEquals(HELLO, server.getDocument().getContent());
    }

    @Test
    public void testReceiveByManyThreads() {
        List<Message> messages = createMessages(HELLO, esid);
        receiveByThreads(messages, HELLO, commService);
    }

    @Test
    public void testReceive100Threads() { // in the honour of my desk mate :)
        String result = "do you like my editor? If you do so, do not hesitate to use it! Jerome est gai. Il gazouille toujours";
        int size = result.length();
        assertTrue("Invalid message size", size > 100);
        List<Message> messages = createMessages(result, esid);

        receiveByThreads(messages, result, commService);
    }

    @Test
    public void testReceive1000Threads() { //really crunch my laptop
        /* Run the test only if explicitly requested on the command line by passing a system property */
        if (System.getProperty(TestUtils.RUN_STRESS_TESTS_FLAG) == null) {
            TestUtils.warnSkipped(LOG, TestUtils.RUN_STRESS_TESTS_FLAG);
            return;
        }
        
        String bis = RIEN_DE_RIEN + RIEN_DE_RIEN;
        int size = bis.length();
        assertTrue("Invalid message size",size > 1000);
        List<Message> messages = createMessages(bis, esid);
        receiveByThreads(messages, bis, commService);
    }


    /**
     * Simulates the server who spawns a given nr of threads to serve the requests. 
     * This is how actually the web server works. <br>
     * To be called by test methods
     * @param messages the messages to be received
     * @param finalOutcome the document that the server produced
     * @param commService
     */
    private void receiveByThreads(List<Message> messages, String finalOutcome, final CommunicationService commService) {
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch endSignal = new CountDownLatch(messages.size());

        Executor executor = Executors.newFixedThreadPool(messages.size());
        for (final Message clientSentMessage : messages) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        startSignal.await();
                        commService.serverReceive(clientSentMessage);
                    } catch (InterruptedException e) {
                        fail(e.getMessage());
                    }
                    finally {
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
        ServerJupiterAlg server = ClientServerCorrespondents.getInstance().getCorrespondents().get(siteId);
        assertEquals(finalOutcome, server.getDocument().getContent());
    }

    private List<Message> createMessages(String result, int esid) {
        List<AbstractOperation> ops = new ArrayList<AbstractOperation>();
        for (int i = 0; i < result.length(); i++) {
            ops.add(new InsertOperation(siteId, i, result.charAt(i)));
        }
        return TestUtils.createMessagesFromOperations(esid, ops);
    }
}
