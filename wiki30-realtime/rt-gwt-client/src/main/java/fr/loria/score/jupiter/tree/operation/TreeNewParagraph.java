package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.TreeUtils;

public class TreeNewParagraph extends TreeOperation {

    public TreeNewParagraph() {}

    public TreeNewParagraph(int siteId, int position) {
        super(siteId, position);
    }

    public void execute(Tree root) {
        Tree paragraph = TreeFactory.createParagraphTree();
        root.addChild(paragraph, position);
    }

    public String toString() {
        return "NewP(" + super.toString() + ")";
    }

    //OT pour NewP
    public TreeOperation handleTreeInsertText(TreeInsertText op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, 1);
        return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab, op1.text);
    }

    public TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, 1);
        return new TreeDeleteText(op1.getSiteId(), op1.getPosition(), tab);
    }

    public TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {
        if (op1.getPosition() < position) {
            return op1;
        }
        if (op1.getPosition() == position && op1.siteId < siteId) {
            return op1;
        }
        return new TreeNewParagraph(op1.siteId, op1.getPosition() + 1);
    }

    public TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        if (op1.getPosition() == position) {
            return new TreeCompositeOperation(new TreeMoveParagraph(op1.getSiteId(), position, position + 2), op1);
        }
        if (op1.getPosition() < position) {
            return op1;
        }
        return new TreeMergeParagraph(op1.getPosition() + 1, op1.leftSiblingChildrenNr, op1.rightSiblingChildrenNr);
    }

    public TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, 1);
        return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition(), tab, op1.splitLeft);
    }

    public TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, 1);
        return new TreeDeleteTree(tab);
    }

    public TreeOperation handleTreeStyle(TreeStyle op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, 1);
        return new TreeStyle(op1.getSiteId(), tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight);
    }

    public TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {//si deplacement au meme endroit que creation, depl<crea
        int sp = op1.sp;
        int ep = op1.ep;
        if (position <= sp) {
            sp++;
        }
        if (position < ep) {
            ep++;
        }
        return new TreeMoveParagraph(op1.getSiteId(), sp, ep);
    }
}
