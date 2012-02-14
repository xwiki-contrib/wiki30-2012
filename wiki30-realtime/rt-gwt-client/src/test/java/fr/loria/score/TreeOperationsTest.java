package fr.loria.score;

import org.junit.Before;
import org.junit.Test;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeStyle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the effect of executing tree operations on the tree model.
 * It should test all Tree API
 *
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeOperationsTest
{
    private Tree root;
    private Tree expectedRoot;

    @Before
    public void init()
    {
        root = TreeFactory.createEmptyTree();
        expectedRoot = TreeFactory.createEmptyTree();
    }

    @Test
    public void testCloneTree()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(TreeFactory.createTextTree("abcd"));
        root.addChild(paragraphTree);

        expectedRoot = root.deepCloneNode();
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void executeStyle()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(TreeFactory.createTextTree("abcd"));
        root.addChild(paragraphTree);
        
        TreeStyle bold = new TreeStyle(0, new int[] {0, 0}, 0, 4, "bold", "true", true, false, false);
        bold.execute(root);

        Tree expectedParagraph = TreeFactory.createParagraphTree();
        Tree expectedSpan = TreeFactory.createElementTree("span");
        expectedSpan.setAttribute("bold", "true");  // todo: make a builder
        expectedSpan.addChild(TreeFactory.createTextTree("abcd"));
        expectedParagraph.addChild(expectedSpan);
        expectedRoot.addChild(expectedParagraph);
        // expectRoot = <p><span bold>abcd</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);

        TreeStyle style1 = new TreeStyle(0, new int[] {0, 0, 0}, 0, 2, "bold", "true", false, false, true);
        style1.execute(root);

        // now modify expectedRoot to mirror the change
        expectedSpan.removeChild(0);
        expectedSpan.addChild(TreeFactory.createTextTree("ab"));

        Tree expectedSpan2 = TreeFactory.createElementTree("span");
        expectedSpan2.setAttribute("bold", "true");
        expectedSpan2.addChild(TreeFactory.createTextTree("cd"));
        expectedParagraph.addChild(expectedSpan2);
        // expectedRoot = <p><span bold>ab</span><span bold>cd</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);

        //TreeStyle style3 = new TreeStyle(0, new int[] {0, 0, 0}, 0, 2, "style", "bold", false, false, false);
        //style3.execute(t);

        // todo fix
        //TreeStyle style2 = new TreeStyle(0, new int[] {0, 0, 0}, 2, 4, "style", "bold", true, true, false);
        //style2.execute(t);
        fail("Fix style");
    }

    @Test
    public void executeInsertParagraph()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(TreeFactory.createTextTree("abcd"));
        root.addChild(paragraphTree);

        TreeStyle bold = new TreeStyle(0, new int[] {0, 0}, 0, 4, "bold", "true", true, false, false);
        bold.execute(root);

        TreeInsertParagraph p = new TreeInsertParagraph(0, 1, new int[] {0, 0});
        p.execute(root);

        fail("Fix the problem in InsertParagraph");
    }
}
