package fr.loria.score;

import org.junit.Before;
import org.junit.Test;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeStyle;

import static org.junit.Assert.assertEquals;

/**
 * Test the effect of executing tree operations on the tree model.
 * It should test all Tree API
 *
 * @author Bogdan.Flueras@inria.fr
 * @author Gerald.Oster@loria.fr
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

    
    private static final boolean SPLIT_LEFT = true;
    private static final boolean NO_SPLIT_LEFT = false;
    private static final boolean SPLIT_RIGHT = true;
    private static final boolean NO_SPLIT_RIGHT = false;
    private static final boolean ADD_STYLE = true;
    private static final boolean NO_ADD_STYLE = false;
    
    @Test
    public void executeStyle()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(TreeFactory.createTextTree("abcd"));
        root.addChild(paragraphTree);
        
        TreeStyle bold = new TreeStyle(0, new int[] {0, 0}, 0, 4, "bold", "true", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        bold.execute(root);

        Tree expectedParagraph = TreeFactory.createParagraphTree();
        Tree expectedSpan = TreeFactory.createElementTree("span");
        expectedSpan.setAttribute("bold", "true");  // todo: make a builder
        expectedSpan.addChild(TreeFactory.createTextTree("abcd"));
        expectedParagraph.addChild(expectedSpan);
        expectedRoot.addChild(expectedParagraph);
        // expectRoot = <p><span bold=true>abcd</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);
        
        Tree rootClone = root.deepCloneNode();

        TreeStyle style1 = new TreeStyle(0, new int[] {0, 0, 0}, 0, 2, "bold", "true", NO_ADD_STYLE, NO_SPLIT_LEFT, SPLIT_RIGHT);
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
        
        TreeStyle style2 = new TreeStyle(0, new int[] {0, 0, 0}, 2, 4, "bold", "true", NO_ADD_STYLE, SPLIT_LEFT, NO_SPLIT_RIGHT);
        style2.execute(rootClone);
        assertEquals("Invalid tree result", root, rootClone);
    }

    @Test
    public void executeSplitParagraphContainingText()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(TreeFactory.createTextTree("abcd"));
        root.addChild(paragraphTree);

        TreeInsertParagraph insertP = new TreeInsertParagraph(0, 2, new int[] {0, 0});
        insertP.execute(root);
        
        Tree expectedParagraph1 = TreeFactory.createParagraphTree();
        expectedParagraph1.addChild(TreeFactory.createTextTree("ab"));
        expectedRoot.addChild(expectedParagraph1);
        Tree expectedParagraph2 = TreeFactory.createParagraphTree();
        expectedParagraph2.addChild(TreeFactory.createTextTree("cd"));
        expectedRoot.addChild(expectedParagraph2);
        // expectRoot = <p>ab</p><p>cd</p>
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void executeSplitParagraphContainingStyles()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        Tree span1 = TreeFactory.createElementTree("span");
        span1.setAttribute("bold", "true");
        span1.addChild(TreeFactory.createTextTree("ab"));
        paragraphTree.addChild(span1);
        Tree span2 = TreeFactory.createElementTree("span");
        span2.setAttribute("bold", "true");
        span2.addChild(TreeFactory.createTextTree("cd"));
        paragraphTree.addChild(span2);
        root.addChild(paragraphTree);
        
        TreeInsertParagraph insertP = new TreeInsertParagraph(0, 1, new int[] {0, 0});
        insertP.execute(root);
        
        final Tree expectedParagraph1 = TreeFactory.createParagraphTree();
        Tree expectedSpan1 = TreeFactory.createElementTree("span");
        expectedSpan1.setAttribute("bold", "true");
        expectedSpan1.addChild(TreeFactory.createTextTree("ab"));
        expectedParagraph1.addChild(expectedSpan1);
        expectedRoot.addChild(expectedParagraph1);
        
        final Tree expectedParagraph2 = TreeFactory.createParagraphTree();
        Tree expectedSpan2 = TreeFactory.createElementTree("span");
        expectedSpan2.setAttribute("bold", "true");
        expectedSpan2.addChild(TreeFactory.createTextTree("cd"));
        expectedParagraph2.addChild(expectedSpan2);
        expectedRoot.addChild(expectedParagraph2);
        // expectRoot = <p><span bold=true>ab</span></p><p><span bold=true>cd</span></p>
        
        assertEquals("Invalid tree result", expectedRoot, root);        
    }

    @Test
    public void executeSplitParagraphContainingStylesWithSimpleInsertParagraphOperation()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        Tree span1 = TreeFactory.createElementTree("span");
        span1.setAttribute("bold", "true");
        span1.addChild(TreeFactory.createTextTree("ab"));
        paragraphTree.addChild(span1);
        Tree span2 = TreeFactory.createElementTree("span");
        span2.setAttribute("bold", "true");
        span2.addChild(TreeFactory.createTextTree("cd"));
        paragraphTree.addChild(span2);
        root.addChild(paragraphTree);

        TreeInsertParagraph insertP = new TreeInsertParagraph(0, 1, new int[] {0, 0, 0});
        insertP.execute(root);

        final Tree expectedParagraph1 = TreeFactory.createParagraphTree();
        Tree expectedSpan1 = TreeFactory.createElementTree("span");
        expectedSpan1.setAttribute("bold", "true");
        expectedSpan1.addChild(TreeFactory.createTextTree("a"));
        expectedParagraph1.addChild(expectedSpan1);
        expectedRoot.addChild(expectedParagraph1);

        final Tree expectedParagraph2 = TreeFactory.createParagraphTree();
        Tree expectedSpan12 = TreeFactory.createElementTree("span");
        expectedSpan12.setAttribute("bold", "true");
        expectedSpan12.addChild(TreeFactory.createTextTree("b"));
        expectedParagraph2.addChild(expectedSpan12);

        Tree expectedSpan2 = TreeFactory.createElementTree("span");
        expectedSpan2.setAttribute("bold", "true");
        expectedSpan2.addChild(TreeFactory.createTextTree("cd"));
        expectedParagraph2.addChild(expectedSpan2);
        expectedRoot.addChild(expectedParagraph2);
        // expectRoot = <p><span bold=true>a</span></p><p><span bold=true>b</span><span bold=true>cd</span></p>

        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void executeSplitParagraphContainingStylesWithSimpleInsertParagraphOperation1()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        Tree span1 = TreeFactory.createElementTree("span");
        span1.setAttribute("bold", "true");
        span1.addChild(TreeFactory.createTextTree("ab"));
        paragraphTree.addChild(span1);
        Tree span2 = TreeFactory.createElementTree("span");
        span2.setAttribute("bold", "true");
        span2.addChild(TreeFactory.createTextTree("cd"));
        paragraphTree.addChild(span2);
        root.addChild(paragraphTree);

        TreeInsertParagraph insertP = new TreeInsertParagraph(0, 2, new int[] {0, 0, 0});
        insertP.execute(root);

        final Tree expectedParagraph1 = TreeFactory.createParagraphTree();
        Tree expectedSpan1 = TreeFactory.createElementTree("span");
        expectedSpan1.setAttribute("bold", "true");
        expectedSpan1.addChild(TreeFactory.createTextTree("ab"));
        expectedParagraph1.addChild(expectedSpan1);
        expectedRoot.addChild(expectedParagraph1);

        final Tree expectedParagraph2 = TreeFactory.createParagraphTree();
        Tree expectedSpan12 = TreeFactory.createElementTree("span");
        expectedSpan12.setAttribute("bold", "true");
        expectedSpan12.addChild(TreeFactory.createTextTree(""));
        expectedParagraph2.addChild(expectedSpan12);

        Tree expectedSpan2 = TreeFactory.createElementTree("span");
        expectedSpan2.setAttribute("bold", "true");
        expectedSpan2.addChild(TreeFactory.createTextTree("cd"));
        expectedParagraph2.addChild(expectedSpan2);
        expectedRoot.addChild(expectedParagraph2);
        // expectRoot = <p><span bold=true>ab</span></p><p><span bold=true></span><span bold=true>cd</span></p>

        assertEquals("Invalid tree result", expectedRoot, root);
    }
}
