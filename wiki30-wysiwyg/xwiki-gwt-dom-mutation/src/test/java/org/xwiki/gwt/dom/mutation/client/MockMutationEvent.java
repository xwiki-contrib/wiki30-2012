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

import org.xwiki.gwt.dom.mutation.client.MutationEvent.AttrChangeType;
import org.xwiki.gwt.dom.mutation.client.MutationEvent.MutationEventType;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;

/**
 * Mock mutation event that allows us to set its properties.
 * 
 * @version $Id$
 */
public class MockMutationEvent extends JavaScriptObject
{
    /**
     * Default constructor. Needs to be protected because all instances are created from JavaScript.
     */
    protected MockMutationEvent()
    {
        super();
    }

    /**
     * @return a new mock mutation event
     */
    public static MockMutationEvent newInstance()
    {
        return JavaScriptObject.createObject().cast();
    }

    /**
     * Sets the type of this mutation event.
     * 
     * @param type the new mutation event type
     */
    public final native void setMutationEventType(MutationEventType type)
    /*-{
        this.type = type.@org.xwiki.gwt.dom.mutation.client.MutationEvent.MutationEventType::toString()();
    }-*/;

    /**
     * Sets the type of attribute change.
     * 
     * @param type a {@link AttrChangeType} constant
     */
    public final native void setAttrChange(AttrChangeType type)
    /*-{
        this.attrChange = type.@org.xwiki.gwt.dom.mutation.client.MutationEvent.AttrChangeType::value;
    }-*/;

    /**
     * Sets the name of the changed attribute.
     * 
     * @param attrName the name of an attribute
     */
    public final native void setAttrName(String attrName)
    /*-{
        this.attrName = attrName;
    }-*/;

    /**
     * Sets the new attribute or character data value.
     * 
     * @param newValue a string representing the new value
     */
    public final native void setNewValue(String newValue)
    /*-{
        this.newValue = newValue;
    }-*/;

    /**
     * Sets the previous attribute or character data value.
     * 
     * @param prevValue a string representing the previous value
     */
    public final native void setPrevValue(String prevValue)
    /*-{
        this.prevValue = prevValue;
    }-*/;

    /**
     * Sets the related node.
     * 
     * @param relatedNode a DOM node
     */
    public final native void setRelatedNode(Node relatedNode)
    /*-{
        this.relatedNode = relatedNode;
    }-*/;

    /**
     * Sets the event target.
     * 
     * @param target a DOM node
     */
    public final native void setEventTarget(Node target)
    /*-{
        this.target = target;
    }-*/;
}
