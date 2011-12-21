package fr.loria.score.client;

import com.google.gwt.dom.client.Node;
import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.plain.operation.Operation;
import fr.loria.score.jupiter.tree.TreeUtils;
import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeOperation;
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

    void onConnected(ClientDTO dto, Document document);
    void onDisconnected();
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
        public void onConnected(ClientDTO dto, Document document) {
            log.fine("Successfully connected:" + dto);
            this.document = document;

            final int siteId = dto.getSiteId();
            editor.setSiteId(siteId);

            //update UI
            editor.setContent(dto.getDocument().getContent());
            editor.paint();
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
        private Node root;

        public TreeClientCallback(Node root) {
            this.root = root;
        }

        @Override
        public void onConnected(ClientDTO dto, Document document) {
        }

        @Override
        public void onDisconnected() {
        }

        @Override
        public void onExecute(Message receivedMessage) {   // todo: generify
            log.fine("Executing received: " + receivedMessage);
            TreeOperation operation = (TreeOperation) receivedMessage.getOperation();

            if (operation instanceof TreeInsertText) {
                //operates on a text node
                TreeInsertText tit = (TreeInsertText) operation;

                Mutation mutation = new Mutation();
                mutation.setType(Mutation.MutationType.INSERT);
                mutation.setLocator(TreeUtils.getStringLocatorFromPath(tit.getPath()));
                mutation.setValue(String.valueOf(tit.getPosition()) + "," + tit.getText());

                MutationOperator operator = new DefaultMutationOperator();
                operator.operate(mutation, root);
                log.info("Applied mutation: " + mutation + " on node: " + root.toString());
            }
        }
    }
}
