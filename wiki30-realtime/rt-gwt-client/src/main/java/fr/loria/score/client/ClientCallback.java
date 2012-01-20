package fr.loria.score.client;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.plain.operation.Operation;
import fr.loria.score.jupiter.tree.TreeDocument;
import fr.loria.score.jupiter.tree.TreeUtils;
import fr.loria.score.jupiter.tree.operation.*;
import org.xwiki.gwt.dom.mutation.client.DefaultMutationOperator;
import org.xwiki.gwt.dom.mutation.client.Mutation;
import org.xwiki.gwt.dom.mutation.client.MutationOperator;

import java.util.logging.Logger;

/**
 * Callback which performs different actions: initializing, updating UI aso
 * @author Bogdan.Flueras@inria.fr
 */
public interface ClientCallback {
    static final Logger log = Logger.getLogger(ClientCallback.class.getName());

    /**
     * Executed after the client is connected to server
     * @param dto the DTO replied by the server used to initialize and update different things on client
     * @param document the document
     * @param updateUI if <code>true</code> syncs the view according to the new received document content
     */
    void onConnected(ClientDTO dto, Document document, boolean updateUI);

    /**
     * Executed when client is disconnected from server
     */
    void onDisconnected();

    /**
     * Executed each time the client receives a new message from the server, <strong>after</strong> the original message was transformed
     * @param receivedMessage the transformed message
     */
    void onExecute(Message receivedMessage);


    /**
     * Callback for plain text documents, wiki editor
     */
    public class PlainClientCallback implements ClientCallback {
        private Editor editor;
        private Document document;

        public PlainClientCallback(Editor editor) {
            this.editor = editor;
        }

        @Override
        public void onConnected(ClientDTO dto, Document document, boolean updateUI) {
            this.document = document;

            final int siteId = dto.getSiteId();
            editor.setSiteId(siteId);

            if (updateUI) {
                editor.setContent(dto.getDocument().getContent());
                editor.paint();
            }
        }

        @Override
        public void onDisconnected() {}

        @Override
        public void onExecute(Message receivedMessage) {
            log.fine("Executing received: " + receivedMessage);
            int oldCaretPos = editor.getCaretPosition(); // the caret position in the editor's old document model
            editor.setOldCaretPos(oldCaretPos);

            Operation operation = (Operation)receivedMessage.getOperation();
            operation.beforeUpdateUI(editor); // the highlighter code here

            editor.setContent(document.getContent()); // sets the WHOLE text

            if (editor.getSiteId() != operation.getSiteId()) {
                operation.afterUpdateUI(editor);
            }

            editor.paint(); //cursor.focus() paints it, but it might occur that the page is not focused and thus not updated
        }
    }

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
                Node newNode = Converter.fromCustomToNative(((TreeDocument)document).getRoot());
                nativeNode.getParentNode().replaceChild(newNode, nativeNode);
                nativeNode = newNode;
                log.finest("New native node: " + Element.as(nativeNode).getString());
            }
        }

        @Override
        public void onDisconnected() {}

        @Override
        public void onExecute(Message receivedMessage) {   // todo: generify
            log.fine("Executing received: " + receivedMessage);
            log.fine("Native node is before: " + Element.as(nativeNode).getString());
            TreeOperation operation = (TreeOperation) receivedMessage.getOperation();
            int position = operation.getPosition();

            Mutation mutation = new Mutation();
            final String locator = TreeUtils.getStringLocatorFromPath(operation.getPath());
            mutation.setLocator(locator);

            MutationOperator operator = new DefaultMutationOperator();

            if (operation instanceof TreeInsertText) {
                //operates on a text node
                TreeInsertText tit = (TreeInsertText) operation;

                mutation.setType(Mutation.MutationType.INSERT);
                mutation.setValue(String.valueOf(position) + "," + tit.getText()); // insert 1 char

                operator.operate(mutation, nativeNode);
            } else if (operation instanceof TreeDeleteText) {
                TreeDeleteText treeDeleteText = (TreeDeleteText) operation;

                mutation.setType(Mutation.MutationType.REMOVE);
                mutation.setValue(String.valueOf(position) + "," + String.valueOf(position + 1)); // delete 1 char

                operator.operate(mutation, nativeNode);
            } else if (operation instanceof TreeInsertParagraph) {
                TreeInsertParagraph treeInsertParagraph = (TreeInsertParagraph) operation;

                //Get the actual text node
                //first remove from the textnode what was after caret position
                Node textNode = DefaultMutationOperator.getChildNodeFromLocator(nativeNode, locator);
                String actualText = textNode.getNodeValue();
                textNode.setNodeValue(actualText.substring(0, position));

                //then insert new node
                Node n = textNode.getParentElement();
                log.fine("Parent node: " + Element.as(n).getString());
                Element p = DOM.createElement("p");
                p.setInnerText(actualText.substring(position));
                n.getParentElement().insertAfter(p, n);
            } else if (operation instanceof TreeMergeParagraph) {
                Node textNode = DefaultMutationOperator.getChildNodeFromLocator(nativeNode, locator);
                Node p = textNode.getParentElement();
                Node pPreviousSibling = p.getPreviousSibling();

                textNode.removeFromParent();
                p.removeFromParent();

                Node oldTextNode = pPreviousSibling.getChild(0);
                Text newTextNode = com.google.gwt.dom.client.Document.get().createTextNode(oldTextNode.getNodeValue() + textNode.getNodeValue());
                pPreviousSibling.replaceChild(newTextNode, oldTextNode);
            }
            log.fine("Applied mutation: " + mutation);
            log.fine("Native node is after: " + Element.as(nativeNode).getString());
        }
    }
}
