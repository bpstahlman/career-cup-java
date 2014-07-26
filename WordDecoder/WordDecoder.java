/*
1 represent A, 2 rep B etc and 26 rep Z. Given a number, find number of
possible decoding for this number. No need to consider number starts with zero.
Eg: input – 1234, output – 3(ABCD, AWD, LCD)
*/
// Note: Submitted as first answer on CareerCup.com on 24Jul2014.

import java.util.*;

class Decoder {

    static final char[] CHARY;
    static {
        CHARY = ((String)"ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
    }
    private String numStr;
    Decoder(String numStr) {
        this.numStr = numStr;
    }
    void printWords() {
        recurse("", numStr);
    }
    void recurse(String ltrStr, String numStr) {
        int len = numStr.length();
        if (len == 0 && ltrStr.length() > 0) {
            System.out.println(ltrStr);
            return;
        } else if (len > 2) {
            len = 2;
        }
        // Try valid 1 and 2 digit encodings.
        for (int i = 1; i <= len; i++) {
            int n = Integer.parseInt(numStr.substring(0, i));
            if (n >= 1 && n <= 26)
                recurse(ltrStr + CHARY[n - 1], numStr.substring(i));
        }
    }
}
public class WordDecoder {
    // Usage: java WordDecoder 1234
    // Output:
    //   ABCD
    //   AWD
    //   LCD
    public static void main(String[] args) {
        try {
            // Validate input.
            int n = Integer.parseInt(args[0]);
            if (n <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Error: " + args[0] + " is not a positive, base-10 numeric string.");
            return;
        }
        Decoder decoder = new Decoder(args[0]);
        decoder.printWords();
    }
}
// vim:ts=4:sw=4:et:tw=78
