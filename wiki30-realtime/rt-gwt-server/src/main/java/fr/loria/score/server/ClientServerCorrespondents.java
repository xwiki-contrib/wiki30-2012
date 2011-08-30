package fr.loria.score.server;


import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.jupiter.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Each site has a pair of a client and a server
 */
public final class ClientServerCorrespondents {

    private final Map<Integer, ServerJupiterAlg> correspondents = new HashMap<Integer, ServerJupiterAlg>();

    //the mapping between the editing session id and the list of ids of the clients that share the same editing session
    private final Map<Integer, List<Integer>> editingSessions = new HashMap<Integer, List<Integer>>();

    private static ClientServerCorrespondents instance = new ClientServerCorrespondents();
    
    /* The logger to use for logging. */
    private Logger log = LoggerFactory.getLogger(ClientServerCorrespondents.class);

    private ClientServerCorrespondents() {}

    public static ClientServerCorrespondents getInstance() {
        return instance;
    }

    /**
     * Creates a new server correspondent for the given client
     *
     * @param clientJupiterAlg the client for which to create a server correspondent
     * @return the text on some server in same editing session if any, otherwise the text sent by the client
     */
    public String addServerForClient(ClientJupiterAlg clientJupiterAlg) {
        //Based on it's editing session id the client's id is added to the sessions map
        int editingSessionId = clientJupiterAlg.getEditingSessionId();
        synchronized (editingSessions) {
            if (!editingSessions.containsKey(editingSessionId)) {
                editingSessions.put(editingSessionId, new ArrayList<Integer>());
            }
            editingSessions.get(editingSessionId).add(clientJupiterAlg.getSiteId());
        }

        // the client receives the content available on an existing 'jupiter server' in same editing session (if any)
        int siteId = clientJupiterAlg.getSiteId();
        String availableContent = clientJupiterAlg.getData();
        List<Integer> serverIds = editingSessions.get(editingSessionId);
        if (serverIds.size() > 0) {
            ServerJupiterAlg serverPair = correspondents.get(serverIds.get(0));
            if (serverPair != null) {
                availableContent = serverPair.getData();
            }
        }
        ServerJupiterAlg serverJupiter = new ServerJupiterAlg(availableContent, siteId);
        synchronized (correspondents) {
            correspondents.put(siteId, serverJupiter);
        }
        return availableContent;
    }

    public void removeServerForClient(ClientJupiterAlg clientJupiterAlg) {
        int editingSessionId = clientJupiterAlg.getEditingSessionId();
        int siteId = clientJupiterAlg.getSiteId();
         //1. remove it from the editing session id
        if (editingSessions.containsKey(editingSessionId)) {
            synchronized (editingSessions) {
                editingSessions.get(editingSessionId).remove(Integer.valueOf(siteId));
            }
        }

        //2. remove it's server correspondent
        if (correspondents.containsKey(siteId)) {
            synchronized (correspondents) {
                correspondents.remove(siteId);
            }
        }
    }

    public void serverReceive(Message msg) {
        // now the corresponding server receives the message and atomically notifies peer servers which send their updates to their clients
        int esid = msg.getEditingSessionId();
        if (editingSessions.containsKey(esid)) {
            int siteId = msg.getSiteId();
            ServerJupiterAlg serverJupiter = ClientServerCorrespondents.getInstance().getCorrespondents().get(siteId);
            // overkill for performance, but that's a Jupiter constraint to serialize receive operations
            //sync on a per editing session lock
            synchronized (editingSessions.get(esid)) {
                serverJupiter.receive(msg);
            }
        }
    }

    public Message[] clientReceive(int siteId) {
        Message[] msg = new Message[]{};
        ServerJupiterAlg server = correspondents.get(siteId);
        if (server != null) {
            msg = server.getMessages();
            if (msg.length >= 0) {
                log.debug("Client #: " + siteId + " receives: " + Arrays.asList(msg));
            }
        }
        return msg;
    }

    /**
     * @return the server correspondents map
     */
    public Map<Integer, ServerJupiterAlg> getCorrespondents() {
        return correspondents;
    }

    /**
     * @return the mapping between editing sessions and the participants
     */
    public Map<Integer, List<Integer>> getEditingSessions() {
        return editingSessions;
    }
}
