package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;

public class TreeNewList extends TreeOperation{

    public TreeNewList()
    {
    }

    public TreeNewList(int siteId, int position) {
        super(siteId, position);
    }

    @Override
    public void execute(Tree root) {
        Tree list = TreeFactory.createElementTree("list");
        Tree item = TreeFactory.createElementTree("item");
        Tree text = TreeFactory.createTextTree("");
        item.addChild(text);
        list.addChild(item);
        root.addChild(list, position);
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