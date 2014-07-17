/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simplifyparens;
import static simplifyparens.Lexer.Token;

/**
 *
 * @author stahlmanb
 */
public class SimplifyParens {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Lexer tokener = new Lexer(String.join("", args));

        for (Token tok = tokener.getTok(); tok != null; tok = tokener.getTok())
            System.out.println(tok.type + ": " + tok.value);

        Parser parser = new Parser(tokener);
        parser.parse(0);
    }
        
	
}
