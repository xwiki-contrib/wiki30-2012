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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

import static org.xwiki.gwt.wysiwyg.client.plugin.rt.EditorUtils.getAncestorBelowParagraph;
import static org.xwiki.gwt.wysiwyg.client.plugin.rt.EditorUtils.getAncestorParagraph;
import static org.xwiki.gwt.wysiwyg.client.plugin.rt.EditorUtils.getIntermediaryTargets;
import static org.xwiki.gwt.wysiwyg.client.plugin.rt.EditorUtils.getNonEmptyTextNodes;

/**
 * Broadcasts DOM mutations generated inside the rich text area. It overrides nearly all plugin-based features of
 * WYSIWYG: line, text aso.
 *
 * @version $Id: 4e19fb82c1f5869f4850b80c3b5f5d3b3d319483 $
 */
public class RealTimePlugin extends AbstractStatefulPlugin
    implements KeyDownHandler, KeyPressHandler, KeyUpHandler, CommandListener, ClickHandler
{
    public static final String P = "p";

    public static final String SPAN = "span";

    public static final String BR = "br";

    private static Logger log = Logger.getLogger(RealTimePlugin.class.getName());

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
        addFeature("strikethrough", Command.LINE_THROUGH, Images.INSTANCE.strikeThrough(),
            Strings.INSTANCE.strikeThrough());

        if (toolBarExtension.getFeatures().length > 0) {
            registerTextAreaHandlers();
            getUIExtensionList().add(toolBarExtension);
        }

        // Jupiter algo initializing
        Node bodyNode = textArea.getDocument().getBody();
        // insert a new paragraph on an empty text area
        final Element p = Document.get().createElement(P); // todo: add empty text node and the caret stays there!
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
                String styleValue =
                    getTextArea().getCommandManager().isExecuted(command) ? styleProperty.getDefaultValue() :
                        command.toString();

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
                    TreeOperation op =
                        new TreeStyle(clientJupiter.getSiteId(), path, start, end, styleKey, styleValue, addStyle,
                            splitLeft, splitRight);
                    clientJupiter.generate(op);
                }
                // Block the command because it's already handled in DomStyle operation.
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(ClickEvent event)
    {
        Command command = buttons.get(event.getSource());
        // We have to test if the text area is attached because this method can be called after the event was consumed.
        if (command != null && getTextArea().isAttached() && ((FocusWidget) event.getSource()).isEnabled()) {
            getTextArea().setFocus(true);
            getTextArea().getCommandManager().execute(command);
        }
    }

    @Override
    public void update()
    {
        for (Map.Entry<ToggleButton, Command> entry : buttons.entrySet()) {
            if (entry.getKey().isEnabled()) {
                entry.getKey().setDown(getTextArea().getCommandManager().isExecuted(entry.getValue()));
            }
        }
    }

    @Override
    public void onKeyDown(KeyDownEvent event)
    {
        final int keyCode = event.getNativeKeyCode();
        Selection selection = getTextArea().getDocument().getSelection();
        if (selection.getRangeCount() > 0) {
            Range range = selection.getRangeAt(0);
            logRange(range);

            range = EditorUtils.computeNewCaretPosition(range);
            log.fine("New range");
            logRange(range);

            Node startContainer = range.getStartContainer();
            Node endContainer = range.getEndContainer();

            TreeOperation op = null;
            switch (keyCode) {
                case KeyCodes.KEY_BACKSPACE: {
                    if (Node.TEXT_NODE == startContainer.getNodeType()) {
                        op = handleBackspaceOnTextNode(range);
                    } else if (Node.ELEMENT_NODE == startContainer.getNodeType()) {
                        op = handleBackspaceOnElement(range);
                    }
                    if (op == null) {
                        event.preventDefault();
                    }
                }
                break;

                case KeyCodes.KEY_DELETE: {
                    if (Node.TEXT_NODE == startContainer.getNodeType()) {
                        op = handleDeleteOnTextNode(range);
                    } else if (Node.ELEMENT_NODE == startContainer.getNodeType()) {
                        op = handleDeleteOnElement(range);
                    }
                    if (op == null) {
                        event.preventDefault();
                    }
                }
                break;

                case KeyCodes.KEY_ENTER: {
                    if (Node.TEXT_NODE == endContainer.getNodeType()) {
                        op = handleEnterOnTextNode(range);
                    } else if (Node.ELEMENT_NODE == endContainer.getNodeType()) {
                        op = handleEnterOnElement(range);
                    }
                    if (op == null) {
                        event.preventDefault();
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

        if (getTextArea().isAttached() && getTextArea().isEnabled() && !isAltControlOrMetaDown &&
            isNoteworthyKeyPressed)
        {
            Selection selection = getTextArea().getDocument().getSelection();
            if (selection.getRangeCount() > 0) {
                Range range = selection.getRangeAt(0);
                range = EditorUtils.computeNewCaretPosition(range);

                log.fine("New range");
                logRange(range);

                char character = new String(new int[]{ event.getUnicodeCharCode() }, 0, 1).charAt(0);
                clientJupiter.generate(treeOperationFactory.createTreeInsertText(clientJupiter.getSiteId(), range,
                    character));

                event.preventDefault();
            }
        }
    }

    /**
     * Customize the action listeners found in actionButtonsRT.js
     */
    private static native void customizeActionListeners() /*-{
        $wnd.onCancelHook = function()
        {
            @org.xwiki.gwt.wysiwyg.client.plugin.rt.RealTimePlugin::disconnect()();
        };

        $wnd.onSaveAndViewHook = function()
        {
            @org.xwiki.gwt.wysiwyg.client.plugin.rt.RealTimePlugin::disconnect()();
        };

        $wnd.onSaveAndContinueHook = function()
        {
            @org.xwiki.gwt.wysiwyg.client.plugin.rt.RealTimePlugin::disconnect()();
        }
    }-*/;

    private static void disconnect()
    {
        clientJupiter.disconnect();
    }

    private void logRange(Range r)
    {
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

    public TreeOperation handleBackspaceOnElement(Range caret)
    {
        Node startContainer = caret.getStartContainer();
        List<Integer> path = TreeHelper.getLocator(startContainer);
        TreeOperation op = null;

        Element rightParagraph;
        Node leftParagraph;
        Element element = Element.as(startContainer);

        // Go up below the parent paragraph node, because we might have span tags with text nodes
        if (P.equalsIgnoreCase(element.getNodeName())) {
            rightParagraph = element;
            leftParagraph = rightParagraph.getPreviousSibling();

            if (leftParagraph != null) {
                //definitively a line merge
                int lBrCount = Element.as(leftParagraph).getElementsByTagName(BR).getLength();
                int rBrCount = rightParagraph.getElementsByTagName(BR).getLength();

                op = new TreeMergeParagraph(clientJupiter.getSiteId(), path.get(0),
                    leftParagraph.getChildCount() - lBrCount,
                    rightParagraph.getChildCount() - rBrCount);
                op.setPath(TreeHelper.toIntArray(path));
            } else {
                log.fine("Backspace on element: " + element.getTagName() + ", or above paragraph is null");
            }
        } else { // assume element is a span or other element contained in a paragraph
            rightParagraph = Element.as(getAncestorParagraph(element));
            op = skipBackspaceOnEmptyTexts(element, path, rightParagraph, rightParagraph.getPreviousSibling());
        }
        return op;
    }

    public TreeOperation handleBackspaceOnTextNode(Range caret)
    {
        int pos = caret.getStartOffset();
        Node startContainer = caret.getStartContainer();
        List<Integer> path = TreeHelper.getLocator(startContainer);
        TreeOperation op = null;

        Node ancestorBelowParagraph = getAncestorBelowParagraph(startContainer);
        Element rightParagraph = ancestorBelowParagraph.getParentElement();
        Node leftParagraph = rightParagraph
            .getPreviousSibling(); // Go up below the parent paragraph node, because we might have span tags with text nodes

        Text textNode = Text.as(startContainer);
        if (pos == 0) {
            if (leftParagraph != null) {
                boolean merge = false;
                // merge iff textNode is the first child
                if (textNode != ancestorBelowParagraph && textNode == ancestorBelowParagraph.getFirstChild()) {
                    merge = true;
                }
                if (textNode == ancestorBelowParagraph && ancestorBelowParagraph == rightParagraph.getFirstChild()) {
                    merge = true;
                }
                if (merge) {
                    //definitively a line merge
                    int lBrCount = Element.as(leftParagraph).getElementsByTagName(BR).getLength();
                    int rBrCount = rightParagraph.getElementsByTagName(BR).getLength();

                    op = new TreeMergeParagraph(clientJupiter.getSiteId(), path.get(0),
                        leftParagraph.getChildCount() - lBrCount,
                        rightParagraph.getChildCount() - rBrCount);
                    op.setPath(TreeHelper.toIntArray(path));
                } else {
                    op = skipBackspaceOnEmptyTexts(textNode, path, rightParagraph, leftParagraph);
                    // todo: can I extract above code in skipBackspaceOnEmptyTexts ?
                }
            } else {
                log.fine("Backspace on text node: Above paragraph is null, nothing to be done.");
            }
        } else {
            pos = pos - 1;
            op = new TreeDeleteText(clientJupiter.getSiteId(), pos, TreeHelper.toIntArray(path));
        }
        return op;
    }

    public TreeOperation handleEnterOnElement(Range caret)
    {
        int pos = caret.getEndOffset();
        Element element = Element.as(caret.getEndContainer());
        List<Integer> path = TreeHelper.getLocator(caret.getEndContainer());
        TreeOperation op = null;

        // Start of the line
        if (0 == pos) {
            op = new TreeNewParagraph(clientJupiter.getSiteId(), path.get(0));
            op.setPath(TreeHelper.toIntArray(path));
        } else {
            int brCount = element.getElementsByTagName(BR).getLength();
            int childCount = element.getChildCount();
            boolean isBeforeLastBrTag =
                ((pos == (childCount - brCount)) && (BR.equalsIgnoreCase(element.getLastChild().getNodeName())));
            boolean isAfterLastTag = (pos == childCount);
            // End of the line
            if (isBeforeLastBrTag || isAfterLastTag) {
                pos = path.get(0) + 1;
                op = new TreeNewParagraph(clientJupiter.getSiteId(), pos);
                op.setPath(TreeHelper.toIntArray(path));
            } else {
                // Position represents the n-th child of this element
                path.add(pos - 1);
                Node child = element.getChild(pos - 1); // //todo: child can be a span!
                if (child.getNodeType() == Node.TEXT_NODE) {
                    pos = child.getNodeValue().length();
                    op = new TreeInsertParagraph(clientJupiter.getSiteId(), pos, TreeHelper.toIntArray(path));
                } else {
                    log.severe("ENTER not handled!");
                }
            }
        }
        return op;
    }

    public TreeOperation handleEnterOnTextNode(Range caret)
    {
        int pos = caret.getEndOffset();
        Node container = caret.getEndContainer();
        Text textNode = Text.as(container);
        List<Integer> path = TreeHelper.getLocator(container);
        TreeOperation op;

        boolean isNewParagraph = false;

        // Go up below the parent paragraph node, because we might have span tags with text nodes
        Node ancestorBelowParagraph = getAncestorBelowParagraph(textNode);

        // Start of the line: textNode is the first child of it's ancestor (directly or indirectly)
        boolean isFirstChild = false;
        if (textNode != ancestorBelowParagraph) {
            isFirstChild = ancestorBelowParagraph == ancestorBelowParagraph.getParentElement().getFirstChild();
            isFirstChild = isFirstChild && (textNode == ancestorBelowParagraph.getFirstChild());
        } else {
            isFirstChild = textNode == ancestorBelowParagraph.getParentElement().getFirstChild();
        }
        isFirstChild = isFirstChild || (ancestorBelowParagraph.getPreviousSibling() == null);
        if (isFirstChild && 0 == pos)
        {
            isNewParagraph = true;
            pos = path.get(0);
        }

        // End of line: textNode is last child of it's ancestor (directly or indirectly)
        boolean isLastChild = false;
        if (textNode != ancestorBelowParagraph) {
            isLastChild = ancestorBelowParagraph == ancestorBelowParagraph.getParentElement().getLastChild();
            isLastChild = isLastChild && (textNode == ancestorBelowParagraph.getLastChild());
        } else {
            isLastChild = textNode == ancestorBelowParagraph.getParentNode().getLastChild();
        }
        isLastChild = isLastChild || (ancestorBelowParagraph.getNextSibling() == null || BR.equalsIgnoreCase(ancestorBelowParagraph.getNextSibling().getNodeName()));
        if (isLastChild && textNode.getLength() == pos)
        {
            isNewParagraph = true;
            pos = path.get(0) + 1;
        }

        if (isNewParagraph)
        {
            op = new TreeNewParagraph(clientJupiter.getSiteId(), pos);
            op.setPath(TreeHelper.toIntArray(path));
        } else {
            op = new TreeInsertParagraph(clientJupiter.getSiteId(), pos, TreeHelper.toIntArray(path));
        }
        return op;
    }

    public TreeOperation handleDeleteOnElement(Range caret)
    {
        Element element = Element.as(caret.getStartContainer());
        List<Integer> path = TreeHelper.getLocator(element);
        TreeOperation op = null;

        Element leftParagraph;
        Element rightParagraph; //todo: same scenario as for Backspace on span element...LUC

        // Go up below the parent paragraph node, because we might have span tags with text nodes
        if (P.equalsIgnoreCase(element.getNodeName())) {
            leftParagraph = element;
        } else { //assume element is a span or other element contained in a paragraph
            log.severe("It shouldn't happen but I'm handling it anyway!");
            leftParagraph = getAncestorBelowParagraph(element).getParentElement();
        }
        rightParagraph = leftParagraph.getNextSiblingElement();

        if (rightParagraph != null) {
            int lBrCount = leftParagraph.getElementsByTagName(BR).getLength();
            int rBrCount = rightParagraph.getElementsByTagName(BR).getLength();
            op = new TreeMergeParagraph(clientJupiter.getSiteId(), path.get(0) + 1,
                leftParagraph.getChildCount() - lBrCount,
                rightParagraph.getChildCount() - rBrCount);
            op.setPath(TreeHelper.toIntArray(path));
        } else {
            log.fine("Delete on element node: Below paragraph is null, nothing to be done.");
        }
        return op;
    }

    public TreeOperation handleDeleteOnTextNode(Range caret)
    {
        int pos = caret.getStartOffset();
        Node startContainer = caret.getStartContainer();
        Text textNode = Text.as(startContainer);
        List<Integer> path = TreeHelper.getLocator(startContainer);
        TreeOperation op = null;

        // Go up below the parent paragraph node, because we might have span tags with text nodes
        final Node ancestorParagraph = getAncestorParagraph(startContainer);
        Element leftParagraph = Element.as(ancestorParagraph);
        Element rightParagraph = leftParagraph.getNextSiblingElement();

        if (textNode.getLength() > 0) {
            if (textNode.getLength() == pos) { // perhaps a line merge
                op = skipDeleteOnEmptyTexts(textNode, path, leftParagraph, rightParagraph);
            } else {
                op = new TreeDeleteText(clientJupiter.getSiteId(), pos, TreeHelper.toIntArray(path));
            }
        } else {
            op = skipDeleteOnEmptyTexts(textNode, path, leftParagraph, rightParagraph);
        }
        return op;
    }

    /**
     * Skips empty texts on delete
     *
     * @param node the node
     * @param path the node's locator - passed only to avoid computing it again
     * @param leftParagraph the left paragraph which is the ancestor of the emptyText node
     * @param rightParagraph the right paragraph
     * @return a {@link TreeOperation} or {@code null}
     */
    private TreeOperation skipDeleteOnEmptyTexts(Node node, List<Integer> path, Node leftParagraph, Node rightParagraph)
    {
        return handleBackspaceOrDeleteKeyOnEmptyTexts(false, node, path, rightParagraph, leftParagraph);
    }

    /**
     * Skips empty texts on backspace
     *
     * @param node the node
     * @param path the node's locator - passed only to avoid computing it again
     * @param rightParagraph the right paragraph which is the ancestor of the emptyText node
     * @param leftParagraph the left paragraph
     * @return a {@link TreeOperation} or {@code null}
     */
    private TreeOperation skipBackspaceOnEmptyTexts(Node node, List<Integer> path, Node rightParagraph,
        Node leftParagraph)
    {
        return handleBackspaceOrDeleteKeyOnEmptyTexts(true, node, path, rightParagraph, leftParagraph) ;
    }

    private TreeOperation handleBackspaceOrDeleteKeyOnEmptyTexts(boolean isBackspace, Node node,
        List<Integer> path, Node rightParagraph, Node leftParagraph)
    {
        org.xwiki.gwt.dom.client.Document document = getTextArea().getDocument();
        Range range = document.createRange();
        if (isBackspace) {
            // select backward
            range.setStart(document.getBody().getFirstChild(), 0);
            range.setEndBefore(node);
        } else {
            // select forward
            range.setStartAfter(node);
            Node lastChild = document.getBody().getLastChild();
            range.setEnd(lastChild, lastChild.getChildCount());
        }

        TreeOperation op = null;

        List<Text> nonEmptyTextNodes = getNonEmptyTextNodes(range);
        if (nonEmptyTextNodes.size() > 0) {
            int idx = isBackspace ? nonEmptyTextNodes.size() - 1 : 0;
            Node nonEmptyTextNode = nonEmptyTextNodes.get(idx);

            log.fine("Non empty text node is: " + nonEmptyTextNode.getNodeValue());

            if (node.getParentNode() == nonEmptyTextNode.getParentNode()) {
                // nonEmptyTextNode is in the same paragraph as the node, so generate a delete text operation
                int deletePos = isBackspace ? nonEmptyTextNode.getNodeValue().length() - 1 : 0;
                op = new TreeDeleteText(clientJupiter.getSiteId(), deletePos, TreeHelper.toIntArray(path));
            } else {
                // nonEmptyTextNode is in different paragraph so generate a merge operation
                if((isBackspace && leftParagraph != null) || (!isBackspace && rightParagraph != null)) {
                    int lBbrCount = Element.as(leftParagraph).getElementsByTagName(BR).getLength();
                    int rBbrCount = Element.as(rightParagraph).getElementsByTagName(BR).getLength();
                    int mergePos = isBackspace ? path.get(0) : path.get(0) + 1;
                    op = new TreeMergeParagraph(clientJupiter.getSiteId(), mergePos,
                        leftParagraph.getChildCount() - lBbrCount,
                        rightParagraph.getChildCount() - rBbrCount);
                    op.setPath(TreeHelper.toIntArray(path));
                }
            }
        }
        return op;
    }
}