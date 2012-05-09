package fr.loria.score.jupiter.transform;

import fr.loria.score.jupiter.model.AbstractOperation;

/**
 * The operational transformation that should be applied on divergent copies of document to achieve convergence
 */
public abstract class Transformation {

   public abstract AbstractOperation transform(AbstractOperation op1, AbstractOperation op2);
}
