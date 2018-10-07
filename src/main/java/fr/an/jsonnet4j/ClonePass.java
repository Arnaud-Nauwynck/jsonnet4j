package fr.an.jsonnet4j;

import fr.an.jsonnet4j.AST.Apply;
import fr.an.jsonnet4j.AST.ApplyBrace;
import fr.an.jsonnet4j.AST.Array;
import fr.an.jsonnet4j.AST.ArrayComprehension;
import fr.an.jsonnet4j.AST.Assert;
import fr.an.jsonnet4j.AST.AstError;
import fr.an.jsonnet4j.AST.AstObject;
import fr.an.jsonnet4j.AST.Binary;
import fr.an.jsonnet4j.AST.BuiltinFunction;
import fr.an.jsonnet4j.AST.Conditional;
import fr.an.jsonnet4j.AST.DesugaredObject;
import fr.an.jsonnet4j.AST.Dollar;
import fr.an.jsonnet4j.AST.Function;
import fr.an.jsonnet4j.AST.Import;
import fr.an.jsonnet4j.AST.Importstr;
import fr.an.jsonnet4j.AST.InSuper;
import fr.an.jsonnet4j.AST.Index;
import fr.an.jsonnet4j.AST.LiteralBoolean;
import fr.an.jsonnet4j.AST.LiteralNull;
import fr.an.jsonnet4j.AST.LiteralNumber;
import fr.an.jsonnet4j.AST.LiteralString;
import fr.an.jsonnet4j.AST.Local;
import fr.an.jsonnet4j.AST.ObjectComprehension;
import fr.an.jsonnet4j.AST.ObjectComprehensionSimple;
import fr.an.jsonnet4j.AST.Parens;
import fr.an.jsonnet4j.AST.Self;
import fr.an.jsonnet4j.AST.SuperIndex;
import fr.an.jsonnet4j.AST.Unary;
import fr.an.jsonnet4j.AST.Var;

//TODO recursive clones ????..
/** A pass that clones the AST it is given. */
public class ClonePass extends ASTVisitorRes<AST> {

	ClonePass(Allocator alloc) {
		// super(alloc);
	}

	/**
	 * Return an equivalent AST that can be modified without affecting the original.
	 *
	 * This is a deep copy.
	 */
	public static AST clone_ast(Allocator alloc, AST ast) {
		return ast.accept(new ClonePass(alloc));
	}

	@Override
	public AST visit(Apply ast) {
		return new Apply(ast.location, ast.openFodder, ast.target, ast.fodderL, ast.args, ast.trailingComma,
				ast.fodderR, ast.tailstrictFodder, ast.tailstrict);
	}

	@Override
	public AST visit(ApplyBrace ast) {
		return new ApplyBrace(ast.location, ast.openFodder, ast.left, ast.right);
	}

	@Override
	public AST visit(Array ast) {
		return new Array(ast.location, ast.openFodder, ast.elements, ast.trailingComma, ast.closeFodder);
	}

	@Override
	public AST visit(ArrayComprehension ast) {
		return new ArrayComprehension(ast.location, ast.openFodder, //
				ast.body, ast.commaFodder, ast.trailingComma, ast.specs, ast.closeFodder);
	}

	@Override
	public AST visit(Assert ast) {
		return new Assert(ast.location, ast.openFodder, //
				ast.cond, ast.colonFodder, ast.message, ast.semicolonFodder, ast.rest);
	}

	@Override
	public AST visit(Binary ast) {
		return new Binary(ast.location, ast.openFodder, //
				ast.left, ast.opFodder, ast.op, ast.right);
	}

	@Override
	public AST visit(BuiltinFunction ast) {
		return new BuiltinFunction(ast.location, ast.name, ast.params);

	}

	@Override
	public AST visit(Conditional ast) {
		return new Conditional(ast.location, ast.openFodder, ast.cond, ast.thenFodder, ast.branchTrue, ast.elseFodder, ast.branchFalse);
	}

	@Override
	public AST visit(Dollar ast) {
		return new Dollar(ast.location, ast.openFodder);
	}

	@Override
	public AST visit(AstError ast) {
		return new AstError(ast.location, ast.openFodder, ast.expr);
	}

	@Override
	public AST visit(Function ast) {
		return new Function(ast.location, ast.openFodder, ast.parenLeftFodder, ast.params, ast.trailingComma,
				ast.parenRightFodder, ast.body);
	}

	@Override
	public AST visit(Import ast) {
		return new Import(ast.location, ast.openFodder, ast.file);
	}

	@Override
	public AST visit(Importstr ast) {
		return new Importstr(ast.location, ast.openFodder, ast.file);
	}

	@Override
	public AST visit(InSuper ast) {
		return new InSuper(ast.location, ast.openFodder, ast.element, ast.inFodder, ast.superFodder);
	}

	@Override
	public AST visit(Index ast) {
		return new Index(ast.location, ast.openFodder, ast.target, ast.dotFodder, ast.isSlice, ast.index,
				ast.endColonFodder, ast.end, ast.stepColonFodder, ast.step, ast.idFodder);
	}

	@Override
	public AST visit(Local ast) {
		return new Local(ast.location, ast.openFodder, ast.binds, ast.body);
	}

	@Override
	public AST visit(LiteralBoolean ast) {
		return new LiteralBoolean(ast.location, ast.openFodder, ast.value);
	}

	@Override
	public AST visit(LiteralNumber ast) {
		return new LiteralNumber(ast.location, ast.openFodder, ast.value, ast.originalString);
	}

	@Override
	public AST visit(LiteralString ast) {
		return new LiteralString(ast.location, ast.openFodder, ast.value, ast.tokenKind, ast.blockIndent,
				ast.blockTermIndent);
	}

	@Override
	public AST visit(LiteralNull ast) {
		return new LiteralNull(ast.location, ast.openFodder);
	}

	@Override
	public AST visit(AstObject ast) {
		return new AstObject(ast.location, ast.openFodder, ast.fields, ast.trailingComma, ast.closeFodder);
	}

	@Override
	public AST visit(DesugaredObject ast) {
		return new DesugaredObject(ast.location, ast.asserts, ast.fields);
	}

	@Override
	public AST visit(ObjectComprehension ast) {
		return new ObjectComprehension(ast.location, ast.openFodder, ast.fields, ast.trailingComma, ast.specs,
				ast.closeFodder);
	}

	@Override
	public AST visit(ObjectComprehensionSimple ast) {
		return new ObjectComprehensionSimple(ast.location, ast.openFodder, ast.field, ast.value, ast.id, ast.array);
	}

	@Override
	public AST visit(Parens ast) {
		return new Parens(ast.location, ast.openFodder, ast.expr, ast.closeFodder);
	}

	@Override
	public AST visit(Self ast) {
		return new Self(ast.location, ast.openFodder);
	}

	@Override
	public AST visit(SuperIndex ast) {
		return new SuperIndex(ast.location, ast.openFodder, ast.dotFodder, ast.index, ast.idFodder, ast.id);
	}

	@Override
	public AST visit(Unary ast) {
		return new Unary(ast.location, ast.openFodder, ast.op, ast.expr);
	}

	@Override
	public AST visit(Var ast) {
		return new Var(ast.location, ast.openFodder, ast.id);
	}

//	@Override
//	public AST visitExpr(AST ast) {
//		return new AST 
//	}

}
