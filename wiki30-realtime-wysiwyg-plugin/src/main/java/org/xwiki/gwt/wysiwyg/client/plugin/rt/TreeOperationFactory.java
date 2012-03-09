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
import java.util.logging.Logger;

import org.xwiki.gwt.dom.client.Range;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.operation.TreeCaretPosition;
import fr.loria.score.jupiter.tree.operation.TreeInsertText;
import fr.loria.score.jupiter.tree.operation.TreeMergeParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;
import fr.loria.score.jupiter.tree.operation.TreeStyle;
import fr.loria.score.jupiter.tree.operation.TreeUpdateElement;

import static org.xwiki.gwt.wysiwyg.client.plugin.rt.EditorUtils.getIntermediaryTargets;

/**
 * Utility methods for creating {@link TreeOperation}s.
 * 
 * @version $Id$
 */
public class TreeOperationFactory
{
    private static Logger log = Logger.getLogger(TreeOperationFactory.class.getName());

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
        List<Integer> path = EditorUtils.getLocator(location.getStartContainer());
        return new TreeInsertText(siteId, location.getStartOffset(), EditorUtils.toIntArray(path), character);
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
        List<Integer> path = EditorUtils.getLocator(location.getStartContainer());
        return new TreeCaretPosition(siteId, offset, EditorUtils.toIntArray(path));
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
        op.setPath(EditorUtils.toIntArray(path));

        return op;
    }

    /**
     *
     * @param siteId the site id
     * @param range the range selection, which could span across multiple text nodes
     * @param styleKey the name of the style attribute
     * @param styleValue the value of the style attribute
     * @return a List<TreeStyle> of operations, for every text node included in the selection range
     */
    public List<TreeStyle> createStyleOperation(int siteId, Range range, String styleKey, String styleValue)
    {
        List<TreeStyle> styleOperations = new ArrayList<TreeStyle>();

        List<OperationTarget> targets = getIntermediaryTargets(range);
        log.info(targets.toString());

        for (OperationTarget target : targets) {
            log.finest("Generate tree style op for :" + target.toString() + ", key: " + styleKey + ", val: " +
                styleValue);
            boolean addStyle = false;
            int[] path = EditorUtils.toIntArray(target.getStartContainer());
            if (path.length == 2) {
                addStyle = true;
            }

            boolean splitLeft = true;
            int start = target.getStartOffset();
            if (start == 0) {
                splitLeft = false;
            }

            boolean splitRight = true;
            int end = target.getEndOffset();
            if (end == target.getDataLength()) {
                splitRight = false;
            }

            TreeStyle op = new TreeStyle(siteId, path, start, end, styleKey, styleValue, addStyle,
                    splitLeft, splitRight);
            styleOperations.add(op);
        }

        return styleOperations;
    }

    /**
     * @param siteId the site id
     * @param range the range selection
     * @param headingOrParagraphValue a value representing a heading level (h1, h2, .., h6) or a paragraph (p)
     * @return a {@code TreeUpdateElement} operation
     */
    public TreeOperation createHeadingOrParagraphOperation(int siteId, Range range, String headingOrParagraphValue)
    {
        List<Integer> path = EditorUtils.getLocator(range.getStartContainer());
        return new TreeUpdateElement(siteId, new int[] {path.get(0)}, Tree.NODE_NAME, headingOrParagraphValue);
    }
}
