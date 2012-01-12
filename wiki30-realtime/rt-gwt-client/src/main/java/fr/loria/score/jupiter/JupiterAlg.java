package fr.loria.score.jupiter;

import fr.loria.score.jupiter.model.AbstractOperation;
import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.model.State;
import fr.loria.score.jupiter.transform.Transformation;
import fr.loria.score.jupiter.transform.TransformationFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Base class which uses the Jupiter algorithm for achieving convergence across divergent copies of document.
 * It uses a plug-able Document and transformation functions
 *
 * <b>Note: it only requires that TP1 is satisfied by transformation functions.</b>
 */
public abstract class JupiterAlg {
    private static final Logger logger = Logger.getLogger(JupiterAlg.class.getName());

    // Identifies a client to the server.
    // If 2 operations are simultaneously received by the server, it will sequentially apply them in an ascending order
    protected int siteId;

    protected int editingSessionId = -1;

    protected State currentState = new State();

    //the outgoing list of processed operations used to transform the received operations
    protected List<Message> queue = new LinkedList<Message>();
    protected Transformation xform;

    protected volatile Document document;

    /**
     * Used only by GWT's reflection mechanism
     */
    protected JupiterAlg() {}

    public JupiterAlg(Document document, Transformation transformation) {
        this.document = document;
        xform = transformation;
    }

    public JupiterAlg(Document document) {
        this(document, TransformationFactory.createTransformation(document));
    }

    /**
     * Generates a local operation
     *
     * @param op the operation to be applied and sent
     */
    public void generate(AbstractOperation op) { //todo: use message and remove esid here
        logger.info(this + "\t Generate: " + op);
        //apply op locally
        document.apply(op);

        Message newMsg = new Message(new State(currentState), op);
        newMsg.setEditingSessionId(editingSessionId);
        queue.add(newMsg);
        currentState.incGeneratedMsgs();
        logger.fine(this.toString());
        send(newMsg);
    }

    /**
     * Receive a message
     *
     * @param receivedMsg the received message
     */
    public void receive(Message receivedMsg) {
        logger.info(this + "\tReceive: " + receivedMsg);
        logger.fine("Queue is: " + queue);

        // Discard acknowledged messages
        for (Iterator<Message> it = queue.iterator(); it.hasNext();) {
            Message m = it.next();
            if (m.getState().getGeneratedMsgs() < receivedMsg.getState().getReceivedMsgs()) {
                logger.fine(this + "\tRemove " + m);
                it.remove();
            }
        }
        assert (receivedMsg.getState().getGeneratedMsgs() == currentState.getReceivedMsgs());

        // Transform received message and the ones in the queue
        AbstractOperation receivedOperation = receivedMsg.getOperation();
        for (Message m : queue) {
            AbstractOperation originallyReceivedOperation = receivedOperation;

            AbstractOperation localOperation = m.getOperation();
            receivedOperation = xform.transform(receivedOperation, localOperation);
            logger.fine(this + "\tTransformed op1 = " + receivedOperation);

            AbstractOperation op2 = xform.transform(localOperation, originallyReceivedOperation);
            logger.fine(this + "\tTransformed op2 = " + op2);

            m.setOperation(op2);
        }
        logger.fine(this + "\t applying operation: " + receivedOperation);
        //apply transformed receivedMsg
        document.apply(receivedOperation);

        Message newMsg = new Message(new State(currentState), receivedOperation);
        newMsg.setEditingSessionId(receivedMsg.getEditingSessionId());

        currentState.incReceivedMsgs();
        execute(newMsg); // todo:bf -rename afterReceive
        logger.fine(this.toString());
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getSiteId() {
        return siteId;
    }

    public State getCurrentState() {
        return new State(currentState);
    }
    
    /**
     * Both client and server must execute some action after receiving the message
     *
     * @param receivedMsg the received message to use
     */
    protected abstract void execute(Message receivedMsg);

    /**
     * Sends the message either to the corresponding server - if the sender is a client, either to the corresponding client- if the sender is a server
     *
     * @param m the message to be sent
     */
    protected abstract void send(Message m);


    @Override
    public int hashCode() {
        return siteId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof JupiterAlg) {
            JupiterAlg other = (JupiterAlg) o;
            return siteId == other.siteId;
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + siteId + "#" + document + ", " + currentState + "#";
    }

    public void setEditingSessionId(int editingSessionId) {
        this.editingSessionId = editingSessionId;
    }

    public int getEditingSessionId() {
        return editingSessionId;
    }
}
