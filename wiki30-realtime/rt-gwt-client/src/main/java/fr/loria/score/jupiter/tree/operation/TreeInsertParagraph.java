package fr.loria.score.jupiter.tree.operation;

import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeUtils;

import java.util.ArrayList;
import java.util.List;

public class TreeInsertParagraph extends TreeOperation {

    public boolean splitLeft;//split left : true if position<>0(generation)

    public TreeInsertParagraph() {}

    /**
     * This is the usual constructor to be used.
     * @param siteId the site id
     * @param position the insertion position. If split between 2 nodes, uses the second and position=0.
     * @param path the path
     */
    public TreeInsertParagraph(int siteId, int position, List<Integer> path) {
        super(siteId, position);
        setPath(path);
        if (position == 0) {  // !=
            this.splitLeft = true;
        }
    }

    /**
     * This constructor should be used only by transformation functions.
     * @param siteId
     * @param position
     * @param path
     * @param splitLeft
     */
    public TreeInsertParagraph(int siteId, int position, List<Integer> path, boolean splitLeft) {
        super(siteId, position);
        setPath(path);
        this.splitLeft = splitLeft;
    }

    public void execute(Tree root) {
        int d = 1;//décalage
        Tree tree = root;
        Tree pTree = new Tree("p", null);
        Tree tTree = pTree;
        tree = tree.getChild(path.get(0));
        Tree r;
        for (int i = 0; i < path.size() - 1; i++) {
            while ((r = tree.removeChild(path.get(i + 1) + 1)) != null) {
                tTree.addChild(r);
            }
            if (i != path.size() - 2) {
                tTree.addChild(tree.getChild(path.get(i + 1)).cloneNode(), 0);
                tTree = tTree.getChild(0);
                tree = tree.getChild(path.get(i + 1));
            } else {
                if (!splitLeft) {

                    tTree.addChild(tree.removeChild(path.get(i + 1)), 0);
                    int j = 0;
                    while ((i + 1 - j != 0) && (path.get(i + 1 - j) == 0)) {
                        tree = tree.getParent();
                        tree.removeChild(path.get(i - j));
                        j = j + 1;
                    }
                    if (i + 1 - j == 0) {
                        d = 0;
                    }
                } else {
                    tree = tree.getChild(path.get(i + 1));
                    String str = tree.split(position);

                    tTree.addChild(new Tree("#text",str), 0);
                }
            }
        }
        root.addChild(pTree, path.get(0) + d);
    }

    public String toString() {
        return "InsertP(" +  super.toString() + "," + path + ", " + splitLeft + ")";
    }

    @Override
    public void updateUI() {
        //Todo
    }

    //OT pour InsertP
    protected TreeOperation handleTreeInsertText(TreeInsertText op1) {
        if (!TreeUtils.diff(op1.path, path)) {
            if (op1.getPosition() < position) {
                return op1;
            }
            List<Integer> tab =  new ArrayList<Integer>(op1.path.size());
            tab.set(0, op1.path.get(0) + 1);
            return new TreeInsertText(op1.getSiteId(), op1.getPosition() - position, tab, op1.getText());
        }
        if (TreeUtils.inf(op1.path, path)) {
            return op1;
        }
        if (op1.path.get(0) > path.get(0)) {
            List<Integer> tab = TreeUtils.addP(op1.path, 1);
            return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab, op1.getText());
        }
        List<Integer> tab = TreeUtils.reference(op1.path, path);
        return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab, op1.getText());
    }

    protected TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        if (!TreeUtils.diff(op1.path, path)) {
            if (op1.getPosition() < position) {
                return op1;
            }
            List<Integer> tab = new ArrayList<Integer>(op1.path.size());
            tab.set(0, op1.path.get(0) + 1);
            return new TreeDeleteText(op1.getPosition() - position, tab);
        }
        if (TreeUtils.inf(op1.path, path)) {
            return op1;
        }
        if (op1.path.get(0) > path.get(0)) {
            List<Integer> tab = TreeUtils.addP(op1.path, 1);
            return new TreeDeleteText(op1.getPosition(), tab);
        }
        List<Integer> tab = TreeUtils.reference(op1.path, path);
        return new TreeDeleteText(op1.getPosition(), tab);
    }

    protected TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {
        if (op1.getPosition() <= path.get(0)) {
            return op1;
        }
        return new TreeNewParagraph(op1.getSiteId(), op1.getPosition() + 1);
    }

    protected TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        if (op1.getPosition() == path.get(0)) {
            return new TreeMergeParagraph(op1.getSiteId(), op1.getPosition(), op1.leftSiblingChildrenNr, path.get(1) + (splitLeft ? 1 : 0));
        }
        if (op1.getPosition() == path.get(0) + 1) {
            return new TreeMergeParagraph(op1.getSiteId(), op1.getPosition() + 1, op1.leftSiblingChildrenNr - path.get(1), op1.rightSiblingChildrenNr);
        }
        if (op1.getPosition() < path.get(0)) {
            return op1;
        }
        return new TreeMergeParagraph(op1.getSiteId(), op1.getPosition() + 1, op1.leftSiblingChildrenNr, op1.rightSiblingChildrenNr);
    }

    protected TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        if (!TreeUtils.diff(op1.path, path)) {
            if (op1.getPosition() < position) {
                return op1;
            }
            if (op1.getPosition() == position) {
                return new TreeIdOp();
            }
            List<Integer> tab = new ArrayList<Integer>(op1.path.size());
            tab.set(0, op1.path.get(0) + 1);
            return new TreeInsertParagraph(op1.siteId, op1.getPosition() - position, tab, op1.splitLeft);
        }
        if (TreeUtils.inf(op1.path, path)) {
            return op1;
        }
        if (op1.path.get(0) > path.get(0)) {
            List<Integer> tab = TreeUtils.addP(op1.path, 1);
            return new TreeInsertParagraph(op1.siteId, op1.getPosition(), tab, op1.splitLeft);
        }
        List<Integer> tab = TreeUtils.reference(op1.path, path);
        return new TreeInsertParagraph(op1.siteId, op1.getPosition(), tab, op1.splitLeft);
    }

    protected TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        if (TreeUtils.inf(op1.path, path)) {
            return op1;
        }
        if (!TreeUtils.diff(op1.path, path)) {
            if (position == 0) {
                List<Integer> tab = new ArrayList<Integer>(op1.path.size());
                tab.set(0, op1.path.get(0) + 1);
                return new TreeDeleteTree(tab);
            }
            return op1;
        }
        if (op1.path.get(0) > path.get(0)) {
            List<Integer> tab = TreeUtils.addP(op1.path, 1);
            return new TreeDeleteTree(tab);
        }
        List<Integer> tab = TreeUtils.reference(op1.path, path);
        return new TreeDeleteTree(tab);

    }

    protected TreeOperation handleTreeStyle(TreeStyle op1) {
        if (TreeUtils.inf(op1.path, path)) {
            return op1;
        }
        if (!TreeUtils.diff(op1.path, path)) {
            /*if(op1.end<=position){
				return new TreeStyle(op1.path,op1.start,op1.end,op1.param,op1.value,op1.siteId,op1.addStyle,op1.splitLeft,op1.sr);
			}*/
            if (op1.end < position) {
                return op1;
            }
            if (op1.end == position) {
                return new TreeStyle(op1.getSiteId(), op1.path, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, false);
            }
            List<Integer> tab = new ArrayList<Integer>(op1.path.size());
            tab.set(0, op1.path.get(0) + 1);
            /*if(op1.start>=position){
				return new TreeStyle(tab,op1.start-position,op1.end-position,op1.param,op1.value,op1.siteId,op1.addStyle,op1.splitLeft,op1.sr);
			}*/
            if (op1.start > position) {
                return new TreeStyle(op1.getSiteId(), tab, op1.start - position, op1.end - position, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
            }
            if (op1.start == position) {
                return new TreeStyle(op1.getSiteId(), tab, op1.start - position, op1.end - position, op1.param, op1.value, op1.addStyle, false, op1.sr);
            }
            //paragraphe entre start et end
            return new TreeCompositeOperation(
                    new TreeStyle(op1.getSiteId(), op1.path, op1.start, position, op1.param, op1.value, op1.addStyle, op1.splitLeft, false),
                    new TreeStyle(op1.getSiteId(), tab, 0, op1.end - position, op1.param, op1.value, op1.addStyle, false, op1.sr)
            );
        }
        //TreeUtils.inf(path,op1.path)
        if (op1.path.get(0) > path.get(0)) {
            List<Integer> tab = TreeUtils.addP(op1.path, 1);
            return new TreeStyle(op1.getSiteId(), tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
        }
        List<Integer> tab = TreeUtils.reference(op1.path, path);
        return new TreeStyle(op1.getSiteId(), tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
    }

    protected TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        if (op1.sp == path.get(0)) {
            if (op1.sp < op1.ep) {
                return new TreeCompositeOperation(
                        new TreeMoveParagraph(op1.getSiteId(), op1.sp, op1.ep + 1),
                        new TreeMoveParagraph(op1.getSiteId(), op1.sp, op1.ep + 1)
                );
            } else {
                return new TreeCompositeOperation(
                        new TreeMoveParagraph(op1.getSiteId(), op1.sp, op1.ep),
                        new TreeMoveParagraph(op1.getSiteId(), op1.sp + 1, op1.ep + 1)
                );
            }
        }
        int sp = op1.sp;
        int ep = op1.ep;
        if (path.get(0) < sp) {
            sp++;
        }
        if (path.get(0) < ep) {
            ep++;
        }
        return new TreeMoveParagraph(op1.getSiteId(), sp, ep);
    }
}
