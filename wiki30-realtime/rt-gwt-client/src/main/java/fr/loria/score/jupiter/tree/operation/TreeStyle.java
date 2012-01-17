package fr.loria.score.jupiter.tree.operation;


import fr.loria.score.jupiter.tree.Tree;
import fr.loria.score.jupiter.tree.TreeFactory;
import fr.loria.score.jupiter.tree.TreeUtils;

import java.util.ArrayList;

public class TreeStyle extends TreeOperation {

    public int start; // inclusive index
    public int end; //exclusive index
    public String param;
    public String value;
    public boolean addStyle; //true if adding a <style> node is needed, false if it already exixts
    public boolean splitLeft;//split left : true if start<>0 (generation)  // todo: I dont understand this
    public boolean sr;//split right : true if end= end of the string (generation)

    public TreeStyle() {}

    public TreeStyle(int siteId, int[] path, int start, int end, String param, String value, boolean addStyle, boolean splitLeft, boolean splitRight) {
        this.path = path;
        this.start = start;
        this.end = end;
        this.param = param;
        this.value = value;
        this.siteId = siteId;
        this.addStyle = addStyle;
        this.splitLeft = splitLeft;
        sr = splitRight;
    }

    public void execute(Tree root) {
        Tree tree = root;
        for (int i = 0; i < path.length - 1; i++) {
            tree = tree.getChild(path[i]);
        }
        //last==tree.getChild(path[path.length-1]).getValue.length;
        if/*(start==0)*/ (!splitLeft) {
            if/*(end==last)*/ (!sr) {//whole String
                if/*(tree.getValue().toString().equals("style"))*/ (!addStyle) {//suppose one child
                    tree.setAttribute(param, value);
                } else {
                    //addStyle=true;
                    Tree tc = tree.removeChild(path[path.length - 1]);
                    Tree ts = TreeFactory.createElementTree("style"); //todo: on native DOM this is an attribute !
                    ts.setAttribute(param, value);
                    ts.addChild(tc);
                    tree.addChild(ts, path[path.length - 1]);
                    if (tc.isInvisible()) {
                        ts.hide();
                    }
                }
            } else {//Style applied to the left side
                if/*(tree.getValue().toString().equals("style"))*/ (!addStyle) {//suppose one child
                    String text = tree.getChild(path[path.length - 1]).split(end);
                    Tree tc = TreeFactory.createTextTree(text);
                    Tree ts = tree.cloneNode();
                    tree.setAttribute(param, value);
                    ts.addChild(tc);
                    Tree parent = tree.getParent();
                    parent.addChild(ts, path[path.length - 2] + 1);
                    if (tree.isInvisible()) {
                        ts.hide();
                    }
                } else {
                    //addStyle=true;
                    Tree t = tree.getChild(path[path.length - 1]);
                    Tree tc = TreeFactory.createTextTree(t.split(end));
                    Tree ts = TreeFactory.createElementTree("style");
                    ts.setAttribute(param, value);
                    ts.addChild(tree.removeChild(path[path.length - 1]));
                    tree.addChild(tc, path[path.length - 1]);
                    tree.addChild(ts, path[path.length - 1]);
                    if (t.isInvisible()) {
                        tc.hide();
                        ts.hide();
                    }
                }
            }
        } else {
            if/*(end==last)*/ (!sr) {//Style applied to the right side
                if/*(tree.getValue().toString().equals("style"))*/ (!addStyle) {//suppose one child
                    Tree tc = TreeFactory.createTextTree(tree.getChild(path[path.length - 1]).split(start));
                    Tree ts = tree.cloneNode();
                    ts.setAttribute(param, value);
                    ts.addChild(tc);
                    Tree parent = tree.getParent();
                    parent.addChild(ts, path[path.length - 2] + 1);
                    if (tree.isInvisible()) {
                        ts.hide();
                    }
                } else {
                    //addStyle=true;
                    Tree t = tree.getChild(path[path.length - 1]);
                    Tree tc = TreeFactory.createTextTree(t.split(start));
                    Tree ts = TreeFactory.createElementTree("style");
                    ts.setAttribute(param, value);
                    ts.addChild(tc);
                    tree.addChild(ts, path[path.length - 1] + 1);
                    if (t.isInvisible()) {
                        tc.hide();
                        ts.hide();
                    }
                }
            } else {//Style applied to the middle
                if/*(tree.getValue().toString().equals("style"))*/ (!addStyle) {//suppose one child
                    Tree tc = TreeFactory.createTextTree(tree.getChild(path[path.length - 1]).split(start));
                    Tree tc2 = TreeFactory.createTextTree(tc.split(end - start));
                    Tree ts = tree.cloneNode();
                    Tree ts2 = tree.cloneNode();
                    ts.setAttribute(param, value);
                    ts.addChild(tc);
                    ts2.addChild(tc2);
                    Tree parent = tree.getParent();
                    parent.addChild(ts, path[path.length - 2] + 1);
                    parent.addChild(ts2, path[path.length - 2] + 2);
                    if (tree.isInvisible()) {
                        ts.hide();
                        ts2.hide();
                    }
                } else {
                    //addStyle=true;
                    Tree t = tree.getChild(path[path.length - 1]);
                    Tree tc = TreeFactory.createTextTree(t.split(start));
                    Tree tc2 = TreeFactory.createTextTree( tc.split(end - start));
                    Tree ts = TreeFactory.createElementTree("style");
                    ts.setAttribute(param, value);
                    ts.addChild(tc);
                    tree.addChild(ts, path[path.length - 1] + 1);
                    tree.addChild(tc2, path[path.length - 1] + 2);
                    if (t.isInvisible()) {
                        tc.hide();
                        ts.hide();
                        tc2.hide();
                    }
                }
            }
        }

    }

    public String toString() {
        return "TreeStyle(" + super.toString() + ", start: " + start + ", end: " + end + ", param: " + param + ", value: " + value + ")";
    }

    //OT pour Style
    public TreeOperation handleTreeInsertText(TreeInsertText op1) {
        if (op1.path[0] != path[0]) {
            return op1;
        }
        if (op1.path[1] < path[1]) {
            return op1;
        }
        if (op1.path[1] == path[1]) {//meme chemin
            if (op1.getPosition() < start) {
                return op1;
            }
            if (op1.getPosition() == start || op1.getPosition() <= end) {
                int[] tab = TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0);
                if (addStyle) {
                    tab = TreeUtils.addLevel(tab);
                }
                return new TreeInsertText(op1.getSiteId(), op1.getPosition() - start, tab, op1.text);
            }
            int[] tab = TreeUtils.addC(op1.path, 1, splitLeft ? 2 : 1);
            if (addStyle) {
                tab = TreeUtils.addLevel(tab);
            }
            return new TreeInsertText(op1.getSiteId(), op1.getPosition() - end, tab, op1.text);
        }
        //op1.path[1]>path[1]
        int d = 0;//decalage
        if (splitLeft) {
            d++;
        }
        if (sr) {
            d++;
        }
        int[] tab = TreeUtils.addC(op1.path, 1, d);
        return new TreeInsertText(op1.getSiteId(), op1.getPosition(), tab, op1.text);
    }

    public TreeOperation handleTreeDeleteText(TreeDeleteText op1) {
        if (op1.path[0] != path[0]) {
            return op1;
        }
        if (op1.path[1] < path[1]) {
            return op1;
        }
        if (op1.path[1] == path[1]) {//meme chemin
            if (op1.getPosition() < start) {
                return op1;
            }
            if (op1.getPosition() == start || op1.getPosition() < end) {
                int[] tab = TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0);
                if (addStyle) {
                    tab = TreeUtils.addLevel(tab);
                }
                return new TreeDeleteText(op1.getSiteId(), op1.getPosition() - start, tab);
            }
            int[] tab = TreeUtils.addC(op1.path, 1, splitLeft ? 2 : 1);
            if (addStyle) {
                tab = TreeUtils.addLevel(tab);
            }
            return new TreeDeleteText(op1.getSiteId(), op1.getPosition() - end, tab);
        }
        //op1.path[1]>path[1]
        int d = 0;//decalage
        if (splitLeft) {
            d++;
        }
        if (sr) {
            d++;
        }
        int[] tab = TreeUtils.addC(op1.path, 1, d);
        return new TreeDeleteText(op1.getSiteId(), op1.getPosition(), tab);
    }

    public TreeOperation handleTreeNewParagraph(TreeNewParagraph op1) {
        return op1;
    }

    public TreeOperation handleTreeMergeParagraph(TreeMergeParagraph op1) {
        if (op1.getPosition() == path[0]) {
            int d = 0;
            if (splitLeft) {
                d++;
            }
            if (sr) {
                d++;
            }
            return new TreeMergeParagraph(op1.getPosition(), op1.leftSiblingChildrenNr, op1.rightSiblingChildrenNr + d);
        }
        if (op1.getPosition() == path[0] + 1) {
            int d = 0;
            if (splitLeft) {
                d++;
            }
            if (sr) {
                d++;
            }
            return new TreeMergeParagraph(op1.getPosition(), op1.leftSiblingChildrenNr + d, op1.rightSiblingChildrenNr);
        }
        return op1;
    }

    public TreeOperation handleTreeInsertParagraph(TreeInsertParagraph op1) {
        if (op1.path[0] != path[0]) {
            return op1;
        }
        if (op1.path[1] < path[1]) {
            return op1;
        }
        if (op1.path[1] == path[1]) {//meme chemin
            if (op1.getPosition() < start) {
                return op1;
            }
            /*if(op1.getPosition()==start || op1.getPosition()<end){
				int[] tab=TreeUtils.addC(op1.path,1,splitLeft?1:0);
				if(addStyle){tab=TreeUtils.addLevel(tab);}
				return new TreeInsertParagraph(op1.getPosition()-start,tab,op1.siteId,op1.splitLeft);
			}*/
            if (op1.getPosition() == start) {
                int[] tab = TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0);
                if (addStyle) {
                    tab = TreeUtils.addLevel(tab);
                }
                return new TreeInsertParagraph(op1.getSiteId(), 0, tab, false);
            }
            if (op1.getPosition() < end) {
                int[] tab = TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0);
                if (addStyle) {
                    tab = TreeUtils.addLevel(tab);
                }
                return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition() - start, tab, op1.splitLeft);
            }
            int[] tab = TreeUtils.addC(op1.path, 1, splitLeft ? 2 : 1);
            if (op1.getPosition() == end) {
                return new TreeInsertParagraph(op1.getSiteId(), 0, tab, false);
            }
            return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition() - end, tab, op1.splitLeft);
        }
        //op1.path[1]>path[1]
        int d = 0;//decalage
        if (splitLeft) {
            d++;
        }
        if (sr) {
            d++;
        }
        int[] tab = TreeUtils.addC(op1.path, 1, d);
        return new TreeInsertParagraph(op1.getSiteId(), op1.getPosition(), tab, op1.splitLeft);
    }

    public TreeOperation handleTreeDeleteTree(TreeDeleteTree op1) {
        if (op1.path[0] != path[0]) {
            return op1;
        }
        if (op1.path.length == 1) {
            return op1;
        }
        if (op1.path[1] < path[1]) {
            return op1;
        }
        int d = 0;//decalage;
        if (splitLeft) {
            d++;
        }
        if (sr) {
            d++;
        }
        if (op1.path[1] > path[1]) {
            int[] tab = TreeUtils.addC(op1.path, 1, d);
            return new TreeDeleteTree(tab);
        }
        ArrayList<TreeOperation> list = new ArrayList<TreeOperation>();
        for (int i = 0; i <= d; i++) {
            int[] tab = TreeUtils.addC(op1.path, 1, i);
            list.add(new TreeDeleteTree(tab));
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        return new TreeCompositeOperation(list);
    }

    public TreeOperation handleTreeStyle(TreeStyle op1) {
        if (op1.path[0] != path[0]) {
            return op1;
        }
        if (op1.path[1] < path[1]) {
            return op1;
        }
        int d = 0;//decalage;
        if (splitLeft) {
            d++;
        }
        if (sr) {
            d++;
        }
        if (op1.path[1] > path[1]) {
            int[] tab = TreeUtils.addC(op1.path, 1, d);
            return new TreeStyle(op1.siteId, tab, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.sr);
        }
        //op1.path=path
        //utilisation de l'ID pour l'édition du même paramètre
        ArrayList<TreeOperation> list = new ArrayList<TreeOperation>();
        if (op1.start < start) {
            if (op1.end <= start) {//style1 before style2
                return new TreeStyle(op1.siteId, op1.path, op1.start, op1.end, op1.param, op1.value, op1.addStyle, op1.splitLeft, op1.end == start ? false : true);
            }
            list.add(new TreeStyle(op1.siteId, op1.path, op1.start, start, op1.param, op1.value, op1.addStyle, op1.splitLeft, false));
            if (op1.end >= end) {//style2 into style1
                if (!(op1.param.equals(param)) || op1.siteId < siteId) {//no conflict between s1 and s2, or s1 win
                    list.add(new TreeStyle(op1.siteId, !addStyle ? TreeUtils.addC(op1.path, 1, op1.splitLeft ? 2 : 1) : TreeUtils.addLevel(TreeUtils.addC(op1.path, 1, op1.splitLeft ? 2 : 1)), 0,
                            end - start, op1.param, op1.value, false, false, false));
                } else {//apply op2 style to split the String
                    list.add(new TreeStyle(op1.siteId, !addStyle ? TreeUtils.addC(op1.path, 1, op1.splitLeft ? 2 : 1) : TreeUtils.addLevel(TreeUtils.addC(op1.path, 1, op1.splitLeft ? 2 : 1)), 0,
                            end - start, op1.param, value, false, false, false));
                }
                if (op1.end != end) {
                    list.add(new TreeStyle(op1.siteId, TreeUtils.addC(op1.path, 1, op1.splitLeft ? 3 : 2), 0, op1.end - end, op1.param, op1.value,
                            op1.addStyle, false, op1.sr));
                }
                return new TreeCompositeOperation(list);
            }
            //op1.end<end && op1.end>start
            if (!(op1.param.equals(param)) || op1.siteId < siteId) {//no conflict between s1 and s2, or s1 win
                list.add(new TreeStyle(op1.siteId, !addStyle ? TreeUtils.addC(op1.path, 1, op1.splitLeft ? 2 : 1) : TreeUtils.addLevel(TreeUtils.addC(op1.path, 1, op1.splitLeft ? 2 : 1)),
                        0, op1.end - start, op1.param, op1.value, false, false, true));
            } else {//apply op2 style to split the String
                list.add(new TreeStyle(op1.siteId, !addStyle ? TreeUtils.addC(op1.path, 1, op1.splitLeft ? 2 : 1) : TreeUtils.addLevel(TreeUtils.addC(op1.path, 1, op1.splitLeft ? 2 : 1)),
                        0, op1.end - start, op1.param, value, false, false, true));
            }
            return new TreeCompositeOperation(list);
        }
        if (op1.start == start) {
            if (!(op1.param.equals(param)) || op1.siteId < siteId) {//no conflict between s1 and s2, or s1 win
                list.add(new TreeStyle(op1.siteId, !addStyle ? TreeUtils.addC(op1.path, 1, op1.splitLeft ? 1 : 0) : TreeUtils.addLevel(TreeUtils.addC(op1.path, 1, op1.splitLeft ? 1 : 0)),
                        0, (op1.end >= end) ? end - start : op1.end - op1.start,
                        op1.param, op1.value, false, false, op1.end <= end ? false : true));
            } else {//apply op2 style to split the String
                list.add(new TreeStyle(op1.siteId, !addStyle ? TreeUtils.addC(op1.path, 1, op1.start == 0 ? 0 : 1) : TreeUtils.addLevel(TreeUtils.addC(op1.path, 1, op1.start == 0 ? 0 : 1)),
                        0, (op1.end >= end) ? end - start : op1.end - op1.start,
                        op1.param, value, false, false, op1.end <= end ? false : true));
            }
            if (op1.end > end) {
                list.add(new TreeStyle(op1.siteId, TreeUtils.addC(op1.path, 1, op1.splitLeft ? 2 : 1), 0, op1.end - end, op1.param, op1.value,
                        op1.addStyle, false, op1.sr));
            }
            if (list.size() == 1) {
                return list.get(0);
            }
            return new TreeCompositeOperation(list);
        }
        //op1.start>start
        if (op1.start >= end) {//style1 after style2
            return new TreeStyle(op1.siteId, TreeUtils.addC(op1.path, 1, splitLeft ? 2 : 1), op1.start - end, op1.end - end,
                    op1.param, op1.value, op1.addStyle, op1.start == end ? false : true, op1.sr);
        }
        if (op1.end <= end) {//style1 between style 2
            if (!(op1.param.equals(param)) || op1.siteId < siteId) {//no conflict between s1 and s2, or s1 win
                return new TreeStyle(op1.siteId, !addStyle ? TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0) : TreeUtils.addLevel(TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0)),
                        op1.start - start, op1.end - start, op1.param, op1.value,
                        false, true, op1.end == end ? false : true);
            } else {//apply op2 style to split the String
                return new TreeStyle(op1.siteId, !addStyle ? TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0) : TreeUtils.addLevel(TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0)),
                        op1.start - start, op1.end - start, op1.param, value,
                        false, true, op1.end == end ? false : true);
            }
        }
        //last case : s2<s1<e2<e1
        if (!(op1.param.equals(param)) || op1.siteId < siteId) {//no conflict between s1 and s2, or s1 win
            list.add(new TreeStyle(op1.siteId, !addStyle ? TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0) : TreeUtils.addLevel(TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0)),
                    op1.start - start, end - start, op1.param, op1.value, false, true, false));
        } else {//apply op2 style to split the String
            list.add(new TreeStyle(op1.siteId, !addStyle ? TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0) : TreeUtils.addLevel(TreeUtils.addC(op1.path, 1, splitLeft ? 1 : 0)),
                    op1.start - start, end - start, op1.param, value, false, true, false));
        }
        list.add(new TreeStyle(op1.siteId, TreeUtils.addC(op1.path, 1, splitLeft ? 3 : 2), 0, op1.end - end, op1.param, op1.value, op1.addStyle, false, op1.sr));
        return new TreeCompositeOperation(list);
    }

    public TreeOperation handleTreeMoveParagraph(TreeMoveParagraph op1) {
        return op1;
    }
}
