/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression.help;
import net.codjo.expression.UserFunctionHolder;
import java.sql.Types;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import junit.framework.TestCase;
/**
 * Classe de test de {@link net.codjo.expression.help.UserFunctionHolderHelp}.
 */
public class UserFunctionHolderHelpTest extends TestCase {
    public void test_getEmptyFunction() throws Exception {
        UserFunctionHolderHelp userFH =
            new UserFunctionHolderHelp(new UserFunctionHolder("user"));
        assertEquals(0, userFH.getFunctionHelpList().size());
    }


    public void test_getAllFunction() throws Exception {
        UserFunctionHolder userHolder = new UserFunctionHolder("user");
        userHolder.addFunction(Types.BIT, "afoo", "Usage : mon aide",
            "public boolean afoo()...");
        userHolder.addFunction(Types.BIT, "bar", "Usage : mon aide de bar",
            "public boolean bar(int a, String b)...");
        UserFunctionHolderHelp userFH = new UserFunctionHolderHelp(userHolder);

        List functionHelpList = userFH.getFunctionHelpList();
        sortByFunctionName(functionHelpList);
        assertEquals(2, functionHelpList.size());

        FunctionHelp funcHelp = (FunctionHelp)functionHelpList.get(0);
        assertEquals("user.afoo", funcHelp.getFunctionName());
        assertEquals("Usage : mon aide", funcHelp.getHelp());
        assertEquals(0, funcHelp.getParameterNumber());

        funcHelp = (FunctionHelp)functionHelpList.get(1);
        assertEquals("user.bar", funcHelp.getFunctionName());
        assertEquals("Usage : mon aide de bar", funcHelp.getHelp());
        assertEquals(2, funcHelp.getParameterNumber());
    }


    private void sortByFunctionName(List functionHelpList) {
        Collections.sort(functionHelpList,
            new Comparator() {
                public int compare(Object first, Object second) {
                    return ((FunctionHelp)first).getFunctionName().compareTo(((FunctionHelp)second)
                        .getFunctionName());
                }
            });
    }
}
