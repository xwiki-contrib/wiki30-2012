package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.TreeUtils;


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
        if(op1.path[0]==path[0]){
            if (!TreeUtils.diff(op1.path, path)) {
                if (op1.getPosition() < position) {
                    return op1;
                }
                int[] tab = new int[op1.path.length];
                tab[0] = op1.path[0];
                tab[1] = op1.path[1] + 1;
                return new TreeInsertText(op1.getSiteId(), op1.getPosition() - position, tab, op1.text);
            }
            if (TreeUtils.inf(op1.path, path)) {
                return op1;
            }
            if (op1.path[1] > path[1]) {
                int[] tab = TreeUtils.addC(op1.path,1,1);
                return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab, op1.text);
            }
            int[] p1=new int[op1.path.length-1];
            int[] p2=new int[path.length-1];
            for(int i=0;i<p1.length;i++){
                p1[i]=op1.path[i+1];
            }
            for(int i=0;i<p2.length;i++){
                p2[i]=path[i+1];
            }
            int[] tab = TreeUtils.reference(p1, p2);
            int[] tab2 = new int[tab.length+1];
            for(int i=0;i<tab.length;i++){
                tab2[i+1]=tab[i];
            }
            tab2[0]=path[0];
            
            return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab2, op1.text);            
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        if(op1.path[0]==path[0]){
            if (!TreeUtils.diff(op1.path, path)) {
                if (op1.getPosition() < position) {
                    return op1;
                }
                int[] tab = new int[op1.path.length];
                tab[0] = op1.path[0];
                tab[1] = op1.path[1]+1;
                return new TreeDeleteText(op1.getSiteId(), op1.getPosition() - position, tab);
            }
            if (TreeUtils.inf(op1.path, path)) {
                return op1;
            }
            if (op1.path[1] > path[1]) {
                int[] tab = TreeUtils.addC(op1.path,1,1);
                return new TreeDeleteText(op1.getSiteId(), op1.getPosition(), tab);
            }
            int[] p1=new int[op1.path.length-1];
            int[] p2=new int[path.length-1];
            for(int i=0;i<p1.length;i++){
                p1[i]=op1.path[i+1];
            }
            for(int i=0;i<p2.length;i++){
                p2[i]=path[i+1];
            }
            int[] tab = TreeUtils.reference(p1, p2);
            int[] tab2 = new int[tab.length+1];
            for(int i=0;i<tab.length;i++){
                tab2[i+1]=tab[i];
            }
            tab2[0]=path[0];
            return new TreeDeleteText(op1.getSiteId(), op1.getPosition(), tab2); 
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
        if(op1.path[0]==path[0]){
            if (TreeUtils.inf(op1.path, path)) {
                return op1;
            }
            if (!TreeUtils.diff(op1.path, path)) {
                if (position == 0) {
                    int[] tab = new int[op1.path.length];
                    tab[0] = op1.path[0];
                    tab[1] = op1.path[1] + 1;
                    return new TreeDeleteTree(tab);
                }
                return op1;
            }
            if (op1.path[1] > path[1]) {
                int[] tab = TreeUtils.addC(op1.path,1, 1);
                return new TreeDeleteTree(tab);
            }
            int[] p1=new int[op1.path.length-1];
            int[] p2=new int[path.length-1];
            for(int i=0;i<p1.length;i++){
                p1[i]=op1.path[i+1];
            }
            for(int i=0;i<p2.length;i++){
                p2[i]=path[i+1];
            }
            int[] tab = TreeUtils.reference(p1, p2);
            int[] tab2 = new int[tab.length+1];
            for(int i=0;i<tab.length;i++){
                tab2[i+1]=tab[i];
            }
            tab2[0]=path[0];
            return new TreeDeleteTree(tab2); 
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeStyle(TreeStyle op1) {
        if(op1.path[0]==path[0]){
            if (TreeUtils.inf(op1.path, path)) {
                return op1;
            }
            if (!TreeUtils.diff(op1.path, path)) {
                if (op1.end < position) {
                    return op1;
                }
                if (op1.end == position) {
                    return new TreeStyle(op1.getSiteId(), op1.path, op1.start, op1.end, op1.param, op1.value, op1.addStyle,
                        op1.splitLeft, false,op1.getTagName());
                }
                int[] tab = new int[op1.path.length];
                tab[0] = op1.path[0];
                tab[1] = op1.path[1] + 1;
                if (op1.start > position) {
                    return new TreeStyle(op1.getSiteId(), tab, op1.start - position, op1.end - position, op1.param,
                        op1.value, op1.addStyle, op1.splitLeft, op1.splitRight,op1.getTagName());
                }
                if (op1.start == position) {
                    return new TreeStyle(op1.getSiteId(), tab, op1.start - position, op1.end - position, op1.param,
                        op1.value, op1.addStyle, false, op1.splitRight,op1.getTagName());
                }
                //paragraphe entre start et end
                return new TreeCompositeOperation(
                    new TreeStyle(op1.getSiteId(), op1.path, op1.start, position, op1.param, op1.value, op1.addStyle,
                        op1.splitLeft, false,op1.getTagName()),
                    new TreeStyle(op1.getSiteId(), tab, 0, op1.end - position, op1.param, op1.value, op1.addStyle, false,
                        op1.splitRight,op1.getTagName())
                );
            }
            //TreeUtils.inf(path,op1.path)
            if (op1.path[1] > path[1]) {
                int[] tab = TreeUtils.addC(op1.path,1,1);
                return new TreeStyle(op1.getSiteId(), tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle,
                    op1.splitLeft, op1.splitRight,op1.getTagName());
            }
            int[] p1=new int[op1.path.length-1];
            int[] p2=new int[path.length-1];
            for(int i=0;i<p1.length;i++){
                p1[i]=op1.path[i+1];
            }
            for(int i=0;i<p2.length;i++){
                p2[i]=path[i+1];
            }
            int[] tab = TreeUtils.reference(p1, p2);
            int[] tab2 = new int[tab.length+1];
            for(int i=0;i<tab.length;i++){
                tab2[i+1]=tab[i];
            }
            tab2[0]=path[0];
            return new TreeStyle(op1.getSiteId(), tab2, op1.start, op1.end, op1.param, op1.value, op1.addStyle,
                op1.splitLeft, op1.splitRight,op1.getTagName());   
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeCaretPosition(TreeCaretPosition op1) {
        if(op1.path[0]==path[0]){
            if (!TreeUtils.diff(op1.path, path)) {
                if (op1.getPosition() < position) {
                    return op1;
                }
                int[] tab = new int[op1.path.length];
                tab[0] = op1.path[0];
                tab[1] = op1.path[1] + 1;
                return new TreeCaretPosition(op1.getSiteId(), op1.getPosition() - position, tab);
            }
            if (TreeUtils.inf(op1.path, path)) {
                return op1;
            }
            if (op1.path[1] > path[1]) {
                int[] tab = TreeUtils.addC(op1.path,1,1);
                return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), tab);
            }
            int[] p1=new int[op1.path.length-1];
            int[] p2=new int[path.length-1];
            for(int i=0;i<p1.length;i++){
                p1[i]=op1.path[i+1];
            }
            for(int i=0;i<p2.length;i++){
                p2[i]=path[i+1];
            }
            int[] tab = TreeUtils.reference(p1, p2);
            int[] tab2 = new int[tab.length+1];
            for(int i=0;i<tab.length;i++){
                tab2[i+1]=tab[i];
            }
            tab2[0]=path[0];
            return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), tab2);      
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMergeItem(TreeMergeItem op1) {
        if(op1.getPosition()==path[0]){
            if (op1.posItem == path[1]) {
                return new TreeMergeItem(op1.getSiteId(),op1.getPosition(),op1.posItem,
                        op1.leftSiblingChildrenNr, path[2] + (splitLeft ? 1 : 0));
            }
            if (op1.posItem == path[1] + 1) {
                return new TreeMergeItem(op1.getSiteId(),op1.getPosition(),op1.posItem + 1,
                        op1.leftSiblingChildrenNr - path[2], op1.childrenNr);
            }
            if (op1.getPosition() < path[1]) {
                return op1;
            }
            return new TreeMergeItem(op1.getSiteId(),op1.getPosition(),op1.posItem + 1, op1.leftSiblingChildrenNr, op1.childrenNr);  
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMoveItem(TreeMoveItem op1) {
        if(op1.getPosition()==path[0]){
            if (op1.sp == path[1]) {
                if (op1.sp < op1.ep) {
                    return new TreeCompositeOperation(
                        new TreeMoveItem(op1.getSiteId(),op1.getPosition(), op1.sp, op1.ep + 1),
                        new TreeMoveItem(op1.getSiteId(),op1.getPosition(), op1.sp, op1.ep + 1)
                    );
                } else {
                    return new TreeCompositeOperation(
                        new TreeMoveItem(op1.getSiteId(),op1.getPosition(), op1.sp, op1.ep),
                        new TreeMoveItem(op1.getSiteId(),op1.getPosition(), op1.sp + 1, op1.ep + 1)
                    );
                }
            }
            int sp = op1.sp;
            int ep = op1.ep;
            if (path[1] < sp) {
                sp++;
            }
            if (path[1] < ep) {
                ep++;
            }
            return new TreeMoveItem(op1.getSiteId(),op1.getPosition(), sp, ep);
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeNewItem(TreeNewItem op1) {
        if(op1.getPosition()==path[0]){
            if (op1.posItem <= path[1]) {
                return op1;
            }
            return new TreeNewItem(op1.getSiteId(),op1.getPosition(),op1.posItem + 1); 
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeNewList(TreeNewList op1) {
        return op1;
    }

    @Override
    protected TreeOperation handleTreeSplitItem(TreeSplitItem op1) {
        if(op1.path[0]==path[0]){
            if (!TreeUtils.diff(op1.path, path)) {
                if (op1.getPosition() < position) {
                    return op1;
                }
                if (op1.getPosition() == position) {
                    return new TreeIdOp();
                }
                int[] tab = new int[op1.path.length];
                tab[0] = op1.path[0];
                tab[1] = op1.path[1] + 1;
                return new TreeSplitItem(op1.siteId, op1.getPosition() - position, tab, op1.splitLeft);
            }
            if (TreeUtils.inf(op1.path, path)) {
                return op1;
            }
            if (op1.path[1] > path[1]) {
                int[] tab = TreeUtils.addC(op1.path,1,1);
                return new TreeSplitItem(op1.siteId, op1.getPosition(), tab, op1.splitLeft);
            }
            int[] p1=new int[op1.path.length-1];
            int[] p2=new int[path.length-1];
            for(int i=0;i<p1.length;i++){
                p1[i]=op1.path[i+1];
            }
            for(int i=0;i<p2.length;i++){
                p2[i]=path[i+1];
            }
            int[] tab = TreeUtils.reference(p1, p2);
            int[] tab2 = new int[tab.length+1];
            for(int i=0;i<tab.length;i++){
                tab2[i+1]=tab[i];
            }
            tab2[0]=path[0];
            return new TreeInsertParagraph(op1.siteId, op1.getPosition(), tab2, op1.splitLeft);            
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeUpdateElement(TreeUpdateElement op1) {
        if(op1.path[0]==path[0] && op1.path.length==2){
            if(op1.path[1]<path[1]){
                return op1;
            }
            if(op1.path[1]>path[1]){
                return new TreeUpdateElement(op1.getSiteId(), TreeUtils.addC(op1.getPath(),1,1), op1.tag, op1.value);
            }
            //op1.path[1]==path[1] : same item
            return new TreeCompositeOperation(
                 new TreeUpdateElement(op1.getSiteId(), op1.getPath(), op1.tag, op1.value),
                 new TreeUpdateElement(op1.getSiteId(), TreeUtils.addC(op1.getPath(),1,1), op1.tag, op1.value)
            );            
        }
        return op1;
    }
    
}