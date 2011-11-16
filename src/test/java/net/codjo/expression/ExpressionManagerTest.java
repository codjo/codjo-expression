/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
/**
 * Overview.
 *
 * @author $Author: palmont $
 * @version $Revision: 1.12 $
 */
public class ExpressionManagerTest extends TestCase {
    /*
     *  Source : A(int), B(int)
     *  Dest : A(int), F(int), D(date)
     *  expression : DEST_A = SRC_A + SRC_B (prio = 1)
     *  DEST_F = DEST_A + Math.abs(-5) (prio = 2)
     *  DEST_G = DEST_G + Math.abs(-5)
     *  DEST_D = utils.lastDay("200002xxxxx"),
     */
    private ExpressionManager expManager;
    private FunctionManager functionManager;


    public void test_compute() throws Throwable {
        expManager.setFieldSourceValue("A", 1);
        expManager.setFieldSourceValue("B", 2);

        expManager.compute();

        assertEquals(expManager.getComputedValue("VAR_A"), 5);
        assertEquals(expManager.getComputedValue("A"), 3);
        assertEquals(expManager.getComputedValue("F"), 8);
        assertEquals(expManager.getComputedValue("D"), java.sql.Date.valueOf("2000-02-29"));
    }


    public void test_compute_dateCompare() throws Throwable {
        expManager = new ExpressionManager(functionManager);

        Map<String, Integer> srcCol = new HashMap<String, Integer>();
        srcCol.put("A", Types.TIMESTAMP);
        expManager.setSourceColumn(srcCol);

        Map<String, Integer> destCol = new HashMap<String, Integer>();
        destCol.put("A", Types.BIT);
        expManager.setDestColumn(destCol);

        Map<String, Integer> varCol = new HashMap<String, Integer>();
        expManager.setVarColumn(varCol);

        expManager.add("A", "SRC_A >= \"2004-01-01\"");
        expManager.compileExpressions();

        expManager.setFieldSourceValue("A", Timestamp.valueOf("2000-01-01 00:00:00.0"));

        expManager.compute();

        assertEquals(false, expManager.getComputedValue("A"));
    }


    public void test_computeWithNumeric() throws Throwable {
        functionManager = new FunctionManager();
        functionManager.addFunctionHolder(new UtilsForTest());
        expManager = new ExpressionManager(functionManager);

        Map<String, Integer> srcCol = new HashMap<String, Integer>();
        srcCol.put("N", Types.NUMERIC);
        srcCol.put("BI", Types.BIGINT);
        expManager.setSourceColumn(srcCol);

        Map<String, Integer> destCol = new HashMap<String, Integer>();
        destCol.put("NN", Types.DOUBLE);
        destCol.put("F", Types.FLOAT);
        expManager.setDestColumn(destCol);

        expManager.add("NN", "SRC_N");
        expManager.add("F", "8");

        expManager.compileExpressions();
        expManager.setFieldSourceValue("N", new java.math.BigDecimal(8));
        expManager.setFieldSourceValue("BI", (long)2);

        expManager.compute();

        assertEquals((double)8, expManager.getComputedValue("NN"));
        assertEquals((float)8, expManager.getComputedValue("F"));
    }


    /**
     * Test le cas d'utilisation de fonctions définis par l'utilisateur.
     */
    public void test_compute_defined_functions() throws Throwable {
        // Init des fonctions
        UserFunctionHolder users = new UserFunctionHolder("users");
        users.addFunction(Types.INTEGER, "mySquare", "mySquare(int)",
                          "public static int mySquare(int i) { return i * i; }");
        functionManager.addFunctionHolder(users);

        expManager = new ExpressionManager(functionManager);

        Map<String, Integer> srcCol = new HashMap<String, Integer>();
        srcCol.put("TOTO$A", Types.INTEGER);
        expManager.setSourceColumn(srcCol);

        Map<String, Integer> destCol = new HashMap<String, Integer>();
        destCol.put("A", Types.INTEGER);
        expManager.setDestColumn(destCol);

        Map<String, Integer> varCol = new HashMap<String, Integer>();
        varCol.put("VAR_A", Types.INTEGER);
        expManager.setVarColumn(varCol);

        expManager.add("A", "users.mySquare(SRC_TOTO$A) + 5");

        // Init valeur
        expManager.compileExpressions();
        expManager.setFieldSourceValue("TOTO$A", 2);

        // Calcul
        expManager.compute();

        // Assert
        assertEquals(expManager.getComputedValue("A"), 9);
    }


    public void test_compile_error() {
        expManager = new ExpressionManager(functionManager);
        expManager.setSourceColumn(new HashMap<String, Integer>());
        Map<String, Integer> destCol = new HashMap<String, Integer>();
        destCol.put("ANOTHER_DATE", Types.DATE);
        expManager.setDestColumn(destCol);
        expManager.add("ANOTHER_DATE", "isNull(45==2)");

        try {
            expManager.compileExpressions();
            fail();
        }
        catch (InvalidExpressionException e) {
        }
    }


    public void test_compute_Error() throws Throwable {
        // Init
        expManager = new ExpressionManager(functionManager);

        Map<String, Integer> srcCol = new HashMap<String, Integer>();
        srcCol.put("A_DATE", Types.VARCHAR);
        expManager.setSourceColumn(srcCol);

        Map<String, Integer> destCol = new HashMap<String, Integer>();
        destCol.put("ANOTHER_DATE", Types.DATE);
        expManager.setDestColumn(destCol);

        Map varCol = new HashMap();
        expManager.setVarColumn(varCol);

        expManager.add("ANOTHER_DATE", "utils.lastDay(SRC_A_DATE)");
        expManager.compileExpressions();

        // Test pour de vrai
        expManager.setFieldSourceValue("A_DATE", "Je ne suis pas une date");

        try {
            expManager.compute();
            fail("Exception non lancee");
        }
        catch (ExpressionException e) {
            assertEquals("getNbError", e.getNbError(), 1);
            assertTrue("ParseException : " + e.getException(0).getClass(),
                       e.getException(0) instanceof java.text.ParseException);
        }
    }


    public void test_compute_RowByRow() throws Throwable {
        expManager.setFieldSourceValue("A", 1);
        expManager.setFieldSourceValue("B", 2);

        expManager.compute();
        expManager.clear();

        expManager.setFieldSourceValue("A", 3);
        expManager.setFieldSourceValue("B", 4);

        expManager.compute();

        assertEquals(expManager.getComputedValue("A"), 7);
        assertEquals(expManager.getComputedValue("F"), 12);
        assertEquals(expManager.getComputedValue("D"), java.sql.Date.valueOf("2000-02-29"));
    }


    public void test_compute_Sum() throws Throwable {
        expManager.setFieldSourceValue("A", 1);
        expManager.setFieldSourceValue("B", 2);

        expManager.compute();

        expManager.setFieldSourceValue("A", 3);
        expManager.setFieldSourceValue("B", 4);

        expManager.compute();

        assertEquals(expManager.getComputedValue("A"), 7);
        assertEquals(expManager.getComputedValue("F"), 12);
        assertEquals(expManager.getComputedValue("G"), 10);
        assertEquals(expManager.getComputedValue("D"), java.sql.Date.valueOf("2000-02-29"));
    }


    public void test_compute_without_prefix() throws Throwable {
        functionManager = new FunctionManager();
        functionManager.addFunctionHolder(new UtilsForTest());
        expManager = new ExpressionManager(functionManager, "", "");

        Map<String, Integer> srcCol = new HashMap<String, Integer>();
        srcCol.put("A_in", Types.INTEGER);
        srcCol.put("B_in", Types.INTEGER);
        expManager.setSourceColumn(srcCol);

        Map<String, Integer> destCol = new HashMap<String, Integer>();
        destCol.put("A_out", Types.INTEGER);
        expManager.setDestColumn(destCol);

        Map<String, Integer> varCol = new HashMap<String, Integer>();
        varCol.put("VAR_A", Types.INTEGER);
        expManager.setVarColumn(varCol);

        expManager.add("VAR_A", "A_in + B_in + B_in");
        expManager.add("A_out", "VAR_A - B_in");

        expManager.compileExpressions();

        expManager.setFieldSourceValue("A_in", 1);
        expManager.setFieldSourceValue("B_in", 2);

        expManager.compute();

        assertEquals(expManager.getComputedValue("VAR_A"), 5);
        assertEquals(expManager.getComputedValue("A_out"), 3);
    }


    public void test_getDestFieldSQLType() throws Exception {
        assertEquals(java.sql.Types.INTEGER, expManager.getDestFieldSQLType("A"));
        assertEquals(java.sql.Types.DATE, expManager.getDestFieldSQLType("D"));
    }


    public void test_isDestFieldNumeric() throws Exception {
        assertEquals(true, expManager.isDestFieldNumeric("A"));
        assertEquals(false, expManager.isDestFieldNumeric("D"));
    }


    public void test_setFieldSourceValue_NullValue_Numeric()
          throws Throwable {
        expManager.setFieldSourceValue("A", null);
        expManager.setFieldSourceValue("B", 1);

        expManager.compute();

        assertEquals(expManager.getComputedValue("A"), 1);
    }


    public void test_setFieldSourceValue_NullValue_Object()
          throws Throwable {
        // Init
        expManager = new ExpressionManager(functionManager);

        Map<String, Integer> srcCol = new HashMap<String, Integer>();
        srcCol.put("A_STR", Types.VARCHAR);
        expManager.setSourceColumn(srcCol);

        Map<String, Integer> destCol = new HashMap<String, Integer>();
        destCol.put("ANOTHER_STR", Types.VARCHAR);
        expManager.setDestColumn(destCol);

        Map varCol = new HashMap();
        expManager.setVarColumn(varCol);

        expManager.add("ANOTHER_STR", "SRC_A_STR");
        expManager.compileExpressions();

        // Test pour de vrai
        expManager.setFieldSourceValue("A_STR", "");

        expManager.compute();

        assertEquals(expManager.getComputedValue("ANOTHER_STR"), "");
    }


    public void test_setDefaultNullValue() throws Throwable {
        assertDefaultDateValue(Date.valueOf("1973-03-18"));
        assertDefaultDateValue(null);
    }


    private void assertDefaultDateValue(Date defaultDateValue)
          throws InvalidExpressionException, ExpressionException {
        // Init
        expManager = new ExpressionManager(functionManager);

        Map<String, Integer> srcCol = new HashMap<String, Integer>();
        srcCol.put("DATE_A", Types.DATE);
        expManager.setSourceColumn(srcCol);

        Map<String, Integer> destCol = new HashMap<String, Integer>();
        destCol.put("DATE_B", Types.DATE);
        expManager.setDestColumn(destCol);

        expManager.add("DATE_B", "iif( 5 > 2, SRC_DATE_A, \"2004-01-01\")");
        expManager.compileExpressions();

        // Positionne la valeur par défaut pour le type Types.DATE
        expManager.setDefaultNullValue(Types.DATE, defaultDateValue);

        // Test pour de vrai
        expManager.setFieldSourceValue("DATE_A", null);

        expManager.compute();

        assertEquals(defaultDateValue, expManager.getComputedValue("DATE_B"));
    }


    public void test_iifOperator() throws ExpressionException {
        expManager.setFieldSourceValue("C", "*");

        expManager.compute();

        assertNull(expManager.getComputedValue("E"));

        expManager.setFieldSourceValue("C", "truc");

        expManager.compute();

        assertEquals("truc", expManager.getComputedValue("E"));
    }


    public void test_lastDate() throws Throwable {
        // Init
        functionManager = new FunctionManager();
        functionManager.addFunctionHolder(new UtilsForTest());
        expManager = new ExpressionManager(functionManager);
        Map srcCol = new HashMap();
        expManager.setSourceColumn(srcCol);

        Map<String, Integer> destCol = new HashMap<String, Integer>();
        destCol.put("D", Types.DATE);
        expManager.setDestColumn(destCol);

        expManager.add("D", "utils.lastDay(\"200002xxxxx\")");

        expManager.compileExpressions();
        expManager.compute();

        assertEquals(expManager.getComputedValue("D"), java.sql.Date.valueOf("2000-02-29"));
    }


    public void test_treateIsNullMethod() {
        String expression;
        Map<String, Integer> varCol;
        List expVarHavIsNullList;

        String expressionExpected;
        Map<String, Integer> varColExpected;
        List<String> expVarHavIsNullListExpected;

        // Test 1
        expression = "isNull(SRC_A)";
        varCol = new HashMap<String, Integer>();
        expVarHavIsNullList = new ArrayList();

        expressionExpected = "(VAR_SRC_A_ISNULL==true)";
        varColExpected = new HashMap<String, Integer>();
        varColExpected.put("VAR_SRC_A_ISNULL", Types.BIT);
        expVarHavIsNullListExpected = new ArrayList<String>();
        expVarHavIsNullListExpected.add("SRC_A");

        expManager.setVarColumn(varCol);
        expManager.setSrcFieldHavingIsNullList(expVarHavIsNullList);
        expression = expManager.treateIsNullMethod(expression);

        assertEquals(expressionExpected, expression);
        assertEquals(varColExpected, varCol);
        assertEquals(expVarHavIsNullListExpected, expVarHavIsNullList);

        // Test 2
        expression = "iif(  isNull (   SRC_A    )  , isNotNull(SRC_B   )  , isNull (  SRC_A))";
        varCol = new HashMap<String, Integer>();
        expVarHavIsNullList = new ArrayList();

        expressionExpected
              = "iif(  (VAR_SRC_A_ISNULL==true)  , (VAR_SRC_B_ISNULL==false)  , (VAR_SRC_A_ISNULL==true))";
        varColExpected = new HashMap<String, Integer>();
        varColExpected.put("VAR_SRC_A_ISNULL", Types.BIT);
        varColExpected.put("VAR_SRC_B_ISNULL", Types.BIT);
        expVarHavIsNullListExpected = new ArrayList<String>();
        expVarHavIsNullListExpected.add("SRC_A");
        expVarHavIsNullListExpected.add("SRC_B");

        expManager.setVarColumn(varCol);
        expManager.setSrcFieldHavingIsNullList(expVarHavIsNullList);
        expression = expManager.treateIsNullMethod(expression);

        assertEquals(expressionExpected, expression);
        assertEquals(varColExpected, varCol);
        assertEquals(expVarHavIsNullListExpected, expVarHavIsNullList);

        // Test 3
        expression = "isNull(VAR_A)";
        varCol = new HashMap<String, Integer>();
        varCol.put("VAR_A", Types.INTEGER);
        expVarHavIsNullList = new ArrayList();

        expressionExpected = "(kernel.isObjectNull(VAR_A)==true)";
        varColExpected = new HashMap<String, Integer>();
        varColExpected.put("VAR_A", Types.INTEGER);
        expVarHavIsNullListExpected = new ArrayList<String>();

        expManager.setVarColumn(varCol);
        expManager.setSrcFieldHavingIsNullList(expVarHavIsNullList);
        expression = expManager.treateIsNullMethod(expression);

        assertEquals(expressionExpected, expression);
        assertEquals(varColExpected, varCol);
        assertEquals(expVarHavIsNullListExpected, expVarHavIsNullList);
    }


    public void test_compute_isNull() throws Exception {
        // Test avec isNull(A) true
        expManager.setFieldSourceValue("A", null);
        expManager.setFieldSourceValue("B", 2);
        expManager.setFieldSourceValue("C", "*");

        expManager.compute();

        assertEquals(expManager.getComputedValue("H"), Boolean.TRUE);
        assertEquals(expManager.getComputedValue("I"), Boolean.FALSE);

        // Test avec isNull(A) false
        expManager.setFieldSourceValue("A", 2);
        expManager.setFieldSourceValue("B", 2);
        expManager.setFieldSourceValue("C", "*");

        expManager.compute();

        assertEquals(expManager.getComputedValue("H"), Boolean.FALSE);
        assertEquals(expManager.getComputedValue("I"), Boolean.TRUE);

        // Test avec isNull(VAR_B) et isNull(VAR_C)
        expManager.setFieldSourceValue("A", 2);
        expManager.setFieldSourceValue("B", 2);
        expManager.setFieldSourceValue("C", "*");

        expManager.compute();

        assertEquals(expManager.getComputedValue("J"), Boolean.FALSE);
        assertEquals(expManager.getComputedValue("K"), Boolean.TRUE);
        assertEquals(expManager.getComputedValue("L"), Boolean.TRUE);
        assertEquals(expManager.getComputedValue("M"), Boolean.FALSE);
    }


    @Override
    protected void setUp() throws InvalidExpressionException {
        functionManager = new FunctionManager();
        functionManager.addFunctionHolder(new UtilsForTest());
        expManager = new ExpressionManager(functionManager);

        Map<String, Integer> srcCol = new HashMap<String, Integer>();
        srcCol.put("A", Types.INTEGER);
        srcCol.put("B", Types.INTEGER);
        srcCol.put("C", Types.VARCHAR);
        expManager.setSourceColumn(srcCol);

        Map<String, Integer> destCol = new HashMap<String, Integer>();
        destCol.put("A", Types.INTEGER);
        destCol.put("F", Types.INTEGER);
        destCol.put("G", Types.INTEGER);
        destCol.put("D", Types.DATE);
        destCol.put("E", Types.VARCHAR);
        destCol.put("H", Types.BIT);
        destCol.put("I", Types.BIT);
        destCol.put("J", Types.BIT);
        destCol.put("K", Types.BIT);
        destCol.put("L", Types.BIT);
        destCol.put("M", Types.BIT);
        expManager.setDestColumn(destCol);

        Map<String, Integer> varCol = new HashMap<String, Integer>();
        varCol.put("VAR_A", Types.INTEGER);
        varCol.put("VAR_B", Types.DATE);
        varCol.put("VAR_C", Types.DATE);
        expManager.setVarColumn(varCol);

        expManager.add("VAR_A", "SRC_A + SRC_B + SRC_B");
        expManager.add("A", "VAR_A - SRC_B");
        expManager.add("F", "DEST_A + Math.abs(-5)");
        expManager.add("G", "DEST_G + Math.abs(-5)");
        expManager.add("D", "utils.lastDay(\"200002xxxxx\")");
        expManager.add("E", "iif(SRC_C == \"*\", null, SRC_C)");
        expManager.add("H", "isNull(SRC_A)");
        expManager.add("I", "isNotNull(SRC_A)");
        expManager.add("VAR_B", "\"2004-06-01\"");
        expManager.add("J", "isNull(VAR_B)");
        expManager.add("K", "isNotNull(VAR_B)");
        expManager.add("VAR_C", "null");
        expManager.add("L", "isNull(VAR_C)");
        expManager.add("M", "isNotNull(VAR_C)");
        expManager.compileExpressions();
    }


    public static class UtilsForTest implements FunctionHolder {
        public List getAllFunctions() {
            return null;
        }


        public String getName() {
            return "utils";
        }


        public java.sql.Date lastDay(String aDate)
              throws java.text.ParseException {
            if (aDate.startsWith("200002")) {
                return java.sql.Date.valueOf("2000-02-29");
            }
            throw new java.text.ParseException("", 0);
        }
    }
}
