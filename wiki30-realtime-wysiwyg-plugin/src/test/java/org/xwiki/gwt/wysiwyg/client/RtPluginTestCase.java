package org.xwiki.gwt.wysiwyg.client;

import org.xwiki.gwt.dom.client.Document;
import org.xwiki.gwt.dom.client.Element;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * @author: Bogdan.Flueras@inria.fr
 */
public class RtPluginTestCase extends GWTTestCase
{
    public static final String MODULE_NAME = "org.xwiki.gwt.wysiwyg.RealTimePlugin";

    /**
     * The document in which we run the tests.
     */
    private Document document;

    /**
     * The paragraph DOM element in which we run the tests.
     */
    private Element container;

    @Override public String getModuleName()
    {
        return MODULE_NAME;
    }

    /**
     * {@inheritDoc}
     *
     * @see GWTTestCase#gwtSetUp()
     */
    protected void gwtSetUp() throws Exception
    {
        super.gwtSetUp();

        document = Document.get().cast();
        container = document.createPElement().cast();
        document.getBody().appendChild(container);
    }

    /**
     * {@inheritDoc}
     *
     * @see GWTTestCase#gwtTearDown()
     */
    protected void gwtTearDown() throws Exception
    {
        super.gwtTearDown();

        container.getParentNode().removeChild(container);
    }

    /**
     * @return the document in which we run the tests
     */
    protected Document getDocument()
    {
        return document;
    }

    /**
     * @return the DOM element in which we run the tests
     */
    protected Element getContainer()
    {
        return container;
    }
}
