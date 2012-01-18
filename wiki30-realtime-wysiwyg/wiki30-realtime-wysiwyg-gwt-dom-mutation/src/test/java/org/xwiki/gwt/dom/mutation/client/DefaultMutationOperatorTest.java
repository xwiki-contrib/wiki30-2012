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
 * @version $Id: 33e7f9c6d97574588a41c2fe88090afcbb65bf38 $
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
        mutation.setLocator("0");
        mutation.setValue("1,<del title=\"bob\">2</del>");

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
        mutation.setLocator("0/1");
        mutation.setValue("1,c");

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
     * Tests how a mutation that changes the value of a text node is operated.
     * 
     * @param text the text to be changed
     * @param mutationType the type of text mutation
     * @param mutationValue the mutation value, specifying which part of the text will be changed
     * @param expectedText the expected text after the mutation is executed
     */
    private void testChangeText(String text, MutationType mutationType, String mutationValue, String expectedText)
    {
        getContainer().appendChild(getContainer().getOwnerDocument().createTextNode(text));

        Mutation mutation = new Mutation();
        mutation.setType(mutationType);
        mutation.setLocator(String.valueOf('0'));
        mutation.setValue(mutationValue);

        MutationOperator operator = new DefaultMutationOperator();
        operator.operate(mutation, getContainer());

        assertEquals(expectedText, getContainer().getFirstChild().getNodeValue());
    }

    /**
     * Tests how a mutation that inserts some characters is executed.
     */
    public void testInsertCharacters()
    {
        testChangeText("int", MutationType.INSERT, "2,ser", "insert");
    }

    /**
     * Tests how a mutation that deletes some characters is executed.
     */
    public void testDeleteCharacters()
    {
        testChangeText("toucan", MutationType.REMOVE, "3,6", "tou");
    }

    public void testDeleteSingleCharacter()
    {
        //todo: assumes that DMO.deleteText mutationValue is composed of 2 parts, which might not be always the case
        testChangeText("ABCD", MutationType.REMOVE, "0,1", "zzzzBCD");  // it doesn't fail
        fail("This test is failing, fix the line above");

        testChangeText("ABCD", MutationType.REMOVE, "1,2", "ACD");
        testChangeText("ABCD", MutationType.REMOVE, "2,3", "ABD");
        testChangeText("ABCD", MutationType.REMOVE, "3,4", "ABC");

    }

    public void testInsertNewParagraph()
    {
        getContainer().setInnerHTML("<p>abcd</p>");
        Mutation m = new Mutation();
        m.setType(MutationType.INSERT);
        m.setLocator("1/0");
        m.setValue("2");

        MutationOperator mo = new DefaultMutationOperator();
        mo.operate(m, getContainer());
        assertEquals("<p>ab</p><p>cd</p>", getContainer().getInnerHTML());
    }

    /**
     * Tests how a mutation that replaces some characters is executed.
     */
    public void testReplaceCharacters()
    {
        testChangeText("wiki", MutationType.MODIFY, "0,1,XW", "XWiki");
    }
}
