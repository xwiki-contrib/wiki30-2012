package fr.loria.score.server;


import fr.loria.score.client.ClientJupiterAlg;

import java.util.HashMap;
import java.util.Map;

/**
 * Each site has a pair of a client and a server
 */
public final class ClientServerCorrespondents {
    private Map<Integer, ServerJupiterAlg> correspondents = new HashMap<Integer, ServerJupiterAlg>();
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
     * @return the text on the server if any, otherwise the text sent by the client
     */
    public String addServerForClient(ClientJupiterAlg clientJupiterAlg) {
        synchronized (ClientServerCorrespondents.class) {
            int key = clientJupiterAlg.getSiteId();
            // in case a new client joins the editing session he receives the content available on an existing 'jupiter server'
            String availableContent = clientJupiterAlg.getData();

            for (ServerJupiterAlg server : correspondents.values()) {
                availableContent = server.getData();
                break;
            }
            ServerJupiterAlg serverJupiter = new ServerJupiterAlg(availableContent, key);
            correspondents.put(key, serverJupiter);
            return availableContent;
        }
    }

    /**
     * @return the server correspondents map
     */
    public Map<Integer, ServerJupiterAlg> getCorrespondents() {
        return correspondents;
    }
}
