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

import fr.loria.score.jupiter.tree.operation.TreeNewParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;

/**
 * Applies {@link TreeNewParagraph} on a DOM tree.
 * 
 * @version $Id$
 */
public class DomNewParagraph extends AbstractDomOperation
{
    /**
     * Creates a new DOM operation equivalent to the given Tree operation.
     * 
     * @param operation a Tree operation
     */
    public DomNewParagraph(TreeOperation operation)
    {
        super(operation);
    }

    @Override
    public void execute(Document document)
    {
        Element paragraph = document.createPElement().cast();
        paragraph.appendChild(document.createTextNode(""));
        // We append the paragraph to the BODY element because this is how the Tree model works.
        domUtils.insertAt(document.getBody(), paragraph, getOperation().getPosition());
        // Make sure that the new paragraph is edited. On some browsers this means appending a line break.
        paragraph.ensureEditable();
    }
}
