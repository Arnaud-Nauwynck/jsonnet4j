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

public abstract class ASTVisitor {

    public abstract void visit(Apply ast);

    public abstract void visit(ApplyBrace ast) ;

    public abstract void visit(Array ast);

    public abstract void visit(ArrayComprehension ast) ;

    public abstract void visit(Assert ast);

    public abstract void visit(Binary ast);

    public abstract void visit(BuiltinFunction ast);

    public abstract void visit(Conditional ast);

    public abstract void visit(Dollar ast);

    public abstract void visit(AstError ast);

    public abstract void visit(Function ast);

    public abstract void visit(Import ast);

    public abstract void visit(Importstr ast);

    public abstract void visit(InSuper ast);

    public abstract void visit(Index ast);

    public abstract void visit(Local ast);

    public abstract void visit(LiteralBoolean ast);

    public abstract void visit(LiteralNumber ast);

    public abstract void visit(LiteralString ast);

    public abstract void visit(LiteralNull ast);

    public abstract void visit(AstObject ast);

    public abstract void visit(DesugaredObject ast);

    public abstract void visit(ObjectComprehension ast);

    public abstract void visit(ObjectComprehensionSimple ast);

    public abstract void visit(Parens ast);

    public abstract void visit(Self self);

    public abstract void visit(SuperIndex ast);

    public abstract void visit(Unary ast);

    public abstract void visit(Var v);

    public abstract void visitExpr(AST ast);

}
