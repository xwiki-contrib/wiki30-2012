package fr.loria.score.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import fr.loria.score.jupiter.model.DeleteOperation;
import fr.loria.score.jupiter.model.InsertOperation;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.model.Operation;
import org.xwiki.gwt.dom.client.JavaScriptObject;
import org.xwiki.gwt.user.client.Config;
import org.xwiki.gwt.user.client.internal.DefaultConfig;

import java.util.Arrays;

public class RtApi {
    private static final int REFRESH_INTERVAL = 2000;

    private Editor editor = Editor.getEditor();

    private CommunicationServiceAsync comService = CommunicationService.ServiceHelper.getCommunicationService();
    private ClientJupiterAlg clientJupiter = new ClientJupiterAlg("", Random.nextInt(100));


    /**
     * Publishes the RT editor API.
     */
    public static native void  publish()/*-{
          $wnd.RtApi = function(cfg) {
            if(typeof cfg == 'object') {
                this.instance = @fr.loria.score.client.RtApi::new(Lorg/xwiki/gwt/dom/client/JavaScriptObject;)(cfg);
            }
          }
    }-*/;

    public RtApi(JavaScriptObject jsConfig) {
        initClient();
        editor.addHooksToEventListeners(new EditorApi());

        Config config = new DefaultConfig(jsConfig);

        // Get the text area element
        Element hook = DOM.getElementById(config.getParameter("hookId"));
        if (hook == null) {
            return;
        }

        Node txtArea;
        if (hook.hasChildNodes()) {
            txtArea = hook.getChild(0);
        }

        if (hook.getNodeName().equalsIgnoreCase("textarea")) {
            //TODO: replace the text area with the canvas & set the canvas size
            int width = 500;
            int height = 210;
//            if (hook.hasAttribute("offsetHeight") && hook.hasAttribute("offsetWidth")) {
//                width = Integer.valueOf(hook.getAttribute("width"));
//                height = Integer.valueOf(hook.getAttribute("height"));
//            }

            TextArea tArea = TextArea.wrap(hook);
            height = tArea.getOffsetHeight();
            width = tArea.getOffsetWidth();

            Element canvasEl = DOM.createElement("canvas");
            canvasEl.setId(config.getParameter("hookId"));
            canvasEl.setInnerHTML(tArea.getText());
            canvasEl.setPropertyInt("width", width);
            canvasEl.setPropertyInt("height", height);

            com.google.gwt.dom.client.Element parentElem = hook.getParentElement();
            parentElem.replaceChild(hook, canvasEl);
        }
    }

    /**
     * Set the server generated id for this client
     */
    private void initClient() {
        comService.generateClientId(new AsyncCallback<Integer>() {
            public void onFailure(Throwable throwable) {
                //recover somehow, either throw e
                GWT.log("Failed to generate siteId, using local generated id. " + throwable);
            }

            public void onSuccess(Integer id) {
                GWT.log("Generated site id: " + id);
                clientJupiter.setSiteId(id);
                clientJupiter.setEditor(editor);

                clientJupiter.setEditingSessionId(0); // todo: set it by namespace, page, id
                createServerPairForClient();
                serverPushForClient();
            }
        });
    }

    /**
     * Create the corresponding server component for this client on the server side AND update the text area with the available content
     */
    private void createServerPairForClient() {
        comService.createServerPairForClient(clientJupiter, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                GWT.log("Fail to create server pair. Error: " + caught);
            }

            public void onSuccess(String result) {
                GWT.log("Created the server pair for this client");
                if (result != null) {
                    clientJupiter.setData(result);
                    //update UI
                    editor.setContent(result);
//                    editor.paint();
                }
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
                System.out.println(">> Server push for client: clientId = " + clientJupiter.getSiteId());
                comService.clientReceive(clientJupiter.getSiteId(), new AsyncCallback<Message[]>() {
                    public void onFailure(Throwable caught) {
                        GWT.log("Error: " + caught);
                    }

                    public void onSuccess(Message[] messages) {
                        GWT.log("Receive server sent messages: " + Arrays.asList(messages));
                        if (messages.length > 0) {
                            for (int i = 0; i < messages.length; i++) {
                                Message message = messages[i];
                                clientJupiter.receive(message);
                            }
                        }
                    }
                });
            }
        };

        timer.scheduleRepeating(REFRESH_INTERVAL);
    }

    private native void replaceTxtAreaWithCanvas() /*-{

    }-*/;

    //EDITOR API
    class EditorApi {
        /**
     * On insertion/deletion, the JavaScript editor generates an insert/delete operation which is then sent to server
     * @param s the inserted string(split in chars sequence)/character
     * @param position the insertion position
     */
        public void clientInsert(String s, int position) {
            if (s.length() > 1) {
                char [] charSeq = s.toCharArray();
                for (int i = 0; i < charSeq.length; i++) {
                    clientInsert(charSeq[i], position + i);
                }
            } else if (s.length() == 1){
              clientInsert(s.charAt(0), position);
            }
        }

        public void clientInsert(char c, int position) {
            Operation op = new InsertOperation(position, c, clientJupiter.getSiteId());
            clientJupiter.generate(op);
        }

        public void clientDelete(int pos) {
            Operation op = new DeleteOperation(pos, clientJupiter.getSiteId());
            clientJupiter.generate(op);
        }

        public void clientDelete(int from, int to) {
            for (int i = to - 1; i >= from; i--) { // from index is inclusive, to is exclusive, as the end selection idx is positioned at the next position
                clientDelete(i);
            }
        }
    }
}
