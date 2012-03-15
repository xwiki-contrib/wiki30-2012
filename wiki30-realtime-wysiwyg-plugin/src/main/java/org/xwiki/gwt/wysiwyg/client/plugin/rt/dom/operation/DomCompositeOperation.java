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
import fr.loria.score.jupiter.tree.operation.TreeCaretPosition;
import fr.loria.score.jupiter.tree.operation.TreeCompositeOperation;

import fr.loria.score.jupiter.tree.operation.TreeMergeParagraph;
import fr.loria.score.jupiter.tree.operation.TreeOperation;
import java.util.Iterator;
import org.xwiki.gwt.wysiwyg.client.plugin.rt.EditorUtils;
import org.xwiki.gwt.wysiwyg.client.plugin.rt.TreeOperationFactory;

/**
 * Applies {@link TreeMergeParagraph} on a DOM tree.
 * 
 * @version $Id$
 */
public class DomCompositeOperation extends AbstractDomOperation
{
    
    private boolean isRemote;    
    
    /**
     * The factory used to create DOM operations equivalent to Tree operations.
     */
    private DomOperationFactory domOperationFactory = new DomOperationFactory();
    
        
    /**
     * The object used to create the {@link TreeInsertText} operations used to preserve the selection.
     */
    private TreeOperationFactory treeOperationFactory = new TreeOperationFactory();
    
    /**
     * Creates a new DOM operation equivalent to the given Tree operation.
     * 
     * @param operation a Tree operation
     */
    public DomCompositeOperation(TreeOperation operation, boolean isRemote)
    {
        super(operation);
        this.isRemote = isRemote;
    }

    @Override
    public Range execute(Document document)
    {
        Range caret = document.getSelection().getRangeAt(0);
        
        //todo: fix siteIds 
        TreeCaretPosition startSelection = treeOperationFactory.createCaretPosition(getOperation().getSiteId(), caret, caret.getStartOffset());
        TreeCaretPosition endSelection = treeOperationFactory.createCaretPosition(getOperation().getSiteId(), caret, caret.getEndOffset());
        
        Iterator<TreeOperation> it = ((TreeCompositeOperation) getOperation()).operations.iterator();
        while (it.hasNext()) {
            
            TreeOperation op = it.next();
            startSelection = (TreeCaretPosition) op.transform(startSelection);
            endSelection = (TreeCaretPosition) op.transform(endSelection);
            
            DomOperation domOperation = domOperationFactory.createDomOperation(op, this.isRemote);
            domOperation.execute(document);            
        }
        
        
        Node startContainer = EditorUtils.getChildNodeFromLocator(document.getBody(), startSelection.getPath());
        caret.setStart(startContainer, startSelection.getPosition());

        Node endContainer = EditorUtils.getChildNodeFromLocator(document.getBody(), endSelection.getPath());
        caret.setEnd(endContainer, endSelection.getPosition());
                
        return caret;
    }
}
