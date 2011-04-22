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
     * {@inheritDoc}
     */
    Message[] clientReceive(int siteId);

    /**
     * {@inheritDoc}
     */
    void serverReceive(Message msg);

    /**
     * {@inheritDoc}
     */
    String createServerPairForClient(ClientJupiterAlg clientJupiterAlg);

    /**
     * As the client quits the real-time editing session, it's corresponding server pair is removed
     * @param clientJupiterAlg the client for whom to remove it's corresponding server pair
     */
    void removeServerPairForClient(ClientJupiterAlg clientJupiterAlg);

    /**
     * {@inheritDoc}
     */
    Integer generateClientId();

    static class ServiceHelper {
        private static CommunicationServiceAsync communicationService = GWT.create(CommunicationService.class);

        public static synchronized CommunicationServiceAsync getCommunicationService() {
            return communicationService;
        }
    }
}


