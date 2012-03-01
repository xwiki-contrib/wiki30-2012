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
        return null;
    }

    @Override
    protected TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeStyle(TreeStyle op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        return null;
    }

    public String toString() {
        return "TreeIdOp(" + super.toString() + ")";
    }

    @Override
    protected TreeOperation handleTreeCursorPosition(TreeCursorPosition op1) {
        return null;
    }
}
