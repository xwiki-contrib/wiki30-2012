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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import com.google.gwt.event.dom.client.*;
import fr.loria.score.client.ClientJupiterAlg;
import fr.loria.score.client.CommunicationService;
import fr.loria.score.client.Converter;
import fr.loria.score.client.RtApi;
import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeDocument;
import fr.loria.score.jupiter.tree.operation.*;
import org.xwiki.gwt.dom.client.DOMUtils;
import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.dom.client.Selection;
import org.xwiki.gwt.user.client.Config;
import org.xwiki.gwt.user.client.ui.rta.RichTextArea;
import org.xwiki.gwt.user.client.ui.rta.cmd.Command;
import org.xwiki.gwt.user.client.ui.rta.cmd.CommandListener;
import org.xwiki.gwt.user.client.ui.rta.cmd.CommandManager;
import org.xwiki.gwt.wysiwyg.client.plugin.internal.AbstractPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Broadcasts DOM mutations generated inside the rich text area.
 * 
 * @version $Id: 4e19fb82c1f5869f4850b80c3b5f5d3b3d319483 $
 */
public class RealTimePlugin extends AbstractPlugin implements KeyDownHandler, KeyPressHandler, KeyUpHandler, CommandListener
{
    private static Logger log = Logger.getLogger(RealTimePlugin.class.getName());
    private static ClientJupiterAlg clientJupiter;

    /**
     * The list of command that shouldn't be broadcasted.
     */
    private static final List<Command> IGNORED_COMMANDS = Arrays.asList(Command.UPDATE, Command.ENABLE, new Command(
        "submit"));
    private static final String BR = "br";

    /**
     * The last operation call created from a rich text area command, before the command was executed. We don't support
     * nested commands because we can't distinguish between the case when a command is canceled (onCommand is not
     * called) and the case case when a command is nested (consecutive onBeforeCommand calls). We would have used a
     * stack otherwise.
     */
    private OperationCall commandOperationCall;

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPlugin#init(RichTextArea, Config)
     */
    public void init(RichTextArea textArea, Config config)
    {
        super.init(textArea, config);

//        saveRegistrations(new KeyboardHandler().addHandlers(textArea));
        saveRegistration(textArea.addKeyDownHandler(this));
        saveRegistration(textArea.addKeyPressHandler(this));
        saveRegistration(textArea.addKeyUpHandler(this));

        getTextArea().getCommandManager().addCommandListener(this);

        Node bodyNode = textArea.getDocument().getBody();

        // for Jupiter algo's correctness insert a new paragraph on an empty text area
        final Element p = Document.get().createElement("p");
        if (bodyNode.getChildCount() == 0) {
            bodyNode.insertFirst(p);
        } else if (bodyNode.getChildCount() == 1 && bodyNode.getFirstChild().getNodeName().equalsIgnoreCase(BR)) {
            bodyNode.insertBefore(p, bodyNode.getFirstChild());
        }

        Tree t = Converter.fromNativeToCustom(Element.as(bodyNode)); //tree should be returned from
        clientJupiter = new ClientJupiterAlg(new TreeDocument(t));
        clientJupiter.setEditingSessionId(Integer.parseInt(config.getParameter(RtApi.DOCUMENT_ID)));
        clientJupiter.setCommunicationService(CommunicationService.ServiceHelper.getCommunicationService());
        clientJupiter.setCallback(new TreeClientCallback(bodyNode));
        clientJupiter.connect();

        customizeActionListeners();
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPlugin#destroy()
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
        commandOperationCall = null;
        if (getTextArea().isAttached() && getTextArea().isEnabled() && !IGNORED_COMMANDS.contains(command)) {
            Selection selection = getTextArea().getDocument().getSelection();
            if (selection.getRangeCount() > 0) {
                String styleAttribute = "unsupported";
                if ("bold".equals(command.toString())) {
                    styleAttribute = "font-weight:bold";
                } else if ("italic".equals(command.toString())) {
                    styleAttribute = "font-style:italic";
                } else if ("underline".equals(command.toString())) {
                    styleAttribute = "text-decoration:underline";
                } else if ("strikethrough".equals(command.toString())) {
                    styleAttribute = "text-decoration:line-through";
                }

                //Use this range to get all intermediary paths
                Range range = selection.getRangeAt(0);
                List<OperationTarget> targets = getIntermediaryTargets(range);
                log.info(targets.toString());

                for (OperationTarget target : targets) {
                    int[] path = convertPath(target.getStartContainer());
                    boolean addStyle = false;
                    if (path.length == 2) {
                        addStyle = true;
                    }

                    int start = target.getStartOffset();
                    boolean splitLeft = true;
                    if (start == 0) {
                        splitLeft = false;
                    }

                    boolean splitRight = true;
                    int end = target.getEndOffset();
                    if (end == target.getDataLength()) {
                        splitRight = false;
                    }
                    //todo: detect when same style is depressed and change value to false
                    TreeOperation op = new TreeStyle(clientJupiter.getSiteId(), path, start, end, "style", styleAttribute, addStyle, splitLeft, splitRight);
                    clientJupiter.generate(op);

                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see CommandListener#onCommand(CommandManager, Command, String)
     */
    public void onCommand(CommandManager sender, final Command command, final String param)
    {
        if (commandOperationCall != null) {
        }
    }
    //todo: broadcast only if the caret was inside the RTA, not outside..
    @Override
    public void onKeyDown(KeyDownEvent event) {
        final int keyCode = event.getNativeKeyCode();
        log.fine("onKeyDown: " + keyCode + ", native evt.keyCode" + event.getNativeEvent().getKeyCode());
        Selection selection = getTextArea().getDocument().getSelection();
        if (selection.getRangeCount() > 0) {
            Range range = selection.getRangeAt(0);
            logRange(range);

            int pos = -1;
            Node startContainer = range.getStartContainer();
            Node endContainer = range.getEndContainer();

            List<Integer> path = getLocator(range.getStartContainer());
            //make case
            TreeOperation op = null;
            switch (keyCode) {
                case KeyCodes.KEY_BACKSPACE: {
                    pos = range.getStartOffset();
                    log.info("Position is: " + pos);

                    if (Node.TEXT_NODE == startContainer.getNodeType()) {
                        Text textNode = Text.as(startContainer);
                        if (pos == 0) { // perhaps a line merge
                            log.info("1");
                            if (textNode.getParentElement().getPreviousSibling() != null) { // todo: test it 8Feb12.eroare
                                log.info("1 - line merge");
                                //definitively a line merge
                                op = new TreeMergeParagraph(clientJupiter.getSiteId(), path.get(0), 1, 1);
                                op.setPath(convertPath(path));
                            } else {
                                log.info("2 - nothing");
                            }
                        } else {
                            log.info("3 - delete");
                            pos = pos - 1;
                            op = new TreeDeleteText(clientJupiter.getSiteId(), pos, convertPath(path));
                        }
                    } else if (Node.ELEMENT_NODE == startContainer.getNodeType()) {
                        if (pos == 0) {
                            if (startContainer.getPreviousSibling() != null) {
                                log.severe("Delete text on element, pos = 0, prev sibling not null");
                                // nothing for now
                            } else {
                                // nothing for now
                                log.severe("Delete text on element, pos = 0, prev sibling NULL");
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
                                op.setPath(convertPath(path));
                            }
                        } else {
                            op = new TreeDeleteText(clientJupiter.getSiteId(), pos, convertPath(path));
                        }
                    } else if (Node.ELEMENT_NODE == startContainer.getNodeType()) {
                        if (startContainer.getNextSibling() != null) {
                            path.set(0, path.get(0) + 1);
                            op = new TreeMergeParagraph(clientJupiter.getSiteId(), path.get(0), 1, 1);
                            op.setPath(convertPath(path));
                        }
                    }
                }
                break;

                case KeyCodes.KEY_ENTER: {
                    path = getLocator(range.getEndContainer());
                    pos = range.getEndOffset();

                    if (Node.TEXT_NODE == endContainer.getNodeType()) {
                        Text textNode = Text.as(endContainer);

                        boolean isNewParagraph = false;
                        if (textNode.getPreviousSibling() == null && 0 == pos) { // start of the text
                            isNewParagraph = true;
                            pos = path.get(0);
                        }
                        if ((textNode.getNextSibling() == null || BR.equalsIgnoreCase(textNode.getNextSibling().getNodeName())) && textNode.getLength() == pos) { // end of text
                            isNewParagraph = true;
                            pos = path.get(0) + 1;
                        }
                        if (isNewParagraph) {
                            op = new TreeNewParagraph(clientJupiter.getSiteId(), pos);
                            op.setPath(convertPath(path));
                        } else {
                            op = new TreeInsertParagraph(clientJupiter.getSiteId(), pos, convertPath(path));
                        }
                    } else if (Node.ELEMENT_NODE == endContainer.getNodeType()) {
                        Element element = Element.as(endContainer);
                        if (pos == 0) { //start of line
                            op = new TreeNewParagraph(clientJupiter.getSiteId(), path.get(0));
                            op.setPath(convertPath(path));
                        } else {
                            int brCount = element.getElementsByTagName(BR).getLength();
                            int childCount = element.getChildCount();
                            boolean isBeforeLastBrTag = ((pos == (childCount - brCount)) && (BR.equalsIgnoreCase(element.getLastChild().getNodeName())));
                            boolean isAfterLastTag = (pos == childCount);
                            if (isBeforeLastBrTag || isAfterLastTag) { //end of the line
                                pos = path.get(0) + 1;
                                op = new TreeNewParagraph(clientJupiter.getSiteId(), pos);
                                op.setPath(convertPath(path));
                            } else { // somewhere in the middle of the line
                                pos = range.getEndOffset();
                                op = new TreeInsertParagraph(clientJupiter.getSiteId(), pos, convertPath(path));
                            }
                        }
                    }
                }
                break;

                default:
                break;
            }

            if (op != null) {
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
        log.info("onKeyPress: " + getTextArea().getHTML());
        log.fine("onKeyPress: " + event.getCharCode() + ", native keyCode" + event.getNativeEvent().getKeyCode() + ", unicodeCharCode: " + event.getUnicodeCharCode());
        boolean isAltControlOrMetaDown = event.isAltKeyDown() || event.isControlKeyDown() || event.isMetaKeyDown();
        boolean isNoteworthyKeyPressed = event.getCharCode() != '\u0000';
        log.info("isNoteworthy:" + isNoteworthyKeyPressed);

        if (getTextArea().isAttached() && getTextArea().isEnabled() && !isAltControlOrMetaDown && isNoteworthyKeyPressed) {
            Selection selection = getTextArea().getDocument().getSelection();
            if (selection.getRangeCount() > 0) {
                Range range = selection.getRangeAt(0);
                logRange(range);

                List<Integer> path = getLocator(range.getStartContainer());
                clientJupiter.generate(new TreeInsertText(clientJupiter.getSiteId(), range.getStartOffset(), convertPath(path), new String(new int[]{event.getUnicodeCharCode()}, 0, 1).charAt(0)));
            }
        }
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        //for now nothing
    }

    /**
     * @param node a DOM node
     * @return a list of locator for the given node relative to the {@code BODY} element of the edited HTML document
     */
    private List<Integer> getLocator(Node node)
    {
        List<Integer> locator = new ArrayList<Integer>();
        Node ancestor = node;
        while (ancestor != null && ancestor != getTextArea().getDocument().getBody()) {
            locator.add(0, DOMUtils.getInstance().getNodeIndex(ancestor));
            ancestor = ancestor.getParentNode();
        }
        return locator;
    }

    private int[] convertPath(List<Integer> path) {
        int[] ppath = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            ppath[i] = path .get(i);
        }
        return ppath;
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
                ", " + " locator: " + getLocator(r.getStartContainer()) + " offset: " + r.getStartOffset()
                );

        log.info("End container: " + r.getEndContainer().getNodeName() +
                ", " + " locator: " + getLocator(r.getStartContainer()) + " offset: " + r.getEndOffset()
                );
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
            operationTargets.add(new OperationTarget(getLocator(text), startIndex, endIndex, text.getLength()));
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
}
