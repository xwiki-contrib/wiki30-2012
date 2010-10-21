package fr.loria.score.jupiter.model;

/**
 * No op
 */
public class NoOperation extends Operation {
    public NoOperation(int position) {
        super(position);
    }

    public NoOperation() {
        super(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(String data) {
        return data;
    }

    @Override
    public String toString() {
        return "NoOperation";
    }
}
