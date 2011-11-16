/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import koala.dynamicjava.tree.AddExpression;
import koala.dynamicjava.tree.AndExpression;
import koala.dynamicjava.tree.ArrayAllocation;
import koala.dynamicjava.tree.ArrayInitializer;
import koala.dynamicjava.tree.BinaryExpression;
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
 * Visiteur de conversion. Cette objet converti les expressions arithmetiques.
 *
 * <p> Cett classe utilise le design pattern Visitor (coucou Olivier :). Il est appele a chaque noeud de
 * l'arbre syntaxique. Par exemple, la methode <code>visit(AddExpression)</code> sera appele afin de convertir
 * une expression de type "A + B". </p>
 *
 * @author $Author: palmont $
 * @version $Revision: 1.10 $
 */
class InterpreterVisitor extends AbstractVisitor {
    private static final Integer NUMERIC = Types.NUMERIC;
    private static final Integer VARCHAR = Types.VARCHAR;
    private static final Integer DOUBLE = Types.DOUBLE;
    private static final Integer INTEGER = Types.INTEGER;
    private static final Integer BIT = Types.BIT;
    private static final Integer DATE = Types.TIMESTAMP;
    private static final Integer ARRAY = Types.ARRAY;
    private static final Integer SCALAR = Integer.MAX_VALUE;
    private Interpreter interpreter;


    InterpreterVisitor(Interpreter interpreter) {
        this.interpreter = interpreter;
    }


    /**
     * Converti un nom de variable. Exemple "DEST_TABLE_ID".
     *
     * @param node
     *
     * @return
     */
    public Object visit(QualifiedName node) {
        return new NodeInfo(interpreter.getSqlType(node.getRepresentation()),
                            node.getRepresentation());
    }


    /**
     * Converti un literal. Exemple "1".
     *
     * @param node
     *
     * @return
     */
    public Object visit(Literal node) {
        if (node.getType() == String.class) {
            return new NodeInfo(VARCHAR, node.getRepresentation());
        }
        else if (node.getType() == boolean.class) {
            return new NodeInfo(BIT, node.getRepresentation());
        }
        else {
            return new NodeInfo(SCALAR, node.getRepresentation());
        }
    }


    /**
     * Converti les appels de methode. exemple "utils.getLastDay(a)".
     *
     * <p> <b>ATTENTION </b>: Cette methode traite les cas particulier de "Math.abs(a)" converti en "a.abs()".
     * Le cas particulier de "Math.min/max(A,B)" converti en "A.min/max(B)". </p>
     *
     * @param node
     *
     * @return
     *
     * @throws IllegalArgumentException
     */
    public Object visit(ObjectMethodCall node) {
        if ("iif".equals(node.getMethodName())) {
            return visitIif(node);
        }
        if ("in".equals(node.getMethodName())) {
            return visitIn(node);
        }
        if ("notIn".equals(node.getMethodName())) {
            return visitNotIn(node);
        }

        // Determination de expression
        NodeInfo expression;
        if (node.getExpression() == null) {
            throw new IllegalArgumentException("Le cas des fonctions n'est pas traite: "
                                               + node.getMethodName());
        }
        expression = (NodeInfo)node.getExpression().acceptVisitor(this);
        // Cas Particulier de Math
        if ("Math".equals(expression.representation)) {
            return visitMath(node);
        }

        // Cas std
        String args = "(";
        List<Integer> argsTypeList = null;
        if (node.getArguments() != null) {
            argsTypeList = new ArrayList<Integer>();
            for (Iterator iter = node.getArguments().iterator(); iter.hasNext();) {
                Node obj = (Node)iter.next();

                NodeInfo info = (NodeInfo)obj.acceptVisitor(this);
                argsTypeList.add(info.sqlType);
                args += info.representation;
                if (iter.hasNext()) {
                    args += ", ";
                }
            }
        }
        args += ")";

        Integer returnSqlType = interpreter.getReturnSqlType(expression.representation,
                                                             expression.sqlType.intValue(),
                                                             node.getMethodName(), argsTypeList);

        return new NodeInfo(returnSqlType,
                            expression.representation + "." + node.getMethodName() + args);
    }


    public Object visit(PrimitiveType type) {
        return new NodeInfo(VARCHAR, type.getValue().toString());
    }


    public Object visit(ReferenceType node) {
        // Represente le nom de classe complet.
        return new NodeInfo(VARCHAR, node.getRepresentation());
    }


    public Object visit(ArrayInitializer node) {
        String array = "{";
        for (Iterator iter = node.getCells().iterator(); iter.hasNext();) {
            Node oneCell = (Node)iter.next();
            NodeInfo info = (NodeInfo)oneCell.acceptVisitor(this);
            array += info.representation;
            if (iter.hasNext()) {
                array += ", ";
            }
        }
        array += "}";
        return new NodeInfo(ARRAY, array);
    }


    public Object visit(ArrayAllocation node) {
        String representation = "new " + node.getCreationType().acceptVisitor(this);

        for (int i = 0; i < node.getDimension(); i++) {
            representation += "[]";
        }
        representation += node.getInitialization().acceptVisitor(this);

        return new NodeInfo(ARRAY, representation);
    }


    public Object visit(SimpleAllocation node) {
        NodeInfo typeInfo = (NodeInfo)node.getCreationType().acceptVisitor(this);
        String rep = "new " + typeInfo.representation;
        rep += "(";
        for (Iterator iter = node.getArguments().iterator(); iter.hasNext();) {
            Node arg = (Node)iter.next();
            NodeInfo info = (NodeInfo)arg.acceptVisitor(this);
            rep += info.representation;
            if (iter.hasNext()) {
                rep += ", ";
            }
        }
        rep += ")";
        return new NodeInfo(typeInfo.sqlType, rep);
    }


    /**
     * Converti "-A" en "A.neg()".
     *
     * @param node
     *
     * @return
     */
    public Object visit(MinusExpression node) {
        NodeInfo exp = (NodeInfo)node.getExpression().acceptVisitor(this);

        if (exp.isBigDecimal()) {
            return new NodeInfo(NUMERIC, "(" + exp.representation + ").negate()");
        }
        else {
            return new NodeInfo(exp.sqlType, "-(" + exp.representation + ")");
        }
    }


    /**
     * Converti "A-B" en "A.subtract(B)".
     *
     * @param node
     *
     * @return
     */
    public Object visit(SubtractExpression node) {
        return visitMathExpression(node, "-", "subtract");
    }


    /**
     * Converti "A + B" en "A.add(B)".
     *
     * @param node Description of Parameter
     *
     * @return Description of the Returned Value
     */
    public Object visit(AddExpression node) {
        return visitMathExpression(node, "+", "add");
    }


    /**
     * Converti "A / B" en "A.divide(B, BigDecimal.ROUND_HALF_UP)".
     *
     * @param node
     *
     * @return
     */
    public Object visit(DivideExpression node) {
        return visitMathExpression(node, "/", "divide", " ,10 ,BigDecimal.ROUND_HALF_UP");
    }


    /**
     * Converti "A  B" en "A.multiply(B)".
     *
     * @param node
     *
     * @return
     */
    public Object visit(MultiplyExpression node) {
        return visitMathExpression(node, "*", "multiply");
    }


    public Object visit(LessExpression node) {
        return visitCompareExpression(node, "<");
    }


    public Object visit(GreaterExpression node) {
        return visitCompareExpression(node, ">");
    }


    public Object visit(LessOrEqualExpression node) {
        return visitCompareExpression(node, "<=");
    }


    public Object visit(GreaterOrEqualExpression node) {
        return visitCompareExpression(node, ">=");
    }


    /**
     * Converti les expression "A==B" en "(A)==(B)".
     *
     * @param node
     *
     * @return
     */
    public Object visit(EqualExpression node) {
        return visitCompareExpression(node, "==");
    }


    public Object visit(NotEqualExpression node) {
        return visitCompareExpression(node, "!=");
    }


    /**
     * Converti "a && b" en "(a) && (b)"
     *
     * @param node
     *
     * @return
     */
    public Object visit(AndExpression node) {
        return visitBooleanExpression(node, "&&");
    }


    /**
     * Converti "a || b" en "(a) || (b)"
     *
     * @param node
     *
     * @return
     */
    public Object visit(OrExpression node) {
        return visitBooleanExpression(node, "||");
    }


    public Object visit(SimpleAssignExpression node) {
        NodeInfo left = (NodeInfo)node.getLeftExpression().acceptVisitor(this);
        NodeInfo right = (NodeInfo)node.getRightExpression().acceptVisitor(this);

        if (left.isBigDecimal()) {
            right.convertToBigDecimal();
        }
        else if (left.isInteger()) {
            right.convertToInteger();
        }
        else if (left.isDouble()) {
            right.convertToDouble();
        }
        else if (left.isDate() && right.isString()) {
            right.convertToDate();
        }
        return new NodeInfo(left.sqlType, left.representation + "="
                                          + right.representation);
    }


    /**
     * Converti "iif" en operateur ternaire.
     *
     * @param node noeud iif
     *
     * @return nodeInfo du iif
     */
    private NodeInfo visitIif(ObjectMethodCall node) {
        NodeInfo condition =
              (NodeInfo)((Node)node.getArguments().get(0)).acceptVisitor(this);
        NodeInfo expTrue =
              (NodeInfo)((Node)node.getArguments().get(1)).acceptVisitor(this);
        NodeInfo expFalse =
              (NodeInfo)((Node)node.getArguments().get(2)).acceptVisitor(this);

        if (expTrue.isBigDecimal() || expFalse.isBigDecimal()) {
            expTrue.convertToBigDecimal();
            expFalse.convertToBigDecimal();
        }

        if (expTrue.isString() && expFalse.isDate()) {
            expTrue.convertToDate();
        }

        if (expFalse.isString() && expTrue.isDate()) {
            expFalse.convertToDate();
        }

        return new NodeInfo(expTrue.sqlType,
                            "((" + condition.representation + ")?" + "(" + expTrue.representation + "):"
                            + "(" + expFalse.representation + "))");
    }


    /**
     * Converti "in" en une chaine du type Champ1==value1 || Champ2==value2 ...
     *
     * @param node noeud in
     *
     * @return nodeInfo du in
     */
    private NodeInfo visitIn(ObjectMethodCall node) {
        NodeInfo fieldNode =
              (NodeInfo)((Node)node.getArguments().get(0)).acceptVisitor(this);
        String field = (fieldNode).representation;
        StringBuffer inExp = new StringBuffer();
        for (int i = 1; i < node.getArguments().size(); i++) {
            inExp.append("(");
            inExp.append(field);
            inExp.append(").compareTo(");
            inExp.append(((NodeInfo)((Node)node.getArguments().get(i)).acceptVisitor(this)).representation);
            inExp.append(") == 0");
            if (i < node.getArguments().size() - 1) {
                inExp.append(" || ");
            }
        }

        return new NodeInfo(fieldNode.sqlType, inExp.toString());
    }


    /**
     * Converti "notIn" en une chaine du type Champ1!=value1 && Champ2!=value2 ...
     *
     * @param node noeud notIn
     *
     * @return nodeInfo du notIn
     */
    private NodeInfo visitNotIn(ObjectMethodCall node) {
        NodeInfo fieldNode =
              (NodeInfo)((Node)node.getArguments().get(0)).acceptVisitor(this);
        String field = (fieldNode).representation;
        StringBuffer notInExp = new StringBuffer();
        for (int i = 1; i < node.getArguments().size(); i++) {
            notInExp.append("(");
            notInExp.append(field);
            notInExp.append(").compareTo(");
            notInExp.append(((NodeInfo)((Node)node.getArguments().get(i)).acceptVisitor(
                  this)).representation);
            notInExp.append(") != 0");
            if (i < node.getArguments().size() - 1) {
                notInExp.append(" && ");
            }
        }

        return new NodeInfo(fieldNode.sqlType, notInExp.toString());
    }


    /**
     * Converti les methodes de l'objet Math.
     *
     * @param node Le noeud
     *
     * @return Le NodeInfo attache au noeud
     *
     * @throws IllegalArgumentException
     */
    private NodeInfo visitMath(ObjectMethodCall node) {
        if ("abs".equals(node.getMethodName())) {
            NodeInfo arg =
                  (NodeInfo)((Node)node.getArguments().get(0)).acceptVisitor(this);
            if (arg.isBigDecimal()) {
                return new NodeInfo(arg.sqlType, arg.representation + ".abs()");
            }
            else {
                return new NodeInfo(arg.sqlType, "Math.abs(" + arg.representation + ")");
            }
        }
        if ("min".equals(node.getMethodName())) {
            return visitMinMethod(node);
        }
        if ("max".equals(node.getMethodName())) {
            return visitMaxMethod(node);
        }
        throw new IllegalArgumentException("Cas de methode non traitee : Math."
                                           + node.getMethodName());
    }


    private NodeInfo visitMaxMethod(ObjectMethodCall node) {
        NodeInfo argA = (NodeInfo)((Node)node.getArguments().get(0)).acceptVisitor(this);
        NodeInfo argB = (NodeInfo)((Node)node.getArguments().get(1)).acceptVisitor(this);
        if (argA.isBigDecimal() || argB.isBigDecimal()) {
            argA.convertToBigDecimal();
            argB.convertToBigDecimal();
        }
        if (argA.isBigDecimal()) {
            return new NodeInfo(argA.sqlType,
                                argA.representation + ".max(" + argB.representation + ")");
        }
        else {
            return new NodeInfo(argA.sqlType,
                                "Math.max(" + argA.representation + "," + argB.representation + ")");
        }
    }


    private NodeInfo visitMinMethod(ObjectMethodCall node) {
        NodeInfo argA = (NodeInfo)((Node)node.getArguments().get(0)).acceptVisitor(this);
        NodeInfo argB = (NodeInfo)((Node)node.getArguments().get(1)).acceptVisitor(this);
        if (argA.isBigDecimal() || argB.isBigDecimal()) {
            argA.convertToBigDecimal();
            argB.convertToBigDecimal();
        }
        if (argA.isBigDecimal()) {
            return new NodeInfo(argA.sqlType,
                                argA.representation + ".min(" + argB.representation + ")");
        }
        else {
            return new NodeInfo(argA.sqlType,
                                "Math.min(" + argA.representation + "," + argB.representation + ")");
        }
    }


    private NodeInfo visitMathExpression(BinaryExpression node, String ope,
                                         String numericOpe) {
        return visitMathExpression(node, ope, numericOpe, "");
    }


    private NodeInfo visitMathExpression(BinaryExpression node, String ope,
                                         String numericOpe, String addon) {
        NodeInfo left = (NodeInfo)node.getLeftExpression().acceptVisitor(this);
        NodeInfo right = (NodeInfo)node.getRightExpression().acceptVisitor(this);

        if (left.isBigDecimal() || right.isBigDecimal()) {
            left.convertToBigDecimal();
            right.convertToBigDecimal();
            return new NodeInfo(NUMERIC,
                                "(" + left.representation + ")." + numericOpe + "("
                                + right.representation + addon + ")");
        }
        else {
            return new NodeInfo(left.sqlType,
                                "(" + left.representation + ")" + ope + "(" + right.representation + ")");
        }
    }


    private NodeInfo visitCompareExpression(BinaryExpression node, String ope) {
        NodeInfo left = (NodeInfo)node.getLeftExpression().acceptVisitor(this);
        NodeInfo right = (NodeInfo)node.getRightExpression().acceptVisitor(this);

        if (left.isScalar() && right.isScalar()) {
            return new NodeInfo(BIT,
                                "(" + left.representation + ")" + ope + "(" + right.representation + ")");
        }
        else {
            if (left.isBigDecimal() || right.isBigDecimal()) {
                left.convertToBigDecimal();
                right.convertToBigDecimal();
            }
            if (right.isString() && left.isDate()) {
                right.convertToDate();
            }
            return new NodeInfo(BIT,
                                "(" + left.representation + ").compareTo(" + right.representation + ")"
                                + ope + "0");
        }
    }


    /**
     * Converti les expressions booleenne (&& et ||).
     *
     * @param node Description of Parameter
     * @param ope  Description of Parameter
     *
     * @return Description of the Returned Value
     */
    private NodeInfo visitBooleanExpression(BinaryExpression node, String ope) {
        NodeInfo left = (NodeInfo)node.getLeftExpression().acceptVisitor(this);
        NodeInfo right = (NodeInfo)node.getRightExpression().acceptVisitor(this);

        return new NodeInfo(BIT,
                            "(" + left.representation + ")" + ope + "(" + right.representation + ")");
    }


    /**
     * Information attache a un noeud.
     *
     * @author $Author: palmont $
     * @version $Revision: 1.10 $
     */
    private class NodeInfo {
        private Integer sqlType;
        private String representation;


        /**
         * Constructor for the NodeInfo object
         *
         * @param sqlType        Description of Parameter
         * @param representation Description of Parameter
         */
        NodeInfo(Integer sqlType, String representation) {
            this.sqlType = sqlType;
            this.representation = representation;
        }


        /**
         * Gets the Scalar attribute of the NodeInfo object
         *
         * @return The Scalar value
         */
        public boolean isString() {
            return SqlTypeConverter.isString(sqlType);
        }


        /**
         * Gets the Date attribute of the NodeInfo object
         *
         * @return The Date value
         */
        public boolean isDate() {
            return SqlTypeConverter.isDate(sqlType);
        }


        /**
         * Gets the Scalar attribute of the NodeInfo object
         *
         * @return The Scalar value
         */
        public boolean isScalar() {
            if (sqlType == SCALAR) {
                return true;
            }
            else {
                return SqlTypeConverter.isScalar(sqlType);
            }
        }


        /**
         * Gets the BigDecimal attribute of the NodeInfo object
         *
         * @return The BigDecimal value
         */
        public boolean isBigDecimal() {
            return sqlType.intValue() == Types.NUMERIC;
        }


        /**
         * Gets the Double attribute of the NodeInfo object
         *
         * @return The Double value
         */
        public boolean isDouble() {
            return SqlTypeConverter.isDouble(sqlType);
        }


        /**
         * Gets the Integer attribute of the NodeInfo object
         *
         * @return The Integer value
         */
        public boolean isInteger() {
            return SqlTypeConverter.isInteger(sqlType);
        }


        public void convertToBigDecimal() {
            if (sqlType != Types.NUMERIC && !"null".equals(representation)) {
                representation = "new BigDecimal(" + representation + ")";
            }
            sqlType = NUMERIC;
        }


        public void convertToDouble() {
            if (sqlType != Types.NUMERIC) {
            }
            else {
                representation = "(" + representation + ").doubleValue()";
            }
            sqlType = DOUBLE;
        }


        public void convertToDate() {
            if (sqlType == Types.VARCHAR) {
                representation = "(java.sql.Date.valueOf(" + representation + "))";
            }
            sqlType = DATE;
        }


        public void convertToInteger() {
            if (sqlType == Types.NUMERIC) {
                representation = "(" + representation + ").intValue()";
            }
            sqlType = INTEGER;
        }


        @Override
        public String toString() {
            return representation;
        }
    }
}
