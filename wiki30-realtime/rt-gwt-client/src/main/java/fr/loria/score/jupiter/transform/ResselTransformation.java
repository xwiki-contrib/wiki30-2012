package fr.loria.score.jupiter.transform;

import fr.loria.score.jupiter.model.DeleteOperation;
import fr.loria.score.jupiter.model.NoOperation;
import fr.loria.score.jupiter.model.InsertOperation;
import fr.loria.score.jupiter.model.Operation;

/**
 * Ressel transformation implementation
 */
// TODO: it is not mandatory to create new operation, shifting parameters is enough
public class ResselTransformation extends Transformation {

    @Override
    protected Operation handleInsertInsert(Operation m1, Operation m2) {
        int p1 = m1.getPosition();
        int p2 = m2.getPosition();

        int siteId1 = m1.getSiteId();
        int siteId2 = m2.getSiteId();

        InsertOperation i1 = (InsertOperation) m1;

        if ((p1 < p2) || ((p1 == p2) && (siteId1 < siteId2))) {
            return new InsertOperation(p1, i1.getChr(), siteId1);
        } else {
            return new InsertOperation(p1 + 1, i1.getChr(), siteId1);
        }
    }

    @Override
    protected Operation handleInsertDelete(Operation m1, Operation m2) {
        InsertOperation i1 = (InsertOperation) m1;

        int p1 = m1.getPosition();
        int p2 = m2.getPosition();
        int siteId1 = m1.getSiteId();

        if (p1 <= p2) {
            return new InsertOperation(p1, i1.getChr(), siteId1);
        } else {
            return new InsertOperation(p1 - 1, i1.getChr(), siteId1);
        }
    }

    @Override
    protected Operation handleDeleteInsert(Operation m1, Operation m2) {
        int p1 = m1.getPosition();
        int p2 = m2.getPosition();
        int siteId1 = m1.getSiteId();

        if (p1 < p2) {
            return new DeleteOperation(p1, siteId1);
        } else {
            return new DeleteOperation(p1 + 1, siteId1);
        }
    }

    @Override
    protected Operation handleDeleteDelete(Operation m1, Operation m2) {
        int p1 = m1.getPosition();
        int p2 = m2.getPosition();
        int siteId1 = m1.getSiteId();

        if (p1 < p2) {
            return new DeleteOperation(p1, siteId1);
        } else if (p1 > p2) {
            return new DeleteOperation(p1 - 1, siteId1);
        } else {
            return new NoOperation(p1, siteId1);
        }
    }
}
