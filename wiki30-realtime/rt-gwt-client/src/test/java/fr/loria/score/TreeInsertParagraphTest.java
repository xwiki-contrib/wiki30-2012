package fr.loria.score;

import org.junit.Test;

import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

import static fr.loria.score.TestUtils.path;
import static fr.loria.score.TreeDSL.paragraph;
import static fr.loria.score.TreeDSL.span;
import static fr.loria.score.TreeDSL.text;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests TreeInsertParagraph operation in different cases
 *
 * @author Bogdan.Flueras@inria.fr
 * @author Luc.Andre@loria.fr
 * @author Gerald.Oster@loria.fr
 * 
 */
public class TreeInsertParagraphTest extends AbstractTreeOperationTest
{
    
    /*
     * Inserting paragraph at the start of an existing paragraph will not change
     * the current tree. It does not split the existing paragraph.
     * We do not expect that the editor inserts a new paragraph at the beginning 
     * of an existing one. Instead, it must use TreeNewParagraph operation.
     */
    @Test
    public void insertParagraphAtStartOfLine()
    {
        rootDSL.addChild(paragraph().addChild(text("a"), 
                                              span("font-weight", "bold").addChild(text("b"))));
        TreeOperation insert = new TreeInsertParagraph(SITE_ID, 0, path(0, 0));
        insert.execute(rootDSL.getTree());

        //expectedRoot = <p>[a]</p><p><span font-weight="bold">b</span></p>
        expectedRootDSL.addChild(paragraph().addChild(text("a"), 
                                                      span("font-weight", "bold").addChild(text("b"))));

        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
    }
    
    @Test
    public void insertParagraphEndOfLine()
    {
        rootDSL.addChild(paragraph().addChild(text("ab")));

        // Split it at the end of text
        final TreeInsertParagraph paragraphAtEnd = new TreeInsertParagraph(SITE_ID, 2, path(0, 0));
        paragraphAtEnd.execute(rootDSL.getTree());

        // expectedRoot = <p>[ab]</p><p>[]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("ab")),
                                 paragraph().addChild(text("")));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
    }
    
    @Test
    public void insertParagraphMiddleOfLine()
    {
        rootDSL.addChild(paragraph().addChild(text("ab")));

        // Split it at the middle of text
        final TreeInsertParagraph paragraphInMiddle = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        paragraphInMiddle.execute(rootDSL.getTree());

        // expectedRoot = <p>[a]</p><p>[b]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("a")),
                                 paragraph().addChild(text("b")));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void insertParagraphMiddleOfLineMultipleTextNode()
    {
        rootDSL.addChild(paragraph().addChild(text("a"),
                                              text("b")));

        // Split it at the end of text
        final TreeInsertParagraph paragraphAtEnd = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        paragraphAtEnd.execute(rootDSL.getTree());

        // expectedRoot = <p>[a]</p><p>[][b]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("a")),
                                 paragraph().addChild(text(""),
                                                      text("b")));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
    }
         
    /*
     * We do not expect that the editor inserts a new paragraph between two spans.
     * It should always be applied within a text node.
     */
    @Test
    public void insertParagraphInsideParagraphBetweenTwoStyledTextNodesAtSpanLevel()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
                                              span("font-weight", "bold").addChild(text("cd"))));

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        insertP.execute(rootDSL.getTree());

        // expectRoot = <p><span font-weight="bold">[ab]</span></p><p><span font-weight="bold">[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid tree result", expectedRootDSL.getTree(), rootDSL.getTree());
    }
    
    @Test
    public void insertParagraphInsideParagraphBetweenTwoStyledTextNodesInTheMiddleOfTheFirstTextNode()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
                                              span("font-weight", "bold").addChild(text("cd"))));

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 0));
        insertP.execute(rootDSL.getTree());

        // expectRoot = <p><span font-weight="bold">[a]</span></p><p><span font-weight="bold">[b]</span><span font-weight="bold">[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("a"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("b")),
                                                      span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid tree result", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void insertParagraphInsideParagraphBetweenTwoStyledTextNodesAtTheEndOfTheFirstTextNode()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
                                              span("font-weight", "bold").addChild(text("cd"))));

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertP.execute(rootDSL.getTree());

        // expectRoot = <p><span font-weight="bold">[ab]</span></p><p><span font-weight="bold">[]</span><span font-weight="bold">[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("")),
                                                      span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid tree result", expectedRootDSL.getTree(), rootDSL.getTree());
    }
    
    @Test
    public void insertParagraphInsideParagraphBetweenTwoStyledTextNodesAtTheStartOfTheSecondTextNode()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
                                              span("font-weight", "bold").addChild(text("cd"))));

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 0, path(0, 1, 0));
        insertP.execute(rootDSL.getTree());

        // expectRoot = <p><span font-weight="bold">[ab]</span></p><p><span font-weight="bold">[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid tree result", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    /*
     * We do not expect that the tree model contains two text-nodes below a
     * span node.
     * Therefore this test is just to demonstrate the current behavior.
     */    
    @Test
    public void insertParagraphInsideParagraphBetweenStyledTextNodesWithOneSpanStartOfFirstTextNode()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
                                                                                   text("cd"))));
        // insert p after b
        final TreeInsertParagraph insertPAfterB = new TreeInsertParagraph(SITE_ID, 0, path(0, 0, 0));
        insertPAfterB.execute(rootDSL.getTree());

        // expectedRoot = <p><span font-weight="bold">[ab][cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
                                                                                           text("cd"))));

        assertEquals("Invalid result ", expectedRootDSL.getTree(), rootDSL.getTree());
    }
    
    /*
     * We do not expect that the tree model contains two text-nodes below a
     * span node.
     * Therefore this test is just to demonstrate the current behavior.
     */    
    @Test
    public void insertParagraphInsideParagraphBetweenStyledTextNodesWithOneSpanEndOfFirstTextNode()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
                                                                                   text("cd"))));
        // insert p after b
        final TreeInsertParagraph insertPAfterB = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertPAfterB.execute(rootDSL.getTree());

        // expectedRoot = <p><span font-weight="bold">[ab]</span></p><p><span font-weight="bold">[][cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text(""),
                                                                                           text("cd"))));

        assertEquals("Invalid result ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    /*
     * We do not expect that the tree model contains two text-nodes below a
     * span node.
     * Therefore this test is just to demonstrate the current behavior.
     */ 
    @Test
    public void insertParagraphInsideParagraphBetweenStyledTextNodesWithOneSpanStartOfSecondTextNode()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
                                                                                   text("cd"))));

        // insert p before c
        final TreeInsertParagraph insertPBeforeSecondChild = new TreeInsertParagraph(SITE_ID, 0, path(0, 0, 1));
        insertPBeforeSecondChild.execute(rootDSL.getTree());

        // expectedRoot = <p><span font-weight="bold">[ab]</span></p><p><span font-weight="bold">[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid result ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    /*
     * We do not expect that the tree model contains two text-nodes below a
     * span node.
     * Therefore this test is just to demonstrate the current behavior.
     */ 
    @Test
    public void insertParagraphInsideParagraphBetweenStyledTextNodesWithOneSpanMiddleOfSecondTextNode()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
                                                                                   text("cd"))));

        // insert p between c and d
        final TreeInsertParagraph insertPMiddleSecondChild = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 1));
        insertPMiddleSecondChild.execute(rootDSL.getTree());

        // expectedRoot = <p><span font-weight="bold">[ab][c]</span></p><p><span font-weight="bold">[d]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
                                                                                           text("c"))),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("d"))));

        assertEquals("Invalid result ", expectedRootDSL.getTree(), rootDSL.getTree());
    }



    @Test
    public void insertParagraphInsideParagraphBetweenATextNodeAndAStyledTextNodeWithinFirstTextNode()
    {
        rootDSL.addChild(paragraph().addChild(text("a"), 
                                              span("font-weight", "bold").addChild(text("b"))));
        
        TreeOperation insert = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        insert.execute(rootDSL.getTree());

        //expectedRoot = <p>[a]</p><p>[]<span font-weight="bold">[b]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(text("a")),
                                 paragraph().addChild(text(""), span("font-weight", "bold").addChild(text("b"))));
        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
    }
    
    @Test
    public void insertParagraphInsideParagraphBetweenATextNodeAndAStyledTextNodeWithinSecondTextNode()
    {
        rootDSL.addChild(paragraph().addChild(text("a"), 
                                              span("font-weight", "bold").addChild(text("b"))));
        
        TreeOperation insert = new TreeInsertParagraph(SITE_ID, 0, path(0, 1, 0));
        insert.execute(rootDSL.getTree());

        //expectedRoot = <p>[a]</p><p><span font-weight="bold">[b]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(text("a")),
                                 paragraph().addChild(span("font-weight", "bold").addChild(text("b"))));
        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    /*
     * Inserting paragraph at the start of an existing paragraph will not change
     * the current tree. It does not split the existing paragraph.
     * We do not expect that the editor inserts a new paragraph at the beginning 
     * of an existing one. Instead, it must use TreeNewParagraph operation.
     */
    @Test
    public void insertParagraphAtStartOfStyledLine()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("b"))));
        TreeOperation insert = new TreeInsertParagraph(SITE_ID, 0, path(0, 0, 0));
        insert.execute(rootDSL.getTree());

        //expectedRoot = <p><span font-weight="bold">[b]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("b"))));
           
        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
    }
}
