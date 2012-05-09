package fr.loria.score.client;

import java.util.logging.Logger;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;

import fr.loria.score.jupiter.JupiterAlg;
import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.transform.Transformation;

/**
 * Client side implementation for Jupiter Algorithm
 */
public class ClientJupiterAlg extends JupiterAlg {
    public static final int REFRESH_INTERVAL = 2000;

    private static final Logger logger = Logger.getLogger(ClientJupiterAlg.class.getName());
    private CommunicationServiceAsync comService;

    private ClientCallback callback;

    protected ClientJupiterAlg() {}

    public ClientJupiterAlg(Document document) {
        super(document);
    }

    public ClientJupiterAlg(Document document, int editingSessionId) {
        this(document);
        this.editingSessionId = editingSessionId;
    }

    public ClientJupiterAlg(Document document, Transformation transform) {
        super(document, transform);
    }

    public void setCommunicationService(CommunicationServiceAsync comService) {
        this.comService = comService;
    }

	public void setCallback(ClientCallback callback) {
        this.callback = callback;
    }

    /**
     * Connects to server where it creates the corresponding server component for this client on the server side<br>
     * AND if necessary updates the editor view with the newly received content
     */
    public void connect() {
        comService.initClient(new ClientDTO(this), new AsyncCallback<ClientDTO>() {

            @Override
            public void onFailure(Throwable throwable) {
                logger.severe("Failed to connect client to server. Reason: " + throwable);
            }

            @Override
            public void onSuccess(ClientDTO clientDTO) {
                logger.fine("Successfully connected to server. DTO is: " + clientDTO);
                siteId = clientDTO.getSiteId();

                boolean updateUI = !document.getContent().equals(clientDTO.getDocument().getContent());

                document = clientDTO.getDocument();

                callback.onConnected(clientDTO, document, updateUI); // hmm.. dto should be enough

                serverPushForClient();
            }
        });
    }

    /**
     * Disconnects this client from the server. It won't receive any real-time modifications to the document
     */
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
        callback.onDisconnected();
    }

    @Override
    protected void execute(Message receivedMsg) {
        logger.info("Executing message: " + receivedMsg);
        callback.afterReceive(receivedMsg);
    }

    protected void send(Message m) {
        m.setEditingSessionId(this.editingSessionId);
        m.setSiteId(this.siteId); // we've agreed that all operations are forced to have a siteId, but be defensive

        callback.beforeSend(m);

        logger.fine(this + "\t Client sends to server: " + m);

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
}