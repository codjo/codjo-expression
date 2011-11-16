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
import junit.framework.TestCase;
/**
 * Test de la classe <code>UserFunctionHolder</code>.
 *
 * @version $Revision: 1.3 $
 */
public class UserFunctionHolderTest extends TestCase {
    private UserFunctionHolder holder;

    public void test_getName() throws Exception {
        assertEquals("user", holder.getName());
    }


    public void test_getAllFunctions() throws Exception {
        holder.addFunction(Types.VARCHAR, "doStuff", "doStuff(foobar)", "body");
        holder.addFunction(Types.VARCHAR, "doDodidoo", "doDodidoo(foobar)", "bodyx");

        List func = holder.getAllFunctions();

        assertEquals(2, func.size());
        assertTrue("contient 'doStuff(foobar)'", func.contains("doStuff(foobar)"));
        assertTrue("contient 'doDodidoo(foobar)'", func.contains("doDodidoo(foobar)"));
    }


    public void test_getReturnType() throws Exception {
        holder.addFunction(Types.VARCHAR, "doStuff", "doStuff(foobar)", "body");
        holder.addFunction(Types.INTEGER, "doDodidoo", "doDodidoo(foobar)", "bodyx");

        assertEquals(Types.VARCHAR, holder.getReturnSqlType("doStuff").intValue());
        assertEquals(Types.INTEGER, holder.getReturnSqlType("doDodidoo").intValue());
    }


    public void test_functionsCode() throws Exception {
        holder.addFunction(Types.VARCHAR, "doStuff", "doStuff(foobar)", "body");
        holder.addFunction(Types.INTEGER, "doDodidoo", "doDodidoo(foobar)", "bodyx");

        Iterator iter = holder.functionsCode();

        List allCodes = new ArrayList();
        while (iter.hasNext()) {
            String code = (String)iter.next();
            allCodes.add(code);
        }
        assertTrue("contient 'body'", allCodes.contains("body"));
        assertTrue("contient 'bodyx'", allCodes.contains("bodyx"));
    }


    public void test_removeFunctionsCode() throws Exception {
        holder.addFunction(Types.VARCHAR, "doStuff", "doStuff(foobar)", "body");
        holder.addFunction(Types.INTEGER, "doDodidoo", "doDodidoo(foobar)", "bodyx");

        Iterator iter = holder.functionsCode();

        List allCodes = new ArrayList();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
        assertFalse("contient 'body'", allCodes.contains("body"));
        assertFalse("contient 'bodyx'", allCodes.contains("bodyx"));
    }


    protected void setUp() throws Exception {
        holder = new UserFunctionHolder("user");
    }
}
