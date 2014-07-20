import java.util.*;
import java.util.function.*;

// Each Node instance is a node in a binary tree representing all permutations of N pairs of parens. The permutations
// are generated as follows:
// accumulate the chars (`(' or `)') from root to leaf to build a String for each full tree traversal. (A full tree
// traversal is one whose leaf depth equals maxDepth (i.e., 2N - 1).
class Node {
    Node l, r; // left/right child
    char c;    // '(' or ')'
    Node(char c) {
        this.c = c;
    }
    // Build tree of Nodes recursively.
    // depth: 0-based depth of invoking tree node
    // numOpen: # of `(' encountered in traversal up to and including this node.
    // Note: Traversals that end before maxDepth represent impossible permutations: e.g.,
    // N=2: ())
    // N=2: (((
    void buildTree(int maxDepth, int depth, int numOpen) {
        int numClosed = depth + 1 - numOpen;
        if (depth >= maxDepth)
            return;
        // Has this traversal used all open parens?
        if (numOpen < (maxDepth + 1) / 2) {
            // Recurse left.
            l = new Node('(');
            l.buildTree(maxDepth, depth + 1, numOpen + 1);
        }
        // Are there any unclosed open parens in this traversal?
        if (numClosed < numOpen) {
            // Recurse right.
            r = new Node(')');
            r.buildTree(maxDepth, depth + 1, numOpen);
        }
    }
    // Performs an in-order DFS on the Node tree, invoking the supplied BiConsumer functional interface method for each
    // Node, passing the following to its accept method:
    //   leaf: true iff current node is leaf of full (i.e., *non-truncated*) traversal
    //   path: accumulated string representing traversal up to and including current node
    void cascadeBy(int maxDepth, int depth, String path, BiConsumer<Boolean, String> fi) {
        path += c;
        fi.accept(depth == maxDepth, path);
        if (l != null) l.cascadeBy(maxDepth, depth + 1, path, fi);
        if (r != null) r.cascadeBy(maxDepth, depth + 1, path, fi);
    }
}
public class PermuteParens {
    public static void main(String[] args) {
        // Determine # of paren pairs to be permuted.
        int N = Integer.parseInt(args[0]);
        int maxDepth = 2 * N - 1;

        // Construct root node and recurse to build binary tree.
        Node root = new Node('(');
        // Note: Could simplify client interface by implementing top-level wrappers for both buildTree and cascadeBy.
        root.buildTree(maxDepth, 0, 1);

        // Now that tree is built, use cascadeBy method to visit each node in DFS traversal, accumulating a single
        // permutation string at the leaf of each full traversal.
        // Note: There really isn't any need to visit each node in this way; for that matter, there was no need to build
        // the tree, since we could simply have generated output during recursion. I'm really just playing around here
        // (e.g., with Java 8 Functional Interfaces)...
        // Note: The tree traversal itself ensures the permutations are in sorted order.
        Set<String> perms = new TreeSet<String>();
        root.cascadeBy(maxDepth, 0, "", (Boolean leaf, String path) -> {
            if (leaf)
                perms.add(path);
        });
        // Output the list of permutations.
        int i = 1;
        for (String perm : perms) {
            System.out.print(i++ + ".\t");
            System.out.println(perm);
        }
    }
}
// vim:ts=4:sw=4:et:tw=120
