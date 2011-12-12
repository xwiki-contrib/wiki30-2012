package fr.loria.score.jupiter.transform;

import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.plain.PlainDocument;
import fr.loria.score.jupiter.tree.TreeDocument;

/**
 * Abstract Factory for transformations creation
 */

public class TransformationFactory {
    public static Transformation createResselTransformation() {
        return new ResselTransformation();
    }

    public static Transformation createTreeTransformation() {
        return new TreeTransformation();
    }

    /**
     * @param doc the document upon to act
     * @return a transformation based on the document type
     */
    public static Transformation createTransformation(Document doc) {
        if (doc instanceof PlainDocument) {
            return createResselTransformation();
        } else if (doc instanceof TreeDocument) {
            return createTreeTransformation();
        }
        return null;
    }
}