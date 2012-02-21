/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.xwiki.gwt.dom.client.DOMUtils;
import org.xwiki.gwt.dom.client.Property;
import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.dom.client.Selection;
import org.xwiki.gwt.dom.client.Style;
import org.xwiki.gwt.user.client.Config;
import org.xwiki.gwt.user.client.ui.rta.RichTextArea;
import org.xwiki.gwt.user.client.ui.rta.cmd.Command;
import org.xwiki.gwt.user.client.ui.rta.cmd.CommandListener;
import org.xwiki.gwt.user.client.ui.rta.cmd.CommandManager;
import org.xwiki.gwt.wysiwyg.client.Images;
import org.xwiki.gwt.wysiwyg.client.Strings;
import org.xwiki.gwt.wysiwyg.client.plugin.internal.AbstractStatefulPlugin;
import org.xwiki.gwt.wysiwyg.client.plugin.internal.FocusWidgetUIExtension;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.client.RtApi;
import fr.loria.score.jupiter.tree.TreeDocument;
import fr.loria.score.jupiter.tree.operation.TreeDeleteText;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeMergeParagraph;
import fr.loria.score.jupiter.tree.operation.TreeNewParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;
import fr.loria.score.jupiter.tree.operation.TreeStyle;

/**
 * Broadcasts DOM mutations generated inside the rich text area.
 * It overrides nearly all plugin-based features of WYSIWYG: line, text aso.
 * 
 * @version $Id: 4e19fb82c1f5869f4850b80c3b5f5d3b3d319483 $
 */
public class RealTimePlugin extends AbstractStatefulPlugin implements KeyDownHandler, KeyPressHandler, KeyUpHandler, CommandListener, ClickHandler
{
    private static Logger log = Logger.getLogger(RealTimePlugin.class.getName());
    private static final String BR = "br";
    private static ClientJupiterAlg clientJupiter;

    /**
     * The object used to create tree operations.
     */
    private TreeOperationFactory treeOperationFactory = new TreeOperationFactory();

    /**
     * The list of command that shouldn't be broadcasted.
     */
    private static final List<Command> IGNORED_COMMANDS = Arrays.asList(Command.UPDATE, Command.ENABLE, new Command(
            "submit"));

    /**
     * The association between tool bar buttons and the commands that are executed when these buttons are clicked.
     */
    private final Map<ToggleButton, Command> buttons = new HashMap<ToggleButton, Command>();

    /**
     * User interface extension for the editor tool bar.
     */
    private final FocusWidgetUIExtension toolBarExtension = new FocusWidgetUIExtension("toolbar");

    /**
     * Associates commands with style properties. Useful for toggling on/off the local and remote styling
     */
    private final Map<Command, Property> commandStyleProperties = new HashMap<Command, Property>();

    /**
     * {@inheritDoc}
     * 
     * @see AbstractStatefulPlugin#init(RichTextArea, Config)
     */
    public void init(RichTextArea textArea, Config config)
    {
        super.init(textArea, config);

        saveRegistration(textArea.addKeyDownHandler(this));
        saveRegistration(textArea.addKeyPressHandler(this));
        saveRegistration(textArea.addKeyUpHandler(this));

        getTextArea().getCommandManager().addCommandListener(this);

        commandStyleProperties.put(Command.BOLD, Style.FONT_WEIGHT);
        commandStyleProperties.put(Command.ITALIC, Style.FONT_STYLE);
        commandStyleProperties.put(Command.UNDERLINE, Style.TEXT_DECORATION);
        commandStyleProperties.put(Command.LINE_THROUGH, Style.TEXT_DECORATION);

        // register the styling buttons and their actions
        addFeature("bold", Command.BOLD, Images.INSTANCE.bold(), Strings.INSTANCE.bold());
        addFeature("italic", Command.ITALIC, Images.INSTANCE.italic(), Strings.INSTANCE.italic());
        addFeature("underline", Command.UNDERLINE, Images.INSTANCE.underline(), Strings.INSTANCE.underline());
        addFeature("strikethrough", Command.LINE_THROUGH, Images.INSTANCE.strikeThrough(), Strings.INSTANCE.strikeThrough());

        if (toolBarExtension.getFeatures().length > 0) {
            registerTextAreaHandlers();
            getUIExtensionList().add(toolBarExtension);
        }

        // Jupiter algo initializing
        Node bodyNode = textArea.getDocument().getBody();
        // insert a new paragraph on an empty text area
        final Element p = Document.get().createElement("p");
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

        customizeActionListeners();
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractStatefulPlugin#destroy()
     */
    public void destroy()
    {
        getTextArea().getCommandManager().removeCommandListener(this);

        super.destroy();
    }

    /**
     * {@inheritDoc}
     * 
     * @see CommandListener#onBeforeCommand(CommandManager, Command, String)
     */
    public boolean onBeforeCommand(CommandManager sender, Command command, String param)
    {
        if (getTextArea().isAttached() && getTextArea().isEnabled() && !IGNORED_COMMANDS.contains(command)) {
            Selection selection = getTextArea().getDocument().getSelection();
            if (selection.getRangeCount() > 0) {

                final Property styleProperty = commandStyleProperties.get(command);
                String styleKey = styleProperty.getCSSName();
                String styleValue = getTextArea().getCommandManager().isExecuted(command) ? styleProperty.getDefaultValue() : command.toString();

                //Use this range to get all intermediary paths
                Range range = selection.getRangeAt(0);

                List<OperationTarget> targets = getIntermediaryTargets(range);
                log.info(targets.toString());

                for (OperationTarget target : targets) {
                    log.finest("Generate tree style op for :" + target.toString() + ", key: " + styleKey + ", val: " +
                        styleValue);
                    boolean addStyle = false;
                    int[] path = TreeHelper.toIntArray(target.getStartContainer());
                    if (path.length == 2) {
                        addStyle = true;
                    }

                    boolean splitLeft = true;
                    int start = target.getStartOffset();
                    if (start == 0) {
                        splitLeft = false;
                    }

                    boolean splitRight = true;
                    int end = target.getEndOffset();
                    if (end == target.getDataLength()) {
                        splitRight = false;
                    }
                    TreeOperation op = new TreeStyle(clientJupiter.getSiteId(), path, start, end, styleKey, styleValue, addStyle, splitLeft, splitRight);
                    clientJupiter.generate(op);
                }
                // Block the command because it's already handled in DomStyle operation.
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see CommandListener#onCommand(CommandManager, Command, String)
     */
    public void onCommand(CommandManager sender, final Command command, final String param) {}

    @Override
    public void onClick(ClickEvent event) {
        Command command = buttons.get(event.getSource());
        // We have to test if the text area is attached because this method can be called after the event was consumed.
        if (command != null && getTextArea().isAttached() && ((FocusWidget) event.getSource()).isEnabled()) {
            getTextArea().setFocus(true);
            getTextArea().getCommandManager().execute(command);
        }
    }

    @Override
    public void update() {
       for (Map.Entry<ToggleButton, Command> entry : buttons.entrySet()) {
            if (entry.getKey().isEnabled()) {
                entry.getKey().setDown(getTextArea().getCommandManager().isExecuted(entry.getValue()));
            }
        }
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        final int keyCode = event.getNativeKeyCode();
        Selection selection = getTextArea().getDocument().getSelection();
        if (selection.getRangeCount() > 0) {
            Range range = selection.getRangeAt(0);
            logRange(range);

            int pos = -1;
            Node startContainer = range.getStartContainer();
            Node endContainer = range.getEndContainer();

            List<Integer> path = TreeHelper.getLocator(range.getStartContainer());

            TreeOperation op = null;
            switch (keyCode) {
                case KeyCodes.KEY_BACKSPACE: {
                    pos = range.getStartOffset();

                    if (Node.TEXT_NODE == startContainer.getNodeType()) {
                        Text textNode = Text.as(startContainer);

                        // Go up below the parent paragraph node, because we might have span tags with text nodes
                        Node n = getAncestorBelowParagraph(textNode);
                        Node rightParagraph = n.getParentElement();
                        Node leftParagraph = rightParagraph.getPreviousSibling();

                        if (pos == 0) {
                            if(leftParagraph != null) {
                                boolean merge = false;
                                // merge iff textNode is the first child
                                if (textNode != n && textNode == n.getFirstChild()) {
                                    merge = true;
                                }
                                if (textNode == n && n == rightParagraph.getFirstChild()) {
                                    merge = true;
                                }
                                if (merge) {
                                    //definitively a line merge
                                    op = new TreeMergeParagraph(clientJupiter.getSiteId(), path.get(0), leftParagraph.getChildCount(), rightParagraph.getChildCount());
                                    op.setPath(TreeHelper.toIntArray(path));
                                } else {
                                    log.severe("Backspace on text node: to define merge on text nodes!");
                                    event.preventDefault();
                                }
                            } else {
                                log.fine("Backspace on text node: Left paragraph is null, nothing to be done.");
                                event.preventDefault();
                            }
                        } else {
                            pos = pos - 1;
                            op = new TreeDeleteText(clientJupiter.getSiteId(), pos, TreeHelper.toIntArray(path));
                        }
                    } else if (Node.ELEMENT_NODE == startContainer.getNodeType()) {
                        Element element = Element.as(startContainer);
                        Node n = getAncestorBelowParagraph(element);
                        Node rightParagraph = n.getParentNode();
                        Node leftParagraph = rightParagraph.getPreviousSibling();

                        if (pos == 0 && "span".equalsIgnoreCase(n.getNodeName())) { // assume the element is a span
                            if (leftParagraph != null) {
                                boolean merge = false;
                                // merge iff element is the first child
                                if (element == n && n == rightParagraph.getFirstChild()) {
                                    merge = true;
                                }
                                if (merge) {
                                    //definitively a line merge
                                    op = new TreeMergeParagraph(clientJupiter.getSiteId(), path.get(0), leftParagraph.getChildCount(), rightParagraph.getChildCount());
                                    op.setPath(TreeHelper.toIntArray(path));
                                } else {
                                    log.severe("Backspace on element node: to define merge on element nodes!");
                                    event.preventDefault();
                                }
                            } else {
                                log.fine("Backspace on element node: Left paragraph is null, nothing to be done.");
                                // prevent default, otherwise it would remove the paragraph element
                                event.preventDefault();
                            }
                        }
                    }
                }
                break;

                case KeyCodes.KEY_DELETE: {
                    if (Node.TEXT_NODE == startContainer.getNodeType()) {
                        Text textNode = Text.as(startContainer);
                        pos = range.getStartOffset();
                        if (textNode.getLength() == pos) { // perhaps a line merge
                            Element sibling = textNode.getParentElement().getNextSiblingElement();
                            if ((sibling != null) && (!sibling.getClassName().toLowerCase().contains("firebug"))) {
                                //line merge only if there is something to merge: the text node's parent has siblings
                                path.set(0, path.get(0) + 1);
                                op = new TreeMergeParagraph(clientJupiter.getSiteId(), path.get(0), 1, 1);
                                op.setPath(TreeHelper.toIntArray(path));
                            }
                        } else {
                            op = new TreeDeleteText(clientJupiter.getSiteId(), pos, TreeHelper.toIntArray(path));
                        }
                    } else if (Node.ELEMENT_NODE == startContainer.getNodeType()) {
                        if (startContainer.getNextSibling() != null) {
                            path.set(0, path.get(0) + 1);
                            op = new TreeMergeParagraph(clientJupiter.getSiteId(), path.get(0), 1, 1);
                            op.setPath(TreeHelper.toIntArray(path));
                        }
                    }
                }
                break;

                case KeyCodes.KEY_ENTER: {
                    path = TreeHelper.getLocator(range.getEndContainer());
                    pos = range.getEndOffset();

                    if (Node.TEXT_NODE == endContainer.getNodeType()) {
                        Text textNode = Text.as(endContainer);

                        boolean isNewParagraph = false;

                        // Go up below the parent paragraph node, because we might have span tags with text nodes
                        Node n = getAncestorBelowParagraph(textNode);

                        // Start of the line
                        if (n.getPreviousSibling() == null && 0 == pos) {
                            isNewParagraph = true;
                            pos = path.get(0);
                        }

                        // End of line
                        if ((n.getNextSibling() == null || BR.equalsIgnoreCase(n.getNextSibling().getNodeName())) && textNode.getLength() == pos) {
                            isNewParagraph = true;
                            pos = path.get(0) + 1;
                        }
                        if (isNewParagraph) {
                            op = new TreeNewParagraph(clientJupiter.getSiteId(), pos);
                            op.setPath(TreeHelper.toIntArray(path));
                        } else {
                            op = new TreeInsertParagraph(clientJupiter.getSiteId(), pos, TreeHelper.toIntArray(path));
                        }
                    } else if (Node.ELEMENT_NODE == endContainer.getNodeType()) {
                        Element element = Element.as(endContainer);

                        // Start of the line
                        if (element.getPreviousSibling() == null && 0 == pos) {
                            op = new TreeNewParagraph(clientJupiter.getSiteId(), path.get(0));
                            op.setPath(TreeHelper.toIntArray(path));
                        } else {
                            int brCount = element.getElementsByTagName(BR).getLength();
                            int childCount = element.getChildCount();
                            boolean isBeforeLastBrTag = ((pos == (childCount - brCount)) && (BR.equalsIgnoreCase(element.getLastChild().getNodeName())));
                            boolean isAfterLastTag = (pos == childCount);
                            // End of the line
                            if (isBeforeLastBrTag || isAfterLastTag) {
                                pos = path.get(0) + 1;
                                op = new TreeNewParagraph(clientJupiter.getSiteId(), pos);
                                op.setPath(TreeHelper.toIntArray(path));
                            } else {
                                // Somewhere in the middle of the line
                                pos = range.getEndOffset();
                                op = new TreeInsertParagraph(clientJupiter.getSiteId(), pos, TreeHelper.toIntArray(path));
                            }
                        }
                    }
                }
                break;

                default:
                break;
            }

            if (op != null) {
                if (!(op instanceof TreeInsertText || op instanceof TreeDeleteText)) {
                    // Prevent the default behavior because the DOM tree will be synchronized with the Tree model.
                    event.preventDefault();
                }

                clientJupiter.generate(op);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see KeyPressHandler#onKeyPress(KeyPressEvent)
     */
    public void onKeyPress(KeyPressEvent event)
    {
        log.finest("onKeyPress: " + getTextArea().getHTML());
        boolean isAltControlOrMetaDown = event.isAltKeyDown() || event.isControlKeyDown() || event.isMetaKeyDown();
        boolean isNoteworthyKeyPressed = event.getCharCode() != '\u0000';

        if (getTextArea().isAttached() && getTextArea().isEnabled() && !isAltControlOrMetaDown && isNoteworthyKeyPressed) {
            Selection selection = getTextArea().getDocument().getSelection();
            if (selection.getRangeCount() > 0) {
                Range range = selection.getRangeAt(0);
                logRange(range);

                char character = new String(new int[] {event.getUnicodeCharCode()}, 0, 1).charAt(0);
                clientJupiter.generate(treeOperationFactory.createTreeInsertText(clientJupiter.getSiteId(), range,
                    character));
            }
        }
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        //for now nothing
    }

    /**
     * Customize the action listeners found in actionButtonsRT.js
     */
    private static native void customizeActionListeners() /*-{
        $wnd.onCancelHook = function() {
            @org.xwiki.gwt.wysiwyg.client.plugin.rt.RealTimePlugin::disconnect()();
        };

        $wnd.onSaveAndViewHook= function() {
            @org.xwiki.gwt.wysiwyg.client.plugin.rt.RealTimePlugin::disconnect()();
        };

        $wnd.onSaveAndContinueHook = function() {
            @org.xwiki.gwt.wysiwyg.client.plugin.rt.RealTimePlugin::disconnect()();
        }
    }-*/;

    private static void disconnect() {
        clientJupiter.disconnect();
    }

    private void logRange(Range r) {
        log.info("Start container: " + r.getStartContainer().getNodeName() +
                ", " + " locator: " + TreeHelper.getLocator(r.getStartContainer()) + " offset: " + r.getStartOffset()
                );

        log.info("End container: " + r.getEndContainer().getNodeName() +
                ", " + " locator: " + TreeHelper.getLocator(r.getStartContainer()) + " offset: " + r.getEndOffset()
                );
    }

    /**
     * Creates a tool bar feature and adds it to the tool bar.
     *
     * @param name the feature name
     * @param command the rich text area command that is executed by this feature
     * @param imageResource the image displayed on the tool bar
     * @param title the tool tip used on the tool bar button
     * @return the tool bar button that exposes this feature
     */
    private ToggleButton addFeature(String name, Command command, ImageResource imageResource, String title)
    {
        ToggleButton button = null;
        if (getTextArea().getCommandManager().isSupported(command)) {
            button = new ToggleButton(new Image(imageResource));
            saveRegistration(button.addClickHandler(this));
            button.setTitle(title);
            toolBarExtension.addFeature(name, button);
            buttons.put(button, command);
        }
        return button;
    }

     /**
     * Converts a DOM range to an list of operation targets.
     *
     * @param range a DOM range
     * @return the corresponding list of operation targets
     */
    private List<OperationTarget> getIntermediaryTargets(Range range) {
        // Iterate through all the text nodes within the given range and extract the operation target
        List<OperationTarget> operationTargets = new ArrayList<OperationTarget>();
        // Create the intermediary targets backwards because if we preserve the normal order when we modify the tree,
        // the following targets will no longer reflect that
        List<Text> textNodes = getNonEmptyTextNodes(range);
        for (int i = 0; i < textNodes.size(); i++) {
            Text text = textNodes.get(i);
            int startIndex = 0;
            if (text == range.getStartContainer()) {
                startIndex = range.getStartOffset();
            }
            int endIndex = text.getLength();
            if (text == range.getEndContainer()) {
                endIndex = range.getEndOffset();
            }
            operationTargets.add(0, new OperationTarget(TreeHelper.getLocator(text), startIndex, endIndex, text.getLength()));
        }
        return operationTargets;
    }

     /**
     * @param range a DOM range
     * @return the list of non empty text nodes that are completely or partially (at least one character) included in
     *         the given range
     */
    protected List<Text> getNonEmptyTextNodes(Range range) {
        Node leaf = DOMUtils.getInstance().getFirstLeaf(range);
        Node lastLeaf = DOMUtils.getInstance().getLastLeaf(range);
        List<Text> textNodes = new ArrayList<Text>();
        // If the range starts at the end of a text node we have to ignore that node.
        if (isNonEmptyTextNode(leaf)
                && (leaf != range.getStartContainer() || range.getStartOffset() < leaf.getNodeValue().length())) {
            textNodes.add((Text) leaf);
        }
        while (leaf != lastLeaf) {
            leaf = DOMUtils.getInstance().getNextLeaf(leaf);
            if (isNonEmptyTextNode(leaf)) {
                textNodes.add((Text) leaf);
            }
        }
        // If the range ends at the start of a text node then we have to ignore that node.
        int lastIndex = textNodes.size() - 1;
        if (lastIndex >= 0 && range.getEndOffset() == 0 && textNodes.get(lastIndex) == range.getEndContainer()) {
            textNodes.remove(lastIndex);
        }
        return textNodes;
    }

    /**
     * @param node a DOM node
     * @return {@code true} if the given node is of type {@link Node#TEXT_NODE} and it's not empty, {@code false}
     *         otherwise
     */
    private boolean isNonEmptyTextNode(Node node) {
        return node.getNodeType() == Node.TEXT_NODE && node.getNodeValue().length() > 0;
    }

    /**
     * @param node the node whose ancestor is to be returned
     * @return the ancestor for this node which is just below the paragraph ancestor
     */
    private Node getAncestorBelowParagraph(Node node) {
        Node ancestor = node;
        while (!"p".equalsIgnoreCase(ancestor.getParentNode().getNodeName())) {
            ancestor = ancestor.getParentNode();
        }
        return ancestor;
    }
}