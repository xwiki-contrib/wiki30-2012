package fr.loria.score;

import org.junit.Test;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.operation.TreeOperation;
import fr.loria.score.jupiter.tree.operation.TreeStyle;

import static fr.loria.score.TestUtils.path;
import static fr.loria.score.TreeDSL.paragraph;
import static fr.loria.score.TreeDSL.span;
import static fr.loria.score.TreeDSL.text;
import static org.junit.Assert.assertEquals;

/**
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeStyleTest extends AbstractTreeOperationTest
{
    @Test
    public void executeStyle()
    {
        rootDSL.addChild(paragraph().addChild(text("abcd")));

        final TreeStyle bold =
            new TreeStyle(SITE_ID, path(0, 0), 0, 4, "font-weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        bold.execute(rootDSL.getTree());

        // expectRoot = <p><span font-weight=bold>[abcd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("abcd"))));

        assertEquals("Invalid tree result", expectedRootDSL.getTree(), rootDSL.getTree());

        final TreeStyle style1 =
            new TreeStyle(SITE_ID, path(0, 0, 0), 0, 2, "font-weight", "bold", NO_ADD_STYLE, NO_SPLIT_LEFT,
                SPLIT_RIGHT);
        final Tree rootClone = rootDSL.getTree().deepCloneNode();
        style1.execute(rootDSL.getTree());

        // expectedRootDSL.getTree() = <p><span font-weight=bold>[ab]</span><span font-weight=bold>[cd]</span></p>
        expectedRootDSL.clear();
        expectedRootDSL.addChild(paragraph().addChild(
            span("font-weight", "bold").addChild(text("ab")),
            span("font-weight", "bold").addChild(text("cd"))));

        assertEquals("Invalid tree result", expectedRootDSL.getTree(), rootDSL.getTree());

        final TreeStyle style2 =
            new TreeStyle(SITE_ID, path(0, 0, 0), 2, 4, "font-weight", "bold", NO_ADD_STYLE, SPLIT_LEFT,
                NO_SPLIT_RIGHT);
        style2.execute(rootClone);

        assertEquals("Invalid tree result", rootDSL.getTree(), rootClone);
    }

    @Test
    public void addSimpleStyle()
    {
        rootDSL.addChild(paragraph().addChild(text("abcd")));

        final TreeStyle styleOperation =
            new TreeStyle(SITE_ID, path(0, 0), 0, 4, "font-weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        styleOperation.execute(rootDSL.getTree());

        // expectedRootDSL.getTree() = <p><span font-weight="bold">[abcd]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("abcd"))));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void addSimpleStyleOnSubSelection()
    {
        rootDSL.addChild(paragraph().addChild(text("abcd")));

        final TreeStyle styleOperation =
            new TreeStyle(SITE_ID, path(0, 0), 1, 3, "font-weight", "bold", ADD_STYLE, SPLIT_LEFT, SPLIT_RIGHT);
        styleOperation.execute(rootDSL.getTree());

        //expectedRootDSL.getTree() = <p>[a]<span font-weight=bold>[bc]</span>[d]</p>
        expectedRootDSL.addChild(paragraph().addChild(
                                        text("a"),
                                        span("font-weight", "bold").addChild(text("bc")),
                                        text("d")));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void addMultipleStylesOnSameRange()
    {
        rootDSL.addChild(paragraph().addChild(text("abc")));

        final TreeOperation boldOp =
            new TreeStyle(SITE_ID, path(0, 0), 0, 3, "font-weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        boldOp.execute(rootDSL.getTree());

        final TreeOperation italicOp =
            new TreeStyle(SITE_ID, path(0, 0, 0), 0, 1, "font-style", "italic", NO_ADD_STYLE, NO_SPLIT_LEFT,
                SPLIT_RIGHT);
        italicOp.execute(rootDSL.getTree());

        //expectedRootDSL.getTree() = <p><span font-weight=bold, font-style=italic>[a]</span><span font-weight=bold>[bc]</span></p>        
        expectedRootDSL.addChild(
            paragraph().addChild(span("font-weight", "bold").setAttribute("font-style", "italic").addChild(text("a")),
                                 span("font-weight", "bold").addChild(text("bc"))));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    /**
     * Applies styles to different selection ranges
     */
    @Test
    public void addMultipleStylesOnDifferentRanges()
    {
        rootDSL.addChild(paragraph().addChild(text("abc")));

        final TreeOperation boldOp =
            new TreeStyle(SITE_ID, path(0, 0), 0, 3, "font-weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        boldOp.execute(rootDSL.getTree());

        final TreeOperation italicOp =
            new TreeStyle(SITE_ID, path(0, 0, 0), 1, 3, "font-style", "italic", NO_ADD_STYLE, SPLIT_LEFT,
                NO_SPLIT_RIGHT);
        italicOp.execute(rootDSL.getTree());

        //expectedRootDSL.getTree() = <p><span font-weight=bold>[a]</span><span font-weight=bold, font-style=italic>[bc]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(
            span("font-weight", "bold").addChild(text("a")),
            span("font-weight", "bold").setAttribute("font-style", "italic").addChild(text("bc"))));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    
    @Test
    public void treeStyleWithinTwoTextNodes1()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("a"),
                                                                                   text("b"))));   

        final TreeOperation styleOperation2 = new TreeStyle(SITE_ID, path(0, 0, 1), 0, 1, "font-style", "italic", NO_ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        styleOperation2.execute(rootDSL.getTree());

        final TreeOperation styleOperation1 = new TreeStyle(SITE_ID, path(0, 0, 0), 1, 1, "font-style", "italic", NO_ADD_STYLE, SPLIT_LEFT, NO_SPLIT_RIGHT);
        styleOperation1.execute(rootDSL.getTree());
  
        
        //expectedRootDSL.getTree() = <p><span font-weight=bold>[a]</span><span font-weight=bold font-style=italic>[b]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("a")),
                                                      span("font-weight", "bold").setAttribute("font-style", "italic").addChild(text("b"))));
        
        assertEquals("Invalid result ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void addItalicStyleOnSecondBoldTextNode()
    {
        rootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("a"))
                                                                         .addChild(text("b"))));

        TreeOperation italic = new TreeStyle(SITE_ID, path(0, 0, 1), 0, 1, "font-style", "italic", NO_ADD_STYLE, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        italic.execute(rootDSL.getTree());

        //expected = <p><span bold>[a]</span><span bold, italic>[b]</span>
        expectedRootDSL.addChild(paragraph().addChild(span("font-weight", "bold").addChild(text("a")))
                                            .addChild(span("font-weight", "bold").setAttribute("font-style", "italic")
                                                .addChild(text("b"))));

        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
    }
}
