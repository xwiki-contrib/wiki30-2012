package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeUtils;

public class TreeMoveParagraph extends TreeOperation {

    public int sp;//initial position
    public int ep;//new position

    public TreeMoveParagraph() {}

    public TreeMoveParagraph(int siteId, int s, int e) {
        setSiteId(siteId);
        sp = s;
        ep = e;
    }

    public void execute(Tree root) {
        Tree tree = root;
        Tree t1 = tree.removeChild(sp);
        if (sp >= ep) {
            tree.addChild(t1, ep);
        } else {
            tree.addChild(t1, ep - 1);
        }
    }

    public String toString() {
        return "MoveP(" + super.toString()+ ", startPosition: " + sp + ", endPosition: " + ep + ")";
    }

    //OT pour moveP
    public TreeOperation handleTreeInsertText(TreeInsertText op1) {
        if (op1.path[0] == sp) {
            if (sp >= ep) {
                return new TreeInsertText(op1.getSiteId(), op1.getPosition(), TreeUtils.setP(op1.path, ep), op1.text);
            } else {
                return new TreeInsertText(op1.getSiteId(), op1.getPosition(), TreeUtils.setP(op1.path, ep - 1), op1.text);
            }
        }
        if (op1.path[0] < sp) {
            if (op1.path[0] < ep) {
                return op1;
            } else {
                return new TreeInsertText(op1.getSiteId(), op1.getPosition(), TreeUtils.addP(op1.path, 1), op1.text);
            }
        }
        //op1.path[0]>sp
        if (op1.path[0] < ep) {
            return new TreeInsertText(op1.getSiteId(), op1.getPosition(), TreeUtils.addP(op1.path, -1), op1.text);
        } else {
            return op1;
        }
    }

    public TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        if (op1.path[0] == sp) {
            if (sp >= ep) {
                return new TreeDeleteText(op1.getPosition(), TreeUtils.setP(op1.path, ep));
            } else {
                return new TreeDeleteText(op1.getPosition(), TreeUtils.setP(op1.path, ep - 1));
            }
        }
        if (op1.path[0] < sp) {
            if (op1.path[0] < ep) {
                return op1;
            } else {
                return new TreeDeleteText(op1.getPosition(), TreeUtils.addP(op1.path, 1));
            }
        }
        //op1.path[0]>sp
        if (op1.path[0] < ep) {
            return new TreeDeleteText(op1.getPosition(), TreeUtils.addP(op1.path, -1));
        } else {
            return op1;
        }
    }

    public TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {//si deplacement au meme endroit que creation, depl<crea
        if (sp < op1.getPosition()) {
            if (ep <= op1.getPosition()) {
                return op1;
            }
            return new TreeNewParagraph(op1.getSiteId(), op1.getPosition() - 1);
        }
        //sp>=op1.getPosition()
        if (ep <= op1.getPosition()) {
            return new TreeNewParagraph(op1.getSiteId(), op1.getPosition() + 1);
        }
        return op1;
    }

    public TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        //annuler le move si deplacement de droite, avant la gauche ; ou de gauche, après la droite
        //deplacement dans entre la fusion -> après la fusion
        if (sp == op1.getPosition()) {//move du paragraphe de droite du merge
            if (ep == sp - 1) {
                //si move du paragraphe de droite juste avant celui de gauche, annuler et garder la fusion
                return new TreeCompositeOperation(this, op1);
            }
            //sinon deplacer la gauche et fusionner
            if (ep > sp) {
                return new TreeCompositeOperation(
                        new TreeMoveParagraph(siteId, sp - 1, ep - 1),
                        new TreeMergeParagraph(ep - 1, op1.leftSiblingChildrenNr, op1.rightSiblingChildrenNr)
                );
            } else {
                return new TreeCompositeOperation(this, new TreeMergeParagraph(ep + 1, op1.leftSiblingChildrenNr, op1.rightSiblingChildrenNr));
            }
        }
        if (sp == op1.getPosition() - 1) {//move du paragraphe de gauche du merge
            if (ep == sp + 2) {
                //si move du paragraphe de gauche juste apres celui de droite, annuler et garder la fusion
                return new TreeCompositeOperation(this, op1);
            }
            //sinon deplacer la droite et fusionner
            if (ep > sp) {
                return new TreeCompositeOperation(this, new TreeMergeParagraph(ep, op1.leftSiblingChildrenNr, op1.rightSiblingChildrenNr));
            } else {
                return new TreeCompositeOperation(
                        new TreeMoveParagraph(siteId, sp + 1, ep + 1),
                        new TreeMergeParagraph(ep + 1, op1.leftSiblingChildrenNr, op1.rightSiblingChildrenNr)
                );
            }
        }
        if (op1.getPosition() < sp) {
            if (op1.getPosition() == ep) {
                //si destination du move entre les fusionnés, placer apres le resultat de la fusion
                return new TreeCompositeOperation(new TreeMoveParagraph(siteId, ep, ep + 2), op1);
            }
            if (op1.getPosition() < ep) {
                return op1;
            }
            //op1.getPosition()>ep
            return new TreeMergeParagraph(op1.getPosition() + 1, op1.leftSiblingChildrenNr, op1.rightSiblingChildrenNr);
        }
        //op1.getPosition()>sp
        if (op1.getPosition() == ep) {
            //si destination du move entre les fusionnés, placer apres le resultat de la fusion
            return new TreeCompositeOperation(new TreeMoveParagraph(siteId, ep - 1, ep + 1), new TreeMergeParagraph(op1.getPosition() - 1, op1.leftSiblingChildrenNr, op1.rightSiblingChildrenNr));
        }
        if (op1.getPosition() < ep) {
            return new TreeMergeParagraph(op1.getPosition() - 1, op1.leftSiblingChildrenNr, op1.rightSiblingChildrenNr);
        }
        //op1.getPosition()>ep
        return op1;
    }

    public TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        if (op1.path[0] == sp) {
            if (sp >= ep) {
                return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition(), TreeUtils.setP(op1.path, ep), op1.splitLeft);
            } else {
                return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition(), TreeUtils.setP(op1.path, ep - 1), op1.splitLeft);
            }
        }
        if (op1.path[0] < sp) {
            if (op1.path[0] < ep) {
                return op1;
            } else {
                return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition(), TreeUtils.addP(op1.path, 1), op1.splitLeft);
            }
        }
        //op1.path[0]>sp
        if (op1.path[0] < ep) {
            return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition(), TreeUtils.addP(op1.path, -1), op1.splitLeft);
        } else {
            return op1;
        }
    }

    public TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        if (op1.path[0] == sp) {
            if (sp >= ep) {
                return new TreeDeleteTree(TreeUtils.setP(op1.path, ep));
            } else {
                return new TreeDeleteTree(TreeUtils.setP(op1.path, ep - 1));
            }
        }
        if (op1.path[0] < sp) {
            if (op1.path[0] < ep) {
                return op1;
            } else {
                return new TreeDeleteTree(TreeUtils.addP(op1.path, 1));
            }
        }
        //op1.path[0]>sp
        if (op1.path[0] < ep) {
            return new TreeDeleteTree(TreeUtils.addP(op1.path, -1));
        } else {
            return op1;
        }
    }

    public TreeOperation handleTreeStyle(TreeStyle op1) {
        if (op1.path[0] == sp) {
            if (sp >= ep) {
                return new TreeStyle(op1.getSiteId(), TreeUtils.setP(op1.path, ep), op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
            } else {
                return new TreeStyle(op1.getSiteId(), TreeUtils.setP(op1.path, ep - 1), op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
            }
        }
        if (op1.path[0] < sp) {
            if (op1.path[0] < ep) {
                return op1;
            } else {
                return new TreeStyle(op1.getSiteId(), TreeUtils.addP(op1.path, 1), op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
            }
        }
        //op1.path[0]>sp
        if (op1.path[0] < ep) {
            return new TreeStyle(op1.getSiteId(), TreeUtils.addP(op1.path, 1), op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
        } else {
            return op1;
        }
    }

    public TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        if (op1.sp == sp) {
            if (op1.ep == ep) {
                return new TreeIdOp();
            }
            if (op1.ep < ep) {//le plus loin (op2) gagne
                if (op1.sp < op1.ep) {
                    return new TreeMoveParagraph(siteId, op1.ep - 1, ep);
                }
                if (ep < op1.sp) {
                    return new TreeMoveParagraph(siteId, op1.ep, ep + 1);
                }
                //op1.e1<sp<ep
                return new TreeMoveParagraph(siteId, op1.ep, ep);
            }
            if (op1.ep > ep) {//le plus loin (op1) gagne
                return new TreeIdOp();
            }
        }
        int sp = op1.sp;
        int ep = op1.ep;
        if (sp < op1.sp) {
            sp--;
        }
        if (sp < op1.ep) {
            ep--;
        }
        if (ep <= op1.sp) {
            sp++;
        }
        if (ep < op1.ep) {
            ep++;
        } else {
            if (ep == op1.ep) {//le plus loin (sp) est après l'autre
                if (op1.sp > sp) {
                    ep++;
                }
            }
        }
        return new TreeMoveParagraph(siteId, sp, ep);
    }
}
