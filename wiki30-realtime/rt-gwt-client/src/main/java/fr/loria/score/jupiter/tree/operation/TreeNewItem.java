package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;

public class TreeNewItem extends TreeOperation{
    
    public int posItem;

    public TreeNewItem()
    {
    }

    public TreeNewItem(int siteId,int position, int posItem){
        super(siteId,position);
        this.posItem=posItem;
    }

    @Override
    public void execute(Tree root) {
        Tree item=TreeFactory.createElementTree("item");
        Tree text=TreeFactory.createTextTree("");
        item.addChild(text);
        Tree list=root.getChild(position);
        list.addChild(item, posItem);
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