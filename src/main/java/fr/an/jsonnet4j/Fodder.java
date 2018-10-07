package fr.an.jsonnet4j;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import fr.an.jsonnet4j.FodderElement.Kind;

/** A sequence of fodder elements, typically between two tokens.
 *
 * A LINE_END is not allowed to follow a PARAGRAPH or a LINE_END. This can be represented by
 * replacing the indent of the prior fodder and increasing the number of blank lines if necessary.
 * If there was a comment, it can be represented by changing the LINE_END to a paragraph containing
 * the same single comment string.
 *
 * There must be a LINE_END or a PARAGRAPH before a PARAGRAPH.
 */
public class Fodder {

	private List<FodderElement> elts = new ArrayList<>();
	
	public Fodder() {
	}
	
	public Fodder(List<FodderElement> elts) {
		this.elts.addAll(elts);
	}

	public int size() {
		return elts.size();
	}
	
	public FodderElement get(int i) {
		return elts.get(i);
	}
	
	public boolean isEmpty() {
		return elts.isEmpty();
	}
	
	public FodderElement back() {
		return elts.get(elts.size() - 1);
	}
	
	public void clear() {
		elts.clear();
	}
	
	public boolean has_clean_endline() {
	    return !isEmpty() && back().kind != FodderElement.Kind.INTERSTITIAL;
	}

	public void add(FodderElement e) {
		elts.add(e);
	}
	
	public void add(Kind kind, int blanks, int indent, List<String> comment) {
		add(new FodderElement(kind, blanks, indent, comment));
	}
	
	/** As a.add(elem) but preserves constraints.
	 *
	 * See concat_fodder below.
	 */
	public void fodder_add(FodderElement elem) {
	    if (has_clean_endline() && elem.kind == FodderElement.Kind.LINE_END) {
	        if (elem.comment.size() > 0) {
	            // The line end had a comment, so create a single line paragraph for it.
	            add(FodderElement.Kind.PARAGRAPH, elem.blanks, elem.indent, elem.comment);
	        } else {
	            // Merge it into the previous line end.
	            back().indent = elem.indent;
	            back().blanks += elem.blanks;
	        }
	    } else {
	        if (!has_clean_endline() && elem.kind == FodderElement.Kind.PARAGRAPH) {
	            add(FodderElement.Kind.LINE_END, 0, elem.indent, new ArrayList<String>());
	        }
	        add(elem);
	    }
	}
	
	/** As a + b but preserves constraints.
	 *
	 * Namely, a LINE_END is not allowed to follow a PARAGRAPH or a LINE_END.
	 */
	public static Fodder concat_fodder(Fodder a, Fodder b) {
	    if (a.size() == 0)
	        return b;
	    if (b.size() == 0)
	        return a;
	    Fodder r = a;
	    // Carefully add the first element of b.
	    r.fodder_add(b.get(0));
	    // Add the rest of b.
	    for (int i = 1; i < b.size(); ++i) {
	        r.add(b.get(i));
	    }
	    return r;
	}
	
	/** Move b to the front of a. */
	public static Fodder move_front(Fodder a, Fodder b) {
	    a = concat_fodder(b, a);
	    b.clear();
	    return a;
	}
	
	public static Fodder make_fodder(FodderElement elem)
	{
	    Fodder fodder = new Fodder();
	    fodder.add(elem);
	    return fodder;
	}

	public void ensureCleanNewline() {
	    if (!has_clean_endline()) {
	        fodder_add(new FodderElement(FodderElement.Kind.LINE_END, 0, 0, new ArrayList<>()));
	    }
	}

	static int countNewlines(FodderElement elem) {
	    switch (elem.kind) {
	        case INTERSTITIAL: return 0;
	        case LINE_END: return 1;
	        case PARAGRAPH: return elem.comment.size() + elem.blanks;
	        default: throw new JsonnetException("Unknown FodderElement kind");
	    }
	}

	public int countNewlines() {
	    int sum = 0;
	    for (FodderElement elem : elts) {
	        sum += countNewlines(elem);
	    }
	    return sum;
	}

	public void print(PrintStream out) {
	    boolean first = true;
	    for (FodderElement f : elts) {
	        out.print(first ? "[" : ", ");
	        first = false;
	        out.print(f);
	    }
	    out.print(first ? "[]" : "]");
	}

}
