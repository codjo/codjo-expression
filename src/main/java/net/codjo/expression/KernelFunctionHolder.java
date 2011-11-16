/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import net.codjo.expression.help.FunctionHelp;
import net.codjo.expression.help.FunctionHolderHelp;
import java.util.ArrayList;
import java.util.List;
/**
 * Attaché au FunctionManager. Ce FunctionHolder est utilisé pour implanté les fonctions (isNull et
 * isNotNull).
 */
public class KernelFunctionHolder implements FunctionHolder, FunctionHolderHelp {
    public String getName() {
        return "kernel";
    }


    public List<String> getAllFunctions() {
        List<String> allFunction = new ArrayList<String>();
        allFunction
              .add("Description : renvoie vrai si la donnée passée en paramètre est vide, faux dans le cas contraire\nUsage : isNull(donnée)\nExemple : isNull(Devise)");
        allFunction
              .add("Description : renvoie vrai si la donnée passée en paramètre n'est pas vide, faux dans le cas contraire\nUsage : isNotNull(donnée)\nExemple : isNotNull(Devise)");
        return allFunction;
    }


    public List<String> getAllFunctionsName() {
        List<String> allFunction = new ArrayList<String>();
        allFunction
              .add("isNull(variable)");
        allFunction
              .add("isNotNull(variable)");
        return allFunction;
    }

    public boolean isObjectNull(Object object) {
        return object == null;
    }


    public List<FunctionHelp> getFunctionHelpList() {
        List<FunctionHelp> functionsHelp = new ArrayList<FunctionHelp>();
        functionsHelp.add(new FunctionHelp("isNull", 1,
                                           "Description : renvoie vrai si la donnée passée en paramètre est vide, faux dans le cas contraire\nUsage : isNull(donnée)\nExemple : isNull(Devise)"));
        functionsHelp.add(new FunctionHelp("isNotNull", 1,
                                           "Description : renvoie vrai si la donnée passée en paramètre n'est pas vide, faux dans le cas contraire\nUsage : isNotNull(donnée)\nExemple : isNotNull(Devise)"));
        return functionsHelp;
    }
}
