package fr.loria.score.jupiter.tree.operation;

import java.util.ArrayList;
import java.util.Iterator;

import fr.loria.score.jupiter.tree.Tree;

public class TreeCompositeOperation extends TreeOperation {

    private ArrayList<TreeOperation> operations = new ArrayList<TreeOperation>();

    public TreeCompositeOperation() {}

    public TreeCompositeOperation(ArrayList<TreeOperation> operations) {
        this.operations = operations;
    }

    public TreeCompositeOperation(TreeOperation... ops) {
        for (TreeOperation tOp : ops) {
            operations.add(tOp);
        }
    }

    public TreeCompositeOperation add(TreeOperation op) {
        operations.add(op);
        return this;
    }

    public void execute(Tree root) {
        Iterator<TreeOperation> it = operations.iterator();
        while (it.hasNext()) {
            it.next().execute(root);
        }
    }

    public String toString() {
        Iterator<TreeOperation> it = operations.iterator();
        String s = it.next().toString();
        while (it.hasNext()) {
            s = s + ", " + it.next().toString();
        }
        return "Composite(" + s + ")";
    }

    public TreeOperation transform(TreeOperation op1) {
        Iterator<TreeOperation> it = operations.iterator();
        TreeOperation to = op1;
        while (it.hasNext()) {
            to = (TreeOperation) it.next().transform(to);
        }
        return to;
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

    @Override
    protected TreeOperation handleTreeCaretPosition(TreeCaretPosition op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeMergeItem(TreeMergeItem op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeMoveItem(TreeMoveItem op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeNewItem(TreeNewItem op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeNewList(TreeNewList op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeSplitItem(TreeSplitItem op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeUpdateElement(TreeUpdateElement op1) {
        return null;
    }
}
