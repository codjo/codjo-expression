Framework codjo.net
===================

This library is part of the [framework codjo.net](http://codjo.net) effort.

# Description

Cette librairie permet d'evaluer des expression avec une syntaxe proche de java.
L'implantation utilisee pour l'evaluation des expressions est dynamicJava.

# Features

## Aides

Une nouvelle methode plus complete pour l'aide sur les fonctions est mis en place cf net.codjo.expression.help.
L'ancien moyen symbolise par FunctionHolder.getAllFunctions est maintenant deprecated. 
Exemple d'utilisation avec le comportement par defaut:

```java
// Init
manager = new FunctionManager();
manager.addFunctionHolder(myFunctionHolder);

// Recuperation de l'aide
List<FunctionHelp> list = manager.getAllFunctionsHelp();

// Utilisation
FunctionHelp help = list.get(0);
System.out.println("name = " + help.getFunctionName()); // "my.lastDay"
System.out.println("nb argument = " + help.getParameterNumber()); // 1
System.out.println("tooltip = " + help.getHelp()); // "Usage : my.lastDay(cha”ne)"
```

## Customization !

Il est possible de surcharger le comportement par defaut de l'aide. Pour cela il suffit de faire
implementer l'interface FunctionHolderHelp par son FunctionHolder (exemple KernelFunctionHolder).
Resultat :

```java
List<FunctionHelp> list = manager.getAllFunctionsHelp();

assertHelp(list.get(6), "isNull", 1, "Usage : isNull(variable)");
assertHelp(list.get(7), "isNotNull", 1, "Usage : isNotNull(variable)");
```

## Expression
La classe Expression permet de definir une seule expression (qui peux utiliser plusieurs valeur en entree),
elle s'occupe de la creation du manager...
Exemple d'utilisation

```java
Expression expression = new Expression("iif(Valeur == \"*\", null, Valeur)" , Types.VARCHAR, null, Types.VARCHAR);

expression.init();

Object resultat = expression.compute("val");
assertEquals("val", resultat);

resultat = expression.compute("*");
assertEquals(null, resultat);
```

## Fonctions utilisateur

Il est maintenant possible de definir des fonctions parametrables par
l'utilisateur.

```java
UserFunctionHolder users = new UserFunctionHolder("users");
users.addFunction(Types.INTEGER, "mySquare", "users.mySquare(entier)", "public static int mySquare(int i) { return i * i; }");
functionManager.addFunctionHolder(users);
```

## Classe FindUses

Cette classe permet de connaitre la liste des colonnes sources qui sont
reellement utilisees par les expressions.

```java
FindUses uses = new FindUses();
uses.add("SRC_A + SRC_B");
FindUses.Report report = uses.buildReport();
Collection usedColumns = report.getUsedSourceColumns();
// usedColumns contient le nom des colonnes sources (String)
```
