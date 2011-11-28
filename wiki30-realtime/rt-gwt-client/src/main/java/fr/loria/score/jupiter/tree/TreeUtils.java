package fr.loria.score.jupiter.tree;

/**
 * @author Bogdan.Flueras@inria.fr
 */
public class TreeUtils {
    //fonctions de comparaison de chemins

    //return true if p1 and p2 are different paths
    public static boolean diff(int[] t1, int[] t2) {
        if (t2.length != t1.length) {
            return true;
        }
        for (int i = 0; i < t1.length; i++) {
            if (t1[i] != t2[i]) {
                return true;
            }
        }
        return false;
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
    //given two paths in the same paragraph, rewites the second one as if the first one was x+1/0/0/...
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
}
