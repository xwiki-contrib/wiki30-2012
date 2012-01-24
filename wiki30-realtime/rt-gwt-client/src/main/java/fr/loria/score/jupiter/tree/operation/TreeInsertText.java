package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.TreeUtils;

public class TreeInsertText extends TreeOperation {

    public char text; //text to insert

    public TreeInsertText() {}

    public TreeInsertText(int siteId, int position, int[] path, char c) {
        super(siteId, position);
        this.text = c;
        setPath(path);
    }

    public void execute(Tree root) {
        Tree tree = root.getChildFromPath(path);
        if ((tree == root) || (path.length == 1 && position == 0)) {
            if (tree.getChild(0) == null) {
                // path is empty or path has 1 element (browser stuff) so add a new text node child
                tree.addChild(TreeFactory.createTextTree(String.valueOf(text)));
                return;
            } else {
                tree = tree.getChild(0);
            }
        }
        tree.addChar(text, position);
    }

    public char getText() {
        return text;
    }

    public String toString() {
        return "InsertText(" + super.toString() + ", " + text + ")";
    }

    //OT pour InsertText
    // second param is this instance
    public TreeOperation handleTreeInsertText(TreeInsertText op1) {
        if (TreeUtils.diff(op1.path, path)) {
            return op1;
        }
        if (op1.position < position) {
            return op1;
        }
        if (op1.position == position && op1.siteId < siteId) {
            return op1;
        }
        return new TreeInsertText(op1.siteId, op1.position + 1, op1.path, op1.text);
    }

    public TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        if (TreeUtils.diff(op1.path, path)) {
            return op1;
        }
        if (op1.getPosition() < position) {
            return /*new TreeDeleteText(op1.position,op1.path)*/ op1;
        }
        return new TreeDeleteText(op1.getSiteId(), op1.getPosition() + 1, op1.path);
    }

    public TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {
        return op1;
    }

    public TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        return op1;
    }

    public TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        if (TreeUtils.diff(op1.path, path)) {
            return op1;
        }
        if (op1.getPosition() <= position) {
            return op1;
        }
        return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition() + 1, op1.path, op1.splitLeft);
    }

    public TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        return op1;
    }

    public TreeOperation handleTreeStyle(TreeStyle op1) {//en cas d'ajout en limite du style, appliquer le style.
        if (TreeUtils.diff(op1.path, path)) {
            return op1;
        }
        if (op1.start >= position) {
            return new TreeStyle(op1.getSiteId(), op1.path, op1.start == position ? op1.start : op1.start + 1, op1.end + 1,
                    op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
        }
        if (op1.end < position) {
            return new TreeStyle(op1.getSiteId(), op1.path, op1.start, op1.end,
                    op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
        }
        return new TreeStyle(op1.getSiteId(), op1.path, op1.start, op1.end + 1, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
    }

    public TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        return op1;
    }
}
