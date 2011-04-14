package fr.loria.score.jupiter.transform;

/**
 * Abstract Factory for transformations creation
 */

public class TransformationFactory {
    public static Transformation createResselTransformation() {
        return new ResselTransformation();
    }
}