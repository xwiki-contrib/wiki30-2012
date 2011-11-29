package fr.loria.score.jupiter.plain.operation;

import fr.loria.score.client.Editor;

import java.util.logging.Logger;

/**
 * Deletes a character at the given position
 */
public class DeleteOperation extends Operation {
    private transient static final Logger logger = Logger.getLogger(DeleteOperation.class.getName());

    public DeleteOperation() {}

    public DeleteOperation(int siteId, int startPos) {
        super(siteId, startPos);
    }

    @Override
    public void beforeUpdateUI(Editor editor) {
        logger.info("Highlighting: Removing: " + siteId + " at position: " + position);
        editor.prepareUI(position, siteId, true);
    }

    @Override
    public void afterUpdateUI(Editor editor) {
        logger.info("Update UI for Delete operation...");
        if (position < editor.getCaretPosition()) {
            editor.shiftCaret(editor.getOldCaretPos() - 1);
        }
    }

    @Override
    public String toString() {
        return "DeleteOperation(" + position + ")" + super.toString();
    }
    //todo: are they used?
    public Operation handleInsert(InsertOperation op1) {
        return null;
    }

    public Operation handleDelete(DeleteOperation op1) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DeleteOperation) {
            DeleteOperation other = (DeleteOperation) obj;
            return this.siteId == other.siteId && this.position == other.position;
        }
        return false;
    }
}
