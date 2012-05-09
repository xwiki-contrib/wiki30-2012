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

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.List;

/**
 * Represents the target for an operation, which is a representation of a DOM sub-range.
 * Instances of this class are in fact intermediary ranges obtained from a DOM range.
 * Ex: when selecting multiple lines, we have the general range which spans across many elements, but we are interested in all the intermediary ranges
 *
 * @version $Id: 0cc81b445800614e657a9bce9585baedfbc42400 $
 */
public class OperationTarget implements IsSerializable
{
    /**
     * The locator for the DOM node where the range starts.
     */
    private List<Integer> startContainer;

    /**
     * The offset within the start container.
     */
    private int startOffset;

    /**
     * The offset within the end container.
     */
    private int endOffset;

    /**
     * The data length of the DOM node (text node)
     */
    private int dataLength;

    /**
     * Default constructor.
     */
    public OperationTarget()
    {
    }

    /**
     * Creates a new operation target.
     * 
     * @param startContainer the locator for the DOM node where the range starts
     * @param startOffset the offset within the start container
     * @param endOffset the offset within the end container
     */
    public OperationTarget(List<Integer> startContainer, int startOffset, int endOffset, int length)
    {
        this.startContainer = startContainer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.dataLength = length;
    }

    /**
     * @return the locator for the DOM node where the range starts
     */
    public List<Integer> getStartContainer()
    {
        return startContainer;
    }

    /**
     * Sets the locator for the DOM node where the range starts.
     * 
     * @param startContainer the locator for the start node
     */
    public void setStartContainer(List<Integer> startContainer)
    {
        this.startContainer = startContainer;
    }

    /**
     * @return the offset within the start container
     */
    public int getStartOffset()
    {
        return startOffset;
    }

    /**
     * Sets the offset within the start container.
     * 
     * @param startOffset the offset within the start container
     */
    public void setStartOffset(int startOffset)
    {
        this.startOffset = startOffset;
    }

    /**
     * @return the offset within the end container
     */
    public int getEndOffset()
    {
        return endOffset;
    }

    /**
     * Sets the offset within the end container.
     * 
     * @param endOffset the offset within the end container
     */
    public void setEndOffset(int endOffset)
    {
        this.endOffset = endOffset;
    }

    /**
     * @return the data length of the node
     */
    public int getDataLength() {
        return dataLength;
    }

    /**
     * Set the data length of node
     * @param dataLength data length
     */
    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    @Override
    public String toString() {
        return "OperationTarget: locator: " + startContainer + ", startOffset: " + startOffset + ", endOffset:" + endOffset;
    }
}
