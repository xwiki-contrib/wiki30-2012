package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeUtils;

public class TreeMoveItem extends TreeOperation{
    
    public int sp;//initial position
    public int ep;//new position

    public TreeMoveItem()
    {
    }

    public TreeMoveItem(int siteId, int position, int s, int e) {
        super(siteId,position);
        sp = s;
        ep = e;
    }    

    @Override
    public void execute(Tree root) {
        Tree tree = root.getChild(position);
        Tree t1 = tree.removeChild(sp);
        if (sp >= ep) {
            tree.addChild(t1, ep);
        } else {
            tree.addChild(t1, ep - 1);
        }
    }

    @Override
    protected TreeOperation handleTreeInsertText(TreeInsertText op1) {
        if(op1.path[0]==position){
            if (op1.path[1] == sp) {
                if (sp >= ep) {
                    return new TreeInsertText(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.path,1, ep-op1.path[1]), op1.text);
                } else {
                    return new TreeInsertText(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.path,1, ep-op1.path[1]-1), op1.text);
                }
            }
            if (op1.path[1] < sp) {
                if (op1.path[1] < ep) {
                    return op1;
                } else {
                    return new TreeInsertText(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.path,1,1), op1.text);
                }
            }
            //op1.path[1]>sp
            if (op1.path[1] < ep) {
                return new TreeInsertText(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.path,1,-1), op1.text);
            } else {
                return op1;
            }
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        if(op1.path[0]==position){
            if (op1.path[1] == sp) {
                if (sp >= ep) {
                    return new TreeDeleteText(op1.getSiteId(), op1.getPosition(),
                            TreeUtils.addC(op1.path,1, ep-op1.path[1]));
                } else {
                    return new TreeDeleteText(op1.getSiteId(), op1.getPosition(),
                            TreeUtils.addC(op1.path,1, ep-op1.path[1]-1));
                }
            }
            if (op1.path[1] < sp) {
                if (op1.path[1] < ep) {
                    return op1;
                } else {
                    return new TreeDeleteText(op1.getSiteId(), op1.getPosition(),
                            TreeUtils.addC(op1.path,1,1));
                }
            }
            //op1.path[1]>sp
            if (op1.path[1] < ep) {
                return new TreeDeleteText(op1.getSiteId(), op1.getPosition(),
                        TreeUtils.addC(op1.path,1,-1));
            } else {
                return op1;
            }
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
            if (op1.path[1] == sp) {
                if (sp >= ep) {
                    return new TreeDeleteTree(TreeUtils.addC(op1.path,1, ep-op1.path[1]));
                } else {
                    return new TreeDeleteTree(TreeUtils.addC(op1.path,1, ep-op1.path[1]-1));
                }
            }
            if (op1.path[1] < sp) {
                if (op1.path[1] < ep) {
                    return op1;
                } else {
                    return new TreeDeleteTree(TreeUtils.addC(op1.path,1,1));
                }
            }
            //op1.path[1]>sp
            if (op1.path[1] < ep) {
                return new TreeDeleteTree(TreeUtils.addC(op1.path,1,-1));
            } else {
                return op1;
            }
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeStyle(TreeStyle op1) {
        if(op1.path[0]==position){
            if (op1.path[1] == sp) {
                if (sp >= ep) {
                    return new TreeStyle(op1.getSiteId(), TreeUtils.addC(op1.path,1, ep-op1.path[1]), op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight,op1.getTagName());
                } else {
                    return new TreeStyle(op1.getSiteId(), TreeUtils.addC(op1.path,1, ep-op1.path[1]-1), op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight,op1.getTagName());
                }
            }
            if (op1.path[1] < sp) {
                if (op1.path[1] < ep) {
                    return op1;
                } else {
                    return new TreeStyle(op1.getSiteId(), TreeUtils.addC(op1.path,1,1), op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight,op1.getTagName());
                }
            }
            //op1.path[1]>sp
            if (op1.path[1] < ep) {
                return new TreeStyle(op1.getSiteId(), TreeUtils.addC(op1.path,1,-1), op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.splitRight,op1.getTagName());
            } else {
                return op1;
            }
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
            if (op1.path[1] == sp) {
                if (sp >= ep) {
                    return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.path,1, ep-op1.path[1]));
                } else {
                    return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.path,1, ep-op1.path[1]-1));
                }
            }
            if (op1.path[1] < sp) {
                if (op1.path[1] < ep) {
                    return op1;
                } else {
                    return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.path,1,1));
                }
            }
            //op1.path[1]>sp
            if (op1.path[1] < ep) {
                return new TreeCaretPosition(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.path,1,-1));
            } else {
                return op1;
            }
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMergeItem(TreeMergeItem op1) {
        if(op1.getPosition()==position){//same list
            
            //annuler le move si deplacement de droite, avant la gauche ; ou de gauche, après la droite
            //deplacement dans entre la fusion -> après la fusion
            if (sp == op1.posItem) {//move du paragraphe de droite du merge
                if (ep == sp - 1) {
                    //si move du paragraphe de droite juste avant celui de gauche, annuler et garder la fusion
                    return new TreeCompositeOperation(this, op1);
                }
                //sinon deplacer la gauche et fusionner
                if (ep > sp) {
                    return new TreeCompositeOperation(
                            new TreeMoveItem(op1.getSiteId(), position,sp - 1, ep - 1),
                            new TreeMergeItem(op1.getSiteId(),op1.getPosition(), ep - 1, op1.leftSiblingChildrenNr, op1.childrenNr)
                    );
                } else {
                    return new TreeCompositeOperation(this, new TreeMergeItem(op1.getSiteId(),op1.getPosition(),ep + 1, op1.leftSiblingChildrenNr, op1.childrenNr));
                }
            }
            if (sp == op1.posItem - 1) {//move du paragraphe de gauche du merge
                if (ep == sp + 2) {
                    //si move du paragraphe de gauche juste apres celui de droite, annuler et garder la fusion
                    return new TreeCompositeOperation(this, op1);
                }
                //sinon deplacer la droite et fusionner
                if (ep > sp) {
                    return new TreeCompositeOperation(this, new TreeMergeItem(op1.getSiteId(),op1.getPosition(),ep, op1.leftSiblingChildrenNr, op1.childrenNr));
                } else {
                    return new TreeCompositeOperation(
                            new TreeMoveItem(op1.getSiteId(),position, sp + 1, ep + 1),
                            new TreeMergeItem(op1.getSiteId(),op1.getPosition(),ep + 1, op1.leftSiblingChildrenNr, op1.childrenNr)
                    );
                }
            }
            if (op1.posItem < sp) {
                if (op1.posItem == ep) {
                    //si destination du move entre les fusionnés, placer apres le resultat de la fusion
                    return new TreeCompositeOperation(new TreeMoveItem(op1.getSiteId(),op1.getPosition(), ep, ep + 2), op1);
                }
                if (op1.posItem < ep) {
                    return op1;
                }
                //op1.posItem>ep
                return new TreeMergeItem(op1.getSiteId(),op1.getPosition(),op1.posItem + 1, op1.leftSiblingChildrenNr, op1.childrenNr);
            }
            //op1.posItem>sp
            if (op1.posItem == ep) {
                //si destination du move entre les fusionnés, placer apres le resultat de la fusion
                return new TreeCompositeOperation(new TreeMoveItem(op1.getSiteId(),op1.getPosition(), ep - 1, ep + 1),
                        new TreeMergeItem(op1.getSiteId(),op1.getPosition(),op1.posItem - 1, op1.leftSiblingChildrenNr, op1.childrenNr));
            }
            if (op1.posItem < ep) {
                return new TreeMergeItem(op1.getSiteId(),op1.getPosition(),op1.posItem - 1, op1.leftSiblingChildrenNr, op1.childrenNr);
            }
            //op1.posItem>ep
            return op1;      
            }
        
        //different list
        return op1;
    }

    @Override
    protected TreeOperation handleTreeMoveItem(TreeMoveItem op1) {
        if(op1.getPosition()==position){
            if (op1.sp == sp) {
                if (op1.ep == ep) {
                    return new TreeIdOp();
                }
                if (op1.ep < ep) {//le plus loin (op2) gagne
                    if (op1.sp < op1.ep) {
                        return new TreeMoveItem(op1.getSiteId(),position, op1.ep - 1, ep);
                    }
                    if (ep < op1.sp) {
                        return new TreeMoveItem(op1.getSiteId(),position, op1.ep, ep + 1);
                    }
                    //op1.e1<sp<ep
                    return new TreeMoveItem(op1.getSiteId(),position, op1.ep, ep);
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
            return new TreeMoveItem(op1.getSiteId(),position, sp, ep);            
            }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeNewItem(TreeNewItem op1) {
        if(op1.getPosition()==position){
            if (sp < op1.posItem) {
                if (ep <= op1.posItem) {
                    return op1;
                }
                return new TreeNewItem(op1.getSiteId(),position,op1.posItem - 1);
            }
            //sp>=op1.posItem
            if (ep <= op1.posItem) {
                return new TreeNewItem(op1.getSiteId(), position,op1.posItem + 1);
            }
            return op1;            
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
            if (op1.getPath()[1] == sp) {
                if (sp >= ep) {
                    return new TreeSplitItem(op1.getSiteId(), op1.getPosition(),TreeUtils.addC(op1.getPath(),1,ep-op1.getPath()[1]), op1.splitLeft);
                } else {
                    return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.getPath(),1,ep-op1.getPath()[1]-1), op1.splitLeft);
                }
            }
            if (op1.getPath()[1] < sp) {
                if (op1.getPath()[1] < ep) {
                    return op1;
                } else {
                    return new TreeSplitItem(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.getPath(),1, 1), op1.splitLeft);
                }
            }
            //op1.path[1]>sp
            if (op1.getPath()[1] < ep) {
                return new TreeSplitItem(op1.getSiteId(), op1.getPosition(), TreeUtils.addC(op1.getPath(),1,-1), op1.splitLeft);
            } else {
                return op1;            
            }
        }
        return op1;
    }

    @Override
    protected TreeOperation handleTreeUpdateElement(TreeUpdateElement op1) {
        if(op1.path[0]==position){
            if (op1.path[1] == sp) {
                if (sp >= ep) {
                    return new TreeUpdateElement(op1.getSiteId(), TreeUtils.addC(op1.path,1, ep-op1.path[0]),
                            op1.tag, op1.value);
                } else {
                    return new TreeUpdateElement(op1.getSiteId(), TreeUtils.addC(op1.path,1, ep-op1.path[0]-1),
                            op1.tag, op1.value);
                }
            }
            if (op1.path[1] < sp) {
                if (op1.path[1] < ep) {
                    return op1;
                } else {
                    return new TreeUpdateElement(op1.getSiteId(), TreeUtils.addC(op1.path,1,1),
                            op1.tag, op1.value);
                }
            }
            //op1.path[1]>sp
            if (op1.path[1] < ep) {
                    return new TreeUpdateElement(op1.getSiteId(), TreeUtils.addC(op1.path,1,-1),
                            op1.tag, op1.value);
            } else {
                return op1;
            }            
        }
        return op1;
    }
    
}