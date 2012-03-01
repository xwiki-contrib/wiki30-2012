package fr.loria.score;

import org.junit.Test;

import fr.loria.score.jupiter.tree.operation.TreeCaretPosition;
import fr.loria.score.jupiter.tree.operation.TreeDeleteText;
import fr.loria.score.jupiter.tree.operation.TreeIdOp;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeMergeParagraph;
import fr.loria.score.jupiter.tree.operation.TreeNewParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;
import fr.loria.score.jupiter.tree.operation.TreeStyle;

import static fr.loria.score.TestUtils.path;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author andre
 */
public class TreeCaretOperationTest extends AbstractTreeOperationTest {
    
    @Test
    public void localCaretAfterInsertText()
    {
        //insert occurs at same position as caret
        int pos = 0;
        TreeOperation insertText = new TreeInsertText(SITE_ID, pos, path(0,0), 'c');
        TreeCaretPosition caretPosition = new TreeCaretPosition(SITE_ID, pos, path(0,0));
        TreeOperation newCaretPosition = (TreeOperation) insertText.transform(caretPosition);

        assertEquals(1, newCaretPosition.getPosition());
        assertArrayEquals(path(0, 0), newCaretPosition.getPath());
    }

    @Test
    public void localCaretAfterDeleteTextOperationOnDelete()
    {
        //delete occurs at same position as caret
        int pos = 1;
        TreeOperation deleteText = new TreeDeleteText(SITE_ID, pos, path(0,0));
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, pos, path(0,0));

        TreeOperation newCaretPos = (TreeOperation) deleteText.transform(caretPos);
        assertEquals(pos, newCaretPos.getPosition());
        assertArrayEquals(path(0,0), newCaretPos.getPath());
    }

    @Test
    public void localCaretAfterDeleteTextOperationOnBackspace()
    {
        //delete occurs at same position as caret
        int pos = 1;
        TreeOperation deleteText = new TreeDeleteText(SITE_ID, 0, path(0,0));
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, pos, path(0,0));

        TreeOperation newCaretPos = (TreeOperation) deleteText.transform(caretPos);
        assertEquals(0, newCaretPos.getPosition());
        assertArrayEquals(path(0,0), newCaretPos.getPath());
    }

    @Test
    public void localCaretAfterIdOperation()
    {
        TreeOperation localOp = new TreeIdOp();
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, 0, path(0,0));

        TreeOperation newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(0, newCaretPos.getPosition());
        assertArrayEquals(path(0,0), newCaretPos.getPath());
    }

    @Test
    public void localCaretAfterInsertParagraphOperation()
    {
        // insert paragraph occurs at same position as caret
        int pos = 1;
        TreeOperation localOp = new TreeInsertParagraph(SITE_ID, pos, path(0,0));
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, pos, path(0,0));

        TreeOperation newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(0, newCaretPos.getPosition());
        assertArrayEquals(path(1, 0), newCaretPos.getPath());

        pos = 0;
        localOp = new TreeInsertParagraph(SITE_ID, pos, path(0, 1));
        caretPos = new TreeCaretPosition(SITE_ID, pos, path(0, 1));

        newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(0, newCaretPos.getPosition());
        assertArrayEquals(path(1, 0), newCaretPos.getPath());
    }

    @Test
    public void localCaretAfterMergeParagraphOperationOnDelete()
    {
        //caret is at end of the left paragraph and it stays there
        int pos = 1;
        TreeOperation localOp = new TreeMergeParagraph(SITE_ID, pos, 1, 1);
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, pos, path(0,0));

        TreeOperation newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(pos, newCaretPos.getPosition());
        assertArrayEquals(path(0,0), newCaretPos.getPath());
    }

    @Test
    public void localCaretAfterMergeParagraphOperationOnBackspace()
    {
        //caret is at start of the right paragraph and it moves at the end of the left paragraph
        int pos = 1;
        TreeOperation localOp = new TreeMergeParagraph(SITE_ID, pos, 1, 1);
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, 0, path(1, 0));

        TreeOperation newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(0, newCaretPos.getPosition());
        assertArrayEquals(path(0, 1), newCaretPos.getPath());
    }

    @Test
    public void localCaretAfterNewParagraphStartOfLine()
    {
        TreeOperation localOp = new TreeNewParagraph(SITE_ID, 0);
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, 0, path(0,0));

        TreeOperation newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(0, newCaretPos.getPosition());
        assertArrayEquals(path(1, 0), newCaretPos.getPath());
    }

    @Test
    public void localCaretAfterRemoteNewParagraphStartOfLine()
    {
        TreeOperation localCaretPos = new TreeCaretPosition(SITE_ID, 0, path(0,0));
        TreeOperation remoteOp = new TreeNewParagraph(SITE_ID + 1, 0);

        TreeOperation newLocalCaretPos = (TreeOperation) remoteOp.transform(localCaretPos);
        assertEquals(0, newLocalCaretPos.getPosition());
        assertArrayEquals(path(1, 0), newLocalCaretPos.getPath());
    }

    @Test
    public void localCaretAfterNewParagraphEndOfLine()
    {
        TreeOperation localOp = new TreeNewParagraph(SITE_ID, 1);
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, 5, path(0,0));

        TreeOperation newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(0, newCaretPos.getPosition());
        assertArrayEquals(path(1, 0), newCaretPos.getPath());
    }

    @Test
    public void localCaretAfterRemoteNewParagraphEndOfLine()
    {
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, 5, path(0,0));
        TreeOperation remoteOp = new TreeNewParagraph(SITE_ID + 1, 1);

        TreeOperation newLocalCaretPos = (TreeOperation) remoteOp.transform(caretPos);
        assertEquals(5, newLocalCaretPos.getPosition());
        assertArrayEquals(path(0, 0), newLocalCaretPos.getPath());
    }

    //todo: write unit tests, when local caret is after/before the generated op position
    @Test
    public void localCaretBeforePositionOfStyleOperation() // style op is remotely generated
    {
        // todo: play with style op params and the caret should be the same!
        TreeOperation localOp = new TreeStyle(SITE_ID + 1, path(0, 0), 3, 6, "font-weight", "bold", ADD_STYLE, SPLIT_LEFT, SPLIT_RIGHT); // int siteId, int[] path, int start, int end, String param, String value, boolean addStyle, boolean splitLeft, boolean splitRight) {
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, 2, path(0, 0));

        TreeOperation newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(2, newCaretPos.getPosition());
        assertArrayEquals(path(0, 0), newCaretPos.getPath());
    }

    @Test
    public void localCaretInStyleOperation() // style op is remotely generated
    {
        TreeOperation localOp = new TreeStyle(SITE_ID + 1, path(0, 0), 0, 5, "font-weight", "bold", ADD_STYLE, SPLIT_LEFT, SPLIT_RIGHT); // int siteId, int[] path, int start, int end, String param, String value, boolean addStyle, boolean splitLeft, boolean splitRight) {
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, 3, path(0, 0));

        TreeOperation newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(3, newCaretPos.getPosition());
        assertArrayEquals(path(0, 1, 0), newCaretPos.getPath());

        localOp = new TreeStyle(SITE_ID + 1, path(0, 0), 2, 5, "font-weight", "bold", ADD_STYLE, SPLIT_LEFT, SPLIT_RIGHT);
        caretPos = new TreeCaretPosition(SITE_ID, 5, path(0,0));

        newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(5 - 2, newCaretPos.getPosition());
        assertArrayEquals(path(0, 1, 0), newCaretPos.getPath());
    }

    @Test
    public void localCaretAfterPositionOfStyleOperation() // style op is remotely generated
    {
        TreeOperation localOp = new TreeStyle(SITE_ID  + 1, path(0, 0), 0, 5, "font-weight", "bold", ADD_STYLE, NO_SPLIT_LEFT, SPLIT_RIGHT); // int siteId, int[] path, int start, int end, String param, String value, boolean addStyle, boolean splitLeft, boolean splitRight) {
        TreeOperation caretPos = new TreeCaretPosition(SITE_ID, 5, path(0, 0));

        TreeOperation newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(5, newCaretPos.getPosition());
        assertArrayEquals(path(0, 0, 0), newCaretPos.getPath());

        localOp = new TreeStyle(SITE_ID, path(0,0), 0, 5, "font-weight", "bold", ADD_STYLE, SPLIT_LEFT, SPLIT_RIGHT);
        caretPos = new TreeCaretPosition(SITE_ID, 7, path(0,0));

        newCaretPos = (TreeOperation) localOp.transform(caretPos);
        assertEquals(7 - 5, newCaretPos.getPosition());
        assertArrayEquals(path(0, 2), newCaretPos.getPath());
    }
}
