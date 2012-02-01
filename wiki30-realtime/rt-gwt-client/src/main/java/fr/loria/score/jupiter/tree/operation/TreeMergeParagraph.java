package fr.loria.score.jupiter.tree.operation;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeUtils;

import java.util.ArrayList;

public class TreeMergeParagraph extends TreeOperation {

    /**
     * You have 2 or more paragraphs siblings on same level each one with a virtual index (0 based).
     * Merge position is the index number between the paragraphs siblings to be merged.
     * Ex: Merge position = 2 for merging the last 2 paragraphs siblings on a 3 paragraphs
     */
//    public int position;   -> merge position

    /**
     * Nr of children of the left sibling subtree before the merge
     */
    public int leftSiblingChildrenNr;

    /**
     * Nr of children of the tree before the merge
     */
    public int childrenNr;

    public TreeMergeParagraph() {}

    public TreeMergeParagraph(int siteId, int position, int leftSiblingChildrenNr, int childrenNr) {
        this(position, leftSiblingChildrenNr, childrenNr);
        this.siteId = siteId;
    }

    public TreeMergeParagraph(int position, int leftSiblingChildrenNr, int childrenNr) {
        this.position = position;
        this.leftSiblingChildrenNr = leftSiblingChildrenNr;
        this.childrenNr = childrenNr;
    }

    public void execute(Tree root) {
        Tree tree = root;
        Tree leftSibling = tree.getChild(position - 1);
        Tree rightSibling = tree.getChild(position);
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
        tree.removeChild(position);
    }

    public String toString() {
        return "TreeMergeParagraph(" + super.toString() + ", leftSiblingChildrenNr: " + leftSiblingChildrenNr + ", childrenNr: " + childrenNr + ")";
    }

    //OT pour MergeP
    public TreeOperation handleTreeInsertText(TreeInsertText op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, -1);
        if (op1.path[0] > position) {
            return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab, op1.text);
        }
        tab[1] = op1.path[1] + leftSiblingChildrenNr;
        return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab, op1.text);
    }

    public TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, -1);
        if (op1.path[0] > position) {
            return new TreeDeleteText(op1.getSiteId(), op1.getPosition(), tab);
        }
        tab[1] = op1.path[1] + leftSiblingChildrenNr;
        return new TreeDeleteText(op1.getSiteId(), op1.getPosition(), tab);
    }

    public TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {
        if (op1.getPosition() <= position) {
            return op1;
        }
        return new TreeNewParagraph(op1.getSiteId(), op1.getPosition() - 1);
    }

    public TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        if (op1.getPosition() == position) {
            return new TreeIdOp();
        }
        if (op1.getPosition() == position + 1) {
            return new TreeMergeParagraph(op1.getPosition() - 1, op1.leftSiblingChildrenNr + leftSiblingChildrenNr, op1.childrenNr);
        }
        if (op1.getPosition() == position - 1) {
            return new TreeMergeParagraph(op1.getPosition(), op1.leftSiblingChildrenNr, op1.childrenNr + childrenNr);
        }
        if (op1.getPosition() < position) {
            return op1;
        }
        return new TreeMergeParagraph(op1.getPosition() - 1, op1.leftSiblingChildrenNr, op1.childrenNr);
    }

    public TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, -1);
        if (op1.path[0] > position) {
            return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition(), tab, op1.splitLeft);
        }
        tab[1] = tab[1] + leftSiblingChildrenNr;
        return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition(), tab, op1.splitLeft);
    }

    public TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        if (op1.path.length == 1 && op1.path[0] == position - 1) {//merge dans un paragraphe qui va être supprimé
            ArrayList<TreeOperation> list = new ArrayList<TreeOperation>();
            for (int i = 0; i < leftSiblingChildrenNr; i++) {
                int[] tab = new int[2];
                tab[0] = op1.path[0];
                tab[1] = i;
                list.add(new TreeDeleteTree(tab));
            }
            return new TreeCompositeOperation(list);
        }
        if (op1.path[0] < position) {
            return op1;
        }
        if (op1.path[0] > position) {
            return new TreeDeleteTree(TreeUtils.addP(op1.path, -1));
        }
        //meme paragraphe
        if (op1.path.length == 1) {//merge d'un paragraphe qui va être supprimé
            ArrayList<TreeOperation> list = new ArrayList<TreeOperation>();
            for (int i = 0; i < childrenNr; i++) {
                int[] tab = new int[2];
                tab[0] = op1.path[0] - 1;
                tab[1] = i + leftSiblingChildrenNr;
                list.add(new TreeDeleteTree(tab));
            }
            return new TreeCompositeOperation(list);
        }
        int[] tab = TreeUtils.addP(op1.path, -1);
        tab[1] = tab[1] + leftSiblingChildrenNr;
        return new TreeDeleteTree(tab);
    }

    public TreeOperation handleTreeStyle(TreeStyle op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, -1);
        if (op1.path[0] > position) {
            return new TreeStyle(op1.getSiteId(), tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight);
        }
        tab[1] = op1.path[1] + leftSiblingChildrenNr;
        return new TreeStyle(op1.getSiteId(), tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight);
    }

    public TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        int sp = op1.sp;
        int ep = op1.ep;
        if (sp == position) {//move du paragraphe de droite du merge
            if (ep == sp - 1) {
                //si move du paragraphe de droite juste avant celui de gauche, annuler et garder la fusion
                return new TreeIdOp();
            }
            //sinon deplacer le paragraphe fusionné
            if (ep > sp) {
                ep--;
            }
            return new TreeMoveParagraph(op1.getSiteId(), sp - 1, ep);
        }
        if (sp == position - 1) {//move du paragraphe de gauche du merge
            if (ep == sp + 2) {
                //si move du paragraphe de gauche juste apres celui de droite, annuler et garder la fusion
                return new TreeIdOp();
            }
            //sinon deplacer le paragraphe fusionné
            if (ep > sp) {
                ep--;
            }
            return new TreeMoveParagraph(op1.getSiteId(), sp, ep);
        }
        if (position < sp) {
            sp--;
        }
        if (position < ep) {
            ep--;
            //si destination du move entre les fusionnés, placer apres le resultat de la fusion
        }
        return new TreeMoveParagraph(op1.getSiteId(), sp, ep);
    }
}
