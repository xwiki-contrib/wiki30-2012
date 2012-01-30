package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import fr.loria.score.client.ClientCallback;
import fr.loria.score.client.ClientDTO;
import fr.loria.score.client.Converter;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeDocument;

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

    private void updateDOM() {
        log.fine("Native node is before: " + Element.as(nativeNode).getString());
        Node newNode = Converter.fromCustomToNative(root);
        insertBrInEmptyParagraphs(newNode);
        nativeNode.getParentNode().replaceChild(newNode, nativeNode);
        nativeNode = newNode;
        log.fine("Native node is after: " + Element.as(nativeNode).getString());
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onExecute(Message receivedMessage) {
        log.info("Root is before: " + root);

        updateDOM();
//        TreeOperation operation = (TreeOperation) receivedMessage.getOperation();
//        final int position = operation.getPosition();
//        final int[] path = operation.getPath();
//        final Node targetNode = TreeHelper.getChildNodeFromLocator(nativeNode, path);
//        final short targetNodeType = targetNode.getNodeType();
//
//        if (operation instanceof TreeInsertText) {
//            //operates on a text node
//            TreeInsertText insertText = (TreeInsertText) operation;
//            String txt = String.valueOf(insertText.getText());
//
//            if (Node.ELEMENT_NODE == targetNodeType) {
//                Node newTextNode = Document.get().createTextNode(txt);
//                Node node = targetNode.getChild(position);
//                targetNode.insertBefore(newTextNode, node);
//            } else if (Node.TEXT_NODE == targetNodeType) {
//                Text.as(targetNode).insertData(position, txt);
//            }
//        } else if (operation instanceof TreeDeleteText) {
//            TreeDeleteText deleteText = (TreeDeleteText) operation;
//            if (Node.TEXT_NODE == targetNodeType) {
//                Text textNode = Text.as(targetNode);
//                textNode.deleteData(position, 1); //delete 1 char
//            }
//        } else if (operation instanceof TreeInsertParagraph) {
//            TreeInsertParagraph treeInsertParagraph = (TreeInsertParagraph) operation;
//            // cases
//            //1 hit enter on empty text area
//            final Element p = Document.get().createElement("p");
//            if (nativeNode.getChildCount() == 0) {
//                targetNode.insertFirst(p);
//            } else if (nativeNode.getChildCount() == 1 && nativeNode.getFirstChild().getNodeName().equalsIgnoreCase("br")) {
//                log.info("3");
//                nativeNode.replaceChild(nativeNode.getFirstChild(), p);
//                //2 hit enter on first line
//            } else if (position == 0) {
//                //2.1 enter at the start of the text
//                // pull down all lines below
//                p.appendChild(Document.get().createBRElement());
//                Node parentNode = targetNode.getParentElement();
//                if (path.length == 1 && path[0] == 0) {
//                    parentNode.insertBefore(p, targetNode);
//                } else {
//                    parentNode.getParentNode().insertBefore(p, parentNode);
//                }
//            } else {
//                log.info("5");
//                //split the line, assume the targetNode is text
//                String actualText = targetNode.getNodeValue();
//                //2.2 enter in the middle of the text
//                //2.3 enter at the end of the text
//                //position < actualText.length()
//                Text textNode = Text.as(targetNode);
//                textNode.deleteData(0, position);
//
//                p.setInnerText(actualText.substring(0, position));
//
//                Node parentElement = targetNode.getParentElement();
//                parentElement.getParentElement().insertBefore(p, parentElement);
//            }
//            //3 hit enter in between , not first line
//            //4 hit enter at the end , nfl
//
//            //Get the actual text node
//            //first remove from the textnode what was after caret position
//        } else if (operation instanceof TreeNewParagraph) {
//            TreeNewParagraph treeNewParagraph = (TreeNewParagraph) operation;
//            //assume position == 0
//            final Element p = Document.get().createElement("p");
//            p.appendChild(Document.get().createBRElement());
//
//            Node parentNode = targetNode.getParentElement();
//            if (path.length == 1 && path[0] == 0) {
//                parentNode.insertBefore(p, targetNode);
//            } else {
//                parentNode.getParentNode().insertBefore(p, parentNode);
//            }
//        } else if (operation instanceof TreeMergeParagraph) {
//            Node p = targetNode.getParentElement();
//            Node pPreviousSibling = p.getPreviousSibling();
//
//            targetNode.removeFromParent();
//            p.removeFromParent();
//
//            Node oldTextNode = pPreviousSibling.getLastChild();
//            Text newTextNode = Document.get().createTextNode(oldTextNode.getNodeValue() + targetNode.getNodeValue());
//            pPreviousSibling.replaceChild(newTextNode, oldTextNode);
//        } else if (operation instanceof TreeStyle) {
//            TreeStyle style = (TreeStyle) operation;
//            Node parentElement = targetNode.getParentElement();
//            Element styleElement = DOM.createElement(style.param);
//            styleElement.appendChild(targetNode);
//            parentElement.replaceChild(styleElement, targetNode);
//        }
        log.fine("Root is after: " + root);

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
        if (node.getNodeName().equalsIgnoreCase("p") && node.getChildCount() == 0) {
            node.appendChild(Document.get().createBRElement());
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            insertBrInEmptyParagraphs(children.getItem(i));
        }
    }
}