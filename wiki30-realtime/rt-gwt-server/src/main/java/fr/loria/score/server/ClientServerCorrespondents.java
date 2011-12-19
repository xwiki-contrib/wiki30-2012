package fr.loria.score.server;

import fr.loria.score.client.ClientDTO;
import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Each site has a pair of a client and a server
 */
public final class ClientServerCorrespondents {
    // the mapping between the site id's and the corresponding server pair
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
     * @param clientDTO the client for which to create a server correspondent
     * @return the document on some server in same editing session if any, otherwise the document sent by the client
     */
    public Document addServerForClient(ClientDTO clientDTO) {
        //Based on it's editing session id the client's id is added to the sessions map
        int editingSessionId = clientDTO.getEditingSessionId();
        synchronized (editingSessions) {
            if (!editingSessions.containsKey(editingSessionId)) {
                editingSessions.put(editingSessionId, new ArrayList<Integer>());
            }
            editingSessions.get(editingSessionId).add(clientDTO.getSiteId());
        }

        // the client receives the content available on an existing 'jupiter server' in same editing session (if any)
        int siteId = clientDTO.getSiteId();

        Document document = clientDTO.getDocument();

        List<Integer> serverIds = editingSessions.get(editingSessionId);
        if (serverIds.size() > 0) {
            ServerJupiterAlg serverPair = correspondents.get(serverIds.get(0));
            if (serverPair != null) {
                document = serverPair.getDocument();
            }
        }

        ServerJupiterAlg serverJupiter = new ServerJupiterAlg(document, siteId);

        synchronized (correspondents) {
            correspondents.put(siteId, serverJupiter);
        }
        return document;
    }

    public void removeServerForClient(ClientDTO clientDTO) {
        int editingSessionId = clientDTO.getEditingSessionId();
        int siteId = clientDTO.getSiteId();
         //1. remove it from the editing session id
        if (editingSessions.containsKey(editingSessionId)) {
            synchronized (editingSessions) {
                List<Integer> serverIds = editingSessions.get(editingSessionId);
                int idx = serverIds.indexOf(siteId);
                if (idx > -1) {
                    serverIds.remove(idx);
                }
                //remove the empty mapping
                if (serverIds.size() == 0) {
                    editingSessions.remove(editingSessionId);
                }
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
