package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeUtils;

public class TreeMoveItem extends TreeOperation{
    
    public int sp;//initial position
    public int ep;//new position

    public TreeMoveItem(int siteId, int position, int s, int e) {
        super(siteId,position);
        sp = s;
        ep = e;
    }    

    @Override
    public void execute(Tree root) {
        Tree tree = root.getChild(position);
        Tree t1 = tree.removeChild(sp);
        if (sp >= ep) {
            tree.addChild(t1, ep);
        } else {
            tree.addChild(t1, ep - 1);
        }
    }

    @Override
    protected TreeOperation handleTreeInsertText(TreeInsertText op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeStyle(TreeStyle op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeCaretPosition(TreeCaretPosition op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeMergeItem(TreeMergeItem op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeMoveItem(TreeMoveItem op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeNewItem(TreeNewItem op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeNewList(TreeNewList op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeSplitItem(TreeSplitItem op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected TreeOperation handleTreeUpdateElement(TreeUpdateElement op1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}