package fr.loria.score.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fr.loria.score.jupiter.JupiterAlg;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.model.Operation;
import fr.loria.score.jupiter.transform.Transformation;

/**
 * Client side implementation for Jupiter Algorithm
 */
public class ClientJupiterAlg extends JupiterAlg {
    private int editingSessionId;

    protected transient Editor editor;

    
    public ClientJupiterAlg() {
    }

    public ClientJupiterAlg(String initialData, int siteId) {
        super(initialData, siteId);
    }

    public ClientJupiterAlg(String initialData, int siteId, Transformation transform) {
        super(initialData, siteId, transform);
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
    
    @Override
    protected void execute(Message receivedMsg) {
        int oldCaretPos = editor.getCaretPosition(); // the caret position in the editor's old data model
        editor.setOldCaretPos(oldCaretPos);

        editor.setContent(getData()); // sets the WHOLE text

        Operation operation = receivedMsg.getOperation();
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
        GWT.log(this + "\t Client sends to server: " + m);
        AsyncCallback<Void> callback = new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                GWT.log("Error sending message to server: " + caught);
            }

            public void onSuccess(Void result) {
                GWT.log("Got OK from server");
            }
        };

        CommunicationService.ServiceHelper.getCommunicationService().serverReceive(m, callback);
    }
}
