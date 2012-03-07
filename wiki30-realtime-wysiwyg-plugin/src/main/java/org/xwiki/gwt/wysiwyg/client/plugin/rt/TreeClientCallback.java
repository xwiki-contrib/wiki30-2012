/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import org.xwiki.gwt.dom.client.DOMUtils;
import org.xwiki.gwt.dom.client.Document;
import org.xwiki.gwt.dom.client.Element;
import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.dom.client.Selection;
import org.xwiki.gwt.wysiwyg.client.plugin.rt.dom.operation.DomOperation;
import org.xwiki.gwt.wysiwyg.client.plugin.rt.dom.operation.DomOperationFactory;

import com.google.gwt.dom.client.Node;

import fr.loria.score.client.ClientCallback;
import fr.loria.score.client.ClientDTO;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.transform.Transformation;
import fr.loria.score.jupiter.transform.TransformationFactory;
import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeDocument;
import fr.loria.score.jupiter.tree.operation.TreeCaretPosition;
import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

/**
 * Callback for tree documents, used by the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class TreeClientCallback implements ClientCallback
{
    /**
     * The root of the DOM document that is synchronized with the Tree model.
     */
    private Node nativeNode;

    /**
     * The root of the Tree model.
     */
    private Tree customNode;

    /**
     * The factory used to create DOM operations equivalent to Tree operations.
     */
    private DomOperationFactory domOperationFactory = new DomOperationFactory();

    /**
     * The object used to create the {@link TreeInsertText} operations used to preserve the selection.
     */
    private TreeOperationFactory treeOperationFactory = new TreeOperationFactory();

    /**
     * The object used to transform the text selection relative to the executed operation.
     */
    private Transformation transformation;

    /**
     * The siteId, used for re-creating the 'local selection' (which can be collapsed) after receiving remote messages
     */
    private int siteId;

    /**
     * Creates a new instance.
     * 
     * @param nativeNode the root of the DOM document that is synchronized with the Tree model
     */
    public TreeClientCallback(Node nativeNode)
    {
        this.nativeNode = nativeNode;
    }

    @Override
    public void onConnected(ClientDTO dto, fr.loria.score.jupiter.model.Document document, boolean updateUI)
    {
        customNode = ((TreeDocument) document).getRoot();
        siteId = dto.getSiteId();
        transformation = TransformationFactory.createTransformation(document);
        if (updateUI) {
            updateDOM();
        }
    }

    @Override
    public void onDisconnected()
    {
    }

    @Override
    public void beforeSend(Message message)
    {
        log.fine("Before send");
        TreeOperation operation = (TreeOperation) message.getOperation();

        DomOperation domOperation = domOperationFactory.createDomOperation(operation);
        if (domOperation != null) {
            //todo-marius: should restore original selection, as the returned range is not always the original selection.
            // Ex: select many styles, text nodes and apply a new style. The selection applied is the last one, and not the original selection
            applySelection(domOperation.execute((Document) nativeNode.getOwnerDocument()));
        } else {
            updateDOM();
        }
    }

    @Override
    public void afterReceive(Message receivedMessage)
    {
        log.fine("Executing received: " + receivedMessage);
        TreeOperation operation = (TreeOperation) receivedMessage.getOperation();
        DomOperation domOperation = domOperationFactory.createDomOperation(operation);
        if (domOperation != null) {
            TreeOperation[] selectionEndPoints = saveSelection();
            domOperation.execute((Document) nativeNode.getOwnerDocument());
            restoreSelection(selectionEndPoints, operation);
        } else {
            updateDOM();
        }
    }

    /**
     * Places the caret at the beginning of the DOM document.
     */
    private void applyDefaultSelection()
    {
        Document nativeOwnerDocument = (Document) nativeNode.getOwnerDocument();
        Selection selection = nativeOwnerDocument.getSelection();
        Range caret = nativeOwnerDocument.createRange();

        Node firstLeaf = DOMUtils.getInstance().getFirstLeaf(nativeNode);
        if (Node.TEXT_NODE == firstLeaf.getNodeType() || DOMUtils.getInstance().canHaveChildren(firstLeaf)) {
            // Either a text node or an empty element that can have children (e.g. p, span).
            caret.setStart(firstLeaf, 0);
        } else {
            caret.setStartBefore(firstLeaf);
        }
        caret.collapse(true);

        selection.removeAllRanges();
        selection.addRange(caret);
    }

    /**
     * Saves the current selection using two {@link TreeInsertText} operations.
     * 
     * @return two {@link TreeInsertText} operations that represent the start and end of the selection
     * @see TreeClientCallback#restoreSelection(TreeOperation[])
     */
    private TreeOperation[] saveSelection()
    {
        TreeOperation[] selection = new TreeCaretPosition[2];
        Range start = ((Document) nativeNode.getOwnerDocument()).getSelection().getRangeAt(0);
        start = EditorUtils.normalizeCaretPosition(start); // make sure the caret is in a text node
        Range end = start.cloneRange();
        start.collapse(true);
        end.collapse(false);
        selection[0] = treeOperationFactory.createCaretPosition(siteId, start, start.getStartOffset());
        selection[1] = treeOperationFactory.createCaretPosition(siteId, end, end.getEndOffset());
        return selection;
    }

    /**
     * Transforms the selection end points relative to the executed operation and restores them.
     * 
     * @param selectionEndPoints two {@link TreeCaretPosition} operations that mark the start and end points of the
     *            selection
     * @param operation the operation that has been executed
     * @see #saveSelection()
     */
    private void restoreSelection(TreeOperation[] selectionEndPoints, TreeOperation operation)
    {
        // Transform the selection relative to the executed operation.
        selectionEndPoints[0] = (TreeOperation) transformation.transform(selectionEndPoints[0], operation);    //todo: operation.transform(selectionEndPoints)
        selectionEndPoints[1] = (TreeOperation) transformation.transform(selectionEndPoints[1], operation);

        log.fine("Start selection: " + selectionEndPoints[0]);
        log.fine("End selection: " + selectionEndPoints[1]);

        // Place the caret at the updated position.
        Document document = (Document) nativeNode.getOwnerDocument();
        Selection selection = document.getSelection();
        Range caret = document.createRange();

        Node startContainer = EditorUtils.getChildNodeFromLocator(document.getBody(), selectionEndPoints[0].getPath());
        caret.setStart(startContainer, selectionEndPoints[0].getPosition());

        Node endContainer = EditorUtils.getChildNodeFromLocator(document.getBody(), selectionEndPoints[1].getPath());
        caret.setEnd(endContainer, selectionEndPoints[1].getPosition());

        selection.removeAllRanges();
        selection.addRange(caret);
    }

    /**
     * Selects the given DOM range.
     * 
     * @param range the DOM range to be selected
     */
    private void applySelection(Range range)
    {
        if (range != null) {
            Selection selection = ((Document) nativeNode.getOwnerDocument()).getSelection();
            selection.removeAllRanges();
            selection.addRange(range);
        }
    }

    /**
     * Synchronizes the DOM document with the Tree model.
     */
    private void updateDOM()
    {
        log.warning("Update all DOM");
        log.finest("Native node is before: " + Element.as(nativeNode).getString());
        nativeNode = replaceDOMNode(customNode, nativeNode);
        // The BODY element is overwritten in the synchronization process and so the contentEditable state is reset. We
        // have to focus the rich text area in order to make it editable on Firefox.
        Element.as(nativeNode).focus();
        log.finest("Native node is after: " + Element.as(nativeNode).getString());

        // Place the caret at the start of the DOM document.
        applyDefaultSelection();
    }

    /**
     * Updates and replaces the DOM node (structurally) according to the Tree model.
     * 
     * @param custom the model upon to update the DOM node
     * @param node to be replaced
     * @return the replaced DOM node reflecting the structure of the tree
     */
    private Node replaceDOMNode(Tree custom, Node node)
    {
        Node newNode = Converter.fromCustomToNative(custom);
        if (node != null) {
            node.getParentNode().replaceChild(newNode, node);
            Element.as(newNode).ensureEditable();
        } else {
            log.severe("Node is null");
        }
        return newNode;
    }
}
