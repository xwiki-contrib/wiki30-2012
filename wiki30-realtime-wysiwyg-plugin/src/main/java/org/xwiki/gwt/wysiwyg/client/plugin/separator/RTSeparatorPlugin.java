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
package org.xwiki.gwt.wysiwyg.client.plugin.separator;

import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.user.client.Config;
import org.xwiki.gwt.user.client.ui.rta.RichTextArea;
import org.xwiki.gwt.wysiwyg.client.Images;
import org.xwiki.gwt.wysiwyg.client.Strings;
import org.xwiki.gwt.wysiwyg.client.plugin.internal.AbstractPlugin;
import org.xwiki.gwt.wysiwyg.client.plugin.internal.FocusWidgetUIExtension;
import org.xwiki.gwt.wysiwyg.client.plugin.rt.BaseRealTimePlugin;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import fr.loria.score.jupiter.tree.operation.TreeOperation;
import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.operation.*;
import java.util.List;
import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.wysiwyg.client.plugin.rt.BaseRealTimePlugin;
import org.xwiki.gwt.wysiwyg.client.plugin.rt.EditorUtils;

/**
 * Does not inherit the standard SeparatorPlugin because it is so simple code.
 * Allows the user to insert an horizontal line in place of the current selection.
 *
 */
public class RTSeparatorPlugin extends BaseRealTimePlugin implements ClickHandler
{
    /**
     * The tool bar button used for inserting a new horizontal rule.
     */
    private PushButton hr;

    /**
     * Tool bar extension that includes the horizontal rule button.
     */
    private final FocusWidgetUIExtension toolBarExtension = new FocusWidgetUIExtension("toolbar");

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPlugin#init(RichTextArea, Config)
     */
    public void init(RichTextArea textArea, Config config)
    {
        super.init(textArea, config);

        hr = new PushButton(new Image(Images.INSTANCE.hr()));
        saveRegistration(hr.addClickHandler(this));
        hr.setTitle(Strings.INSTANCE.hr());
        toolBarExtension.addFeature("hr", hr);
        
        if (toolBarExtension.getFeatures().length > 0) {
            getUIExtensionList().add(toolBarExtension);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractPlugin#destroy()
     */
    public void destroy()
    {
        if (hr != null) {
            hr.removeFromParent();
            hr = null;
        }

        toolBarExtension.clearFeatures();

        super.destroy();
    }

    /**
     * {@inheritDoc}
     * 
     * @see ClickHandler#onClick(ClickEvent)
     */
    public void onClick(ClickEvent event)
    {
        if (event.getSource() == hr && hr.isEnabled()) {
            getTextArea().setFocus(true); 
            Range range = getTextArea().getDocument().getSelection().getRangeAt(0);
            if (range != null) {
               range = EditorUtils.normalizeCaretPosition(range);               
               int[] path = EditorUtils.toIntArray(EditorUtils.getLocator(range.getStartContainer()));
               int siteId = clientJupiter.getSiteId();
                              
               TreeCompositeOperation seq = null;               
               if (range.getStartOffset() == 0) {
                  TreeOperation newP = new TreeNewParagraph(siteId, path[0]);
                  TreeOperation updateP = new TreeUpdateElement(siteId, new int[] { path[0] }, Tree.NODE_NAME, "hr"); 
                  TreeOperation newP2 = new TreeNewParagraph(siteId, path[0]);
                  seq = new TreeCompositeOperation(newP, updateP, newP2);                   
               } else {
                  TreeOperation splitP = new TreeInsertParagraph(siteId, range.getStartOffset(), path);              
                  TreeOperation newP = new TreeNewParagraph(siteId, path[0] + 1);
                  TreeOperation updateP = new TreeUpdateElement(siteId, new int[] { path[0] + 1 }, Tree.NODE_NAME, "hr");               
                  seq = new TreeCompositeOperation(splitP, newP, updateP);
               }
               clientJupiter.generate(seq);
            }                           
        }
    }
}
