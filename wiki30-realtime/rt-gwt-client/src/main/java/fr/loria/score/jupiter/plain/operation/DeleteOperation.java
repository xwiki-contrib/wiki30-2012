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

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUI(Editor editor) {
        editor.shiftCaret(editor.getOldCaretPos() - 1);
//        else  {
//            if (editor.getOldCaretPos() <= endPos) {
//               editor.shiftCaret(position);
//            } else {
//                editor.shiftCaret(editor.getOldCaretPos() - (endPos - position));
//            }
//        }
//        editor.removeHighlighting(operation.getSiteId(), position);
    }

    @Override
    public String toString() {
        return "DeleteOperation(" + position + ")" + super.toString();
    }

    public Operation handleInsert(InsertOperation op1) {
        return null;  //Todo
    }

    public Operation handleDelete(DeleteOperation op1) {
        return null;  //Todo
    }
}
