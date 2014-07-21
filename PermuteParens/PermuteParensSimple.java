import java.util.*;

public class PermuteParensSimple {
    // Usage: java PermuteParens 3
    // Output:
    /*
    ((()))
    (()())
    (())()
    ()(())
    ()()()
    */
    public static void main(String[] args) {
        // Determine # of paren pairs to be permuted.
        int N = Integer.parseInt(args[0]);
        int numOpen = N, numClose = N;

        walkTree(numOpen, numClose, "");
    }
    // Walk "virtual tree" of nodes recursively, outputting all valid
    // permutations of N parens. Each node in the tree represents either a `('
    // or `)'. Each complete traversal represents a possible permutation of N
    // pairs of parens, defined by the nodes in the path from root to leaf.
    // Note: Traversals that end before all open/close parens have been used
    // (i.e., traversals with fewer than 2N nodes) represent impossible
    // permutations and produce no output: e.g.,
    // N=2: ())
    // N=2: (((
    // Inputs:
    // numOpen: # of open parens remaining in current traversal
    // numClose: # of close parens remaining in current traversal
    static void walkTree(int numOpen, int numClose, String path) {
        if (numOpen == 0 && numClose == 0) {
            // Output accumulated path at end of complete traversal.
            System.out.println(path);
            return;
        }
        // Has this traversal used all open parens?
        if (numOpen > 0)
            // Recurse left.
            walkTree(numOpen - 1, numClose, path + "(");
        // Are there any unmatched open parens in this traversal?
        if (numOpen < numClose)
            // Recurse right.
            walkTree(numOpen, numClose - 1, path + ")");
    }
}
// vim:ts=4:sw=4:et:tw=78
