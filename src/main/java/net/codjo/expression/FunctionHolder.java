/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.util.List;
/**
 * Interface pour les objets contenant (portant) des methodes utilisable par les expressions.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.2 $
 * @see FunctionManager
 */
public interface FunctionHolder {
    /**
     * Retourne le nom de ce porteur de fonction.
     *
     * @return Le nom d'instance (ex: "util")
     */
    public String getName();


    /**
     * Retourne la liste des fonctions definie par ce porteur de fonction.
     *
     * @return Liste de string (ex: { "utils.geLastDay(Month)", ...} "
     *
     * @deprecated Utiliser le nouveau moyen du package help
     */
    @Deprecated
    public List<String> getAllFunctions();
}
