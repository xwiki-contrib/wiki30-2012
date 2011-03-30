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
package org.xwiki.gwt.dom.mutation.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;

/**
 * Represents a native mutation event.
 * 
 * @version $Id$
 */
public class MutationEvent extends NativeEvent
{
    /**
     * Possible mutation event types.
     */
    public static enum MutationEventType
    {
        /**
         * Fired when a node has been added as a child of another node. This event is dispatched after the insertion has
         * taken place. The target of this event is the node being inserted.
         */
        DOM_NODE_INSERTED("DOMNodeInserted"),

        /**
         * Fired when a node is being removed from its parent node. This event is dispatched before the node is removed
         * from the tree. The target of this event is the node being removed.
         */
        DOM_NODE_REMOVED("DOMNodeRemoved"),

        /**
         * Fired after an attribute has been modified on a node. The target of this event is the Node whose attribute
         * changed. The value of {@code attrChange} indicates whether the attribute was modified, added, or removed. The
         * value of {@code relatedNode} indicates the attribute node whose value has been affected. It is expected that
         * string based replacement of an attribute value will be viewed as a modification of the attribute since its
         * identity does not change. Subsequently replacement of the attribute node with a different attribute node is
         * viewed as the removal of the first attribute node and the addition of the second.
         */
        DOM_ATTR_MODIFIED("DOMAttrModified"),

        /**
         * Fired after CharacterData within a node has been modified but the node itself has not been inserted or
         * deleted. This event is also triggered by modifications to PI elements. The target of this event is the
         * CharacterData node.
         */
        DOM_CHARACTER_DATA_MODIFIED("DOMCharacterDataModified");

        /**
         * The name of this mutation event type.
         */
        private String name;

        /**
         * Creates a new mutation event type with the specified name.
         * 
         * @param name the name of the created mutation event type
         */
        MutationEventType(String name)
        {
            this.name = name;
        }

        /**
         * {@inheritDoc}
         * 
         * @see Enum#toString()
         */
        public String toString()
        {
            return this.name;
        }

        /**
         * @param event a native event
         * @return the {@link MutationEventType} constant associated with the given event if it's a mutation event,
         *         {@code null} otherwise
         */
        public static MutationEventType valueOf(NativeEvent event)
        {
            for (MutationEventType type : MutationEventType.values()) {
                if (type.name.equals(event.getType())) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * Indicates in which way the attribute was changed.
     */
    public static enum AttrChangeType
    {
        /**
         * The attribute was modified in place.
         */
        MODIFICATION(1),

        /**
         * The attribute was just added.
         */
        ADDITION(2),

        /**
         * The attribute was just removed.
         */
        REMOVAL(3);

        /**
         * The integer value associated with this type. This is the value of the {@code attrChange} property.
         */
        private int value;

        /**
         * Creates a new attribute change type based on the given value of the {@code attrChange} property.
         * 
         * @param value the integer constant associated with the newly created type
         */
        AttrChangeType(int value)
        {
            this.value = value;
        }

        /**
         * @param value a possible {@code attrChange} value
         * @return the {@link AttrChangeType} constant associated with the specified value
         */
        public static AttrChangeType valueOf(int value)
        {
            for (AttrChangeType type : AttrChangeType.values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * Default constructor. Needs to be protected because all instances are created from JavaScript.
     */
    protected MutationEvent()
    {
        super();
    }

    /**
     * @param document a DOM document
     * @return a new uninitialized mutation event
     */
    public static native MutationEvent newInstance(Document document)
    /*-{
        return document.createEvent('MutationEvents');
    }-*/;

    /**
     * @return the type of this mutation event
     */
    public final MutationEventType getMutationEventType()
    {
        return MutationEventType.valueOf(this);
    }

    /**
     * @return the type of change which triggered the {@link MutationEventType#DOM_ATTR_MODIFIED} event
     */
    public final native AttrChangeType getAttrChange()
    /*-{
        return @org.xwiki.gwt.dom.mutation.client.MutationEvent.AttrChangeType::valueOf(I)(this.attrChange);
    }-*/;

    /**
     * @return the name of the changed attribute node in a {@link MutationEventType#DOM_ATTR_MODIFIED} event
     */
    public final native String getAttrName()
    /*-{
        return this.attrName;
    }-*/;

    /**
     * @return the new value of the attribute node in {@link MutationEventType#DOM_ATTR_MODIFIED} events, and of the
     *         CharacterData node in {@link MutationEventType#DOM_CHARACTER_DATA_MODIFIED} events.
     */
    public final native String getNewValue()
    /*-{
        return this.newValue;
    }-*/;

    /**
     * @return the previous value of the attribute node in {@link MutationEventType#DOM_ATTR_MODIFIED} events, and of
     *         the CharacterData node in {@link MutationEventType#DOM_CHARACTER_DATA_MODIFIED} events.
     */
    public final native String getPrevValue()
    /*-{
        return this.prevValue;
    }-*/;

    /**
     * @return a secondary node related to a mutation event. For example, if a mutation event is dispatched to a node
     *         indicating that its parent has changed, the {@code relatedNode} is the changed parent. If an event is
     *         instead dispatched to a subtree indicating a node was changed within it, the {@code relatedNode} is the
     *         changed node. In the case of the {@link MutationEventType#DOM_ATTR_MODIFIED} event it indicates the
     *         attribute node which was modified, added, or removed.
     */
    public final native Node getRelatedNode()
    /*-{
        return this.relatedNode;
    }-*/;
}
