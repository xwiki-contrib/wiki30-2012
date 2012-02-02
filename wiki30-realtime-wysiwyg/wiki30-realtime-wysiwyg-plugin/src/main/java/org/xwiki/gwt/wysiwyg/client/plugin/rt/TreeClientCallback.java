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
import fr.loria.score.jupiter.tree.operation.TreeOperation;

/**
 * Callback for tree documents, wysiwyg editor
 */
public class TreeClientCallback implements ClientCallback {
    private static Node nativeNode;
    private Tree root;

    public TreeClientCallback(Node nativeNode) {
        this.nativeNode = nativeNode;
    }

    @Override
    public void onConnected(ClientDTO dto, fr.loria.score.jupiter.model.Document document, boolean updateUI) {
        root = ((TreeDocument) document).getRoot();
        if (updateUI) {
            log.finest("Updating UI for WYSIWYG");
            updateDOM();
        }
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void beforeSend(Message message) {
        AbstractOperation op = message.getOperation();
        if (op instanceof TreeOperation) {
            log.finest("Before send");
            TreeOperation treeOperation = (TreeOperation) op;
            int index = treeOperation.getPath()[0];
            boolean update = true;

            if (op instanceof TreeInsertText || op instanceof TreeDeleteText) {
                update = false;
            }

            //modify DOM
            if (update) {
                //todo: be smarter and update just some paragraph NOT all DOM
//                Tree treeParagraph = root.getChild(index);
//                Node nodeParagraph = nativeNode.getChild(index);
//                log.info("New node is:" + Element.as(replaceDOMNode(treeParagraph, nodeParagraph)).getInnerHTML());

                  updateDOM();
                //todo:  set the selection
            }
        }
    }

    @Override
    public void afterReceive(Message receivedMessage) {
        log.finest("Root is before: " + root);
        updateDOM();
        log.finest("Root is after: " + root);

    }

    /**
     * @return the updated native DOM node needed for some of the tree operations
     */
    protected static Node getUpdatedNativeNode() {
        return nativeNode;
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
        nativeNode = replaceDOMNode(root, nativeNode);
        log.fine("Native node is after: " + Element.as(nativeNode).getString());
    }

    /**
     * Updates and replaces the DOM node (structurally) according to the tree node
     * @param tree the model upon to update the DOM node
     * @param node to be replaced
     * @return the replaced DOM node reflecting the structure of the tree
     */
    private Node replaceDOMNode(Tree tree, Node node) {
        Node newNode = Converter.fromCustomToNative(tree);
        insertBrInEmptyParagraphs(newNode);
        node.getParentNode().replaceChild(newNode, node);
        return newNode;
    }
}