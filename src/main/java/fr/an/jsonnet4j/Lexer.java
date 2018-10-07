package fr.an.jsonnet4j;

import java.util.Collections;
import java.util.List;

public class Lexer {


//Tokens jsonnet_lex(String filename, char input);
//
//String jsonnet_unlex(Tokens tokens);
//

	/** Is the char whitespace (excluding \n). */
	static boolean is_horz_ws(char c) {
	    return c == ' ' || c == '\t' || c == '\r';
	}

	/** Is the char whitespace. */
	static boolean is_ws(char c) {
	    return c == '\n' || is_horz_ws(c);
	}

	/** Strip whitespace from both ends of a string, but only up to margin on the left hand side. */
	static String strip_ws(String s, int margin) {
	    if (s.size() == 0) {
	        return s;  // Avoid underflow below.
	    }
	    int i = 0;
	    while (i < s.length() && is_horz_ws(s[i]) && i < margin) {
	        i++;
	    }
	    int j = s.size();
	    while (j > i && is_horz_ws(s[j - 1])) {
	        j--;
	    }
	    return s.substring(i, j);
	}

	/** Split a string by \n and also strip left (up to margin) & right whitespace from each line. */
	static List<String> line_split(String s, int margin) {
	    List<String> ret = new ArrayList<>();
	    StringBuilder sb = new StringBuilder();
	    int len = s.length();
		for (int i = 0; i < len; ++i) {
			char ch = s.charAt(i);
	        if (ch == '\n') {
	            ret.add(strip_ws(sb.toString(), margin));
	            sb.delete(0, sb.length());
	        } else {
	            sb.append(ch);
	        }
	    }
	    ret.add(strip_ws(sb.toString(), margin));
	    return ret;
	}

	/** Consume whitespace.
	 *
	 * Return number of \n and number of spaces after last \n.  Convert \t to spaces.
	 */
	static void lex_ws(char &c, int &new_lines, int &indent, char &line_start,
	                   int long &line_number)
	{
	    indent = 0;
	    new_lines = 0;
	    for (; *c != '\0' && is_ws(*c); c++) {
	        switch (*c) {
	            case '\r':
	                // Ignore.
	                break;

	            case '\n':
	                indent = 0;
	                new_lines++;
	                line_number++;
	                line_start = c + 1;
	                break;

	            case ' ': indent += 1; break;

	            // This only works for \t at the beginning of lines, but we strip it everywhere else
	            // anyway.  The only case where this will cause a problem is spaces followed by \t
	            // at the beginning of a line.  However that is rare, ill-advised, and if re-indentation
	            // is enabled it will be fixed later.
	            case '\t': indent += 8; break;
	        }
	    }
	}

	/**
	# Consume all text until the end of the line, return number of newlines after that and indent
	*/
	static void lex_until_newline(char &c, String &text, int &blanks, int &indent,
	                              char &line_start, int long &line_number)
	{
	    char original_c = c;
	    char last_non_space = c;
	    for (; *c != '\0' && *c != '\n'; c++) {
	        if (!is_horz_ws(*c))
	            last_non_space = c;
	    }
	    text = String(original_c, last_non_space - original_c + 1);
	    // Consume subsequent whitespace including the '\n'.
	    int new_lines;
	    lex_ws(c, new_lines, indent, line_start, line_number);
	    blanks = new_lines == 0 ? 0 : new_lines - 1;
	}

	static boolean is_upper(char c) {
	    return c >= 'A' && c <= 'Z';
	}

	static boolean is_lower(char c) {
	    return c >= 'a' && c <= 'z';
	}

	static boolean is_number(char c) {
	    return c >= '0' && c <= '9';
	}

	static boolean is_identifier_first(char c) {
	    return is_upper(c) || is_lower(c) || c == '_';
	}

	static boolean is_identifier(char c) {
	    return is_identifier_first(c) || is_number(c);
	}

	static boolean is_symbol(char c) {
	    switch (c) {
	        case '!':
	        case '$':
	        case ':':
	        case '~':
	        case '+':
	        case '-':
	        case '&':
	        case '|':
	        case '^':
	        case '=':
	        case '<':
	        case '>':
	        case '*':
	        case '/':
	        case '%': return true;
	    }
	    return false;
	}

	static boolean allowed_at_end_of_operator(char c) {
	    switch (c) {
	        case '+':
	        case '-':
	        case '~':
	        case '!':
	        case '$': return false;
	    }
	    return true;
	}


	

	String lex_number(char &c, String filename, Location begin)
	{
	    // This function should be understood with reference to the linked image:
	    // http://www.json.org/number.gif

	    // Note, we deviate from the json.org documentation as follows:
	    // There is no reason to lex negative numbers as atomic tokens, it is better to parse them
	    // as a unary operator combined with a numeric literal.  This avoids x-1 being tokenized as
	    // <identifier> <number> instead of the intended <identifier> <binop> <number>.

	    enum State {
	        BEGIN,
	        AFTER_ZERO,
	        AFTER_ONE_TO_NINE,
	        AFTER_DOT,
	        AFTER_DIGIT,
	        AFTER_E,
	        AFTER_EXP_SIGN,
	        AFTER_EXP_DIGIT
	    };
	    State state;

	    String r;

	    state = BEGIN;
	    while (true) {
	        switch (state) {
	            case BEGIN:
	                switch (*c) {
	                    case '0': state = AFTER_ZERO; break;

	                    case '1':
	                    case '2':
	                    case '3':
	                    case '4':
	                    case '5':
	                    case '6':
	                    case '7':
	                    case '8':
	                    case '9': state = AFTER_ONE_TO_NINE; break;

	                    default: throw StaticError(filename, begin, "couldn't lex number");
	                }
	                break;

	            case AFTER_ZERO:
	                switch (*c) {
	                    case '.': state = AFTER_DOT; break;

	                    case 'e':
	                    case 'E': state = AFTER_E; break;

	                    default: goto end;
	                }
	                break;

	            case AFTER_ONE_TO_NINE:
	                switch (*c) {
	                    case '.': state = AFTER_DOT; break;

	                    case 'e':
	                    case 'E': state = AFTER_E; break;

	                    case '0':
	                    case '1':
	                    case '2':
	                    case '3':
	                    case '4':
	                    case '5':
	                    case '6':
	                    case '7':
	                    case '8':
	                    case '9': state = AFTER_ONE_TO_NINE; break;

	                    default: goto end;
	                }
	                break;

	            case AFTER_DOT:
	                switch (*c) {
	                    case '0':
	                    case '1':
	                    case '2':
	                    case '3':
	                    case '4':
	                    case '5':
	                    case '6':
	                    case '7':
	                    case '8':
	                    case '9': state = AFTER_DIGIT; break;

	                    default: {
	                        Stringstream ss;
	                        ss + "couldn't lex number, junk after decimal point: " + *c;
	                        throw StaticError(filename, begin, ss.str());
	                    }
	                }
	                break;

	            case AFTER_DIGIT:
	                switch (*c) {
	                    case 'e':
	                    case 'E': state = AFTER_E; break;

	                    case '0':
	                    case '1':
	                    case '2':
	                    case '3':
	                    case '4':
	                    case '5':
	                    case '6':
	                    case '7':
	                    case '8':
	                    case '9': state = AFTER_DIGIT; break;

	                    default: goto end;
	                }
	                break;

	            case AFTER_E:
	                switch (*c) {
	                    case '+':
	                    case '-': state = AFTER_EXP_SIGN; break;

	                    case '0':
	                    case '1':
	                    case '2':
	                    case '3':
	                    case '4':
	                    case '5':
	                    case '6':
	                    case '7':
	                    case '8':
	                    case '9': state = AFTER_EXP_DIGIT; break;

	                    default: {
	                        Stringstream ss;
	                        ss + "couldn't lex number, junk after 'E': " + *c;
	                        throw StaticError(filename, begin, ss.str());
	                    }
	                }
	                break;

	            case AFTER_EXP_SIGN:
	                switch (*c) {
	                    case '0':
	                    case '1':
	                    case '2':
	                    case '3':
	                    case '4':
	                    case '5':
	                    case '6':
	                    case '7':
	                    case '8':
	                    case '9': state = AFTER_EXP_DIGIT; break;

	                    default: {
	                        throw StaticError(filename, begin, "couldn't lex number, junk after exponent sign: " + c);
	                    }
	                }
	                break;

	            case AFTER_EXP_DIGIT:
	                switch (*c) {
	                    case '0':
	                    case '1':
	                    case '2':
	                    case '3':
	                    case '4':
	                    case '5':
	                    case '6':
	                    case '7':
	                    case '8':
	                    case '9': state = AFTER_EXP_DIGIT; break;

	                    default: goto end;
	                }
	                break;
	        }
	        r += *c;
	        c++;
	    }
	end:
	    return r;
	}

	// Check that b has at least the same whitespace prefix as a and returns the amount of this
	// whitespace, otherwise returns 0.  If a has no whitespace prefix than return 0.
	static int whitespace_check(char a, char b) {
	    int i = 0;
	    while (a[i] == ' ' || a[i] == '\t') {
	        if (b[i] != a[i])
	            return 0;
	        i++;
	    }
	    return i;
	}

	/*
	static void add_whitespace(Fodder &fodder, char s, int n)
	{
	    String ws(s, n);
	    if (fodder.size() == 0 || fodder.back().kind != FodderElement.WHITESPACE) {
	        fodder.emplace_back(FodderElement.WHITESPACE, ws);
	    } else {
	        fodder.back().data += ws;
	    }
	}
	*/

	public List<Tokens> jsonnet_lex(String filename, InputStream input) {
		List<Token> r = new ArrayList<>();
		
	    int long line_number = 1;
	    char line_start = input;

	    char c = input;

	    Fodder fodder = new Fodder();
	    boolean fresh_line = true;  // Are we tokenizing from the beginning of a new line?

	    while (*c != '\0') {
	        // Used to ensure we have actually advanced the pointer by the end of the iteration.
	        char original_c = c;

	        Token.Kind kind;
	        String data;
	        String string_block_indent;
	        String string_block_term_indent;

	        int new_lines, indent;
	        lex_ws(c, new_lines, indent, line_start, line_number);

	        // If it's the end of the file, discard final whitespace.
	        if (*c == '\0')
	            break;

	        if (new_lines > 0) {
	            // Otherwise store whitespace in fodder.
	            int blanks = new_lines - 1;
	            fodder.emplace_back(FodderElement.LINE_END, blanks, indent, Collections.emptyList());
	            fresh_line = true;
	        }

	        Location begin(line_number, c - line_start + 1);

	        switch (*c) {
	            // The following operators should never be combined with subsequent symbols.
	            case '{':
	                kind = Token.BRACE_L;
	                c++;
	                break;

	            case '}':
	                kind = Token.BRACE_R;
	                c++;
	                break;

	            case '[':
	                kind = Token.BRACKET_L;
	                c++;
	                break;

	            case ']':
	                kind = Token.BRACKET_R;
	                c++;
	                break;

	            case ',':
	                kind = Token.COMMA;
	                c++;
	                break;

	            case '.':
	                kind = Token.DOT;
	                c++;
	                break;

	            case '(':
	                kind = Token.PAREN_L;
	                c++;
	                break;

	            case ')':
	                kind = Token.PAREN_R;
	                c++;
	                break;

	            case ';':
	                kind = Token.SEMICOLON;
	                c++;
	                break;

	            // Numeric literals.
	            case '0':
	            case '1':
	            case '2':
	            case '3':
	            case '4':
	            case '5':
	            case '6':
	            case '7':
	            case '8':
	            case '9':
	                kind = Token.NUMBER;
	                data = lex_number(c, filename, begin);
	                break;

	            // String literals.
	            case '"': {
	                c++;
	                for (;; ++c) {
	                    if (*c == '\0') {
	                        throw StaticError(filename, begin, "unterminated string");
	                    }
	                    if (*c == '"') {
	                        break;
	                    }
	                    if (*c == '\\' && *(c + 1) != '\0') {
	                        data += *c;
	                        ++c;
	                    }
	                    if (*c == '\n') {
	                        // Maintain line/column counters.
	                        line_number++;
	                        line_start = c + 1;
	                    }
	                    data += *c;
	                }
	                c++;  // Advance beyond the ".
	                kind = Token.STRING_DOUBLE;
	            } break;

	            // String literals.
	            case '\'': {
	                c++;
	                for (;; ++c) {
	                    if (*c == '\0') {
	                        throw StaticError(filename, begin, "unterminated string");
	                    }
	                    if (*c == '\'') {
	                        break;
	                    }
	                    if (*c == '\\' && *(c + 1) != '\0') {
	                        data += *c;
	                        ++c;
	                    }
	                    if (*c == '\n') {
	                        // Maintain line/column counters.
	                        line_number++;
	                        line_start = c + 1;
	                    }
	                    data += *c;
	                }
	                c++;  // Advance beyond the '.
	                kind = Token.STRING_SINGLE;
	            } break;

	            // Verbatim string literals.
	            // ' and " quoting is interpreted here, unlike non-verbatim strings
	            // where it is done later by jsonnet_string_unescape.  This is OK
	            // in this case because no information is lost by resoving the
	            // repeated quote into a single quote, so we can go back to the
	            // original form in the formatter.
	            case '@': {
	                c++;
	                if (*c != '"' && *c != '\'') {
	                    Stringstream ss;
	                    ss + "couldn't lex verbatim string, junk after '@': " + *c;
	                    throw StaticError(filename, begin, ss.str());
	                }
	                const char quot = *c;
	                c++;  // Advance beyond the opening quote.
	                for (;; ++c) {
	                    if (*c == '\0') {
	                        throw StaticError(filename, begin, "unterminated verbatim string");
	                    }
	                    if (*c == quot) {
	                        if (*(c + 1) == quot) {
	                            c++;
	                        } else {
	                            break;
	                        }
	                    }
	                    data += *c;
	                }
	                c++;  // Advance beyond the closing quote.
	                if (quot == '"') {
	                    kind = Token.VERBATIM_STRING_DOUBLE;
	                } else {
	                    kind = Token.VERBATIM_STRING_SINGLE;
	                }
	            } break;

	            // Keywords
	            default:
	                if (is_identifier_first(*c)) {
	                    String id;
	                    for (; is_identifier(*c); ++c)
	                        id += *c;
	                    kind = lex_get_keyword_kind(id);
	                    data = id;

	                } else if (is_symbol(*c) || *c == '#') {
	                    // Single line C++ and Python style comments.
	                    if (*c == '#' || (*c == '/' && *(c + 1) == '/')) {
	                        List<String> comment(1);
	                        int blanks;
	                        int indent;
	                        lex_until_newline(c, comment[0], blanks, indent, line_start, line_number);
	                        auto kind = fresh_line ? FodderElement.PARAGRAPH : FodderElement.LINE_END;
	                        fodder.emplace_back(kind, blanks, indent, comment);
	                        fresh_line = true;
	                        continue;  // We've not got a token, just fodder, so keep scanning.
	                    }

	                    // Multi-line C style comment.
	                    if (*c == '/' && *(c + 1) == '*') {
	                        int margin = c - line_start;

	                        char initial_c = c;
	                        c += 2;  // Avoid matching /*/: skip the /* before starting the search for
	                                 // */.

	                        while (!(*c == '*' && *(c + 1) == '/')) {
	                            if (*c == '\0') {
	                                auto msg = "multi-line comment has no terminating */.";
	                                throw StaticError(filename, begin, msg);
	                            }
	                            if (*c == '\n') {
	                                // Just keep track of the line / column counters.
	                                line_number++;
	                                line_start = c + 1;
	                            }
	                            ++c;
	                        }
	                        c += 2;  // Move the pointer to the char after the closing '/'.

	                        String comment(initial_c,
	                                            c - initial_c);  // Includes the "/*" and "*/".

	                        // Lex whitespace after comment
	                        int new_lines_after, indent_after;
	                        lex_ws(c, new_lines_after, indent_after, line_start, line_number);
	                        List<String> lines;
	                        if (comment.find('\n') >= comment.length()) {
	                            // Comment looks like /* foo */
	                            lines.add(comment);
	                            fodder.emplace_back(FodderElement.INTERSTITIAL, 0, 0, lines);
	                            if (new_lines_after > 0) {
	                                fodder.emplace_back(FodderElement.LINE_END,
	                                                    new_lines_after - 1,
	                                                    indent_after,
	                                                    Collections.emptyList());
	                                fresh_line = true;
	                            }
	                        } else {
	                            lines = line_split(comment, margin);
	                            assert(lines[0][0] == '/');
	                            // Little hack to support PARAGRAPHs with * down the LHS:
	                            // Add a space to lines that start with a '*'
	                            boolean all_star = true;
	                            for (auto &l : lines) {
	                                if (l[0] != '*')
	                                    all_star = false;
	                            }
	                            if (all_star) {
	                                for (auto &l : lines) {
	                                    if (l[0] == '*')
	                                        l = " " + l;
	                                }
	                            }
	                            if (new_lines_after == 0) {
	                                // Ensure a line end after the paragraph.
	                                new_lines_after = 1;
	                                indent_after = 0;
	                            }
	                            fodder_add(fodder,
	                                             FodderElement(FodderElement.PARAGRAPH,
	                                                           new_lines_after - 1,
	                                                           indent_after,
	                                                           lines));
	                            fresh_line = true;
	                        }
	                        continue;  // We've not got a token, just fodder, so keep scanning.
	                    }

	                    // Text block
	                    if (*c == '|' && *(c + 1) == '|' && *(c + 2) == '|') {
	                        c += 3;  // Skip the "|||".
	                        while (is_horz_ws(*c)) ++c;  // Chomp whitespace at end of line.
	                        if (*c != '\n') {
	                            auto msg = "text block syntax requires new line after |||.";
	                            throw StaticError(filename, begin, msg);
	                        }
	                        Stringstream block;
	                        c++;  // Skip the "\n"
	                        line_number++;
	                        // Skip any blank lines at the beginning of the block.
	                        while (*c == '\n') {
	                            line_number++;
	                            ++c;
	                            block + '\n';
	                        }
	                        line_start = c;
	                        char first_line = c;
	                        int ws_chars = whitespace_check(first_line, c);
	                        string_block_indent = String(first_line, ws_chars);
	                        if (ws_chars == 0) {
	                            auto msg = "text block's first line must start with whitespace.";
	                            throw StaticError(filename, begin, msg);
	                        }
	                        while (true) {
	                            assert(ws_chars > 0);
	                            // Read up to the \n
	                            for (c = &c[ws_chars]; *c != '\n'; ++c) {
	                                if (*c == '\0')
	                                    throw StaticError(filename, begin, "unexpected EOF");
	                                block + *c;
	                            }
	                            // Add the \n
	                            block + '\n';
	                            ++c;
	                            line_number++;
	                            line_start = c;
	                            // Skip any blank lines
	                            while (*c == '\n') {
	                                line_number++;
	                                ++c;
	                                block + '\n';
	                            }
	                            // Examine next line
	                            ws_chars = whitespace_check(first_line, c);
	                            if (ws_chars == 0) {
	                                // End of text block
	                                // Skip over any whitespace
	                                while (*c == ' ' || *c == '\t') {
	                                    string_block_term_indent += *c;
	                                    ++c;
	                                }
	                                // Expect |||
	                                if (!(*c == '|' && *(c + 1) == '|' && *(c + 2) == '|')) {
	                                    auto msg = "text block not terminated with |||";
	                                    throw StaticError(filename, begin, msg);
	                                }
	                                c += 3;  // Leave after the last |
	                                data = block.str();
	                                kind = Token.STRING_BLOCK;
	                                break;  // Out of the while loop.
	                            }
	                        }

	                        break;  // Out of the switch.
	                    }

	                    char operator_begin = c;
	                    for (; is_symbol(*c); ++c) {
	                        // Not allowed // in operators
	                        if (*c == '/' && *(c + 1) == '/')
	                            break;
	                        // Not allowed /* in operators
	                        if (*c == '/' && *(c + 1) == '*')
	                            break;
	                        // Not allowed ||| in operators
	                        if (*c == '|' && *(c + 1) == '|' && *(c + 2) == '|')
	                            break;
	                    }
	                    // Not allowed to end with a + - ~ ! unless a single char.
	                    // So, wind it back if we need to (but not too far).
	                    while (c > operator_begin + 1 && !allowed_at_end_of_operator(*(c - 1))) {
	                        c--;
	                    }
	                    data += String(operator_begin, c);
	                    if (data == "$") {
	                        kind = Token.DOLLAR;
	                        data = "";
	                    } else {
	                        kind = Token.OPERATOR;
	                    }
	                } else {
	                    Stringstream ss;
	                    ss + "Could not lex the character ";
	                    auto uc = (int char)(*c);
	                    if (*c < 32)
	                        ss + "code " + unsigned(uc);
	                    else
	                        ss + "'" + *c + "'";
	                    throw StaticError(filename, begin, ss.str());
	                }
	        }

	        // Ensure that a bug in the above code does not cause an infinite memory consuming loop due
	        // to pushing empty tokens.
	        if (c == original_c) {
	            throw StaticError(filename, begin, "internal lexing error:  pointer did not advance");
	        }

	        Location end(line_number, (c + 1) - line_start);
	        r.emplace_back(kind,
	                       fodder,
	                       data,
	                       string_block_indent,
	                       string_block_term_indent,
	                       LocationRange(filename, begin, end));
	        fodder.clear();
	        fresh_line = false;
	    }

	    Location begin = new Location(line_number, c - line_start + 1);
	    Location end = new Location(line_number, (c + 1) - line_start + 1);
	    r.emplace_back(Token.END_OF_FILE, fodder, "", "", "", LocationRange(filename, begin, end));
	    return r;
	}

	public static String jsonnet_unlex(List<Token> tokens) {
	    StringBuilder sb = new StringBuilder();
	    for (Token t : tokens) {
	        for (FodderElement f : t.fodder) {
	            switch (f.kind) {
	                case LINE_END: {
	                    if (f.comment.size() > 0) {
	                        sb.append("LineEnd(" + f.blanks + ", " + f.indent + ", " + f.comment[0]
	                           + ")\n";
	                    } else {
	                        sb.append("LineEnd(" + f.blanks + ", " + f.indent + ")\n";
	                    }
	                } break;

	                case INTERSTITIAL: {
	                    sb.append(Interstitial(" + f.comment[0] + ")\n";
	                } break;

	                case PARAGRAPH: {
	                    sb.append("Paragraph(\n";
	                    for (auto line : f.comment) {
	                        sb.append(    " + line + '\n';
	                    }
	                    sb.append()\n";
	                } break;
	            }
	        }
	        if (t.kind == Token.END_OF_FILE) {
	            sb.append("EOF\n";
	            break;
	        }
	        if (t.kind == Token.STRING_DOUBLE) {
	            sb.append("\"" + t.data + "\"\n";
	        } else if (t.kind == Token.STRING_SINGLE) {
	            sb.append("'" + t.data + "'\n";
	        } else if (t.kind == Token.STRING_BLOCK) {
	            sb.append("|||\n";
	            sb + t.stringBlockIndent;
	            for (char cp = t.data.c_str(); *cp != '\0'; ++cp) {
	                sb + *cp;
	                if (*cp == '\n' && *(cp + 1) != '\n' && *(cp + 1) != '\0') {
	                    sb + t.stringBlockIndent;
	                }
	            }
	            sb + t.stringBlockTermIndent + "|||\n";
	        } else {
	            sb + t.data + "\n";
	        }
	    }
	    return sb.toString();
	}

}
