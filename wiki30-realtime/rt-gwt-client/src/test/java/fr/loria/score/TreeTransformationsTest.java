package fr.loria.score;

import org.junit.Before;
import org.junit.Test;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.operation.TreeCompositeOperation;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeMergeParagraph;
import fr.loria.score.jupiter.tree.operation.TreeMoveParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

import static fr.loria.score.TestUtils.path;
import static fr.loria.score.TreeDSL.paragraph;
import static fr.loria.score.TreeDSL.text;
import static fr.loria.score.TreeDSL.span;
import fr.loria.score.jupiter.tree.operation.*;
import static org.junit.Assert.assertEquals;


/**
 * Test the effect of computing and/or executing tree transformations. It should test all TreeOperation
 * transformations.
 *
 * @author Bogdan.Flueras@inria.fr
 * @author Gerald.Oster@loria.fr
 */
public class TreeTransformationsTest extends AbstractTreeOperationTest
{
    private static final int SITE_A = 1;

    private static final int SITE_B = 2;

    private Tree siteA;

    private TreeDSL siteADSL;

    private Tree expectedSiteA;

    private TreeDSL expectedSiteADSL;

    private Tree siteB;

    private TreeDSL siteBDSL;

    private Tree expectedSiteB;

    private TreeDSL expectedSiteBDSL;

    @Before
    public void init()
    {
        siteA = TreeFactory.createEmptyTree();
        siteADSL = new TreeDSL(siteA);
        expectedSiteA = TreeFactory.createEmptyTree();
        expectedSiteADSL = new TreeDSL(expectedSiteA);

        siteB = TreeFactory.createEmptyTree();
        siteBDSL = new TreeDSL(siteB);
        expectedSiteB = TreeFactory.createEmptyTree();
        expectedSiteBDSL = new TreeDSL(expectedSiteB);
    }

    @Test
    public void insertParagraph_before_insertText()
    {
        TreeOperation op1 = new TreeInsertText(SITE_A, 3, path(0, 0), 'I');
        TreeOperation op2 = new TreeInsertParagraph(SITE_B, 1, path(0, 0));

        TreeOperation opt1 = (TreeOperation) op2.transform(op1);
        TreeOperation expectedOperation1 = new TreeInsertText(SITE_A, 2, path(1, 0), 'I');
        assertEquals("Invalid result ", expectedOperation1.toString(), opt1.toString());

        TreeOperation opt2 = (TreeOperation) op1.transform(op2);
        TreeOperation expectedOperation2 = new TreeInsertParagraph(SITE_B, 1, path(0, 0));
        assertEquals("Invalid result ", expectedOperation2.toString(), opt2.toString());

        siteADSL.addChild(paragraph().addChild(text("abcd")));
        op1.execute(siteA);
        opt2.execute(siteA);

        siteBDSL.addChild(paragraph().addChild(text("abcd")));
        op2.execute(siteB);
        opt1.execute(siteB);

        assertEquals(siteA, siteB);

        expectedSiteADSL.addChild(paragraph().addChild(text("a")),
            paragraph().addChild(text("bcId")));

        assertEquals(expectedSiteA, siteA);
    }

    @Test
    public void insertParagraph_after_insertText()
    {
        TreeOperation op1 = new TreeInsertText(SITE_A, 1, path(0, 0), 'I');
        TreeOperation op2 = new TreeInsertParagraph(SITE_B, 3, path(0, 0));

        TreeOperation opt1 = (TreeOperation) op2.transform(op1);
        TreeOperation expectedOperation1 = new TreeInsertText(SITE_A, 1, path(0, 0), 'I');
        assertEquals("Invalid result ", expectedOperation1.toString(), opt1.toString());

        TreeOperation opt2 = (TreeOperation) op1.transform(op2);
        TreeOperation expectedOperation2 = new TreeInsertParagraph(SITE_B, 4, path(0, 0));
        assertEquals("Invalid result ", expectedOperation2.toString(), opt2.toString());

        siteADSL.addChild(paragraph().addChild(text("abcd")));
        op1.execute(siteA);
        opt2.execute(siteA);

        siteBDSL.addChild(paragraph().addChild(text("abcd")));
        op2.execute(siteB);
        opt1.execute(siteB);

        assertEquals(siteA, siteB);

        expectedSiteADSL.addChild(paragraph().addChild(text("aIbc")),
            paragraph().addChild(text("d")));

        assertEquals(expectedSiteA, siteA);
    }

    @Test
    public void insertParagraph_samePlace_insertText()
    {
        TreeOperation op1 = new TreeInsertText(SITE_A, 2, path(0, 0), 'I');
        TreeOperation op2 = new TreeInsertParagraph(SITE_B, 2, path(0, 0));

        TreeOperation opt1 = (TreeOperation) op2.transform(op1);
        TreeOperation expectedOperation1 = new TreeInsertText(SITE_A, 0, path(1, 0), 'I');
        assertEquals(expectedOperation1.toString(), opt1.toString());

        TreeOperation opt2 = (TreeOperation) op1.transform(op2);
        TreeOperation expectedOperation2 = new TreeInsertParagraph(SITE_B, 2, path(0, 0));
        assertEquals(expectedOperation2.toString(), opt2.toString());

        siteADSL.addChild(paragraph().addChild(text("abcd")));
        op1.execute(siteA);
        opt2.execute(siteA);

        siteBDSL.addChild(paragraph().addChild(text("abcd")));
        op2.execute(siteB);
        opt1.execute(siteB);

        assertEquals(siteA, siteB);

        expectedSiteADSL.addChild(paragraph().addChild(text("ab")),
            paragraph().addChild(text("Icd")));

        assertEquals(expectedSiteA, siteA);
    }

    @Test
    public void moveText_insertText()
    {
        siteADSL.addChild(paragraph().addChild(text("abcd")),
            paragraph().addChild(text("xy")));

        // simulate move of 'bc' string between 'x' and 'y'.       
        final TreeInsertParagraph splitSrc1 = new TreeInsertParagraph(SITE_A, 3, path(0, 0));
        final TreeInsertParagraph splitSrc2 = new TreeInsertParagraph(SITE_A, 1, path(0, 0));
        final TreeInsertParagraph splitDst1 = new TreeInsertParagraph(SITE_A, 1, path(3, 0));
        final TreeMoveParagraph move = new TreeMoveParagraph(SITE_A, 1, 4);
        final TreeMergeParagraph mergeSrc1 = new TreeMergeParagraph(SITE_A, 1, 1, 1);
        final TreeMergeParagraph mergeDst1 = new TreeMergeParagraph(SITE_A, 3, 1, 1);
        final TreeMergeParagraph mergeDst2 = new TreeMergeParagraph(SITE_A, 2, 1, 2);

        final TreeCompositeOperation seq =
            new TreeCompositeOperation(splitSrc1, splitSrc2, splitDst1, move, mergeSrc1, mergeDst1, mergeDst2);
        seq.execute(siteA);

        //expectedRoot = <p>[ad]</p><p>[x][bc][y]</p>
        expectedSiteADSL.addChild(paragraph().addChild(text("a"),
            text("d")),
            paragraph().addChild(text("x"),
                text("bc"),
                text("y")));
        assertEquals("Invalid tree ", expectedSiteA, siteA);

        siteBDSL.addChild(paragraph().addChild(text("abcd")),
            paragraph().addChild(text("xy")));

        final TreeInsertText ins = new TreeInsertText(SITE_B, 2, path(0, 0), 'I');
        ins.execute(siteB);

        expectedSiteBDSL.addChild(paragraph().addChild(text("abIcd")),
            paragraph().addChild(text("xy")));
        assertEquals("Invalid tree ", expectedSiteB, siteB);

        TreeOperation opt = seq.transform(ins);
        opt.execute(siteA);

        TreeOperation expectedOperation = new TreeInsertText(SITE_B, 1, path(1, 1), 'I');
        assertEquals("Invalid result ", expectedOperation.toString(), opt.toString());

        //expectedRoot = <p>[ad]</p><p>[x][bIc]y]</p>
        expectedSiteADSL.clear();
        expectedSiteADSL.addChild(paragraph().addChild(text("a"),
            text("d")),
            paragraph().addChild(text("x"),
                text("bIc"),
                text("y")));
        assertEquals("Invalid result ", expectedSiteA, siteA);

        TreeOperation seqT = (TreeOperation) ins.transform(seq);
        seqT.execute(siteB);

        // expectedSiteA == expectedSiteB
        assertEquals("Invalid result ", expectedSiteA, siteB);
    }
    
    @Test
    public void treeStyle_samePlace_treeStyle()
    {
        TreeOperation op1 = new TreeStyle(SITE_A, path(0, 0), 0, 4, "font-style", "italic", true, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        TreeOperation op2 = new TreeStyle(SITE_B, path(0, 0), 0, 4, "font-style", "italic", true, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
 
        
        TreeOperation opt1 = (TreeOperation) op2.transform(op1);
        TreeOperation expectedOperation1 = new TreeStyle(SITE_A, path(0, 0, 0), 0, 4, "font-style", "italic", false, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        assertEquals(expectedOperation1.toString(), opt1.toString());

        TreeOperation opt2 = (TreeOperation) op1.transform(op2);
        TreeOperation expectedOperation2 = new TreeStyle(SITE_B, path(0, 0, 0), 0, 4, "font-style", "italic", false, NO_SPLIT_LEFT, NO_SPLIT_RIGHT);
        assertEquals(expectedOperation2.toString(), opt2.toString());

        siteADSL.addChild(paragraph().addChild(text("abcd")));
        op1.execute(siteA);
        opt2.execute(siteA);

        siteBDSL.addChild(paragraph().addChild(text("abcd")));
        op2.execute(siteB);
        opt1.execute(siteB);

        assertEquals(siteA, siteB);

        expectedSiteADSL.addChild(paragraph().addChild(span("font-style", "italic").addChild(text("abcd"))));
        assertEquals(expectedSiteA, siteA);       
        
    }
}

