/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import junit.framework.TestCase;
/**
 * Classe de test <code>FindUses</code>.
 *
 * @version $Revision: 1.5 $
 */
public class FindUsesTest extends TestCase {
    private FindUses uses;

    public void test_use_simple() throws Exception {
        uses.add("SRC_A");

        FindUses.Report report = uses.buildReport();

        assertEquals("[A]", report.getUsedSourceColumns().toString());
    }


    public void test_use_src_only() throws Exception {
        uses.add("SRC_A + SRC_B");

        FindUses.Report report = uses.buildReport();

        assertEquals("[A, B]", report.getUsedSourceColumns().toString());
    }


    public void test_use_src_and_var() throws Exception {
        uses.add("SRC_A + VAR_KK + SRC_B");

        FindUses.Report report = uses.buildReport();

        assertEquals("[A, B]", report.getUsedSourceColumns().toString());
    }


    public void test_use_method() throws Exception {
        uses.add("users.mySquare(SRC_A)");
        uses.add("users.mySquare(SRC_A) + VAR_A");
        uses.add("users.mySquare(SRC_B) + VAR_A");
        uses.add("users.mySquare(DEST_A) + VAR_A");
        uses.add("iif(DEST_A,VAR_A, SRC_C)");
        uses.add("15 + VAR_A");

        FindUses.Report report = uses.buildReport();

        assertEquals("[A, B, C]", report.getUsedSourceColumns().toString());
    }


    public void test_use_method_cplx() throws Exception {
        uses.add("users.mySquare()");
        uses.add("SRC_BOBO.dodo(\"5\").mySquare(DEST_A, SRC_B) + VAR_A");

        FindUses.Report report = uses.buildReport();

        assertEquals("[B, BOBO]", report.getUsedSourceColumns().toString());
    }


    public void test_use_cplx() throws Exception {
        uses.add("new String[] {SRC_A, null}");
        uses.add("new Truc(SRC_B)");
        uses.add("-SRC_C");
        uses.add("SRC_D * SRC_DPRIME");
        uses.add("SRC_E / SRC_E");
        uses.add("SRC_F - SRC_F");
        uses.add("SRC_G0 < 5");
        uses.add("SRC_G1 > 5");
        uses.add("SRC_G2 <= 5");
        uses.add("SRC_G3 >= 5");
        uses.add("SRC_G4 != 5");
        uses.add("SRC_G5 == 5");
        uses.add("SRC_H = SRC_Hp");
        uses.add("new boolean[] {true, SRC_I}");

        FindUses.Report report = uses.buildReport();

        assertEquals("[A, B, C, D, DPRIME, E, F, G0, G1, G2, G3, G4, G5, H, Hp, I]",
            report.getUsedSourceColumns().toString());
    }


    public void test_expressionNokThrowable() throws Exception {
        uses.add("SRC_YA switch SRC_YO");
        try {
            FindUses.Report report = uses.buildReport();
            fail("une InvalidExpressionException devrait être jetée");
        }
        catch (Exception e) {
            assertTrue("l'exception devrait être de type InvalidExpressionException",
                e instanceof InvalidExpressionException);
            InvalidExpressionException iee = (InvalidExpressionException)e;

            assertEquals("L'expression : \n" + "'SRC_YA switch SRC_YO'\n"
                + " est invalide.", iee.getMessage());

            assertTrue("la cause commence par ...",
                iee.getCause().getMessage().startsWith("Encountered \"SRC_YA switch\" at line 1, column 1."));
        }
    }


    public void test_expressionNok() throws Exception {
        uses.add("SRC_YA | SRC_YO");
        try {
            FindUses.Report report = uses.buildReport();
            fail("une InvalidExpressionException devrait être jetée");
        }
        catch (Exception ee) {
            assertTrue("l'exception devrait être de type InvalidExpressionException",
                ee instanceof InvalidExpressionException);
            assertEquals("L'expression : \n" + "'SRC_YA | SRC_YO'\n" + " est invalide.",
                ee.getMessage());
        }
    }


    public void test_use_compare() throws Exception {
        uses.add("SRC_G0 < SRC_G0p");
        uses.add("SRC_G1 > SRC_G1p");
        uses.add("SRC_G2 <= SRC_G2p");
        uses.add("SRC_G3 >= SRC_G3p");
        uses.add("SRC_G4 != SRC_G4p");
        uses.add("SRC_G5 == SRC_G5p");

        FindUses.Report report = uses.buildReport();

        assertEquals("[G0, G0p, G1, G1p, G2, G2p, G3, G3p, G4, G4p, G5, G5p]",
            report.getUsedSourceColumns().toString());
    }


    public void test_use_boolean() throws Exception {
        uses.add("SRC_A && SRC_Ap");
        uses.add("SRC_B || SRC_Bp");

        FindUses.Report report = uses.buildReport();

        assertEquals("[A, Ap, B, Bp]", report.getUsedSourceColumns().toString());
    }


    protected void setUp() throws Exception {
        uses = new FindUses();
    }
}
