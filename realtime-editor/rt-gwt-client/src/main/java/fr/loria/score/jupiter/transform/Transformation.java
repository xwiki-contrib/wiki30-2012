package fr.loria.score.jupiter.transform;

import fr.loria.score.jupiter.model.DeleteOperation;
import fr.loria.score.jupiter.model.NoOperation;
import fr.loria.score.jupiter.model.InsertOperation;
import fr.loria.score.jupiter.model.Operation;

/**
 * The operational transformation that should be applied on divergent copies of generic data types (normally <em>String</em>s) to achieve convergence
 */
public abstract class Transformation {

    public Operation transform(Operation m1, Operation m2) {
        if (m1 instanceof InsertOperation) {
            if (m2 instanceof InsertOperation) {
                return handleInsertInsert(m1, m2);
            } else if (m2 instanceof DeleteOperation) {
                return handleInsertDelete(m1, m2);
            } else { //is NoOp
                return m1;
            }

        } else if (m1 instanceof DeleteOperation) {
            if (m2 instanceof InsertOperation) {
                return handleDeleteInsert(m1, m2);
            } else if (m2 instanceof DeleteOperation) {
                return handleDeleteDelete(m1, m2);
            } else { // is NoOp
                return m1;    
            }
        }
        //NoOp
        return new NoOperation(0);
    }

    protected abstract Operation handleInsertInsert(Operation m1, Operation m2);

    protected abstract Operation handleInsertDelete(Operation m1, Operation m2);

    protected abstract Operation handleDeleteInsert(Operation m1, Operation m2);

    protected abstract Operation handleDeleteDelete(Operation m1, Operation m2);
}
