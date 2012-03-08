package fr.loria.score.jupiter.tree.operation;

import fr.loria.score.jupiter.tree.Tree;

public class TreeIdOp extends TreeOperation {

    public TreeIdOp() {}

    public void execute(Tree root) {
    }

    public TreeOperation transform(TreeOperation op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeInsertText(TreeInsertText op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeStyle(TreeStyle op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        return op1;
    }

    public String toString() {
        return "TreeIdOp(" + super.toString() + ")";
    }

    @Override
    protected TreeOperation handleTreeCaretPosition(TreeCaretPosition op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMergeItem(TreeMergeItem op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMoveItem(TreeMoveItem op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeNewItem(TreeNewItem op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeNewList(TreeNewList op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeSplitItem(TreeSplitItem op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeUpdateElement(TreeUpdateElement op1) {
        return op1;
    }
}
