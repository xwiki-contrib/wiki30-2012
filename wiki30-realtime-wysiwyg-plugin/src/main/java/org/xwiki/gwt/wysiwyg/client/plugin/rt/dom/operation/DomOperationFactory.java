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

import fr.loria.score.jupiter.tree.operation.TreeDeleteText;
import fr.loria.score.jupiter.tree.operation.TreeInsertParagraph;
import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeMergeParagraph;
import fr.loria.score.jupiter.tree.operation.TreeNewParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;
import fr.loria.score.jupiter.tree.operation.TreeStyle;

/**
 * Creates DOM operations from Tree operations. This class is used to map Tree operations to DOM operations. Whenever a
 * operation is executed on the Tree model we need to synchronize or update the DOM model and for this we need to know
 * the DOM operation equivalent to the Tree operation that is executed.
 * 
 * @version $Id$
 */
public class DomOperationFactory
{
    /**
     * @param operation a tree operation
     * @return the DOM operation equivalent to the given tree operation
     */
    public DomOperation createDomOperation(TreeOperation operation)
    {
        if (operation instanceof TreeInsertText) {
            return new DomInsertText(operation);
        } else if (operation instanceof TreeDeleteText) {
            return new DomDeleteText(operation);
        } else if (operation instanceof TreeInsertParagraph) {
            return new DomInsertParagraph(operation);
        } else if (operation instanceof TreeNewParagraph) {
            return new DomNewParagraph(operation);
        } else if (operation instanceof TreeMergeParagraph) {
            return new DomMergeParagraph(operation);
        } else if (operation instanceof TreeStyle) {
            return new DomStyle(operation);
        }
        return null;
    }
}
