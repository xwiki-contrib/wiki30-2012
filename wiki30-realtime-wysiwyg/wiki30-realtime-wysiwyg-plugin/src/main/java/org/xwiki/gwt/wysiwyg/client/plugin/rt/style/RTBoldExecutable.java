package org.xwiki.gwt.wysiwyg.client.plugin.rt.style;

import com.google.gwt.dom.client.Node;
import org.xwiki.gwt.user.client.ui.rta.cmd.Executable;

/**
 * Applies a bold style on the tree model
 * @author Bogdan.Flueras@inria.fr
 */
public class RTBoldExecutable implements Executable {
    Node rta;

    public RTBoldExecutable(Node node) {
        this.rta = node;
    }

    @Override
    public boolean execute(String s) {
        return false;  //Todo
    }

    @Override
    public boolean isSupported() {
        return false;  //Todo
    }

    @Override
    public boolean isEnabled() {
        return false;  //Todo
    }

    @Override
    public boolean isExecuted() {
        return false;  //Todo
    }

    @Override
    public String getParameter() {
        return null;  //Todo
    }
}
