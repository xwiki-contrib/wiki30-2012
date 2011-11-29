package fr.loria.score.jupiter.model;

import java.io.Serializable;

/**
 * Message is the way in which client and server communicate
 */
public class Message implements Serializable {
    private State state;
    private AbstractOperation operation;
    private int editingSessionId; // TODO: not sure esid is necessary here.

    public Message() {
    }
    
    public Message(State state, AbstractOperation operation, int sessionId) {
        this(state, operation);
        this.editingSessionId = sessionId;
    }

    public Message(State state, AbstractOperation operation) {
        this.state = new State(state);
        this.operation = operation;  // todo: clone operation
    }

    public Message(Message m) {
        this(new State(m.getState()), m.getOperation(), m.getEditingSessionId());
    }

    public AbstractOperation getOperation() {
        return operation;
    }

    public void setOperation(AbstractOperation operation) {
        this.operation = operation;
    }

    public State getState() {
        return state;
    }

    public int getSiteId() {
        return operation.getSiteId();
    }

    public int getEditingSessionId() {
        return editingSessionId;
    }

    public void setEditingSessionId(int editingSessionId) {
        this.editingSessionId = editingSessionId;
    }

    @Override
    public String toString() {
        return "Message: " + state + ", " + operation + ", editingSessionId:" + editingSessionId;
    }
}
