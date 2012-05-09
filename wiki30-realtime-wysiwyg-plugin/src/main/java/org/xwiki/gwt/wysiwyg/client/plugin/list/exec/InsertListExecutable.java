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
package org.xwiki.gwt.wysiwyg.client.plugin.list.exec;

import org.xwiki.gwt.user.client.ui.rta.RichTextArea;
import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.operation.TreeOperation;
import fr.loria.score.jupiter.tree.operation.TreeUpdateElement;
import org.xwiki.gwt.dom.client.DOMUtils;
import org.xwiki.gwt.dom.client.Element;
import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.dom.client.Selection;
import org.xwiki.gwt.wysiwyg.client.plugin.rt.BaseRealTimePlugin;
import org.xwiki.gwt.wysiwyg.client.plugin.rt.EditorUtils;
import org.xwiki.gwt.user.client.ui.rta.cmd.internal.DefaultExecutable;
import com.google.gwt.dom.client.Node;
import org.xwiki.gwt.user.client.ui.rta.cmd.Command;

/**
 * List executable to insert a list (ordered or unordered).
 * 
 */
public class InsertListExecutable extends DefaultExecutable {

    /**
     * Unordered list element name.
     */
    protected static final String UNORDERED_LIST_TAG = "ul";

    /**
     * Ordered list element name.
     */
    protected static final String ORDERED_LIST_TAG = "ol";
    
    /**
     * Stores whether the lists handled by this executable are ordered lists or not.
     */
    private boolean ordered;    
    
    private BaseRealTimePlugin realTimePlugin;
    
    
    /**
     * Create a list executable to handle lists as specified by the parameter.
     * 
     * @param rta the execution target
     * @param ordered specified whether this executable handles ordered or unordered lists.
     */
    public InsertListExecutable(RichTextArea rta, boolean ordered, BaseRealTimePlugin realTimePlugin)
    {
        super(rta, ordered ? Command.INSERT_ORDERED_LIST.toString() : Command.INSERT_UNORDERED_LIST.toString());
        this.ordered = ordered;
        this.realTimePlugin = realTimePlugin;
    }    

    
    /**
     * {@inheritDoc}. Transforms the current element in a (un)ordered list.
     */
    public boolean execute(String param)
    {        
        boolean executionResult = false;
        Selection selection = rta.getDocument().getSelection();
        Range range = selection.getRangeAt(0);
        
        if (range != null) {
            range = EditorUtils.normalizeCaretPosition(range);               
            int[] path = EditorUtils.toIntArray(EditorUtils.getLocator(range.getStartContainer()));
            int siteId = realTimePlugin.getClientJupiter().getSiteId();
                              
            TreeOperation updateP = new TreeUpdateElement(siteId, new int[] { path[0] }, Tree.NODE_NAME, ordered ? UNORDERED_LIST_TAG : ORDERED_LIST_TAG); 
            realTimePlugin.getClientJupiter().generate(updateP);
            executionResult = true;
        }                    
        
        /*
        if (range.isCollapsed()) {
            Element listItem = getListItem(range);
            if (canExecute(listItem)) {
                execute(listItem);
                executionResult = true;
            }
        } else {
            executionResult = executeOnMultipleItems(range, true);
        }
        // try to restore selection, hope it all stays well
        selection.removeAllRanges();
        selection.addRange(range);

        */
 
        return executionResult;
    }

 /*
    @Override
    protected void execute(Element listItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean executeOnMultipleItems(Range range, boolean perform) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
*/    
    /**
     * {@inheritDoc}
     * <p>
     * Overwrite the default function to handle situations of valid HTML lists which are not detected correctly by the
     * browsers.
     * 
     * @see DefaultExecutable#isExecuted()
     */
    public boolean isExecuted()
    {
        if (rta.getDocument().getSelection().getRangeCount() > 0) {
            Range range = rta.getDocument().getSelection().getRangeAt(0);
            Node rangeContainer = range.getCommonAncestorContainer();
            return (Element) DOMUtils.getInstance().getFirstAncestor(rangeContainer, ordered ? UNORDERED_LIST_TAG : ORDERED_LIST_TAG) != null;
        } else {
            return false;
        }
    }
    
}
