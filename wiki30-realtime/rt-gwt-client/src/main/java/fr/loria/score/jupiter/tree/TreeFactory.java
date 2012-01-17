package fr.loria.score.jupiter.tree;

/**
 * Factory method for creating various types of Tree's: paragraphs, texts aso.
 * This is the recommended way to create instances of Tree and not the constructor,even in this package
 *
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeFactory {
    /**
     * Avoiding explicit dependency on 3rd party libs/classes (ex: GWT)
     */
    public static final short TEXT_NODE = 3;
    public static final short ELEMENT_NODE = 1;

    /**
     * @return an empty tree
     */
    public static Tree createEmptyTree() {
        return new Tree();
    }

    /**
     * @return a paragraph tree
     */
    public static Tree createParagraphTree() {
        Tree t = createElementTree("p");
        t.setValue(null);
        return t;
    }

    /**
     * @param text the value of this text tree
     * @return a text tree
     */
    public static Tree createTextTree(String text) {
        Tree t = createEmptyTree();
        t.setAttribute(Tree.NODE_TYPE, String.valueOf(TEXT_NODE));
        t.setNodeName("#text");
        t.setValue(text);
        return t;
    }

    public static Tree createElementTree(String tagName) {
        Tree t = createEmptyTree();
        t.setAttribute(Tree.NODE_TYPE, String.valueOf(ELEMENT_NODE));
        t.setNodeName(tagName);
        return t;
    }
}
