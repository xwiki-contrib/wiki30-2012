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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

/**
 * Unit test for {@link DefaultMutationSource}.
 * 
 * @version $Id$
 */
public class DefaultMutationSourceTest extends AbstractMutationTest
{
    /**
     * Unit test for {@link DefaultMutationSource#addListener(MutationEventType, MutationListener)} and
     * {@link DefaultMutationSource#removeListener(MutationEventType, MutationListener)}.
     */
    public void testAddRemoveMutationListener()
    {
        final MutationSource source = new DefaultMutationSource(getContainer());
        final int[] counter = {0};
        source.addListener(MutationEventType.DOM_ATTR_MODIFIED, new MutationListener()
        {
            public void onMutation(MutationEvent event)
            {
                source.removeListener(MutationEventType.DOM_ATTR_MODIFIED, this);
                counter[0]++;
            }
        });

        getContainer().setTitle("once");
        getContainer().setTitle("twice");

        assertEquals(1, counter[0]);
    }

    /**
     * Inserts a node and checks if the mutation event follows the specifications.
     */
    public void testCatchNodeInsertion()
    {
        final MutationSource source = new DefaultMutationSource(getContainer());
        final Node child = getDocument().createTextNode("text");
        source.addListener(MutationEventType.DOM_NODE_INSERTED, new MutationListener()
        {
            public void onMutation(MutationEvent event)
            {
                source.removeListener(MutationEventType.DOM_NODE_INSERTED, this);
                assertEquals(MutationEventType.DOM_NODE_INSERTED, event.getMutationEventType());
                assertEquals(child, event.getEventTarget());
                assertEquals(getContainer(), event.getRelatedNode());
            }
        });
        getContainer().appendChild(child);
    }

    /**
     * Removes a node and checks if the mutation event follows the specifications.
     */
    public void testCatchNodeRemoval()
    {
        final MutationSource source = new DefaultMutationSource(getContainer());
        final Node child = getDocument().createSpanElement();
        source.addListener(MutationEventType.DOM_NODE_REMOVED, new MutationListener()
        {
            public void onMutation(MutationEvent event)
            {
                source.removeListener(MutationEventType.DOM_NODE_REMOVED, this);
                assertEquals(MutationEventType.DOM_NODE_REMOVED, event.getMutationEventType());
                assertEquals(child, event.getEventTarget());
                assertEquals(getContainer(), event.getRelatedNode());
            }
        });
        getContainer().appendChild(child);
        getContainer().removeChild(child);
    }

    /**
     * Changes the value of an attribute and checks if the mutation event follows the specifications.
     */
    public void testCatchAttributeChange()
    {
        final MutationSource source = new DefaultMutationSource(getContainer());
        final Element child = getDocument().createAnchorElement();
        final String prevValue = "apple";
        final String newValue = "orange";
        source.addListener(MutationEventType.DOM_ATTR_MODIFIED, new MutationListener()
        {
            public void onMutation(MutationEvent event)
            {
                source.removeListener(MutationEventType.DOM_ATTR_MODIFIED, this);
                assertEquals(MutationEventType.DOM_ATTR_MODIFIED, event.getMutationEventType());
                assertEquals(child, event.getEventTarget());
                assertEquals(2, event.getRelatedNode().getNodeType());
                assertEquals(AttrChangeType.MODIFICATION, event.getAttrChange());
                assertEquals("title", event.getAttrName());
                assertEquals(prevValue, event.getPrevValue());
                assertEquals(newValue, event.getNewValue());
            }
        });
        child.setTitle(prevValue);
        getContainer().appendChild(child);
        child.setTitle(newValue);
    }

    /**
     * Changes the value of a text node and checks if the mutation event follows the specifications.
     */
    public void testCatchCharacterDataChange()
    {
        final MutationSource source = new DefaultMutationSource(getContainer());
        final String prevValue = "old";
        final String newValue = "new";
        final Node child = getDocument().createTextNode(prevValue);
        source.addListener(MutationEventType.DOM_CHARACTER_DATA_MODIFIED, new MutationListener()
        {
            public void onMutation(MutationEvent event)
            {
                source.removeListener(MutationEventType.DOM_CHARACTER_DATA_MODIFIED, this);
                assertEquals(MutationEventType.DOM_CHARACTER_DATA_MODIFIED, event.getMutationEventType());
                assertEquals(child, event.getEventTarget());
                assertEquals(prevValue, event.getPrevValue());
                assertEquals(newValue, event.getNewValue());
            }
        });
        getContainer().appendChild(child);
        child.setNodeValue(newValue);
    }
}
