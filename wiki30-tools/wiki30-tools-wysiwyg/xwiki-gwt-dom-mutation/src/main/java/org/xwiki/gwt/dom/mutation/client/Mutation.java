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
package org.xwiki.gwt.dom.mutation.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A DOM mutation.
 * 
 * @version $Id$
 */
public class Mutation implements IsSerializable
{
    /**
     * Mutation types.
     */
    public static enum MutationType
    {
        /**
         * A node has been inserted.
         */
        INSERT,

        /**
         * A node has been removed.
         */
        REMOVE,

        /**
         * A node has been modified.
         */
        MODIFY,

        /**
         * A node has been renamed.
         */
        RENAME
    }

    /**
     * Mutation type.
     */
    private MutationType type;

    /**
     * Locates the place where the event occurred.
     */
    private String locator;

    /**
     * Either a new attribute value, a new text node value, a new node name or a HTML fragment.
     */
    private String value;

    /**
     * @return the mutation type
     */
    public MutationType getType()
    {
        return type;
    }

    /**
     * Sets the mutation type.
     * 
     * @param type the new type
     */
    public void setType(MutationType type)
    {
        this.type = type;
    }

    /**
     * @return the mutation locator
     */
    public String getLocator()
    {
        return locator;
    }

    /**
     * Sets the mutation locator.
     * 
     * @param locator the new locator
     */
    public void setLocator(String locator)
    {
        this.locator = locator;
    }

    /**
     * @return the mutation value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the mutation value.
     * 
     * @param value the new value
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}
