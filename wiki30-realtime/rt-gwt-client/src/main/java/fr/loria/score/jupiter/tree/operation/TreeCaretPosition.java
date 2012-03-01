/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.loria.score.jupiter.tree.operation;

import fr.loria.score.jupiter.tree.Tree;

/**
 *
 * @author andre
 */
public class TreeCaretPosition extends TreeOperation{

    public TreeCaretPosition(int siteId, int position, int[] path) {
        this.siteId=siteId;
        this.position=position;
        this.path=path;
    }

    @Override
    public void execute(Tree root) {
        
    }

    @Override
    protected TreeOperation handleTreeInsertText(TreeInsertText op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeStyle(TreeStyle op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        return null;
    }

    @Override
    protected TreeOperation handleTreeCaretPosition(TreeCaretPosition op1) {
        return null;
    }

    @Override public String toString()
    {
        return "TreeCaretPos(" + super.toString() + ")";
    }
}
