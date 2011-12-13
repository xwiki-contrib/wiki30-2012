package fr.loria.score.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import fr.loria.score.jupiter.JupiterAlg;
import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.plain.operation.Operation; //todo: AbstractOp
import fr.loria.score.jupiter.transform.Transformation;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Client side implementation for Jupiter Algorithm
 */
public class ClientJupiterAlg extends JupiterAlg {
    public static final int REFRESH_INTERVAL = 2000;

    private static final Logger logger = Logger.getLogger(ClientJupiterAlg.class.getName());
    private CommunicationServiceAsync comService;
    private Editor editor;

    private ClientCallback callback;

    private int editingSessionId;

    public ClientJupiterAlg() {}

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

    public void setCommunicationService(CommunicationServiceAsync comService) {
        this.comService = comService;
    }

	public void setCallback(ClientCallback callback) {
        this.callback = callback;
    }

    public void connect() {
//        Create the corresponding server component for this client on the server side AND update the editor with the available content
        comService.initClient(new ClientDTO(this), new AsyncCallback<ClientDTO>() {

            @Override
            public void onFailure(Throwable throwable) {
                logger.severe("Failed to connect client to server. Reason: " + throwable);
            }

            @Override
            public void onSuccess(ClientDTO clientDTO) {
                logger.fine("Successfully connected to server. DTO is: " + clientDTO);
                siteId = clientDTO.getSiteId();
                document = clientDTO.getDocument();

                callback.onConnected();

                serverPushForClient();
            }
        });
    }

    public void disconnect() {
        ClientDTO dto = new ClientDTO(this);
        comService.removeServerPairForClient(dto, new AsyncCallback<Void>() {
            public void onFailure(Throwable throwable) {
                logger.severe("Could not remove server pair for client. Error: " + throwable.getMessage());
            }

            public void onSuccess(Void aVoid) {
                logger.finest("Successfully removed server pair for client");
            }
        });
        callback.onDisconnected();   // todo: fix NPE
    }

    @Override
    protected void execute(Message receivedMsg) {
        logger.info("Executing message: " + receivedMsg);
        callback.onExecute(receivedMsg);
    }

    protected void send(Message m) {
        logger.fine(this + "\t Client sends to server: " + m);

        m.setEditingSessionId(this.editingSessionId);

        comService.serverReceive(m, new AsyncCallback<Void>() {
            public void onSuccess(Void result) {
                logger.finest("Got OK from server");
            }

            public void onFailure(Throwable caught) {
                logger.severe("Error sending message to server: " + caught);
                logger.severe("Cause:" + caught.getMessage());
            }
        });
    }

    /**
     * Simulate the server-push via simple polling
     */
    private void serverPushForClient() {

        final Timer timer = new Timer() {
            @Override
            public void run() {
                comService.clientReceive(getSiteId(), new AsyncCallback<Message[]>() {
                    public void onSuccess(Message[] messages) {
                        logger.finest("Receive server sent messages: " + Arrays.asList(messages));
                        if (messages.length > 0) {
                            for (int i = 0; i < messages.length; i++) {
                                Message message = messages[i];
                                receive(message);
                            }
                        }
                    }

                    public void onFailure(Throwable caught) {
                        logger.severe("Error: " + caught);
                        if (caught instanceof StatusCodeException && ((StatusCodeException)caught).getStatusCode() == 0) {
                            // timer.cancel();
                        }
                    }
                });
            }
        };
        timer.scheduleRepeating(REFRESH_INTERVAL);
    }

    public interface ClientCallback {
        void onConnected();
        void onDisconnected();
        void onExecute(Message receivedMessage);
    }

    /**
     * Callback for plain text documents, wiki editor
     */
    public class PlainClientCallback implements ClientCallback {
        @Override
        public void onConnected() {
                //update UI
                editor.setContent(document.getContent());
                editor.setSiteId(siteId);
                editor.paint();
        }

        @Override
        public void onDisconnected() {}

        @Override
        public void onExecute(Message receivedMessage) {
            int oldCaretPos = editor.getCaretPosition(); // the caret position in the editor's old document model
            editor.setOldCaretPos(oldCaretPos);

            Operation operation = (Operation)receivedMessage.getOperation();
            operation.beforeUpdateUI(editor); // the highlighter code here

            editor.setContent(getDocument().getContent()); // sets the WHOLE text

            if (ClientJupiterAlg.this.siteId != operation.getSiteId()) {    //CJA.this.siteId == editor.siteId
                operation.afterUpdateUI(editor);
            }

            editor.paint(); //cursor.focus() paints it, but it might occur that the page is not focused and thus not updated
        }
    }

    /**
     * Callback for tree documents, wysiwyg editor
     */
    public class TreeClientCallback implements ClientCallback {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected() {
        }

        @Override
        public void onExecute(Message receivedMessage) {
            TreeOperation operation = (TreeOperation) receivedMessage.getOperation();

        }
    }
}