package fr.loria.score.jupiter.model;

import fr.loria.score.client.Editor;

/**
 * No op
 */
public class NoOperation extends Operation {
    
    public NoOperation() {
        super();
    }
    
    public NoOperation(int position, int siteId) {
        super(position, siteId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(String data) {
        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUI(Editor editor) {
    }

    @Override
    public String toString() {
        return "NoOperation()" + super.toString();
    }
}
