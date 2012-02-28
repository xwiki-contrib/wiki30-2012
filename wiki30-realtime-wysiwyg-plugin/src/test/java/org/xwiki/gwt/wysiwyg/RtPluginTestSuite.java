package org.xwiki.gwt.wysiwyg;

import org.xwiki.gwt.wysiwyg.client.plugin.rt.RtPluginCaretPositionTest;

import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Bogdan.Flueras@inria.fr
 */
public class RtPluginTestSuite extends GWTTestSuite
{
    /**
     * @return The suite of all the client tests to be run.
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite("Realtime plugin GWT Unit Tests");
        suite.addTestSuite(RtPluginCaretPositionTest.class);

        return suite;
    }
}