package fr.loria.score.server;

import fr.loria.score.jupiter.JupiterAlg;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.model.Operation;
import fr.loria.score.jupiter.transform.Transformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class ServerJupiterAlg extends JupiterAlg {
    private static final Log LOG = LogFactory.getLog(ServerJupiterAlg.class);
    private ClientServerCorrespondents clientServerCorrespondents = ClientServerCorrespondents.getInstance();

    protected final List<Message> unsentMessages = new ArrayList<Message>();

    public ServerJupiterAlg() {
    }

    public ServerJupiterAlg(String initialData, int siteId) {
        super(initialData, siteId);
    }

    public ServerJupiterAlg(String initialData, int siteId, Transformation transform) {
        super(initialData, siteId, transform);
    }

    @Override
    protected void execute(Message m) {
        //Broadcasting
        Operation op = m.getOperation();
        for (ServerJupiterAlg peerServer : clientServerCorrespondents.getCorrespondents().values()) {
            if (!peerServer.equals(this)) {
                LOG.debug(this + "\tSend message " + m + " to server = " + peerServer);
                peerServer.generate(op);
            }
        }
    }

    @Override
    protected void send(Message m) {
        LOG.debug(this + " Adding " + m + "to unsent buffer" + unsentMessages);
        synchronized (unsentMessages) {
            unsentMessages.add(new Message(m));
        }
    }

    public List<Message> getUnsentMessages() {
        return unsentMessages;
    }
}

