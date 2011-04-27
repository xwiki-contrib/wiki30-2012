package fr.loria.score.server;

import fr.loria.score.jupiter.JupiterAlg;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.model.Operation;
import fr.loria.score.jupiter.transform.Transformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class ServerJupiterAlg extends JupiterAlg {
    private static final Log logger = LogFactory.getLog(ServerJupiterAlg.class);

    private final List<Message> unsentMessages = new ArrayList<Message>();
    private final SortedSet<Message> causalOrderedMessages = new TreeSet<Message>(new Comparator<Message>() {
        public int compare(Message m1, Message m2) { //+1  if o1 > o2, 0 if o1 equals o2, -1 if o1 < o2
            return m1.getState().getGeneratedMsgs() - m2.getState().getGeneratedMsgs();
        }
    });

    public ServerJupiterAlg() {
    }

    public ServerJupiterAlg(String initialData, int siteId) {
        super(initialData, siteId);
    }

    public ServerJupiterAlg(String initialData, int siteId, Transformation transform) {
        super(initialData, siteId, transform);
    }

    @Override
    public void receive(Message receivedMsg) {
        doReceive(receivedMsg);
    }

    private synchronized void doReceive(Message receivedMsg) {
        // Ensure causality processing
        if (receivedMsg.getState().getGeneratedMsgs() > currentState.getReceivedMsgs()) {
            logger.debug("Adding " + receivedMsg + "to: " + causalOrderedMessages);
            causalOrderedMessages.add(receivedMsg);
        } else {
            super.receive(receivedMsg);
                for (Iterator<Message> it = causalOrderedMessages.iterator(); it.hasNext();) {
                Message m = it.next();
                if (m.getState().getGeneratedMsgs() == currentState.getReceivedMsgs()) { // or use a copy on write list
                    super.receive(m);
                    it.remove();
                }
            }
        }
    }

    @Override
    protected void execute(Message m) {
        //Broadcasting to peers in the same editing session
        Operation op = m.getOperation();
        List<Integer> peersIdList = ClientServerCorrespondents.getInstance().getEditingSessions().get(m.getEditingSessionId());
        for (Integer peerId : peersIdList) {
            ServerJupiterAlg peerServer = ClientServerCorrespondents.getInstance().getCorrespondents().get(peerId);
            if (!peerServer.equals(this)) {
                logger.debug(this + "\tSend message " + m + " to server = " + peerServer);
                peerServer.generate(op);
            }
        }
    }

    @Override
    protected void send(Message m) {
        logger.debug(this + " Adding " + m + "to unsent buffer" + unsentMessages);
        synchronized (unsentMessages) {
            unsentMessages.add(new Message(m));
        }
    }

    public Message[] getMessages() {
        Message[] msgs;
        synchronized (unsentMessages) {
            msgs = unsentMessages.toArray(new Message[0]);
            unsentMessages.clear();
        }
        return msgs;
    }

    public Set<Message> getCausalOrderedMessages() {
        return new TreeSet<Message>(causalOrderedMessages);
    }
}

