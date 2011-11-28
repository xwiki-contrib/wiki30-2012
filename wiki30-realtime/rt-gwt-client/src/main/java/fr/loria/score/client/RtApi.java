package fr.loria.score.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.plain.PlainDocument;
import fr.loria.score.jupiter.plain.operation.DeleteOperation;
import fr.loria.score.jupiter.plain.operation.InsertOperation;
import fr.loria.score.jupiter.plain.operation.Operation;
import org.xwiki.gwt.dom.client.JavaScriptObject;
import org.xwiki.gwt.user.client.Config;
import org.xwiki.gwt.user.client.internal.DefaultConfig;

import java.util.Arrays;
import java.util.logging.Logger;

public class RtApi {
    private static final int REFRESH_INTERVAL = 2000;
    private static final String DOCUMENT_ID = "documentId";

    private Editor editor;
    private JsBundle bundle = GWT.create(JsBundle.class);

    private CommunicationServiceAsync comService = CommunicationService.ServiceHelper.getCommunicationService();
    private ClientJupiterAlg clientJupiter = new ClientJupiterAlg(new PlainDocument(""), Random.nextInt(100));

    private static final Logger logger = Logger.getLogger(RtApi.class.getName());

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
        // and set the caret at pos 0
        Config config = new DefaultConfig(jsConfig);

        // Get the text area element
        Element htmlTextAreaElement = DOM.getElementById(config.getParameter("hookId"));
        if (htmlTextAreaElement == null) {
            return;
        }

        if (htmlTextAreaElement.getTagName().equalsIgnoreCase("textarea")) {
            int width = 500;
            int height = 210;

            TextArea tArea = TextArea.wrap(htmlTextAreaElement);
            height = tArea.getOffsetHeight();
            width = tArea.getOffsetWidth();

            Element canvasEl = DOM.createElement("canvas");
            canvasEl.setId("editor");
            canvasEl.setPropertyInt("width", width);
            canvasEl.setPropertyInt("height", height);

            com.google.gwt.dom.client.Element parentElem = htmlTextAreaElement.getParentElement();
            parentElem.insertFirst(canvasEl);
            parentElem.removeChild(htmlTextAreaElement);

            injectJSFilesForRTEditor(parentElem);
            clientJupiter.setDocument(new PlainDocument(tArea.getText()));
            clientJupiter.setEditingSessionId(Integer.valueOf(config.getParameter(DOCUMENT_ID)));
            initClient();
        }
    }

    private void injectJSFilesForRTEditor(com.google.gwt.dom.client.Element parentElem) {
        ScriptElement u1 = createScriptElement();
        u1.setText(bundle.jquery().getText());
        parentElem.appendChild(u1);

        ScriptElement u2 = createScriptElement();
        u2.setText(bundle.theme().getText());
        parentElem.appendChild(u2);

        ScriptElement u3 = createScriptElement();
        u3.setText(bundle.utils().getText());
        parentElem.appendChild(u3);

        ScriptElement u4 = createScriptElement();
        u4.setText(bundle.keys().getText());
        parentElem.appendChild(u4);

        ScriptElement u5 = createScriptElement();
        u5.setText(bundle.clipboard().getText());
        parentElem.appendChild(u5);

        ScriptElement u6 = createScriptElement();
        u6.setText(bundle.history().getText());
        parentElem.appendChild(u6);

        ScriptElement u7 = createScriptElement();
        u7.setText(bundle.cursor().getText());
        parentElem.appendChild(u7);

        ScriptElement u8 = createScriptElement();
        u8.setText(bundle.editor().getText());
        parentElem.appendChild(u8);

        ScriptElement u9 = createScriptElement();
        u9.setText(bundle.model().getText());
        parentElem.appendChild(u9);

        ScriptElement u10 = createScriptElement();
        u10.setText(bundle.model().getText());
        parentElem.appendChild(u10);

        ScriptElement u11 = createScriptElement();
        u11.setText(bundle.parser().getText());
        parentElem.appendChild(u11);

        ScriptElement u12 = createScriptElement();
        u12.setText(bundle.initEditor().getText());
        parentElem.appendChild(u12);
    }

    private static ScriptElement createScriptElement() {
        ScriptElement script = Document.get().createScriptElement();
        script.setAttribute("language", "javascript");
        return script;
      }

    /**
     * Set the server generated id for this client
     */
    private void initClient() {
        comService.generateClientId(new AsyncCallback<Integer>() {
            public void onFailure(Throwable throwable) {
                //recover somehow, either throw e
                logger.severe("Failed to generate siteId, using local generated id. " + throwable);
            }

            public void onSuccess(Integer id) {
                logger.finest("Generated site id: " + id);
                clientJupiter.setSiteId(id);

                createServerPairForClient();
                serverPushForClient();
            }
        });
    }

    /**
     * Create the corresponding server component for this client on the server side AND update the text area with the available content
     */
    private void createServerPairForClient() {
        comService.createServerPairForClient(new ClientDTO(clientJupiter), new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                logger.severe("Fail to create server pair. Error: " + caught);
            }

            public void onSuccess(String result) {
                logger.finest("Created the server pair for this client");
                if (result != null) {
                    clientJupiter.setDocument(new PlainDocument(result));
                    //update UI
                    editor = Editor.getEditor();
                    clientJupiter.setEditor(editor);

                    editor.addHooksToEventListeners(new EditorApi());
                    editor.setContent(result);
                    editor.paint();
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
                logger.fine(">> Server push for client: clientId = " + clientJupiter.getSiteId());
                comService.clientReceive(clientJupiter.getSiteId(), new AsyncCallback<Message[]>() {
                    public void onFailure(Throwable caught) {
                        logger.severe("Error: " + caught);
                    }

                    public void onSuccess(Message[] messages) {
                        logger.finest("Receive server sent messages: " + Arrays.asList(messages));
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
            Operation op = new InsertOperation(clientJupiter.getSiteId(), position, c);
            clientJupiter.generate(op);
        }

        public void clientDelete(int pos) {
            Operation op = new DeleteOperation(clientJupiter.getSiteId(), pos);
            clientJupiter.generate(op);
        }

        public void clientDelete(int from, int to) {
            for (int i = to - 1; i >= from; i--) { // from index is inclusive, to is exclusive, as the end selection idx is positioned at the next position
                clientDelete(i);
            }
        }

        public void clientQuitsEditingSession() {
            clientJupiter.quitEditingSession();
        }
    }

    interface JsBundle extends ClientBundle {
        @Source("jquery-1.4.3.min.js")
        TextResource jquery();

        @Source("theme.js")
        TextResource theme();

        @Source("utils.js")
        TextResource utils();

        @Source("keys.js")
        TextResource keys();

        @Source("clipboard.js")
        TextResource clipboard();

        @Source("history.js")
        TextResource history();

        @Source("cursor.js")
        TextResource cursor();

        @Source("editor.js")
        TextResource editor();

        @Source("model.js")
        TextResource model();

        @Source("parser.js")
        TextResource parser();

        @Source("init-editor.js")
        TextResource initEditor();
    }
}
