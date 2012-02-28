package org.xwiki.gwt.wysiwyg.client.plugin.rt;

import java.util.ArrayList;
import java.util.List;

import org.xwiki.gwt.dom.client.DOMUtils;

import com.google.gwt.dom.client.Node;

/**
 * @author Bogdan.Flueras@inria.fr  todo: move to EditorUtils
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

    /**
     * @param node a DOM node
     * @return the path from the BODY element of the document that owns the given node to the node; a path item is the
     *         index of the corresponding note among its siblings.
     */
    public static List<Integer> getLocator(Node node)
    {
        List<Integer> locator = new ArrayList<Integer>();
        Node ancestor = node;
        while (ancestor != null && ancestor != node.getOwnerDocument().getBody()) {
            locator.add(0, DOMUtils.getInstance().getNodeIndex(ancestor));
            ancestor = ancestor.getParentNode();
        }
        return locator;
    }

    /**
     * Converts a list of {@link Integer} objects to an array of integer numbers.
     *
     * @param list the list to be converted
     * @return an array of integer numbers
     */
    public static int[] toIntArray(List<Integer> list)
    {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
