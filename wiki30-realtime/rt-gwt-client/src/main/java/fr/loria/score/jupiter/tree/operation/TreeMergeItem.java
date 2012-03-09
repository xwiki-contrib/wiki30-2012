package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;

public class TreeMergeItem extends TreeOperation{
    
    public int posItem;
    
    /**
     * Nr of children of the left sibling subtree before the merge
     */
    public int leftSiblingChildrenNr;

    /**
     * Nr of children of the tree before the merge
     */
    public int childrenNr;

    public TreeMergeItem()
    {
    }

    public TreeMergeItem(int siteId,int position,int posItem,int lc,int rc){
        super(siteId,position);
        this.posItem=posItem;
        leftSiblingChildrenNr = lc;
        childrenNr =rc;
    }

    @Override
    public void execute(Tree root) {
        Tree tree = root.getChild(position);
        Tree leftSibling = tree.getChild(posItem - 1);
        Tree rightSibling = tree.getChild(posItem);
        Tree t4;
        if (leftSibling.isInvisible() && !rightSibling.isInvisible()) {
            leftSibling.hideChildren();
            leftSibling.show();
        }
        if (rightSibling.isInvisible() && !leftSibling.isInvisible()) {
            rightSibling.hideChildren();
        }
        while ((t4 = rightSibling.removeChild(0)) != null) {
            leftSibling.addChild(t4);
        }
        tree.removeChild(posItem);
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