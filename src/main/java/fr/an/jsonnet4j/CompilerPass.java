package fr.an.jsonnet4j;

import java.util.List;

import fr.an.jsonnet4j.AST.Apply;
import fr.an.jsonnet4j.AST.ApplyBrace;
import fr.an.jsonnet4j.AST.ArgParams;
import fr.an.jsonnet4j.AST.Array;
import fr.an.jsonnet4j.AST.ArrayComprehension;
import fr.an.jsonnet4j.AST.Assert;
import fr.an.jsonnet4j.AST.Binary;
import fr.an.jsonnet4j.AST.BuiltinFunction;
import fr.an.jsonnet4j.AST.ComprehensionSpec;
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
import fr.an.jsonnet4j.AST.ObjectField;
import fr.an.jsonnet4j.AST.Parens;
import fr.an.jsonnet4j.AST.Self;
import fr.an.jsonnet4j.AST.SuperIndex;
import fr.an.jsonnet4j.AST.Unary;
import fr.an.jsonnet4j.AST.Var;

public abstract class CompilerPass extends ASTVisitor {
   
	protected Allocator alloc;

   
    CompilerPass(Allocator alloc) {
    	this.alloc = alloc;
    }

    public void fodderElement(FodderElement elt) {}

    public void fodder(Fodder fodder) {}

    public void specs(List<ComprehensionSpec> specs) {}

    public void params(Fodder fodder_l, ArgParams params, Fodder fodder_r) {}

    public void fieldParams(ObjectField field) {}

    public void fields(List<ObjectField> fields) {}

    public void expr(AST ast) {}

    public void visit(Apply ast) {}

    public void visit(ApplyBrace ast)  {}

    public void visit(Array ast) {}

    public void visit(ArrayComprehension ast)  {}

    public void visit(Assert ast) {}

    public void visit(Binary ast) {}

    public void visit(BuiltinFunction ast) {}

    public void visit(Conditional ast) {}

    public void visit(Dollar ast) {}

    public void visit(Error ast) {}

    public void visit(Function ast) {}

    public void visit(Import ast) {}

    public void visit(Importstr ast) {}

    public void visit(InSuper ast) {}

    public void visit(Index ast) {}

    public void visit(Local ast) {}

    public void visit(LiteralBoolean ast) {}

    public void visit(LiteralNumber ast) {}

    public void visit(LiteralString ast) {}

    public void visit(LiteralNull ast) {}

    public void visit(Object ast) {}

    public void visit(DesugaredObject ast) {}

    public void visit(ObjectComprehension ast) {}

    public void visit(ObjectComprehensionSimple ast) {}

    public void visit(Parens ast) {}

    public void visit(Self self) {}

    public void visit(SuperIndex ast) {}

    public void visit(Unary ast) {}

    public void visit(Var v) {}

    public void visitExpr(AST ast) {}

    public void file(AST body, Fodder final_fodder) {}
    
}

