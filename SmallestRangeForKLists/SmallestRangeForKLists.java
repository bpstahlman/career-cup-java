
/*
You have k lists of sorted integers. Find the smallest range that includes at least one number from each of the k lists. 

For example, 
List 1: [4, 10, 15, 24, 26] 
List 2: [0, 9, 12, 20] 
List 3: [5, 18, 22, 30] 

The smallest range here would be [20, 24] as it contains 24 from list 1, 20 from list 2, and 22 from list 3.
*/

import java.util.*;
import java.util.stream.*;

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
    List<List<Integer>> lists;
    // Maintain a "view" on each of the input lists, which can be used to
    // narrow the region of interest as recursion progresses.
    List<List<Integer>> subLists = new ArrayList<List<Integer>>();
    RangeFinder(List<List<Integer>> ll) {
        this.lists = ll;
        // Initialize the sub-lists to the full lists.
        // Note: Recursive calls will successively narrow.
        for (List<Integer> li : lists) subLists.add(li);
    }
    Range find() {
        // Recurse on all numbers in first list to find best range.
        // Note: Recursion base case updates bestRange if the range it's
        // produced is better.
        Range bestRange = new Range();
        for (int n : subLists.get(0)) {
            recurse(1, new Range(n, n), bestRange);
        }
        return bestRange;
    }
    // This is the heart of the algorithm.
    void recurse(int listIdx, Range range, Range bestRange) {
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
        if (listIdx + 1 < numLists) {
            if (ltRange != null) recurse(listIdx + 1, ltRange, bestRange);
            if (rtRange != null) recurse(listIdx + 1, rtRange, bestRange);
        } else {
            // Recursion complete. Is either lt or rt range better than
            // current best?
            if (ltRange != null) bestRange.replaceMaybe(ltRange);
            if (rtRange != null) bestRange.replaceMaybe(rtRange);
        }
    }
}

public class SmallestRangeForKLists {
    // Usage:
    // java SmallestRangeForKLists "4  10  15  24  26 | 0  9  12  20 | 5  18  22  30"
    // --OR--
    // java SmallestRangeForKLists <num-lists> <nums-per-list> <min> <max>
    public static void main(String[] args) {
        SmallestRangeForKLists ob = new SmallestRangeForKLists();
        List<List<Integer>> lists = null;
        try {
            switch (args.length) {
                case 0:
                    throw new IllegalArgumentException("Arguments required");
                case 1:
                    lists = ob.createLists(args[0]);
                    break;
                case 4:
                    lists = ob.createLists(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid # of arguments.");
            }
        } catch (Exception e) {
            ob.usage(e);
        }
        // Create a RangeFinder and use it find the best range.
        Range range = new RangeFinder(lists).find();

        // Display results...
        System.out.println();
        System.out.println("--Lists--");
        for (List<Integer> li : lists)
            System.out.println(li);
        System.out.println();
        System.out.println("Smallest range: " + range);
    }
    // This overload splits the arg string into lists at `|' chars, with
    // individual numbers separated only by spaces.
    List<List<Integer>> createLists(String s) {
        List<List<Integer>> ll = new ArrayList<List<Integer>>();
        String[] lists = s.trim().split("\\|");
        for (int i = 0; i < lists.length; i++) {
            List<Integer> li = new ArrayList<Integer>();
            String[] nums = lists[i].trim().split("\\s+");
            for (int j = 0; j < nums.length; j++)
                // Conversion may throw NumberFormatException
                li.add(Integer.parseInt(nums[j]));
            // Sort the list in case user didn't...
            Collections.sort(li);
            ll.add(li);
        }
        return ll;
    }
    // This overload creates randomized lists subject to parameters supplied
    // by user.
    List<List<Integer>> createLists(Integer numLists, Integer numsPerList, Integer min, Integer max) {
        List<List<Integer>> ll = new ArrayList<List<Integer>>();
        Random rand = new Random();
        for (int i = 0; i < numLists; i++) {
            List<Integer> li = rand.ints(numsPerList, min, max + 1).sorted().boxed().collect(Collectors.toList());
            ll.add(li);
        }
        return ll;
    }
    void usage(Exception e) {
        if (e != null) {
            System.err.println();
            System.err.println(e);
            System.err.println();
        }
        System.err.println("--Usage--");
        System.err.println("Specify the lists of integers in 1 of the following 2 forms:");
        System.err.println("java SmallestRangeForKLists \"<space-separated-int-list> [ | <space-separated-int-list> [ | ... ] ]\"");
        System.err.println("java SmallestRangeForKLists <num-lists> <nums-per-list> <min> <max>");
        System.err.println();
        System.err.println("--Examples--");
        System.err.println("// Specify lists explicitly.");
        System.err.println("java SmallestRangeForKLists \"4  10  15  24  26 | 0  9  12  20 | 5  18  22  30\"");
        System.err.println("// 7 lists, each containing 10 random ints between 0 and 100 inclusive.");
        System.err.println("java SmallestRangeForKLists 7 10 0 100");
        if (e != null)
            System.exit(1);
    }
}
// vim:ts=4:sw=4:et:tw=78
