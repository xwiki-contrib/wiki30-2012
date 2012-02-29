package fr.loria.score;

import org.junit.Test;

import fr.loria.score.jupiter.tree.operation.TreeNewParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

import static fr.loria.score.TreeDSL.paragraph;
import static fr.loria.score.TreeDSL.span;
import static fr.loria.score.TreeDSL.text;
import static org.junit.Assert.assertEquals;

/**
 * Tests TreeInsertParagraph operation in different cases
 *
 * @author Bogdan.Flueras@inria.fr
 * @author Luc.Andre@loria.fr
 * @author Gerald.Oster@loria.fr
 * 
 */
public class TreeNewParagraphTest extends AbstractTreeOperationTest
{
    @Test
    public void newParagraphAtStartOfLine()
    {
        rootDSL.addChild(paragraph().addChild(text("ab")));

        // Split it at the start of the line
        final TreeNewParagraph paragraphAtStart = new TreeNewParagraph(SITE_ID, 0);
        paragraphAtStart.execute(rootDSL.getTree());

        // expectedRoot = <p>[]</p><p>[ab]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("")),
                                 paragraph().addChild(text("ab")));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
    }
    
    @Test
    public void newParagraphAtEndOfLine()
    {
        rootDSL.addChild(paragraph().addChild(text("ab")));

        // Split it at the end of the line
        final TreeNewParagraph paragraphAtStart = new TreeNewParagraph(SITE_ID, 1);
        paragraphAtStart.execute(rootDSL.getTree());

        // expectedRootDSL.getTree() = <p>[ab]</p><p>[]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("ab")),
                                 paragraph().addChild(text("")));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void newParagraphAtStartOfStyledLine()
    {
        rootDSL.addChild(paragraph().addChild(text("a"), 
                                              span("style", "bold").addChild(text("b"))));
 
        TreeOperation insert = new TreeNewParagraph(SITE_ID, 0);
        insert.execute(rootDSL.getTree());

        //expectedRootDSL.getTree() = <p>[]</p><p>[a]<span bold>b</span></p>
        expectedRootDSL.addChild(paragraph().addChild(text("")),
                                 paragraph().addChild(text("a"), 
                                                      span("style", "bold").addChild(text("b"))));
        
        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
    }

    @Test
    public void newParagraphAtEndOfStyledLine()
    {
        rootDSL.addChild(paragraph().addChild(text("a"), 
                                              span("style", "bold").addChild(text("b"))));
 
        TreeOperation insert = new TreeNewParagraph(SITE_ID, 1);
        insert.execute(rootDSL.getTree());

        //expectedRootDSL.getTree() = <p>[a]<span bold>b</span></p><p>[]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("a"), 
                                                      span("style", "bold").addChild(text("b"))),
                                 paragraph().addChild(text("")));
        
        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
    }


    @Test
    public void newParagraphInSpan()
    {
        // FIXME: I've the feeling that this test is useless since it has the same expectations
        // than the newParagraphAtStartOfStyledLine test. I think we should remove it.
        
        rootDSL.addChild(paragraph().addChild(span("style", "bold").addChild(text("b"))));
        
        TreeOperation insert = new TreeNewParagraph(SITE_ID, 0);
        insert.execute(rootDSL.getTree());

        //expectedRootDSL.getTree() = <p>[]</p><p><span bold>[b]</span></p>
        expectedRootDSL.addChild(paragraph().addChild(text("")),
                                 paragraph().addChild(span("style", "bold").addChild(text("b"))));
        
        assertEquals("Invalid tree", expectedRootDSL.getTree(), rootDSL.getTree());
    }
}
