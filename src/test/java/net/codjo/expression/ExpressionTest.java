/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.sql.Types;
import junit.framework.TestCase;
/**
 * Classe permettant de tester la calsse Expression
 */
public class ExpressionTest extends TestCase {
    public void test_constructorExpression() throws ExpressionException {
        Expression expressionVarchar =
            new Expression("\"CONST\"", Types.VARCHAR, null, Types.VARCHAR);
        assertEquals(new Integer(Types.VARCHAR), expressionVarchar.getOutputSqlType());

        Expression expressionInt =
            new Expression("45", Types.INTEGER, null, Types.INTEGER);
        assertEquals(new Integer(Types.INTEGER), expressionInt.getOutputSqlType());

        Expression expressionDate =
            new Expression("\"10/05/2004\"", Types.VARCHAR, null, Types.DATE);
        assertEquals(new Integer(Types.DATE), expressionDate.getOutputSqlType());
    }


    public void test_constantExpression() throws ExpressionException {
        Expression expression =
            new Expression("\"CONST\"", Types.VARCHAR, null, Types.VARCHAR);

        Object resultat = expression.compute(null);

        assertEquals("CONST", resultat);
    }


    public void test_valeurExpression() throws ExpressionException {
        Expression expression =
            new Expression("Valeur", Types.VARCHAR, null, Types.VARCHAR);

        Object resultat = expression.compute("premiere valeur");

        assertEquals("premiere valeur", resultat);
    }


    public void test_concatenationExpression() throws ExpressionException {
        Expression expression =
            new Expression("Valeur+\"-ajout\"", Types.VARCHAR, null, Types.VARCHAR);

        Object resultat = expression.compute("premier");

        assertEquals("premier-ajout", resultat);
    }


    public void test_badOutputTypeExpression() throws ExpressionException {
        Expression expression = new Expression("10", Types.VARCHAR, null, Types.INTEGER);

        Object resultat = expression.compute("val");

        assertEquals(Integer.class, resultat.getClass());
    }


    public void test_equalsMethod() throws ExpressionException {
        Expression expression =
            new Expression("Valeur.equals(\"*\")", Types.VARCHAR, null, Types.BIT);

        Object resultat = expression.compute("val");
        assertEquals(Boolean.FALSE, resultat);

        resultat = expression.compute("*");
        assertEquals(Boolean.TRUE, resultat);
    }


    public void test_equalsOperator() throws ExpressionException {
        Expression expression =
            new Expression("Valeur == \"*\"", Types.VARCHAR, null, Types.BIT);

        Object resultat = expression.compute("val");
        assertEquals(Boolean.FALSE, resultat);

        resultat = expression.compute("*");
        assertEquals(Boolean.TRUE, resultat);
    }


    public void test_iifWithequals() throws ExpressionException {
        Expression expression =
            new Expression("iif(Valeur == \"*\", null, Valeur)", Types.VARCHAR, null,
                Types.VARCHAR);

        Object resultat = expression.compute("val");
        assertEquals("val", resultat);

        resultat = expression.compute("*");
        assertEquals(null, resultat);
    }


    public void test_multiParam() throws ExpressionException {
        Expression.Variable[] inputVariables = new Expression.Variable[2];
        inputVariables[0] = new Expression.Variable("Valeur1", Types.VARCHAR);
        inputVariables[1] = new Expression.Variable("Valeur2", Types.VARCHAR);

        Expression expression =
            new Expression("iif(Valeur1 == Valeur2, null, Valeur1)", inputVariables,
                null, Types.VARCHAR, null);

        inputVariables[0].setValue("*");
        inputVariables[1].setValue("*");
        Object resultat = expression.compute(inputVariables);
        assertNull(resultat);

        inputVariables[0].setValue("Not null");
        inputVariables[1].setValue("*");
        resultat = expression.compute(inputVariables);
        assertEquals("Not null", resultat);
    }


    public void test_methodsCallInCascade() throws ExpressionException {
        Expression expression =
            new Expression("Valeur.substring(5,10).endsWith(\"hij\")", Types.VARCHAR,
                null, Types.BIT);

        Boolean result = (Boolean)expression.compute("abcdefghijkl");
        assertEquals(Boolean.TRUE, result);
    }


    public void test_methodsCallInCascadeWithEquals()
            throws ExpressionException {
        Expression expression =
            new Expression("Valeur.substring(5,10).endsWith(\"hijcc\") == false",
                Types.VARCHAR, null, Types.BIT);

        Boolean result = (Boolean)expression.compute("abcdefghijkl");
        assertEquals(Boolean.TRUE, result);
    }
}
