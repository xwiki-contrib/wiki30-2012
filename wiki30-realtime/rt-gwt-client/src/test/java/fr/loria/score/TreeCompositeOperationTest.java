package fr.loria.score;

import org.junit.Test;

import fr.loria.score.jupiter.tree.operation.TreeCompositeOperation;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeMergeParagraph;
import fr.loria.score.jupiter.tree.operation.TreeMoveParagraph;

import static fr.loria.score.TreeDSL.paragraph;
import static fr.loria.score.TreeDSL.text;
import static org.junit.Assert.assertEquals;

/**
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeCompositeOperationTest extends AbstractTreeOperationTest
{
    @Test
    public void simpleMoveTextRange()
    {
        rootDSL.addChild(paragraph().addChild(text("abcd")),
            paragraph().addChild(text("xy")));

        // simulate move of 'bc' string between 'x' and 'y'.
        final TreeInsertParagraph splitSrc1 = new TreeInsertParagraph(SITE_ID, 3, path(0, 0));
        final TreeInsertParagraph splitSrc2 = new TreeInsertParagraph(SITE_ID, 1, path(0, 0));
        final TreeInsertParagraph splitDst1 = new TreeInsertParagraph(SITE_ID, 1, path(3, 0));
        final TreeMoveParagraph move = new TreeMoveParagraph(SITE_ID, 1, 4);
        final TreeMergeParagraph mergeSrc1 = new TreeMergeParagraph(SITE_ID, 1, 1, 1);
        final TreeMergeParagraph mergeDst1 = new TreeMergeParagraph(SITE_ID, 3, 1, 1);
        final TreeMergeParagraph mergeDst2 = new TreeMergeParagraph(SITE_ID, 2, 1, 2);

        final TreeCompositeOperation moveText =
            new TreeCompositeOperation(splitSrc1, splitSrc2, splitDst1, move, mergeSrc1, mergeDst1, mergeDst2);
        moveText.execute(rootDSL.getTree());

        //expectedRoot = <p>[a][d]</p><p>[x][bc]y]</p>
        expectedRootDSL.addChild(paragraph().addChild(text("a"),
                                                      text("d")),
                                 paragraph().addChild(text("x"),
                                                      text("bc"),
                                                      text("y")));

        assertEquals("Invalid tree ", expectedRootDSL.getTree(), rootDSL.getTree());
    }
}
