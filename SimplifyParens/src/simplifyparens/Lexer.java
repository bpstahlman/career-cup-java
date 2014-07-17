// vim ts=4 sw=4 et tw=120
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simplifyparens;

import java.util.*;
import java.util.regex.*;

/**
 *
 * @author stahlmanb
 */
public class Lexer {
    enum TokenType { LP, RP, VAR, OP, EOF }
    static class Token {
        TokenType type;
        String value;
        Token(String s) {
            if (s == null) {
                type = TokenType.EOF;
                value = null;
                return;
            }
            switch (s) {
                case "(":
                    type = TokenType.LP;
                    break;
                case ")":
                    type = TokenType.RP;
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                    type = TokenType.OP;
                    break;
                default:
                    Matcher m = reVar.matcher(s);
                    if (!m.matches())
                        throw new RuntimeException("Bad var!");
                    // Create var token.
                    type = TokenType.VAR;
            }
            value = s;
        }
        public TokenType getType() { return type; }
        public String getValue() { return value; }
    }
    static Pattern reTok = Pattern.compile("[()]|[a-z]+|[-+*/]");
    // Caveat: The negative lookbehind matching whitespace is needed to avoid extra (empty) fields prior to operators
    // and such. Without it, it appears that the positive lookahead in `\\s*(?=[...])' will match twice at the same
    // location: once with leading whitespace, once without. I believe this is a bug in the engine. Vim handles the
    // equivalent regex without the extra field.
    static Pattern reSep = Pattern.compile("(?<!\\s)\\s*(?=[-+*/()])|(?<=[-+*/()])\\s*+|\\s+");
    static Pattern reVar = Pattern.compile("[a-zA-Z_][a-zA-Z_[0-9]]*");
    Deque<Token> fifo = null;
    Lexer(String s) {
        // Create a FIFO to hold the tokens.
        fifo = new LinkedList<Token>();
        tokenize(s);
    }
    // Fill the FIFO with Token's
    public void tokenize(String s) {
        //Matcher matcher = reTok.matcher(s);
        String[] toks = reSep.split(s);
        for (String tok : toks) {
            fifo.addLast(new Token(tok));
        }
        // Add an EOF. TODO: Is this really necessary?
        fifo.addLast(new Token(null));
    }
    public Token getTok() {
        return fifo.pollFirst();
    }
    public Token peekTok() {
        return fifo.peekFirst();
    }
}

// vim ts=4 sw=4 et tw=120