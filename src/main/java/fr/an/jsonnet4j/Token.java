package fr.an.jsonnet4j;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Token {

    public static enum Kind {
        // Symbols
        BRACE_L("\"{\""),
        BRACE_R("\"}\""),
        BRACKET_L("\"[\""),
        BRACKET_R("\"]\""),
        COMMA("\",\""),
        DOLLAR("\"$\""),
        DOT("\".\""),
        PAREN_L("\"(\""),
        PAREN_R("\")\""),
        SEMICOLON("\";\""),

        // Arbitrary length lexemes
        IDENTIFIER("IDENTIFIER"),
        NUMBER("NUMBER"),
        OPERATOR("OPERATOR"),
        STRING_DOUBLE("STRING_DOUBLE"),
        STRING_SINGLE("STRING_SINGLE"),
        STRING_BLOCK("STRING_BLOCK"),
        VERBATIM_STRING_SINGLE("VERBATIM_STRING_SINGLE"),
        VERBATIM_STRING_DOUBLE("VERBATIM_STRING_DOUBLE"),

        // Keywords
        ASSERT("assert"),
        ELSE("else"),
        ERROR("error"),
        FALSE("false"),
        FOR("for"),
        FUNCTION("function"),
        IF("if"),
        IMPORT("import"),
        IMPORTSTR("importstr"),
        IN("in"),
        LOCAL("local"),
        NULL_LIT("null"),
        TAILSTRICT("tailstrict"),
        THEN("then"),
        SELF("self"),
        SUPER("super"),
        TRUE("true"),

        // A special token that holds line/column information about the end of the file.
        END_OF_FILE("end of file");
        
    	public final String text;
    	
        private Kind(String text) {
			this.text = text;
		}

		public String toString() {
			return text;
        }        

		public void print(PrintStream out) {
		    out.print(text);
		}

		public static final Map<String, Kind> keywordsMap = buildKeywordsMap();
		
		private static Map<String, Kind> buildKeywordsMap() {
			Map<String, Kind> res = new LinkedHashMap<>();
			for(Kind k : new Kind[]{ ASSERT, ELSE, ERROR, FALSE, FOR, FUNCTION, IF, IMPORT, IMPORTSTR, IN, LOCAL, 
					NULL_LIT, SELF, SUPER, TAILSTRICT, THEN, TRUE}) {
				res.put(k.text, k);	
			}
			return Collections.unmodifiableMap(res);
		}

		public static Kind getKeywordKind(String identifier) {
		    Kind res = keywordsMap.get(identifier);
		    return (res == null)? IDENTIFIER : res;
		}
		
    }
    
    Kind kind;

    /** Fodder before this token. */
    Fodder fodder;

    /** Content of the token if it wasn't a keyword. */
    String data;

    /** If kind == STRING_BLOCK then stores the sequence of whitespace that indented the block. */
    String stringBlockIndent;

    /** If kind == STRING_BLOCK then stores the sequence of whitespace that indented the end of
     * the block.
     *
     * This is always fewer whitespace characters than in stringBlockIndent.
     */
    String stringBlockTermIndent;

    LocationRange location;
    
    public Token(Kind kind, Fodder fodder, String data, String stringBlockIndent, String stringBlockTermIndent,
			LocationRange location) {
		this.kind = kind;
		this.fodder = fodder;
		this.data = data;
		this.stringBlockIndent = stringBlockIndent;
		this.stringBlockTermIndent = stringBlockTermIndent;
		this.location = location;
	}

    public Token(Kind kind, String data) {
		this.kind = kind;
		this.data = data;
	}

    public Token(Kind kind) {
		this(kind, "");
	}

    public String data32() {
        // return StringUtils.decode_utf8(data);
    	return data; // TODO ?
    }

	
	public boolean equals(Token b) {
	    if (kind != b.kind)
	        return false;
	    if (data != b.data)
	        return false;
	    return true;
	}
	
	public void print(PrintStream out) {
	    if (data == "") {
	        out.print(kind.toString());
	    } else if (kind == Kind.OPERATOR) {
	    	out.print("\"" + data + "\"");
	    } else {
	    	out.print("(" + kind.toString() + ", \"" + data + "\")");
	    }
	}
	
//	/** IF the given identifier is a keyword, return its kind, otherwise return IDENTIFIER. */
//	Token::Kind lex_get_keyword_kind(String identifier);
	

}

/** The result of lexing.
 *
 * Because of the EOF token, this will always contain at least one token.  So element 0 can be used
 * to get the filename.
 */
//typedef List<Token> Tokens;
