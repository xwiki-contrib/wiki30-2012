package fr.loria.score.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fr.loria.score.jupiter.JupiterAlg;
import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.plain.operation.Operation; //todo: AbstractOp
import fr.loria.score.jupiter.transform.Transformation;

import java.util.logging.Logger;

/**
 * Client side implementation for Jupiter Algorithm
 */
public class ClientJupiterAlg extends JupiterAlg {
    private final static transient Logger logger = Logger.getLogger(ClientJupiterAlg.class.getName());

    private int editingSessionId;

    protected transient Editor editor;

    public ClientJupiterAlg() {
    }

    public ClientJupiterAlg(Document initialDocument, int siteId) {
        super(siteId, initialDocument);
    }

    public ClientJupiterAlg(Document initialDocument, int siteId, int editingSessionId) {
        this(initialDocument, siteId);
        this.editingSessionId = editingSessionId;
    }

    public ClientJupiterAlg(Document initialData, int siteId, Transformation transform) {
        super(siteId, initialData, transform);
    }

    public void setEditingSessionId(int editingSessionId) {
        this.editingSessionId = editingSessionId;
    }

    public int getEditingSessionId() {
        return editingSessionId;
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public void quitEditingSession() {
        ClientDTO dto = new ClientDTO(this);
        CommunicationService.ServiceHelper.getCommunicationService().removeServerPairForClient(dto, new AsyncCallback<Void>() {
            public void onFailure(Throwable throwable) {
                logger.severe("Could not remove server pair for client. Error: " + throwable.getMessage());
            }

            public void onSuccess(Void aVoid) {
                logger.finest("Successfully removed server pair for client");
            }
        });
    }

    @Override
    protected void execute(Message receivedMsg) {
        //todo: this works only for plain messages for now
        int oldCaretPos = editor.getCaretPosition(); // the caret position in the editor's old document model
        editor.setOldCaretPos(oldCaretPos);

        editor.setContent(getDocument().getContent()); // sets the WHOLE text

        Operation operation = (Operation)receivedMsg.getOperation();
        if (this.siteId != operation.getSiteId()) { // remote operation
            //shift left/right the caret
            if (operation.getPosition() < oldCaretPos) {
                operation.updateUI(editor);
            }
        }

        editor.paint();
    }

    protected void send(Message m) {
        m.setEditingSessionId(this.editingSessionId);
        logger.info(this + "\t Client sends to server: " + m);
        CommunicationService.ServiceHelper.getCommunicationService().serverReceive(m, new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                logger.severe("Error sending message to server: " + caught);
            }

            public void onSuccess(Void result) {
                logger.finest("Got OK from server");
            }
        });
    }
}
