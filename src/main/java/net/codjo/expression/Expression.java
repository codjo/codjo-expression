/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */

//package net.codjo.broadcast.common.columns;
package net.codjo.expression;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
/**
 * Gestionnaire d'une expression.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.6 $
 */
public class Expression {
    public static final String DEFAULT_STRING_VALUE = "Valeur";
    public static final String DEFAULT_STRING_NULL_VALUE = "Valeur_nulle";
    private static final String OUT_VALUE = "OUT";
    private String[] isNull = {"Valeur_nulle"};
    private String[] values = {"Valeur"};
    private FunctionManager functionManager = new FunctionManager();
    private String expression;
    private int[] inputSqlTypes = new int[1];
    private Integer outputSqlType = new Integer(Types.VARCHAR);
    private net.codjo.expression.ExpressionManager manager;

    public Expression(String expression, int inputSqlType, FunctionHolder funcs,
        int outputSqlType) {
        this.expression = expression;
        this.inputSqlTypes[0] = inputSqlType;
        this.outputSqlType = new Integer(outputSqlType);
        if (funcs != null) {
            functionManager.addFunctionHolder(funcs);
        }

        init();
    }


    public Expression(String expression, Variable[] inputVariables,
        FunctionHolder[] funcs, int outputSqlType, String[] nullValueNames) {
        this.expression = expression;
        this.values = new String[inputVariables.length];
        this.inputSqlTypes = new int[inputVariables.length];
        for (int i = 0; i < inputVariables.length; i++) {
            this.values[i] = inputVariables[i].name;
            this.inputSqlTypes[i] = inputVariables[i].type;
        }
        this.outputSqlType = new Integer(outputSqlType);
        if (funcs != null) {
            for (int i = 0; i < funcs.length; i++) {
                if (funcs[i] != null) {
                    functionManager.addFunctionHolder(funcs[i]);
                }
            }
        }

        isNull = nullValueNames;

        init();
    }

    public Integer getOutputSqlType() {
        return outputSqlType;
    }


    public Object compute(Object value) throws ExpressionException {
        manager.clear();

        setValue(values[0], value);

        manager.compute();

        return manager.getComputedValue(OUT_VALUE);
    }


    public Object compute(Variable[] inputVariables)
            throws ExpressionException {
        manager.clear();

        if (inputVariables != null) {
            for (int i = 0; i < inputVariables.length; ++i) {
                setValue(inputVariables[i].name, inputVariables[i].value);
            }
        }

        manager.compute();

        return manager.getComputedValue(OUT_VALUE);
    }


    private void setValue(String valueName, Object value) {
        int iValue = findIndex(valueName);

        if (iValue != -1) {
            if ((isNull != null) && (iValue < isNull.length) && (isNull[iValue] != null)) {
                manager.setFieldSourceValue(isNull[iValue],
                    ((value != null) ? Boolean.FALSE : Boolean.TRUE));
            }
            manager.setFieldSourceValue(valueName, value);
        }
    }


    private int findIndex(String valueName) {
        for (int iValue = 0; iValue < values.length; iValue++) {
            if (valueName.equals(values[iValue])) {
                return iValue;
            }
        }
        return -1;
    }


    private void init() {
        manager = new net.codjo.expression.ExpressionManager(functionManager, "", "");
        Map srcCol = new HashMap();

        if (isNull != null) {
            Integer booleanType = new Integer(Types.BIT);
            for (int i = 0; i < isNull.length; i++) {
                srcCol.put(isNull[i], booleanType);
            }
        }

        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                srcCol.put(values[i], new Integer(inputSqlTypes[i]));
            }
        }

        manager.setSourceColumn(srcCol);

        Map destCol = new HashMap();
        destCol.put(OUT_VALUE, outputSqlType);
        manager.setDestColumn(destCol);

        manager.add(OUT_VALUE, expression);

        manager.initExpressions();
    }

    /**
     * Représente une variable dans l'expression.
     */
    public static class Variable {
        private int type;
        private String name;
        private Object value;

        public Variable(String name, int type) {
            this.name = name;
            this.type = type;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
