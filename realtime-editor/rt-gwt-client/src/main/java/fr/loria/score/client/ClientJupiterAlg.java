package fr.loria.score.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import fr.loria.score.jupiter.JupiterAlg;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.transform.Transformation;

/**
 * Client side implementation for Jupiter Algorithm
 */
public class ClientJupiterAlg extends JupiterAlg {

    public ClientJupiterAlg() {
    }

    public ClientJupiterAlg(String initialData, int siteId) {
        super(initialData, siteId);
    }

    public ClientJupiterAlg(String initialData, int siteId, Transformation transform) {
        super(initialData, siteId, transform);
    }

    @Override
    protected void execute(Message receivedMsg) {
        // do nothing
    }

    protected void send(Message m) {
        GWT.log(this + "\t Client sends to server: " + m);
        AsyncCallback<Void> callback = new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                GWT.log("Error:" + caught);
            }

            public void onSuccess(Void result) {
                GWT.log("Got OK from server");
            }
        };

        CommunicationService.ServiceHelper.getCommunicationService().serverReceive(m, callback);
    }
}
