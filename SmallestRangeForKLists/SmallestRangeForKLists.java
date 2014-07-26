
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
    List<List<Integer>> ll;
    RangeFinder(List<List<Integer>> ll) { this.ll = ll; }
    Range find() {
        List<List<Integer>> subLists = new ArrayList<List<Integer>>();
        // Initialize the sub-lists to the full lists.
        // Note: Recursive calls will successively narrow.
        for (List<Integer> li : ll) subLists.add(li);
        // Recurse on all numbers in first list to find best range.
        // Note: Recursion base case updates bestRange if the range it's
        // produced is better.
        Range bestRange = new Range();
        for (int n : subLists.get(0)) {
            recurse(1, new Range(n, n), bestRange, subLists);
            System.out.println("Iteration complete. bestRange: " + bestRange);
        }
        return bestRange;
    }
    void recurse(int listIdx, Range range, Range bestRange, List<List<Integer>> subLists) {
        List<Integer> subList = subLists.get(listIdx);
        int nPrev = 0, numVals = subList.size(), numLists = subLists.size();
        // Loop over (remaining) numbers in current (sub-)list.
        Range ltRange = null, rtRange = null;
        int idx = 0;
        for (int n : subList) {
            if (n >= range.lval) {
                if (idx > 0) {
                    // nPrev must have been < current range's left edge.
                    ltRange = new Range(nPrev, range.rval);
                }
                // Current number may or may not widen rightward.
                rtRange = new Range(range.lval, Integer.max(n, range.rval));
                break;
            } else if (idx == numVals - 1) {
                // This list has only left-widening potential.
                ltRange = new Range(n, range.rval);
                break; // Prevent idx increment
            }
            nPrev = n;
            idx++;
        }
        // Discard nPrev (if applicable) and earlier numbers.
        // Assumption: Can't get out of preceding loop without idx pointing to
        // the index of the first number with potential significance for
        // subsequent roots.
        // Rationale: No subsequent root could provide better solution
        // with nPrev (or earlier) than can current root.
        subLists.set(listIdx, subList.subList(idx, subList.size()));
        System.out.println("Narrowing subList " + listIdx + ": " + subLists.get(listIdx));
        if (listIdx + 1 < numLists) {
            if (ltRange != null) recurse(listIdx + 1, ltRange, bestRange, subLists);
            if (rtRange != null) recurse(listIdx + 1, rtRange, bestRange, subLists);
        } else {
            // Recursion complete. Is either lt or rt range better than
            // current best?
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
                Arrays.asList(0, 9, 12, 23),
                Arrays.asList(5, 18, 22, 30)));
        Range range = new RangeFinder(ll).find();
    }
    
}
// vim:ts=4:sw=4:et:tw=78
