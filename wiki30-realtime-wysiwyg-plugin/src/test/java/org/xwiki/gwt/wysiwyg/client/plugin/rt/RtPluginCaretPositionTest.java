package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import java.util.List;

import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.dom.client.Text;
import org.xwiki.gwt.wysiwyg.client.RtPluginTestCase;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

/**
 * Test that caret is placed deterministically within a text node, if for instance user places with mouse the caret
 *
 * @author Bogdan.Flueras@inria.fr
 */
public class RtPluginCaretPositionTest extends RtPluginTestCase
{
    public static final String INNER_HTML = "ab<span>cd</span>ef";

    private Range oldCaretPos;

    @Override
    public void gwtSetUp() throws Exception
    {
        super.gwtSetUp();
        getContainer().setInnerHTML(INNER_HTML);
        oldCaretPos = getDocument().createRange();
    }

    public void testFindNonEmptyTextNodes()
    {
        oldCaretPos.setStart(getContainer(), 0);
        oldCaretPos.setEnd(getContainer(), 0);

        List textNodes = EditorUtils.getNonEmptyTextNodes(oldCaretPos);
        assertNotNull(textNodes);
        assertEquals("Invalid nr of text nodes", 0, textNodes.size());
    }

    // Caret is <p>|[ab]<span font-weight=”bold”>[cd]</span>ef</p>
    public void testCaretAfterParagraphBeforeFirstChild()
    {
        oldCaretPos.setStart(getContainer(), 0);
        oldCaretPos.setEnd(getContainer(), 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // New caret is <p>[|ab]<span font-weight=”bold”>[cd]</span>ef</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(getContainer().getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[|ab]<span font-weight=”bold”>[cd]</span>ef</p>
    public void testCaretAtStartOfTheFirstChildTextNode() // todo: first child is element node
    {
        assertEquals(Node.TEXT_NODE, getContainer().getFirstChild().getNodeType());
        Text firstTextNode = Text.as(getContainer().getFirstChild());

        oldCaretPos.setStart(firstTextNode, 0);
        oldCaretPos.setEnd(firstTextNode, 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[|ab]<span font-weight=”bold”>[cd]</span>ef</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(firstTextNode, newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[a|b]<span font-weight=”bold”>[cd]</span>ef</p>
    public void testCaretAtMiddleOfTheFirstChildTextNode() // todo: first child is element node
    {
        assertEquals(Node.TEXT_NODE, getContainer().getFirstChild().getNodeType());
        Text firstTextNode = Text.as(getContainer().getFirstChild());

        oldCaretPos.setStart(firstTextNode, 1);
        oldCaretPos.setEnd(firstTextNode, 1);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);
        // Caret is <p>[a|b]<span font-weight=”bold”>[cd]</span>ef</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(firstTextNode, newCaretPos.getStartContainer());
        assertEquals(1, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab|]<span font-weight=”bold”>[cd]</span>ef</p>
    public void testCaretAtEndOfTheFirstChildTextNode() // todo: first child is element node
    {
        assertEquals(Node.TEXT_NODE, getContainer().getFirstChild().getNodeType());
        Text firstTextNode = Text.as(getContainer().getFirstChild());

        oldCaretPos.setStart(firstTextNode, firstTextNode.getLength());
        oldCaretPos.setEnd(firstTextNode, firstTextNode.getLength());

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[ab|]<span font-weight=”bold”>[cd]</span>ef</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(firstTextNode, newCaretPos.getStartContainer());
        assertEquals(firstTextNode.getLength(), newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]|<span font-weight=”bold”>[cd]</span>ef</p>
    public void testCaretAfterFirstChildTextNode() // todo: first child is element node
    {
        oldCaretPos.setStartAfter(getContainer().getFirstChild());
        oldCaretPos.setEndBefore(getContainer().getFirstChild());

//        oldCaretPos.setStart(getContainer(), 1);
//        oldCaretPos.setEnd(getContainer(), 1);
        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[ab|]<span font-weight=”bold”>[cd]</span>ef</p>
        Text expectedText = Text.as(getContainer().getFirstChild()); //Get the first child text node
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedText, newCaretPos.getStartContainer());
        assertEquals(expectedText.getLength(), newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]<span font-weight=”bold”>|[cd]</span>ef</p>
    public void testCaretInChildElementBeforeFirstChildTextNode() // todo: first child is element node
    {
        Element spanElement = getContainer().getFirstChildElement();
        assertNotNull(spanElement);
        assertEquals("Invalid element name", "span", spanElement.getNodeName().toLowerCase());

        oldCaretPos.setStartAfter(spanElement); // spanElement,0
        oldCaretPos.setEndBefore(spanElement.getFirstChild());

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[ab|]<span font-weight=”bold”>[cd]</span>ef</p>
        Text expectedText = Text.as(getContainer().getFirstChild()); //Get the first child text node
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedText, newCaretPos.getStartContainer());
        assertEquals(expectedText.getLength(), newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]<span font-weight=”bold”>[|cd]</span>ef</p>
    public void testCaretInChildElementStartOfTextNode()
    {
        Element element = getContainer().getFirstChildElement();
        assertNotNull(element);
        assertEquals("Invalid element name", "span", element.getNodeName().toLowerCase());

        assertEquals(Node.TEXT_NODE, element.getFirstChild().getNodeType());
        Text textNode = Text.as(element.getFirstChild());

        oldCaretPos.setStart(textNode, 0);
        oldCaretPos.setEnd(textNode, 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);
        // Caret is <p>[ab]<span font-weight=”bold”>[|cd]</span>ef</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(textNode, newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]<span font-weight=”bold”>[c|d]</span>ef</p>
    public void testCaretInChildElementMiddleOfTextNode()
    {
        Element element = getContainer().getFirstChildElement();
        assertNotNull(element);
        assertEquals("Invalid element name", "span", element.getNodeName().toLowerCase());

        assertEquals(Node.TEXT_NODE, element.getFirstChild().getNodeType());
        Text textNode = Text.as(element.getFirstChild());

        oldCaretPos.setStart(textNode, 1);
        oldCaretPos.setEnd(textNode, 1);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[ab]<span font-weight=”bold”>[c|d]</span>ef</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(textNode, newCaretPos.getStartContainer());
        assertEquals(1, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]<span font-weight=”bold”>[cd|]</span>ef</p>
    public void testCaretInChildElementEndOfTextNode()
    {
        Element element = getContainer().getFirstChildElement();
        assertNotNull(element);
        assertEquals("Invalid element name", "span", element.getNodeName().toLowerCase());

        Text expectedText = Text.as(element.getFirstChild());
        assertEquals(Node.TEXT_NODE, expectedText.getNodeType());

        oldCaretPos.setStart(expectedText, expectedText.getLength());
        oldCaretPos.setEnd(expectedText, expectedText.getLength());

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[ab]<span font-weight=”bold”>[cd|]</span>ef</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedText, newCaretPos.getStartContainer());
        assertEquals(expectedText.getLength(), newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]<span font-weight=”bold”>[cd]|</span>ef</p>
    public void testCaretInChildElementAfterFirstChildTextNode()
    {
        Element spanElement = getContainer().getFirstChildElement();
        assertNotNull(spanElement);
        assertEquals("Invalid element name", "span", spanElement.getNodeName().toLowerCase());

        oldCaretPos.setStart(spanElement, 1);
        oldCaretPos.setEnd(spanElement, 1);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        Text expectedText = Text.as(spanElement.getFirstChild());
        assertEquals(Node.TEXT_NODE, expectedText.getNodeType());

        // Caret is <p>[ab]<span font-weight=”bold”>[cd|]</span>ef</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedText, newCaretPos.getStartContainer());
        assertEquals(expectedText.getLength(), newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]<span font-weight=”bold”>[cd]</span>|[ef]</p>
    public void testCaretAfterSpanBeforeTextNode()
    {
        oldCaretPos.setStart(getContainer(), 2);
        oldCaretPos.setEnd(getContainer(), 2);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[ab]<span font-weight=”bold”>[cd|]</span>ef</p>
        //Get the first child text node of span element
        Text expectedText = Text.as(getContainer().getFirstChildElement().getFirstChild());
        assertEquals(Node.TEXT_NODE, expectedText.getNodeType());

        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedText, newCaretPos.getStartContainer());
        assertEquals(expectedText.getLength(), newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]<span font-weight=”bold”>[cd]</span>[|ef]</p>
    public void testCaretAfterSpanStartOfTextNode()
    {
        Element spanElement = getContainer().getFirstChildElement();
        assertNotNull(spanElement);
        assertEquals("Invalid element name", "span", spanElement.getNodeName().toLowerCase());

        Text expectedText = Text.as(spanElement.getFirstChild());
        assertEquals(Node.TEXT_NODE, expectedText.getNodeType());

        oldCaretPos.setStart(expectedText, 0);
        oldCaretPos.setEnd(expectedText, 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[ab]<span font-weight=”bold”>[cd]</span>[|ef]</p>
        assertEquals(oldCaretPos, newCaretPos);
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedText, newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]<span font-weight=”bold”>[cd]</span>[e|f]</p>
    public void testCaretAfterSpanMiddleOfTextNode()
    {
        Element spanElement = getContainer().getFirstChildElement();
        assertNotNull(spanElement);
        assertEquals("Invalid element name", "span", spanElement.getNodeName().toLowerCase());

        Text expectedText = Text.as(spanElement.getFirstChild());
        assertEquals(Node.TEXT_NODE, expectedText.getNodeType());

        oldCaretPos.setStart(expectedText, 1);
        oldCaretPos.setEnd(expectedText, 1);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[ab]<span font-weight=”bold”>[cd]</span>[e|f]</p>
        assertEquals(oldCaretPos, newCaretPos);
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedText, newCaretPos.getStartContainer());
        assertEquals(1, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]<span font-weight=”bold”>[cd]</span>[ef|]</p>
    public void testCaretAfterSpanEndOfTextNode()
    {
        Element spanElement = getContainer().getFirstChildElement();
        assertNotNull(spanElement);
        assertEquals("Invalid element name", "span", spanElement.getNodeName().toLowerCase());

        Text expectedText = Text.as(spanElement.getFirstChild());
        assertEquals(Node.TEXT_NODE, expectedText.getNodeType());

        oldCaretPos.setStart(expectedText, expectedText.getLength());
        oldCaretPos.setEnd(expectedText, expectedText.getLength());

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[ab]<span font-weight=”bold”>[cd]</span>[ef|]</p>
        assertEquals(oldCaretPos, newCaretPos);
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedText, newCaretPos.getStartContainer());
        assertEquals(expectedText.getLength(), newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[ab]<span font-weight=”bold”>[cd]</span>[ef]|</p>
    public void testCaretAtEndOfParagraph()
    {
        Element spanElement = getContainer().getFirstChildElement();
        assertNotNull(spanElement);
        assertEquals("Invalid element name", "span", spanElement.getNodeName().toLowerCase());

        Text expectedText = Text.as(spanElement.getFirstChild());
        assertEquals(Node.TEXT_NODE, expectedText.getNodeType());

        oldCaretPos.setStart(getContainer(), 3);
        oldCaretPos.setEnd(getContainer(), 3);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[ab]<span font-weight=”bold”>[cd]</span>[ef|]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedText, newCaretPos.getStartContainer());
        assertEquals(expectedText.getLength(), newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }
}

