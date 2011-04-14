package fr.loria.score.jupiter.model;

import java.io.Serializable;

/**
 * Message is the way in which client and server communicate
 */
public class Message implements Serializable {
    private State state;
    private Operation operation;
    private int editingSessionId; // TODO: not sure esid is necessary here.

    public Message() {
    }
    
    public Message(State state, Operation operation, int sesssionId) {
        this(state, operation);
        this.editingSessionId = sesssionId;
    }

    public Message(State state, Operation operation) {
        this.state = state;
        this.operation = operation;
    }

    public Message(Message m) {
        this(new State(m.getState()), m.getOperation());
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
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
