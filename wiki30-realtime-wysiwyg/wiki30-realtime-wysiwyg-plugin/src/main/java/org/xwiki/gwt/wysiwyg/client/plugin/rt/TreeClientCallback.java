package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import fr.loria.score.client.ClientCallback;
import fr.loria.score.client.ClientDTO;
import fr.loria.score.client.Converter;
import fr.loria.score.jupiter.model.AbstractOperation;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeDocument;
import fr.loria.score.jupiter.tree.operation.TreeDeleteText;
import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeNewParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

/**
 * Callback for tree documents, wysiwyg editor
 */
public class TreeClientCallback implements ClientCallback {
    private Node nativeNode;
    private Tree customNode;

    public TreeClientCallback(Node nativeNode) {
        this.nativeNode = nativeNode;
    }

    @Override
    public void onConnected(ClientDTO dto, fr.loria.score.jupiter.model.Document document, boolean updateUI) {
        customNode = ((TreeDocument) document).getRoot();
        if (updateUI) {
            log.finest("Updating UI for WYSIWYG");
            updateDOM();
            //todo: set the selection on first paragraph
        }
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void beforeSend(Message message) {
        AbstractOperation op = message.getOperation();
        if (op instanceof TreeOperation) {
            log.fine("Before send");
            TreeOperation treeOp = (TreeOperation) op;
            int index;
            boolean update = true;

            if (op instanceof TreeInsertText || op instanceof TreeDeleteText) {
                update = false;
            }

            if (treeOp instanceof TreeNewParagraph) {
                //todo: it doesn't work this way because the line plugin, default behaviour is executed afterwards
                update = true;
                log.fine("New Paragraph update DOM");
                index = treeOp.getPosition();
                Node newNode = Converter.fromCustomToNative(customNode.getChild(index)); // new paragraph
                nativeNode.replaceChild(newNode, nativeNode.getChild(index)); //
            }

            //modify DOM
            if (update) {
                //todo: be smarter and update just some paragraph NOT all DOM
//                Tree treeParagraph = customNode.getChild(index);
//                Node nodeParagraph = nativeNode.getChild(index);
//                log.info("New node is:" + Element.as(replaceDOMNode(treeParagraph, nodeParagraph)).getInnerHTML());

                  updateDOM();
                //todo:  set the selection
            }
        }
    }

    private void receiverNewParagraph(TreeOperation treeOp) {
        int index;Element p = Document.get().createPElement();
        p.appendChild(Document.get().createTextNode(""));
        p.appendChild(Document.get().createBRElement());

        index = treeOp.getPosition();
        Node old = nativeNode.getChild(index);
        if (old != null) {
            old.getParentElement().insertBefore(p, old);
        } else { // new paragraph on last line
            log.info("was null :)");
            nativeNode.appendChild(p);
        }

    }

    @Override
    public void afterReceive(Message receivedMessage) {
        log.finest("Root is before: " + customNode);
        updateDOM();
        log.finest("Root is after: " + customNode);

    }

    /**
     * Inserts BR elements into the empty P elements
     * @param node the root node to start inserting
     */
    private void insertBrInEmptyParagraphs(Node node) {
        if (node == null || !Element.is(node)) {
            return;
        }
        if (node.getNodeName().equalsIgnoreCase("p")) {
            final int childCount = node.getChildCount();
            if (childCount == 0 || (childCount == 1 && (node.getChild(0).getNodeName().equalsIgnoreCase("#text") && node.getChild(0).getNodeValue().equals("")))) {
                node.appendChild(Document.get().createBRElement());
            }
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            insertBrInEmptyParagraphs(children.getItem(i));
        }
    }

    private void updateDOM() {
        log.fine("Native node is before: " + Element.as(nativeNode).getString());
        nativeNode = replaceDOMNode(customNode, nativeNode);
        log.fine("Native node is after: " + Element.as(nativeNode).getString());
    }

    /**
     * Updates and replaces the DOM node (structurally) according to the tree node
     * @param custom the model upon to update the DOM node
     * @param node to be replaced
     * @return the replaced DOM node reflecting the structure of the tree
     */
    private Node replaceDOMNode(Tree custom, Node node) {
        Node newNode = Converter.fromCustomToNative(custom);
        insertBrInEmptyParagraphs(newNode);
        if (node != null) {
            node.getParentNode().replaceChild(newNode, node);
        } else {
            log.severe("Node is null");
        }
        return newNode;
    }
}