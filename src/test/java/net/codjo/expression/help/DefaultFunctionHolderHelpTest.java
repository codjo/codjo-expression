/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression.help;
import net.codjo.expression.FunctionHolder;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
/**
 * Classe de test de {@link net.codjo.expression.help.DefaultFunctionHolderHelp}.
 */
public class DefaultFunctionHolderHelpTest extends TestCase {
    public void test_emptyFunctionHolder() throws Exception {
        DefaultFunctionHolderHelp helper =
            new DefaultFunctionHolderHelp(new EmptyFunctionHolder());
        List functionHelperList = helper.getFunctionHelpList();
        assertEquals(0, functionHelperList.size());
    }


    public void test_getSimpleFunction() throws Exception {
        DefaultFunctionHolderHelp helper =
            new DefaultFunctionHolderHelp(new SimpleFunctionHolder());
        List functionHelperList = helper.getFunctionHelpList();
        assertEquals(1, functionHelperList.size());

        FunctionHelp funcHelp = (FunctionHelp)functionHelperList.get(0);
        assertEquals(0, funcHelp.getParameterNumber());
        assertEquals("utils.foo", funcHelp.getFunctionName());
        assertEquals("Usage : utils.foo()", funcHelp.getHelp());
    }


    public void test_complexeFunctionHolder() throws Exception {
        DefaultFunctionHolderHelp helper =
            new DefaultFunctionHolderHelp(new ComplexeFunctionHolder());
        List functionHelperList = helper.getFunctionHelpList();
        assertEquals(1, functionHelperList.size());

        FunctionHelp funcHelp = (FunctionHelp)functionHelperList.get(0);
        assertEquals(6, funcHelp.getParameterNumber());
        assertEquals("utils.bar", funcHelp.getFunctionName());
        assertEquals("Usage : utils.bar(chaîne, entier, nombre, date, variable, booléen)",
            funcHelp.getHelp());
    }

    public static class ComplexeFunctionHolder extends EmptyFunctionHolder {
        public void bar(String myValue, int myNumber, BigDecimal big, Date maDate,
            FunctionHolder nawak, boolean value) {}
    }


    public static class SimpleFunctionHolder extends EmptyFunctionHolder {
        public void foo() {}
    }


    public static class EmptyFunctionHolder implements FunctionHolder {
        public List<String> getAllFunctions() {
            return null;
        }


        public String getName() {
            return "utils";
        }
    }
}
