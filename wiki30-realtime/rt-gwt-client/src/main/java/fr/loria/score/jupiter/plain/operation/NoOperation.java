package fr.loria.score.jupiter.plain.operation;

import fr.loria.score.client.Editor;

/**
 * No op
 */
public class NoOperation extends Operation {

    public NoOperation(){}

    public NoOperation(int siteId, int position) {
        super(siteId, position);
    }

    @Override
    public void beforeUpdateUI(Editor editor) {}

    @Override
    public void afterUpdateUI(Editor editor) {}

    @Override
    public String toString() {
        return "NoOperation()" + super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NoOperation;
    }
}
