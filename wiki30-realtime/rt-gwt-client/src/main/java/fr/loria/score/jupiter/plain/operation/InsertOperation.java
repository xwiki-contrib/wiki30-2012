package fr.loria.score.jupiter.plain.operation;

import fr.loria.score.client.Editor;

import java.util.logging.Logger;

/**
 * Inserts a char at a given position
 */
public class InsertOperation extends Operation {
    private transient static final Logger logger = Logger.getLogger(InsertOperation.class.getName());

    // the char to be inserted
    private char chr;

    public InsertOperation() {}

    public InsertOperation(int siteId, int position, char c) {
        super(siteId, position);
        this.chr = c;
    }

    @Override
    public void beforeUpdateUI(Editor editor) {
        logger.info("Highlighting: Inserting: " + siteId + " at position: " + position);
        editor.prepareUI(position, siteId, false);
    }

    @Override
    public void afterUpdateUI(Editor editor) {
        if (position < editor.getCaretPosition()) {
            editor.shiftCaret(editor.getOldCaretPos() + 1);
        }
    }

    public char getChr() {
        return chr;
    }

    @Override
    public String toString() {
        return "InsertOperation(" + position + ", " + chr + ")" + super.toString();
    }
}
