package fr.loria.score.jupiter.tree.operation;

import fr.loria.score.jupiter.tree.Tree;

import java.util.List;

public class TreeDeleteTree extends TreeOperation {

    public TreeDeleteTree() {}

    public TreeDeleteTree(List<Integer> path) {
        setPath(path);
    }

    public void execute(Tree root) {
        Tree tree = root.getChildFromPath(path);
        tree.hide();
    }

    public String toString() {
        return "DeleteTree(" + super.toString() + path + ")";
    }

    public TreeOperation transform(TreeOperation op1) {
        return transp(op1);
    }

    @Override
    public void updateUI() {
        //Todo
    }

    //OT pour DeleteTree
    private TreeOperation transp(TreeOperation op1) {
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
}
