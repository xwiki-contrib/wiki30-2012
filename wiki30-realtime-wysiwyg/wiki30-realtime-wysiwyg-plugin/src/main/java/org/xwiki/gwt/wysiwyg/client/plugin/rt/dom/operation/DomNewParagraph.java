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
import org.xwiki.gwt.dom.client.RangeCompare;

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
    public Range execute(Document document)
    {
        boolean moveCaretInsideNewParagraph = isInsertedAfterCaret(document);

        Element paragraph = document.createPElement().cast();
        paragraph.appendChild(document.createTextNode(""));
        // We append the paragraph to the BODY element because this is how the Tree model works.
        domUtils.insertAt(document.getBody(), paragraph, getOperation().getPosition());
        // Make sure that the new paragraph is edited. On some browsers this means appending a line break.
        paragraph.ensureEditable();

        if (moveCaretInsideNewParagraph) {
            Range range = document.createRange();
            // NOTE: We don't place the caret inside the empty text node because Firefox renders the caret badly if we
            // do so: the caret appears to be at the start of the document. Once you start typing the caret is rendered
            // at the right position, but this is confusing for the users.
            range.setStart(paragraph, 1);
            range.collapse(true);
            return range;
        }
        return null;
    }

    /**
     * Specifies whether the new paragraph is inserted before or after the caret position. In other words, this method
     * can be used to check if the Enter key was pressed at the start of the paragraph or at the end.
     * 
     * @param document the document where the paragraph will be inserted
     * @return {@code true} if the new paragraph will be inserted after the caret, {@code false} otherwise
     */
    private boolean isInsertedAfterCaret(Document document)
    {
        Range caret = document.getSelection().getRangeAt(0);
        Range insertionPoint = document.createRange();
        insertionPoint.setStart(document.getBody(), getOperation().getPosition());
        insertionPoint.collapse(true);
        return caret.compareBoundaryPoints(RangeCompare.START_TO_START, insertionPoint) <= 0;
    }
}
