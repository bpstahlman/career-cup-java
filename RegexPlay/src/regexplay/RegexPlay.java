/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package regexplay;
import java.util.regex.*;

/**
 *
 * @author stahlmanb
 */
public class RegexPlay {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Pattern reSep = Pattern.compile("(?<!\\s)\\s*(?=[-+*/()])|(?<=[-+*/()])\\s*+|\\s+");
        String s = "a + b";
        Matcher m = reSep.matcher(s);
        System.out.println(s);
        while (m.find()) {
            System.out.println(m);
        }
    }
    
}
