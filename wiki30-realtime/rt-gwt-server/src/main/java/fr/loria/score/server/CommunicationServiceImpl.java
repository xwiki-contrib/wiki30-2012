package fr.loria.score.server;

import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.jupiter.model.Message;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CommunicationServiceImpl implements CommunicationService {
    //the client id generator
    private final AtomicInteger atomicInt = new AtomicInteger();

    //the mapping between the editing session id and the list of ids of the clients that share the same editing session
    private final Map<Integer, List<Integer>> locks = new HashMap<Integer, List<Integer>>();

    /**
     * {@inheritDoc}
     */
    public Message[] clientReceive(int siteId) {
        System.out.println("Client receive for siteId: " + siteId);
        ServerJupiterAlg server = ClientServerCorrespondents.getInstance().getCorrespondents().get(siteId);
        Message[] msg = server.getMessages();
        System.out.println(">>> Client #: " + siteId + " receives: " + Arrays.asList(msg));
        return msg;
    }

    /**
     * {@inheritDoc}
     */
    public void serverReceive(Message msg) {
        // now the corresponding server receives the message and atomically notifies peer servers which send their updates to their clients
        System.out.println("Thread: "+Thread.currentThread().getName()+ " Server receives message: " + msg );
        int esid = msg.getEditingSessionId();
        if (locks.containsKey(esid)) {
            int siteId = msg.getSiteId();
            ServerJupiterAlg serverJupiter = ClientServerCorrespondents.getInstance().getCorrespondents().get(siteId);
            // overkill for performance, but that's a Jupiter constraint to serialize receive operations
            //sync on a per editing session lock
            synchronized (locks.get(esid)) {
                serverJupiter.receive(msg);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Integer generateClientId() {
        int increment = atomicInt.getAndIncrement();
        System.out.println("Generated client id: " + increment);
        return increment;
    }

    /**
     * {@inheritDoc}
     */
    public String createServerPairForClient(ClientJupiterAlg clientJupiterAlg) {
        //Based on it's editing session id the client's id is added to the sessions map
        int esid = clientJupiterAlg.getEditingSessionId();
        synchronized (locks) {
            if (!locks.containsKey(esid)) {
                locks.put(esid, new ArrayList<Integer>());
            }
            locks.get(esid).add(clientJupiterAlg.getSiteId());
            ClientServerCorrespondents.getInstance().setEditingSessions(locks);
        }
        System.out.println("Create server pair..");
        return ClientServerCorrespondents.getInstance().addServerForClient(clientJupiterAlg);
    }
}