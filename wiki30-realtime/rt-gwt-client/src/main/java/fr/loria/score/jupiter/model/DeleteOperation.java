package fr.loria.score.jupiter.model;

import com.google.gwt.core.client.GWT;
import fr.loria.score.client.Editor;

/**
 * Deletes a character at the given position
 */
public class DeleteOperation extends Operation {

    public DeleteOperation() {
        super();
    }
    
    public DeleteOperation(int startPos, int siteId) {
        super(startPos, siteId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(String data) {
        GWT.log("\tExecuting " + this + " on data = " + data);
        int length = data.length();

        if (position >= length) {
            String errMsg = POSITION_GREATER_THAN_DATA_LENGTH + length;
            GWT.log(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        
        StringBuilder sb = new StringBuilder(data);
        sb.deleteCharAt(position);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUI(Editor editor) {
        editor.shiftCaret(editor.getOldCaretPos() - 1);
    }

    @Override
    public String toString() {
        return "DeleteOperation(" + position + ")" + super.toString();
    }
}
