package fr.loria.score;

import org.junit.Before;
import org.junit.Test;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeNewParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;
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

    
    private static Tree createStyledText(String text, String styleName, String styleValue) {
        Tree span = TreeFactory.createElementTree("span");
        span.setAttribute(styleName, styleValue);
        span.addChild(TreeFactory.createTextTree(text));
        return span;
    }
    
    private static int[] path(int... positions) {
        int[] path = positions;
        return path;
    }
    
    
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
        
        final TreeStyle bold = new TreeStyle(SITE_ID, path(0,0), 0, 4, "bold", "true", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        bold.execute(root);

        final Tree expectedParagraph = TreeFactory.createParagraphTree();
        final Tree expectedSpan = createStyledText("abcd", "bold", "true");
        expectedParagraph.addChild(expectedSpan);
        expectedRoot.addChild(expectedParagraph);
        // expectRoot = <p><span bold=true>[abcd]</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);
        
        final Tree rootClone = root.deepCloneNode();

        final TreeStyle style1 = new TreeStyle(SITE_ID, path(0, 0, 0), 0, 2, "bold", "true", NO_ADD_STYLE, NO_SPLIT_LEFT, SPLIT_RIGHT);
        style1.execute(root);
        
        // now modify expectedRoot to mirror the change
        expectedSpan.removeChild(0);
        expectedSpan.addChild(TreeFactory.createTextTree("ab"));
        expectedParagraph.addChild(createStyledText("cd", "bold", "true"));
        // expectedRoot = <p><span bold>[ab]</span><span bold>[cd]</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);
        
        final TreeStyle style2 = new TreeStyle(SITE_ID, path(0, 0, 0), 2, 4, "bold", "true", NO_ADD_STYLE, SPLIT_LEFT, NO_SPLIT_RIGHT);
        style2.execute(rootClone);
        assertEquals("Invalid tree result", root, rootClone);
    }
    
    @Test
    public void executeSplitParagraphContainingText()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(TreeFactory.createTextTree("abcd"));
        root.addChild(paragraphTree);

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 2, path(0, 0));
        insertP.execute(root);
        
        final Tree expectedParagraph1 = TreeFactory.createParagraphTree();
        expectedParagraph1.addChild(TreeFactory.createTextTree("ab"));
        expectedRoot.addChild(expectedParagraph1);
        final Tree expectedParagraph2 = TreeFactory.createParagraphTree();
        expectedParagraph2.addChild(TreeFactory.createTextTree("cd"));
        expectedRoot.addChild(expectedParagraph2);
        // expectRoot = <p>[ab]</p><p>[cd]</p>
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void executeSplitParagraphContainingStyles()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(createStyledText("ab", "bold", "true"));
        paragraphTree.addChild(createStyledText("cd", "bold", "true"));
        root.addChild(paragraphTree);
        
        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        insertP.execute(root);
        
        final Tree expectedParagraph1 = TreeFactory.createParagraphTree();
        expectedParagraph1.addChild(createStyledText("ab", "bold", "true"));
        expectedRoot.addChild(expectedParagraph1);
        
        final Tree expectedParagraph2 = TreeFactory.createParagraphTree();
        expectedParagraph2.addChild(createStyledText("cd", "bold", "true"));
        expectedRoot.addChild(expectedParagraph2);
        // expectRoot = <p><span bold=true>[ab]</span></p><p><span bold=true>[cd]</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void executeSplitParagraphContainingStylesWithSimpleInsertParagraphOperation()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(createStyledText("ab", "bold", "true"));
        paragraphTree.addChild(createStyledText("cd", "bold", "true"));
        root.addChild(paragraphTree);

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 0));
        insertP.execute(root);

        final Tree expectedParagraph1 = TreeFactory.createParagraphTree();
        expectedParagraph1.addChild(createStyledText("a", "bold", "true"));
        expectedRoot.addChild(expectedParagraph1);

        final Tree expectedParagraph2 = TreeFactory.createParagraphTree();
        expectedParagraph2.addChild(createStyledText("b", "bold", "true"));
        expectedParagraph2.addChild(createStyledText("cd", "bold", "true"));
        expectedRoot.addChild(expectedParagraph2);
        // expectRoot = <p><span bold=true>[a]</span></p><p><span bold=true>[b]</span><span bold=true>[cd]</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void executeSplitParagraphContainingStylesWithSimpleInsertParagraphOperation1()
    {
        final Tree paragraphTree = TreeFactory.createParagraphTree();
        paragraphTree.addChild(createStyledText("ab", "bold", "true"));
        paragraphTree.addChild(createStyledText("cd", "bold", "true"));
        root.addChild(paragraphTree);

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertP.execute(root);

        final Tree expectedParagraph1 = TreeFactory.createParagraphTree();
        expectedParagraph1.addChild(createStyledText("ab", "bold", "true"));
        expectedRoot.addChild(expectedParagraph1);

        final Tree expectedParagraph2 = TreeFactory.createParagraphTree();
        expectedParagraph2.addChild(createStyledText("", "bold", "true"));
        expectedParagraph2.addChild(createStyledText("cd", "bold", "true"));
        expectedRoot.addChild(expectedParagraph2);
        // expectRoot = <p><span bold=true>[ab]</span></p><p><span bold=true>[]</span><span bold=true>[cd]</span></p>
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void insertParagraphMiddleOfLine()
    {
        final Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("ab"));
        root.addChild(paragraph);
        expectedRoot = root.deepCloneNode();

        // Split it at the middle of text
        final TreeInsertParagraph paragraphInMiddle = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        paragraphInMiddle.execute(root);

        expectedRoot.getChild(0).removeChild(0);
        expectedRoot.getChild(0).addChild(TreeFactory.createTextTree("a"));
        final Tree p = TreeFactory.createParagraphTree();
        p.addChild(TreeFactory.createTextTree("b"));
        expectedRoot.addChild(p);
        // expectedRoot = <p>[a]</p><p>[b]</p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }

     @Test
    public void insertParagraphEndOfLine()
    {
        final Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("ab"));
        root.addChild(paragraph);
        expectedRoot = root.deepCloneNode();

        // Split it at the end of text
        final TreeInsertParagraph paragraphAtEnd = new TreeInsertParagraph(SITE_ID, 2, path(0, 0));
        paragraphAtEnd.execute(root);

        final Tree p = TreeFactory.createParagraphTree();
        p.addChild(TreeFactory.createTextTree(""));
        expectedRoot.addChild(p);
        // expectedRoot = <p>[ab]</p><p>[]</p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStylingMiddle()
    {
        final Tree p = TreeFactory.createParagraphTree();
        p.addChild(createStyledText("xy", "bold", "true"));
        root.addChild(p);
        expectedRoot = root.deepCloneNode();

        final TreeInsertParagraph insertPMiddle = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 0));
        insertPMiddle.execute(root);

        final Tree text = expectedRoot.getChildFromPath(path(0, 0, 0));
        text.setValue("x");

        final Tree p1 = p.deepCloneNode();
        Tree text1 = p1.getChildFromPath(path(0, 0));
        text1.setValue("y");
        expectedRoot.addChild(p1, 1);
        // expectedRoot = <p><span: bold true>[x]</span></p><p><span: bold true>[y]</span></p>
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStylingEnd()
    {
        final Tree p = TreeFactory.createParagraphTree();
        p.addChild(createStyledText("ab", "bold", "true"));
        root.addChild(p);
        expectedRoot = root.deepCloneNode();

        final TreeInsertParagraph insertPEnd = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertPEnd.execute(root);

        final Tree p1 = p.deepCloneNode();
        final Tree text1 = p1.getChildFromPath(path(0, 0));
        text1.setValue("");
        expectedRoot.addChild(p1, 1);
        // expectedRoot = <p><span: bold true>[ab]</span></p><p><span: bold true>[]</span></p>
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStyleWith2ChildrenAtEndOfFirstChild()
    {
        final Tree p = TreeFactory.createParagraphTree();
        final Tree bold = createStyledText("ab", "bold", "true");
        bold.addChild(TreeFactory.createTextTree("cd"));
        p.addChild(bold);
        root.addChild(p);

        expectedRoot = root.deepCloneNode();

        // insert p after b
        final TreeInsertParagraph insertPAfterB = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertPAfterB.execute(root);

        expectedRoot.getChildFromPath(path(0, 0)).removeChild(1); // remove text node cd from first span
        final Tree p1 = expectedRoot.getChild(0).deepCloneNode();
        final Tree span1 = p1.getChild(0);
        span1.getChild(0).setValue("cd");
        span1.addChild(TreeFactory.createTextTree(""), 0);
        expectedRoot.addChild(p1, 1);
        // expectedRoot = <p><span bold true>[ab]</span></p><p><span bold true>[][cd]</span></p>
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStyleWith2ChildrenBeforeSecondChild()
    {
        final Tree p = TreeFactory.createParagraphTree();
        final Tree bold = createStyledText("ab", "bold", "true");
        bold.addChild(TreeFactory.createTextTree("cd"));
        p.addChild(bold);
        root.addChild(p);

        expectedRoot = root.deepCloneNode();

        // insert p before c
        final TreeInsertParagraph insertPBeforeSecondChild = new TreeInsertParagraph(SITE_ID, 0, path(0, 0, 1));
        insertPBeforeSecondChild.execute(root);

        expectedRoot.getChildFromPath(path(0, 0)).removeChild(1); // remove text node cd from first span
        final Tree p1 = expectedRoot.getChild(0).deepCloneNode();
        final Tree text = p1.getChildFromPath(path(0, 0));
        text.setValue("cd");
        expectedRoot.addChild(p1);
        // expectedRoot = <p><span bold true>[ab]</span></p><p><span bold true>[cd]</span></p>
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStyleWith2ChildrenMiddleOfSecondChild()
    {
        final Tree p = TreeFactory.createParagraphTree();
        final Tree bold = createStyledText("ab", "bold", "true");
        bold.addChild(TreeFactory.createTextTree("cd"));
        p.addChild(bold);
        root.addChild(p);

        expectedRoot = root.deepCloneNode();

        // insert p between c and d
        final TreeInsertParagraph insertPMiddleSecondChild = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 1));
        insertPMiddleSecondChild.execute(root);

        expectedRoot.getChildFromPath(path(0, 0, 1)).setValue("c"); // replace cd with c
        final Tree span1 = createStyledText("d", "bold", "true");
        final Tree p1 = TreeFactory.createParagraphTree();
        p1.addChild(span1);
        expectedRoot.addChild(p1, 1);
        // expectedRoot = <p><span bold true>[ab][c]</span></p><p><span bold true>[d]</span></p>
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void newParagraphStartOfLine()
    {
        final Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("ab"));
        root.addChild(paragraph);
        expectedRoot = root.deepCloneNode();

        // Split it at the beginning of line
        final TreeNewParagraph paragraphAtStart = new TreeNewParagraph(SITE_ID, 0);
        paragraphAtStart.execute(root);

        final Tree p1 = TreeFactory.createParagraphTree();
        p1.addChild(TreeFactory.createTextTree(""));
        expectedRoot.addChild(p1, 0);
        // expectedRoot = <p>[]</p><p>[ab]</p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void newParagraphEndOfLine()
    {
        final Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("ab"));
        root.addChild(paragraph);
        expectedRoot = root.deepCloneNode();

        // Split it at the beginning of line
        final TreeNewParagraph paragraphAtStart = new TreeNewParagraph(SITE_ID, 1);
        paragraphAtStart.execute(root);

        final Tree p1 = TreeFactory.createParagraphTree();
        p1.addChild(TreeFactory.createTextTree(""));
        expectedRoot.addChild(p1, 1);
        // expectedRoot = <p>[ab]</p><p>[]</p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void addSimpleStyle()
    {
        Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("abcd"));
        root.addChild(paragraph);
        // SLeft: false if start == 0, SRight: false if end = text.len
        //TreeStyle(int siteId, int[] path, int start, int end, String param, String value, boolean addStyle, boolean splitLeft, boolean splitRight) {
        TreeStyle styleOperation = new TreeStyle(SITE_ID, path(0, 0), 0, 4, "font-weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        styleOperation.execute(root);

        Tree p1 = TreeFactory.createParagraphTree();
        p1.addChild(createStyledText("abcd", "font-weight", "bold"));
        expectedRoot.addChild(p1);
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void addSimpleStyleOnSubSelection()
    {
        Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("abcd"));
        root.addChild(paragraph);

        TreeStyle styleOperation = new TreeStyle(SITE_ID, path(0, 0), 1, 3, "font-weight", "bold", ADD_STYLE, SPLIT_LEFT, SPLIT_RIGHT);
        styleOperation.execute(root);

        Tree p1 = TreeFactory.createParagraphTree();
        p1.addChild(TreeFactory.createTextTree("a"));
        p1.addChild(createStyledText("bc", "font-weight", "bold"));
        p1.addChild(TreeFactory.createTextTree("d"));
        expectedRoot.addChild(p1);
        //expectedRoot = <p>[a]<span font bold>[bc]</span>[d]</p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void addMultipleStylesOnSameRange()
    {
        Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("abc"));
        root.addChild(paragraph);

        TreeOperation boldOp = new TreeStyle(SITE_ID, path(0, 0), 0, 3, "weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        boldOp.execute(root);

        TreeOperation italicOp = new TreeStyle(SITE_ID, path(0, 0, 0), 0, 1, "style", "italic", NO_ADD_STYLE, NO_SPLIT_LEFT, SPLIT_RIGHT);
        italicOp.execute(root);

        Tree p1 = TreeFactory.createParagraphTree();
        Tree italicBold = TreeFactory.createElementTree("span");
        italicBold.setAttribute("weight", "bold");
        italicBold.setAttribute("style", "italic");
        italicBold.addChild(TreeFactory.createTextTree("a"));
        p1.addChild(italicBold);
        Tree bold = TreeFactory.createElementTree("span");
        bold.setAttribute("weight", "bold");
        bold.addChild(TreeFactory.createTextTree("bc"));
        p1.addChild(bold);
        expectedRoot.addChild(p1);
        //expectedRoot = <p><span weight bold, style italic>[a]</span><span weight bold>[bc]</span></p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }
    /**
     * Applies styles to different selection ranges
     */
    @Test
    public void addMultipleStylesOnDifferentRanges()
    {
        Tree paragraph = TreeFactory.createParagraphTree();
        paragraph.addChild(TreeFactory.createTextTree("abc"));
        root.addChild(paragraph);

        TreeOperation boldOp = new TreeStyle(SITE_ID, path(0, 0), 0, 3, "weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        boldOp.execute(root);

        TreeOperation italicOp = new TreeStyle(SITE_ID, path(0, 0, 0), 1, 3, "style", "italic", NO_ADD_STYLE, SPLIT_LEFT, NO_SPLIT_RIGHT);
        italicOp.execute(root);

        Tree p1 = TreeFactory.createParagraphTree();
        Tree bold = TreeFactory.createElementTree("span");
        bold.setAttribute("weight", "bold");
        bold.addChild(TreeFactory.createTextTree("a"));
        p1.addChild(bold);

        Tree italicBold = TreeFactory.createElementTree("span");
        italicBold.setAttribute("weight", "bold");
        italicBold.setAttribute("style", "italic");
        italicBold.addChild(TreeFactory.createTextTree("bc"));
        p1.addChild(italicBold);

        expectedRoot.addChild(p1);
        //expectedRoot = <p><span weight bold>[a]</span><span weight bold, style italic>[bc]</span></p>
        assertEquals("Invalid tree ", expectedRoot, root);
    }
}
