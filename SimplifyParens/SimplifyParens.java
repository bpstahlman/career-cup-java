/**
 *
 * @author Brett Stahlman
 */
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
