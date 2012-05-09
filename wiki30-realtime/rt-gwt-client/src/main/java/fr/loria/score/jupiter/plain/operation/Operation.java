package fr.loria.score.jupiter.plain.operation;

import fr.loria.score.client.Editor;
import fr.loria.score.jupiter.model.AbstractOperation;

/**
 * The operation that has to be executed on the server or client side.
 * Currently it is only an insert/delete character/string for a linear model
 */
public abstract class Operation extends AbstractOperation {

    protected Operation() {}

    public Operation(AbstractOperation o) {
        super(o);
    }

    protected Operation(int siteId, int position) {
        super(siteId, position);
    }

    /**
     * Prepares the UI (the editor) <strong>before</strong> this operation is executed
     * according to the semantics of <code>this</code> operation.<br/>
     * Eg: Update the telepointers which have to be modified before applying the operation.
     * @param editor the editor
     */
    public abstract void beforeUpdateUI(Editor editor);

    /**
     * Updates the UI(the editor) <strong>after</strong> this operation has been executed,
     * according to the semantics of <code>this</code> operation.<br/>
     * <strong>Nb:</strong>This could be embedded in <code>execute()</code> method, but I choose to follow the OC principle.
     * @param editor the editor
     */
    public abstract void afterUpdateUI(Editor editor);

    public String toString() {
        return " siteId: " + siteId;
    }

    @Override
    public AbstractOperation transform(AbstractOperation op1) {
        return new NoOperation();
    }
}
