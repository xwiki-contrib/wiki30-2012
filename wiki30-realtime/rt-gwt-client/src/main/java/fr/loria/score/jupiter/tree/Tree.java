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
     * Node type: same as for native DOM elements
     */
    public static final String NODE_TYPE = "nodeType";

    protected Map<String, String> attributes = new HashMap<String, String>();
    protected List<Tree> children = new ArrayList<Tree>();
    protected boolean invisible;
    protected Tree parent;

    public Tree() {}

    public Tree(String nodeName, String nodeValue) {
        setNodeName(nodeName);
        setValue(nodeValue);
    }

    public void setNodeName(String nodeName) {
//        if (nodeName != null) {
            attributes.put(NODE_NAME, nodeName);
//        }
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
        if (attributes.containsKey(key)) {
            attributes.remove(key);
        }
        attributes.put(key, value);
    }

    public void setAttributes( Map<String, String> attributes) {
        this.attributes.putAll(attributes);
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

    public Tree cloneNode() {
        Tree newTree = new Tree();
        for (Iterator<Map.Entry<String, String>> it = attributes.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            newTree.setAttribute(entry.getKey(), entry.getValue());
        }
        return newTree;
    }

    public String split(int p) {//new value of the String value : subString from 0 to position-1. Returns new String : from position to end.
        String value = attributes.get(NODE_VALUE);
        if (value != null) {
            String s1 = value.substring(0, p);
            String s2 = value.substring(p);
            value = s1;
            setValue(value);
            return s2;
        } else {
            return ""; // todo: this is not good.
        }
    }

    public String toString() {
        if (invisible) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        final String tag = attributes.get(NODE_VALUE) != null ? attributes.get(NODE_VALUE) : attributes.get(NODE_NAME);
        sb = sb.append("<").append(tag).append(":");

        for (Iterator<Map.Entry<String, String>> it = attributes.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();

            if (entry.getValue() != null && (!entry.getKey().equals(NODE_NAME) && !entry.getKey().equals(NODE_VALUE))) {
                sb = sb.append(" ").append(entry.getKey()).append("=").append(entry.getValue());
            }

            if (it.hasNext()) {
                sb = sb.append(", ");
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
}

