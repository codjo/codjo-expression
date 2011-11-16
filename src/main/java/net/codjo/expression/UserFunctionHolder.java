/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * Porteur de fonction définie par l'utilisateur.
 *
 * @version $Revision: 1.5 $
 */
public class UserFunctionHolder implements FunctionHolder {
    private final String name;
    private final Map<String, Function> functions = new HashMap<String, Function>();


    public UserFunctionHolder(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public List<String> getAllFunctions() {
        List<String> allHelps = new ArrayList<String>();
        for (Function function : functions.values()) {
            allHelps.add(function.getHelp());
        }
        return allHelps;
    }


    public void addFunction(int type, String functionName, String help, String function) {
        functions.put(functionName, new Function(functionName, type, help, function));
    }


    public Integer getReturnSqlType(String methodName) {
        Function func = functions.get(methodName);

        if (func != null) {
            return func.getReturnSqlType();
        }

        return null;
    }


    public Iterator<Function> functions() {
        return functions.values().iterator();
    }


    public Iterator<String> functionsCode() {
        return new IteratorOnCode(functions.values().iterator());
    }


    /**
     * Iterateur sur le code d'une fonction.
     */
    private static class IteratorOnCode implements Iterator<String> {
        private Iterator<Function> functionIterator;


        IteratorOnCode(Iterator<Function> funcIterator) {
            this.functionIterator = funcIterator;
        }


        public boolean hasNext() {
            return functionIterator.hasNext();
        }


        public String next() {
            return functionIterator.next().getCode();
        }


        public void remove() {
            functionIterator.remove();
        }
    }

    /**
     * Description d'une fonction.
     */
    public static class Function {
        private int type;
        private String help;
        private String code;
        private String name;


        Function(String functionName, int type, String help, String code) {
            this.type = type;
            this.help = help;
            this.code = code;
            this.name = functionName;
        }


        public int getReturnSqlType() {
            return type;
        }


        public String getName() {
            return name;
        }


        public String getCode() {
            return code;
        }


        public String getHelp() {
            return help;
        }
    }
}
