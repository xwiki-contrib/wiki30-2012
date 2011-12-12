package fr.loria.score;

import fr.loria.score.client.ClientDTO;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.jupiter.model.AbstractOperation;
import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.model.State;
import fr.loria.score.jupiter.plain.PlainDocument;
import fr.loria.score.jupiter.plain.operation.InsertOperation;
import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeDocument;
import fr.loria.score.server.ClientServerCorrespondents;
import fr.loria.score.server.ServerJupiterAlg;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author Bogdan Flueras (email: Bogdan.Flueras@inria.fr)
 */
public class TestUtils
{
    public static final String RUN_STRESS_TESTS_FLAG = "stressTests"; 
    
    /**
     * Creates the server pairs for each client request
     */
    public static void createServerPairs(int nrClients,
                                         final int nrSessions,
                                         final CommunicationService commService,
                                         final int docType)
    {
        final AtomicInteger count = new AtomicInteger(0);
        // create server pairs
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(nrClients);
        final Executor executor = Executors.newFixedThreadPool(nrClients);

        for (int i = 0; i < nrClients; i++) {
            executor.execute(new Runnable()
            {
                public void run()
                {
                    try {
                        start.await();

                        int counter = count.getAndIncrement();
                        //todo: review this stuff   MAKE an ENUM!
                        ClientDTO dto = new ClientDTO();
                        if (docType == 0) {
                            dto.setDocument(new PlainDocument("")).setSiteId(counter).setEditingSessionId(counter % nrSessions); // uniformly distribute clients for sessions
                        } else if (docType == 1){
                            dto.setDocument(new TreeDocument(new Tree("", null))).setSiteId(counter).setEditingSessionId(counter % nrSessions); // uniformly distribute clients for sessions
                        }
                        commService.createServerPairForClient(dto);
                    } catch (InterruptedException e) {
                        fail(e.getMessage());
                    } finally {
                        end.countDown();
                    }
                }
            });
        }
        start.countDown();
        try {
            end.await();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        assertEquals("Invalid nr of server pairs created", nrClients, ClientServerCorrespondents.getInstance().getCorrespondents().size());
    }

    /**
     * Each client throws its messages to server in concurrency
     */
    public static Map<Integer, List<Message>> sendMessagesToServer(int nrClients,
                                            int nrMessages,
                                            int nrSessions, final CommunicationService commService)
    {
        final Map<Integer, List<Message>> clientMessages = createPlainMessagesForClients(nrClients, nrMessages, nrSessions);
        return sendMessagesToServer(nrClients, commService, clientMessages);
    }

    /**
     * Each client throws its messages to server in concurrency
     */
    public static Map<Integer, List<Message>> sendMessagesToServer(int nrClients,
                                                                   final CommunicationService commService,
                                                                   final Map<Integer, List<Message>> clientMessages)
    {
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(nrClients);
        final Executor executor = Executors.newFixedThreadPool(nrClients);

        final AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < nrClients; i++) {
            executor.execute(new Runnable()
            {
                public void run()
                {
                    try {
                        start.await();
                        int val = count.getAndIncrement();
                        List<Message> messages = clientMessages.get(val);
                        for (Message m : messages) {
                            commService.serverReceive(m);
                        }
                    } catch (InterruptedException e) {
                        fail(e.getMessage());
                    } finally {
                        end.countDown();
                    }
                }
            });
        }
        start.countDown();
        try {
            end.await();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        return clientMessages;
    }

    /**
     * @param esid the editing session id
     * @param operations the operations to build the messages from
     * @return the messages to be sent by the clients
     */
    public static List<Message> createMessagesFromOperations(int esid, AbstractOperation... operations) {
        List<Message> messages = new ArrayList<Message>();
        int i = 0;

        for (AbstractOperation op : operations) {
            Message message = new Message(new State(i++, 0), op);
            message.setEditingSessionId(esid);
            messages.add(message);
        }
        return messages;
    }

    /**
     * Check if servers have the same document & equivalent state at quiescence
     */
    public static void compareServersAtQuiescence() {

        Collection<ServerJupiterAlg> servers = ClientServerCorrespondents.getInstance().getCorrespondents().values();
        ServerJupiterAlg previous = null;

        for (Iterator<ServerJupiterAlg> it = servers.iterator(); it.hasNext(); ) {
            ServerJupiterAlg current = it.next();

            if (previous != null) {
                Document previousDocument = previous.getDocument();
                Document currentDocument = current.getDocument();
                assertEquals("Inconsistent final document among servers. Server1:" + previous + ", Server2:" + current,
                        previousDocument.getContent(), currentDocument.getContent());

                State previousState = previous.getCurrentState();
                State currentState = current.getCurrentState();
                assertEquals("Inconsistent final state among servers. Server1:" + previous + ", Server2:" + current,
                        previousState.getGeneratedMsgs() + previousState.getReceivedMsgs(),
                        currentState.getGeneratedMsgs() + currentState.getReceivedMsgs());
            }
            previous = current;
        }
    }

    /**
     * @return  the mapping of the messages sent to the server by each client
     */
    private static Map<Integer, List<Message>> createPlainMessagesForClients(int nrClients, int nrMessages, int nrSessions) {
        Random charsRandom = new Random();
        String alphaNumeric = "abcdefghijklmnopqrstuwxyz0123456789";
        Map<Integer, List<Message>> result = new HashMap<Integer, List<Message>>();

        for (int siteId = 0;  siteId < nrClients; siteId++) {
            List<Message> messages = new ArrayList<Message>();
            for (int j = 0 ; j < nrMessages; j++) {
                Message message = new Message(
                        new State(j, 0),
                        new InsertOperation(siteId, j, alphaNumeric.charAt(charsRandom.nextInt(35)))  //todo: make random operations: insert delete noop
                );
                message.setEditingSessionId(siteId % nrSessions);
                messages.add(message);
            }
            result.put(siteId, messages);
        }
        assertEquals("Invalid nr of clients created", nrClients, result.size());
        assertEquals("Invalif nr of client messages created", nrMessages, result.get(0).size());

        return result;
    }

    /**
     * This method is used to output a standard warning message on the logger in order to notify the user that a given
     * test is skipped and that it can be activated by specifying a flag on the maven command line. This is useful
     * whenever a test needs to access remote services or needs a special setup to be executed; by default it is not
     * executed unless a flag is specified.
     * 
     * @param log The logger to be used to output the warning.
     * @param flag The flag that should be specified on the command line to activate the test.
     */
    public static void warnSkipped(Logger log, String flag)
    {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        log.warn(String.format(
            "Test %s.%s() is skipped. Specify the -DargLine=\"-D%s\" (or simply -D%s) parameter on the mvn command line to enable it.",
            stackTraceElement.getClassName(), stackTraceElement.getMethodName(), flag, flag));
    }
}
