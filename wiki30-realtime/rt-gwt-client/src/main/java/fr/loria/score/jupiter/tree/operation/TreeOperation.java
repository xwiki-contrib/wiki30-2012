package fr.loria.score.jupiter.tree.operation;

import com.google.gwt.dom.client.Node;
import fr.loria.score.jupiter.model.AbstractOperation;
import fr.loria.score.jupiter.tree.Tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public abstract class TreeOperation extends AbstractOperation {
    private transient static final Logger log = Logger.getLogger(TreeOperation.class.getName());

    protected transient Node node; // todo: or stick it in Tree
    protected List<Integer> path; //path node

    public TreeOperation(AbstractOperation o) {
        super(o);
    }

    public TreeOperation(int siteId, int position) {
        super(siteId, position);
    }

    public TreeOperation(int position) {
        super(position);
    }

    public TreeOperation() {}

    public TreeOperation(List<Integer> path) {
        this.path = path;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public abstract void execute(Tree root);

    /**
     * Updates the UI
     */
    public abstract void updateUI(); //todo: or add node here

    public AbstractOperation transform(AbstractOperation op1) {
        log.fine("Transforming:" + op1 + " according to this operation: " + this);
        TreeOperation transformed = null;
        if (op1 instanceof TreeInsertText) {
            transformed = handleTreeInsertText((TreeInsertText) op1);
        } else if (op1 instanceof TreeDeleteText) {
            transformed = handleTreeDeleteText((TreeDeleteText) op1);
        } else if (op1 instanceof TreeNewParagraph) {
            transformed = handleTreeNewParagraph((TreeNewParagraph) op1);
        } else if (op1 instanceof TreeMergeParagraph) {
            transformed = handleTreeMergeParagraph((TreeMergeParagraph) op1);
        } else if (op1 instanceof TreeInsertParagraph) {
            transformed = handleTreeInsertParagraph((TreeInsertParagraph) op1);
        } else if (op1 instanceof TreeDeleteTree) {
            transformed = handleTreeDeleteTree((TreeDeleteTree) op1);
        } else if (op1 instanceof TreeStyle) {
            transformed = handleTreeStyle((TreeStyle) op1);
        } else if (op1 instanceof TreeMoveParagraph) {
            transformed = handleTreeMoveParagraph((TreeMoveParagraph) op1);
        } else if (op1 instanceof TreeIdOp) { // todo: this goes for all operations
            transformed = handleTreeId((TreeIdOp) op1);
        } else if (op1 instanceof TreeCompositeOperation) {
            transformed = handleTreeComposite((TreeCompositeOperation) op1);
        }
        return transformed;
    }

    // make an intf  & delegate
    protected abstract TreeOperation handleTreeInsertText(TreeInsertText op1);

    protected abstract TreeOperation handleTreeDeleteText(TreeDeleteText op1);

    protected abstract TreeOperation handleTreeNewParagraph(TreeNewParagraph op1);

    protected abstract TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1);

    protected abstract TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1);

    protected abstract TreeOperation handleTreeDeleteTree(TreeDeleteTree op1);

    protected abstract TreeOperation handleTreeStyle(TreeStyle op1);

    protected abstract TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1);

    protected TreeOperation handleTreeId(TreeIdOp op1) {
        return op1;
    }

    protected TreeOperation handleTreeComposite(TreeCompositeOperation op1) {
        ArrayList<TreeOperation> l = new ArrayList<TreeOperation>();
        Iterator<TreeOperation> it = op1.operations.iterator();

        TreeOperation to = this;
        TreeOperation n = it.next();

        l.add((TreeOperation)to.transform(n));

        while (it.hasNext()) {
            to = (TreeOperation)n.transform(to);
            n = it.next();
            l.add((TreeOperation) to.transform(n));
        }
        return new TreeCompositeOperation(l);
    }

    public String toString() {
        return "siteId: " + siteId + " position: " + position;
    }

    public List<Integer> getPath() {
        return path;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }
}
