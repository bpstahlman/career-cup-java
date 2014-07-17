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
        String sexp = String.join("", args);
        Lexer tokener = new Lexer(sexp);
        Parser parser = new Parser(tokener);
        System.out.println("Original expression:   " + sexp);
        System.out.println("Simplified expression: " + parser.toString());

    }
}
