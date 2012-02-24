package fr.loria.score;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite class for client
 *
 * @author Bogdan.Flueras@inria.fr
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    TreeCompositeOperationTest.class,
    TreeInsertParagraphTest.class,
    TreeNewParagraphTest.class,
    TreeStyleTest.class
})
public class ClientTestsSuite
{
}
