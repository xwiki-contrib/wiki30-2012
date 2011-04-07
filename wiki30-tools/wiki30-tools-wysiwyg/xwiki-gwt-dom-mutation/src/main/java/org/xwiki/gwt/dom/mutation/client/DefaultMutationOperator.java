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
 * Default {@link MutationOperator} implementation.
 * 
 * @version $Id$
 */
public class DefaultMutationOperator implements MutationOperator
{
    /**
     * A mutation target.
     */
    public static class MutationTarget
    {
        /**
         * The node affected by the mutation. In case of a {@link MutationType#INSERT} mutation this node is the parent
         * of the inserted node.
         */
        private Node targetNode;

        /**
         * The name of the attribute affected by the mutation. This field is used only when a new attribute is added, in
         * which case {@link #targetNode} is the owner element.
         */
        private String attributeName;

        /**
         * The position where the new node is inserted. This field is used only with {@link MutationType#INSERT} in
         * which case the {@link #targetNode} is the parent element.
         */
        private int childIndex = -1;

        /**
         * @return {@link #targetNode}
         */
        public Node getTargetNode()
        {
            return targetNode;
        }

        /**
         * Sets the target node.
         * 
         * @param targetNode {@link #targetNode}
         */
        public void setTargetNode(Node targetNode)
        {
            this.targetNode = targetNode;
        }

        /**
         * @return {@link #attributeName}
         */
        public String getAttributeName()
        {
            return attributeName;
        }

        /**
         * Sets the attribute name.
         * 
         * @param attributeName {@link #attributeName}
         */
        public void setAttributeName(String attributeName)
        {
            this.attributeName = attributeName;
        }

        /**
         * @return {@link #childIndex}
         */
        public int getChildIndex()
        {
            return childIndex;
        }

        /**
         * Sets the child index.
         * 
         * @param childIndex {@link #childIndex}
         */
        public void setChildIndex(int childIndex)
        {
            this.childIndex = childIndex;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see MutationOperator#operate(Mutation, Node)
     */
    public void operate(Mutation mutation, Node root)
    {
        MutationTarget mutationTarget = getMutationTarget(mutation.getLocator(), root, mutation.getType());
        switch (mutation.getType()) {
            case INSERT:
                insertNode(mutationTarget, mutation.getValue());
                break;
            case MODIFY:
                mutationTarget.getTargetNode().setNodeValue(mutation.getValue());
                break;
            case REMOVE:
                removeNode(mutationTarget.getTargetNode());
                break;
            case RENAME:
                // Unsupported right now.
                break;
            default:
                break;
        }
    }

    /**
     * @param locator the location where the mutation took place
     * @param root the node the locator is relative to
     * @param mutationType the type of the mutation
     * @return the target of the specified mutation
     */
    private MutationTarget getMutationTarget(String locator, Node root, MutationType mutationType)
    {
        MutationTarget mutationTarget = new MutationTarget();

        if (locator.length() > 0) {
            String[] path = locator.split("/");
            Node targetNode = root;
            for (int i = 0; i < path.length - 1; i++) {
                targetNode = targetNode.getChildNodes().getItem(Integer.parseInt(path[i]));
            }
            String pathEnd = path[path.length - 1];
            if (pathEnd.charAt(0) >= '0' && pathEnd.charAt(0) <= '9') {
                if (mutationType == MutationType.INSERT) {
                    mutationTarget.setTargetNode(targetNode);
                    mutationTarget.setChildIndex(Integer.parseInt(pathEnd));
                } else {
                    mutationTarget.setTargetNode(targetNode.getChildNodes().getItem(Integer.parseInt(pathEnd)));
                }
            } else {
                if (mutationType == MutationType.INSERT) {
                    mutationTarget.setTargetNode(targetNode);
                    mutationTarget.setAttributeName(pathEnd);
                } else {
                    mutationTarget.setTargetNode(getAttributeNode(Element.as(targetNode), pathEnd));
                }
            }
        } else {
            mutationTarget.setTargetNode(root);
        }

        return mutationTarget;
    }

    /**
     * @param element a DOM element
     * @param attributeName the name of an attribute
     * @return the attribute node with the specified name and the given owner element
     */
    private native Node getAttributeNode(Element element, String attributeName)
    /*-{
        return element.getAttributeNode(attributeName);
    }-*/;

    /**
     * Creates a new DOM node based on the specified value (e.g. attribute value, character data or HTML source) and
     * inserts it as a child of the given mutation target.
     * 
     * @param mutationTarget indicates where to insert the new DOM node
     * @param value defines the node to be inserted
     */
    private void insertNode(MutationTarget mutationTarget, String value)
    {
        Element targetElement = Element.as(mutationTarget.getTargetNode());
        if (mutationTarget.getAttributeName() != null) {
            targetElement.setAttribute(mutationTarget.getAttributeName(), value);
        } else {
            Element container = Element.as(targetElement.cloneNode(false));
            container.setInnerHTML(value);
            insertNodeAt(targetElement, mutationTarget.getChildIndex(), container.getFirstChild());
        }
    }

    /**
     * Inserts a node at the specified position under the given parent.
     * 
     * @param parent the parent node which will adopt the given node
     * @param position specifies where the new child should be placed
     * @param newChild the node to be inserted
     */
    private void insertNodeAt(Element parent, int position, Node newChild)
    {
        int i = Math.max(0, position);
        if (i >= parent.getChildNodes().getLength()) {
            parent.appendChild(newChild);
        } else {
            parent.insertBefore(newChild, parent.getChildNodes().getItem(i));
        }
    }

    /**
     * Removes the given DOM node from its parent element.
     * 
     * @param node the DOM node to be removed
     */
    private void removeNode(Node node)
    {
        if (node.getNodeType() == 2) {
            removeAttributeNode(node);
        } else {
            node.getParentNode().removeChild(node);
        }
    }

    /**
     * Removes the given attribute node from its owner element.
     * 
     * @param attrNode the attribute node to be removed
     */
    private native void removeAttributeNode(Node attrNode)
    /*-{
        attrNode.ownerElement.removeAttributeNode(attrNode);
    }-*/;
}
