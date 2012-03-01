package fr.loria.score;

import org.junit.Test;

import fr.loria.score.jupiter.tree.operation.TreeCaretPosition;
import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

import static fr.loria.score.TestUtils.path;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author andre
 */
public class TreeCaretOperationTest {
    
    @Test
    public void localCaretAfterInsert(){
        TreeOperation insertText=new TreeInsertText(1, 0, path(0,0), 'c');
        TreeCaretPosition caretPosition=new TreeCaretPosition(1, 0, path(0,0));
        TreeOperation newCaretPosition =(TreeOperation) insertText.transform(caretPosition);

        assertEquals(1, newCaretPosition.getPosition());
    }
    
}
