/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import koala.dynamicjava.tree.AddExpression;
import koala.dynamicjava.tree.AndExpression;
import koala.dynamicjava.tree.ArrayAllocation;
import koala.dynamicjava.tree.ArrayInitializer;
import koala.dynamicjava.tree.DivideExpression;
import koala.dynamicjava.tree.EqualExpression;
import koala.dynamicjava.tree.GreaterExpression;
import koala.dynamicjava.tree.GreaterOrEqualExpression;
import koala.dynamicjava.tree.LessExpression;
import koala.dynamicjava.tree.LessOrEqualExpression;
import koala.dynamicjava.tree.Literal;
import koala.dynamicjava.tree.MinusExpression;
import koala.dynamicjava.tree.MultiplyExpression;
import koala.dynamicjava.tree.NotEqualExpression;
import koala.dynamicjava.tree.ObjectMethodCall;
import koala.dynamicjava.tree.OrExpression;
import koala.dynamicjava.tree.PrimitiveType;
import koala.dynamicjava.tree.QualifiedName;
import koala.dynamicjava.tree.ReferenceType;
import koala.dynamicjava.tree.SimpleAllocation;
import koala.dynamicjava.tree.SimpleAssignExpression;
import koala.dynamicjava.tree.SubtractExpression;
/**
 * Classe de test de AbstractVisitor.
 * 
 * <p>
 * <b>ATTENTION</b> : L'erreur 'Abstrac' sans 't' est volontaire. Pour que maven execute
 * le test (on ne passe pas dans le pattern exclude 'Abstract.java').
 * </p>
 *
 * @version $Revision: 1.3 $
 */
public class AbstracVisitorTest extends TestCase {
    private AbstractVisitor visitor;

    public void test_notUsed() throws Exception {
        Method[] allMethods = AbstractVisitor.class.getMethods();
        for (int idx = 0; idx < allMethods.length; idx++) {
            Method method = allMethods[idx];
            if ("visit".equals(method.getName())
                    && method.getDeclaringClass() == AbstractVisitor.class
                    && !Modifier.isAbstract(method.getModifiers())) {
                assertMethodIsUnsupported(method);
            }
        }
    }


    private void assertMethodIsUnsupported(Method method)
            throws IllegalAccessException {
        try {
            method.invoke(visitor, new Object[] {null});
            fail("La methode doit echouer ! " + method);
        }
        catch (InvocationTargetException error) {
            assertEquals(UnsupportedOperationException.class,
                error.getTargetException().getClass());
        }
    }


    protected void setUp() throws Exception {
        visitor = new MockVisitor();
    }

    public static class MockVisitor extends AbstractVisitor {
        public Object visit(Literal literal) {
            return null;
        }


        public Object visit(QualifiedName name) {
            return null;
        }


        public Object visit(ObjectMethodCall call) {
            return null;
        }


        public Object visit(PrimitiveType type) {
            return null;
        }


        public Object visit(ReferenceType type) {
            return null;
        }


        public Object visit(ArrayInitializer initializer) {
            return null;
        }


        public Object visit(ArrayAllocation allocation) {
            return null;
        }


        public Object visit(SimpleAllocation allocation) {
            return null;
        }


        public Object visit(MinusExpression expression) {
            return null;
        }


        public Object visit(MultiplyExpression expression) {
            return null;
        }


        public Object visit(DivideExpression expression) {
            return null;
        }


        public Object visit(AddExpression expression) {
            return null;
        }


        public Object visit(SubtractExpression expression) {
            return null;
        }


        public Object visit(LessExpression expression) {
            return null;
        }


        public Object visit(GreaterExpression expression) {
            return null;
        }


        public Object visit(LessOrEqualExpression expression) {
            return null;
        }


        public Object visit(GreaterOrEqualExpression expression) {
            return null;
        }


        public Object visit(EqualExpression expression) {
            return null;
        }


        public Object visit(NotEqualExpression expression) {
            return null;
        }


        public Object visit(AndExpression expression) {
            return null;
        }


        public Object visit(OrExpression expression) {
            return null;
        }


        public Object visit(SimpleAssignExpression expression) {
            return null;
        }
    }
}
