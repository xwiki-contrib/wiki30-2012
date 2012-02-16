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
package org.xwiki.gwt.wysiwyg.client.plugin.rt.dom.operation;

import org.xwiki.gwt.dom.client.Document;
import org.xwiki.gwt.dom.client.Element;
import org.xwiki.gwt.dom.client.Range;

import com.google.gwt.dom.client.Node;

import fr.loria.score.jupiter.tree.operation.TreeMergeParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

/**
 * Applies {@link TreeMergeParagraph} on a DOM tree.
 * 
 * @version $Id$
 */
public class DomMergeParagraph extends AbstractDomOperation
{
    /**
     * Creates a new DOM operation equivalent to the given Tree operation.
     * 
     * @param operation a Tree operation
     */
    public DomMergeParagraph(TreeOperation operation)
    {
        super(operation);
    }

    @Override
    public Range execute(Document document)
    {
        int position = getOperation().getPosition();
        Node left = document.getBody().getChild(position - 1);
        Node right = document.getBody().getChild(position);

        // Remove the line break at the end of the left paragraph. We should probably determine if the line break was
        // added manually (with Shift+Enter) or automatically (to be able to edit the paragraph).
        Node lastLeaf = domUtils.getLastLeaf(left);
        if ("br".equalsIgnoreCase(lastLeaf.getNodeName())) {
            lastLeaf.removeFromParent();
        }

        // The caret should be at the merging point.
        Range caret = document.createRange();
        caret.setStart(left, left.getChildCount());
        caret.collapse(true);

        // Move the nodes from the right paragraph to the left paragraph.
        left.appendChild(Element.as(right).extractContents());

        // Remove the right paragraph.
        right.removeFromParent();

        return caret;
    }
}
