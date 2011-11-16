/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.sql.Types;
import junit.framework.TestCase;
/**
 * Classe permettant de tester UserFunctionHolderInfo.
 */
public class UserFunctionHolderInfoTest extends TestCase {
    private UserFunctionHolderInfo userFunctionHolderInfo;

    public void test_returnSqlType() {
        assertEquals(Types.VARCHAR,
            userFunctionHolderInfo.getReturnSqlType("doStuff", null).intValue());
        assertEquals(Types.INTEGER,
            userFunctionHolderInfo.getReturnSqlType("doDodidoo", null).intValue());
    }


    public void test_shouldDefine() {
        assertFalse(userFunctionHolderInfo.shouldDefineVariable());
    }


    public void test_buildFieldExpression() {
        String expression = userFunctionHolderInfo.buildDefinition();
        final String expected =
            "public class user{doStuff(foobar){body}public int helloWorld() {System.out.print(\"Hello World!\";return 0;}}";

        assertTrue(expected.equals(expression));
    }


    protected void setUp() throws Exception {
        UserFunctionHolder holder = new UserFunctionHolder("user");
        holder.addFunction(Types.VARCHAR, "doStuff", "doStuff(foobar)",
            "doStuff(foobar){body}");
        holder.addFunction(Types.INTEGER, "doDodidoo", "doDodidoo(foobar)",
            "public int helloWorld() {System.out.print(\"Hello World!\";return 0;}");
        userFunctionHolderInfo = new UserFunctionHolderInfo(holder);
    }
}
