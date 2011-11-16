/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.math.BigDecimal;
import static java.sql.Types.BIT;
import static java.sql.Types.CHAR;
import static java.sql.Types.DATE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
/**
 * Class test de <code>Interpreter</code> .
 */
public class InterpreterTest extends TestCase {
    Interpreter parser;
    Map<String, Integer> variables;


    public InterpreterTest(String name) {
        super(name);
    }


    public void test_constante() {
        parser.addVariable("A", INTEGER);
        assertEquals(parser.convert("15"), "15");
        assertEquals(parser.convert("15.0123456789"), "15.0123456789");
        assertEquals(parser.convert("\"Ma cabanne au canada\""),
                     "\"Ma cabanne au canada\"");
        assertEquals(parser.convert("A"), "A");
    }


    public void test_assignement_Integer() {
        parser.addVariable("A", INTEGER);
        parser.addVariable("B", INTEGER);
        parser.addVariable("C", NUMERIC);
        assertEquals(parser.convert("A=15"), "A=15");
        assertEquals(parser.convert("A=B"), "A=B");
        assertEquals(parser.convert("A=C"), "A=(C).intValue()");
    }


    public void test_assignement_Float() {
        parser.addVariable("A", FLOAT);
        parser.addVariable("B", FLOAT);
        parser.addVariable("C", NUMERIC);
        assertEquals(parser.convert("A=15"), "A=15");
        assertEquals(parser.convert("A=B"), "A=B");
        assertEquals(parser.convert("A=C"), "A=(C).doubleValue()");
    }


    public void test_assignement_Date() {
        parser.addVariable("A", TIMESTAMP);
        parser.addVariable("B", TIMESTAMP);
        assertEquals(parser.convert("A=\"1900-01-01\""),
                     "A=(java.sql.Date.valueOf(\"1900-01-01\"))");
        assertEquals(parser.convert("A=B"), "A=B");
    }


    public void test_assignement_BigDecimal() {
        parser.addVariable("A", NUMERIC);
        parser.addVariable("B", NUMERIC);
        assertEquals(parser.convert("A=15"), "A=new BigDecimal(15)");
        assertEquals(parser.convert("A=B"), "A=B");
        assertEquals(parser.convert("A=null"), "A=null");
    }


    public void test_compare_Scalaire() {
        parser.addVariable("A", INTEGER);
        assertEquals(parser.convert("A == 1"), "(A)==(1)");
        assertEquals(parser.convert("A == null"), "(A)==(null)");
        assertEquals(parser.convert("A >= 1"), "(A)>=(1)");
        assertEquals(parser.convert("A > 1"), "(A)>(1)");
        assertEquals(parser.convert("A <= 1"), "(A)<=(1)");
        assertEquals(parser.convert("A < 1"), "(A)<(1)");
    }


    public void test_compare_Char() {
        parser.addVariable("A", CHAR);
        assertEquals(parser.convert("A == \"blabla\""), "(A).compareTo(\"blabla\")==0");
    }


    public void test_compare_Object() {
        parser.addVariable("A", TIMESTAMP);
        parser.addVariable("B", TIMESTAMP);
        assertEquals(parser.convert("A == B"), "(A).compareTo(B)==0");
        assertEquals(parser.convert("A >= B"), "(A).compareTo(B)>=0");
        assertEquals(parser.convert("A > B"), "(A).compareTo(B)>0");
        assertEquals(parser.convert("A <= B"), "(A).compareTo(B)<=0");
        assertEquals(parser.convert("A < B"), "(A).compareTo(B)<0");
        assertEquals(parser.convert("A == \"1900-01-01\""),
                     "(A).compareTo((java.sql.Date.valueOf(\"1900-01-01\")))==0");
        assertEquals(parser.convert("A >= \"1900-01-01\""),
                     "(A).compareTo((java.sql.Date.valueOf(\"1900-01-01\")))>=0");
        assertEquals(parser.convert("A <= \"1900-01-01\""),
                     "(A).compareTo((java.sql.Date.valueOf(\"1900-01-01\")))<=0");
        assertEquals(parser.convert("A < \"1900-01-01\""),
                     "(A).compareTo((java.sql.Date.valueOf(\"1900-01-01\")))<0");
    }


    public void test_compare_BigDecimal() {
        parser.addVariable("A", NUMERIC);
        parser.addVariable("B", NUMERIC);
        assertEquals(parser.convert("A == B"), "(A).compareTo(B)==0");
        assertEquals(parser.convert("A == 1"), "(A).compareTo(new BigDecimal(1))==0");
        assertEquals(parser.convert("A >= 1"), "(A).compareTo(new BigDecimal(1))>=0");
        assertEquals(parser.convert("A > 1"), "(A).compareTo(new BigDecimal(1))>0");
        assertEquals(parser.convert("A <= 1"), "(A).compareTo(new BigDecimal(1))<=0");
        assertEquals(parser.convert("A < 1"), "(A).compareTo(new BigDecimal(1))<0");
    }


    public void test_operateurBoolean() {
        parser.addVariable("A", BIT);
        parser.addVariable("B", BIT);
        assertEquals(parser.convert("A && B"), "(A)&&(B)");
        assertEquals(parser.convert("A || B"), "(A)||(B)");
    }


    public void test_math_Scalaire() {
        parser.addVariable("A", INTEGER);
        parser.addVariable("B", INTEGER);
        assertEquals(parser.convert("A + B"), "(A)+(B)");
        assertEquals(parser.convert("A + 1"), "(A)+(1)");
        assertEquals(parser.convert("A * B"), "(A)*(B)");
        assertEquals(parser.convert("A - B"), "(A)-(B)");
        assertEquals(parser.convert("A / B"), "(A)/(B)");
    }


    public void test_math_String() {
        parser.addVariable("A", VARCHAR);
        parser.addVariable("B", VARCHAR);
        assertEquals(parser.convert("A + B"), "(A)+(B)");
    }


    public void test_math_BigDecimal() {
        parser.addVariable("A", NUMERIC);
        parser.addVariable("B", NUMERIC);
        assertEquals(parser.convert("A + B"), "(A).add(B)");
        assertEquals(parser.convert("A + 1"), "(A).add(new BigDecimal(1))");
        assertEquals(parser.convert("A * B"), "(A).multiply(B)");
        assertEquals(parser.convert("A - B"), "(A).subtract(B)");
        assertEquals(parser.convert("A / B"),
                     "(A).divide(B ,10 ,BigDecimal.ROUND_HALF_UP)");
    }


    public void test_unaryOperator_Scalaire() {
        parser.addVariable("A", INTEGER);
        assertEquals(parser.convert("- A"), "-(A)");
    }


    public void test_unaryOperator_BigDecimal() {
        parser.addVariable("A", NUMERIC);
        assertEquals(parser.convert("- A"), "(A).negate()");
    }


    public void test_add_multiply() {
        parser.addVariable("A", NUMERIC);
        parser.addVariable("B", NUMERIC);
        parser.addVariable("C", NUMERIC);
        assertEquals(parser.convert("A + B * C"), "(A).add((B).multiply(C))");
    }


    public void test_method_Math_SCALAR() {
        parser.addVariable("A", INTEGER);
        parser.addVariable("B", INTEGER);
        assertEquals(parser.convert("Math.abs(A)"), "Math.abs(A)");
        assertEquals(parser.convert("Math.min(A, B)"), "Math.min(A,B)");
        assertEquals(parser.convert("Math.max(A, B)"), "Math.max(A,B)");
    }


    public void test_method_Math_BigDecimal() {
        parser.addVariable("A", NUMERIC);
        parser.addVariable("B", NUMERIC);
        assertEquals(parser.convert("Math.abs(A)"), "A.abs()");
        assertEquals(parser.convert("Math.min(A, B)"), "A.min(B)");
        assertEquals(parser.convert("Math.max(A, B)"), "A.max(B)");
    }


    public void test_method() {
        parser.defineObjectMethods(new DefaultFunctionHolderInfo(new ClassForMethodTest()),
                                   "essai");
        parser.addVariable("A", NUMERIC);

        assertEquals(parser.convert("essai.getAnInt()"), "essai.getAnInt()");

        assertEquals(parser.convert("essai.getToto(A, \"e\")"), "essai.getToto(A, \"e\")");
    }


    public void test_method_Foo() {
        parser.defineObjectMethods(new DefaultFunctionHolderInfo(new ClassForMethodTest()),
                                   "essai");
        parser.addVariable("A", NUMERIC);

        assertEquals(parser.convert("essai.getFoo(A) * A"),
                     "(essai.getFoo(A)).multiply(A)");
        assertEquals(parser.convert("essai.getFoo(\"a\") + \"a\""),
                     "(essai.getFoo(\"a\"))+(\"a\")");
    }


    public void test_method_string() {
        parser.addVariable("A", VARCHAR);
        assertEquals(parser.convert("A.toString()"), "A.toString()");
    }


    public void test_method_Unknown() {
        parser.addVariable("A", VARCHAR);
        try {
            parser.convert("A.laMethodeAbobo(\"e\")");
            fail("La methode est inconnue");
        }
        catch (RuntimeException error) {
            ; // OK
        }
    }


    public void test_method_Unsupported() {
        parser.addVariable("A", VARCHAR);
        try {
            parser.convert("A.getBytes()");
            fail("Le tableau de byte n'est pas supporte");
        }
        catch (RuntimeException error) {
            ; // OK
        }
    }


    public void test_iif() {
        parser.addVariable("A", INTEGER);
        parser.addVariable("B", TIMESTAMP);
        assertEquals(parser.convert("iif(A>5,A+1,-A)"), "(((A)>(5))?((A)+(1)):(-(A)))");
        assertEquals(parser.convert("iif(B==\"1900-01-01\",\"1900-01-01\",B)"),
                     "(((B).compareTo((java.sql.Date.valueOf(\"1900-01-01\")))==0)?"
                     + "((java.sql.Date.valueOf(\"1900-01-01\"))):(B))");
    }


    public void test_in() {
        parser.addVariable("field", VARCHAR);
        parser.addVariable("value1", VARCHAR);
        parser.addVariable("value2", VARCHAR);
        parser.defineObjectMethods(new DefaultFunctionHolderInfo(new ClassForMethodTest()),
                                   "essai");
        assertEquals(parser.convert("in(field,value1,value2)"),
                     "(field).compareTo(value1) == 0 || (field).compareTo(value2) == 0");
        assertEquals(parser.convert("in(essai.getString()==\"a string\",value1,value2)"),
                     "((essai.getString()).compareTo(\"a string\")==0).compareTo(value1) == 0 "
                     + "|| ((essai.getString()).compareTo(\"a string\")==0).compareTo(value2) == 0");
    }


    public void test_notIn() {
        parser.addVariable("field", VARCHAR);
        parser.addVariable("value1", VARCHAR);
        parser.addVariable("value2", VARCHAR);
        parser.defineObjectMethods(new DefaultFunctionHolderInfo(new ClassForMethodTest()),
                                   "essai");
        assertEquals(parser.convert("notIn(field,value1,value2)"),
                     "(field).compareTo(value1) != 0 && (field).compareTo(value2) != 0");
        assertEquals(parser.convert(
              "notIn(essai.getString()==\"a string\",value1,value2)"),
                     "((essai.getString()).compareTo(\"a string\")==0).compareTo(value1) != 0 "
                     + "&& ((essai.getString()).compareTo(\"a string\")==0).compareTo(value2) != 0");
    }


    public void test_function_add() {
        parser.addVariable("A", NUMERIC);
        parser.defineObjectMethods(new DefaultFunctionHolderInfo(new ClassForMethodTest()),
                                   "essai");
        assertEquals(parser.convert("essai.getBigDecimal() + A"),
                     "(essai.getBigDecimal()).add(A)");
    }


    public void test_array() {
        parser.addVariable("A", VARCHAR);
        assertEquals(parser.convert("new Object[]{A, \"bobo\"}"),
                     "new Object[]{A, \"bobo\"}");
    }


    public void test_array_function() {
        parser.addVariable("A", VARCHAR);
        parser.defineObjectMethods(new DefaultFunctionHolderInfo(new ClassForMethodTest()),
                                   "essai");

        assertEquals(parser.convert("essai.caseof(new Object[]{A, \"bobo\"})"),
                     "essai.caseof(new Object[]{A, \"bobo\"})");
    }


    public void test_array_function_boolean() {
        parser.addVariable("A", BIT);
        parser.defineObjectMethods(new DefaultFunctionHolderInfo(new ClassForMethodTest()),
                                   "essai");

        assertEquals(parser.convert(
              "essai.caseof(new boolean[]{A}, new String[] {\"BB\"})"),
                     "essai.caseof(new boolean[]{A}, new String[]{\"BB\"})");
    }


    public void test_array_function_BooleanConstructor() {
        parser.addVariable("A", VARCHAR);
        parser.defineObjectMethods(new DefaultFunctionHolderInfo(new ClassForMethodTest()),
                                   "essai");
        assertEquals(parser.convert(
              "essai.caseof(new Object[]{new Boolean(A==\"B\"), \"bobo\"})"),
                     "essai.caseof(new Object[]{new Boolean((A).compareTo(\"B\")==0), \"bobo\"})");
    }


    public void test_array_function_BooleanConstructorWithFunction() {
        parser.addVariable("A", VARCHAR);
        parser.defineObjectMethods(new DefaultFunctionHolderInfo(new ClassForMethodTest()),
                                   "essai");
        assertEquals(parser.convert(
              "essai.caseof(new Object[]{new Boolean(essai.getString()==\"a string\"), \"bobo\"})"),
                     "essai.caseof(new Object[]{new Boolean((essai.getString()).compareTo(\"a string\")==0), \"bobo\"})");
    }


    @Override
    protected void setUp() {
        variables = new HashMap<String, Integer>();
        variables.put("DATE", DATE);
        variables.put("INT", INTEGER);
        variables.put("NUMERIC", NUMERIC);
        variables.put("STRING", VARCHAR);

        parser = new Interpreter();
        parser.setVariables(variables);
    }


    public static class ClassForMethodTest implements FunctionHolder {
        public int getAnInt() {
            return 0;
        }


        public String getString() {
            return "a string";
        }


        public BigDecimal getBigDecimal() {
            return null;
        }


        public BigDecimal getToto(BigDecimal va, String vs) {
            return null;
        }


        public BigDecimal getFoo(BigDecimal va) {
            return va;
        }


        public String getFoo(String va) {
            return va;
        }


        public String caseof(Object[][] args) {
            return "";
        }


        public String caseof(boolean[] args, String[] argbis) {
            return "";
        }


        public void methodToForget() {
        }


        public String getName() {
            return null;
        }


        public List<String> getAllFunctions() {
            return null;
        }
    }
}
