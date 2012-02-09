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
import org.xwiki.gwt.dom.client.Range;

import com.google.gwt.dom.client.Node;

import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

/**
 * Utility methods for creating {@link TreeOperation}s.
 * 
 * @version $Id$
 */
public class TreeOperationFactory
{
    /**
     * Creates a new {@link TreeInsertText} operation.
     * 
     * @param siteId the client identifier
     * @param location the caret position
     * @param character the character to be inserted
     * @return a new {@link TreeInsertText} operation
     */
    public TreeInsertText createTreeInsertText(int siteId, Range location, char character)
    {
        List<Integer> path = getLocator(location.getStartContainer());
        return new TreeInsertText(siteId, location.getStartOffset(), toIntArray(path), character);
    }

    /**
     * @param node a DOM node
     * @return the path from the BODY element of the document that owns the given node to the node; a path item is the
     *         index of the corresponding note among its siblings.
     */
    public List<Integer> getLocator(Node node)
    {
        List<Integer> locator = new ArrayList<Integer>();
        Node ancestor = node;
        while (ancestor != null && ancestor != node.getOwnerDocument().getBody()) {
            locator.add(0, DOMUtils.getInstance().getNodeIndex(ancestor));
            ancestor = ancestor.getParentNode();
        }
        return locator;
    }

    /**
     * Converts a list of {@link Integer} objects to an array of integer numbers.
     * 
     * @param list the list to be converted
     * @return an array of integer numbers
     */
    public int[] toIntArray(List<Integer> list)
    {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
