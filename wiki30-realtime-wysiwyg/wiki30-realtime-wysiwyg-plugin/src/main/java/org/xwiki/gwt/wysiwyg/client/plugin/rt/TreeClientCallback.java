package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Text;
import fr.loria.score.client.ClientCallback;
import fr.loria.score.client.ClientDTO;
import fr.loria.score.client.Converter;
import fr.loria.score.jupiter.model.AbstractOperation;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeDocument;
import fr.loria.score.jupiter.tree.operation.*;
import org.xwiki.gwt.dom.client.DOMUtils;
import org.xwiki.gwt.dom.client.Document;
import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.dom.client.Selection;

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

            //set the selection on first paragraph
            org.xwiki.gwt.dom.client.Document nativeOwnerDocument = (org.xwiki.gwt.dom.client.Document)nativeNode.getOwnerDocument();
            Selection selection = nativeOwnerDocument.getSelection();
            Range caret = nativeOwnerDocument.createRange();

            Node firstLeaf = DOMUtils.getInstance().getFirstLeaf(nativeNode);
            log.fine("First leaf" + firstLeaf.getNodeName() + "," + firstLeaf.getNodeValue());
            log.fine("First leaf parent node" + firstLeaf.getParentElement().getInnerHTML());

             // is either text node or it's an element which can have children: span, strong, em, p
            if (DOMUtils.getInstance().canHaveChildren(firstLeaf) || Node.TEXT_NODE == firstLeaf.getNodeType()) {
                log.fine("Can have children");
                caret.setStart(firstLeaf, 0);
            } else {
                caret.setStartBefore(firstLeaf);
            }
            caret.collapse(true);

            selection.removeAllRanges();
            selection.addRange(caret);
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
        else if (operation instanceof TreeInsertParagraph) {
            log.info("Tree insert paragraph: native node is: " + Element.as(nativeNode).getInnerHTML());
            // Todo: make a facade, an abstract factory so that this code is the same as for TreeInsParagraph
            TreeInsertParagraph treeInsertParagraph = (TreeInsertParagraph) operation;
            final Element newParagraph = Document.get().createElement("p");
            if (Node.TEXT_NODE == targetNodeType) {
                Text textNode = Text.as(targetNode);

                int d = 1;
                Node rootNode = nativeNode;
                if (path.length > 0) {
                    rootNode = rootNode.getChild(path[0]);
                    Node tNode = newParagraph;
                    for (int i = 0; i < path.length - 1; i++) {
                        Node r;
                        while ((r = rootNode.getChild(path[i + 1] + 1)) != null) {
                            log.fine("Removing node: " + r.getNodeValue() +", " + r.getNodeName());
                            if (!"br".equalsIgnoreCase(r.getNodeName())) {
                                r.removeFromParent();
                            }
                            log.fine(">> Tree is:" + Element.as(rootNode).getInnerHTML());
                            tNode.appendChild(r);
                            log.fine(">> tNode is: " +Element.as(tNode).getInnerHTML());
                        }
                        log.fine("Tree is: " + Element.as(rootNode).getInnerHTML());
                        log.fine("tTree is: " + Element.as(tNode).getInnerHTML());

                        if (i != path.length - 2) {
                            tNode.insertFirst(rootNode.getChild(path[i + 1]));
                            tNode = tNode.getChild(0);
                            rootNode = rootNode.getChild(path[i + 1]);
                        } else {
                            if (!treeInsertParagraph.splitLeft) {
                                log.fine("split left false");
                                Node n = rootNode.getChild(path[i + 1]);
                                tNode.insertFirst(n);
                                log.fine("tNode is: " + Element.as(n).getInnerHTML());
                                n.removeFromParent();
                                log.fine("n is: " + Element.as(n).getInnerHTML());
                                int j = 0;
                                while ((i + 1 - j != 0) && (path[i + 1 - j] == 0)) {
                                    rootNode = rootNode.getParentNode();
                                    Node n1 = rootNode.getChild(path[i - j]);
                                    rootNode.removeChild(n1);
                                    j = j + 1;
                                }
                                if (i + 1 - j == 0) {
                                    d = 0;
                                }
                            } else {
                                log.fine("Splitting...");
                                rootNode = rootNode.getChild(path[i + 1]); // actually the textNode

                                Text newTextNode = textNode.splitText(position);
                                log.fine("New textNode is:" + newTextNode.getData());
                                log.fine("TextNode is:" + textNode.getData());

                                tNode.insertFirst(newTextNode);
                                log.fine("tTree is: " + Element.as(tNode).getInnerHTML());
                            }
                        }
                    }

                    log.info("New Paragraph is:" + Element.as(newParagraph).getInnerHTML());
                    nativeNode.insertBefore(newParagraph, nativeNode.getChild(path[0] + d));
                    log.info("Native node is after:" + Element.as(nativeNode).getInnerHTML());
                } else {  // path.len == 0
                    nativeNode.insertFirst(newParagraph);
                }
            } else if (Element.ELEMENT_NODE == targetNodeType) {
                log.severe("Not yet handled on element nodes");
            }
        } else if (operation instanceof TreeNewParagraph) {
            final Element p = Document.get().createElement("p");
            p.addClassName("newParagraph");
            p.appendChild(Document.get().createTextNode(""));
            p.appendChild(Document.get().createBRElement());

            nativeNode.insertBefore(p, nativeNode.getChild(position));
        }
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
        //todo:  set the selection
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