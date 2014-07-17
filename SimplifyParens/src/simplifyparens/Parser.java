/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simplifyparens;

import java.util.*;
import static simplifyparens.Lexer.Token;

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
    private boolean isP;
    private int prec = 0;
    Term(boolean isP, IElement... els) {
        this.isP = isP;
        for (IElement el : els)
            add(el);
    }
    public boolean getIsP() { return isP; }
    public void setIsP(boolean isP) {
        this.isP = isP;
    }
    public void add(IElement el) {
        if (el instanceof Op && el.getPrec() > 0)
            prec = 1;
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

/*
*/

/**
 *
 * @author stahlmanb
 */
public class Parser {
    Lexer tokener;
    public static int getOpPrec(String op) {
        return op.equals("*") || op.equals("/") ? 1 : 0;
    }
    //StackFrame state = new StackFrame(State.EXPECT_TERM, 0, new Term());
    Parser(Lexer tokener) {
        this.tokener = tokener;
        Term term = parse(0);
        System.out.println(term);
    }
    public Term parse(int level) {
        Lexer.Token tok = null, nextTok = null;
        Term term = new Term(level > 0);
        State state = State.EXPECT_TERM;
        int prevPrec = 0, nextPrec = 0;
        tok = tokener.getTok();
        while (tok != null) {
            switch (tok.getType()) {
                case VAR:
                    if (state == State.EXPECT_TERM) {
                        term.add(new Var(tok.value));
                        state = State.EXPECT_OP;
                    } else {
                        throw new RuntimeException("Unexpected variable: " + tok.value);
                    }
                    break;
                case OP:
                    if (state == State.EXPECT_OP) {
                        term.add(new Op(tok.value.charAt(0)));
                        state = State.EXPECT_TERM;
                        prevPrec = getOpPrec(tok.value);
                    } else {
                        throw new RuntimeException("Unexpected operator: " + tok.value);
                    }
                    break;
                case LP:
                    if (state == State.EXPECT_TERM) {
                        // Recurse
                        Term subTerm = parse(level + 1);
                        if (subTerm.getIsP()) {
                            term.add(subTerm);
                        } else {
                            // Splice the sub-term with redundant parens into current term.
                            term.splice(subTerm);
                        }
                        prevPrec = 0;
                    } else {
                        throw new RuntimeException("Unexpected `('");
                    }
                case RP:
                    if (state == State.EXPECT_OP) {
                        nextTok = tokener.peekTok();
                        if (nextTok != null) {
                            if (nextTok.getType() == Lexer.TokenType.OP) {
                                nextPrec = getOpPrec(nextTok.value);
                            } else if (nextTok.type == Lexer.TokenType.RP || nextTok.type == Lexer.TokenType.EOF) {
                                nextPrec = 0;
                            } else {
                                throw new RuntimeException("Unexpected token: " + nextTok.value);
                            }
                            if (prevPrec < term.getPrec() && term.getPrec() > nextPrec) {
                                // Redundant parens! Convert to regular Term
                                term.setIsP(false);
                            }
                            return term;
                        }
                    } else {
                        throw new RuntimeException("Unexpected `)'");
                    }
                    break;
                case EOF:
                    // Ok to end here?
                    if (state == State.EXPECT_TERM) {
                        throw new RuntimeException("Unexpected End-of-input");
                    }
            }
            // Advance
            tok = tokener.getTok();
        }
        return term;
    }
}

// vim:ts=4:sw=4:et:tw=120
