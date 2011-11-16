/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.util.Iterator;
import koala.dynamicjava.tree.AddExpression;
import koala.dynamicjava.tree.AndExpression;
import koala.dynamicjava.tree.ArrayAllocation;
import koala.dynamicjava.tree.ArrayInitializer;
import koala.dynamicjava.tree.DivideExpression;
import koala.dynamicjava.tree.EqualExpression;
import koala.dynamicjava.tree.GreaterExpression;
import koala.dynamicjava.tree.GreaterOrEqualExpression;
import koala.dynamicjava.tree.LessExpression;
import koala.dynamicjava.tree.LessOrEqualExpression;
import koala.dynamicjava.tree.Literal;
import koala.dynamicjava.tree.MinusExpression;
import koala.dynamicjava.tree.MultiplyExpression;
import koala.dynamicjava.tree.Node;
import koala.dynamicjava.tree.NotEqualExpression;
import koala.dynamicjava.tree.ObjectMethodCall;
import koala.dynamicjava.tree.OrExpression;
import koala.dynamicjava.tree.PrimitiveType;
import koala.dynamicjava.tree.QualifiedName;
import koala.dynamicjava.tree.ReferenceType;
import koala.dynamicjava.tree.SimpleAllocation;
import koala.dynamicjava.tree.SimpleAssignExpression;
import koala.dynamicjava.tree.SubtractExpression;
/**
 * Visitor d'expression permettant de remplir un FindUses$Report.
 *
 * @version $Revision: 1.5 $
 */
class FindUsesReportFiller extends AbstractVisitor {
    private static final String USELESS_CALL = "Inutile pour la recherche de variables.";
    private final FindUses.Report report;

    FindUsesReportFiller(FindUses.Report report) {
        this.report = report;
    }

    public Object visit(Literal literal) {
        return null;
    }


    public Object visit(QualifiedName name) {
        report.declareUse(name.getRepresentation());
        return null;
    }


    public Object visit(ObjectMethodCall call) {
        if (call.getExpression() != null) {
            call.getExpression().acceptVisitor(this);
        }
        if (call.getArguments() != null) {
            for (Iterator iter = call.getArguments().iterator(); iter.hasNext();) {
                Node obj = (Node)iter.next();
                obj.acceptVisitor(this);
            }
        }
        return null;
    }


    public Object visit(PrimitiveType type) {
        throw new InternalError(USELESS_CALL);
    }


    public Object visit(ReferenceType type) {
        throw new InternalError(USELESS_CALL);
    }


    public Object visit(ArrayInitializer node) {
        for (Iterator iter = node.getCells().iterator(); iter.hasNext();) {
            Node oneCell = (Node)iter.next();
            oneCell.acceptVisitor(this);
        }
        return null;
    }


    public Object visit(ArrayAllocation node) {
        node.getInitialization().acceptVisitor(this);
        return null;
    }


    public Object visit(SimpleAllocation node) {
        for (Iterator iter = node.getArguments().iterator(); iter.hasNext();) {
            Node arg = (Node)iter.next();
            arg.acceptVisitor(this);
        }
        return null;
    }


    public Object visit(MinusExpression node) {
        node.getExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(MultiplyExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(DivideExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(AddExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(SubtractExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(LessExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(GreaterExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(LessOrEqualExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(GreaterOrEqualExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(EqualExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(NotEqualExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(AndExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(OrExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }


    public Object visit(SimpleAssignExpression expression) {
        expression.getLeftExpression().acceptVisitor(this);
        expression.getRightExpression().acceptVisitor(this);
        return null;
    }
}
