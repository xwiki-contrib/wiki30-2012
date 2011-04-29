package fr.loria.score.server;

import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.jupiter.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class CommunicationServiceImpl implements CommunicationService {
    private static final Log logger = LogFactory.getLog(CommunicationServiceImpl.class);

    //the client id generator
    private final AtomicInteger atomicInt = new AtomicInteger();

    //the mapping between the editing session id and the list of ids of the clients that share the same editing session
    private final Map<Integer, List<Integer>> locks = new HashMap<Integer, List<Integer>>();

    /**
     * {@inheritDoc}
     */
    public Message[] clientReceive(int siteId) {
        logger.debug("Client receive for siteId: " + siteId);
        ServerJupiterAlg server = ClientServerCorrespondents.getInstance().getCorrespondents().get(siteId);
        Message[] msg = new Message[]{};
        if (server != null) {
            msg = server.getMessages();
            logger.debug("Client #: " + siteId + " receives: " + Arrays.asList(msg));
        }
        return msg;
    }

    /**
     * {@inheritDoc}
     */
    public void serverReceive(Message msg) {
        // now the corresponding server receives the message and atomically notifies peer servers which send their updates to their clients
        logger.debug(" Server receives message: " + msg );
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
        logger.debug("Generated client id: " + increment);
        return increment;
    }

    /**
     * {@inheritDoc}
     */
    public String createServerPairForClient(ClientJupiterAlg clientJupiterAlg) {
        logger.debug("Create server pair for client: " + clientJupiterAlg);
        //Based on it's editing session id the client's id is added to the sessions map
        int esid = clientJupiterAlg.getEditingSessionId();
        synchronized (locks) {
            if (!locks.containsKey(esid)) {
                locks.put(esid, new ArrayList<Integer>());
            }
            locks.get(esid).add(clientJupiterAlg.getSiteId());
            ClientServerCorrespondents.getInstance().setEditingSessions(locks);
        }
        return ClientServerCorrespondents.getInstance().addServerForClient(clientJupiterAlg);
    }

    public void removeServerPairForClient(ClientJupiterAlg clientJupiterAlg) {
        logger.debug("Removing server pair for client with id: " + clientJupiterAlg.getSiteId());

        //1. remove it from the editing session id
        int esid = clientJupiterAlg.getEditingSessionId();
        if (locks.containsKey(esid)) {
            synchronized (locks) {
                locks.get(esid).remove(Integer.valueOf(clientJupiterAlg.getSiteId()));
                ClientServerCorrespondents.getInstance().setEditingSessions(locks);
            }
        }
        //2. remove it's server correspondent
        ClientServerCorrespondents.getInstance().removeServerForClient(clientJupiterAlg);
    }
}