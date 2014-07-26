// Find the longest sequence of prefix shared by all the words in a string. 
// "abcdef abcdxxx abcdabcdef abcyy" => "abc"

public class LongestWordPrefix {
    static String findLongestPrefix(String[] words) {
        for (String s : words) {
            System.out.println(s);
        }
        String ret = "";
        if (words.length > 0) {
            int i;
            prefix_loop:
            for (i = 0; ; i++) {
                Character pc = null, c = null;
                for (String s : words) {
                    c = s.charAt(i);
                    if (i >= s.length() || (pc != null && c != pc))
                        break prefix_loop;
                    pc = c;
                }
            }
            if (i > 0)
                ret = words[0].substring(0, i);
        }
        return ret;
    }
    public static void main(String[] args) {
        String para = String.join(" ", args);
        String[] words = para.split("\\s+");
        String prefix = findLongestPrefix(words);
        System.out.println("Longest prefix: " + prefix);
    }

}
// vim:ts=4:sw=4:et:tw=78
