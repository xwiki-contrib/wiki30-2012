package fr.loria.score.jupiter.tree;

import java.util.*;

public class Tree { // todo: has it to be serializable

    protected String value; //todo: where it is used?
    protected Map<String, String> attributes;
    protected List<Tree> children;
    protected boolean invisible;
    protected Tree parent;

    public Tree(String s) {
        this.value = s;
        attributes = new HashMap<String, String>();
        children = new ArrayList<Tree>();
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
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAttribute(String key, String value) {
        if (attributes.containsKey(key)) {
            attributes.remove(key);
        }
        attributes.put(key, value);
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
        //value.deleteChar(pos);
        if (pos == 0) {
            value = value.substring(1);
        } else {
            if (pos == value.length() - 1) {
                value = value.substring(0, pos);
            } else {
                value = value.substring(0, pos) + value.substring(pos + 1);
            }
        }
    }

    public void addChar(char c, int pos) {
        //value.addChar(c,pos);
        value = value.substring(0, pos) + c + value.substring(pos);
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
        Tree newTree = new Tree(value);
        for (Iterator<Map.Entry<String, String>> it = attributes.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            newTree.setAttribute(entry.getKey(), entry.getValue());
        }
        return newTree;
    }

    public String split(int p) {//new value of the String value : subString from 0 to position-1. Returns new String : from position to end.
        String s1 = value.substring(0, p);
        String s2 = value.substring(p);
        value = s1;
        return s2;
    }

    public String toString() {
        return toString(0);
    }

    private String toString(int i) {
        if (invisible) {
            return "";
        }
        String ta = "";
        for (int t = 0; t < i; t++) {
            ta = ta + "  ";
        }
        if (children.size() == 0) {
            return ta + value;
        }
        String s = ta + "<" + value;
        for (Iterator<Map.Entry<String, String>> it = attributes.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            s = s + " " + entry.getKey() + "=" + entry.getValue() + ",";
        }
        s = s + ">";
        for (int j = 0; j < children.size(); j++) {
            s = s + children.get(j).toString(i + 1);
        }
        s = s + ta + "</" + value + ">";
        return s;
    }

    public Tree getChildFromPath(int[] path) {
        Tree tree = this;
        for (int i = 0; i < path.length; i++) {
            tree = tree.getChild(path[i]);
        }
        return tree;
    }
}

