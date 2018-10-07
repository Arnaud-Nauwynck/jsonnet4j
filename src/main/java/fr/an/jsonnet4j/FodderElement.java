package fr.an.jsonnet4j;

import java.io.PrintStream;
import java.util.List;

/** Whitespace and comments.
 *
 * "Fodder" (as in cannon fodder) implies this data is expendable.
 */
public class FodderElement {
    public static enum Kind {
        /** The next token, paragraph, or interstitial should be on a new line.
         *
         * A single comment string is allowed, which flows before the new line.
         *
         * The LINE_END fodder specifies the indentation level and vertical spacing before whatever
         * comes next.
         */
        LINE_END,

        /** A C-style comment that begins and ends on the same line.
         *
         * If it follows a token (i.e., it is the first fodder element) then it appears after the
         * token on the same line.  If it follows another interstitial, it will also flow after it
         * on the same line.  If it follows a new line or a paragraph, it is the first thing on the
         * following line, after the blank lines and indentation specified by the previous fodder.
         *
         * There is exactly one comment string.
         */
        INTERSTITIAL,

        /** A comment consisting of at least one line.
         *
         * // and # style commes have exactly one line.  C-style comments can have more than one
         * line.
         *
         * All lines of the comment are indented according to the indentation level of the previous
         * new line / paragraph fodder.
         *
         * The PARAGRAPH fodder specifies the indentation level and vertical spacing before whatever
         * comes next.
         */
        PARAGRAPH,
    };
    Kind kind;

    /** How many blank lines (vertical space) before the next fodder / token. */
    int blanks;

    /** How far the next fodder / token should be indented. */
    int indent;

    /** Whatever comments are part of this fodder.
     *
     * Constraints apply.  See Kind, above.
     *
     * The strings include any delimiting characters, e.g. // # and C-style comment delimiters but
     * not newline characters or indentation.
     */
    List<String> comment;

    
    public FodderElement(Kind kind, int blanks, int indent, List<String> comment) {
		this.kind = kind;
		this.blanks = blanks;
		this.indent = indent;
		this.comment = comment;

        assert(kind != Kind.LINE_END || comment.size() <= 1);
        assert(kind != Kind.INTERSTITIAL || (blanks == 0 && indent == 0 && comment.size() == 1));
        assert(kind != Kind.PARAGRAPH || comment.size() >= 1);
    }

	public void print(PrintStream out) {
		switch (kind) {
		case LINE_END:
			out.print("END(" + blanks + ", " + indent);
			if (!comment.isEmpty()) {
				out.print(", " + comment.get(0));
			}
			out.print(")");
			break;
		case INTERSTITIAL:
			out.print("INT(" + blanks + ", " + indent + ", " + comment.get(0) + ")");
			break;
		case PARAGRAPH:
			out.print("PAR(" + blanks + ", " + indent + ", " + comment.get(0) + "...)");
			break;
		}
	}

}