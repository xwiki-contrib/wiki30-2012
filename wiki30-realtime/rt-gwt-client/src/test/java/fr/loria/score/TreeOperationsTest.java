package fr.loria.score;

import org.junit.Before;
import org.junit.Test;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeStyle;

import static org.junit.Assert.fail;

/**
 * Test the effect of executing tree operations on the tree model
 *
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeOperationsTest
{
    private Tree root;

    @Before
    public void init()
    {
        root = TreeFactory.createEmptyTree();
    }

    @Test
    public void executeStyle()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(TreeFactory.createTextTree("abcd"));
        root.addChild(paragraphTree);

        TreeStyle bold = new TreeStyle(0, new int[] {0, 0}, 0, 4, "bold", "true", true, false, false);
        bold.execute(root);

        TreeStyle style1 = new TreeStyle(0, new int[] {0, 0, 0}, 0, 2, "bold", "true", false, false, true);
        style1.execute(root);

        //TreeStyle style3 = new TreeStyle(0, new int[] {0, 0, 0}, 0, 2, "style", "bold", false, false, false);
        //style3.execute(t);

        // todo fix
        //TreeStyle style2 = new TreeStyle(0, new int[] {0, 0, 0}, 2, 4, "style", "bold", true, true, false);
        //style2.execute(t);
        //todo: define equals on trees
        System.out.println("t = " + root);
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

        TreeStyle style1 = new TreeStyle(0, new int[] {0, 0, 0}, 0, 2, "bold", "true", false, false, true);
        style1.execute(root);

        TreeInsertParagraph p = new TreeInsertParagraph(0, 1, new int[] {0, 0});
        p.execute(root);

        fail("Fix the problem in InsertParagraph");
    }
}
