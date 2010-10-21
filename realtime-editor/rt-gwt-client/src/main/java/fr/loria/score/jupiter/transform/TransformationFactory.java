package fr.loria.score.jupiter.transform;

/**
 * User: bogdan
 * Date: Aug 31, 2010
 * Time: 4:58:47 PM
 */

public class TransformationFactory {
    public static Transformation createResselTransformation() {
        return new ResselTransformation();
    }

    public static Transformation createSunTransformation() {
        return new SunTransformation();
    }
}