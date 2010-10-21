package fr.loria.score.server;


import fr.loria.score.client.ClientJupiterAlg;

import java.io.Serializable;

/**
 * A pair of a client and the corresponding server
 */
public class ClientServerPair implements Serializable {
    private ClientJupiterAlg client;
    private ServerJupiterAlg server;

    public ClientServerPair(ClientJupiterAlg client, ServerJupiterAlg server) {
        this.client = client;
        this.server = server;
    }

    public ClientJupiterAlg getClient() {
        return client;
    }

    public ServerJupiterAlg getServer() {
        return server;
    }
}
