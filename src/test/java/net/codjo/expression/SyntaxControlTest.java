package net.codjo.expression;
import static net.codjo.expression.SyntaxControl.checkComas;
import static net.codjo.expression.SyntaxControl.checkEqualityOpers;
import static net.codjo.expression.SyntaxControl.checkLogicalOpers;
import static net.codjo.expression.SyntaxControl.checkNotEqualityOpers;
import static net.codjo.expression.SyntaxControl.checkQuotes;
import static net.codjo.expression.SyntaxControl.selectText;
import static net.codjo.expression.SyntaxControl.viewEqualityOperError;
import static net.codjo.expression.SyntaxControl.viewLogicalOperError;
import static net.codjo.expression.SyntaxControl.viewNotEqualityOperError;
import static net.codjo.expression.SyntaxControl.viewQuoteError;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTextArea;
import junit.framework.TestCase;
/**
 *
 */
public class SyntaxControlTest extends TestCase {
    // Cas généraux
    private static final String SRC1 = "( \"a!=b && c==d && (e == 2 || 3)";
    private static final String SRC2 = "( \"a\"=b & c!d || && e == 2 | 3)";
    private static final String SRC3 = "( \"a\"==\"b & c==d) || & e = 2 | 3 && |)=)";
    private static final String SRC4 = "( || (|(a!b & c!d & e == 2 | 3 & |)";
    private static final String SRC5 = "((c==d) && (e==2 || 3))";

    // Cas particuliers
    private static final String SRC6 = "((c==d) && (e==2 || 3))&";
    private static final String SRC7 = "&((c==d)  (e==2 || 3))";
    private static final String SRC8 = "((c==d) && (e==2 || 3))|";
    private static final String SRC9 = "|((c==d) && (e==2  3))";
    private static final String SRC10 = "((c==d) && (e==2 || 3))!";
    private static final String SRC11 = "!((c==d) && (e==2 || 3))";
    private static final String SRC12 = "((c==d) && (e==2 || 3))=";
    private static final String SRC13 = "=((cd) && (e2 || 3))";
    private static final String SRC14 = "((c==d) && (e==2 || 3))\"";
    private static final String SRC15 = "\"((c==d) && (e==2 || 3))";

    //Gestion des inégalités
    private static final String SRC16 = "\"((c>=d) && (e==2))";
    private static final String SRC17 = "\"((c<=d) && (e==2))";
    private static final String SRC18 = "\"((c=>d) && (e==2))";
    private static final String SRC19 = "\"((c=<d) && (e==2))";
    private JTextArea expressionTextArea;


    public void test_checkComas() {
        assertEquals(1, checkComas(SRC1));
        assertEquals(0, checkComas(SRC2));
        assertEquals(-2, checkComas(SRC3));
        assertEquals(2, checkComas(SRC4));
    }


    public void test_checkQuotes() {
        assertFalse(checkQuotes(SRC1));
        assertTrue(checkQuotes(SRC2));
        assertFalse(checkQuotes(SRC3));
        assertTrue(checkQuotes(SRC4));
        assertFalse(checkQuotes(SRC14));
        assertFalse(checkQuotes(SRC15));
    }


    public void test_checkEqualityOpers() {
        assertEquals(0, checkEqualityOpers(SRC1));
        assertEquals(1, checkEqualityOpers(SRC2));
        assertEquals(2, checkEqualityOpers(SRC3));
        assertEquals(0, checkEqualityOpers(SRC4));
        assertEquals(1, checkEqualityOpers(SRC12));
        assertEquals(1, checkEqualityOpers(SRC13));

        assertEquals(0, checkEqualityOpers(SRC16));
        assertEquals(0, checkEqualityOpers(SRC17));
        assertEquals(1, checkEqualityOpers(SRC18));
        assertEquals(1, checkEqualityOpers(SRC19));
    }


    public void test_checkNotEqualityOpers() {
        assertEquals(0, checkNotEqualityOpers(SRC1));
        assertEquals(1, checkNotEqualityOpers(SRC2));
        assertEquals(0, checkNotEqualityOpers(SRC3));
        assertEquals(2, checkNotEqualityOpers(SRC4));
        assertEquals(1, checkNotEqualityOpers(SRC10));
        assertEquals(1, checkNotEqualityOpers(SRC11));
    }


    public void test_checkLogicalOpers_And() {
        assertEquals(0, checkLogicalOpers(SRC1, "&&"));
        assertEquals(1, checkLogicalOpers(SRC2, "&&"));
        assertEquals(2, checkLogicalOpers(SRC3, "&&"));
        assertEquals(3, checkLogicalOpers(SRC4, "&&"));
        assertEquals(1, checkLogicalOpers(SRC6, "&&"));
        assertEquals(1, checkLogicalOpers(SRC7, "&&"));
    }


    public void test_checkLogicalOpers_Or() {
        assertEquals(0, checkLogicalOpers(SRC1, "||"));
        assertEquals(1, checkLogicalOpers(SRC2, "||"));
        assertEquals(2, checkLogicalOpers(SRC3, "||"));
        assertEquals(3, checkLogicalOpers(SRC4, "||"));
        assertEquals(1, checkLogicalOpers(SRC8, "||"));
        assertEquals(1, checkLogicalOpers(SRC9, "||"));
    }


    public void test_viewEqualityOperError() {
        expressionTextArea.setText(SRC3);

        int lastEqualityIdx = viewEqualityOperError(expressionTextArea, 0);
        assertEquals(lastEqualityIdx, 26);
        assertEquals(expressionTextArea.getSelectionStart(), 25);
        assertEquals(expressionTextArea.getSelectionEnd(), 26);

        lastEqualityIdx = viewEqualityOperError(expressionTextArea, lastEqualityIdx);
        assertEquals(0, lastEqualityIdx);
        assertEquals(38, expressionTextArea.getSelectionStart());
        assertEquals(39, expressionTextArea.getSelectionEnd());

        lastEqualityIdx = viewEqualityOperError(expressionTextArea, lastEqualityIdx);
        assertEquals(26, lastEqualityIdx);
        assertEquals(25, expressionTextArea.getSelectionStart());
        assertEquals(26, expressionTextArea.getSelectionEnd());
    }


    public void test_viewEqualityOperError_endIdx() {
        expressionTextArea.setText(SRC12);

        int lastEqualityIdx = viewEqualityOperError(expressionTextArea, 0);
        assertEquals(0, lastEqualityIdx);
        assertEquals(23, expressionTextArea.getSelectionStart());
        assertEquals(24, expressionTextArea.getSelectionEnd());

        lastEqualityIdx = viewEqualityOperError(expressionTextArea, lastEqualityIdx);
        assertEquals(0, lastEqualityIdx);
        assertEquals(23, expressionTextArea.getSelectionStart());
        assertEquals(24, expressionTextArea.getSelectionEnd());
    }


    public void test_viewEqualityOperError_startIdx() {
        expressionTextArea.setText(SRC13);

        int lastEqualityIdx = viewEqualityOperError(expressionTextArea, 0);
        assertEquals(0, lastEqualityIdx);
        assertEquals(0, expressionTextArea.getSelectionStart());
        assertEquals(1, expressionTextArea.getSelectionEnd());

        lastEqualityIdx = viewEqualityOperError(expressionTextArea, lastEqualityIdx);
        assertEquals(0, lastEqualityIdx);
        assertEquals(0, expressionTextArea.getSelectionStart());
        assertEquals(1, expressionTextArea.getSelectionEnd());
    }


    public void test_viewNotEqualityOperError() {
        expressionTextArea.setText(SRC4);

        int lastEqualityIdx = viewNotEqualityOperError(expressionTextArea, 0);
        assertEquals(10, lastEqualityIdx);
        assertEquals(9, expressionTextArea.getSelectionStart());
        assertEquals(10, expressionTextArea.getSelectionEnd());

        lastEqualityIdx = viewNotEqualityOperError(expressionTextArea, lastEqualityIdx);
        assertEquals(0, lastEqualityIdx);
        assertEquals(15, expressionTextArea.getSelectionStart());
        assertEquals(16, expressionTextArea.getSelectionEnd());

        lastEqualityIdx = viewNotEqualityOperError(expressionTextArea, lastEqualityIdx);
        assertEquals(10, lastEqualityIdx);
        assertEquals(9, expressionTextArea.getSelectionStart());
        assertEquals(10, expressionTextArea.getSelectionEnd());
    }


    public void test_viewNotEqualityOperError_endIdx() {
        expressionTextArea.setText(SRC10);

        int lastEqualityIdx = viewNotEqualityOperError(expressionTextArea, 0);
        assertEquals(0, lastEqualityIdx);
        assertEquals(23, expressionTextArea.getSelectionStart());
        assertEquals(24, expressionTextArea.getSelectionEnd());

        lastEqualityIdx = viewNotEqualityOperError(expressionTextArea, lastEqualityIdx);
        assertEquals(0, lastEqualityIdx);
        assertEquals(23, expressionTextArea.getSelectionStart());
        assertEquals(24, expressionTextArea.getSelectionEnd());
    }


    public void test_viewNotEqualityOperError_startIdx() {
        expressionTextArea.setText(SRC11);

        int lastEqualityIdx = viewNotEqualityOperError(expressionTextArea, 0);
        assertEquals(0, lastEqualityIdx);
        assertEquals(0, expressionTextArea.getSelectionStart());
        assertEquals(1, expressionTextArea.getSelectionEnd());

        lastEqualityIdx = viewNotEqualityOperError(expressionTextArea, lastEqualityIdx);
        assertEquals(0, lastEqualityIdx);
        assertEquals(0, expressionTextArea.getSelectionStart());
        assertEquals(1, expressionTextArea.getSelectionEnd());
    }


    public void test_viewLogicalOperError_And() {
        Map lastLogicalIdx = new HashMap();
        lastLogicalIdx.put("&&", 0);
        expressionTextArea.setText(SRC3);

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "&&", lastLogicalIdx);
        assertEquals(11, lastLogicalIdx.get("&&"));
        assertEquals(10, expressionTextArea.getSelectionStart());
        assertEquals(11, expressionTextArea.getSelectionEnd());

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "&&", lastLogicalIdx);
        assertEquals(22, lastLogicalIdx.get("&&"));
        assertEquals(21, expressionTextArea.getSelectionStart());
        assertEquals(22, expressionTextArea.getSelectionEnd());

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "&&", lastLogicalIdx);
        assertEquals(11, lastLogicalIdx.get("&&"));
        assertEquals(10, expressionTextArea.getSelectionStart());
        assertEquals(11, expressionTextArea.getSelectionEnd());
    }


    public void test_viewLogicalOperError_And_endIdx() {
        Map lastLogicalIdx = new HashMap();
        lastLogicalIdx.put("&&", 0);
        expressionTextArea.setText(SRC6);

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "&&", lastLogicalIdx);
        assertEquals(0, lastLogicalIdx.get("&&"));
        assertEquals(23, expressionTextArea.getSelectionStart());
        assertEquals(24, expressionTextArea.getSelectionEnd());

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "&&", lastLogicalIdx);
        assertEquals(0, lastLogicalIdx.get("&&"));
        assertEquals(23, expressionTextArea.getSelectionStart());
        assertEquals(24, expressionTextArea.getSelectionEnd());
    }


    public void test_viewLogicalOperError_And_startIdx() {
        Map lastLogicalIdx = new HashMap();
        lastLogicalIdx.put("&&", 0);
        expressionTextArea.setText(SRC7);

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "&&", lastLogicalIdx);
        assertEquals(0, lastLogicalIdx.get("&&"));
        assertEquals(0, expressionTextArea.getSelectionStart());
        assertEquals(1, expressionTextArea.getSelectionEnd());

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "&&", lastLogicalIdx);
        assertEquals(0, lastLogicalIdx.get("&&"));
        assertEquals(0, expressionTextArea.getSelectionStart());
        assertEquals(1, expressionTextArea.getSelectionEnd());
    }


    public void test_viewLogicalOperError_Or() {
        Map lastLogicalIdx = new HashMap();
        lastLogicalIdx.put("||", 0);
        expressionTextArea.setText(SRC3);

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "||", lastLogicalIdx);
        assertEquals(30, lastLogicalIdx.get("||"));
        assertEquals(29, expressionTextArea.getSelectionStart());
        assertEquals(30, expressionTextArea.getSelectionEnd());

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "||", lastLogicalIdx);
        assertEquals(0, lastLogicalIdx.get("||"));
        assertEquals(36, expressionTextArea.getSelectionStart());
        assertEquals(37, expressionTextArea.getSelectionEnd());

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "||", lastLogicalIdx);
        assertEquals(30, lastLogicalIdx.get("||"));
        assertEquals(29, expressionTextArea.getSelectionStart());
        assertEquals(30, expressionTextArea.getSelectionEnd());
    }


    public void test_viewLogicalOperError_Or_endIdx() {
        Map lastLogicalIdx = new HashMap();
        lastLogicalIdx.put("||", 0);
        expressionTextArea.setText(SRC8);

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "||", lastLogicalIdx);
        assertEquals(0, lastLogicalIdx.get("||"));
        assertEquals(23, expressionTextArea.getSelectionStart());
        assertEquals(24, expressionTextArea.getSelectionEnd());

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "||", lastLogicalIdx);
        assertEquals(0, lastLogicalIdx.get("||"));
        assertEquals(23, expressionTextArea.getSelectionStart());
        assertEquals(24, expressionTextArea.getSelectionEnd());
    }


    public void test_viewLogicalOperError_Or_startIdx() {
        Map lastLogicalIdx = new HashMap();
        lastLogicalIdx.put("||", 0);
        expressionTextArea.setText(SRC9);

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "||", lastLogicalIdx);
        assertEquals(0, lastLogicalIdx.get("||"));
        assertEquals(0, expressionTextArea.getSelectionStart());
        assertEquals(1, expressionTextArea.getSelectionEnd());

        lastLogicalIdx = viewLogicalOperError(expressionTextArea, "||", lastLogicalIdx);
        assertEquals(0, lastLogicalIdx.get("||"));
        assertEquals(0, expressionTextArea.getSelectionStart());
        assertEquals(1, expressionTextArea.getSelectionEnd());
    }


    public void test_viewQuoteError() {
        expressionTextArea.setText(SRC2);

        int lastQuoteIdx = viewQuoteError(expressionTextArea, 0);
        assertEquals(3, lastQuoteIdx);
        assertEquals(2, expressionTextArea.getSelectionStart());
        assertEquals(3, expressionTextArea.getSelectionEnd());

        lastQuoteIdx = viewQuoteError(expressionTextArea, lastQuoteIdx);
        assertEquals(0, lastQuoteIdx);
        assertEquals(4, expressionTextArea.getSelectionStart());
        assertEquals(5, expressionTextArea.getSelectionEnd());

        lastQuoteIdx = viewQuoteError(expressionTextArea, lastQuoteIdx);
        assertEquals(3, lastQuoteIdx);
        assertEquals(2, expressionTextArea.getSelectionStart());
        assertEquals(3, expressionTextArea.getSelectionEnd());
    }


    public void test_selectText() {
        expressionTextArea.setText(SRC5);

        selectText(expressionTextArea, "(", ")", 1, true);
        assertEquals(1, expressionTextArea.getSelectionStart());
        assertEquals(22, expressionTextArea.getSelectionEnd());

        selectText(expressionTextArea, "(", ")", 12, true);
        assertEquals(12, expressionTextArea.getSelectionStart());
        assertEquals(21, expressionTextArea.getSelectionEnd());

        selectText(expressionTextArea, "(", ")", 2, true);
        assertEquals(2, expressionTextArea.getSelectionStart());
        assertEquals(6, expressionTextArea.getSelectionEnd());

        selectText(expressionTextArea, ")", "(", 23, false);
        assertEquals(1, expressionTextArea.getSelectionStart());
        assertEquals(22, expressionTextArea.getSelectionEnd());

        selectText(expressionTextArea, ")", "(", 22, false);
        assertEquals(12, expressionTextArea.getSelectionStart());
        assertEquals(21, expressionTextArea.getSelectionEnd());

        selectText(expressionTextArea, ")", "(", 7, false);
        assertEquals(2, expressionTextArea.getSelectionStart());
        assertEquals(6, expressionTextArea.getSelectionEnd());
    }


    public void test_isCorrectFormula() throws Exception {
        assertTrue(SyntaxControl.isCorrectFormula("manager in(\"toto\",\"titi\")"));
        assertFalse(SyntaxControl.isCorrectFormula("manager in(\"toto\",\"titi)"));

        assertTrue(SyntaxControl.isCorrectFormula("manager in()"));
        assertFalse(SyntaxControl.isCorrectFormula("manager in("));

        assertTrue(SyntaxControl.isCorrectFormula("+++-||&&=="));

        assertTrue(SyntaxControl.isCorrectFormula("!=manager"));
        assertFalse(SyntaxControl.isCorrectFormula("manager!"));

        assertTrue(SyntaxControl.isCorrectFormula("manager == 2"));
        assertFalse(SyntaxControl.isCorrectFormula("manager = 2"));

        assertTrue(SyntaxControl.isCorrectFormula("manager || toto"));
        assertTrue(SyntaxControl.isCorrectFormula("manager && toto"));
        assertTrue(SyntaxControl.isCorrectFormula("manager >= toto"));

        assertFalse(SyntaxControl.isCorrectFormula("manager &&& toto"));
    }


    @Override
    protected void setUp() {
        expressionTextArea = new JTextArea();
    }


    @Override
    protected void tearDown() {
    }
}
