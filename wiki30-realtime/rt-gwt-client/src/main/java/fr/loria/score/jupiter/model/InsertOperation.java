package fr.loria.score.jupiter.model;

import com.google.gwt.core.client.GWT;
import fr.loria.score.client.Editor;

/**
 * Inserts a char at a given position
 */
public class InsertOperation extends Operation {

    // the char to be inserted
    private char chr;

    public InsertOperation() {
        super();
    }
    
    public InsertOperation(int position, char c, int siteId) {
        super(position, siteId);
        this.chr = c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(String data) {
        GWT.log("\tExecuting " + this + " on data = " + data);
        int length = data.length();

        if (position > length) {
            String errMsg = POSITION_GREATER_THAN_DATA_LENGTH + length;
            GWT.log(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        StringBuilder sb = new StringBuilder(data);
        sb.insert(position, chr);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUI(Editor editor) {
        editor.shiftCaret(editor.getOldCaretPos() + 1);
    }

    public char getChr() {
        return chr;
    }

    @Override
    public String toString() {
        return "InsertOperation(" + position + ", " + chr + ")" + super.toString();
    }
}
