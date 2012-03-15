package fr.loria.score.jupiter.tree;

/**
 * Factory method for creating various types of Tree's: paragraphs, texts aso.
 * This is the recommended way to create instances of Tree and not the constructor,even in this package
 *
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeFactory {

    /**
     * @return an empty tree
     */
    public static Tree createEmptyTree() {
        Tree t = new Tree();
        t.setNodeName("");
        t.setAttribute(Tree.NODE_TYPE, String.valueOf(Tree.ELEMENT_NODE));
        return t;
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
     * @return a heading tree
     */
    public static Tree createHeadingTree(int level) {
        Tree t = createElementTree("h" + level);
        t.setValue(null);
        return t;
    }

    /**
     * @return a horizontal rule tree
     */
    public static Tree createHorizontalRuleTree() {
        Tree t = createElementTree("hr");
        t.setValue(null);
        return t;
    }
    
    /**
     * @param text the value of this text tree
     * @return a text tree
     */
    public static Tree createTextTree(String text) {
        Tree t = createEmptyTree();
        t.setAttribute(Tree.NODE_TYPE, String.valueOf(Tree.TEXT_NODE));
        t.setNodeName("#text");
        t.setValue(text);
        return t;
    }

    public static Tree createElementTree(String tagName) {
        Tree t = createEmptyTree();
        t.setNodeName(tagName);
        return t;
    }
}
