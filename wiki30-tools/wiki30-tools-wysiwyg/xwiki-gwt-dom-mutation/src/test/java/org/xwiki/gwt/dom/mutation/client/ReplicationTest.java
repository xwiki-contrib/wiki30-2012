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

import java.util.EnumSet;

import org.xwiki.gwt.dom.mutation.client.MutationEvent.MutationEventType;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;

/**
 * Integration tests to prove the mutation event can be replicated on the same HTML page.
 * 
 * @version $Id$
 */
public class ReplicationTest extends AbstractMutationTest
{
    /**
     * The root of the source DOM subtree, where the original mutations take place.
     */
    private Element source;

    /**
     * The root of the destination subtree, where the mutations are replicated.
     */
    private Element destination;

    /**
     * {@inheritDoc}
     * 
     * @see AbstractMutationTest#gwtSetUp()
     */
    protected void gwtSetUp() throws Exception
    {
        super.gwtSetUp();

        getContainer().setInnerHTML("<div></div><div></div>");
        source = Element.as(getContainer().getFirstChild());
        destination = Element.as(getContainer().getLastChild());
    }

    /**
     * Tests if the action of removing an element is replicated correctly.
     */
    public void testReplicateRemoveElement()
    {
        source.setInnerHTML("1<em><ins>2</ins>3</em>");
        replicate(EnumSet.of(MutationEventType.DOM_NODE_REMOVED));
        source.getLastChild().removeChild(source.getLastChild().getFirstChild());
        assertEquals("1<em>3</em>", destination.getInnerHTML());
    }

    /**
     * Tests if the action of removing a text node is replicated correctly.
     */
    public void testReplicateRemoveTextNode()
    {
        source.setInnerHTML("1<em>2</em>");
        replicate(EnumSet.of(MutationEventType.DOM_NODE_REMOVED));
        source.removeChild(source.getFirstChild());
        assertEquals("<em>2</em>", destination.getInnerHTML());
    }

    /**
     * Tests if the action of removing an attribute is replicated correctly.
     */
    public void testReplicateRemoveAttribute()
    {
        source.setInnerHTML("<em>1<img alt=\"xwiki\"/></em>");
        replicate(EnumSet.of(MutationEventType.DOM_ATTR_MODIFIED));
        Element.as(source.getFirstChild().getLastChild()).removeAttribute("alt");
        assertEquals("<em>1<img></em>", destination.getInnerHTML());
    }

    /**
     * Tests if the action of inserting an element is replicated correctly.
     */
    public void testReplicateInsertElement()
    {
        source.setInnerHTML("<del>1</del>");
        replicate(EnumSet.of(MutationEventType.DOM_NODE_INSERTED));

        Element span = source.getOwnerDocument().createSpanElement();
        span.setTitle("alice");
        span.setInnerHTML("3<em>2</em>");

        source.insertBefore(span, source.getFirstChild());
        assertEquals("<span title=\"alice\">3<em>2</em></span><del>1</del>", destination.getInnerHTML());
    }

    /**
     * Tests if the action of inserting a text node is replicated correctly.
     */
    public void testReplicateInsertTextNode()
    {
        source.setInnerHTML("<span>before</span><img/>");
        replicate(EnumSet.of(MutationEventType.DOM_NODE_INSERTED));
        source.insertBefore(source.getOwnerDocument().createTextNode("after"), source.getLastChild());
        assertEquals("<span>before</span>after<img>", destination.getInnerHTML());
    }

    /**
     * Tests if the action of adding an attribute is replicated correctly.
     */
    public void testReplicateAddAttribute()
    {
        replicate(EnumSet.of(MutationEventType.DOM_ATTR_MODIFIED));
        source.setTitle("toucan");
        assertEquals(source.getTitle(), destination.getTitle());
    }

    /**
     * Tests if the action of changing an attribute's value is replicated correctly.
     */
    public void testReplicateChangeAttribute()
    {
        source.setInnerHTML("<em>this<span>is<!--x--><img/></span></em>");
        replicate(EnumSet.of(MutationEventType.DOM_ATTR_MODIFIED));
        ((ImageElement) source.getLastChild().getLastChild().getLastChild()).setAlt("none");
        assertEquals("<em>this<span>is<!--x--><img alt=\"none\"></span></em>", destination.getInnerHTML());
    }

    /**
     * Tests if the action of changing a text node's value is replicated correctly.
     */
    public void testReplicateChangeText()
    {
        source.setInnerHTML("1<em>2</em><del>45</del>");
        replicate(EnumSet.of(MutationEventType.DOM_CHARACTER_DATA_MODIFIED));
        source.getLastChild().getLastChild().setNodeValue("3");
        assertEquals("1<em>2</em><del>3</del>", destination.getInnerHTML());
    }

    /**
     * Tests if the action of moving an element is replicated correctly.
     */
    public void testReplicateMoveElement()
    {
        source.setInnerHTML("<em>1</em><ins>2</ins>3");
        replicate(EnumSet.of(MutationEventType.DOM_NODE_REMOVED, MutationEventType.DOM_NODE_INSERTED));
        source.getFirstChildElement().appendChild(source.getChildNodes().getItem(1));
        assertEquals("<em>1<ins>2</ins></em>3", destination.getInnerHTML());
    }

    /**
     * Tests if the action of moving a text node is replicated correctly.
     */
    public void testReplicateMoveTextNode()
    {
        source.setInnerHTML("<em><strong>2</strong><del>1</del></em>");
        replicate(EnumSet.of(MutationEventType.DOM_NODE_REMOVED, MutationEventType.DOM_NODE_INSERTED));
        source.insertBefore(source.getLastChild().getLastChild().getFirstChild(), source.getFirstChild());
        assertEquals("1<em><strong>2</strong><del></del></em>", destination.getInnerHTML());
    }

    /**
     * Tests if the action of replacing an element is replicated correctly.
     */
    public void testReplicateReplaceElement()
    {
        source.setInnerHTML("<em>1<img/>3</em><span><del>2</del>4</span>");
        replicate(EnumSet.of(MutationEventType.DOM_NODE_REMOVED, MutationEventType.DOM_NODE_INSERTED));
        source.getFirstChildElement().replaceChild(source.getLastChild().getFirstChild(),
            source.getFirstChild().getChildNodes().getItem(1));
        assertEquals("<em>1<del>2</del>3</em><span>4</span>", destination.getInnerHTML());
    }

    /**
     * Tests if the action of replacing a text node is replicated correctly.
     */
    public void testReplicateReplaceTextNode()
    {
        source.setInnerHTML("<span>1</span>3<em>12</em>");
        replicate(EnumSet.of(MutationEventType.DOM_NODE_REMOVED, MutationEventType.DOM_NODE_INSERTED));
        source.getFirstChildElement().replaceChild(source.getLastChild(), source.getFirstChild().getFirstChild());
        assertEquals("<span><em>12</em></span>3", destination.getInnerHTML());
    }

    /**
     * Tests if the action of setting the inner HTML of an element is replicated correctly.
     */
    public void testReplicateSetInnerHTML()
    {
        source.setInnerHTML("1<em><img/>3</em>");
        replicate(EnumSet.of(MutationEventType.DOM_NODE_REMOVED, MutationEventType.DOM_NODE_INSERTED));
        Element.as(source.getLastChild()).setInnerHTML("2<span title=\"3\">4</span>");
        assertEquals("1<em>2<span title=\"3\">4</span></em>", destination.getInnerHTML());
    }

    /**
     * Replicates the DOM mutations of the specified types from the source subtree to the destination subtree.
     * 
     * @param mutationTypes the set of mutation types to replicate
     */
    private void replicate(EnumSet<MutationEventType> mutationTypes)
    {
        // Make sure the destination has the same initial content as the source.
        destination.setInnerHTML(source.getInnerHTML());
        // Create the mutation replicator.
        final MutationSerializer mutationSerializer = new DefaultMutationSerializer();
        final MutationOperator mutationOperator = new DefaultMutationOperator();
        MutationListener replicator = new MutationListener()
        {
            public void onMutation(MutationEvent event)
            {
                Mutation mutation = mutationSerializer.serialize(event, source);
                mutationOperator.operate(mutation, destination);
            }
        };
        // Register the mutation replicator for the specified mutation types.
        MutationSource mutationSource = new DefaultMutationSource(source);
        for (MutationEventType mutationType : mutationTypes) {
            mutationSource.addListener(mutationType, replicator);
        }
    }
}
