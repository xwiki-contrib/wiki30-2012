package fr.loria.score;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;

/**
 * A little DSL to simplify test writing.
 *
 * @author Gerald.Oster@loria.fr
 */
public class TreeDSL {
        private Tree wrappedTree;
        public TreeDSL(Tree t) {
            this.wrappedTree = t;
        }
    
        public TreeDSL addChild(TreeDSL... children) {
            for (TreeDSL c : children) {
                this.wrappedTree.addChild(c.wrappedTree);
            }
            return this;
                
        }
        
        public void removeChild(int i) {
            this.wrappedTree.removeChild(i);
        }
        
        public TreeDSL getChild(int i) {
            return new TreeDSL(this.wrappedTree.getChild(i));
        }

        public void removeChild() {
            for (int i=0; i<this.wrappedTree.nbChildren(); i++) {
                this.wrappedTree.removeChild(i);
            }
        }

        public void clear() {
            removeChild();
        }

        public TreeDSL setAttribute(String styleName, String styleValue) {
            if (! this.wrappedTree.getNodeName().equals("span"))
                throw new UnsupportedOperationException("Not supported by this DSL");
            this.wrappedTree.setAttribute(styleName, styleValue);
            return this;
        }
    

    public static TreeDSL paragraph() {
        return new TreeDSL(TreeFactory.createParagraphTree());
    }
    public static TreeDSL text(String str) {
        return new TreeDSL(TreeFactory.createTextTree(str));
    }
    public static TreeDSL span(String styleName, String styleValue) {        
        Tree span = TreeFactory.createElementTree("span");
        span.setAttribute(styleName, styleValue);
        return new TreeDSL(span);
    }
     
}
