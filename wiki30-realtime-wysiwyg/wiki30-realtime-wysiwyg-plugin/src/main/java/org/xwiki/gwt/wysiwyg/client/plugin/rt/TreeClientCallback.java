package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import com.google.gwt.dom.client.*;
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
        log.fine("Executing received: " + receivedMessage);
        TreeOperation operation = (TreeOperation) receivedMessage.getOperation();

        final int position = operation.getPosition();
        final int[] path = operation.getPath();
        final Node targetNode = TreeHelper.getChildNodeFromLocator(nativeNode, path);
        final short targetNodeType = targetNode.getNodeType();

        if (operation instanceof TreeInsertText) {
            //operates on a text node
            TreeInsertText insertText = (TreeInsertText) operation;
            String txt = String.valueOf(insertText.getText());

            if (Node.ELEMENT_NODE == targetNodeType) {
                Node newTextNode = Document.get().createTextNode(txt);
                Node node = targetNode.getChild(position);
                targetNode.insertBefore(newTextNode, node);
            } else if (Node.TEXT_NODE == targetNodeType) {
                Text.as(targetNode).insertData(position, txt);
            }
        }
        else if (operation instanceof TreeDeleteText) {
            TreeDeleteText deleteText = (TreeDeleteText) operation;
            if (Node.TEXT_NODE == targetNodeType) {
                Text textNode = Text.as(targetNode);
                textNode.deleteData(position, 1); //delete 1 char
            } else if (Node.ELEMENT_NODE == targetNodeType) {
                // there is no delete text op generated on elements yet
            }
        }
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
        else {
            log.warning("Update all DOM");
            log.finest("Root is before: " + customNode);
            updateDOM();
            log.finest("Root is after: " + customNode);
        }
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