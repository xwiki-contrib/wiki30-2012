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
 * @version $Id: 0dd5e9959455f9941c4c7209b84c7e53602acc18 $
 */
public class DefaultMutationSerializer implements MutationSerializer
{
    /**
     * Specifies how a text node is modified when a {@code DOM_CHARACTER_DATA_MODIFIED} event is fired. The range of
     * text modified is assumed to be contiguous.
     */
    private static class CharacterDataModification
    {
        /**
         * Where the modification starts.
         */
        private int start;

        /**
         * Where the modification ends.
         */
        private int end;

        /**
         * The string that replaces the text between the {@link #start} and {@link #end}.
         */
        private String replacement;

        /**
         * @return the index of the first character affected by the modification
         */
        public int getStart()
        {
            return start;
        }

        /**
         * Sets the index of the first modified character.
         * 
         * @param start the index of the first modified character
         */
        public void setStart(int start)
        {
            this.start = start;
        }

        /**
         * @return where the modification ends
         */
        public int getEnd()
        {
            return end;
        }

        /**
         * Sets where the modification ends.
         * 
         * @param end the index of the first character that follows the modification
         */
        public void setEnd(int end)
        {
            this.end = end;
        }

        /**
         * @return the string that replaced the text between {@link #start} and {@link #end}
         */
        public String getReplacement()
        {
            return replacement;
        }

        /**
         * Sets the string that will replace the text between {@link #start} and {@link #end}.
         * 
         * @param replacement the string that replaces the text between {@link #start} and {@link #end}
         */
        public void setReplacement(String replacement)
        {
            this.replacement = replacement;
        }

        /**
         * {@inheritDoc}
         * 
         * @see Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder result = new StringBuilder().append(start);
            if (start != end) {
                result.append(',').append(end);
            }
            if (replacement != null && replacement.length() > 0) {
                result.append(',').append(replacement);
            }
            return result.toString();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see MutationSerializer#serialize(MutationEvent, Node)
     */
    public Mutation serialize(MutationEvent event, Node root)
    {
        Mutation mutation = new Mutation();
        Node target = Node.as(event.getEventTarget());
        switch (event.getMutationEventType()) {
            case DOM_NODE_INSERTED:
                mutation.setType(MutationType.INSERT);
                mutation.setValue(getNodeIndex(target) + "," + serialize(target));
                mutation.setLocator(getLocator(target.getParentNode(), root));
                break;
            case DOM_NODE_REMOVED:
                mutation.setType(MutationType.REMOVE);
                mutation.setLocator(getLocator(target, root));
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
                mutation.setLocator(getLocator(target, event.getRelatedNode().getNodeName(), root));
                break;
            case DOM_CHARACTER_DATA_MODIFIED:
                CharacterDataModification modification =
                    getCharacterDataModification(event.getPrevValue(), event.getNewValue());
                if (modification.getReplacement().length() == 0) {
                    mutation.setType(MutationType.REMOVE);
                } else if (modification.getStart() == modification.getEnd()) {
                    mutation.setType(MutationType.INSERT);
                } else {
                    mutation.setType(MutationType.MODIFY);
                }
                mutation.setValue(modification.toString());
                mutation.setLocator(getLocator(target, root));
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

    /**
     * @param oldValue the old character data
     * @param newValue the new character data
     * @return the modification that can be used to transform the old character data into the new character data
     */
    private CharacterDataModification getCharacterDataModification(String oldValue, String newValue)
    {
        int startOffset = 0;
        while (startOffset < oldValue.length() && startOffset < newValue.length()
            && oldValue.charAt(startOffset) == newValue.charAt(startOffset)) {
            startOffset++;
        }
        int oldEndOffset = oldValue.length() - 1;
        int newEndOffset = newValue.length() - 1;
        while (startOffset <= oldEndOffset && startOffset <= newEndOffset
            && oldValue.charAt(oldEndOffset) == newValue.charAt(newEndOffset)) {
            oldEndOffset--;
            newEndOffset--;
        }
        CharacterDataModification modification = new CharacterDataModification();
        modification.setStart(startOffset);
        modification.setEnd(oldEndOffset + 1);
        modification.setReplacement(newValue.substring(startOffset, newEndOffset + 1));
        return modification;
    }
}
