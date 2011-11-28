package fr.loria.score.jupiter.transform;


import fr.loria.score.jupiter.model.AbstractOperation;

import java.util.logging.Logger;

/**
 * Transformation for concurrent operations on a tree like model
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeTransformation extends Transformation {
    private static final Logger log = Logger.getLogger(TreeTransformation.class.getName());

    @Override
    public AbstractOperation transform(AbstractOperation op1, AbstractOperation op2) {
        log.fine("Transforming op1:" + op1 +" with respect to op2: " + op2);
        AbstractOperation result = op2.transform(op1);
        return result;
    }
}
