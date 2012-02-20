/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.sql.Types;
import junit.framework.TestCase;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.internal.matchers.StringContains.containsString;
/**
 * Classe permettant de tester UserFunctionHolderInfo.
 */
public class UserFunctionHolderInfoTest extends TestCase {
    private UserFunctionHolderInfo userFunctionHolderInfo;


    public void test_returnSqlType() {
        assertEquals(Types.VARCHAR, userFunctionHolderInfo.getReturnSqlType("doStuff", null).intValue());
        assertEquals(Types.INTEGER, userFunctionHolderInfo.getReturnSqlType("doDodidoo", null).intValue());
    }


    public void test_shouldDefine() {
        assertFalse(userFunctionHolderInfo.shouldDefineVariable());
    }


    public void test_buildFieldExpression() {
        String expression = userFunctionHolderInfo.buildDefinition();
        assertStartWith(expression, "public class user{");
        assertBodyContains(expression, "doStuff(foobar){body}");
        assertBodyContains(expression, "public int helloWorld() {System.out.print(\"Hello World!\";return 0;}");
        assertEndsWith(expression, "}");
    }


    @Override
    protected void setUp() throws Exception {
        UserFunctionHolder holder = new UserFunctionHolder("user");
        holder.addFunction(Types.VARCHAR, "doStuff", "doStuff(foobar)", "doStuff(foobar){body}");
        holder.addFunction(Types.INTEGER, "doDodidoo", "doDodidoo(foobar)", "public int helloWorld() {System.out.print(\"Hello World!\";return 0;}");
        userFunctionHolderInfo = new UserFunctionHolderInfo(holder);
    }


    private static void assertBodyContains(String expression, String substring) {
        assertThat(expression, containsString(substring));
    }


    private static void assertEndsWith(String expression, String value) {
        assertThat(expression.substring(expression.length() - value.length()), is(value));
    }


    private static void assertStartWith(String expression, String expected) {
        assertThat(expression.substring(0, expected.length()), is(expected));
    }
}
