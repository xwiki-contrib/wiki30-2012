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
     * Updates the UI(the editor in fact) according to the semantics of the operation (<code>this</code>).<br/>
     * <strong>Nb:</strong>This could be embedded in <code>execute()</code> method, but I choose to follow the OC principle.
     */
    public abstract void updateUI(Editor editor);

    public String toString() {
        return " siteId: " + siteId;
    }

    @Override
    public AbstractOperation transform(AbstractOperation op1) {
//        if (op1 instanceof InsertOperation) {
//            //todo: refactor this too   .. delegate to operation
//            // op2 is this
////            if (this instanceof InsertOperation) {
//                return handleInsert((InsertOperation)op1); // 1 insert, 2 insert
////            } else if (this instanceof DeleteOperation) {
////                return handleInsert((InsertOperation)op1); // 1 insert, 2 delete
////            }
////
// //op2 is NoOp
////                return op1;       // 1 insert, 2 NoOp
////            }
//
//        } else if (op1 instanceof DeleteOperation) {
//            DeleteOperation deleteOp1 = (DeleteOperation) op1;
////            if (op2 instanceof InsertOperation) { // 1 delete 2 insert
//                return handleDelete(deleteOp1);
////            } else if (op2 instanceof DeleteOperation) { // 1 delete 2 delete
////                return handleDelete(deleteOp1);
//            } else { //op2  is NoOp
//                return op1;
//            }
//        } else {
//            // op1 is NoOp
//            return op1;
//        }
        return null;
    }

//    public abstract Operation handleInsert(InsertOperation op1);
//    public abstract Operation handleDelete(DeleteOperation op1);
    // handleNoOp
}
