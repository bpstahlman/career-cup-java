import java.util.*;

/**
 *
 * @author Brett Stahlman
 */
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
class StackFrame {
    State state;
    int prec;
    ITerm term;
}

public class Parser {
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

// vim:ts=4:sw=4:et:tw=120
