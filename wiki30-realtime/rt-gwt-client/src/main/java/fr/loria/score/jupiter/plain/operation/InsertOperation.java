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

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUI(Editor editor) {
        editor.shiftCaret(editor.getOldCaretPos() + 1);

//      editor.insert(position, chr);
        if ('\n' == chr) {
//            editor.onRemoteEnterHighlighting(position);
        } else {
//            editor.insertHighlighting(getSiteId(), position);
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
