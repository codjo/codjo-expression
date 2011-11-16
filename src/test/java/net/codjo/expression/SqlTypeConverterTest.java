/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.math.BigDecimal;
import java.sql.Types;
import junit.framework.TestCase;
/**
 * Classe de test de {@link SqlTypeConverter}.
 */
public class SqlTypeConverterTest extends TestCase {
    public void test_defaultValues() throws Exception {
        checkdefaultValue(new BigDecimal(0), Types.NUMERIC);
        checkdefaultValue(new Float(0.0f), Types.FLOAT);

        checkdefaultValue(new java.sql.Date(0), Types.DATE);
        checkdefaultValue(new java.sql.Date(0), Types.TIMESTAMP);
    }


    private void checkdefaultValue(Object defaultValue, int sqlType) {
        Object defaultSqlValue =  new SqlTypeConverter().getDefaultSqlValue(new Integer(sqlType));
        assertEquals(defaultValue, defaultSqlValue);
        assertSame(defaultValue.getClass(), defaultSqlValue.getClass());
    }
}
