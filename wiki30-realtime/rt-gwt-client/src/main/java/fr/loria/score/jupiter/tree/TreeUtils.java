package fr.loria.score.jupiter.tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fonctions de comparaison de chemins
 *
 * @author Luc.Andre@loria.fr
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeUtils {

    //return true if p1 and p2 are different paths
    public static boolean diff(int[] t1, int[] t2) {
        return !Arrays.equals(t1, t2);
    }

    //return true if the pat t1 is before the path t2 (bottom-left ordering)
    public static boolean inf(int[] t1, int[] t2) {
        int i = 0;
        while (i < t1.length && i < t2.length) {
            if (t1[i] < t2[i]) {
                return true;
            }
            if (t1[i] > t2[i]) {
                return false;
            }
            i++;
        }
        return (t1.length < t2.length);
    }

    //fonctions de modification de chemins

    //add nb to the start of the path (paragraph index)
    //addP(1/0/2,2)=3/0/2
    public static int[] addP(int[] path, int nb) {
        int[] tab = new int[path.length];
        tab[0] = path[0] + nb;
        for (int i = 1; i < path.length; i++) {
            tab[i] = path[i];
        }
        return tab;
    }

    //set nb as the new start of the path (paragraph index)
    //setP(1/0/2,2)=2/0/2
    public static int[] setP(int[] path, int nb) {
        int[] tab = new int[path.length];
        tab[0] = nb;
        for (int i = 1; i < path.length; i++) {
            tab[i] = path[i];
        }
        return tab;
    }

    //add nb at the position pos of the path
    //addC(1/2/0,1,3)=1/5/0
    public static int[] addC(int[] path, int pos, int nb) {
        int[] tab = new int[path.length];
        for (int i = 0; i < path.length; i++) {
            tab[i] = path[i];
        }
        tab[pos] = tab[pos] + nb;
        return tab;
    }

    //add a new level to the path
    //addLevel(1/3)=1/3/0
    //used when a received operation was created on a text node without style (for exemple path 1/3)
    //and has to be executed now on a state where a style was executed (so new path is 1/3/0)
    public static int[] addLevel(int[] path) {
        int[] tab = new int[path.length + 1];
        for (int i = 0; i < path.length; i++) {
            tab[i] = path[i];
        }
        tab[tab.length - 1] = 0;
        return tab;
    }

    //used to change a path after a paragraph split occured.
    //given two paths in the same paragraph, rewrites the second one as if the first one was x+1/0/0/...
    //reference(1/3/2,1/4/0)=2/1/0
    //reference(1/3/2,1/3/3)=2/0/1
    public static int[] reference(int[] path, int[] ref) {
        int[] tab = addP(path, 1);
        int i = 1;
        while (ref[i] == tab[i]) {
            tab[i] = 0;
            i++;
        }
        tab[i] = tab[i] - ref[i];
        return tab;
    }

    /**
     * Convert the path into a string as required by XWiki code
     *
     * @param path the path of the node
     * @return the locator as a string with slashes as delimitators
     */
    public static String getStringLocatorFromPath(List<Integer> path) {
        StringBuffer locator = new StringBuffer();
        for (Integer i : path) {
            locator.append(i).append("/");
        }
        return locator.toString();
    }

    public static Tree cloneTree(Tree root) {
        if (root == null) {
            return null;
        }
        while (root.parent != null) {
            root = root.parent;
        }
        return cloneTree(root, null); // implicitly root's parent is null
    }

    /**
     * @param root the tree to be cloned
     * @param parent the parent of the tree to be cloned
     * @return a deep clone of the give tree: parent, children, attributes
     */
    private static Tree cloneTree(Tree root, Tree parent) {
        Tree cloned = new Tree();
        cloned.setParent(parent);

        Map<String, String> clonedAttrs = new HashMap<String, String>();
        clonedAttrs.putAll(root.attributes);
        cloned.setAttributes(clonedAttrs);

        for (Tree child : root.children) {
            Tree clonedChild = cloneTree(child, cloned);
            cloned.addChild(clonedChild);
        }

        cloned.invisible = root.invisible;
        return cloned;
    }
}
