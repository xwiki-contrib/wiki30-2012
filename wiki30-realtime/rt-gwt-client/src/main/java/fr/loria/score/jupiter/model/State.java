package fr.loria.score.jupiter.model;

import java.io.Serializable;

/**
 * Models the current state of the client or the server. This is an indicator of how many messages were locally generated and how many were received.
 * Basically it is a vector clock of two entries.
 * <ul>
 * <li>generatedMsgs - the nr of generated messages by the owner entity - could be client or server</li>
 * <li>receivedMsgs - the nr of received and processed messages by the owner entity - could be client or server.</li>
 * </ul>
 */
public class State implements Serializable {
    private int generatedMsgs = 0;
    private int receivedMsgs = 0;

    public State() {
    }

    public State(State s) {
        this(s.generatedMsgs, s.receivedMsgs);
    }

    public State(int myMsgs, int receivedMsgs) {
        this.generatedMsgs = myMsgs;
        this.receivedMsgs = receivedMsgs;
    }

    public int getGeneratedMsgs() {
        return generatedMsgs;
    }

    public void incGeneratedMsgs() {
        this.generatedMsgs++;
    }

    public int getReceivedMsgs() {
        return receivedMsgs;
    }

    public void incReceivedMsgs() {
        this.receivedMsgs++;
    }

    @Override
    public String toString() {
        return "State(" + generatedMsgs + ", " + receivedMsgs + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof State)){
            return false;
        }
        State other = (State)o;
        return other.generatedMsgs == this.generatedMsgs && other.receivedMsgs == this.receivedMsgs;
    }
}
