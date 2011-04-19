package fr.loria.score.jupiter;

import com.google.gwt.core.client.GWT;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.model.Operation;
import fr.loria.score.jupiter.model.State;
import fr.loria.score.jupiter.transform.Transformation;
import fr.loria.score.jupiter.transform.TransformationFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class which uses the Jupiter algorithm for achieving convergence across divergent copies of data.
 * <b>Note: it only requires that TP1 is satisfied by transformation functions.</b>
 */
public abstract class JupiterAlg implements Serializable {

    // Identifies a client to the server.
    // If 2 operations are simultaneously received by the server, it will sequentially apply them in an ascending order
    protected int siteId;

    protected State currentState = new State();

    //the outgoing list of processed operations used to transform the received operations
    protected transient List<Message> queue = new LinkedList<Message>();
    protected transient Transformation xform;

    protected volatile String data;

    public JupiterAlg() {
    }

    public JupiterAlg(String initialData, int siteId, Transformation transformation) {
        this.siteId = siteId;
        this.data = initialData;
        xform = transformation;
    }

    public JupiterAlg(String initalData, int siteId) {
        this(initalData, siteId, TransformationFactory.createResselTransformation());
    }

    public JupiterAlg(String initalData) {
        this.data = initalData;
    }

    /**
     * Generates a local operation
     *
     * @param op the operation to be applied and sent
     */
    public void generate(Operation op) {
        GWT.log(this + "\t Generate: " + op);
        //apply op locally
        data = op.execute(data);
        //todo: clone
        Message newMsg = new Message(new State(currentState.getGeneratedMsgs(), currentState.getReceivedMsgs()), op);
        queue.add(newMsg);
        currentState.incGeneratedMsgs();
        GWT.log(this.toString());
        send(newMsg);
    }

    /**
     * Receive a message
     *
     * @param receivedMsg the received message
     */
    public void receive(Message receivedMsg) {
        GWT.log(this + "\tReceive: " + receivedMsg);
        // Discard acknowledged messages
        for (Iterator<Message> it = queue.iterator(); it.hasNext();) {
            Message m = it.next();
            if (m.getState().getGeneratedMsgs() < receivedMsg.getState().getReceivedMsgs()) {
                GWT.log(this + "\tRemove " + m);
                it.remove();
            }
        }
        assert (receivedMsg.getState().getGeneratedMsgs() == currentState.getReceivedMsgs());

        // Transform received message and the ones in the queue
        Operation opr = receivedMsg.getOperation();
        for (Message m : queue) {
            Operation tmp = opr;

            opr = xform.transform(opr, m.getOperation());
            GWT.log(this + "\tTransformed op1 = " + opr);

            Operation op2 = xform.transform(m.getOperation(), tmp);
            GWT.log(this + "\tTransformed op2 = " + op2);

            m.setOperation(op2);
        }
        //apply transformed receivedMsg
        data = opr.execute(data);
        //todo: clone instead of copy constructor
        Message newMsg = new Message(new State(currentState.getGeneratedMsgs(), currentState.getReceivedMsgs()), opr);
		newMsg.setEditingSessionId(receivedMsg.getEditingSessionId());
        currentState.incReceivedMsgs();
        execute(newMsg);
        GWT.log(this.toString());
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
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
        return getClass().getName() + "@" + siteId + "#" + data + ", " + currentState + "#";
    }
}
