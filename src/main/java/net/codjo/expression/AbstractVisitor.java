/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import koala.dynamicjava.tree.*;
import koala.dynamicjava.tree.visitor.Visitor;
/**
 * Classe abstraite du visiteur ne contenant que les méthodes non utilisés.
 *
 * @version $Revision: 1.5 $
 */
abstract class AbstractVisitor implements Visitor {
    private static final String MSG_VISIT_NOT_IMPLEMENTED =
        "Method visit() not yet implemented.";

    public Object visit(PackageDeclaration node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ImportDeclaration node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(EmptyStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(WhileStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ForStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(DoStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(SwitchStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(SwitchBlock node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(LabeledStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(BreakStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(TryStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(CatchStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ThrowStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ReturnStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(SynchronizedStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ContinueStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(IfThenStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(IfThenElseStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ThisExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ObjectFieldAccess node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(StaticFieldAccess node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ArrayAccess node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(SuperFieldAccess node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(FunctionCall node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(StaticMethodCall node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ConstructorInvocation node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(SuperMethodCall node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ArrayType node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(TypeExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(PostIncrement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(PostDecrement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(PreIncrement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(PreDecrement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ClassAllocation node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(InnerAllocation node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(InnerClassAllocation node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(CastExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(NotExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ComplementExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(PlusExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(RemainderExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ShiftLeftExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ShiftRightExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(UnsignedShiftRightExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(InstanceOfExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(BitAndExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ExclusiveOrExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(BitOrExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ConditionalExpression node) {
        throw new UnsupportedOperationException(
            "Method visit(ConditionalExpression) no more implemented.");
    }


    public Object visit(MultiplyAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(DivideAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(RemainderAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(AddAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(SubtractAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ShiftLeftAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ShiftRightAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(UnsignedShiftRightAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(BitAndAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ExclusiveOrAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(BitOrAssignExpression node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(BlockStatement node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ClassDeclaration node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(InterfaceDeclaration node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ConstructorDeclaration node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(MethodDeclaration node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(FormalParameter node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(FieldDeclaration node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(VariableDeclaration node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(ClassInitializer node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }


    public Object visit(InstanceInitializer node) {
        throw new UnsupportedOperationException(MSG_VISIT_NOT_IMPLEMENTED);
    }
}
