package fr.loria.score.server;

import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.jupiter.model.Message;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CommunicationServiceImpl implements CommunicationService {
    //the client id generator
    private final AtomicInteger atomicInt = new AtomicInteger();

    //todo: just a hack!
    //the mapping between the editing session id and the list of ids of the clients that share the same editing session
    private Map<Integer, List<Integer>> locks = new HashMap<Integer, List<Integer>>();
    {
        locks.put(0, new ArrayList<Integer>());
    }

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
        Map<Integer, List<Integer>> locks = getEditingSessions();
        if (locks.containsKey(esid)) {
            int siteId = msg.getSiteId();
            ServerJupiterAlg serverJupiter = ClientServerCorrespondents.getInstance().getCorrespondents().get(siteId);
            // overkill for performance, but that's a Jupiter constraint to serialize receive operations
            //sync on a per editing session lock
            Object editingSessionLock = locks.get(esid);
            synchronized (editingSessionLock) {
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
        System.out.println("Create server pair..");
        updateEditingSessions(clientJupiterAlg);
        return ClientServerCorrespondents.getInstance().addServerForClient(clientJupiterAlg);
    }

    /**
     * @return the mapping between editing sessions and the participants
     */
    protected Map<Integer, List<Integer>> getEditingSessions() {
        return locks;
    }

    /**
     * Based on it's editing session id the client's id is added to the sessions map
     * @param clientJupiterAlg it's id is to be added to the editing sessions map
     */
    private void updateEditingSessions(ClientJupiterAlg clientJupiterAlg) {
        System.out.println("Update editing sessions ..");
        int esid = clientJupiterAlg.getEditingSessionId();
        Map<Integer, List<Integer>> locks = getEditingSessions();
        synchronized (locks) {
            locks.get(esid).add(clientJupiterAlg.getSiteId());
            ClientServerCorrespondents.getInstance().setEditingSessions(locks);
        }
    }
}