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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.xwiki.gwt.dom.client.Document;
import org.xwiki.gwt.dom.client.Element;
import org.xwiki.gwt.dom.client.Property;
import org.xwiki.gwt.dom.client.Range;
import org.xwiki.gwt.dom.client.Selection;
import org.xwiki.gwt.dom.client.Style;
import org.xwiki.gwt.dom.client.Text;
import org.xwiki.gwt.dom.client.TextFragment;
import org.xwiki.gwt.user.client.ui.rta.RichTextArea;
import org.xwiki.gwt.user.client.ui.rta.cmd.internal.ToggleInlineStyleExecutable;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;

import fr.loria.score.jupiter.tree.operation.TreeOperation;
import fr.loria.score.jupiter.tree.operation.TreeStyle;

/**
 * Applies {@link TreeStyle} on a DOM tree.
 *
 * @version $Id$
 */
public class DomStyle extends AbstractDomOperation
{
    private static final Logger log = Logger.getLogger(DomStyle.class.getName());

    /**
     * The real executable which applies the style to the DOM document
     */
    private DomStyleExecutable realDomStyleExecutable;

    /**
     * Creates a new DOM operation equivalent to the given Tree operation.
     *
     * @param operation a Tree operation
     */
    public DomStyle(TreeOperation operation)
    {
        super(operation);
    }

    @Override
    public Range execute(Document document) {
        TreeStyle treeStyleOp = getOperation();
        String stylePropertyValue = treeStyleOp.value;
        String [] vals = stylePropertyValue.split(":");

        if (vals[1].equalsIgnoreCase("bold")) {
            realDomStyleExecutable = new DomStyleExecutable(document, Style.FONT_WEIGHT, Style.FontWeight.BOLD);
        } else if (vals[1].equalsIgnoreCase("italic")) {
             realDomStyleExecutable = new DomStyleExecutable(document, Style.FONT_STYLE, Style.FontStyle.ITALIC);
        } else if (vals[1].equalsIgnoreCase("underline")) {
             realDomStyleExecutable = new DomStyleExecutable(document, Style.TEXT_DECORATION, Style.TextDecoration.UNDERLINE);
        } else if (vals[1].equalsIgnoreCase("line-through")) {
            realDomStyleExecutable = new DomStyleExecutable(document, Style.TEXT_DECORATION, Style.TextDecoration.LINE_THROUGH);
        }

        // Create range object from the op context
        Range styleRange = document.createRange();
        // Target node is the same for start and end because the style op is applied sequentially on every sub-range (text node) within the original selection range
        Node targetNode = getTargetNode(document);
        styleRange.setStart(targetNode, treeStyleOp.start);
        styleRange.setEnd(targetNode, treeStyleOp.end);
        log.info("Style range: " + styleRange.toString());

        //todo: what happens when remote user has selection?
        Range localRange = document.getSelection().getRangeAt(0);
        log.info("Local range: " + localRange.toString());

        return realDomStyleExecutable.execute(styleRange, vals[1]);
    }


    /**
     * If there is no selection, the insertion point will set the given style for subsequently typed characters. If there is a
     * selection and all of the characters are already styled, the style will be removed. Otherwise, all selected characters
     * will become styled.
     * <p/>
     * It would be easier to inherit directly from BoldExecutable and override just one method,
     * but this class is located into xwiki-platform-wysiwyg-client module and thus we were introducing a circular dependence.
     *
     * @version $Id: dd0a6a0520f2764164a0b938aaa5a52815febff6 $
     */
    class DomStyleExecutable extends ToggleInlineStyleExecutable {
        /**
         * The tag name, which is empty string since we use CSS styling properties
         */
        private static final String TAG_NAME = "";

        private static final String SPAN = "span";

        /**
         * The document target
         */
        private Document document;

        //todo: commit changes in gwt-user to have access to it
        private String propertyValue;
        /**
         * Creates a new executable of this type.
         *
         * @param document the document target
         * @param propertyName the style property name
         * @param propertyValue the style property value
         */
        public DomStyleExecutable(Document document, Property propertyName, String propertyValue) {
            // We don't use the RTA but the document, and override all methods that use the RTA
            super(new RichTextArea(), propertyName, propertyValue, TAG_NAME);
            this.document = document;
            this.propertyValue = propertyValue;

        }

        @Override
        public boolean execute(String parameter) {
            Selection selection = document.getSelection();
            List<Range> ranges = new ArrayList<Range>();
            for (int i = 0; i < selection.getRangeCount(); i++) {
                ranges.add(execute(selection.getRangeAt(i), parameter));
            }
            selection.removeAllRanges();
            for (Range range : ranges) {
                selection.addRange(range);
            }
            return true;
        }

        @Override
        public Range execute(Range range, String parameter) {
            return super.execute(range, parameter);
        }

        @Override
        public String getParameter() {
            Selection selection = document.getSelection();
            String selectionParameter = null;
            for (int i = 0; i < selection.getRangeCount(); i++) {
                String rangeParameter = getParameter(selection.getRangeAt(i));
                if (rangeParameter == null || (selectionParameter != null && !selectionParameter.equals(rangeParameter))) {
                    return null;
                }
                selectionParameter = rangeParameter;
            }
            return selectionParameter;
        }

        @Override
        public boolean isExecuted() {
            return ((TreeStyle)DomStyle.this.getOperation()).addStyle;
        }

        @Override
        protected TextFragment execute(Text text, int startIndex, int endIndex, String parameter) {
            boolean addStyle = isExecuted();
            return addStyle ? addStyle(text, startIndex, endIndex) : removeStyle(text, startIndex, endIndex);
        }

        /**
         * Adds the underlying style to the given text node.
         *
         * @param text           the target text node
         * @param firstCharIndex the first character on which we apply the style
         * @param lastCharIndex  the last character on which we apply the style
         * @return a text fragment indicating what has been formatted
         */
        protected TextFragment addStyle(Text text, int firstCharIndex, int lastCharIndex) {
            if (matchesStyle(text)) {
                // Already styled. Skip.
                return new TextFragment(text, firstCharIndex, lastCharIndex);
            }

            // Make sure we apply the style only to the selected text.
            text.crop(firstCharIndex, lastCharIndex);
            Element element = (Element) text.getParentElement();
            if (SPAN.equalsIgnoreCase(element.getNodeName())) {
                element.getStyle().setProperty(getProperty().getJSName(), propertyValue);
            } else {
                SpanElement spanElement = Document.get().createSpanElement();
                spanElement.getStyle().setProperty(getProperty().getJSName(), propertyValue);

                element.replaceChild(spanElement, text);
                spanElement.appendChild(text);
            }
            return new TextFragment(text, 0, text.getLength());
        }

        @Override
        /**
         * Override because of a bug
         */
        protected boolean matchesInheritedStyle(Element element) {
            final com.google.gwt.dom.client.Style style = element.getStyle();
            String computedValue;
            if (style != null && style.getProperty(getProperty().getJSName()).length() > 0) {
                log.fine("computing existing property");
                computedValue = style.getProperty(getProperty().getJSName());
            } else {
                log.fine("computing style property");
                computedValue = element.getComputedStyleProperty(getProperty().getJSName());
            }
            log.fine("Computed value is: " + computedValue);
            if (getProperty().isMultipleValue()) {
                return computedValue != null && computedValue.toLowerCase().contains(propertyValue);
            } else {
                return propertyValue.equalsIgnoreCase(computedValue);
            }
        }
    }
}
