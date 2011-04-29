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
    public Message[] clientReceive(int siteId) {
        logger.debug("Client receive for siteId: " + siteId);
        return ClientServerCorrespondents.getInstance().clientReceive(siteId);
    }

    /**
     * {@inheritDoc}
     */
    public void serverReceive(Message msg) {
        logger.debug("Server receives message: " + msg );
        ClientServerCorrespondents.getInstance().serverReceive(msg);
    }

    /**
     * {@inheritDoc}
     */
    public String createServerPairForClient(ClientJupiterAlg clientJupiterAlg) {
        logger.debug("Create server pair for client with id: " + clientJupiterAlg.getSiteId());
        return ClientServerCorrespondents.getInstance().addServerForClient(clientJupiterAlg);
    }

    public void removeServerPairForClient(ClientJupiterAlg clientJupiterAlg) {
        logger.debug("Remove server pair for client with id: " + clientJupiterAlg.getSiteId());
        ClientServerCorrespondents.getInstance().removeServerForClient(clientJupiterAlg);
    }
}