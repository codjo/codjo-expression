/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Classe de test de ExpressionException.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.2 $
 */
public class ExpressionExceptionTest extends TestCase {
    ExpressionException exception;

    /**
     * Constructor for the ExpressionExceptionTest object
     *
     * @param name Description of Parameter
     */
    public ExpressionExceptionTest(String name) {
        super(name);
    }

    /**
     * A unit test for JUnit
     *
     * @exception Throwable Description of Exception
     */
    public void test_getMessage() throws Throwable {
        exception.addException("A", new NullPointerException("e"));
        assertEquals(exception.getMessage(0), "A a provoque l'erreur e");
        assertEquals(exception.getException(0) instanceof NullPointerException, true);
    }


    public void test_getMessage_twoError() throws Throwable {
        exception.addException("A", new NullPointerException("e"));
        exception.addException("B", new IllegalArgumentException("f"));

        assertEquals(exception.getMessage(0), "A a provoque l'erreur e");
        assertEquals(exception.getException(0) instanceof NullPointerException, true);

        assertEquals(exception.getMessage(1), "B a provoque l'erreur f");
        assertEquals(exception.getException(1) instanceof IllegalArgumentException, true);
    }


    /**
     * The JUnit setup method
     */
    protected void setUp() {
        exception = new ExpressionException();
    }


    /**
     * The teardown method for JUnit
     */
    protected void tearDown() {}


    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(ExpressionExceptionTest.class);
    }
}
