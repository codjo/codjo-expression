/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.io.PrintStream;
/**
 * Exception levée quand on ne peut pas déterminer la liste des colonnes dans une
 * expression ou l'évaluation de l'expression.
 *
 * @version $Revision: 1.2 $
 */
public class InvalidExpressionException extends Exception {
    private Exception cause;

    public InvalidExpressionException(String msg) {
        super(msg);
    }


    public InvalidExpressionException(Exception cause) {
        super(cause.getMessage());
        setCause(cause);
    }


    public InvalidExpressionException(String msg, Exception cause) {
        super(msg);
        setCause(cause);
    }

    public Throwable getCause() {
        return cause;
    }


    public void printStackTrace(java.io.PrintWriter writer) {
        super.printStackTrace(writer);
        if (getCause() != null) {
            writer.println(" ---- cause ---- ");
            getCause().printStackTrace(writer);
        }
    }


    public void printStackTrace() {
        super.printStackTrace();
        if (getCause() != null) {
            System.err.println(" ---- cause ---- ");
            getCause().printStackTrace();
        }
    }


    public void printStackTrace(PrintStream writer) {
        super.printStackTrace(writer);
        if (getCause() != null) {
            writer.println(" ---- cause ---- ");
            getCause().printStackTrace(writer);
        }
    }


    public void setCause(Exception cause) {
        this.cause = cause;
    }
}
