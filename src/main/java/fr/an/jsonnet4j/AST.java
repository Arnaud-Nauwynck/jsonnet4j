package fr.an.jsonnet4j;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.an.jsonnet4j.AST.ObjectField.Hide;

/** 
 * All AST nodes are subtypes of this class.
 */
public abstract class AST {
	
    protected LocationRange location;
    protected Fodder openFodder;
    
    protected List<Identifier> freeVariables = new ArrayList<>();

    protected AST(LocationRange location, Fodder openFodder) {
    	this.location = location;
    	this.openFodder = openFodder;
    }
	 
    public abstract void accept(ASTVisitor visitor);
    public abstract <TRes> TRes accept(ASTVisitorRes<TRes> visitor);
    
    public abstract ASTType type();
	
	public static String ASTTypeToString(ASTType type) {
	    switch (type) {
	        case AST_APPLY: return "AST_APPLY";
	        case AST_APPLY_BRACE: return "AST_APPLY_BRACE";
	        case AST_ARRAY: return "AST_ARRAY";
	        case AST_ARRAY_COMPREHENSION: return "AST_ARRAY_COMPREHENSION";
	        case AST_ARRAY_COMPREHENSION_SIMPLE: return "AST_ARRAY_COMPREHENSION_SIMPLE";
	        case AST_ASSERT: return "AST_ASSERT";
	        case AST_BINARY: return "AST_BINARY";
	        case AST_BUILTIN_FUNCTION: return "AST_BUILTIN_FUNCTION";
	        case AST_CONDITIONAL: return "AST_CONDITIONAL";
	        case AST_DESUGARED_OBJECT: return "AST_DESUGARED_OBJECT";
	        case AST_DOLLAR: return "AST_DOLLAR";
	        case AST_ERROR: return "AST_ERROR";
	        case AST_FUNCTION: return "AST_FUNCTION";
	        case AST_IMPORT: return "AST_IMPORT";
	        case AST_IMPORTSTR: return "AST_IMPORTSTR";
	        case AST_INDEX: return "AST_INDEX";
	        case AST_IN_SUPER: return "AST_IN_SUPER";
	        case AST_LITERAL_BOOLEAN: return "AST_LITERAL_BOOLEAN";
	        case AST_LITERAL_NULL: return "AST_LITERAL_NULL";
	        case AST_LITERAL_NUMBER: return "AST_LITERAL_NUMBER";
	        case AST_LITERAL_STRING: return "AST_LITERAL_STRING";
	        case AST_LOCAL: return "AST_LOCAL";
	        case AST_OBJECT: return "AST_OBJECT";
	        case AST_OBJECT_COMPREHENSION: return "AST_OBJECT_COMPREHENSION";
	        case AST_OBJECT_COMPREHENSION_SIMPLE: return "AST_OBJECT_COMPREHENSION_SIMPLE";
	        case AST_PARENS: return "AST_PARENS";
	        case AST_SELF: return "AST_SELF";
	        case AST_SUPER_INDEX: return "AST_SUPER_INDEX";
	        case AST_UNARY: return "AST_UNARY";
	        case AST_VAR: return "AST_VAR";
	        default: throw new JsonnetException("Invalid AST type " + type);
	    }
	}
	
	/** Represents a variable / parameter / field name. */
	public static final class Identifier {
	    public final String name;
	    
	    public Identifier(String name) {
	    	this.name = name;
	    }

	    public void print(PrintStream out) {
	    	out.print(name);
	    }

		@Override
		public String toString() {
			return name;
		}
	    
	}
	
	
	/** Either an arg in a function apply, or a param in a closure / other function definition.
	 *
	 * They happen to have exactly the same structure.
	 *
	 * In the case of an arg, the id is optional and the expr is required.  Presence of the id indicates
	 * that this is a named rather than positional argument.
	 *
	 * In the case of a param, the id is required and if expr is given, it is a default argument to be
	 * used when no argument is bound to the param.
	 */
	public static class ArgParam {
	    Fodder idFodder;       // Empty if no id.
	    Identifier id;  // null if there isn't one
	    Fodder eqFodder;       // Empty if no id or no expr.
	    AST expr;             // null if there wasn't one.
	    Fodder commaFodder;    // Before the comma (if there is a comma).

	    // Only has id
	    public ArgParam(Fodder idFodder, Identifier id, Fodder commaFodder) {
			this.idFodder = idFodder;
			this.id = id;
			this.commaFodder = commaFodder;
		}
		// Only has expr
	    public ArgParam(AST expr, Fodder commaFodder) {
	    	this.expr = expr;
	    	this.commaFodder = commaFodder;
	    }
		// Has both id and expr
		public ArgParam(Fodder idFodder, Identifier id, Fodder eqFodder, AST expr, Fodder commaFodder) {
			super();
			this.idFodder = idFodder;
			this.id = id;
			this.eqFodder = eqFodder;
			this.expr = expr;
			this.commaFodder = commaFodder;
		}
		
	}
	
	public static class ArgParams extends ArrayList<ArgParam> {
	}

	/** Used in Object & Array Comprehensions. */
	public static class ComprehensionSpec {
	    public static enum Kind { FOR, IF };
	    Kind kind;
	    Fodder openFodder;
	    Fodder varFodder;       // {} when kind != SPEC_FOR.
	    Identifier var;  // Null when kind != SPEC_FOR.
	    Fodder inFodder;        // {} when kind != SPEC_FOR.
	    AST expr;

	}
	
	/** Represents function calls. */
	public static class Apply extends AST {
	    AST target;
	    Fodder fodderL;
	    ArgParams args;
	    boolean trailingComma;
	    Fodder fodderR;
	    Fodder tailstrictFodder;
	    boolean tailstrict;

		public Apply(LocationRange location, Fodder openFodder,
				AST target, Fodder fodderL, ArgParams args, boolean trailingComma, Fodder fodderR,
				Fodder tailstrictFodder, boolean tailstrict) {
			super(location, openFodder);
			this.target = target;
			this.fodderL = fodderL;
			this.args = args;
			this.trailingComma = trailingComma;
			this.fodderR = fodderR;
			this.tailstrictFodder = tailstrictFodder;
			this.tailstrict = tailstrict;
		}
	 
		@Override
		public ASTType type() { return ASTType.AST_APPLY; }
		
		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}
	}
	
	/** Represents e { }.  Desugared to e + { }. */
	public static class ApplyBrace extends AST {
	    AST left;
	    AST right;  // This is always an object or object comprehension.
	    
		public ApplyBrace(LocationRange location, Fodder openFodder,
				AST left, AST right) {
			super(location, openFodder);
			this.left = left;
			this.right = right;
		}

		@Override
		public ASTType type() { return ASTType.AST_APPLY_BRACE; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}
		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents array constructors [1, 2, 3]. */
	public static class Array extends AST {
	    public static class Element {
	        AST expr;
	        Fodder commaFodder;
			public Element(AST expr, Fodder commaFodder) {
				this.expr = expr;
				this.commaFodder = commaFodder;
			}
	    }
	    // typedef List<Element> Elements;
	    List<Element> elements;
	    boolean trailingComma;
	    Fodder closeFodder;
	    
	    
		public Array(LocationRange location, Fodder openFodder,
				List<Element> elements, boolean trailingComma, Fodder closeFodder) {
			super(location, openFodder);
			this.elements = elements;
			this.trailingComma = trailingComma;
			this.closeFodder = closeFodder;
		}
	    
		@Override
		public ASTType type() { return ASTType.AST_ARRAY; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}
		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents array comprehensions (which are like Python list comprehensions). */
	public static class ArrayComprehension extends AST {
	    AST body;
	    Fodder commaFodder;
	    boolean trailingComma;
	    List<ComprehensionSpec> specs;
	    Fodder closeFodder;
	    
	    public ArrayComprehension(LocationRange location, Fodder openFodder, 
	    		AST body, Fodder commaFodder, boolean trailingComma, List<ComprehensionSpec> specs,
				Fodder closeFodder) {
			super(location, openFodder);
			this.body = body;
			this.commaFodder = commaFodder;
			this.trailingComma = trailingComma;
			this.specs = specs;
			this.closeFodder = closeFodder;

	        assert specs.size() > 0;
	    }

		@Override
		public ASTType type() { return ASTType.AST_ARRAY_COMPREHENSION; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}
		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents an assert expression (not an object-level assert).
	 *
	 * After parsing, message can be null indicating that no message was specified. This AST is
	 * elimiated by desugaring.
	 */
	public static class Assert extends AST {
	    AST cond;
	    Fodder colonFodder;
	    AST message;
	    Fodder semicolonFodder;
	    AST rest;
	    
	    public Assert(LocationRange location, Fodder openFodder,
	    	AST cond, Fodder colonFodder, AST message, Fodder semicolonFodder, AST rest) {
	    	super(location, openFodder);
			this.cond = cond;
			this.colonFodder = colonFodder;
			this.message = message;
			this.semicolonFodder = semicolonFodder;
			this.rest = rest;
		}

		@Override
		public ASTType type() { return ASTType.AST_ASSERT; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}
		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}

	public static enum BinaryOp {
	    BOP_MULT("*", 5),
	    BOP_DIV("/", 5),
	    BOP_PERCENT("%", 5),
	
	    BOP_PLUS("+", 6),
	    BOP_MINUS("-", 6),
	
	    BOP_SHIFT_L("<<", 7),
	    BOP_SHIFT_R(">>", 7),
	
	    BOP_GREATER(">", 8),
	    BOP_GREATER_EQ(">=", 8),
	    BOP_LESS("<", 8),
	    BOP_LESS_EQ("<=", 8),
	    BOP_IN("in", 8),
	
	    BOP_MANIFEST_EQUAL("==", 9),
	    BOP_MANIFEST_UNEQUAL("!=", 9),
	
	    BOP_BITWISE_AND("&", 10),
	    BOP_BITWISE_XOR("^", 11),
	    BOP_BITWISE_OR("|", 12),
	
	    BOP_AND("&&", 13),
	    BOP_OR("||", 14);
	    
		public final String op;
		public final int precedence;
		
		BinaryOp(String op, int precedence) {
			this.op = op;
			this.precedence = precedence;
		}
	    
		// public static String bop_string(BinaryOp bop)
	    public String toString() {
	    	return op;
	    }
	    
		private static Map<String, BinaryOp> mapByText() {
		    Map<String, BinaryOp> r = new LinkedHashMap<>();
		    for(BinaryOp e : BinaryOp.values()) {
		    	r.put(e.op, e);
		    }
		    return Collections.unmodifiableMap(r);
		}

		@Deprecated
		private static Map<BinaryOp, Integer> precedenceMap() {
		    Map<BinaryOp, Integer> r = new LinkedHashMap<>();
		    for(BinaryOp e : BinaryOp.values()) {
		    	r.put(e, e.precedence);
		    }
		    return Collections.unmodifiableMap(r);
		}

	}

	/** Represents binary operators. */
	public static class Binary extends AST {
	    AST left;
	    Fodder opFodder;
	    BinaryOp op;
	    AST right;
	    
	    public Binary(LocationRange location, Fodder openFodder,
	    	AST left, Fodder opFodder, BinaryOp op, AST right) {
			super(location, openFodder);
			this.left = left;
			this.opFodder = opFodder;
			this.op = op;
			this.right = right;
		}

	    @Override
		public ASTType type() { return ASTType.AST_BINARY; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents built-in functions.
	 *
	 * There is no parse rule to build this AST.  Instead, it is used to build the std object in the
	 * interpreter.
	 */
	public static class BuiltinFunction extends AST {
	    String name;
	    List<Identifier> params;
	    
	    public BuiltinFunction(LocationRange location, //Fodder openFodder,
	    		String name, List<Identifier> params) {
	    	super(location, new Fodder());
			this.name = name;
			this.params = params;
		}

	    @Override
		public ASTType type() { return ASTType.AST_BUILTIN_FUNCTION; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}
		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents if then else.
	 *
	 * After parsing, branchFalse can be null indicating that no else branch was specified.  The
	 * desugarer fills this in with a LiteralNull.
	 */
	public static class Conditional extends AST {
	    AST cond;
	    Fodder thenFodder;
	    AST branchTrue;
	    Fodder elseFodder;
	    AST branchFalse;
	    
	    public Conditional(LocationRange location, Fodder openFodder,
	    		AST cond, Fodder thenFodder, AST branchTrue, Fodder elseFodder, AST branchFalse) {
	    	super(location, openFodder);
			this.cond = cond;
			this.thenFodder = thenFodder;
			this.branchTrue = branchTrue;
			this.elseFodder = elseFodder;
			this.branchFalse = branchFalse;
		}

	    @Override
		public ASTType type() { return ASTType.AST_CONDITIONAL; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}
		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}

	/** Represents the $ keyword. */
	public static class Dollar extends AST {

		public Dollar(LocationRange location, Fodder openFodder) {
			super(location, openFodder);
		}
		
		@Override
		public ASTType type() { return ASTType.AST_DOLLAR; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}
		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents error e. */
	public static class AstError extends AST {
	    AST expr;
	    
	    public AstError(LocationRange location, Fodder openFodder, AST expr) {
			super(location, openFodder);
			this.expr = expr;
		}

		@Override
		public ASTType type() { return ASTType.AST_ERROR; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents closures. */
	public static class Function extends AST {
	    Fodder parenLeftFodder;
	    ArgParams params;
	    boolean trailingComma;
	    Fodder parenRightFodder;
	    AST body;
	    
		Function(LocationRange location, Fodder openFodder,
				Fodder parenLeftFodder, ArgParams params, boolean trailingComma, Fodder parenRightFodder, AST body) {
			super(location, openFodder); 
			this.parenLeftFodder = parenLeftFodder;
			this.params = params;
			this.trailingComma = trailingComma;
			this.parenRightFodder = parenRightFodder;
			this.body = body;
	    }

		@Override
		public ASTType type() { return ASTType.AST_FUNCTION; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents import "file". */
	public static class Import extends AST {
	    LiteralString file;
	    public Import(LocationRange location, Fodder openFodder, LiteralString file) {
	    	super(location, openFodder);
	    	this.file = file;
	    }

		@Override
		public ASTType type() { return ASTType.AST_IMPORT; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}
	
		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}

	/** Represents importstr "file". */
	public static class Importstr extends AST {
	    LiteralString file;
	    
	    public Importstr(LocationRange location, Fodder openFodder, LiteralString file) {
	    	super(location, openFodder);
			this.file = file;
		}

	    @Override
		public ASTType type() { return ASTType.AST_IMPORTSTR; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}

	/** Represents both e[e] and the syntax sugar e.f.
	 *
	 * One of index and id will be null before desugaring.  After desugaring id will be null.
	 */
	public static class Index extends AST {
	    AST target;
	    Fodder dotFodder;  // When index is being used, this is the fodder before the [.
	    boolean isSlice;
	    AST index;
	    Fodder endColonFodder;  // When end is being used, this is the fodder before the :.
	    AST end;
	    Fodder stepColonFodder;  // When step is being used, this is the fodder before the :.
	    AST step;
	    Fodder idFodder;  // When index is being used, this is the fodder before the ].
	    Identifier id;
	    
	    // Use this constructor for e.f
	    Index(LocationRange location, Fodder openFodder,
	    		AST target, Fodder dotFodder, Fodder idFodder, Identifier id) {
	    	super(location, openFodder);
	    	this.target = target;
	    	this.dotFodder = dotFodder;
	    	this.idFodder = idFodder;
	    	this.id = id;
	    }

	    // Use this constructor for e[x:y:z] with null for index, end or step if not present.
		public Index(LocationRange location, Fodder openFodder, 
				AST target, Fodder dotFodder, boolean isSlice, AST index, Fodder endColonFodder, AST end,
				Fodder stepColonFodder, AST step, Fodder idFodder) {
			super(location, openFodder);
			this.target = target;
			this.dotFodder = dotFodder;
			this.isSlice = isSlice;
			this.index = index;
			this.endColonFodder = endColonFodder;
			this.end = end;
			this.stepColonFodder = stepColonFodder;
			this.step = step;
			this.idFodder = idFodder;
			this.id = null; // null;
		}

		@Override
		public ASTType type() { return ASTType.AST_INDEX; }
	
		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	public static class Bind { // used in Local
		Fodder varFodder;
		Identifier var;
		Fodder opFodder;
		AST body;
		boolean functionSugar;
		Fodder parenLeftFodder;
		ArgParams params;  // If functionSugar == true
		boolean trailingComma;
		Fodder parenRightFodder;
		Fodder closeFodder;

		public Bind(Fodder varFodder, Identifier var, Fodder opFodder, AST body, boolean functionSugar,
				Fodder parenLeftFodder, ArgParams params, boolean trailingComma, Fodder parenRightFodder,
				Fodder closeFodder) {
			super();
			this.varFodder = varFodder;
			this.var = var;
			this.opFodder = opFodder;
			this.body = body;
			this.functionSugar = functionSugar;
			this.parenLeftFodder = parenLeftFodder;
			this.params = params;
			this.trailingComma = trailingComma;
			this.parenRightFodder = parenRightFodder;
			this.closeFodder = closeFodder;
		}
		
	}
	
	/** Represents local x = e; e.  After desugaring, functionSugar is false. */
	public static class Local extends AST {
	    // typedef List<Bind> Binds;
	    List<Bind> binds;
	    AST body;
	    
	    public Local(LocationRange location, Fodder openFodder,
	    		List<Bind> binds, AST body) {
	    	super(location, openFodder);
			this.binds = binds;
			this.body = body;
		}

		@Override
		public ASTType type() { return ASTType.AST_LOCAL; }
		
		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents true and false. */
	public static class LiteralBoolean extends AST {
	    boolean value;
	    
	    public LiteralBoolean(LocationRange location, Fodder openFodder,
	    		boolean value) {
	    	super(location, openFodder);
			this.value = value;
		}

		@Override
		public ASTType type() { return ASTType.AST_LITERAL_BOOLEAN; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents the null keyword. */
	public static class LiteralNull extends AST {
		LiteralNull(LocationRange location, Fodder openFodder) {
			super(location, openFodder);
	    }

		@Override
		public ASTType type() { return ASTType.AST_LITERAL_NULL; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents JSON numbers. */
	public static class LiteralNumber extends AST {
	    double value;
	    String originalString;
	    
	    public LiteralNumber(LocationRange location, Fodder openFodder,
	    		double value, String originalString) {
	    	super(location, openFodder);
			this.value = value;
			this.originalString = originalString;
		}

		@Override
		public ASTType type() { return ASTType.AST_LITERAL_NUMBER; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents JSON strings. */
	public static class LiteralString extends AST {
	    String value;
	    enum TokenKind { SINGLE, DOUBLE, BLOCK, VERBATIM_SINGLE, VERBATIM_DOUBLE };
	    TokenKind tokenKind;
	    String blockIndent;      // Only contains ' ' and '\t'.
	    String blockTermIndent;  // Only contains ' ' and '\t'.

	    public LiteralString(LocationRange location, Fodder openFodder,
	    		String value, TokenKind tokenKind, String blockIndent, String blockTermIndent) {
	    	super(location, openFodder); 
			this.value = value;
			this.tokenKind = tokenKind;
			this.blockIndent = blockIndent;
			this.blockTermIndent = blockTermIndent;
		}

		@Override
		public ASTType type() { return ASTType.AST_LITERAL_STRING; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	public static class ObjectField {
	    // Depending on the kind of Jsonnet field, the fields of this C++ class are used for storing
	    // different parts of the AST.
	    public static enum Kind {
	
	        // <fodder1> 'assert' <expr2>
	        // [ <opFodder> : <expr3> ]
	        // <commaFodder>
	        ASSERT,
	
	        // <fodder1> id
	        // [ <fodderL> '(' <params> <fodderR> ')' ]
	        // <opFodder> [+]:[:[:]] <expr2>
	        // <commaFodder>
	        FIELD_ID,
	
	        // <fodder1> '[' <expr1> <fodder2> ']'
	        // [ <fodderL> '(' <params> <fodderR> ')' ]
	        // <opFodder> [+]:[:[:]] <expr2>
	        // <commaFodder>
	        FIELD_EXPR,
	
	        // <expr1>
	        // <fodderL> '(' <params> <fodderR> ')'
	        // <opFodder> [+]:[:[:]] <expr2>
	        // <commaFodder>
	        FIELD_STR,
	
	        // <fodder1> 'local' <fodder2> id
	        // [ <fodderL> '(' <params> <fodderR> ')' ]
	        // [ <opFodder> = <expr2> ]
	        // <commaFodder>
	        LOCAL,
	    };
	
	    // NOTE TO SELF: sort out fodder1-4, then modify desugarer (maybe) parser and unparser.
	
	    public static enum Hide {
	        HIDDEN,   // f:: e
	        INHERIT,  // f: e
	        VISIBLE,  // f::: e
	    };
	    
	    Kind kind;
	    Fodder fodder1, fodder2, fodderL, fodderR;
	    Hide hide;    // (ignore if kind != FIELD_something
	    boolean superSugar;   // +:  (ignore if kind != FIELD_something)
	    boolean methodSugar;  // f(x, y, z): ...  (ignore if kind  == ASSERT)
	    AST expr1;        // Not in scope of the object
	    Identifier id;
	    ArgParams params;    // If methodSugar == true then holds the params.
	    boolean trailingComma;  // If methodSugar == true then remembers the trailing comma.
	    Fodder opFodder;     // Before the : or =
	    AST expr2;
	    AST expr3;  // In scope of the object (can see self).
	    Fodder commaFodder;  // If this field is followed by a comma, this is its fodder.
	
	    
	    public ObjectField(Kind kind, Fodder fodder1, Fodder fodder2, Fodder fodderL, Fodder fodderR, Hide hide,
				boolean superSugar, boolean methodSugar, AST expr1, Identifier id, ArgParams params,
				boolean trailingComma, Fodder opFodder, AST expr2, AST expr3, Fodder commaFodder) {
			super();
			this.kind = kind;
			this.fodder1 = fodder1;
			this.fodder2 = fodder2;
			this.fodderL = fodderL;
			this.fodderR = fodderR;
			this.hide = hide;
			this.superSugar = superSugar;
			this.methodSugar = methodSugar;
			this.expr1 = expr1;
			this.id = id;
			this.params = params;
			this.trailingComma = trailingComma;
			this.opFodder = opFodder;
			this.expr2 = expr2;
			this.expr3 = expr3;
			this.commaFodder = commaFodder;

	        // Enforce what is written in comments above.
	        assert(kind != Kind.ASSERT || (hide == Hide.VISIBLE && !superSugar && !methodSugar));
	        assert(kind != Kind.LOCAL || (hide == Hide.VISIBLE && !superSugar));
	        assert(kind != Kind.FIELD_ID || (id != null && expr1 == null));
	        assert(kind == Kind.FIELD_ID || kind == Kind.LOCAL || id == null);
	        assert(methodSugar || (params.size() == 0 && !trailingComma));
	        assert(kind == Kind.ASSERT || expr3 == null);
	    }
	    
	    // For when we don't know if it's a function or not.
	    public static ObjectField Local(Fodder fodder1, Fodder fodder2, Fodder fodder_l,
	                             Fodder fodder_r, boolean method_sugar, Identifier id,
	                             ArgParams params, boolean trailing_comma, Fodder op_fodder,
	                             AST body, Fodder comma_fodder) {
	        return new ObjectField(Kind.LOCAL,
	                           fodder1,
	                           fodder2,
	                           fodder_l,
	                           fodder_r,
	                           Hide.VISIBLE,
	                           false,
	                           method_sugar,
	                           null,
	                           id,
	                           params,
	                           trailing_comma,
	                           op_fodder,
	                           body,
	                           null,
	                           comma_fodder);
	    }
	    
	    public static ObjectField Local(Fodder fodder1, Fodder fodder2, Identifier id,
	                             Fodder op_fodder, AST body, Fodder comma_fodder) {
	        return new ObjectField(Kind.LOCAL,
	                           fodder1,
	                           fodder2,
	                           new Fodder(),
	                           new Fodder(),
	                           Hide.VISIBLE,
	                           false,
	                           false,
	                           null,
	                           id,
	                           new ArgParams(),
	                           false,
	                           op_fodder,
	                           body,
	                           null,
	                           comma_fodder);
	    }
	    
	    public static ObjectField LocalMethod(Fodder fodder1, Fodder fodder2,
	                                   Fodder fodder_l, Fodder fodder_r,
	                                   Identifier id, ArgParams params,
	                                   boolean trailing_comma, Fodder op_fodder, AST body,
	                                   Fodder comma_fodder) {
	        return new ObjectField(Kind.LOCAL,
	                           fodder1,
	                           fodder2,
	                           fodder_l,
	                           fodder_r,
	                           Hide.VISIBLE,
	                           false,
	                           true,
	                           null,
	                           id,
	                           params,
	                           trailing_comma,
	                           op_fodder,
	                           body,
	                           null,
	                           comma_fodder);
	    }
	    
	    public static ObjectField Assert(Fodder fodder1, AST body, Fodder op_fodder, AST msg,
	                              Fodder comma_fodder) {
	        return new ObjectField(Kind.ASSERT,
	                           fodder1,
	                           new Fodder(),
	                           new Fodder(),
	                           new Fodder(),
	                           Hide.VISIBLE,
	                           false,
	                           false,
	                           null,
	                           null,
	                           new ArgParams(),
	                           false,
	                           op_fodder,
	                           body,
	                           msg,
	                           comma_fodder);
	    }
	}
//	public static class ObjectFields extends ArrayList<ObjectField> {}

	/** Represents object constructors { f: e ... }.
	 *
	 * The trailing comma is only allowed if fields.size() > 0.  Converted to DesugaredObject during
	 * desugaring.
	 */
	public static class AstObject extends AST {
		ArrayList<ObjectField> fields;
	    boolean trailingComma;
	    Fodder closeFodder;

		public AstObject(LocationRange location, Fodder openFodder,
				ArrayList<ObjectField> fields, boolean trailingComma, Fodder closeFodder) {
			super(location, openFodder);
			this.fields = fields;
			this.trailingComma = trailingComma;
			this.closeFodder = closeFodder;

	        assert(fields.size() > 0 || !trailingComma);
	        if (fields.size() > 0)
	            assert(trailingComma || fields.get(fields.size() - 1).commaFodder.size() == 0);
	    }
	
		@Override
		public ASTType type() { return ASTType.AST_OBJECT; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents object constructors { f: e ... } after desugaring.
	 *
	 * The assertions either return true or raise an error.
	 */
	public static class DesugaredObject extends AST {
	    public static class Field {
	        ObjectField.Hide hide;
	        AST name;
	        AST body;
			public Field(Hide hide, AST name, AST body) {
				this.hide = hide;
				this.name = name;
				this.body = body;
			}
	    }
	    // public static class Fields extends ArrayList<Field> {};
	    List<AST> asserts;
	    List<Field> fields;
	    
	    public DesugaredObject(LocationRange lr, List<AST> asserts, List<Field> fields) {
	    	super(lr, new Fodder());
	        this.asserts = asserts;
	        this.fields = fields;
	    }

		@Override
		public ASTType type() { return ASTType.AST_DESUGARED_OBJECT; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}

	/** Represents object comprehension { [e]: e for x in e for.. if... }. */
	public static class ObjectComprehension extends AST {
	    List<ObjectField> fields;
	    boolean trailingComma;
	    List<ComprehensionSpec> specs;
	    Fodder closeFodder;

		public ObjectComprehension(LocationRange location, Fodder openFodder,
				List<ObjectField> fields, boolean trailingComma, List<ComprehensionSpec> specs,
				Fodder closeFodder) {
	    	super(location, openFodder);
			this.fields = fields;
			this.trailingComma = trailingComma;
			this.specs = specs;
			this.closeFodder = closeFodder;
		}

		@Override
		public ASTType type() { return ASTType.AST_OBJECT_COMPREHENSION; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents post-desugaring object comprehension { [e]: e for x in e }. */
	public static class ObjectComprehensionSimple extends AST {
	    AST field;
	    AST value;
	    Identifier id;
	    AST array;

	    public ObjectComprehensionSimple(LocationRange location, Fodder openFodder,
	    		AST field, AST value, Identifier id, AST array) {
	    	super(location, openFodder);
			this.field = field;
			this.value = value;
			this.id = id;
			this.array = array;
		}

		@Override
		public ASTType type() { return ASTType.AST_OBJECT_COMPREHENSION_SIMPLE; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents (e), which is desugared. */
	public static class Parens extends AST {
	    AST expr;
	    Fodder closeFodder;
	    
	    public Parens(LocationRange location, Fodder openFodder, 
	    		AST expr, Fodder closeFodder) {
	    	super(location, openFodder);
			this.expr = expr;
			this.closeFodder = closeFodder;
		}

		@Override
		public ASTType type() { return ASTType.AST_PARENS; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents the self keyword. */
	public static class Self extends AST {
		
	    public Self(LocationRange lr, Fodder openFodder) {
	    	super(lr, openFodder);
	    }
	    
		@Override
		public ASTType type() { return ASTType.AST_SELF; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents the super[e] and super.f constructs.
	 *
	 * Either index or identifier will be set before desugaring.  After desugaring, id will be null.
	 */
	public static class SuperIndex extends AST {
	    Fodder dotFodder;
	    AST index;
	    Fodder idFodder;
	    Identifier id;
	    
	    public SuperIndex(LocationRange lr, Fodder openFodder, 
	    		Fodder dotFodder, AST index, Fodder idFodder, Identifier id) {
	    	super(lr, openFodder);
			this.dotFodder = dotFodder;
			this.index = index;
			this.idFodder = idFodder;
			this.id = id;
		}

		@Override
		public ASTType type() { return ASTType.AST_SUPER_INDEX; }
	    
		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents the e in super construct.
	 */
	public static class InSuper extends AST {
	    AST element;
	    Fodder inFodder;
	    Fodder superFodder;
	    
		public InSuper(LocationRange location, Fodder openFodder,
				AST element, Fodder inFodder, Fodder superFodder) {
			super(location, openFodder);
			this.element = element;
			this.inFodder = inFodder;
			this.superFodder = superFodder;
		}

		@Override
		public ASTType type() { return ASTType.AST_IN_SUPER; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	public static enum UnaryOp { 
		UOP_NOT("!"), 
		UOP_BITWISE_NOT("~"), 
		UOP_PLUS("+"), 
		UOP_MINUS("-"); 
		
		private final String op;

		private UnaryOp(String op) {
			this.op = op;
		}
		
		public String toString() {
			return op;
		}
		
		private static Map<String, UnaryOp> mapByText() {
		    Map<String, UnaryOp> r = new LinkedHashMap<>();
		    for(UnaryOp op : UnaryOp.values()) {
		    	r.put(op.op, op);
		    }
		    return Collections.unmodifiableMap(r);
		}

	}
	
	/** Represents unary operators. */
	public static class Unary extends AST {
	    UnaryOp op;
	    AST expr;
	    
	    public Unary(LocationRange location, Fodder openFodder,
	    		UnaryOp op, AST expr) {
			super(location, openFodder);
			this.op = op;
			this.expr = expr;
		}

		@Override
		public ASTType type() { return ASTType.AST_UNARY; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	/** Represents variables. */
	public static class Var extends AST {
	    Identifier id;
	    
	    public Var(LocationRange location, Fodder openFodder,
	    		Identifier id) {
			super(location, openFodder);
			this.id = id;
		}

		@Override
		public ASTType type() { return ASTType.AST_VAR; }

		@Override
		public void accept(ASTVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public <TRes> TRes accept(ASTVisitorRes<TRes> visitor) {
			return visitor.visit(this);
		}

	}
	
	// Precedences used by various compilation units are defined here.
	public static final int APPLY_PRECEDENCE = 2;         // Function calls and indexing.
	public static final int UNARY_PRECEDENCE = 4;         // Logical and bitwise negation, unary + -
	public static final int MAX_PRECEDENCE = 15;          // higher than any other precedence
	
	@Deprecated
	public static final Map<BinaryOp, Integer> precedence_map = BinaryOp.precedenceMap();
	public static final Map<String, UnaryOp> unary_map = UnaryOp.mapByText();
	public static final Map<String, BinaryOp> binary_map = BinaryOp.mapByText();

}
