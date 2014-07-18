/**
 *
 * @author Brett Stahlman
 */

import java.util.*;
import java.util.regex.*;

enum TokenType { LP, RP, VAR, OP, EOF }
class Token {
    TokenType type;
    String value;
    // Caveat: The negative lookbehind matching whitespace is needed to avoid extra (empty) fields prior to operators
    // and such. Without it, it appears that the positive lookahead in `\\s*(?=[...])' will match twice at the same
    // location: once with leading whitespace, once without. I believe this is a bug in the engine. Vim handles the
    // equivalent regex without the extra field.
    public static final Pattern reSep = Pattern.compile("(?<!\\s)\\s*(?=[-+*/()])|(?<=[-+*/()])\\s*+|\\s+");
    static final Pattern reVar = Pattern.compile("[a-zA-Z_][a-zA-Z_[0-9]]*");
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
                    throw new RuntimeException("Invalid var: " + s);
                // Create var token.
                type = TokenType.VAR;
        }
        value = s;
    }
    public TokenType getType() { return type; }
    public String getValue() { return value; }
}

class Lexer {
    Deque<Token> fifo = null;
    Lexer(String s) {
        // Create a FIFO to hold the tokens.
        fifo = new LinkedList<Token>();
        tokenize(s);
    }
    // Fill the FIFO with Token's
    public void tokenize(String s) {
        String[] toks = Token.reSep.split(s);
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

interface IElement {
    String toString();
    int getPrec();
}

interface ITerm extends IElement {
    void add(IElement el);
}

class Op implements IElement {
    private char op;
    Op(char op) {
        this.op = op;
    }
    public String toString() {
        return " " + op + " ";
    }
    public char getOp() { return op; }
    public int getPrec() {
        return op == '+' || op == '-' ? 0 : 1;
    }
}

class Var implements IElement {
    String name;
    Var(String name) {
        this.name = name;
    }
    public String toString() { return name; }
    public int getPrec() { return 1; }
}

class Term implements ITerm {
    private List<IElement> els = new ArrayList<IElement>();
    private int prec = 1;
    Term(IElement... els) {
        for (IElement el : els)
            add(el);
    }
    public String toString() {
        String ret = "";
        for (IElement el : els) {
            String elStr = el.toString();
            if (el instanceof Term)
                ret += "(" + elStr + ")";
            else
                ret += elStr;
        }
        return ret;
    }
    public void add(IElement el) {
        if (el instanceof Op && el.getPrec() == 0)
            // Can only decrease.
            prec = 0;
        els.add(el);
    }
    // Sub-term shouldn't have been a subterm; splice into this.
    public void splice(Term subTerm) {
        for (IElement el : subTerm.els) {
            add(el);
        }
    }
    public int getPrec() { return prec; }
}

enum State { EXPECT_OP, EXPECT_TERM }

class Parser {
    Lexer tokener;
    Term term;
    public static int getOpPrec(String op) {
        return op.equals("*") || op.equals("/") ? 1 : 0;
    }
    Parser(Lexer tokener) {
        this.tokener = tokener;
        term = parse(0);
    }
    public String toString() {
        return term.toString();
    }
    public Term parse(int level) {
        Token tok = null, nextTok = null;
        Term term = new Term();
        State state = State.EXPECT_TERM;
        int prevPrec = 0, nextPrec = 0;
        tok = tokener.getTok();
        term_loop: while (tok != null) {
            switch (tok.getType()) {
                case VAR:
                    if (state == State.EXPECT_TERM) {
                        term.add(new Var(tok.getValue()));
                        state = State.EXPECT_OP;
                    } else {
                        throw new RuntimeException("Unexpected variable: " + tok.getValue());
                    }
                    break;
                case OP:
                    if (state == State.EXPECT_OP) {
                        term.add(new Op(tok.getValue().charAt(0)));
                        state = State.EXPECT_TERM;
                        prevPrec = getOpPrec(tok.getValue());
                    } else {
                        throw new RuntimeException("Unexpected operator: " + tok.getValue());
                    }
                    break;
                case LP:
                    if (state == State.EXPECT_TERM) {
                        // Recurse to get sub-term.
                        Term subTerm = parse(level + 1);
                        // See whether sub-term needed to be a sub-term.
                        // TODO: Currently, existence of EOF precludes the following if being entered.
                        if ((nextTok = tokener.peekTok()) != null) {
                            if (nextTok.getType() == TokenType.OP)
                                nextPrec = getOpPrec(nextTok.getValue());
                            else if (nextTok.getType() == TokenType.RP || nextTok.getType() == TokenType.EOF)
                                nextPrec = 0;
                            else
                                throw new RuntimeException("Unexpected token: " + nextTok.getValue());
                            if (prevPrec <= subTerm.getPrec() && subTerm.getPrec() >= nextPrec)
                                // Redundant parens! Splice the sub-term with redundant parens into current term.
                                term.splice(subTerm);
                            else
                                // Add sub-term as a sub-term.
                                term.add(subTerm);
                        }
                        state = State.EXPECT_OP;
                    } else {
                        throw new RuntimeException("Unexpected `('");
                    }
                    break;
                case RP:
                    if (state == State.EXPECT_OP && level > 0)
                        return term;
                    else
                        throw new RuntimeException("Unexpected `)'");
                case EOF:
                    break term_loop;
                    
            }
            // Advance...
            tok = tokener.getTok();
        }
        // Ok to end here?
        if (state == State.EXPECT_TERM || level > 0)
            throw new RuntimeException("Unexpected End-of-input");
        return term;
    }
}

public class SimplifyParens {

    /**
     * @param args the command line arguments
     */
    // Usage: java -jar SimplifyParens "(a + b * (c / d - e * (f + g)) - h * (i))"
    // Usage: java SimplifyParens "(a * (b / c) - d * ((e * f) / g) * (h - i * (j - k) - l))"
    // Output:
    // Original expression:   (a * (b / c) - d * ((e * f) / g) * (h - i * (j - k) - l))
    // Simplified expression: a * b / c - d * e * f / g * (h - i * (j - k) - l)
    public static void main(String[] args) {
        String sexp = String.join("", args);
        Lexer tokener = new Lexer(sexp);
        Parser parser = new Parser(tokener);
        System.out.println("Original expression:   " + sexp);
        System.out.println("Simplified expression: " + parser.toString());

    }
}

// vim:ts=4:sw=4:et:tw=120
