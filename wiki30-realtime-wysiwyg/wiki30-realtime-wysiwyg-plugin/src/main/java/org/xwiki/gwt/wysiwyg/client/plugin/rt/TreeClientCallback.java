package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import fr.loria.score.client.ClientCallback;
import fr.loria.score.client.ClientDTO;
import fr.loria.score.client.Converter;
import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.tree.TreeDocument;
import fr.loria.score.jupiter.tree.operation.*;

/**
 * Callback for tree documents, wysiwyg editor
 */
public class TreeClientCallback implements ClientCallback {
    private Node nativeNode;

    public TreeClientCallback(Node nativeNode) {
        this.nativeNode = nativeNode;
    }

    @Override
    public void onConnected(ClientDTO dto, Document document, boolean updateUI) {
        if (updateUI) {
            log.finest("Updating UI for WYSIWYG. Replacing native node: " + Element.as(nativeNode).getString());
            Node newNode = Converter.fromCustomToNative(((TreeDocument) document).getRoot());
            nativeNode.getParentNode().replaceChild(newNode, nativeNode);
            nativeNode = newNode;
            log.finest("New native node: " + Element.as(nativeNode).getString());
        }
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onExecute(Message receivedMessage) {
        log.fine("Executing received: " + receivedMessage);
        log.fine("Native node is before: " + Element.as(nativeNode).getString());
        TreeOperation operation = (TreeOperation) receivedMessage.getOperation();
        int position = operation.getPosition();

        final Node targetNode = TreeHelper.getChildNodeFromLocator(nativeNode, operation.getPath());

        if (operation instanceof TreeInsertText) {
            //operates on a text node
            TreeInsertText insertText = (TreeInsertText) operation;
            String txt = String.valueOf(insertText.getText());

            if (targetNode == nativeNode) {
                Node newTextNode = com.google.gwt.dom.client.Document.get().createTextNode(txt);
                //some browsers insert a br element on an empty text area, so remove it
                Node brElement = nativeNode.getChild(0);
                if (brElement != null) {
                    nativeNode.replaceChild(newTextNode, brElement);
                } else {
                    nativeNode.appendChild(newTextNode);
                }
            } else {
                ((Text)targetNode).insertData(position, txt);
            }
        } else if (operation instanceof TreeDeleteText) {
            TreeDeleteText deleteText = (TreeDeleteText) operation;

            Text textNode = (Text) targetNode;
            textNode.deleteData(position, 1); //delete 1 char
        } else if (operation instanceof TreeInsertParagraph) {
            TreeInsertParagraph treeInsertParagraph = (TreeInsertParagraph) operation;

            //Get the actual text node
            //first remove from the textnode what was after caret position
            String actualText = targetNode.getNodeValue();
            targetNode.setNodeValue(actualText.substring(0, position));

            //then insert new node
            Node n = targetNode.getParentElement();
            log.fine("Parent node: " + Element.as(n).getString());
            Element p = DOM.createElement("p");
            p.setInnerText(actualText.substring(position));
            n.getParentElement().insertAfter(p, n);
        } else if (operation instanceof TreeMergeParagraph) {
            Node p = targetNode.getParentElement();
            Node pPreviousSibling = p.getPreviousSibling();

            targetNode.removeFromParent();
            p.removeFromParent();

            Node oldTextNode = pPreviousSibling.getChild(0);
            Text newTextNode = com.google.gwt.dom.client.Document.get().createTextNode(oldTextNode.getNodeValue() + targetNode.getNodeValue());
            pPreviousSibling.replaceChild(newTextNode, oldTextNode);
        }
        log.fine("Native node is after: " + Element.as(nativeNode).getString());
    }
}