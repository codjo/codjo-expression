/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import net.codjo.expression.help.DefaultFunctionHolderHelp;
import net.codjo.expression.help.FunctionHolderHelp;
import java.util.List;
/**
 * Implémentation par défaut de FunctionHolderInfo.
 */
class DefaultFunctionHolderInfo implements FunctionHolderInfo {
    private FunctionHolder functionHolder;
    private MethodMap methodMap = new MethodMap();

    DefaultFunctionHolderInfo(FunctionHolder functionHolder) {
        this.functionHolder = functionHolder;
    }

    public FunctionHolder getFunctionHolder() {
        return functionHolder;
    }


    public Integer getReturnSqlType(String methodName, List argsTypeList) {
        return methodMap.getReturnSqlType(getFunctionHolder().getClass(), methodName,
            argsTypeList);
    }


    public String buildDefinition() {
        return "";
    }


    public boolean shouldDefineVariable() {
        return true;
    }


    public FunctionHolderHelp getHelp() {
        if (functionHolder instanceof FunctionHolderHelp) {
            return (FunctionHolderHelp)functionHolder;
        }
        return new DefaultFunctionHolderHelp(functionHolder);
    }
}
