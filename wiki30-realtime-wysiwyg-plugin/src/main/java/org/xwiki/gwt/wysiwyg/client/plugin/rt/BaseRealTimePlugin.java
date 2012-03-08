package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import java.util.logging.Logger;

import org.xwiki.gwt.user.client.Config;
import org.xwiki.gwt.user.client.Console;
import org.xwiki.gwt.user.client.ui.rta.RichTextArea;
import org.xwiki.gwt.wysiwyg.client.plugin.internal.AbstractStatefulPlugin;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.client.RtApi;
import fr.loria.score.jupiter.tree.TreeDocument;

/**
 * Base class for real time plugins
 * @author Bogdan.Flueras@inria.fr
 */
public class BaseRealTimePlugin extends AbstractStatefulPlugin
{
    public static final String P = "p";

    public static final String SPAN = "span";

    public static final String BR = "br";

    protected static ClientJupiterAlg clientJupiter;

    /**
     * {@code true} if this client is already connected, {@code false} otherwise
     */
    private static boolean isInitialized;

    private static Logger log = Logger.getLogger(BaseRealTimePlugin.class.getName());

    /**
     * The object used to create tree operations.
     */
    protected TreeOperationFactory treeOperationFactory = new TreeOperationFactory();

    @Override public void update()
    {
    }

    @Override public void init(RichTextArea textArea, Config config)
    {
        super.init(textArea, config);
        Console.getInstance().log("Loading Base RT Plugin... ");

        if (!isInitialized) {
            initJupiterClient(textArea, config);
            isInitialized = true;
            customizeActionListeners();
        }
    }

    private static void initJupiterClient(RichTextArea textArea, Config config)
    {
        Console.getInstance().log("Initializing Jupiter client");

        // Jupiter algo initializing
        Node bodyNode = textArea.getDocument().getBody();
        // insert a new paragraph on an empty text area
        final Element p = Document.get().createElement(P);
        if (bodyNode.getChildCount() == 0) {
            bodyNode.insertFirst(p);
        } else if (bodyNode.getChildCount() == 1 && bodyNode.getFirstChild().getNodeName().equalsIgnoreCase(BR)) {
            bodyNode.insertBefore(p, bodyNode.getFirstChild());
        }

        clientJupiter = new ClientJupiterAlg(new TreeDocument(Converter.fromNativeToCustom(Element.as(bodyNode))));
        clientJupiter.setEditingSessionId(Integer.parseInt(config.getParameter(RtApi.DOCUMENT_ID)));
        clientJupiter.setCommunicationService(CommunicationService.ServiceHelper.getCommunicationService());
        clientJupiter.setCallback(new TreeClientCallback(bodyNode));
        clientJupiter.connect();
        Console.getInstance().log("Jupiter client initialized");
    }

    /**
     * Customize the action listeners found in actionButtonsRT.js
     */
    protected static native void customizeActionListeners() /*-{
        $wnd.onCancelHook = function()
        {
            @org.xwiki.gwt.wysiwyg.client.plugin.rt.BaseRealTimePlugin::disconnect()();
        };

        $wnd.onSaveAndViewHook = function()
        {
            @org.xwiki.gwt.wysiwyg.client.plugin.rt.BaseRealTimePlugin::disconnect()();
        };

        $wnd.onSaveAndContinueHook = function()
        {
            @org.xwiki.gwt.wysiwyg.client.plugin.rt.BaseRealTimePlugin::disconnect()();
        }
    }-*/;

    private static void disconnect()
    {
        clientJupiter.disconnect();
    }
}
