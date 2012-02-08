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
import fr.loria.score.client.Converter;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeDocument;
import fr.loria.score.jupiter.tree.operation.TreeDeleteText;
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
        if (updateUI) {
            log.finest("Updating UI for WYSIWYG");
            updateDOM();
            applyDefaultSelection();
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
        if (operation instanceof TreeInsertText || operation instanceof TreeDeleteText) {
            return;
        }
        DomOperation domOperation = domOperationFactory.createDomOperation(operation);
        if (domOperation != null) {
            domOperation.execute((Document) nativeNode.getOwnerDocument());
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
            domOperation.execute((Document) nativeNode.getOwnerDocument());
        } else {
            log.warning("Update all DOM");
            log.finest("Root is before: " + customNode);
            updateDOM();
            log.finest("Root is after: " + customNode);
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
     * Synchronizes the DOM document with the Tree model.
     */
    private void updateDOM()
    {
        log.fine("Native node is before: " + Element.as(nativeNode).getString());
        nativeNode = replaceDOMNode(customNode, nativeNode);
        // The BODY element is overwritten in the synchronization process and so the contentEditable state is reset. We
        // have to focus the rich text area in order to make it editable on Firefox.
        Element.as(nativeNode).focus();
        log.fine("Native node is after: " + Element.as(nativeNode).getString());
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
