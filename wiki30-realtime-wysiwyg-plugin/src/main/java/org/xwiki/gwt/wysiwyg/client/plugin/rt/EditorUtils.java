package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import java.util.ArrayList;
import java.util.List;

import org.xwiki.gwt.dom.client.DOMUtils;
import org.xwiki.gwt.dom.client.Range;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

/**
 * Utility class used for controlling the behaviour of the realtime wysiwyg editor.
 * The caret has to be deterministically positioned, it is always located as follows:
 * <ul>
 *     <li>at the last position of the previous non-empty text-node within the same paragraph if such a previous text-node exists.</li>
 *     <li>if not, at the first position of the next non-empty text-node relative to the locator within the same paragraph.</li>
 * </ul>
 * @author Bogdan.Flueras@inria.fr
 */
public class EditorUtils
{
    /**
     * @param oldCaretPosition the old caret position
     * @return the new caret position, as described above
     */
    public static Range computeNewCaretPosition(Range oldCaretPosition)
    {
        Range newCaretPos = null;
        if (oldCaretPosition.isCollapsed()) {
            newCaretPos = oldCaretPosition.cloneRange();
            newCaretPos.collapse(true);

            // if I'm in a non-empty text node, return the oldCaretPosition
            if (isNonEmptyTextNode(oldCaretPosition.getStartContainer())) {
                return oldCaretPosition;
            } else {
                // go left whenever possible, if not go right
                List<Text> leftTextNodesInSameP = getLeftTextNodesInSameP(oldCaretPosition);
                int leftSize = leftTextNodesInSameP.size();
                if (leftSize > 0) {
                    Text firstLeftTextNode = leftTextNodesInSameP.get(leftSize - 1);
                    int leftPosition = Math.min(oldCaretPosition.getEndOffset(), firstLeftTextNode.getLength());
                    newCaretPos.setStart(firstLeftTextNode, firstLeftTextNode.getLength());
                } else {
                    List<Text> rightTextNodesInSameP = getRightTextNodesInSameP(oldCaretPosition);
                    if (rightTextNodesInSameP.size() > 0) {
                        Text firstRightTextNode = rightTextNodesInSameP.get(0);
                        newCaretPos.setStart(firstRightTextNode, 0);
                    }
                }
            }
        }
        return newCaretPos;
    }

    /**
     * @param node the node whose ancestor is to be returned
     * @return the ancestor for this node which is just below the paragraph ancestor
     */
    public static Node getAncestorBelowParagraph(Node node) {
        Node ancestor = node;
        while (!"p".equalsIgnoreCase(ancestor.getParentNode().getNodeName())) {
            ancestor = ancestor.getParentNode();
        }
        return ancestor;
    }

    /**
     * @param node a node
     * @return the paragraph ancestor of the node
     */
    public static Node getAncestorParagraph(Node node)
    {
        // We can't have nested paragraphs
        if (node.getNodeName().equalsIgnoreCase("p")) {
            return node;
        }
        return getAncestorBelowParagraph(node).getParentNode();
    }

    /**
     * Converts a DOM range to an list of operation targets.
     *
     * @param range a DOM range
     * @return the corresponding list of operation targets
     */
    public static List<OperationTarget> getIntermediaryTargets(Range range) {
        // Iterate through all the text nodes within the given range and extract the operation target
        List<OperationTarget> operationTargets = new ArrayList<OperationTarget>();
        // Create the intermediary targets backwards because if we preserve the normal order when we modify the tree,
        // the following targets will no longer reflect that
        List<Text> textNodes = getNonEmptyTextNodes(range);
        for (int i = 0; i < textNodes.size(); i++) {
            Text text = textNodes.get(i);
            int startIndex = 0;
            if (text == range.getStartContainer()) {
                startIndex = range.getStartOffset();
            }
            int endIndex = text.getLength();
            if (text == range.getEndContainer()) {
                endIndex = range.getEndOffset();
            }
            operationTargets.add(0, new OperationTarget(TreeHelper.getLocator(text), startIndex, endIndex, text.getLength()));
        }
        return operationTargets;
    }

     /**
     * @param range a DOM range
     * @return the list of non empty text nodes that are completely or partially (at least one character) included in
     *         the given range
     */
    public static List<Text> getNonEmptyTextNodes(Range range) {
        Node leaf = DOMUtils.getInstance().getFirstLeaf(range);
        Node lastLeaf = DOMUtils.getInstance().getLastLeaf(range);
        List<Text> textNodes = new ArrayList<Text>();
        // If the range starts at the end of a text node we have to ignore that node.
        if (isNonEmptyTextNode(leaf)
                && (leaf != range.getStartContainer() || range.getStartOffset() < leaf.getNodeValue().length())) {
            textNodes.add((Text) leaf);
        }
        while (leaf != lastLeaf) {
            leaf = DOMUtils.getInstance().getNextLeaf(leaf);
            if (isNonEmptyTextNode(leaf)) {
                textNodes.add((Text) leaf);
            }
        }
        // If the range ends at the start of a text node then we have to ignore that node.
        int lastIndex = textNodes.size() - 1;
        if (lastIndex >= 0 && range.getEndOffset() == 0 && textNodes.get(lastIndex) == range.getEndContainer()) {
            textNodes.remove(lastIndex);
        }
        return textNodes;
    }

    /**
     * @param range the selection range, usually a collapsed range which is the caret position
     * @return all the non-empty text nodes from the left of the caret within the same paragraph
     */
    private static List<Text> getLeftTextNodesInSameP(Range range)
    {
        Range leftRange = range.cloneRange();
        leftRange.setEnd(range.getStartContainer(), range.getStartOffset());
        Node parentPNode = getAncestorParagraph(range.getStartContainer());
        leftRange.setStart(parentPNode, 0);

        return getNonEmptyTextNodes(leftRange);
    }

    /**
     * @param range the selection range, usually a collapsed range which is the caret position
     * @return all the non-empty text nodes from the right of the caret within the same paragraph
     */
    private static List<Text> getRightTextNodesInSameP(Range range)
    {
        Range rightRange = range.cloneRange();
        Node parentPNode = getAncestorParagraph(range.getEndContainer());
        rightRange.setEndAfter(parentPNode.getLastChild());

        return getNonEmptyTextNodes(rightRange);
    }

    /**
     * @param node a DOM node
     * @return {@code true} if the given node is of type {@link Node#TEXT_NODE} and it's not empty, {@code false}
     *         otherwise
     */
    public static boolean isNonEmptyTextNode(Node node) {
        return node != null && node.getNodeType() == Node.TEXT_NODE && node.getNodeValue().length() > 0;
    }

    /**
     * @param range a DOM range
     * @return the node in the same paragraph that precedes the start point of the given range, in a depth-first pre-order search
     */
    public Node getPreviousNodeInSameParagraph(Range range)
    {
        Node node = range.getStartContainer();
        if (node.getNodeName().equalsIgnoreCase("p")) {
            return null;
        }

        if (node.hasChildNodes() && range.getStartOffset() > 0) {
            return node.getChildNodes().getItem(range.getStartOffset() - 1);
        }
        while (node != null && node.getPreviousSibling() == null) {
            node = node.getParentNode();
        }
        return node;
    }
}
