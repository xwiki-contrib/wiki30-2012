package fr.loria.score.jupiter.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fonctions de comparaison de chemins
 *
 * @author Luc.Andre@loria.fr
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeUtils {

    //return true if p1 and p2 are different paths
    public static boolean diff(List<Integer> t1, List<Integer> t2) {
        return !Arrays.equals(t1.toArray(), t2.toArray());
    }

    //return true if the pat t1 is before the path t2 (bottom-left ordering)
    public static boolean inf(List<Integer> t1, List<Integer> t2) {
        int i = 0;
        while (i < t1.size() && i < t2.size()) {
            if (t1.get(i) < t2.get(i)) {
                return true;
            }
            if (t1.get(i) > t2.get(i)) {
                return false;
            }
            i++;
        }
        return (t1.size() < t2.size());
    }

    //fonctions de modification de chemins

    //add nb to the start of the path (paragraph index)
    //addP(1/0/2, 2)=3/0/2
    public static List<Integer> addP(List<Integer> path, int nb) {
       List<Integer> copy = new ArrayList<Integer>(path.size());
       copy.add(0, path.get(0) + nb);
       for (int i = 1; i < path.size(); i++) {
           copy.add(new Integer(path.get(i).intValue()));
       }
       return copy;
    }

    //set nb as the new start of the path (paragraph index)
    //setP(1/0/2,2)=2/0/2
    public static List<Integer> setP(List<Integer> path, int nb) {
        List<Integer> tab = new ArrayList<Integer>(path.size());
        tab.add(0, nb);
        for (int i = 1; i < path.size(); i++) {
            tab.add(new Integer(path.get(i).intValue()));
        }
        return tab;
    }

    //add nb at the position pos of the path
    //addC(1/2/0,1,3)=1/5/0
    public static List<Integer> addC(List<Integer> path, int pos, int nb) {
        List<Integer> tab = new ArrayList<Integer>(path.size());
        for (int i = 0; i < path.size(); i++) {
            tab.add(new Integer(path.get(i).intValue()));
        }
        tab.set(pos,tab.get(pos) + nb);
        return tab;
    }

    //add a new level to the path
    //addLevel(1/3)=1/3/0
    //used when a received operation was created on a text node without style (for exemple path 1/3)
    //and has to be executed now on a state where a style was executed (so new path is 1/3/0)
    public static List<Integer> addLevel(List<Integer> path) {
        List<Integer> tab = new ArrayList<Integer>(path.size() + 1);
        for (int i = 0; i < path.size(); i++) {
            tab.add(new Integer(path.get(i).intValue()));
        }
        tab.add(0);
        return tab;
    }

    //used to change a path after a paragraph split occured.
    //given two paths in the same paragraph, rewrites the second one as if the first one was x+1/0/0/...
    //reference(1/3/2,1/4/0)=2/1/0
    //reference(1/3/2,1/3/3)=2/0/1
    public static List<Integer> reference(List<Integer> path, List<Integer> ref) {
        List<Integer> tab = addP(path, 1);
        int i = 1;
        while (ref.get(i).equals(tab.get(i))) {
            tab.set(i, 0);
            i++;
        }
        tab.set(i, tab.get(i) - ref.get(i));
        return tab;
    }

    /**
     * Convert the path into a string as required by XWiki code
     * @param path the path of the node
     * @return the locator as a string with slashes as delimitators
     */
    public static String getStringLocatorFromPath(List<Integer> path) {
        StringBuffer locator = new StringBuffer();
        for(Integer i : path) {
            locator.append(i).append("/");
        }
        return locator.toString();
    }
}
