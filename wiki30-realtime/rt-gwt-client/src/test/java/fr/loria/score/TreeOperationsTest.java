package fr.loria.score;

import org.junit.Before;
import org.junit.Test;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeNewParagraph;
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

    private static final boolean SPLIT_LEFT = true;
    private static final boolean NO_SPLIT_LEFT = false;
    private static final boolean SPLIT_RIGHT = true;
    private static final boolean NO_SPLIT_RIGHT = false;
    private static final boolean ADD_STYLE = true;
    private static final boolean NO_ADD_STYLE = false;

    private static final int SITE_ID = 0;

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
        
        TreeStyle bold = new TreeStyle(SITE_ID, new int[] {0, 0}, 0, 4, "bold", "true", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        bold.execute(root);

        Tree expectedParagraph = TreeFactory.createParagraphTree();
        Tree expectedSpan = TreeFactory.createElementTree("span");
        expectedSpan.setAttribute("bold", "true");  // todo: make a builder
        expectedSpan.addChild(TreeFactory.createTextTree("abcd"));
        expectedParagraph.addChild(expectedSpan);
        expectedRoot.addChild(expectedParagraph);
        // expectRoot = <p><span bold=true>[abcd]</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);
        
        Tree rootClone = root.deepCloneNode();

        TreeStyle style1 = new TreeStyle(SITE_ID, new int[] {0, 0, 0}, 0, 2, "bold", "true", NO_ADD_STYLE, NO_SPLIT_LEFT, SPLIT_RIGHT);
        style1.execute(root);
        
        // now modify expectedRoot to mirror the change
        expectedSpan.removeChild(0);
        expectedSpan.addChild(TreeFactory.createTextTree("ab"));
        Tree expectedSpan2 = TreeFactory.createElementTree("span");
        expectedSpan2.setAttribute("bold", "true");
        expectedSpan2.addChild(TreeFactory.createTextTree("cd"));
        expectedParagraph.addChild(expectedSpan2);
        // expectedRoot = <p><span bold>[ab]</span><span bold>[cd]</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);
        
        TreeStyle style2 = new TreeStyle(SITE_ID, new int[] {0, 0, 0}, 2, 4, "bold", "true", NO_ADD_STYLE, SPLIT_LEFT, NO_SPLIT_RIGHT);
        style2.execute(rootClone);
        assertEquals("Invalid tree result", root, rootClone);
    }

    @Test
    public void executeSplitParagraphContainingText()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(TreeFactory.createTextTree("abcd"));
        root.addChild(paragraphTree);

        TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 2, new int[] {0, 0});
        insertP.execute(root);
        
        Tree expectedParagraph1 = TreeFactory.createParagraphTree();
        expectedParagraph1.addChild(TreeFactory.createTextTree("ab"));
        expectedRoot.addChild(expectedParagraph1);
        Tree expectedParagraph2 = TreeFactory.createParagraphTree();
        expectedParagraph2.addChild(TreeFactory.createTextTree("cd"));
        expectedRoot.addChild(expectedParagraph2);
        // expectRoot = <p>[ab]</p><p>[cd]</p>
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
        
        TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 1, new int[] {0, 0});
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
        // expectRoot = <p><span bold=true>[ab]</span></p><p><span bold=true>[cd]</span></p>
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

        TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 1, new int[] {0, 0, 0});
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
        // expectRoot = <p><span bold=true>[a]</span></p><p><span bold=true>[b]</span><span bold=true>[cd]</span></p>
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

        TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 2, new int[] {0, 0, 0});
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
        // expectRoot = <p><span bold=true>[ab]</span></p><p><span bold=true>[]</span><span bold=true>[cd]</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void insertParagraphMiddleOfLine()
    {
        Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("ab"));
        root.addChild(paragraph);
        expectedRoot = root.deepCloneNode();

        // Split it at the middle of text
        TreeInsertParagraph paragraphInMiddle = new TreeInsertParagraph(SITE_ID, 1, new int[] {0, 0});
        paragraphInMiddle.execute(root);

        expectedRoot.getChild(0).removeChild(0);
        expectedRoot.getChild(0).addChild(TreeFactory.createTextTree("a"));
        Tree p = TreeFactory.createParagraphTree();
        p.addChild(TreeFactory.createTextTree("b"));
        expectedRoot.addChild(p);
        // expectedRoot = <p>[a]</p><p>[b]</p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }

     @Test
    public void insertParagraphEndOfLine()
    {
        Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("ab"));
        root.addChild(paragraph);
        expectedRoot = root.deepCloneNode();

        // Split it at the end of text
        TreeInsertParagraph paragraphAtEnd = new TreeInsertParagraph(SITE_ID, 2, new int[] {0, 0});
        paragraphAtEnd.execute(root);

        Tree p = TreeFactory.createParagraphTree();
        p.addChild(TreeFactory.createTextTree(""));
        expectedRoot.addChild(p);
        // expectedRoot = <p>[ab]</p><p>[]</p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStylingMiddle()
    {
        Tree p = TreeFactory.createParagraphTree();
        Tree bold = TreeFactory.createElementTree("span");
        bold.setAttribute("bold", "true");
        bold.addChild(TreeFactory.createTextTree("xy"));
        p.addChild(bold);
        root.addChild(p);
        expectedRoot = root.deepCloneNode();

        TreeInsertParagraph insertPMiddle = new TreeInsertParagraph(SITE_ID, 1, new int[] {0, 0, 0});
        insertPMiddle.execute(root);

        Tree text = expectedRoot.getChildFromPath(new int[] {0, 0, 0});
        text.setValue("x");

        Tree p1 = p.deepCloneNode();
        Tree text1 = p1.getChildFromPath(new int[] {0, 0});
        text1.setValue("y");
        expectedRoot.addChild(p1, 1);
        // expectedRoot = <p><span: bold true>[x]</span></p><p><span: bold true>[y]</span></p>
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStylingEnd()
    {
        Tree p = TreeFactory.createParagraphTree();
        Tree bold = TreeFactory.createElementTree("span");
        bold.setAttribute("bold", "true");
        bold.addChild(TreeFactory.createTextTree("ab"));
        p.addChild(bold);
        root.addChild(p);
        expectedRoot = root.deepCloneNode();

        TreeInsertParagraph insertPEnd = new TreeInsertParagraph(SITE_ID, 2, new int[] {0, 0, 0});
        insertPEnd.execute(root);

        Tree p1 = p.deepCloneNode();
        Tree text1 = p1.getChildFromPath(new int[] {0, 0});
        text1.setValue("");
        expectedRoot.addChild(p1, 1);
        // expectedRoot = <p><span: bold true>[ab]</span></p><p><span: bold true>[]</span></p>
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStyleWith2ChildrenAtEndOfFirstChild()
    {
        Tree p = TreeFactory.createParagraphTree();
        Tree bold = TreeFactory.createElementTree("span");
        bold.setAttribute("bold", "true");
        bold.addChild(TreeFactory.createTextTree("ab"));
        bold.addChild(TreeFactory.createTextTree("cd"));
        p.addChild(bold);
        root.addChild(p);

        expectedRoot = root.deepCloneNode();

        // insert p after b
        TreeInsertParagraph insertPAfterB = new TreeInsertParagraph(SITE_ID, 2, new int[] {0, 0, 0});
        insertPAfterB.execute(root);

        expectedRoot.getChildFromPath(new int[] {0, 0}).removeChild(1); // remove text node cd from first span
        Tree p1 = expectedRoot.getChild(0).deepCloneNode();
        Tree span1 = p1.getChild(0);
        span1.getChild(0).setValue("cd");
        span1.addChild(TreeFactory.createTextTree(""), 0);
        expectedRoot.addChild(p1, 1);
        // expectedRoot = <p><span bold true>[ab]</span></p><p><span bold true>[][cd]</span></p>
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStyleWith2ChildrenBeforeSecondChild()
    {
        Tree p = TreeFactory.createParagraphTree();
        Tree bold = TreeFactory.createElementTree("span");
        bold.setAttribute("bold", "true");
        bold.addChild(TreeFactory.createTextTree("ab"));
        bold.addChild(TreeFactory.createTextTree("cd"));
        p.addChild(bold);
        root.addChild(p);

        expectedRoot = root.deepCloneNode();

        // insert p before c
        TreeInsertParagraph insertPBeforeSecondChild = new TreeInsertParagraph(SITE_ID, 0, new int[] {0, 0, 1});
        insertPBeforeSecondChild.execute(root);

        expectedRoot.getChildFromPath(new int[] {0, 0}).removeChild(1); // remove text node cd from first span
        Tree p1 = expectedRoot.getChild(0).deepCloneNode();
        Tree text = p1.getChildFromPath(new int[]{ 0, 0 });
        text.setValue("cd");
        expectedRoot.addChild(p1);
        // expectedRoot = <p><span bold true>[ab]</span></p><p><span bold true>[cd]</span></p>
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStyleWith2ChildrenMiddleOfSecondChild()
    {
        Tree p = TreeFactory.createParagraphTree();
        Tree bold = TreeFactory.createElementTree("span");
        bold.setAttribute("bold", "true");
        bold.addChild(TreeFactory.createTextTree("ab"));
        bold.addChild(TreeFactory.createTextTree("cd"));
        p.addChild(bold);
        root.addChild(p);

        expectedRoot = root.deepCloneNode();

        // insert p between c and d
        TreeInsertParagraph insertPMiddleSecondChild = new TreeInsertParagraph(SITE_ID, 1, new int[] {0, 0, 1});
        insertPMiddleSecondChild.execute(root);

        expectedRoot.getChildFromPath(new int[] {0, 0, 1}).setValue("c"); // replace cd with c
        Tree span1 = TreeFactory.createElementTree("span");
        span1.setAttribute("bold", "true");
        span1.addChild(TreeFactory.createTextTree("d"));
        Tree p1 = TreeFactory.createParagraphTree();
        p1.addChild(span1);
        expectedRoot.addChild(p1, 1);
        // expectedRoot = <p><span bold true>[ab][c]</span></p><p><span bold true>[d]</span></p>
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void newParagraphStartOfLine()
    {
        Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("ab"));
        root.addChild(paragraph);
        expectedRoot = root.deepCloneNode();

        // Split it at the beginning of line
        TreeNewParagraph paragraphAtStart = new TreeNewParagraph(SITE_ID, 0);
        paragraphAtStart.execute(root);

        Tree p1 = TreeFactory.createParagraphTree();
        p1.addChild(TreeFactory.createTextTree(""));
        expectedRoot.addChild(p1, 0);
        // expectedRoot = <p>[]</p><p>[ab]</p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void newParagraphEndOfLine()
    {
        Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("ab"));
        root.addChild(paragraph);
        expectedRoot = root.deepCloneNode();

        // Split it at the beginning of line
        TreeNewParagraph paragraphAtStart = new TreeNewParagraph(SITE_ID, 1);
        paragraphAtStart.execute(root);

        Tree p1 = TreeFactory.createParagraphTree();
        p1.addChild(TreeFactory.createTextTree(""));
        expectedRoot.addChild(p1, 1);
        // expectedRoot = <p>[ab]</p><p>[]</p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }
}
