package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.TreeUtils;

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
        if(op1.path[0]==position){
            if (op1.path[1] < posItem) {
                return op1;
            }
            int[] tab = TreeUtils.addC(op1.path,1,1);
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
            int[] tab = TreeUtils.addC(op1.path,1,1);
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
            if (op1.path[1] < posItem) {
                return op1;
            }
            int[] tab = TreeUtils.addC(op1.path,1,1);
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
            int[] tab = TreeUtils.addC(op1.path,1,1);
            return new TreeStyle(op1.getSiteId(), tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight);
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
            if(op1.getSiteId()!=siteId){
                if (op1.path[1] < posItem) {
                    return op1;
                }
                int[] tab = TreeUtils.addC(op1.path,1,1);
                return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), tab);
            }

            //same id : cursor at start or end of a item, press enter
            //start : op1.path[1]==posItem
            //end : op1.path[1]==posItem+1

            if(op1.getPath()[1]==posItem){//start
                int[] tab = TreeUtils.addC(op1.path,1,1);
                return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), tab);   
            }
            //else : end
            int[] tab=new int[3];
            tab[0]=position;
            tab[1]=posItem;
            tab[2]=0;
            return new TreeCaretPosition(op1.getSiteId(),0,tab);
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMergeItem(TreeMergeItem op1) {
        if(op1.getPosition()==position){
            if (op1.posItem == posItem) {
                return new TreeCompositeOperation(new TreeMoveItem(op1.getSiteId(), position,posItem, posItem + 2), op1);
            }
            if (op1.posItem < posItem) {
                return op1;
            }
            return new TreeMergeItem(op1.getSiteId(), op1.getPosition(),op1.posItem+1, op1.leftSiblingChildrenNr, op1.childrenNr);
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMoveItem(TreeMoveItem op1) {
        if(op1.getPosition()==position){
            int sp = op1.sp;
            int ep = op1.ep;
            if (posItem <= sp) {
                sp++;
            }
            if (posItem < ep) {
                ep++;
            }
            return new TreeMoveItem(op1.getSiteId(),op1.getPosition(), sp, ep);   
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeNewItem(TreeNewItem op1) {
        if(op1.getPosition()==position){
            if (op1.posItem < posItem) {
                return op1;
            }
            if (op1.posItem == posItem && op1.siteId < siteId) {
                return op1;
            }
            return new TreeNewItem(op1.getSiteId(), op1.getPosition(),op1.posItem + 1);
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeNewList(TreeNewList op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeSplitItem(TreeSplitItem op1) {
        if(op1.path[0]==position){
            if (op1.path[1] < posItem) {
                return op1;
            }
            int[] tab = TreeUtils.addC(op1.path,1,1);
            return new TreeSplitItem(op1.getSiteId(), op1.getPosition(), tab, op1.splitLeft);   
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeUpdateElement(TreeUpdateElement op1) {
        if(op1.path[0]==position && op1.path.length==2){
            if (op1.path[1] < posItem) {
                return op1;
            }
            return new TreeUpdateElement(op1.getSiteId(), TreeUtils.addC(op1.path,1,1),
                    op1.tag, op1.value);
        }
        return op1;
    }
    
}