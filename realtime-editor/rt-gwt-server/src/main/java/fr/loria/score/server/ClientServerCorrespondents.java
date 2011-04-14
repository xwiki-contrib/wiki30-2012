package fr.loria.score.server;


import fr.loria.score.client.ClientJupiterAlg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Each site has a pair of a client and a server
 */
public final class ClientServerCorrespondents {
    private static final Logger log = Logger.getLogger(ClientServerCorrespondents.class.getName());

    private final Map<Integer, ServerJupiterAlg> correspondents = new HashMap<Integer, ServerJupiterAlg>();
    private Map<Integer, List<Integer>> editingSessions = new HashMap<Integer, List<Integer>>();
    private static ClientServerCorrespondents instance = new ClientServerCorrespondents();

    private ClientServerCorrespondents() {
    }

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
        log.fine("Add server pair for client: " + clientJupiterAlg);
        int key = clientJupiterAlg.getSiteId();
        // in case a new client joins he receives the content available on an existing 'jupiter server' in same editing session
        String availableContent = clientJupiterAlg.getData();
        List<Integer> serverIds = editingSessions.get(clientJupiterAlg.getEditingSessionId());
        if (serverIds.size() > 0) {
            ServerJupiterAlg serverPair = correspondents.get(serverIds.get(0));
            if (serverPair != null) {
                availableContent = serverPair.getData();
            }
        }
        ServerJupiterAlg serverJupiter = new ServerJupiterAlg(availableContent, key);
        synchronized (correspondents) {
            correspondents.put(key, serverJupiter);
        }
        return availableContent;
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

    public void setEditingSessions(Map<Integer, List<Integer>> editingSessions) {
        this.editingSessions = editingSessions;
    }
}
