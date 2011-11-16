/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression.help;
import net.codjo.expression.FunctionHolder;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Aide par défaut sur un {@link net.codjo.expression.FunctionHolder} standard.
 * 
 * <p>
 * Le message d'aide associé à une fonction  est construit de manière automatique (e.g.
 * <code>Usage : utils.bar(chaîne, entier)</code>).
 * </p>
 *
 * @see FunctionHolderHelp
 * @see UserFunctionHolderHelp
 */
public class DefaultFunctionHolderHelp implements FunctionHolderHelp {
    private List<FunctionHelp> functionList;

    /**
     * Constructeur qui permet de créer la liste de function à partir du functionHolder
     * transmis
     *
     * @param functionHolder le FunctionHolder
     */
    public DefaultFunctionHolderHelp(FunctionHolder functionHolder) {
        this.functionList = new ArrayList<FunctionHelp>();

        Method[] methods = functionHolder.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers()) && notDefinedInInterface(method)) {
                String name = functionHolder.getName() + "." + method.getName();
                functionList.add(new FunctionHelp(name,
                                                  method.getParameterTypes().length,
                                                  "Usage : " + name + "(" + parameterToString(method) + ")"));
            }
        }
    }

    /**
     * Retourne la liste des paramètres de la méthode sous forme de chaine de caractère.
     *
     * @param method la méthode.
     *
     * @return la liste des paramètres
     */
    private String parameterToString(Method method) {
        StringBuffer parameters = new StringBuffer();
        for (int j = 0; j < method.getParameterTypes().length; j++) {
            if (j > 0) {
                parameters.append(", ");
            }
            parameters.append(translate(method.getParameterTypes()[j]));
        }
        return parameters.toString();
    }


    /**
     * Traduit une classe pour le rendre lisible par un utilisateur.
     *
     * @param clazz la class à traduire
     *
     * @return une chaine de caractère correspondant à la classe
     */
    private String translate(Class clazz) {
        if (clazz == String.class) {
            return "chaîne";
        }
        else if (clazz == BigDecimal.class) {
            return "nombre";
        }
        else if (Date.class.isAssignableFrom(clazz)) {
            return "date";
        }
        else if (clazz == int.class) {
            return "entier";
        }
        else if (clazz == boolean.class) {
            return "booléen";
        }
        return "variable";
    }


    /**
     * Indique si la méthode fait partie de l'interface FunctionHolder.
     *
     * @param method
     *
     * @return retourne true si la méthode ne fait pas partie des méthodes implementées
     */
    private boolean notDefinedInInterface(Method method) {
        return !"getName".equals(method.getName())
        && !"getAllFunctions".equals(method.getName());
    }


    public List<FunctionHelp> getFunctionHelpList() {
        return functionList;
    }
}
