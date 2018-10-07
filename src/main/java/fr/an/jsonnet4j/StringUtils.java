package fr.an.jsonnet4j;

public final class StringUtils {

	/** Unparse the string. */
	public static String unparse(String str, boolean single) {
	    return "" + ((single) ? '\'' : '\"')
	    	+ escape(str, single)
	    	+ (single ? '\'' : '\"');
	}

	/** Escape special characters. */
	public static String escape(String str, boolean single) {
	    String res = "";
	    int len = str.length();
		for (int i = 0; i < len; ++i) {
	        char c = str.charAt(i);
	        switch (c) {
	            case '\"': res += (single ? "\"" : "\\\""); break;
	            case '\'': res += (single ? "\\\'" : "\'"); break;
	            case '\\': res += "\\\\"; break;
	            case '\b': res += "\\b"; break;
	            case '\f': res += "\\f"; break;
	            case '\n': res += "\\n"; break;
	            case '\r': res += "\\r"; break;
	            case '\t': res += "\\t"; break;
	            case '\0': res += "\\u0000"; break;
	            default: {
	                if (c < 0x20 || (c >= 0x7f && c <= 0x9f)) {
	                    // Unprintable, use \\u
	                	res += "\\u" + String.format("%04X", (int) c);
	                } else {
	                    // Printable, write verbatim
	                    res += c;
	                }
	            }
	        }
	    }
	    return res;
	}

	/** Resolve escape chracters in the string. */
	public static String unescape(LocationRange loc, String s) {
	    String r = "";
	    int len = s.length();
	    for (int i = 0; i < len; i++) {
	    	char c = s.charAt(i);
	        switch (c) {
	            case '\\':
	            	c = s.charAt(++i);
	                switch (c) {
	                    case '"':
	                    case '\'': r += c; break;
	                    case '\\': r += c; break;
	                    case '/': r += c; break;
	                    case 'b': r += '\b'; break;
	                    case 'f': r += '\f'; break;
	                    case 'n': r += '\n'; break;
	                    case 'r': r += '\r'; break;
	                    case 't': r += '\t'; break;
	                    case 'u': {
	                        ++i;  // Consume the 'u'.
	                        long codepoint = 0;
	                        // Expect 4 hex digits.
	                        for (int di = 0; di < 4; di++,i++) {
	                            char x = s.charAt(i+di);
	                            int digit;
	                            if (x == '\0') {
	                                throw new StaticError(loc, "Truncated unicode escape sequence in string literal.");
	                            } else if (x >= '0' && x <= '9') {
	                                digit = x - '0';
	                            } else if (x >= 'a' && x <= 'f') {
	                                digit = x - 'a' + 10;
	                            } else if (x >= 'A' && x <= 'F') {
	                                digit = x - 'A' + 10;
	                            } else {
	                                throw new StaticError(loc, "Malformed unicode escape character, should be hex: '" + x + "'");
	                            }
	                            codepoint *= 16;
	                            codepoint += digit;
	                        }

	                        r += codepoint;
	                    } break;

	                    case '\0': {
	                        throw new StaticError(loc, "Truncated escape sequence in string literal.");
	                    }

	                    default: {
	                        throw new StaticError(loc, "Unknown escape sequence in string literal: '\\" + c + "'");
	                    }
	                }
	                break;

	            default:
	                // Just a regular letter.
	                r += c;
	        }
	    }
	    return r;
	}

}
