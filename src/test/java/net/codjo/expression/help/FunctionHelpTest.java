/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression.help;
import junit.framework.TestCase;
/**
 * Classe de test de {@link net.codjo.expression.help.FunctionHelp}.
 */
public class FunctionHelpTest extends TestCase {
    public void test_equals() throws Exception {
        FunctionHelp help = new FunctionHelp("foo", 0, "my help");
        assertTrue(help.equals(new FunctionHelp("foo", 0, "my help")));
        assertEquals(help.hashCode(), new FunctionHelp("foo", 0, "my help").hashCode());
        assertEquals("FunctionHelp{parameterNumber=0, functionName='foo', help='my help'}",
            help.toString());

        assertFalse(help.equals(new FunctionHelp("bar", 0, "my help")));
        assertFalse(help.equals(new FunctionHelp("foo", 1, "my help")));
        assertFalse(help.equals(new FunctionHelp("foo", 0, "other help")));
    }


    public void test_equals_notFunctionHelp() throws Exception {
        FunctionHelp help = new FunctionHelp("foo", 0, "my help");
        assertFalse(help.equals(null));
        assertFalse(help.equals("pas une fonction"));
    }
}
