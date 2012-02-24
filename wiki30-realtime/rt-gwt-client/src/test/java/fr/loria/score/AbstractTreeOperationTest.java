package fr.loria.score;

import org.junit.Before;
import org.junit.Test;

import fr.loria.score.jupiter.tree.TreeFactory;

import static fr.loria.score.TreeDSL.paragraph;
import static fr.loria.score.TreeDSL.text;
import static org.junit.Assert.assertEquals;

/**
 * Test the effect of executing tree operations on the tree model. It should test all Tree API
 *
 * @author Bogdan.Flueras@inria.fr
 * @author Gerald.Oster@loria.fr
 */
public abstract class AbstractTreeOperationTest
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

    @Test
    public void testCloneTree()
    {
        rootDSL.addChild(paragraph().addChild(text("abcd")));
        expectedRootDSL.addChild(paragraph().addChild(text("abcd")));

        assertEquals("Invalid tree result", expectedRootDSL.getTree(), rootDSL.getTree().deepCloneNode());
    }
}
