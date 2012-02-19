/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import net.codjo.expression.help.FunctionHelp;
/**
 * G�re une liste de fonction
 */
public class FunctionManagerTest extends TestCase {
    private FunctionManager manager;
    private FakeFunctionHolder fakeFHolder;


    @Override
    public void setUp() throws Exception {
        fakeFHolder = new FakeFunctionHolder();
        manager = new FunctionManager();
    }


    public void test_allFunctionDeclared() {
        List fakes = fakeFHolder.getAllFunctions();
        List defaultFunctions = manager.getAllFunctions();
        manager.addFunctionHolder(fakeFHolder);

        List all = manager.getAllFunctions();

        all.removeAll(fakes);
        all.removeAll(defaultFunctions);

        assertEquals("Les fonctions definies dans le manager "
                     + "sont les fonctions par d�faut plus celles des Holder declar�s.", 0,
                     all.size());
    }


    public void test_helpFunctions_noFunctionHolder()
          throws Exception {
        List functionList = manager.getAllFunctionsHelp();
        assertEquals(8, functionList.size());
        FunctionHelp help = (FunctionHelp)functionList.get(0);

        assertHelp(help, "in", -1,
                   "Description : renvoie vrai si la colonne pass�e en premier param�tre contient une des valeurs pass�es dans les param�tres suivants\nUsage : in(colonne, valeur1, valeur2, ...)\nExemple : in(Pays, \"FRA\", \"DEU\", \"USA\", \"JPN\")");
        assertHelp((FunctionHelp)functionList.get(1), "notIn", -1,
                   "Description : renvoie vrai si la colonne pass�e en premier param�tre ne contient aucune des valeurs pass�es dans les param�tres suivants\nUsage : notIn(colonne, valeur1, valeur2, ...)\nExemple : notIn(Pays, \"FRA\", \"DEU\", \"USA\", \"JPN\")");
        assertHelp((FunctionHelp)functionList.get(2), "Math.abs", 1,
                   "Description : renvoie la valeur absolue du nombre pass� en param�tre\nUsage : Math.abs(colonne)\nN.B. ne s'applique qu'aux colonnes num�riques\nExemple : Math.abs(Montant)");
        assertHelp((FunctionHelp)functionList.get(3), "iif", 3,
                   "Description : renvoie Valeur si vraie si Condition est remplie et Valeur si fausse dans le cas contraire\nUsage : iif(Condition, Valeur si vraie, Valeur si fausse)\nExemple : iif(in(V�hicule, \"Camion\", \"Voiture\", \"Moto\"), \"V�hicule motoris�\", \"V�hicule non motoris�\")");
        assertHelp((FunctionHelp)functionList.get(4), "Math.max", 2,
                   "Description : renvoie la plus �lev�e des deux donn�es pass�es en param�tres\nUsage : Math.max(donn�e1, donn�e2)\nExemple : Math.max(Cours d'ouverture, Cours de cl�ture)");
        assertHelp((FunctionHelp)functionList.get(5), "Math.min", 2,
                   "Description : renvoie la plus faible des deux donn�es pass�es en param�tres\nUsage : Math.min(donn�e1, donn�e2)\nExemple : Math.min(Cours d'ouverture, Cours de cl�ture)");
        assertHelp((FunctionHelp)functionList.get(6), "isNull", 1,
                   "Description : renvoie vrai si la donn�e pass�e en param�tre est vide, faux dans le cas contraire\nUsage : isNull(donn�e)\nExemple : isNull(Devise)");
        assertHelp((FunctionHelp)functionList.get(7), "isNotNull", 1,
                   "Description : renvoie vrai si la donn�e pass�e en param�tre n'est pas vide, faux dans le cas contraire\nUsage : isNotNull(donn�e)\nExemple : isNotNull(Devise)");
    }


    public void test_helpFunctions_withFunctionHolder()
          throws Exception {
        manager.addFunctionHolder(fakeFHolder);

        List functionList = manager.getAllFunctionsHelp();
        assertEquals(9, functionList.size());
        assertHelp((FunctionHelp)functionList.get(6), "utils.lastDay", 1,
                   "Usage : utils.lastDay(cha�ne)");
    }


    public void test_helpFunctions_withUserFunctionHolder() throws Exception {
        UserFunctionHolder userHolder = new UserFunctionHolder("user");
        userHolder.addFunction(Types.BIT, "afoo", "Usage : mon aide", "public boolean afoo()...");
        manager.addFunctionHolder(userHolder);

        List<FunctionHelp> functionList = manager.getAllFunctionsHelp();
        assertEquals(9, functionList.size());

        assertHelp(getHelpFor(functionList, "user.afoo"), "user.afoo", 0, "Usage : mon aide");
    }


    private static FunctionHelp getHelpFor(List<FunctionHelp> functionList, String name) {
        for (FunctionHelp functionHelp : functionList) {
            if (name.equalsIgnoreCase(functionHelp.getFunctionName())) {
                return functionHelp;
            }
        }
        fail("user.afoo can't be found");
        return null;
    }


    private void assertHelp(FunctionHelp functionHelp, String name, int argNb, String help) {
        assertEquals(name, functionHelp.getFunctionName());
        assertEquals(argNb, functionHelp.getParameterNumber());
        assertEquals(help, functionHelp.getHelp());
    }


    public static class FakeFunctionHolder implements FunctionHolder {
        public List<String> getAllFunctions() {
            List<String> allFunction = new ArrayList<String>();
            allFunction.add("utils.lastDay(AAAAMM)");

            return allFunction;
        }


        public String getName() {
            return "utils";
        }


        public java.sql.Date lastDay(String date)
              throws java.text.ParseException {
            if (date.startsWith("200002")) {
                return java.sql.Date.valueOf("2000-02-29");
            }

            throw new java.text.ParseException("", 0);
        }
    }
}
