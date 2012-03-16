package fr.loria.score;

import org.junit.Before;

import fr.loria.score.jupiter.tree.TreeFactory;

/**
 * Test the effect of executing tree operations on the tree model. It should test all Tree API
 *
 * @author Bogdan.Flueras@inria.fr
 * @author Gerald.Oster@loria.fr
 */
public class AbstractTreeOperationTest
{
    // TODO: naming convention
    protected TreeDSL rootDSL;

    protected TreeDSL expectedRootDSL;

    protected static final boolean SPLIT_LEFT = true;

    protected static final boolean NO_SPLIT_LEFT = false;

    protected static final boolean SPLIT_RIGHT = true;

    protected static final boolean NO_SPLIT_RIGHT = false;

    protected static final boolean ADD_STYLE = true;

    protected static final boolean NO_ADD_STYLE = false;

    protected static final int SITE_ID = 0;

    @Before
    public void init()
    {
        rootDSL = new TreeDSL(TreeFactory.createEmptyTree());
        expectedRootDSL = new TreeDSL(TreeFactory.createEmptyTree());
    }

    public static int[] path(int... positions)
    {
        int[] path = positions;
        return path;
    }
}
