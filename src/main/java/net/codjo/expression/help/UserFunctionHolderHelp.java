/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression.help;
import net.codjo.expression.UserFunctionHolder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
/**
 * Aide par défaut sur un {@link net.codjo.expression.UserFunctionHolder}.
 */
public class UserFunctionHolderHelp implements FunctionHolderHelp {
    private List<FunctionHelp> functionList = new ArrayList<FunctionHelp>();


    public UserFunctionHolderHelp(UserFunctionHolder userHolder) {
        Iterator iter = userHolder.functions();
        while (iter.hasNext()) {
            // on récupère la fonction
            UserFunctionHolder.Function function = (UserFunctionHolder.Function)iter.next();
            int paramNumber = determineParameters(function);

            // on crée une functionHelp à partir de function
            FunctionHelp functionHelp = new FunctionHelp(userHolder.getName() + "." + function.getName(),
                                                         paramNumber, function.getHelp());
            functionList.add(functionHelp);
        }
    }


    /**
     * Retourne le nombre de paramètres de la fonction.
     *
     * @param function la fonction.
     *
     * @return le nombre de paramètres de la fonction
     */
    private int determineParameters(UserFunctionHolder.Function function) {
        String code = function.getCode();
        String paramList = code.substring(code.indexOf("(") + 1, code.indexOf(")")).trim();

        return new StringTokenizer(paramList, ",").countTokens();
    }


    public List<FunctionHelp> getFunctionHelpList() {
        return functionList;
    }
}
