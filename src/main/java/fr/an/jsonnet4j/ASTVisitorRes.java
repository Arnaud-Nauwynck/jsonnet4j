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

public abstract class ASTVisitorRes<TRes> {

    public abstract TRes visit(Apply ast);

    public abstract TRes visit(ApplyBrace ast) ;

    public abstract TRes visit(Array ast);

    public abstract TRes visit(ArrayComprehension ast) ;

    public abstract TRes visit(Assert ast);

    public abstract TRes visit(Binary ast);

    public abstract TRes visit(BuiltinFunction ast);

    public abstract TRes visit(Conditional ast);

    public abstract TRes visit(Dollar ast);

    public abstract TRes visit(AstError ast);

    public abstract TRes visit(Function ast);

    public abstract TRes visit(Import ast);

    public abstract TRes visit(Importstr ast);

    public abstract TRes visit(InSuper ast);

    public abstract TRes visit(Index ast);

    public abstract TRes visit(Local ast);

    public abstract TRes visit(LiteralBoolean ast);

    public abstract TRes visit(LiteralNumber ast);

    public abstract TRes visit(LiteralString ast);

    public abstract TRes visit(LiteralNull ast);

    public abstract TRes visit(AstObject ast);

    public abstract TRes visit(DesugaredObject ast);

    public abstract TRes visit(ObjectComprehension ast);

    public abstract TRes visit(ObjectComprehensionSimple ast);

    public abstract TRes visit(Parens ast);

    public abstract TRes visit(Self self);

    public abstract TRes visit(SuperIndex ast);

    public abstract TRes visit(Unary ast);

    public abstract TRes visit(Var v);

}
