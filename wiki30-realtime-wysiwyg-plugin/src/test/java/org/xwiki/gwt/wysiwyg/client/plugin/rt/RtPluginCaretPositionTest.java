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

        List textNodes = EditorUtils.getTextNodes(oldCaretPos).get(EditorUtils.NON__EMPTY);
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
        oldCaretPos.setStart(getContainer(), 1);
        oldCaretPos.setEnd(getContainer(), 1);
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
        // Caret is <p>[ab|]<span font-weight=”bold”>[cd]</span>ef</p>
        Text expectedTextNode = Text.as(getContainer().getFirstChild());
        assertEquals(Node.TEXT_NODE, expectedTextNode.getNodeType());

        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedTextNode, newCaretPos.getStartContainer());
        assertEquals(expectedTextNode.getLength(), newCaretPos.getStartOffset());
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

        Text text = Text.as(spanElement.getNextSibling());
        assertEquals(Node.TEXT_NODE, text.getNodeType());

        oldCaretPos.setStart(text, 0);
        oldCaretPos.setEnd(text, 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        Text expectedText = Text.as(spanElement.getFirstChild());
        assertEquals(Node.TEXT_NODE, expectedText.getNodeType());

        // Caret is <p>[ab]<span font-weight=”bold”>[cd|]</span>[ef]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(expectedText, newCaretPos.getStartContainer());
        assertEquals(expectedText.getLength(), newCaretPos.getStartOffset());
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

        Text expectedText = Text.as(spanElement.getNextSibling());
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

    // Caret is <p>|</p>
    public void testCaretStartOfEmptyParagraph()
    {
        Element p = getDocument().createPElement().cast();
        getDocument().getBody().replaceChild(p, getContainer());
        assertEquals("Invalid nr of children", 0, getDocument().getBody().getFirstChild().getChildCount());

        oldCaretPos.setStart(p, 0);
        oldCaretPos.setEnd(p, 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>|</p>
        assertEquals(Node.ELEMENT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p, newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[]<span>[]</span>[|z]</p>
    public void testCaretForBackspace1()
    {
        getContainer().removeFromParent();
        Element p = getDocument().createPElement().cast();
        getDocument().getBody().appendChild(p);

        com.google.gwt.dom.client.Text firstTextNode = getDocument().createTextNode("");
        p.appendChild(firstTextNode);
        p.appendChild(getDocument().createSpanElement().appendChild(getDocument().createTextNode("")));
        com.google.gwt.dom.client.Text lastTextNode = getDocument().createTextNode("z");
        p.appendChild(lastTextNode);

        oldCaretPos.setStart(lastTextNode, 0);
        oldCaretPos.setEnd(lastTextNode, 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);
        // Caret is <p>[]<span>[]</span>[|z]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(lastTextNode, newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[a]<span font:bold>foox</span><span font:normal>bar</span>|</p>
    public void testCaretAfterNonEmptySpan()
    {
        getContainer().setInnerHTML("a<span style=\"font-weight: bold;\">foox</span><span style=\"font-weight: normal;\">bar</span>");
        oldCaretPos.setStart(getContainer(), 3);
        oldCaretPos.setEnd(getContainer(), 3);

        final Text textNode = Text.as(getContainer().getLastChild().getFirstChild());
        assertEquals (Node.TEXT_NODE, textNode.getNodeType());
        assertEquals(textNode.getData(), "bar");

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);
        // Caret is <p>[a]<span font:bold>foo</span><span font:normal>bar|</span></p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(textNode, newCaretPos.getStartContainer());
        assertEquals(textNode.getLength(), newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[a]<span font:bold>foo</span><span font:normal></span>|</p>
    public void testCaretAfterEmptySpan()
    {
        getContainer().setInnerHTML("a<span style=\"font-weight: bold;\">foo</span><span style=\"font-weight: normal;\"></span>");
        oldCaretPos.setStart(getContainer(), 3);
        oldCaretPos.setEnd(getContainer(), 3);

        final Text textNode = Text.as(getContainer().getFirstChildElement().getFirstChild());
        assertEquals (Node.TEXT_NODE, textNode.getNodeType());
        assertEquals(textNode.getData(), "foo");

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);
        // Caret is <p>[a]<span font:bold>foo|</span><span font:normal></span></p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(textNode, newCaretPos.getStartContainer());
        assertEquals(textNode.getLength(), newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[][|][ab]</p>
    public void testCaretInEmptyTextNodeGoNextText()
    {
        getContainer().setInnerHTML("ab");
        getContainer().insertFirst(getDocument().createTextNode(""));
        getContainer().insertFirst(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 3, getContainer().getChildCount());

        oldCaretPos.setStart(getContainer().getChild(1), 0);
        oldCaretPos.setEnd(getContainer().getChild(1), 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);
        // Caret is <p>[][][|ab]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(getContainer().getLastChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[][ab][][|]</p>
    public void testCaretInEmptyTextNodeGoPrevText()
    {
        getContainer().setInnerHTML("ab");
        getContainer().insertAfter(getDocument().createTextNode(""), getContainer().getFirstChild());
        getContainer().insertAfter(getDocument().createTextNode(""), getContainer().getFirstChild());
        getContainer().insertFirst(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 4, getContainer().getChildCount());

        oldCaretPos.setStart(getContainer().getLastChild(), 0);
        oldCaretPos.setEnd(getContainer().getLastChild(), 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);
        // Caret is <p>[][ab|][][]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(getContainer().getChild(1), newCaretPos.getStartContainer());
        assertEquals(2, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>|[]</p>
    public void testCaretGoesToRightEmptyText()
    {
        getContainer().removeFromParent();
        Element p = getDocument().createPElement().cast();
        p.appendChild(getDocument().createTextNode(""));
        getDocument().getBody().appendChild(p);
        assertEquals("Invalid nr of children", 1, p.getChildCount());

        oldCaretPos.setStart(p, 0);
        oldCaretPos.setEnd(p, 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[|]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[]|</p>
    public void testCaretGoesLeftToEmptyText()
    {
        getContainer().removeFromParent();
        Element p = getDocument().createPElement().cast();
        p.appendChild(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 1, p.getChildCount());

        oldCaretPos.setStart(p, 1);
        oldCaretPos.setEnd(p, 1);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[|]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[]|[]</p>
    public void testCaretGoesToEmptyText3()
    {
        getContainer().removeFromParent();
        Element p = getDocument().createPElement().cast();
        p.appendChild(getDocument().createTextNode(""));
        p.appendChild(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 2, p.getChildCount());

        oldCaretPos.setStart(p, 1);
        oldCaretPos.setEnd(p, 1);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[|][]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

    // Caret is <p>[][|]</p>
    public void testCaretGoesToFirstEmptyText()
    {
        getContainer().removeFromParent();
        Element p = getDocument().createPElement().cast();
        p.appendChild(getDocument().createTextNode(""));
        p.appendChild(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 2, p.getChildCount());

        oldCaretPos.setStart(p.getChild(1), 0);
        oldCaretPos.setEnd(p.getChild(1), 0);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[|][]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }

     // Caret is <p>[][]|</p>
    public void testCaretGoesToFirstEmptyText1()
    {
        getContainer().removeFromParent();
        Element p = getDocument().createPElement().cast();
        p.appendChild(getDocument().createTextNode(""));
        p.appendChild(getDocument().createTextNode(""));
        assertEquals("Invalid nr of children", 2, p.getChildCount());

        oldCaretPos.setStart(p, 2);
        oldCaretPos.setEnd(p, 2);

        Range newCaretPos = EditorUtils.computeNewCaretPosition(oldCaretPos);

        // Caret is <p>[|][]</p>
        assertEquals(Node.TEXT_NODE, newCaretPos.getStartContainer().getNodeType());
        assertEquals(p.getFirstChild(), newCaretPos.getStartContainer());
        assertEquals(0, newCaretPos.getStartOffset());
        assertTrue(newCaretPos.isCollapsed());
    }
}

