package fr.loria.score.jupiter.plain.operation;

import fr.loria.score.client.Editor;

/**
 * No op
 */
public class NoOperation extends Operation {

    public NoOperation(int siteId, int position) {
        super(siteId, position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUI(Editor editor) {}

    @Override
    public String toString() {
        return "NoOperation()" + super.toString();
    }
}
