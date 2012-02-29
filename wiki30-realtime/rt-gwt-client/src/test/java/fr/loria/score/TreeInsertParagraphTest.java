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
 * Tests the TreeInsertParagraph operation in different scenarios
 *
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeInsertParagraphTest extends AbstractTreeOperationTest
{
    @Test
    public void splitParagraphWithText()
    {
        rootDSL.addChild(paragraph().addChild(text("abcd")));

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 2, path(0, 0));
        insertP.execute(rootDSL.getTree());

        // expectRoot = <p>[ab]</p><p>[cd]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("ab")),
            paragraph().addChild(text("cd")));

        assertEquals("Invalid tree result", expectedRootDSL, rootDSL);
    }

    @Test
    public void splitParagraphContainingStyles()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
            span("font-weight", "bold").addChild(text("cd"))));

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        insertP.execute(rootDSL.getTree());

        // expectRoot = <p><span font-weight=bold>[ab]</span></p><p><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
            paragraph().addChild(span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid tree result", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void splitParagraphContainingStylesWithSimpleInsertParagraphOperation()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
            span("font-weight", "bold").addChild(text("cd"))));

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 0));
        insertP.execute(rootDSL.getTree());

        // expectRoot = <p><span font-weight=bold>[a]</span></p><p><span font-weight=bold>[b]</span><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("a"))),
            paragraph().addChild(span("font-weight", "bold").addChild(text("b")),
                span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid tree result", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void splitParagraphContainingStylesWithSimpleInsertParagraphOperation1()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab")),
            span("font-weight", "bold").addChild(text("cd"))));

        final TreeInsertParagraph insertP = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertP.execute(rootDSL.getTree());

        // expectRoot = <p><span font-weight=bold>[ab]</span></p><p><span font-weight=bold>[]</span><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
            paragraph().addChild(span("font-weight", "bold").addChild(text("")),
                span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid tree result", expectedRootDSL.getTree(), rootDSL.getTree());
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
    public void insertParagraphMiddleOfLineMultipleText()
    {
        rootDSL.addChild(paragraph().addChild(text("a"),
            text("b")));

        // Split it at the end of text
        final TreeInsertParagraph paragraphAtEnd = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        paragraphAtEnd.execute(rootDSL.getTree());

        // expectedRoot = <p>[a]</p><p>[][b]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("a")),
            paragraph().addChild(text(""),text("b")));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
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
    public void insertParagraphStylingMiddle()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("xy"))));

        final TreeInsertParagraph insertPMiddle = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 0));
        insertPMiddle.execute(rootDSL.getTree());

        // expectedRoot = <p><span font-weight=bold>[x]</span></p><p><span font-weight=bold>[y]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("x"))),
            paragraph().addChild(span("font-weight", "bold").addChild(text("y"))));

        assertEquals("Invalid result ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void insertParagraphStylingEnd()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))));

        final TreeInsertParagraph insertPEnd = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertPEnd.execute(rootDSL.getTree());

        // expectedRoot = <p><span font-weight=bold>[ab]</span></p><p><span font-weight=bold>[]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
            paragraph().addChild(span("font-weight", "bold").addChild(text(""))));

        assertEquals("Invalid result ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void insertParagraphStyleWith2ChildrenAtEndOfFirstChild()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
            text("cd"))));
        // insert p after b
        final TreeInsertParagraph insertPAfterB = new TreeInsertParagraph(SITE_ID, 2, path(0, 0, 0));
        insertPAfterB.execute(rootDSL.getTree());

        // expectedRoot = <p><span font-weight=bold>[ab]</span></p><p><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
            paragraph().addChild(span("font-weight", "bold").addChild(text(""),text("cd"))));

        assertEquals("Invalid result ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void insertParagraphStyleWith2ChildrenBeforeSecondChild()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
            text("cd"))));

        // insert p before c
        final TreeInsertParagraph insertPBeforeSecondChild = new TreeInsertParagraph(SITE_ID, 0, path(0, 0, 1));
        insertPBeforeSecondChild.execute(rootDSL.getTree());

        // expectedRoot = <p><span font-weight=bold>[ab]</span></p><p><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"))),
            paragraph().addChild(span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid result ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void insertParagraphStyleWith2ChildrenMiddleOfSecondChild()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
            text("cd"))));

        // insert p between c and d
        final TreeInsertParagraph insertPMiddleSecondChild = new TreeInsertParagraph(SITE_ID, 1, path(0, 0, 1));
        insertPMiddleSecondChild.execute(rootDSL.getTree());

        // expectedRoot = <p><span font-weight=bold>[ab][c]</span></p><p><span font-weight=bold>[d]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("ab"),
            text("c"))),
            paragraph().addChild(span("font-weight", "bold").addChild(text("d"))));

        assertEquals("Invalid result ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void simpleInsertParagraphAtStartOfLine()
    {
        rootDSL.addChild(paragraph().addChild(text("a"), span("style", "bold").addChild(text("b"))));
        TreeOperation insert = new TreeInsertParagraph(SITE_ID, 0, path(0, 0));
        insert.execute(rootDSL.getTree());

        //expectedRoot = <p>[]</p><p>[a]<span bold>b</span></p> FALSE
        expectedRootDSL.addChild(paragraph().addChild(text("a"), span("style", "bold").addChild(text("b"))));
        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
        //fail("Cannot use InsertParagraph at start of line OR InsertParagraph is not well coded");
    }

    @Test
    public void simpleInsertParagraphInSpan()
    {
        rootDSL.addChild(paragraph().addChild(text("a"), span("style", "bold").addChild(text("b"))));
        TreeOperation insert = new TreeInsertParagraph(SITE_ID, 0, path(0, 1, 0));
        insert.execute(rootDSL.getTree());

        //expectedRoot = <p>[a]</p><p><span bold>[b]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(text("a"))).
            addChild(paragraph().addChild(span("style", "bold").addChild(text("b"))));
        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void simpleInsertParagraphInSpan1()
    {
        rootDSL.addChild(paragraph().addChild(span("style", "bold").addChild(text("b"))));
        TreeOperation insert = new TreeInsertParagraph(SITE_ID, 0, path(0, 0, 0));
        insert.execute(rootDSL.getTree());

        //expectedRoot = <p><span bold>[]</span></p><p><span bold>[b]</span></p> FALSE
        expectedRootDSL.addChild(paragraph().addChild(span("style", "bold").addChild(text("b"))));
           
        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
    }
}
