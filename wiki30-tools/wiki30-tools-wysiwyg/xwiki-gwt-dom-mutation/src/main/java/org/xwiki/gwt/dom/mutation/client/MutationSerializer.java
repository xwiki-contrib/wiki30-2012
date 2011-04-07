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

import com.google.gwt.dom.client.Node;

/**
 * Serializes a {@link MutationEvent}.
 * 
 * @version $Id$
 */
public interface MutationSerializer
{
    /**
     * Serializes a {@link MutationEvent} relative to the given root node.
     * 
     * @param event the mutation event to be serialized
     * @param root the node the mutation event is serialized relative to
     * @return a {@link Mutation}
     */
    Mutation serialize(MutationEvent event, Node root);
}
