package fr.loria.score.jupiter.model;

import java.io.Serializable;

/**
 * Message is the way in which client and server communicate
 */
public class Message implements Serializable {
    private State state;
    private Operation operation;

    public Message(State state, Operation operation) {
        this.state = state;
        this.operation = operation;
    }

    public Message(Message m) {
        this.state = new State(m.getState());
        this.operation = m.getOperation();
    }

    public Message() {
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

    @Override
    public String toString() {
        return "Message: " + state + ", " + operation;
    }
}
