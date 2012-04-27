package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeUtils;
import java.util.ArrayList;

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
        if(op1.path[0]==position){
            if (op1.path[1] < posItem) {
                return op1;
            }
            int[] tab = TreeUtils.addC(op1.path,1,-1);
            if (op1.path[1] > posItem) {
                return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab, op1.text);
            }
            tab[2] = op1.path[2] + leftSiblingChildrenNr;
            return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab, op1.text);           
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        if(op1.path[0]==position){
            if (op1.path[1] < posItem) {
                return op1;
            }
            int[] tab = TreeUtils.addC(op1.path,1,-1);
            if (op1.path[1] > posItem) {
                return new TreeDeleteText(op1.getSiteId(), op1.getPosition(), tab);
            }
            tab[2] = op1.path[2] + leftSiblingChildrenNr;
            return new TreeDeleteText(op1.getSiteId(), op1.getPosition(), tab);           
        }
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
        if(op1.path[0]==position){
            if (op1.path.length == 2 && op1.path[1]== posItem-1) {//merge dans un item qui va être supprimé
                ArrayList<TreeOperation> list = new ArrayList<TreeOperation>();
                for (int i = 0; i < leftSiblingChildrenNr; i++) {
                    int[] tab = new int[3];
                    tab[0] = op1.path[0];
                    tab[1] = op1.path[1];
                    tab[2] = i;
                    list.add(new TreeDeleteTree(tab));
                }
                return new TreeCompositeOperation(list);
            }
            if (op1.path[1] < posItem) {
                return op1;
            }
            if (op1.path[1] > posItem) {
                return new TreeDeleteTree(TreeUtils.addC(op1.path,1,-1));
            }
            //meme item
            if (op1.path.length == 2) {//merge d'un item qui va être supprimé
                ArrayList<TreeOperation> list = new ArrayList<TreeOperation>();
                for (int i = 0; i < childrenNr; i++) {
                    int[] tab = new int[3];
                    tab[0]=op1.path[0];
                    tab[1] = op1.path[1] - 1;
                    tab[2] = i + leftSiblingChildrenNr;
                    list.add(new TreeDeleteTree(tab));
                }
                return new TreeCompositeOperation(list);
            }
            int[] tab = TreeUtils.addC(op1.path,1,-1);
            tab[2] = tab[2] + leftSiblingChildrenNr;
            return new TreeDeleteTree(tab);
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeStyle(TreeStyle op1) {
        if(op1.path[0]==position){
            if (op1.path[1] < posItem) {
                return op1;
            }
            int[] tab = TreeUtils.addC(op1.path,1,-1);
            if (op1.path[1] > posItem) {
                return new TreeStyle(op1.getSiteId(), tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight,op1.getTagName());
            }
            tab[2] = op1.path[2] + leftSiblingChildrenNr;
            return new TreeStyle(op1.getSiteId(), tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight,op1.getTagName());
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeCaretPosition(TreeCaretPosition op1) {
       if(op1.path[0]==position){
            if (op1.path[1] < posItem) {
                return op1;
            }
            int[] tab = TreeUtils.addC(op1.path,1,-1);
            if (op1.path[1] > posItem) {
                return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), tab);
            }
            tab[2] = op1.path[2] + leftSiblingChildrenNr;
            return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), tab);           
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMergeItem(TreeMergeItem op1) {
        if(op1.getPosition()==position){
            if (op1.posItem == posItem) {
                return new TreeIdOp();
            }
            if (op1.posItem == posItem + 1) {
                return new TreeMergeItem(op1.getSiteId(), op1.getPosition(),op1.posItem - 1, op1.leftSiblingChildrenNr + leftSiblingChildrenNr, op1.childrenNr);
            }
            if (op1.posItem == posItem - 1) {
                return new TreeMergeItem(op1.getSiteId(), op1.getPosition(),op1.posItem, op1.leftSiblingChildrenNr, op1.childrenNr + childrenNr);
            }
            if (op1.posItem < posItem) {
                return op1;
            }
            return new TreeMergeItem(op1.getSiteId(),op1.getPosition(),op1.posItem - 1, op1.leftSiblingChildrenNr, op1.childrenNr);           
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMoveItem(TreeMoveItem op1) {
        if(op1.getPosition()==position){
            int sp = op1.sp;
            int ep = op1.ep;
            if (sp == posItem) {//move de l'item de droite du merge
                if (ep == sp - 1) {
                    //si move de l'item de droite juste avant celui de gauche, annuler et garder la fusion
                    return new TreeIdOp();
                }
                //sinon deplacer l'item fusionné
                if (ep > sp) {
                    ep--;
                }
                return new TreeMoveItem(op1.getSiteId(),op1.getPosition(), sp - 1, ep);
            }
            if (sp == posItem - 1) {//move de l'item de gauche du merge
                if (ep == sp + 2) {
                    //si move de l'item de gauche juste apres celui de droite, annuler et garder la fusion
                    return new TreeIdOp();
                }
                //sinon deplacer l'item fusionné
                if (ep > sp) {
                    ep--;
                }
                return new TreeMoveItem(op1.getSiteId(),op1.getPosition(), sp, ep);
            }
            if (posItem < sp) {
                sp--;
            }
            if (posItem < ep) {
                ep--;
                //si destination du move entre les fusionnés, placer apres le resultat de la fusion
            }
            return new TreeMoveItem(op1.getSiteId(),op1.getPosition(), sp, ep);
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeNewItem(TreeNewItem op1) {
        if(op1.getPosition()==position){
             if (op1.posItem <= posItem) {
                return op1;
            }
            return new TreeNewItem(op1.getSiteId(), op1.getPosition(),op1.posItem - 1);           
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeNewList(TreeNewList op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeSplitItem(TreeSplitItem op1) {
        if(op1.getPath()[0]==position){
            if (op1.path[1] < posItem) {
                return op1;
            }
            int[] tab = TreeUtils.addC(op1.path,1,-1);
            if (op1.path[1] > posItem) {
                return new TreeSplitItem(op1.getSiteId(), op1.getPosition(), tab, op1.splitLeft);
            }
            tab[2] = tab[2] + leftSiblingChildrenNr;
            return new TreeSplitItem(op1.getSiteId(), op1.getPosition(), tab, op1.splitLeft);            
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeUpdateElement(TreeUpdateElement op1) {
        if(op1.getPath()[0]==position){
            if(op1.getPath().length==1){
                return op1;
            }
            if(op1.getPath()[1]==posItem){
                return new TreeIdOp();
            }
            if(op1.getPath()[1]<posItem){
                return op1;
            }
            return new TreeUpdateElement(op1.getSiteId(),TreeUtils.addC(op1.path,1,-1),
                    op1.tag, op1.value);
        }
        return op1;
    }
    
}