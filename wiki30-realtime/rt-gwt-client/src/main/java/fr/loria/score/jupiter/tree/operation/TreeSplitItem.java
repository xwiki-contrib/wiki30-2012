package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;


public class TreeSplitItem extends TreeOperation{
    
    public boolean splitLeft;//split left : true if position<>0(generation)

    public TreeSplitItem()
    {
    }

    public TreeSplitItem(int siteId, int position, int[] path)
    {
        super(siteId, position);
        setPath(path);
        if (position != 0) {
            this.splitLeft = true;
        } else {
            this.splitLeft = false;
        }
    }

    /**
     * This constructor should be used only by transformation functions.
     */
    public TreeSplitItem(int siteId, int position, int[] path, boolean splitLeft)
    {
        super(siteId, position);
        setPath(path);
        this.splitLeft = splitLeft;
    }    

    @Override
    public void execute(Tree root) {
        int d = 1;//décalage
        Tree tree = root.getChild(path[0]);
        Tree pTree = tree.getChild(path[1]).cloneNode();
        Tree tTree = pTree;
        int[] path2=new int[path.length-1];
        for(int k=0;k<path2.length;k++){
            path2[k]=path[k+1];
        }
        tree = tree.getChild(path2[0]);
        Tree r;
        for (int i = 0; i < path2.length - 1; i++) {
            while ((r = tree.removeChild(path2[i + 1] + 1)) != null) {
                tTree.addChild(r);
            }
            if (i != path2.length - 2) {
                tTree.addChild(tree.getChild(path2[i + 1]).cloneNode(), 0);
                tTree = tTree.getChild(0);
                tree = tree.getChild(path2[i + 1]);
            } else {
                if (!splitLeft) {

                    tTree.addChild(tree.removeChild(path2[i + 1]), 0);
                    int j = 0;
                    while ((i + 1 - j != 0) && (path2[i + 1 - j] == 0)) {
                        tree = tree.getParent();
                        tree.removeChild(path2[i - j]);
                        j = j + 1;
                    }
                    if (i + 1 - j == 0) {
                        d = 0;
                    }
                } else {
                    tree = tree.getChild(path2[i + 1]);
                    String str = tree.split(position);
                    if (str != null) {
                        tTree.addChild(TreeFactory.createTextTree(str), 0);
                    }
                }
            }
        }
        root.getChild(path[0]).addChild(pTree, path[1] + d);
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