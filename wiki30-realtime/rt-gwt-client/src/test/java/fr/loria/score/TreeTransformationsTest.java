package fr.loria.score;

import org.junit.Before;
import org.junit.Test;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.operation.*;

import static org.junit.Assert.assertEquals;

import static fr.loria.score.TreeDSL.paragraph;
import static fr.loria.score.TreeDSL.span;
import static fr.loria.score.TreeDSL.text;
import java.util.List;

/**
 * Test the effect of computing and/or executing tree transformations.
 * It should test all TreeOperation transformations.
 *
 * @author Bogdan.Flueras@inria.fr
 * @author Gerald.Oster@loria.fr
 */
public class TreeTransformationsTest
{
    private static final int SITE_A = 1;
    private static final int SITE_B = 2;
    
    private static int[] path(int... positions) {
        int[] path = positions;
        return path;
    }
    
    
    @Before
    public void init()
    {
    }
    
    @Test
    public void insertText_insertParagraph()
    { 
        TreeOperation op1 = new TreeInsertText(SITE_A, 3, path(0, 0), 'x');
        TreeOperation op2 = new TreeInsertParagraph(SITE_B, 1, path(0, 0));

        TreeOperation opt = (TreeOperation) op2.transform(op1);
       
        TreeOperation expectedOperation = new TreeInsertText(SITE_A, 2, path(1, 0), 'x');

        assertEquals("Invalid result ", expectedOperation.toString(), opt.toString());
    }
     
    
    @Test
    public void moveText_insertText() 
    {
        Tree siteA = TreeFactory.createEmptyTree();
        TreeDSL siteADSL = new TreeDSL(siteA);
        Tree expectedSiteA = TreeFactory.createEmptyTree();
        TreeDSL expectedSiteADSL = new TreeDSL(expectedSiteA);
        
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

        final TreeCompositeOperation seq = new TreeCompositeOperation(splitSrc1, splitSrc2, splitDst1, move, mergeSrc1, mergeDst1, mergeDst2);
        seq.execute(siteA);

        //expectedRoot = <p>[ad]</p><p>[x][bc]y]</p>
        expectedSiteADSL.addChild(paragraph().addChild(text("a"),
                                                       text("d")),
                                  paragraph().addChild(text("x"),
                                                       text("bc"),
                                                       text("y"))); 
        assertEquals("Invalid tree ", expectedSiteA, siteA);   
        
        
        Tree siteB = TreeFactory.createEmptyTree();
        TreeDSL siteBDSL = new TreeDSL(siteB);
        Tree expectedSiteB = TreeFactory.createEmptyTree();
        TreeDSL expectedSiteBDSL = new TreeDSL(expectedSiteB);
        
        siteBDSL.addChild(paragraph().addChild(text("abcd")),
                          paragraph().addChild(text("xy")));
        
        final TreeInsertText ins = new TreeInsertText(SITE_B, 2, path(0, 0), 'I');
        ins.execute(siteB);
        
        expectedSiteBDSL.addChild(paragraph().addChild(text("abIcd")),
                                  paragraph().addChild(text("xy")));        
        assertEquals("Invalid tree ", expectedSiteB, siteB);

        TreeOperation opt = seq.transform(ins);
        seq.execute(siteA);
  
        TreeOperation expectedOperation = new TreeInsertText(SITE_B, 1, path(1, 1), 'I');
        assertEquals("Invalid result ", expectedOperation.toString(), opt.toString());  
        
        //expectedRoot = <p>[ad]</p><p>[x][bIc]y]</p>
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
}

