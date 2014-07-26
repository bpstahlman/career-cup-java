
/*
You have k lists of sorted integers. Find the smallest range that includes at least one number from each of the k lists. 

For example, 
List 1: [4, 10, 15, 24, 26] 
List 2: [0, 9, 12, 20] 
List 3: [5, 18, 22, 30] 

The smallest range here would be [20, 24] as it contains 24 from list 1, 20 from list 2, and 22 from list 3.
*/

import java.util.*;

class Range implements Comparable<Range> {
    Integer lval = null, rval = null;
    Range() {}
    Range(Integer lval, Integer rval) {
        this.lval = lval;
        this.rval = rval;
    }
    public String toString() {
        return "[" + lval + ".." + rval + "]";
    }
    public int compareTo(Range other) {
        int span = span(), otherSpan = other.span();
        return span < otherSpan ? -1 : span > otherSpan ? 1 : 0;
    }
    public int span() {
        return lval == null || rval == null ? Integer.MAX_VALUE : rval - lval;
    }
    void replaceMaybe(Range range) {
        if (range != null && this.compareTo(range) > 0) {
            lval = range.lval;
            rval = range.rval;
        }
    }
}
class RangeFinder {
    int[][] ll;
    RangeFinder(List<List<Integer>> ll) {
        // Convert the collection to a 2D array for convenience.
        this.ll = new int[ll.size()][];
        int i = 0;
        for (List<Integer> li : ll) {
            this.ll[i] = new int[li.size()];
            // Copy the values
            int j = 0;
            for (int n : li)
                this.ll[i][j++] = n;
            i++;
        }
        for (int[] ln : this.ll) {
            for (int n : ln) {
                //System.out.println(n);
            }
        }
    }
    Range find() {
        Range bestRange = new Range();
        int[] minIdxs = new int[ll.length]; // zero init
        // Recurse on all numbers in first list.
        for (int i = 0; i < ll[0].length; i++) {
            int rootVal = ll[0][i];
            recurse(1, new Range(rootVal, rootVal), bestRange);
            System.out.println(bestRange);
        }
        return bestRange;
    }
    void recurse(int listIdx, Range range, Range bestRange) {
        int nPrev = 0, numVals = ll[listIdx].length, numLists = ll.length;
        // Loop over numbers in current list.
        // TODO: Optimize by starting with earliest significant index...
        Range ltRange = null, rtRange = null;
        for (int i = 0; i < numVals; i++) {
            int n = ll[listIdx][i];
            if (n >= range.lval) {
                if (i > 0) {
                    // nPrev must have been < current range's left edge.
                    ltRange = new Range(nPrev, range.rval);
                    //System.out.println("Created ltRange at i > 0: " + ltRange);
                }
                // Current number may or may not widen rightward.
                rtRange = new Range(range.lval, Integer.max(n, range.rval));
                //System.out.println("Created rtRange at: " + rtRange);
                break;
            } else if (i == numVals - 1) {
                // This list has only left-widening potential.
                ltRange = new Range(n, range.rval);
                //System.out.println("Created lwo ltRange: " + ltRange);
            }
            nPrev = n;
        }
        if (listIdx + 1 < numLists) {
            if (ltRange != null) recurse(listIdx + 1, ltRange, bestRange);
            if (rtRange != null) recurse(listIdx + 1, rtRange, bestRange);
        } else {
            // Recursion complete. Is either lt or rt range better than
            // current best?
            if (ltRange != null) System.out.println("lt Leaf: Comparing " + bestRange + " with " + ltRange);
            if (rtRange != null) System.out.println("rt Leaf: Comparing " + bestRange + " with " + rtRange);
            if (ltRange != null) bestRange.replaceMaybe(ltRange);
            if (rtRange != null) bestRange.replaceMaybe(rtRange);
        }
    }
}

public class SmallestRangeForKLists {
    // Usage: java SmallestRangeForKLists 1234
    public static void main(String[] args) {
        List<List<Integer>> ll = new ArrayList<>(
            Arrays.asList(
                Arrays.asList(4, 10, 15, 24, 26),
                Arrays.asList(0, 9, 12, 20),
                Arrays.asList(5, 18, 22, 30)));
        Range range = new RangeFinder(ll).find();
    }
    
}
// vim:ts=4:sw=4:et:tw=78
