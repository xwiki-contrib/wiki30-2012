package fr.loria.score;

import static junit.framework.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.slf4j.Logger;

import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.jupiter.model.InsertOperation;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.model.State;

/**
 * @author: Bogdan Flueras (email: Bogdan.Flueras@inria.fr)
 */
public class TestUtils
{
    public static final String RUN_STRESS_TESTS_FLAG = "stressTests"; 
    
    /**
     * Creates the server pairs for each client request
     */
    public static void createServerPairs(final int nrClients, final int nrSessions,
        final CommunicationService commService)
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
                        ClientJupiterAlg client = new ClientJupiterAlg("", counter);
                        client.setEditingSessionId(counter % nrSessions);

                        commService.createServerPairForClient(client);
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
    }

    /**
     * Each client throws its messages to server in concurrency
     */
    public static void sendMessagesToServer(int nrClients, int nrMessages, int nrSessions,
        final CommunicationService commService)
    {
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(nrClients);
        final Executor executor = Executors.newFixedThreadPool(nrClients);

        final Map<Integer, List<Message>> clientMessages = createMessagesForClients(nrClients, nrMessages, nrSessions);
        final AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < nrClients; i++) {
            executor.execute(new Runnable()
            {
                public void run()
                {
                    try {
                        start.await();
                        for (Message m : clientMessages.get(count.getAndIncrement())) {
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
    }

    // Each client generates some messages to be sent to central server
    private static Map<Integer, List<Message>> createMessagesForClients(int nrClients, int nrMessages, int nrSessions)
    {
        Map<Integer, List<Message>> result = new HashMap<Integer, List<Message>>();

        Random charsRandom = new Random();
        String alphaNumeric = "abcdefghijklmnopqrstuwxyz0123456789";

        for (int siteId = 0; siteId < nrClients; siteId++) {
            List<Message> messages = new ArrayList<Message>();
            for (int j = 0; j < nrMessages; j++) {
                Message message =
                    new Message(new State(j, 0), new InsertOperation(j, alphaNumeric.charAt(charsRandom.nextInt(35)),
                        siteId));
                message.setEditingSessionId(siteId % nrSessions);
                messages.add(message);
            }
            result.put(siteId, messages);
        }
        Assert.assertEquals("Invalid nr of clients created", nrClients, result.size());
        Assert.assertEquals("Invalid nr of client messages created", nrMessages, result.get(0).size());
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
