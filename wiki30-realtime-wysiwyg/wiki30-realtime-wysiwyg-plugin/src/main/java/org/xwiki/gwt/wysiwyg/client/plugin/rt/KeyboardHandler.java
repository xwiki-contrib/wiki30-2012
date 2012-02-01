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
import java.util.List;

import org.xwiki.gwt.dom.client.DOMUtils;
import org.xwiki.gwt.dom.client.Element;
import org.xwiki.gwt.dom.client.Event;
import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.dom.client.Selection;
import org.xwiki.gwt.user.client.ui.rta.RichTextArea;

import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Catches keyboard events and overwrites the default browser behavior in order to ensure that the effect of the
 * corresponding action on the DOM tree is equivalent with the effect on the tree model used by the real time
 * synchronization algorithm.
 * 
 * @version $Id$
 */
public class KeyboardHandler implements KeyDownHandler, KeyUpHandler, KeyPressHandler
{
    /**
     * Collection of DOM utility methods.
     */
    private final DOMUtils domUtils = DOMUtils.getInstance();

    /**
     * Flag used to avoid handling both KeyDown and KeyPress events. This flag is needed because of the inconsistencies
     * between browsers regarding keyboard events. For instance IE doesn't generate the KeyPress event for backspace key
     * and generates multiple KeyDown events while a key is hold down. On the contrary, FF generates the KeyPress event
     * for the backspace key and generates just one KeyDown event while a key is hold down. FF generates multiple
     * KeyPress events when a key is hold down.
     */
    private boolean ignoreNextKeyPress;

    /**
     * Flag used to prevent the default browser behavior for the KeyPress event when the KeyDown event has been
     * canceled. This is needed only in functional tests where keyboard events (KeyDown, KeyPress, KeyUp) are triggered
     * independently and thus canceling KeyDown doesn't prevent the default KeyPress behavior. Without this flag, and
     * because we have to handle the KeyDown event besides the KeyPress in order to overcome cross-browser
     * inconsistencies, simulating keyboard typing in functional tests would trigger our custom behavior but also the
     * default browser behavior.
     */
    private boolean cancelNextKeyPress;

    /**
     * The text area that generates the keyboard events.
     */
    private RichTextArea textArea;

    /**
     * Registers all keyboard handlers on the given rich text area.
     * 
     * @param textArea the text area to listen to for keyboard events
     * @return the list of handler registrations that can be used to unregister the handlers
     */
    public List<HandlerRegistration> addHandlers(RichTextArea textArea)
    {
        this.textArea = textArea;
        List<HandlerRegistration> registrations = new ArrayList<HandlerRegistration>();
        registrations.add(textArea.addKeyDownHandler(this));
        registrations.add(textArea.addKeyUpHandler(this));
        registrations.add(textArea.addKeyPressHandler(this));
        return registrations;
    }

    /**
     * {@inheritDoc}
     * 
     * @see KeyDownHandler#onKeyDown(KeyDownEvent)
     */
    public void onKeyDown(KeyDownEvent event)
    {
        if (event.getSource() == textArea) {
            ignoreNextKeyPress = true;
            handleRepeatableKey((Event) event.getNativeEvent());
            cancelNextKeyPress = ((Event) event.getNativeEvent()).isCancelled();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see KeyPressHandler#onKeyPress(KeyPressEvent)
     */
    public void onKeyPress(KeyPressEvent event)
    {
        if (event.getSource() == textArea) {
            if (!ignoreNextKeyPress) {
                handleRepeatableKey((Event) event.getNativeEvent());
            } else if (cancelNextKeyPress) {
                ((Event) event.getNativeEvent()).xPreventDefault();
            }
            ignoreNextKeyPress = false;
            cancelNextKeyPress = false;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see KeyUpHandler#onKeyUp(KeyUpEvent)
     */
    public void onKeyUp(KeyUpEvent event)
    {
        ignoreNextKeyPress = false;
        cancelNextKeyPress = false;
    }

    /**
     * Handles a repeatable key press.
     * 
     * @param event the native event that was fired
     */
    private void handleRepeatableKey(Event event)
    {
        // Don't handle the key if the event was canceled by a different party.
        if (event.isCancelled()) {
            return;
        }
        switch (event.getKeyCode()) {
            case KeyCodes.KEY_BACKSPACE:
                onBackspace(event);
                break;
            default:
                break;
        }
    }

    /**
     * @param container a block level element containing the caret
     * @param caret the position of the caret inside the document
     * @return {@code true} if the caret is at the beginning of its block level container, {@code false} otherwise
     */
    protected boolean isAtStart(Node container, Range caret)
    {
        if (!container.hasChildNodes()) {
            return true;
        }
        if (caret.getStartOffset() > 0) {
            return false;
        }
        return domUtils.getFirstLeaf(container) == domUtils.getFirstLeaf(caret.getStartContainer());
    }

    /**
     * Overwrites the default rich text area behavior when the Backspace key is being pressed.
     * 
     * @param event the native event that was fired
     */
    private void onBackspace(Event event)
    {
        Selection selection = textArea.getDocument().getSelection();
        if (!selection.isCollapsed()) {
            return;
        }
        Range caret = selection.getRangeAt(0);
        // Look for the nearest block-level element that contains the caret.
        Node container = domUtils.getNearestBlockContainer(caret.getStartContainer());
        // See if both the caret container and its previous sibling are block-level elements that support only in-line
        // content and if the caret is at the start of its block-level container.
        if (domUtils.isBlockLevelInlineContainer(container)
            && domUtils.isBlockLevelInlineContainer(container.getPreviousSibling()) && isAtStart(container, caret)) {
            // Cancel the event to prevent its default behavior.
            event.xPreventDefault();
            // Place the caret at the end of the previous block container.
            caret.setStart(container.getPreviousSibling(), container.getPreviousSibling().getChildCount());
            caret.collapse(true);
            // Merge the two blocks.
            container.getPreviousSibling().appendChild(Element.as(container).extractContents());
            container.getParentNode().removeChild(container);
            // Update the selection.
            selection.removeAllRanges();
            selection.addRange(caret);
        }
    }
}
