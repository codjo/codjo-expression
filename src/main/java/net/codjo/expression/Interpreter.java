/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.io.StringReader;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import koala.dynamicjava.parser.wrapper.JavaCCParser;
import koala.dynamicjava.tree.Node;
/**
 * Traduction d'une expression dans le format Java.
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.9 $
 */
class Interpreter {
    // Constantes
    private static final Integer OBJECT = new Integer(Types.JAVA_OBJECT);
    private InterpreterVisitor visitor = new InterpreterVisitor(this);

    // Mapping du type des variables
    private Map sqlTypeMap = new HashMap();
    Map javaTypeMap = new HashMap();
    MethodMap methodMap = new MethodMap();
    private Map functionHolderInfoMap = new HashMap();

    /**
     * Constructor
     */
    Interpreter() {}

    /**
     * Construit la javaTypeMap correspondant a sqlTypeMap.
     *
     * @param sqlTypeMap Description of Parameter
     *
     * @return Description of the Returned Value
     */
    private static Map buildJavaTypeMap(Map sqlTypeMap) {
        Map javaTypes = new HashMap();
        for (Iterator iter = sqlTypeMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            String name = (String)entry.getKey();
            Integer sqlType = (Integer)entry.getValue();
            Class javaType = SqlTypeConverter.toJavaType(sqlType.intValue());

            javaTypes.put(name, javaType);
        }
        return javaTypes;
    }


    /**
     * Constructor for the setVariables object
     *
     * @param variables Description of Parameter
     */
    public void setVariables(Map variables) {
        sqlTypeMap.clear();
        sqlTypeMap.putAll(variables);
        javaTypeMap = buildJavaTypeMap(sqlTypeMap);
        sqlTypeMap.put("Math", OBJECT);
    }


    /**
     * Ajoute une variable.
     *
     * @param variable Le nom de la variable
     * @param sqlType Le type SQL
     */
    public void addVariable(String variable, Integer sqlType) {
        sqlTypeMap.put(variable, sqlType);
        javaTypeMap.put(variable, SqlTypeConverter.toJavaType(sqlType.intValue()));
    }


    /**
     * Declare les methodes de la classe.
     *
     * @param functionHolderInfo Description of Parameter
     * @param variableName Description of Parameter
     */
    public void defineObjectMethods(FunctionHolderInfo functionHolderInfo,
        String variableName) {
        sqlTypeMap.put(variableName, OBJECT);
        javaTypeMap.put(variableName, functionHolderInfo.getClass());

//        if (functionHolderInfo.getFunctionHolder().getName() != null) {
//            functionHolderInfoMap.put(functionHolderInfo.getFunctionHolder().getName(),
//                functionHolderInfo);
//        }
        functionHolderInfoMap.put(variableName, functionHolderInfo);
    }


    /**
     * Converti une expression numerique en format BigDecimal.
     * 
     * <p>
     * Exemple : Cette methode converti "A + B" en "A.add(B)"
     * </p>
     *
     * @param expression Une expression sans ";" ( ex "A - B" )
     *
     * @return L'expression converti
     */
    public String convert(String expression) {
        StringReader reader = new StringReader(expression + ";");
        JavaCCParser parser = new JavaCCParser(reader, null);

        List list = parser.parseStream();

        String result = "";
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Node node = (Node)iter.next();
            result += node.acceptVisitor(visitor);
        }
        return result;
    }


    /**
     * Retourne le type SQL de la variable ou de la methode.
     *
     * @param variable Le nom de la variable ou de la methode
     *
     * @return type SQL
     *
     * @throws MethodMap.UnknownItemException type SQL inconnu
     */
    Integer getSqlType(String variable) {
        Integer sqlType = (Integer)sqlTypeMap.get(variable);
        if (sqlType == null) {
            throw new MethodMap.UnknownItemException(variable);
        }
        return sqlType;
    }


    /**
     * Retourne le type de retour de la methode.
     *
     * @param variable Le nom de la variable
     * @param variableSqlType
     * @param methodName Le nom de la methode
     * @param argsTypeList Type SQL des arguments
     *
     * @return le type SQL de retour de la methode
     */
    Integer getReturnSqlType(String variable, int variableSqlType, String methodName,
        List argsTypeList) {
        Class variableClass = (Class)javaTypeMap.get(variable);

        if (variableClass == null) {
            Class javaType = SqlTypeConverter.toJavaType(variableSqlType);
            return methodMap.getReturnSqlType(javaType, methodName, argsTypeList);
        }

        FunctionHolderInfo functionHolderInfo =
            (FunctionHolderInfo)functionHolderInfoMap.get(variable);
        if (functionHolderInfo != null) {
            return functionHolderInfo.getReturnSqlType(methodName, argsTypeList);
        }
        else {
            return methodMap.getReturnSqlType(variableClass, methodName, argsTypeList);
        }
    }
}
