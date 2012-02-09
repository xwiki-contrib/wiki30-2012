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
import org.xwiki.gwt.dom.client.Range;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

/**
 * Applies {@link TreeInsertText} on a DOM tree.
 * 
 * @version $Id$
 */
public class DomInsertText extends AbstractDomOperation
{
    /**
     * Creates a new DOM operation equivalent to the given Tree operation.
     * 
     * @param operation a Tree operation
     */
    public DomInsertText(TreeOperation operation)
    {
        super(operation);
    }

    @Override
    public Range execute(Document document)
    {
        TreeInsertText insertText = getOperation();
        String text = String.valueOf(insertText.getText());
        Node targetNode = getTargetNode(document);

        if (Node.ELEMENT_NODE == targetNode.getNodeType()) {
            // Create a new text node in the target element.
            Node newTextNode = document.createTextNode(text);
            domUtils.insertAt(targetNode, newTextNode, insertText.getPosition());
        } else if (Node.TEXT_NODE == targetNode.getNodeType()) {
            // Insert a character in the target text node.
            Text.as(targetNode).insertData(insertText.getPosition(), text);
        }

        // No change to the selection.
        return null;
    }
}
