package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.TreeUtils;

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
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, 1);
        return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab, op1.text);
    }

    @Override
    protected TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, 1);
        return new TreeDeleteText(op1.getSiteId(), op1.getPosition(), tab);
    }

    @Override
    protected TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {
        if (op1.getPosition() < position) {
            return op1;
        }
        if (op1.getPosition() == position && op1.getSiteId() < siteId) {
            return op1;
        }
        return new TreeNewParagraph(op1.getSiteId(), op1.getPosition() + 1);
    }

    @Override
    protected TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        if (op1.getPosition() == position) {
            return new TreeCompositeOperation(new TreeMoveParagraph(op1.getSiteId(), position, position + 2), op1);
        }
        if (op1.getPosition() < position) {
            return op1;
        }
        return new TreeMergeParagraph(op1.getPosition() + 1, op1.leftSiblingChildrenNr, op1.childrenNr);
    }

    @Override
    protected TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, 1);
        return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition(), tab, op1.splitLeft);
    }

    @Override
    protected TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, 1);
        return new TreeDeleteTree(tab);
    }

    @Override
    protected TreeOperation handleTreeStyle(TreeStyle op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        int[] tab = TreeUtils.addP(op1.path, 1);
        return new TreeStyle(op1.getSiteId(), tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight,op1.getTagName());
    }

    @Override
    protected TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
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

    @Override
    protected TreeOperation handleTreeCaretPosition(TreeCaretPosition op1) {
        if(op1.getSiteId()!=siteId){
            if (op1.path[0] < position) {
                return op1;
            }
            int[] tab = TreeUtils.addP(op1.path, 1);
            return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), tab);
        }
        if(op1.getPath()[0]==position){//start
            int[] tab = TreeUtils.addP(op1.path, 1);
            return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), tab);   
        }
        //else : end
        int[] tab=new int[2];
        tab[0]=position;
        tab[1]=0;
        return new TreeCaretPosition(op1.getSiteId(),0,tab);        
    }

    @Override
    protected TreeOperation handleTreeMergeItem(TreeMergeItem op1) {
        if (op1.getPosition() < position) {
            return op1;
        }
        return new TreeMergeItem(op1.getSiteId(), op1.getPosition()+1, op1.posItem,
                op1.leftSiblingChildrenNr, op1.childrenNr);
    }

    @Override
    protected TreeOperation handleTreeMoveItem(TreeMoveItem op1) {
        if (op1.getPosition() < position) {
            return op1;
        }
        return new TreeMoveItem(op1.getSiteId(), op1.getPosition(), op1.sp, op1.ep);
    }

    @Override
    protected TreeOperation handleTreeNewItem(TreeNewItem op1) {
        if (op1.getPosition() < position) {
            return op1;
        }
        return new TreeNewItem(op1.getSiteId(), op1.getPosition()+1, op1.posItem);
    }

    @Override
    protected TreeOperation handleTreeNewList(TreeNewList op1) {
        if (op1.getPosition() < position) {
            return op1;
        }
        if (op1.getPosition() == position && op1.getSiteId() < siteId) {
            return op1;
        }
        return new TreeNewList(op1.getSiteId(), op1.getPosition() + 1);
    }

    @Override
    protected TreeOperation handleTreeSplitItem(TreeSplitItem op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        return new TreeSplitItem(op1.getSiteId(), op1.getPosition(),
                TreeUtils.addP(op1.path , 1), op1.splitLeft);
    }

    @Override
    protected TreeOperation handleTreeUpdateElement(TreeUpdateElement op1) {
        if (op1.path[0] < position) {
            return op1;
        }
        return new TreeUpdateElement(op1.getSiteId(), TreeUtils.addP(op1.path, 1),
                op1.tag, op1.value);
    }
}