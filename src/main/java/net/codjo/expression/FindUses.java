/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import koala.dynamicjava.parser.wrapper.JavaCCParser;
import koala.dynamicjava.tree.Node;
/**
 * Classe permettant de determiner les éléments utilisé par les expressions.
 *
 * @version $Revision: 1.2 $
 */
public class FindUses {
    private List expressionList = new ArrayList();
    private final String srcFieldPrefix;

    public FindUses() {
        this(Constants.DEFAULT_SRC_FIELD_PREFIX);
    }


    public FindUses(String srcFieldPrefix) {
        this.srcFieldPrefix = srcFieldPrefix;
    }

    public void add(String expression) {
        expressionList.add(expression);
    }


    public Report buildReport() throws InvalidExpressionException {
        Report report = new Report();
        FindUsesReportFiller reportBuilder = new FindUsesReportFiller(report);
        for (Iterator iter = expressionList.iterator(); iter.hasNext();) {
            String expression = (String)iter.next();
            analyze(expression, reportBuilder);
        }
        return report;
    }


    private void analyze(String expression, FindUsesReportFiller reportBuilder)
            throws InvalidExpressionException {
        try {
            StringReader reader = new StringReader(expression + ";");
            JavaCCParser parser = new JavaCCParser(reader, null);

            List list = parser.parseStream();

            for (Iterator iter = list.iterator(); iter.hasNext();) {
                Node node = (Node)iter.next();
                node.acceptVisitor(reportBuilder);
            }
        }
        catch (Throwable ex) {
            String msg = "L'expression : \n'" + expression + "'\n est invalide.";
            if (ex instanceof Exception) {
                throw new InvalidExpressionException(msg, (Exception)ex);
            }
            else {
                throw new InvalidExpressionException(msg, new Exception(ex.getMessage()));
            }
        }
    }

    /**
     * Resultat de l'inspection des expressions.
     */
    public class Report {
        private Set usedSourceColumns = new TreeSet();

        public Collection getUsedSourceColumns() {
            return usedSourceColumns;
        }


        public void declareUse(String varName) {
            if (varName.startsWith(srcFieldPrefix)) {
                usedSourceColumns.add(varName.substring(srcFieldPrefix.length()));
            }
        }
    }
}
