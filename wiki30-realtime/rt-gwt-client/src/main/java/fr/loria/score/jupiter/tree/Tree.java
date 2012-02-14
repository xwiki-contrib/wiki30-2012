package fr.loria.score.jupiter.tree;

import java.io.Serializable;
import java.util.*;

public class Tree implements Serializable {
    /**
     * Node name: for Text nodes is "#text", for Elements is tagName
     */
    public static final String NODE_NAME = "nodeName";

    /**
     * Node value: for Text nodes it is the actual text, for Elements is null
     */
    public static final String NODE_VALUE = "nodeValue";

    /**
     * Node type: same as for native DOM elements: see below constants
     */
    public static final String NODE_TYPE = "nodeType";

    /**
     * Avoiding explicit dependency on 3rd party libs/classes (ex: GWT)
     */
    public static final short TEXT_NODE = 3;
    public static final short ELEMENT_NODE = 1;

    /**
     * The attributes for this Tree.
     * The key order is preserved during serialization/de-serialization process
     */
    protected Map<String, String> attributes = new TreeMap<String, String>();

    protected List<Tree> children = new ArrayList<Tree>();
    protected boolean invisible;
    protected Tree parent;

    protected Tree() {}

    public void setNodeName(String nodeName) {
        attributes.put(NODE_NAME, nodeName);
    }

    public String getNodeName() {
        return attributes.get(NODE_NAME);
    }

    public void setParent(Tree t) {
        this.parent = t;
    }

    public Tree getParent() {
        return parent;
    }

    public Tree getChild(int pos) {
        if (pos < nbChildren()) {
            return children.get(pos);
        }
        return null;
    }

    public String getValue() {
        return attributes.get(NODE_VALUE);
    }

    public void setValue(String nodeValue) {
        if (nodeValue != null) {
            attributes.put(NODE_VALUE, nodeValue);
        }
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes.putAll(attributes);
    }

    public Map<String, String> getAttributes() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.putAll(attributes);
        return map;
    }

    public void hide() {
        invisible = true;
    }

    public void hideChildren() {
        Iterator<Tree> it = children.iterator();
        while (it.hasNext()) {
            it.next().hide();
        }
    }

    public void show() {
        invisible = false;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void deleteChar(int pos) {
        String value = attributes.get(NODE_VALUE);
        if (pos == 0) {
            value = value.substring(1);
        } else {
            if (pos == value.length() - 1) {
                value = value.substring(0, pos);
            } else {
                value = value.substring(0, pos) + value.substring(pos + 1);
            }
        }
        setValue(value);
    }

    public void addChar(char c, int pos) {
        String value = attributes.get(NODE_VALUE);
        value = value.substring(0, pos) + c + value.substring(pos);
        setValue(value);
    }

    public void addChild(Tree t) {
        children.add(t);
        t.setParent(this);
    }

    public void addChild(Tree t, int p) {
        children.add(p, t);
        t.setParent(this);
    }

    public Tree removeChild(int p) {
        if (children.size() == p) {
            return null;
        }
        return children.remove(p);
    }

    public int nbChildren() {
        return children.size();
    }

    // todo: @Luc what do you want to do semantically? Clone all node or just the attributes?
    public Tree cloneNode() {
        Tree newTree = new Tree();
        for (Iterator<Map.Entry<String, String>> it = attributes.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            newTree.setAttribute(entry.getKey(), entry.getValue());
        }
        return newTree;
    }

    /**
     * @return a deep clone of this tree, including attributes and child clones
     */
    public Tree deepCloneNode() {
        Tree newTree = cloneNode();
        newTree.invisible = this.invisible;
        for (Tree child : children) {
            newTree.addChild(child.deepCloneNode());
        }
        return newTree;
    }

    public String split(int p) {//new value of the String value : subString from 0 to position-1. Returns new String : from position to end.
        if (String.valueOf(TEXT_NODE).equals(attributes.get(NODE_TYPE))) {
            String value = attributes.get(NODE_VALUE);
            String s1 = value.substring(0, p);
            String s2 = value.substring(p);
            setValue(s1);
            return s2;
        } else {
            return null; // todo: to check what it implies in TreeStyle usages
        }
    }

    public String toString() {
        if (invisible) {
            return "";
        }

        // simplify ouput for text-node
        if (String.valueOf(Tree.TEXT_NODE).equals(attributes.get(NODE_TYPE)))
            return "["+attributes.get(NODE_VALUE)+"]";        
        
        StringBuilder sb = new StringBuilder();
        final String tag = attributes.get(NODE_TYPE).equals(String.valueOf(ELEMENT_NODE)) ? attributes.get(NODE_NAME).toUpperCase() : attributes.get(NODE_VALUE);
        sb = sb.append("<").append(tag).append(": ");

        for (Iterator<Map.Entry<String, String>> it = attributes.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();

            if (entry.getValue() != null && (!entry.getKey().equals(NODE_NAME) && !entry.getKey().equals(NODE_VALUE) && !entry.getKey().equals(NODE_TYPE))) {
                sb = sb.append(entry.getKey()).append("=").append(entry.getValue());

                if (it.hasNext()) {
                    sb = sb.append(", ");
                }
            }
        }
        sb = sb.append(">");

        for (Tree child : children) {
            sb = sb.append(child.toString());
        }

        sb = sb.append("</").append(tag).append(">");
        return sb.toString();
    }

    public Tree getChildFromPath(int[] path) {
        Tree tree = this;
        for (int i = 0; i < path.length; i++) {
            tree = tree.getChild(path[i]);
        }
        return tree;
    }

    /**
     * @return the next sibling for a tree or null.
     * <br>It is necessary to iterate each time,
     * because we could call this method for child 0 then for child 7 so an index optimization would be useless
     */
    public Tree getNextSibling() {
        for (int i = 0; i < parent.nbChildren(); i++) {
            if (parent.getChild(i) == this) {
                return parent.getChild(i + 1); // not quite.
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) {
            return true;
        } else if (obj instanceof Tree) {
            Tree other = (Tree) obj;
            boolean equals = this.invisible == other.invisible;
            equals = equals && (this.attributes.equals(other.attributes));
            equals = equals && (this.children.equals(other.children));
            return equals;
        }
        return false;
    }
}

