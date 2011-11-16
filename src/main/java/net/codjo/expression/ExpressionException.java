/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.util.ArrayList;
import java.util.List;
/**
 * Exceptions levées lors de l'évaluation des expressions.
 * 
 * <p>
 * Une ExpressionException encapsule l'ensemble des exceptions lancees lors de
 * l'evaluation des expressions d'une ligne.
 * </p>
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.3 $
 */
public final class ExpressionException extends Exception {
    private List exceptionList = new ArrayList();
    private List fieldList = new ArrayList();

    /**
     * Constructor for the TreatmentException object
     */
    public ExpressionException() {}

    /**
     * Retourne le nombre d'erreurs rencontrées sur le traitement d'une ligne.
     *
     * @return Le nombre.
     */
    public int getNbError() {
        return exceptionList.size();
    }


    /**
     * Retourne une exception rencontrée sur le traitement d'une ligne.
     *
     * @param errorNumber Numéro d'erreur.
     *
     * @return L'exception.
     */
    public Exception getException(int errorNumber) {
        return (Exception)exceptionList.get(errorNumber);
    }


    /**
     * Retourne le message de l'exception rencontrée sur le traitement d'une ligne.
     *
     * @param errorNumber Numéro d'erreur.
     *
     * @return L'exception.
     */
    public String getMessage(int errorNumber) {
        Exception error = getException(errorNumber);
        String field = (String)fieldList.get(errorNumber);

        String errorMsg = error.getLocalizedMessage();
        if (errorMsg == null) {
            errorMsg = error.toString();
        }
        return field + " a provoque l'erreur " + errorMsg;
    }


    /**
     * Ajoute une exception.
     *
     * @param field The feature to be added to the Exception attribute
     * @param ex L'exception à ajouter.
     */
    public void addException(String field, Exception ex) {
        exceptionList.add(ex);
        fieldList.add(field);
    }


    /**
     * Overview.
     *
     * @return Description of the Returned Value
     */
    public String toString() {
        if (fieldList.size() == 0) {
            return "ExpressionException(0 erreur sous-jacente)";
        }
        return "ExpressionException(" + fieldList.size() + " erreur(s), " + getMessage(0)
        + ", ...)";
    }


    /**
     * Efface la liste des exceptions ayant declanche cette ExpressionException.
     */
    void clearException() {
        exceptionList.clear();
        fieldList.clear();
    }
}
