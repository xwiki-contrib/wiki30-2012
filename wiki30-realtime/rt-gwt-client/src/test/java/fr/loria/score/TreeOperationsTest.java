package fr.loria.score;

import org.junit.Test;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeStyle;

/**
 * Test the effect of executing tree operations on the tree model
 *
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeOperationsTest
{
    @Test
    public void executeStyle()
    {
        Tree t = TreeFactory.createEmptyTree();

        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(TreeFactory.createTextTree("abcd"));
        t.addChild(paragraphTree);

        TreeStyle bold = new TreeStyle(0, new int[] {0, 0}, 0, 4, "bold", "true", true, false, false);
        bold.execute(t);

        TreeStyle style1 = new TreeStyle(0, new int[] {0, 0, 0}, 0, 2, "bold", "true", false, false, true);
        style1.execute(t);

        TreeInsertParagraph p = new TreeInsertParagraph(0, 1, new int[] {0, 0});
        p.execute(t);

        //TreeStyle style3 = new TreeStyle(0, new int[] {0, 0, 0}, 0, 2, "style", "bold", false, false, false);
        //style3.execute(t);


        // to fix
        //TreeStyle style2 = new TreeStyle(0, new int[] {0, 0, 0}, 2, 4, "style", "bold", true, true, false);
        //style2.execute(t);
        System.out.println("t = " + t);
    }
}
