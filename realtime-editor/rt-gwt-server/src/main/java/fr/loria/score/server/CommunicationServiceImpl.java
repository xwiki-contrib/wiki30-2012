package fr.loria.score.server;

import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.jupiter.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class CommunicationServiceImpl implements CommunicationService {
    private static final Log LOG = LogFactory.getLog(CommunicationService.class);
    private ClientServerCorrespondents clientServerCorrespondents = ClientServerCorrespondents.getInstance();

    /**
     * {@inheritDoc}
     */
    public Message[] clientReceive(int siteId) {
        ServerJupiterAlg server = clientServerCorrespondents.getCorrespondents().get(siteId);
        // todo: bf in case not found, create one.
        // It is the case when the user has left the tab open, the server went down and the client asks the content for an unexistent id
        if (server == null) {
        }

        //todo: add buffering
        Message[] msgs;
        List<Message> list = server.unsentMessages;
        synchronized (list) {
            msgs = list.toArray(new Message[0]);
            list.clear();
        }
        return msgs;
    }

    /**
     * {@inheritDoc}
     */
    public void serverReceive(Message msg) {
        // now the corresponding server receives the message and atomically notifies peer servers which send their updates to their clients
        synchronized (CommunicationServiceImpl.class) {
            LOG.debug("Server received message " + msg);
            int siteId = msg.getSiteId();
            ServerJupiterAlg serverJupiter = clientServerCorrespondents.getCorrespondents().get(siteId);
            serverJupiter.receive(msg);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String createServerPairForClient(ClientJupiterAlg clientJupiterAlg) {
        LOG.debug("Adding server correspondent for client " + clientJupiterAlg);
        return clientServerCorrespondents.addServerForClient(clientJupiterAlg);
    }
}  