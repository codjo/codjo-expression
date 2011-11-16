/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression.help;
/**
 * Aide sur une fonction.
 *
 * @see FunctionHolderHelp#getFunctionHelpList()
 */
public class FunctionHelp {
    private int parameterNumber;
    private String functionName;
    private String help;

    public FunctionHelp(String functionName, int parameterNumber, String help) {
        this.functionName = functionName;
        this.parameterNumber = parameterNumber;
        this.help = help;
    }

    public int getParameterNumber() {
        return parameterNumber;
    }


    public boolean equals(Object obj) {
        if (this == obj || !(obj instanceof FunctionHelp)) {
            return false;
        }

        final FunctionHelp functionHelp = (FunctionHelp)obj;

        if (parameterNumber != functionHelp.parameterNumber
                || !functionName.equals(functionHelp.functionName)
                || !help.equals(functionHelp.help)) {
            return false;
        }
        return true;
    }


    public int hashCode() {
        int result;
        result = parameterNumber;
        result = 29 * result + functionName.hashCode();
        result = 29 * result + help.hashCode();
        return result;
    }


    public String getFunctionName() {
        return functionName;
    }


    public String getHelp() {
        return help;
    }


    public String toString() {
        return "FunctionHelp{" + "parameterNumber=" + parameterNumber
        + ", functionName='" + functionName + "'" + ", help='" + help + "'" + "}";
    }
}
