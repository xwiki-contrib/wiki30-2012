package fr.loria.score.jupiter.transform;

import fr.loria.score.jupiter.model.DeleteOperation;
import fr.loria.score.jupiter.model.NoOperation;
import fr.loria.score.jupiter.model.InsertOperation;
import fr.loria.score.jupiter.model.Operation;

/**
 * Sun transformation implementation
 */
public class SunTransformation extends Transformation {

    @Override
    protected Operation handleInsertInsert(Operation m1, Operation m2) {
        int p1 = m1.getPosition();
        int p2 = m2.getPosition();

        InsertOperation i1 = (InsertOperation) m1;
        char c1 = i1.getCharacter();

        if (p1 < p2) {
            return new InsertOperation(p1, c1);
        } else {
            return new InsertOperation(p1 + 1, c1);
        }
    }

    @Override
    protected Operation handleInsertDelete(Operation m1, Operation m2) {
        int p1 = m1.getPosition();
        int p2 = m2.getPosition();

        InsertOperation i1 = (InsertOperation) m1;
        char c1 = i1.getCharacter();

        if (p1 <= p2) {
            return new InsertOperation(p1, c1);
        } else {
            return new InsertOperation(p1 - 1, c1);
        }
    }

    @Override
    protected Operation handleDeleteInsert(Operation m1, Operation m2) {
        int p1 = m1.getPosition();
        int p2 = m2.getPosition();

        if (p1 < p2) {
            return new DeleteOperation(p1);
        } else {
            return new DeleteOperation(p1 + 1);
        }
    }

    @Override
    protected Operation handleDeleteDelete(Operation m1, Operation m2) {
        int p1 = m1.getPosition();
        int p2 = m2.getPosition();

        if (p1 < p2) {
            return new DeleteOperation(p1);
        } else if (p1 > p2) {
            return new DeleteOperation(p1 - 1);
        } else {
            return new NoOperation();
        }
    }
}
