package fr.loria.score.client;

import org.xwiki.component.annotation.ComponentRole;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import fr.loria.score.jupiter.model.Message;

@ComponentRole
@RemoteServiceRelativePath("CommunicationService.gwtrpc")
public interface CommunicationService extends RemoteService {
    /**
     * This method is regularly called by the client in an attempt to simulate 'server-push. Consider using WebSockets etc.
     * It fetches from the server the messages to be applied on client side.
     *
     * @param siteId   the id of the site
     */
    Message[] clientReceive(int siteId);

    /**
     * Client pushes the data to server, simulating a 'receive' from the server
     *
     * @param msg      the message to be 'received' by server, which is actually sent by the client
     */
    void serverReceive(Message msg);

    /**
     * Each time a new client joins the editing session, it's corresponding server pair is created
     *
     * @param clientJupiterAlg the client instance. It must have the session id set up
     * @return the available content on which other clients are performing real time operations <b>if any</b>
     */
    String createServerPairForClient(ClientJupiterAlg clientJupiterAlg);

    /**
     * As the client quits the real-time editing session, it's corresponding server pair is removed
     * @param clientJupiterAlg the client for whom to remove it's corresponding server pair
     */
    void removeServerPairForClient(ClientJupiterAlg clientJupiterAlg);

    /**
     * Every new generated client gets an ID
     */
    Integer generateClientId();

    static class ServiceHelper {
        private static CommunicationServiceAsync communicationService = GWT.create(CommunicationService.class);

        public static synchronized CommunicationServiceAsync getCommunicationService() {
            return communicationService;
        }
    }
}


