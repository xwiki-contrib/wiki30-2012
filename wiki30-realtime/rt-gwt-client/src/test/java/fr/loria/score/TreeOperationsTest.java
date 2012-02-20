package fr.loria.score;

import org.junit.Before;
import org.junit.Test;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.operation.*;

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
    
    private static int[] path(int... positions) {
        int[] path = positions;
        return path;
    }
    
    
    @Before
    public void init()
    {
        root = TreeFactory.createEmptyTree();
        rootDSL = new TreeDSL(root);
        expectedRoot = TreeFactory.createEmptyTree();
        expectedRootDSL = new TreeDSL(expectedRoot);
    }

    @Test
    public void testCloneTree()
    {        
        rootDSL.addChild(paragraph().addChild(text("abcd")));
        root = root.deepCloneNode();
        
        expectedRootDSL.addChild(paragraph().addChild(text("abcd")));
        
        assertEquals("Invalid tree result", expectedRoot, root);
    }
    
    @Test
    public void executeStyle()
    {
        rootDSL.addChild(paragraph().addChild(text("abcd")));
              
        final TreeStyle bold = new TreeStyle(SITE_ID, path(0,0), 0, 4, "font-weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        bold.execute(root);
   
        // expectRoot = <p><span font-weight=bold>[abcd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("abcd"))));
               
        assertEquals("Invalid tree result", expectedRoot, root);
        
        final TreeStyle style1 = new TreeStyle(SITE_ID, path(0, 0, 0), 0, 2, "font-weight", "bold", NO_ADD_STYLE, NO_SPLIT_LEFT, SPLIT_RIGHT);
        final Tree rootClone = root.deepCloneNode();
        style1.execute(root);
                
        // expectedRoot = <p><span font-weight=bold>[ab]</span><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.clear();
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
                                                      span("font-weight", "bold").addChild(text("cd"))));
        
        assertEquals("Invalid tree result", expectedRoot, root);
        
        final TreeStyle style2 = new TreeStyle(SITE_ID, path(0, 0, 0), 2, 4, "font-weight", "bold", NO_ADD_STYLE, SPLIT_LEFT, NO_SPLIT_RIGHT);
        style2.execute(rootClone);
        
        assertEquals("Invalid tree result", root, rootClone);
    }
    
    @Test
    public void executeSplitParagraphContainingText()
    {
        rootDSL.addChild(paragraph().addChild(text("abcd")));

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 2, path(0, 0));
        insertP.execute(root);
                
        // expectRoot = <p>[ab]</p><p>[cd]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("ab")),
                                 paragraph().addChild(text("cd")));
        
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void executeSplitParagraphContainingStyles()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
                                              span("font-weight", "bold").addChild(text("cd"))));
        
        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        insertP.execute(root);
        
        // expectRoot = <p><span font-weight=bold>[ab]</span></p><p><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("cd"))));
        
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void executeSplitParagraphContainingStylesWithSimpleInsertParagraphOperation()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
                                              span("font-weight", "bold").addChild(text("cd"))));
 
        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 0));
        insertP.execute(root);
        
        // expectRoot = <p><span font-weight=bold>[a]</span></p><p><span font-weight=bold>[b]</span><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("a"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("b")),
                                                      span("font-weight", "bold").addChild(text("cd"))));        
 
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void executeSplitParagraphContainingStylesWithSimpleInsertParagraphOperation1()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
                                              span("font-weight", "bold").addChild(text("cd"))));

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertP.execute(root);
       
        // expectRoot = <p><span font-weight=bold>[ab]</span></p><p><span font-weight=bold></span><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
                                 paragraph().addChild(span("font-weight", "bold"),
                                                      span("font-weight", "bold").addChild(text("cd"))));
        
        assertEquals("Invalid tree result", expectedRoot, root);
    }

    @Test
    public void insertParagraphMiddleOfLine()
    {        
        rootDSL.addChild(paragraph().addChild(text("ab")));
        
        // Split it at the middle of text
        final TreeInsertParagraph paragraphInMiddle = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        paragraphInMiddle.execute(root);

        // expectedRoot = <p>[a]</p><p>[b]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("a")),
                                 paragraph().addChild(text("b")));
        
        assertEquals("Invalid tree ", expectedRoot, root);
    }
    
    @Test
    public void insertParagraphMiddleOfLineMultipleText()
    {
        rootDSL.addChild(paragraph().addChild(text("a"),
                                              text("b")));
        
        // Split it at the end of text
        final TreeInsertParagraph paragraphAtEnd = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        paragraphAtEnd.execute(root);

        // expectedRoot = <p>[a]</p><p>[b]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("a")),
                                 paragraph().addChild(text("b")));
        
        assertEquals("Invalid tree ", expectedRoot, root);
    }
    

    @Test
    public void insertParagraphEndOfLine()
    {
        rootDSL.addChild(paragraph().addChild(text("ab")));
        
        // Split it at the end of text
        final TreeInsertParagraph paragraphAtEnd = new TreeInsertParagraph(SITE_ID, 2, path(0, 0));
        paragraphAtEnd.execute(root);

        // expectedRoot = <p>[ab]</p><p></p>
        expectedRootDSL.addChild(paragraph().addChild(text("ab")),
                                 paragraph());
        
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStylingMiddle()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("xy"))));

        final TreeInsertParagraph insertPMiddle = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 0));
        insertPMiddle.execute(root);

        // expectedRoot = <p><span font-weight=bold>[x]</span></p><p><span font-weight=bold>[y]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("x"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("y"))));
        
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStylingEnd()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))));

        final TreeInsertParagraph insertPEnd = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertPEnd.execute(root);
     
        // expectedRoot = <p><span font-weight=bold>[ab]</span></p><p><span font-weight=bold></span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
                                 paragraph().addChild(span("font-weight", "bold")));
        
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStyleWith2ChildrenAtEndOfFirstChild()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
                                                                                   text("cd"))));   
        // insert p after b
        final TreeInsertParagraph insertPAfterB = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertPAfterB.execute(root);

        // expectedRoot = <p><span font-weight=bold>[ab]</span></p><p><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("cd"))));
        
        assertEquals("Invalid result ", expectedRoot, root);
    }
    
    @Test
    public void insertParagraphStyleWith2ChildrenBeforeSecondChild()
    {       
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
                                                                                   text("cd"))));

        // insert p before c
        final TreeInsertParagraph insertPBeforeSecondChild = new TreeInsertParagraph(SITE_ID, 0, path(0, 0, 1));
        insertPBeforeSecondChild.execute(root);


        // expectedRoot = <p><span font-weight=bold>[ab]</span></p><p><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void insertParagraphStyleWith2ChildrenMiddleOfSecondChild()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
                                                                                   text("cd"))));

        // insert p between c and d
        final TreeInsertParagraph insertPMiddleSecondChild = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 1));
        insertPMiddleSecondChild.execute(root);
        
        // expectedRoot = <p><span font-weight=bold>[ab][c]</span></p><p><span font-weight=bold>[d]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
                                                                                           text("c"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("d"))));
        
        
        assertEquals("Invalid result ", expectedRoot, root);
    }

    @Test
    public void newParagraphStartOfLine()
    {
        rootDSL.addChild(paragraph().addChild(text("ab")));
        
        // Split it at the beginning of line
        final TreeNewParagraph paragraphAtStart = new TreeNewParagraph(SITE_ID, 0);
        paragraphAtStart.execute(root);

        // expectedRoot = <p>[]</p><p>[ab]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("")),
                                 paragraph().addChild(text("ab")));

        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void newParagraphEndOfLine()
    {
        rootDSL.addChild(paragraph().addChild(text("ab")));

        // Split it at the beginning of line
        final TreeNewParagraph paragraphAtStart = new TreeNewParagraph(SITE_ID, 1);
        paragraphAtStart.execute(root);
        
        // expectedRoot = <p>[ab]</p><p>[]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("ab")),
                                 paragraph().addChild(text("")));        
        
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void addSimpleStyle()
    {
        rootDSL.addChild(paragraph().addChild(text("abcd")));
                
        final TreeStyle styleOperation = new TreeStyle(SITE_ID, path(0, 0), 0, 4, "font-weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        styleOperation.execute(root);

        // expectedRoot = <p><span font-weight="bold">[abcd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("abcd"))));
        
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void addSimpleStyleOnSubSelection()
    {
        rootDSL.addChild(paragraph().addChild(text("abcd")));

        final TreeStyle styleOperation = new TreeStyle(SITE_ID, path(0, 0), 1, 3, "font-weight", "bold", ADD_STYLE, SPLIT_LEFT, SPLIT_RIGHT);
        styleOperation.execute(root);

        //expectedRoot = <p>[a]<span font-weight=bold>[bc]</span>[d]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("a"),
                                                      span("font-weight", "bold").addChild(text("bc")),
                                                      text("d")));
        
        assertEquals("Invalid tree ", expectedRoot, root);
    }

    @Test
    public void addMultipleStylesOnSameRange()
    {
        rootDSL.addChild(paragraph().addChild(text("abc")));

        final TreeOperation boldOp = new TreeStyle(SITE_ID, path(0, 0), 0, 3, "font-weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        boldOp.execute(root);

        final TreeOperation italicOp = new TreeStyle(SITE_ID, path(0, 0, 0), 0, 1, "font-style", "italic", NO_ADD_STYLE, NO_SPLIT_LEFT, SPLIT_RIGHT);
        italicOp.execute(root);

        //expectedRoot = <p><span font-weight=bold, font-style=italic>[a]</span><span font-weight=bold>[bc]</span></p>        
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").setAttribute("font-style", "italic").addChild(text("a")),
                                                      span("font-weight", "bold").addChild(text("bc"))));
        
        assertEquals("Invalid tree ", expectedRoot, root);
    }
    /**
     * Applies styles to different selection ranges
     */
    @Test
    public void addMultipleStylesOnDifferentRanges()
    {
        rootDSL.addChild(paragraph().addChild(text("abc")));
       
        final TreeOperation boldOp = new TreeStyle(SITE_ID, path(0, 0), 0, 3, "font-weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        boldOp.execute(root);

        final TreeOperation italicOp = new TreeStyle(SITE_ID, path(0, 0, 0), 1, 3, "font-style", "italic", NO_ADD_STYLE, SPLIT_LEFT, NO_SPLIT_RIGHT);
        italicOp.execute(root);
    
        //expectedRoot = <p><span font-weight=bold>[a]</span><span font-weight=bold, font-style=italic>[bc]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("a")),
                                                      span("font-weight", "bold").setAttribute("font-style", "italic").addChild(text("bc"))));        
        
        assertEquals("Invalid tree ", expectedRoot, root);
    }
    
      
    
    @Test
    public void simpleMoveTextRange() {
        
        rootDSL.addChild(paragraph().addChild(text("abcd")),
                         paragraph().addChild(text("xy")));
        
        
        final TreeInsertParagraph splitSrc1 = new TreeInsertParagraph(SITE_ID, 3, path(0, 0));
        splitSrc1.execute(root);
        
        
        final TreeInsertParagraph splitSrc2 = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        splitSrc2.execute(root);
        
        final TreeInsertParagraph splitDst1 = new TreeInsertParagraph(SITE_ID, 1, path(3, 0));
        splitDst1.execute(root);
        
        final TreeMoveParagraph move = new TreeMoveParagraph(SITE_ID, 1, 4);
        move.execute(root);
               
        final TreeMergeParagraph mergeSrc1 = new TreeMergeParagraph(SITE_ID, 1, 1, 1); 
        mergeSrc1.execute(root);
 
        final TreeMergeParagraph mergeDst1 = new TreeMergeParagraph(SITE_ID, 3, 1, 1); 
        mergeDst1.execute(root);
 
        final TreeMergeParagraph mergeDst2 = new TreeMergeParagraph(SITE_ID, 2, 1, 2); 
        mergeDst2.execute(root);
        
        //expectedRoot = <p>[ad]</p><p>[x][bc]y]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("a"),
                                                      text("d")),
                                 paragraph().addChild(text("x"),
                                                      text("bc"),
                                                      text("y")));
       
        assertEquals("Invalid tree ", expectedRoot, root);
        
    }
    
    
    /* --- Little DSL to simplify test writing --- */
    
    private TreeDSL rootDSL;
    private TreeDSL expectedRootDSL;

    private class TreeDSL {
        private Tree wrappedTree;
        public TreeDSL(Tree t) {
            this.wrappedTree = t;
        }
    
        public TreeDSL addChild(TreeDSL... children) {
            for (TreeDSL c : children) {
                this.wrappedTree.addChild(c.wrappedTree);
            }
            return this;
                
        }
        
        private void removeChild(int i) {
            this.wrappedTree.removeChild(i);
        }
        
        private TreeDSL getChild(int i) {
            return new TreeDSL(this.wrappedTree.getChild(i));
        }

        private void removeChild() {
            for (int i=0; i<this.wrappedTree.nbChildren(); i++) {
                this.wrappedTree.removeChild(i);
            }
        }

        private void clear() {
            removeChild();
        }

        private TreeDSL setAttribute(String styleName, String styleValue) {
            if (! this.wrappedTree.getNodeName().equals("span"))
                throw new UnsupportedOperationException("Not supported by this DSL");
            this.wrappedTree.setAttribute(styleName, styleValue);
            return this;
        }
    }

    private TreeDSL paragraph() {
        return new TreeDSL(TreeFactory.createParagraphTree());
    }
    private TreeDSL text(String str) {
        return new TreeDSL(TreeFactory.createTextTree(str));
    }
    private TreeDSL span(String styleName, String styleValue) {        
        Tree span = TreeFactory.createElementTree("span");
        span.setAttribute(styleName, styleValue);
        return new TreeDSL(span);
    }
 
}
