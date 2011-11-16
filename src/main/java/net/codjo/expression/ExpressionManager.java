/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import koala.dynamicjava.interpreter.InterpreterException;
import koala.dynamicjava.interpreter.TreeInterpreter;
import koala.dynamicjava.interpreter.error.CatchedExceptionError;
import koala.dynamicjava.interpreter.throwable.ThrownException;
import koala.dynamicjava.parser.wrapper.JavaCCParserFactory;
import org.apache.log4j.Logger;
/**
 * Gestionnaire des expressions d'un lot.
 *
 * @author $Author: rivierv $
 * @version $Revision: 1.18 $
 */
public class ExpressionManager {
    // Log
    private static final Logger APP = Logger.getLogger(ExpressionManager.class);
    /**
     * ça sert à jeter une exception bien claire qui contient le nom de l'expressionManager
     */
    private String expressionManagerName;
    private SqlTypeConverter sqlTypeConverter = new SqlTypeConverter();

    // DynamicJava Stuff
    private Interpreter converter = new Interpreter();
    private Map<String, Integer> destColumn;

    // Les expressions
    private List<String> destFieldList = new ArrayList<String>();
    private String destFieldPrefix = Constants.DEFAULT_DEST_FIELD_PREFIX;

    // Exception lancee lors de l'evaluation des expressions (@see compute)
    private ExpressionException exception = new ExpressionException();
    private List<String> expressionFieldList = new ArrayList<String>();
    private List<String> expressionVarList = new ArrayList<String>();
    private FunctionManager functionManager;
    private TreeInterpreter interpreter = new TreeInterpreter(new JavaCCParserFactory());

    // Definition des colonnes des table src et dest
    private Map<String, Integer> sourceColumn;
    private String srcFieldPrefix = Constants.DEFAULT_SRC_FIELD_PREFIX;
    private List tokens;
    private Map<String, Integer> varColumn = new HashMap<String, Integer>();
    private List<String> varList = new ArrayList<String>();
    /**
     * Liste des variables <code>XXX</code> pour lesquelles une variable <code>SRC_XXX_ISNULL</code> a été
     * définie (cela arrive lorsque <code>isNull(XXX)</code> apparaît dans une expression).
     */
    private List srcFieldHavingIsNullList = new ArrayList();


    /**
     * Constructor for the ExpressionManager object
     *
     * @param funcMan FunctionManager
     */
    public ExpressionManager(FunctionManager funcMan) {
        this(funcMan, Constants.DEFAULT_SRC_FIELD_PREFIX,
             Constants.DEFAULT_DEST_FIELD_PREFIX);
    }


    public ExpressionManager(FunctionManager funcMan, String srcFieldPrefix,
                             String destFieldPrefix) {
        this.srcFieldPrefix = srcFieldPrefix;
        this.destFieldPrefix = destFieldPrefix;

        if (funcMan == null) {
            functionManager = new FunctionManager();
        }
        else {
            // Si un FunctionManager a été passé, on le clone (la Map des FunctionHolder est clonée) avant
            // d'ajouter le KernelFunctionHolder ; ainsi, cet ajout restera invisible de l'extérieur.
            functionManager = (FunctionManager)funcMan.clone();
        }
    }


    public String getExpressionManagerName() {
        return expressionManagerName;
    }


    public void setExpressionManagerName(String expressionManagerName) {
        this.expressionManagerName = expressionManagerName;
    }


    /**
     * Ajoute une expression.
     *
     * <p> <b>ATTENTION</b> : Les expressions doivent etre ajoutees par ordre de priorite (Ce qui est fait par
     * le home). </p>
     *
     * @param destField  Description of Parameter
     * @param expression L'expression.
     */
    public void add(String destField, String expression) {
        if (destField == null || expression == null) {
            throw new IllegalArgumentException();
        }

        if (!destColumn.containsKey(destField) && !isVariable(destField)) {
            throw new IllegalArgumentException("Erreur de parametrage : le champ "
                                               + destField + " n'existe pas dans la table destination"
                                               + "ou n'est pas défini comme variable");
        }

        expression = treateIsNullMethod(expression);

        if (!isVariable(destField)) {
            destFieldList.add(destField);
            expressionFieldList.add(expression);
        }
        else {
            varList.add(destField);
            expressionVarList.add(expression);
        }
    }


    /**
     * Efface les valeurs des variables (source et destination).
     */
    public void clear() {
        clearAllVariable(destFieldPrefix, destColumn);
        clearAllVariable(srcFieldPrefix, sourceColumn);
        clearAllVariable("", varColumn);
    }


    /**
     * Lance l'interpretation des expressions.
     *
     * @throws ExpressionException   Erreur durant le calcult
     * @throws IllegalStateException Erreur interne
     * @see #initExpressions()
     */
    public void compute() throws ExpressionException {
        try {
            exception.clearException();
            interpreter.interpret(tokens);
        }
        catch (InterpreterException ex) {
            Throwable rootEx = getRootException(ex);
            if (rootEx.getClass() == ExpressionException.class) {
                throw (ExpressionException)rootEx;
            }
            else {
                APP.error("Erreur interne!", rootEx);
                throw new IllegalStateException("Erreur Interne : " + rootEx.getMessage());
            }
        }
    }


    /**
     * Retourne la valeur du champ de destination (variable ou non).
     *
     * @param columnName Nom physique de la colonne.
     *
     * @return La valeur.
     */
    public Object getComputedValue(String columnName) {
        if (isVariable(columnName)) {
            return interpreter.getVariable(columnName);
        }
        else {
            return interpreter.getVariable(destFieldPrefix + columnName);
        }
    }


    /**
     * Retourne la liste des colonnes de la table de destination.
     *
     * @return Map des colonnes (Nom physique / Type SQL).
     */
    public java.util.Map getDestColumn() {
        return destColumn;
    }


    /**
     * Retourne la liste des colonnes destinations des expressions.
     *
     * <p> todo a repasser en privee, des que possible </p>
     *
     * @return Liste de String
     */
    public List getDestFieldList() {
        return destFieldList;
    }


    public int getDestFieldSQLType(String fieldName) {
        return destColumn.get(fieldName);
    }


    /**
     * Retourne la liste des colonnes de la table source.
     *
     * @return Map des colonnes (Nom physique / Type SQL).
     */
    public java.util.Map<String, Integer> getSourceColumn() {
        return sourceColumn;
    }


    /**
     * Initialise ce manager d'expressions. Cette méthode doit être appelée une seule fois avant le lancement
     * du Compute.
     *
     * @throws RuntimeException TODO
     * @deprecated use compileExpressions instead
     */
    @Deprecated
    public void initExpressions() {
        try {
            compileExpressions();
        }
        catch (InvalidExpressionException e) {
            throw new RuntimeException(e.getCause().getMessage());
        }
    }


    public void compileExpressions() throws InvalidExpressionException {
        initConverter();

        defineVariable(destFieldPrefix, destColumn);
        defineVariable(srcFieldPrefix, sourceColumn);
        defineVariable("", varColumn);
        interpreter.defineVariable("expressionException", exception,
                                   ExpressionException.class);

        StringBuffer headerString =
              new StringBuffer("package net.codjo.operation.treatment;import java.math.BigDecimal;");

        Map<String, FunctionHolderInfo> fhi = functionManager.getFunctionHolderInfo();
        for (Map.Entry<String, FunctionHolderInfo> entry : fhi.entrySet()) {
            final FunctionHolderInfo instance = entry.getValue();
            headerString.append(instance.buildDefinition());

            if (instance.shouldDefineVariable()) {
                interpreter.defineVariable(entry.getKey(),
                                           instance.getFunctionHolder(),
                                           instance.getFunctionHolder().getClass());
            }
        }

        try {
            Reader expression = new StringReader(buildExpressions(headerString));
            tokens = interpreter.buildStatementList(expression, null);
        }
        catch (Throwable ex) {
            String message =
                  "Impossible de compiler : " + getExpressionManagerName()
                  + ". Une ou plusieurs de ses expressions comportent des erreurs.";
            APP.error("Erreur dans l'expression ", ex);
            try {
                printExpressions();
            }
            catch (Throwable t) {
                APP.error("Erreur lors de la compilation de l'expression ", t);
            }
            if (ex instanceof Exception) {
                throw new InvalidExpressionException(message, (Exception)ex);
            }
            else {
                throw new InvalidExpressionException(message, new Exception(ex.getMessage()));
            }
        }
    }


    public boolean isDestFieldNumeric(String fieldName) {
        return SqlTypeConverter.isNumeric(getDestFieldSQLType(fieldName));
    }


    /**
     * Renseigne la liste des colonnes de la table de destination..
     *
     * @param newDestColumn Map des colonnes (Nom physique / Type SQL).
     */
    public void setDestColumn(java.util.Map<String, Integer> newDestColumn) {
        destColumn = newDestColumn;
    }


    /**
     * Renseigne la valeur d'un champ source.
     *
     * @param columnName Nom physique de la colonne.
     * @param value      Valeur de la colonne.
     */
    public void setFieldSourceValue(String columnName, Object value) {
        String variableName = srcFieldPrefix + columnName;
        if (value == null) {
            clearVariable(variableName, getSourceFieldSQLType(columnName));
        }
        else if (value instanceof Timestamp) {
            interpreter.setVariable(variableName, new java.sql.Date(((Timestamp)value).getTime()));
        }
        else {
            interpreter.setVariable(variableName, value);
        }

        // Si le champ source variableName a une variable VAR_variableName_ISNULL
        // associée, il faut positionner la valeur de cette dernière.
        if (srcFieldHavingIsNullList.contains(variableName)) {
            String variableIsNullName = isNullVarName(variableName);
            Boolean isValueNull = (value == null) ? Boolean.TRUE : Boolean.FALSE;
            interpreter.setVariable(variableIsNullName, isValueNull);
        }
    }


    /**
     * Renseigne la liste des colonnes de la table source.
     *
     * @param newSourceColumn Map des colonnes (Nom physique / Type SQL).
     */
    public void setSourceColumn(java.util.Map<String, Integer> newSourceColumn) {
        sourceColumn = newSourceColumn;
    }


    /**
     * Renseigne la liste des colonnes variables.
     *
     * @param newVarColumn The new VarColumn value
     */
    public void setVarColumn(java.util.Map<String, Integer> newVarColumn) {
        varColumn = newVarColumn;
    }


    public void setSrcFieldHavingIsNullList(List srcFieldHavingIsNullList) {
        this.srcFieldHavingIsNullList = srcFieldHavingIsNullList;
    }


    /**
     * Converti les colonnes en attribut java.
     *
     * @param prefix  Le prefix de l'attribut
     * @param columns Les colonnes
     *
     * @return la chaine (ex : "String DEST_BOBO;int age;")
     */
    private static String toJava(String prefix, Map<String, Integer> columns) {
        StringBuffer buffer = new StringBuffer();
        for (String field : columns.keySet()) {
            int sqlType = columns.get(field);
            buffer.append(toJavaType(sqlType)).append(" ").append(prefix).append(field).append(";");
        }
        return buffer.toString();
    }


    /**
     * Retourne le type Java associe a un type SQL.
     *
     * @return Une String contenant le type Java (ex : "String")
     */
    private static Object toJavaType(int sqlType) {
        return SqlTypeConverter.toJavaType(sqlType).getName();
    }


    /**
     * Construit une chaine de caractere contenant toutes les expressions.
     *
     * <p> La chaine construite est de la forme : <blockquote>
     * <pre>
     *
     *  package net.codjo.operation.treatment; <br>
     *  try { <br>
     *  A = B + C + utils.lastDay(E); <br>
     *  } <br>
     *  catch (Exception e) { <br>
     *  expressionException.addException(e); <br>
     *  } <br>
     *  try { <br>
     *  B = utils.getR(e); <br>
     *  } catch (Exception e) { <br>
     *  expressionException.addException(e); <br>
     *  } <br>
     *  if (expressionException.getNbError() >0) { throw expressionException; }
     *  </pre>
     * </blockquote> </p>
     *
     * @return La chaine.
     */
    private String buildExpressions(StringBuffer expressions) {

        buildFieldExpression(expressions, varList, expressionVarList, "");
        buildFieldExpression(expressions, destFieldList, expressionFieldList,
                             destFieldPrefix);

        expressions.append("if (expressionException.getNbError() > 0) { "
                           + "expressionException.fillInStackTrace();" + "throw expressionException;}");

        return expressions.toString();
    }


    /**
     * Construit une chaine de caractere pour les expressions des variables et des champs de destination.
     *
     * @param expressions    Description of Parameter
     * @param fieldList      Liste des champs de destination (variables ou non)
     * @param expressionList Liste des expressions (variables ou non)
     * @param prefix         Le prefix ("" ou destFieldPrefix)
     */
    private void buildFieldExpression(StringBuffer expressions, List fieldList,
                                      List expressionList, String prefix) {
        for (int i = 0; i < fieldList.size(); i++) {
            StringBuffer oneExpression = new StringBuffer();
            String destField = (String)fieldList.get(i);

            expressions.append("try { ");

            // Une Expression
            oneExpression.setLength(0);
            oneExpression.append(prefix).append(destField).append('=').append(expressionList
                  .get(i));

            try {
                expressions.append(converter.convert(oneExpression.toString()));
            }
            catch (Error ex) {
                APP.error("Impossible de traduire l'expression de " + destField + " ("
                          + oneExpression + ")", ex);
                throw ex;
            }
            expressions.append(';');

            expressions.append("} catch (Exception e) { ")
                  .append("expressionException.addException(\"").append(destField).append("\", e); ")
                  .append('}');
        }
    }


    /**
     * Efface un jeu de variables.
     *
     * @param prefixe Le prefixe (DEST_ ou SRC_).
     * @param columns Map des colonnes (Nom physique / Type SQL).
     *
     * @see #clear()
     */
    private void clearAllVariable(String prefixe, Map<String, Integer> columns) {
        for (String field : columns.keySet()) {
            clearVariable(prefixe + field, columns.get(field));
        }
    }


    /**
     * Efface une variable.
     *
     * @param variableName Nom de la variable (ex : "SRC_ANOMALY")
     * @param sqlType      Type sql
     */
    private void clearVariable(String variableName, Integer sqlType) {
        interpreter.setVariable(variableName, sqlTypeConverter.getDefaultSqlValue(sqlType));
    }


    public void setDefaultNullValue(int sqlTypes, Object defaultValue) {

        sqlTypeConverter.setDefaultNullValue(sqlTypes, defaultValue);
    }


    /**
     * Definit les variables dans l'interpreter.
     *
     * @param prefixe Le prefixe a ajouter au nom de colonne
     * @param columns Une Map de type (colonne / type sql)
     */
    private void defineVariable(String prefixe, Map<String, Integer> columns) {
        for (String field : columns.keySet()) {
            Integer sqlType = columns.get(field);
            sqlTypeConverter.defineVariableInto(interpreter, prefixe + field, sqlType);
        }
    }


    private void fillVariablesMap(String prefixe, Map<String, Integer> columns, Map<String, Integer> dest) {
        for (String field : columns.keySet()) {
            Integer sqlType = columns.get(field);
            dest.put(prefixe + field, sqlType);
        }
    }


    /**
     * Retourne l'exception racine de l'exception DynamicJava.
     *
     * <p> DynamicJava encapsule l'exception lancee lors d'une interpretation avec plein d'exceptions de son
     * cru. </p>
     *
     * @return L'exception racine
     */
    private Throwable getRootException(Throwable ex) {
        if (ex instanceof ThrownException) {
            return getRootException(((ThrownException)ex).getException());
        }
        else if (ex instanceof CatchedExceptionError) {
            return getRootException(((CatchedExceptionError)ex).getException());
        }
        return ex;
    }


    private Integer getSourceFieldSQLType(String fieldName) {
        return sourceColumn.get(fieldName);
    }


    /**
     * Initialise le convertisseur d'expression.
     */
    private void initConverter() {
        Map<String, Integer> allVariables = new HashMap<String, Integer>();
        fillVariablesMap(destFieldPrefix, destColumn, allVariables);
        fillVariablesMap(srcFieldPrefix, sourceColumn, allVariables);
        fillVariablesMap("", varColumn, allVariables);
        converter.setVariables(allVariables);

        Map<String, FunctionHolderInfo> fhi = functionManager.getFunctionHolderInfo();
        for (Map.Entry<String, FunctionHolderInfo> entry : fhi.entrySet()) {
            final FunctionHolderInfo holder = entry.getValue();
            final String variableName = entry.getKey();
            converter.defineObjectMethods(holder, variableName);
        }
    }


    /**
     * Indique si le champ fieldName est défini comme variable.
     *
     * @param fieldName Un champ destination
     *
     * @return 'true' si oui
     */
    private boolean isVariable(String fieldName) {
        return varColumn.containsKey(fieldName);
    }


    /**
     * Trace de Debug
     */
    private void printExpressions() {
        String str =
              toJava(destFieldPrefix, destColumn) + toJava(srcFieldPrefix, sourceColumn)
              + toJava("", varColumn);
        APP.debug("              expression :" + buildExpressions(new StringBuffer()));
        APP.debug("              definition :" + str);
    }


    /**
     * Recherche les motifs <code>isNull(XXX)</code> et <code>isNotNull(XXX)</code> dans l'expression donnée,
     * les remplace respectivement par <code>(VAR_XXX_ISNULL==true)</code> et
     * <code>(VAR_XXX_ISNULL==false)</code> puis, si cela n'avait pas déjà été fait, ajoute
     * <code>VAR_XXX_ISNULL</code> à <code>varCol</code> et <code>XXX</code> à
     * <code>srcFieldHavingIsNullList</code>
     *
     * @param expression l'expression à analyser.
     */
    String treateIsNullMethod(String expression) {
        // Recherche les isNull
        expression = treateIsNullMethod(expression, true);
        // Recherche les isNotNull
        expression = treateIsNullMethod(expression, false);
        return expression;
    }


    private String treateIsNullMethod(String expression, boolean isNull) {
        String methodToFind = isNull ? "isNull" : "isNotNull";

        int isNullPos;
        //noinspection NestedAssignment
        while ((isNullPos = expression.indexOf(methodToFind)) != -1) {
            // Recherche du nom sur lequel s'applique la méthode isNull
            int openingBracePos = expression.indexOf("(", isNullPos);
            int closingBrace = expression.indexOf(")", openingBracePos);

            // @todo Lancer une Exception si l'on n'a pas trouvé les deux parenthèses
            String varName =
                  expression.substring(openingBracePos + 1, closingBrace).trim();

            if (isVariable(varName)) {
                String operator = isNull ? "==" : "!=";

                // On remplace isNull(XXX) par (XXX==null) dans l'expression
                expression =
                      expression.substring(0, isNullPos) + "(kernel.isObjectNull("
                      + varName + ")" + operator + "true)"
                      + expression.substring(closingBrace + 1);
            }
            else {
                // @todo Peut-on vérifier la validité du nom varName ?
                String varIsNullName = isNullVarName(varName);

                // Si varIsNullName n'a pas encore été déclarée, il faut le faire
                if (!srcFieldHavingIsNullList.contains(varName)) {
                    srcFieldHavingIsNullList.add(varName);
                    varColumn.put(varIsNullName, Types.BIT);
                }

                // On remplace isNull(XXX) par VAR_XXX_ISNULL dans l'expression
                expression =
                      expression.substring(0, isNullPos) + "(" + varIsNullName + "=="
                      + isNull + ")" + expression.substring(closingBrace + 1);
            }
        }

        return expression;
    }


    /**
     * Renvoie le nom de la variable <code>VAR_XXX_ISNULL</code> associée à la variable <code>XXX</code>.
     *
     * @param varName Le nom de la variable
     *
     * @return Le nom de la variable indiquant si la variable est null
     */
    private static String isNullVarName(String varName) {
        return "VAR_" + varName + "_ISNULL";
    }
}
