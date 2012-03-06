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

import java.util.List;

import org.xwiki.gwt.dom.client.Range;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import fr.loria.score.jupiter.tree.operation.TreeCaretPosition;
import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeMergeParagraph;
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
        List<Integer> path = TreeHelper.getLocator(location.getStartContainer());
        return new TreeInsertText(siteId, location.getStartOffset(), TreeHelper.toIntArray(path), character);
    }

    /**
     *
     * @param siteId the client id
     * @param location the native DOM caret position
     * @param offset
     * @return a new {@link TreeCaretPosition} operation
     */
    public TreeCaretPosition createCaretPosition(int siteId, Range location, int offset)
    {
        List<Integer> path = TreeHelper.getLocator(location.getStartContainer());
        return new TreeCaretPosition(siteId, offset, TreeHelper.toIntArray(path));
    }

    public TreeMergeParagraph createTreeMergeParagraph(boolean isBackspace, int siteId, Node leftParagraph, Node rightParagraph, List<Integer> path)
    {
        TreeMergeParagraph op = null;
        int lBbrCount = Element.as(leftParagraph).getElementsByTagName("br").getLength();
        int rBbrCount = Element.as(rightParagraph).getElementsByTagName("br").getLength();
        int mergePos = isBackspace ? path.get(0) : path.get(0) + 1;
        op = new TreeMergeParagraph(siteId, mergePos,
            leftParagraph.getChildCount() - lBbrCount,
            rightParagraph.getChildCount() - rBbrCount);
        op.setPath(TreeHelper.toIntArray(path));

        return op;
    }
}
