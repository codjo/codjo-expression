/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * HashMap de methode.
 *
 * @author $Author: palmont $
 * @version $Revision: 1.3 $
 */
class MethodMap {
    static final Object NOT_SUPPORTED = new Object();
    private Map classMap = new HashMap();

    /**
     * Ajoute la definition de la methode dans la map . 2 definitions sont ajoute : La
     * premiere indexe seulement par le nom et la Deuxieme (optionnel) par le nom suvie
     * d'une serie de chiffre (type sql de l'argument)
     *
     * @param classMethods The feature to be added to the Method attribute
     * @param method The feature to be added to the Method attribute
     */
    private static void addMethod(Map classMethods, Method method) {
        try {
            classMethods.put(method.getName(),
                SqlTypeConverter.toSqlType(method.getReturnType()));
        }
        catch (IllegalArgumentException error) {
            classMethods.put(method.getName(), NOT_SUPPORTED);
        }

        try {
            Class[] argsClass = method.getParameterTypes();
            if (argsClass == null || argsClass.length == 0) {
                return;
            }
            Integer[] argsSqlType = new Integer[argsClass.length];
            for (int i = 0; i < argsClass.length; i++) {
                argsSqlType[i] = SqlTypeConverter.toSqlType(argsClass[i]);
            }
            classMethods.put(method.getName() + Arrays.asList(argsSqlType).toString(),
                SqlTypeConverter.toSqlType(method.getReturnType()));
        }
        catch (SqlTypeConverter.UnsupportedConvertionException error) {
            ; // Les méthodes avec une conversion impossible sont pas prise en compte
        }
    }


    /**
     * Retourne le type de retour de la methode.
     *
     * @param cl La classe
     * @param methodeName Le nom de la methode
     * @param argsTypeList Description of Parameter
     *
     * @return Un type SQL
     *
     * @throws UnknownItemException item inconnu
     * @throws UnsupportedItemException item non supporté
     */
    Integer getReturnSqlType(Class cl, String methodeName, List argsTypeList) {
        Map classMethods = (Map)classMap.get(cl);
        if (classMethods == null) {
            classMethods = buildClassMethodsMap(cl);
        }
        Object sqlType = null;
        if (argsTypeList != null && !argsTypeList.isEmpty()) {
            sqlType = classMethods.get(methodeName + argsTypeList.toString());
        }
        if (sqlType == null) {
            sqlType = classMethods.get(methodeName);
        }
        if (sqlType == null) {
            throw new UnknownItemException(methodeName);
        }
        else if (sqlType == NOT_SUPPORTED) {
            throw new UnsupportedItemException(methodeName);
        }
        return (Integer)sqlType;
    }


    /**
     * Construit une HashMap contenant comme clef le nom de la methode, et comme valeur
     * le type de retour SQL de la methode.
     *
     * @param cls Description of Parameter
     *
     * @return Description of the Returned Value
     */
    private Map buildClassMethodsMap(Class cls) {
        Map classMethods = new HashMap();
        Method[] method = cls.getDeclaredMethods();
        for (int i = 0; i < method.length; i++) {
            if (Modifier.isPublic(method[i].getModifiers())
                    && method[i].getReturnType() != void.class) {
                addMethod(classMethods, method[i]);
            }
        }

        classMap.put(cls, classMethods);
        return classMethods;
    }

    /**
     * Exception lance lorsque l'item n'a pas ete defini dans l'interpreter.
     *
     * @author $Author: palmont $
     * @version $Revision: 1.3 $
     */
    static class UnknownItemException extends RuntimeException {
        UnknownItemException(String item) {
            super("La variable (ou methode) \"" + item + "\" est inconnue");
        }
    }


    /**
     * Exception lance lorsque la methode possede un type de retour non supporte par
     * l'interpreter.
     *
     * @author $Author: palmont $
     * @version $Revision: 1.3 $
     */
    static class UnsupportedItemException extends RuntimeException {
        UnsupportedItemException(String item) {
            super("La methode \"" + item + "\" possede un type retour non supporte");
        }
    }
}
