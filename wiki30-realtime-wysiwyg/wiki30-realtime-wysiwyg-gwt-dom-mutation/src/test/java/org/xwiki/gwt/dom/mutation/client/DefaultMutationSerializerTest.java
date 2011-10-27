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
 * @version $Id: 3a67674b60ae16137870d84b63ecbdf77100f28b $
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
        assertEquals("1/0", mutation.getLocator());
        assertEquals("2,<ins>3</ins>", mutation.getValue());
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
     * Generic test for the serialization of a {@link MutationEventType#DOM_CHARACTER_DATA_MODIFIED} mutation.
     * 
     * @param previousValue the previous text value
     * @param newValue the new text value
     * @param expectedMutationType the expected mutation type
     * @param expectedMutationValue the expected mutation value
     */
    private void testSerializeCharacterDataChange(String previousValue, String newValue,
        MutationType expectedMutationType, String expectedMutationValue)
    {
        // Setup the DOM tree.
        getContainer().appendChild(getContainer().getOwnerDocument().createTextNode(previousValue));
        // Create a mock mutation event.
        MockMutationEvent event = MockMutationEvent.newInstance();
        event.setMutationEventType(MutationEventType.DOM_CHARACTER_DATA_MODIFIED);
        event.setEventTarget(getContainer().getFirstChild());
        event.setPrevValue(previousValue);
        event.setNewValue(newValue);
        // Serialize the mutation event.
        MutationSerializer serializer = new DefaultMutationSerializer();
        Mutation mutation = serializer.serialize((MutationEvent) event.cast(), getContainer());
        // Check the result.
        assertEquals(expectedMutationType, mutation.getType());
        assertEquals(String.valueOf('0'), mutation.getLocator());
        assertEquals(expectedMutationValue, mutation.getValue());
    }

    /**
     * Tests how the mutation generated by inserting a character is serialized.
     */
    public void testSerializeInsertCharacter()
    {
        testSerializeCharacterDataChange("inset", "insert", MutationType.INSERT, "4,r");
    }

    /**
     * Tests how the mutation generated by inserting a character sequence before a given text is serialized.
     */
    public void testSerializeInsertCharactersBefore()
    {
        testSerializeCharacterDataChange("iki", "XWiki", MutationType.INSERT, "0,XW");
    }

    /**
     * Tests how the mutation generated by inserting a character sequence after a given text is serialized.
     */
    public void testSerializeInsertCharactersAfter()
    {
        testSerializeCharacterDataChange("xwi", "xwiki", MutationType.INSERT, "3,ki");
    }

    /**
     * Tests how the mutation generated by duplicating a given text is serialized.
     */
    public void testSerializeDuplicateText()
    {
        testSerializeCharacterDataChange("ole", "oleole", MutationType.INSERT, "3,ole");
    }

    /**
     * Tests how the mutation generated by deleting a character is serialized.
     */
    public void testSerializeDeleteCharacter()
    {
        testSerializeCharacterDataChange("first", "fist", MutationType.REMOVE, "2,3");
    }

    /**
     * Tests how the mutation generated by deleting a prefix is serialized.
     */
    public void testSerializeDeleteCharactersBefore()
    {
        testSerializeCharacterDataChange("colibri", "ibri", MutationType.REMOVE, "0,3");
    }

    /**
     * Tests how the mutation generated by deleting a suffix is serialized.
     */
    public void testSerializeDeleteCharactersAfter()
    {
        testSerializeCharacterDataChange("toucan", "touc", MutationType.REMOVE, "4,6");
    }

    /**
     * Tests how the mutation generated by modifying a few characters from a given text is serialized.
     */
    public void testSerializeModifyCharacters()
    {
        testSerializeCharacterDataChange("before", "bEfOre", MutationType.MODIFY, "1,4,EfO");
    }

    /**
     * Tests how the mutation generated by modifying the prefix of a given text is serialized.
     */
    public void testSerializeReplacePrefix()
    {
        testSerializeCharacterDataChange("field", "held", MutationType.MODIFY, "0,2,h");
    }

    /**
     * Tests how the mutation generated by modifying the suffix of a given text is serialized.
     */
    public void testSerializeReplaceSuffix()
    {
        testSerializeCharacterDataChange("handler", "handling", MutationType.MODIFY, "5,7,ing");
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
