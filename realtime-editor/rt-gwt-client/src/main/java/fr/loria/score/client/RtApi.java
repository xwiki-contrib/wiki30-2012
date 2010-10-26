package fr.loria.score.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import fr.loria.score.jupiter.model.DeleteOperation;
import fr.loria.score.jupiter.model.InsertOperation;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.model.Operation;
import org.xwiki.gwt.dom.client.JavaScriptObject;
import org.xwiki.gwt.user.client.Config;
import org.xwiki.gwt.user.client.internal.DefaultConfig;


public class RtApi {
    public static int siteId = Random.nextInt(100);
    private static final int REFRESH_INTERVAL = 2000;

    private TextArea textArea;

    private CommunicationServiceAsync comService = CommunicationService.ServiceHelper.getCommunicationService();
    private ClientJupiterAlg clientJupiter;

    public RtApi(JavaScriptObject jsConfig) {
        Config config = new DefaultConfig(jsConfig);

        // Get the text area element
        Element hook = DOM.getElementById(config.getParameter("hookId"));
        if (hook == null) {
            return;
        }
        //todo bf:review!
        if (hook.getNodeName().equalsIgnoreCase("textarea")) {
            textArea = TextArea.wrap(hook);
            clientJupiter = new ClientJupiterAlg(textArea.getText(), siteId);
        }

        createServerPairForClient();

        //simulate the server-push via simple-polling
        receiveFromServer();

        textArea.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                int nativeKeyCode = event.getNativeKeyCode();
                if (nativeKeyCode != 0) {
                    Operation operation = null;
                    int position = textArea.getCursorPos();

                    switch (nativeKeyCode) {
                        case KeyCodes.KEY_UP:
                        case KeyCodes.KEY_DOWN:
                        case KeyCodes.KEY_LEFT:
                        case KeyCodes.KEY_RIGHT:
                        case KeyCodes.KEY_HOME:
                        case KeyCodes.KEY_END:
                        case KeyCodes.KEY_CTRL:// when user pressed CTRL key in any combination, its intent was not to edit the data, but rather to achieve some other OS functionality
                        case KeyCodes.KEY_ESCAPE:
                        case KeyCodes.KEY_PAGEDOWN:
                        case KeyCodes.KEY_PAGEUP:
                            break;

                        case KeyCodes.KEY_TAB: {
                            //when TAB is hit, by default it moves the focus to the next "focusable" element
                            //prevent default behaviour and stop bubbling
                            event.preventDefault();
                            event.stopPropagation();

                            //insert the TAB at the current cursor position
                            if (event.getSource() instanceof TextArea) {
                                String text = textArea.getText();
                                textArea.setText(text.substring(0, position) + "\t" + text.substring(position));
                                textArea.setCursorPos(position + 1);
                            }
                            operation = new InsertOperation(position, '\t', siteId);
                            break;
                        }
                        case KeyCodes.KEY_ENTER: {
                            operation = new InsertOperation(position, '\n', siteId);
                            break;
                        }
                        case KeyCodes.KEY_BACKSPACE: {
                            operation = new DeleteOperation(position - 1, siteId);
                            break;
                        }
                        case KeyCodes.KEY_DELETE: {
                            operation = new DeleteOperation(position, siteId);
                            break;
                        }
                    }
                    if (operation != null) {
                        clientJupiter.generate(operation);
                    }
                }
            }
        });

        // todo: study case when user holds the key pressed for long time- which is KP for
        textArea.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {

                Operation operation = null;
                char charCode = event.getCharCode();
                int position = textArea.getCursorPos();

                switch (charCode) {
                    case 0:
                        break;
                    default: {
                        operation = new InsertOperation(position, charCode, siteId);
                    }
                }
                // todo: handle mouse selection or shift selection delete and copy paste + key shortcuts!!
                if (operation != null) {
                    clientJupiter.generate(operation);
                }
            }
        });

    }

    /**
     * Sends every typed character to server. TODO: add buffering
     */
    private void sendToServer() {
        //todo:bf impl
    }

    /**
     * Simulate the server-push via simple-polling
     */
    private void receiveFromServer() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                clientReceive();
            }
        };
        timer.scheduleRepeating(REFRESH_INTERVAL);
    }

    private void createServerPairForClient() {
        //create the corresponding server component for this client on the server side AND update the text area with the available content
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                GWT.log("Fail to create server pair. Error: " + caught);
            }

            public void onSuccess(String result) {
                GWT.log("Created server pair with id: " + siteId);
                clientJupiter.setData(result);
                //todo:bf check if result not empty
                textArea.setText(result);
            }
        };
        comService.createServerPairForClient(clientJupiter, callback);
    }

    private void clientReceive() {
        AsyncCallback<Message[]> callback = new AsyncCallback<Message[]>() {
            public void onFailure(Throwable caught) {
                GWT.log("Error: " + caught);
            }

            public void onSuccess(Message[] messages) {
                if (messages.length > 0) {
                    for (int i = 0; i < messages.length; i++) {
                        Message message = messages[i];
                        clientJupiter.receive(message);
                    }
                    //update txtArea with transformed text
                    String clientData = clientJupiter.getData();
                    if (!textArea.getText().equals(clientData)) {
                        int oldCaretPos = textArea.getCursorPos(); // todo: put the cursor to its original position
                        textArea.setText(clientData);
                    }
                }
            }
        };
        comService.clientReceive(siteId, callback);
    }

    /**
     * Publishes the RT editor API.
     */
    public static native void publish()
        /*-{
          $wnd.RtApi = function(cfg) {
            if(typeof cfg == 'object') {
                this.instance = @fr.loria.score.client.RtApi::new(Lorg/xwiki/gwt/dom/client/JavaScriptObject;)(cfg);
            }
          }
        }-*/;
}
