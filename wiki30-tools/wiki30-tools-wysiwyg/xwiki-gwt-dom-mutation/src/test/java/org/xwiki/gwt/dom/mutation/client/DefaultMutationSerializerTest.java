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

import org.xwiki.gwt.dom.mutation.client.Mutation.MutationType;
import org.xwiki.gwt.dom.mutation.client.MutationEvent.AttrChangeType;
import org.xwiki.gwt.dom.mutation.client.MutationEvent.MutationEventType;

import com.google.gwt.dom.client.Node;

/**
 * Unit test for {@link DefaultMutationSerializer}.
 * 
 * @version $Id$
 */
public class DefaultMutationSerializerTest extends AbstractMutationTest
{
    /**
     * Tests how a {@link MutationEventType#DOM_NODE_INSERTED} mutation is serialized.
     */
    public void testSerializeNodeInsertion()
    {
        // Setup the DOM tree.
        getContainer().setInnerHTML("1<em><span>2<img/><ins>3</ins></span></em>");
        // Create a mock mutation event.
        MockMutationEvent event = MockMutationEvent.newInstance();
        event.setMutationEventType(MutationEventType.DOM_NODE_INSERTED);
        event.setEventTarget(getContainer().getLastChild().getLastChild().getLastChild());
        // Serialize the mutation event.
        MutationSerializer serializer = new DefaultMutationSerializer();
        Mutation mutation = serializer.serialize((MutationEvent) event.cast(), getContainer());
        // Check the result.
        assertEquals(MutationType.INSERT, mutation.getType());
        assertEquals("1/0/2", mutation.getLocator());
        assertEquals("<ins>3</ins>", mutation.getValue());
    }

    /**
     * Tests how a {@link MutationEventType#DOM_NODE_REMOVED} mutation is serialized.
     */
    public void testSerializeNodeRemoval()
    {
        // Setup the DOM tree.
        getContainer().setInnerHTML("<span><img/>test</span>");
        // Create a mock mutation event.
        MockMutationEvent event = MockMutationEvent.newInstance();
        event.setMutationEventType(MutationEventType.DOM_NODE_REMOVED);
        event.setEventTarget(getContainer().getFirstChild().getLastChild());
        // Serialize the mutation event.
        MutationSerializer serializer = new DefaultMutationSerializer();
        Mutation mutation = serializer.serialize((MutationEvent) event.cast(), getContainer());
        // Check the result.
        assertEquals(MutationType.REMOVE, mutation.getType());
        assertEquals("0/1", mutation.getLocator());
        assertNull(mutation.getValue());
    }

    /**
     * Tests how a {@link MutationEventType#DOM_ATTR_MODIFIED} mutation is serialized.
     */
    public void testSerializeAttributeChange()
    {
        // Setup the DOM tree.
        getContainer().setInnerHTML("<span class=\"test\"></span>");
        // Create a mock mutation event.
        MockMutationEvent event = MockMutationEvent.newInstance();
        event.setMutationEventType(MutationEventType.DOM_ATTR_MODIFIED);
        event.setEventTarget(getContainer().getFirstChild());
        event.setAttrChange(AttrChangeType.ADDITION);
        String value = "alice";
        event.setNewValue(value);
        event.setRelatedNode(getAttributeNode(getContainer().getFirstChild(), "class"));
        // Serialize the mutation event.
        MutationSerializer serializer = new DefaultMutationSerializer();
        Mutation mutation = serializer.serialize((MutationEvent) event.cast(), getContainer());
        // Check the result.
        assertEquals(MutationType.INSERT, mutation.getType());
        assertEquals("0/class", mutation.getLocator());
        assertEquals(value, mutation.getValue());
    }

    /**
     * Tests how a {@link MutationEventType#DOM_CHARACTER_DATA_MODIFIED} mutation is serialized.
     */
    public void testSerializeCharacterDataChange()
    {
        // Setup the DOM tree.
        getContainer().setInnerHTML("<img/><span>1</span><em>2</em>");
        // Create a mock mutation event.
        MockMutationEvent event = MockMutationEvent.newInstance();
        event.setMutationEventType(MutationEventType.DOM_CHARACTER_DATA_MODIFIED);
        event.setEventTarget(getContainer().getLastChild().getFirstChild());
        String value = "3";
        event.setNewValue(value);
        // Serialize the mutation event.
        MutationSerializer serializer = new DefaultMutationSerializer();
        Mutation mutation = serializer.serialize((MutationEvent) event.cast(), getContainer());
        // Check the result.
        assertEquals(MutationType.MODIFY, mutation.getType());
        assertEquals("2/0", mutation.getLocator());
        assertEquals(value, mutation.getValue());
    }

    /**
     * Utility method for accessing an attribute node.
     * 
     * @param element a DOM element
     * @param attrName the name of an attribute of the given element node
     * @return the attribute node with the specified name
     */
    private native Node getAttributeNode(Node element, String attrName)
    /*-{
        return element.getAttributeNode(attrName);
    }-*/;
}
