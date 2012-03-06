package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.wysiwyg.client.RtPluginTestCase;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

/**
 * Test that the caret moves into an empty text (when possible) if non-empty text nodes are missing
 *
 * @author Bogdan.Flueras@inria.fr
 */
public class CaretPositionInEmptyTextTest extends RtPluginTestCase
{
    private Range oldCaretPos;

    @Override protected void gwtSetUp() throws Exception
    {
        super.gwtSetUp();
        oldCaretPos = getDocument().createRange();
    }

    // Caret is <p>|</p>
    public void testCaretStartOfEmptyParagraph()
    {
        assertEquals("Invalid nr of children", 0, getContainer().getChildCount());

        oldCaretPos.setStart(getContainer(), 0);
        oldCaretPos.setEnd(getContainer(), 0);

        Range newCaretPos = EditorUtils.normalizeCaretPosition(oldCaretPos);

        // Caret is <p>|</p>
        assertEquals(Node.ELEMENT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(getContainer(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>|[]</p>
    public void testCaretGoesToRightEmptyText()
    {
        Element p = getContainer();
        p.appendChild(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 1, p.getChildCount());

        oldCaretPos.setStart(p, 0);
        oldCaretPos.setEnd(p, 0);

        Range newCaretPos = EditorUtils.normalizeCaretPosition(oldCaretPos);

        // Caret is <p>[|]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>|[][]</p>
    public void testCaretGoesToFirstRightEmptyText()
    {
        Element p = getContainer();
        p.appendChild(getDocument().createTextNode(""));
        p.appendChild(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 2, p.getChildCount());

        oldCaretPos.setStart(p, 0);
        oldCaretPos.setEnd(p, 0);

        Range newCaretPos = EditorUtils.normalizeCaretPosition(oldCaretPos);

        // Caret is <p>[|][]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[]|</p>
    public void testCaretGoesLeftToEmptyText()
    {
        Element p = getContainer();
        p.appendChild(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 1, p.getChildCount());

        oldCaretPos.setStart(p, 1);
        oldCaretPos.setEnd(p, 1);

        Range newCaretPos = EditorUtils.normalizeCaretPosition(oldCaretPos);

        // Caret is <p>[|]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[]|[]</p>
    public void testCaretGoesToEmptyText3()
    {
        Element p = getContainer();
        p.appendChild(getDocument().createTextNode(""));
        p.appendChild(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 2, p.getChildCount());

        oldCaretPos.setStart(p, 1);
        oldCaretPos.setEnd(p, 1);

        Range newCaretPos = EditorUtils.normalizeCaretPosition(oldCaretPos);

        // Caret is <p>[|][]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[][|]</p>
    public void testCaretGoesToFirstEmptyText()
    {
        Element p = getContainer();
        p.appendChild(getDocument().createTextNode(""));
        p.appendChild(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 2, p.getChildCount());

        oldCaretPos.setStart(p.getChild(1), 0);
        oldCaretPos.setEnd(p.getChild(1), 0);

        Range newCaretPos = EditorUtils.normalizeCaretPosition(oldCaretPos);

        // Caret is <p>[|][]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

     // Caret is <p>[][]|</p>
    public void testCaretGoesToFirstEmptyText1()
    {
        Element p = getContainer();
        p.appendChild(getDocument().createTextNode(""));
        p.appendChild(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 2, p.getChildCount());

        oldCaretPos.setStart(p, 2);
        oldCaretPos.setEnd(p, 2);

        Range newCaretPos = EditorUtils.normalizeCaretPosition(oldCaretPos);

        // Caret is <p>[|][]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }
}
