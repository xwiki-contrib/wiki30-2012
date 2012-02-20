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
                
}

