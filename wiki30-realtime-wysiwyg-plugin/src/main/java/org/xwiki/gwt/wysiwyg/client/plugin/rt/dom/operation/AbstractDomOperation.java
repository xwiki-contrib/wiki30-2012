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

import org.xwiki.gwt.dom.client.DOMUtils;
import org.xwiki.gwt.wysiwyg.client.plugin.rt.EditorUtils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;

import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

/**
 * Applies {@link TreeInsertText} on a DOM tree.
 * 
 * @version $Id$
 */
public abstract class AbstractDomOperation implements DomOperation
{
    /**
     * The underlying tree operation.
     */
    private TreeOperation operation;

    /**
     * DOM utility methods.
     */
    protected DOMUtils domUtils = DOMUtils.getInstance();

    /**
     * Creates a new DOM operation based on the given Tree operation.
     * 
     * @param operation a Tree operation
     */
    public AbstractDomOperation(TreeOperation operation)
    {
        this.operation = operation;
    }

    /**
     * @param <T> the type of the Tree operation this DOM operation is equivalent to
     * @return the Tree operation equivalent to this DOM operation
     */
    @SuppressWarnings("unchecked")
    protected <T extends TreeOperation> T getOperation()
    {
        return (T) operation;
    }

    /**
     * @param document the document this operation is executed on
     * @return the DOM node targeted by this operation
     */
    public Node getTargetNode(Document document)
    {
        return EditorUtils.getChildNodeFromLocator(document.getBody(), operation.getPath());
    }
}
