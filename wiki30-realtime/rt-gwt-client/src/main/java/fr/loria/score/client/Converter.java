package fr.loria.score.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import fr.loria.score.jupiter.tree.Tree;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * Utility class to convert back and forth from a GWT DOM to a custom tree like object model.
 * Adapting XWiki's DepthFirstPreOrderIterator to create the custom tree like model
 *
 * @author Bogdan.Flueras@inria.fr
 */
public class Converter {
    public static final Logger log = Logger.getLogger(Converter.class.getName());

    /**
     * Converts a native DOM element into a serializable, hierarchical object upon which OT functions work
     * @param nativeElement the native DOM element
     * @return a custom tree like object
     */
    public Tree fromNativeToCustom(Element nativeElement) {
        log.finest("Native DOM element: " + nativeElement.toString());
        Tree root = new Tree();
        DepthFirstPreOrderIterator dfs = new DepthFirstPreOrderIterator(nativeElement, root);
        while (dfs.hasNext()) {
            dfs.next();
        }
        log.finest("Returning tree: " + root.toString());
        return root;
    }


    class DepthFirstPreOrderIterator {
        /**
         * The current position of the iterator.
         */
        private Node currentNode;

        /**
         * The node where the iteration has started (the root of the subtree which we're iterating).
         */
        private Node startNode;

        /**
         * The node which is to be filled in from the currentNode
         */
        private Tree currentTree;

        /**
         * Creates an iterator for the subtree rooted in startNode.
         *
         * @param startNode root of the subtree to iterate through.
         * @param startTree root of the custom tree to create
         */
        DepthFirstPreOrderIterator(Node startNode, Tree startTree) {
            this.startNode = startNode;
            this.currentNode = startNode;

            this.currentTree = startTree;
            copyAttributes(currentNode, startTree);
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            return this.currentNode != null;
        }

        /**
         * {@inheritDoc}
         */
        public Node next() {
            // return the currentNode
            Node nodeToReturn = this.currentNode;
            log.finest("Current node:" + currentNode);

            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // compute the next node
            // try to go down
            if (currentNode.getFirstChild() != null) {
                this.currentNode = currentNode.getFirstChild();

                Tree child = new Tree();
                child.setParent(currentTree);
                currentTree.addChild(child);
                copyAttributes(currentNode, child);

                currentTree = child;
            } else {
                // try to go right: from this node or any of its ancestors, until we haven't reached the startNode
                Node ancestor = currentNode;
                while (ancestor != startNode) {
                    if (ancestor.getNextSibling() != null) {
                        this.currentNode = ancestor.getNextSibling();

                        Tree parent = currentTree.getParent();

                        Tree child = new Tree();

                        child.setParent(parent);
                        parent.addChild(child);
                        copyAttributes(currentNode, child);

                        currentTree = child;

                        break;
                    }
                    ancestor = ancestor.getParentNode();
                    currentTree = currentTree.getParent();
                }
                // if we got back to the root searching up, then we have no more options
                if (ancestor == startNode) {
                    this.currentNode = null;
                }
            }
            return nodeToReturn;
        }
    }

    private void copyAttributes(Node currentNode, Tree treeNode) {
        Map<String, String> attributes = new HashMap<String, String>();

        putIfValueNotNull(attributes, Tree.NODE_VALUE, currentNode.getNodeName());
        putIfValueNotNull(attributes, Tree.NODE_VALUE, currentNode.getNodeValue());

        int type = currentNode.getNodeType();
        putIfValueNotNull(attributes, Tree.NODE_TYPE, String.valueOf(type));

        if (type == Node.ELEMENT_NODE) {
            Element element = (Element) currentNode;

            putIfValueNotNull(attributes, "className", element.getClassName());
            putIfValueNotNull(attributes, "id", element.getId());
            putIfValueNotNull(attributes, "style", element.getStyle().toString());
            putIfValueNotNull(attributes, "tagName", element.getTagName());
            putIfValueNotNull(attributes, "title", element.getTitle());
        } else if (type == Node.TEXT_NODE) {
            Text textElement = (Text) currentNode;
            treeNode.setValue(textElement.getData());
        }
        treeNode.setAttributes(attributes);
    }

    private <K, V> void putIfValueNotNull(Map<K, V> map, K key, V value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
