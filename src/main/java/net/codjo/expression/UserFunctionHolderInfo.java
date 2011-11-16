/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import net.codjo.expression.help.FunctionHolderHelp;
import net.codjo.expression.help.UserFunctionHolderHelp;
import java.util.Iterator;
import java.util.List;
/**
 * Classe permettant de gérer une instance de la classe UserFunctionHolder.
 */
class UserFunctionHolderInfo implements FunctionHolderInfo {
    private UserFunctionHolder functionHolder;

    UserFunctionHolderInfo(UserFunctionHolder holder) {
        this.functionHolder = holder;
    }

    public FunctionHolder getFunctionHolder() {
        return functionHolder;
    }


    public Integer getReturnSqlType(String methodName, List argsTypeList) {
        return functionHolder.getReturnSqlType(methodName);
    }


    public String buildDefinition() {
        StringBuilder expression = new StringBuilder("public class " + functionHolder.getName() + "{");

        Iterator funcCode = functionHolder.functionsCode();
        while (funcCode.hasNext()) {
            expression.append(funcCode.next());
        }

        expression.append("}");

        return expression.toString();
    }


    public boolean shouldDefineVariable() {
        return false;
    }


    public FunctionHolderHelp getHelp() {
        return new UserFunctionHolderHelp(functionHolder);
    }
}
