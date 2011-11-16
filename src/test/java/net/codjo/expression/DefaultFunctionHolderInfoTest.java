/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import net.codjo.expression.help.DefaultFunctionHolderHelp;
import net.codjo.expression.help.FunctionHelp;
import net.codjo.expression.help.FunctionHolderHelp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
/**
 * Classe permettant de tester la classe DefaultFunctionHolderInfo.
 */
public class DefaultFunctionHolderInfoTest extends TestCase {
    private DefaultFunctionHolderInfo info;


    public void test_returnSqlType() {
        List<Class> argsList = new ArrayList<Class>();
        argsList.add(String.class);

        assertEquals(Types.DATE, info.getReturnSqlType("lastDay", argsList).intValue());
    }


    public void test_help() {
        assertEquals(
              "Si le FunctionHolder n'implemente pas FunctionHolderHelp alors on utilise un comportement par défaut",
              new DefaultFunctionHolderHelp(info.getFunctionHolder()).getFunctionHelpList(),
              info.getHelp().getFunctionHelpList());

        info = new DefaultFunctionHolderInfo(new UtilsWithHelpTest());

        assertSame("Si le FunctionHolder implements FunctionHolderHelp alors on l'utilise",
                   info.getFunctionHolder(), info.getHelp());
    }


    public void test_shouldDefine() {
        assertTrue(info.shouldDefineVariable());
    }


    @Override
    protected void setUp() throws Exception {
        info = new DefaultFunctionHolderInfo(new UtilsForTest());
    }


    public static class UtilsForTest implements FunctionHolder {
        public List<String> getAllFunctions() {
            return null;
        }


        public String getName() {
            return "utils";
        }


        public java.sql.Date lastDay(String period)
              throws java.text.ParseException {
            if (period.startsWith("200002")) {
                return java.sql.Date.valueOf("2000-02-29");
            }
            throw new java.text.ParseException("", 0);
        }
    }

    public static class UtilsWithHelpTest extends UtilsForTest
          implements FunctionHolderHelp {
        public List<FunctionHelp> getFunctionHelpList() {
            return null;
        }
    }
}
