package fr.loria.score.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fr.loria.score.jupiter.model.Message;

/**
 * Enables communication between client and server.
 */
public interface CommunicationServiceAsync {

    /**
     * This method is regularly called by the client in an attempt to simulate 'server-push. Consider using WebSockets etc.
     * It fetches from the server the messages to be applied on client side.
     *
     * @param siteId   the id of the site
     * @param callback to be executed after the call completed
     */
    void clientReceive(int siteId, AsyncCallback<Message[]> callback);

    /**
     * Client pushes the data to server, simulating a 'receive' from the server
     *
     * @param msg      the message to be 'received' by server, which is actually sent by the client
     * @param callback to be executed after the call completed
     */
    void serverReceive(Message msg, AsyncCallback<Void> callback);

    /**
     * Each time a new client joins the editing session, it's corresponding server pair is created
     *
     * @param clientJupiterAlg the client instance
     * @param async            to be executed after the call completed
     * @return the available content on which other clients are performing real time operations <b>if any</b>
     */
    void createServerPairForClient(ClientJupiterAlg clientJupiterAlg, AsyncCallback<String> async);

    /**
     * Every new generated client gets an ID
     *
     * @param callback to be executed on client side after call completion
     */
    void generateClientId(AsyncCallback<Integer> callback);
}
