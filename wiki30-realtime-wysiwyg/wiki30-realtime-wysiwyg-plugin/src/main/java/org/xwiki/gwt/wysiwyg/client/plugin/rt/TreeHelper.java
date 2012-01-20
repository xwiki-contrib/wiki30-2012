package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import com.google.gwt.dom.client.Node;

/**
 * @author: Bogdan.Flueras@inria.fr
 */
public class TreeHelper {

    /**
     * @param root the node where the locator is relative to
     * @param path the location/path
     * @return the child node at the locator value starting from the root node
     */
    public static Node getChildNodeFromLocator(Node root, int[] path)
    {
        Node targetNode = root;
        for (int i = 0; i < path.length; i++) {
            targetNode = targetNode.getChildNodes().getItem(path[i]);
        }
        return targetNode;
    }
}
