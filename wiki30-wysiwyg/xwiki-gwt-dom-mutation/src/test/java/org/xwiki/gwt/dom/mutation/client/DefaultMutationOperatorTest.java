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

import org.xwiki.gwt.dom.mutation.client.Mutation.MutationType;

/**
 * Unit tests for {@link DefaultMutationOperator}.
 * 
 * @version $Id$
 */
public class DefaultMutationOperatorTest extends AbstractMutationTest
{
    /**
     * Operates a {@link MutationType#REMOVE} mutation on an element node.
     */
    public void testRemoveElement()
    {
        getContainer().setInnerHTML("1<em><ins>2</ins>3</em>");

        Mutation mutation = new Mutation();
        mutation.setType(MutationType.REMOVE);
        mutation.setLocator("1/0");

        MutationOperator operator = new DefaultMutationOperator();
        operator.operate(mutation, getContainer());

        assertEquals("1<em>3</em>", getContainer().getInnerHTML());
    }

    /**
     * Operates a {@link MutationType#REMOVE} mutation on a text node.
     */
    public void testRemoveTextNode()
    {
        getContainer().setInnerHTML("1<em>2</em>3");

        Mutation mutation = new Mutation();
        mutation.setType(MutationType.REMOVE);
        mutation.setLocator("");

        MutationOperator operator = new DefaultMutationOperator();
        operator.operate(mutation, getContainer().getLastChild());

        assertEquals("1<em>2</em>", getContainer().getInnerHTML());
    }

    /**
     * Operates a {@link MutationType#REMOVE} mutation on an attribute node.
     */
    public void testRemoveAttribute()
    {
        getContainer().setInnerHTML("<em>1<ins title=\"alice\">2</ins></em>");

        Mutation mutation = new Mutation();
        mutation.setType(MutationType.REMOVE);
        mutation.setLocator("0/1/title");

        MutationOperator operator = new DefaultMutationOperator();
        operator.operate(mutation, getContainer());

        assertEquals("<em>1<ins>2</ins></em>", getContainer().getInnerHTML());
    }

    /**
     * Operates a {@link MutationType#INSERT} mutation with an element node.
     */
    public void testInsertElement()
    {
        getContainer().setInnerHTML("<em>1</em>3");

        Mutation mutation = new Mutation();
        mutation.setType(MutationType.INSERT);
        mutation.setLocator("0/1");
        mutation.setValue("<del title=\"bob\">2</del>");

        MutationOperator operator = new DefaultMutationOperator();
        operator.operate(mutation, getContainer());

        assertEquals("<em>1<del title=\"bob\">2</del></em>3", getContainer().getInnerHTML());
    }

    /**
     * Operates a {@link MutationType#INSERT} mutation with a text node.
     */
    public void testInsertTextNode()
    {
        getContainer().setInnerHTML("<em>a<ins>b</ins></em>");

        Mutation mutation = new Mutation();
        mutation.setType(MutationType.INSERT);
        mutation.setLocator("0/1/1");
        mutation.setValue("c");

        MutationOperator operator = new DefaultMutationOperator();
        operator.operate(mutation, getContainer());

        assertEquals("<em>a<ins>bc</ins></em>", getContainer().getInnerHTML());
    }

    /**
     * Operates a {@link MutationType#INSERT} mutation with an attribute.
     */
    public void testAddAttribute()
    {
        getContainer().setInnerHTML("<em>1</em>");

        Mutation mutation = new Mutation();
        mutation.setType(MutationType.INSERT);
        mutation.setLocator("id");
        mutation.setValue("xwiki");

        MutationOperator operator = new DefaultMutationOperator();
        operator.operate(mutation, getContainer().getFirstChild());

        assertEquals("<em id=\"xwiki\">1</em>", getContainer().getInnerHTML());
    }

    /**
     * Operates a {@link MutationType#MODIFY} mutation on an attribute node.
     */
    public void testChangeAttribute()
    {
        getContainer().setInnerHTML("1<em title=\"toucan\">2</em>");

        Mutation mutation = new Mutation();
        mutation.setType(MutationType.MODIFY);
        mutation.setLocator("1/title");
        mutation.setValue("colibri");

        MutationOperator operator = new DefaultMutationOperator();
        operator.operate(mutation, getContainer());

        assertEquals("1<em title=\"colibri\">2</em>", getContainer().getInnerHTML());
    }

    /**
     * Operates a {@link MutationType#MODIFY} mutation on a text node.
     */
    public void testChangeText()
    {
        getContainer().setInnerHTML("<del>1<em>2</em>45</del>");

        Mutation mutation = new Mutation();
        mutation.setType(MutationType.MODIFY);
        mutation.setLocator("0/2");
        mutation.setValue("3");

        MutationOperator operator = new DefaultMutationOperator();
        operator.operate(mutation, getContainer());

        assertEquals("<del>1<em>2</em>3</del>", getContainer().getInnerHTML());
    }
}
