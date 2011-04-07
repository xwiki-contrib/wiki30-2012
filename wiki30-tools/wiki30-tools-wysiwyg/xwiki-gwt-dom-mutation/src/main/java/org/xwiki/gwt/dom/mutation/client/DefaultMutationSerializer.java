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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

/**
 * Default {@link MutationSerializer} implementation.
 * 
 * @version $Id$
 */
public class DefaultMutationSerializer implements MutationSerializer
{
    /**
     * {@inheritDoc}
     * 
     * @see MutationSerializer#serialize(MutationEvent, Node)
     */
    public Mutation serialize(MutationEvent event, Node root)
    {
        Mutation mutation = new Mutation();
        switch (event.getMutationEventType()) {
            case DOM_NODE_INSERTED:
                mutation.setType(MutationType.INSERT);
                mutation.setValue(serialize(Node.as(event.getEventTarget())));
                mutation.setLocator(getLocator(Node.as(event.getEventTarget()), root));
                break;
            case DOM_NODE_REMOVED:
                mutation.setType(MutationType.REMOVE);
                mutation.setLocator(getLocator(Node.as(event.getEventTarget()), root));
                break;
            case DOM_ATTR_MODIFIED:
                switch (event.getAttrChange()) {
                    case ADDITION:
                        mutation.setType(MutationType.INSERT);
                        mutation.setValue(event.getNewValue());
                        break;
                    case MODIFICATION:
                        mutation.setType(MutationType.MODIFY);
                        mutation.setValue(event.getNewValue());
                        break;
                    case REMOVAL:
                        mutation.setType(MutationType.REMOVE);
                        break;
                    default:
                        return null;
                }
                mutation.setLocator(getLocator(Node.as(event.getEventTarget()), event.getRelatedNode().getNodeName(),
                    root));
                break;
            case DOM_CHARACTER_DATA_MODIFIED:
                mutation.setType(MutationType.MODIFY);
                mutation.setValue(event.getNewValue());
                mutation.setLocator(getLocator(Node.as(event.getEventTarget()), root));
                break;
            default:
                return null;
        }
        return mutation;
    }

    /**
     * Computes the locator of an attribute node specified by its owner element and its name.
     * 
     * @param ownerElement the element holding the attribute
     * @param attrName the name of the attribute for which to compute the locator
     * @param root the node the locator is relative to
     * @return a string locator for the specified attribute node relative to the specified root
     * @see Mutation#getLocator()
     */
    private String getLocator(Node ownerElement, String attrName, Node root)
    {
        String locator = getLocator(ownerElement, root);
        if (locator.length() > 0) {
            locator += "/";
        }
        return locator + attrName;
    }

    /**
     * @param node a DOM node
     * @param root the node the locator is relative to
     * @return a string locator for the given node relative to the specified root
     * @see Mutation#getLocator()
     */
    private String getLocator(Node node, Node root)
    {
        StringBuffer locator = new StringBuffer();
        Node ancestor = node;
        while (ancestor != null && ancestor != root) {
            if (locator.length() > 0) {
                locator.insert(0, '/');
            }
            locator.insert(0, getNodeIndex(ancestor));
            ancestor = ancestor.getParentNode();
        }
        return locator.toString();
    }

    /**
     * @param node a DOM node
     * @return the index of the given node between its siblings
     */
    private int getNodeIndex(Node node)
    {
        Node sibling = node;
        int index = 0;
        while (sibling.getPreviousSibling() != null) {
            index++;
            sibling = sibling.getPreviousSibling();
        }
        return index;
    }

    /**
     * @param node a DOM node
     * @return a string representation of the given event target from which it can be recomputed
     */
    private String serialize(Node node)
    {
        Node nodeClone = node.cloneNode(true);
        Element container = node.getOwnerDocument().createDivElement();
        container.appendChild(nodeClone);
        return container.getInnerHTML();
    }
}
