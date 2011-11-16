/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.expression;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import koala.dynamicjava.interpreter.TreeInterpreter;
/**
 * Convertion de type SQL.
 *
 * @author $Author: virasis $
 * @version $Revision: 1.11 $
 */
final class SqlTypeConverter {
    private static final Map TYPES;

    static {
        Map definers = new HashMap();

        definers.put(new Integer(Types.CHAR), new CharTypeMapper());
        definers.put(new Integer(Types.VARCHAR), new StringTypeMapper());
        definers.put(new Integer(Types.LONGVARCHAR), new StringTypeMapper());
        definers.put(new Integer(Types.INTEGER), new IntegerTypeMapper());
        definers.put(new Integer(Types.SMALLINT), new IntegerTypeMapper());
        definers.put(new Integer(Types.BIGINT), new LongTypeMapper());
        definers.put(new Integer(Types.FLOAT), new FloatTypeMapper());
        definers.put(new Integer(Types.DOUBLE), new DoubleTypeMapper());
        definers.put(new Integer(Types.BIT), new BooleanTypeMapper());
        definers.put(new Integer(Types.TIMESTAMP), new DateTypeMapper());
        definers.put(new Integer(Types.DATE), new DateTypeMapper());
        definers.put(new Integer(Types.TIME), new TimeTypeMapper());
        definers.put(new Integer(Types.DECIMAL), new BigDecimalTypeMapper());
        definers.put(new Integer(Types.NUMERIC), new BigDecimalTypeMapper());

        definers.put(char.class, new CharTypeMapper());
        definers.put(String.class, new StringTypeMapper());
        definers.put(int.class, new IntegerTypeMapper());
        definers.put(long.class, new LongTypeMapper());
        definers.put(float.class, new FloatTypeMapper());
        definers.put(double.class, new DoubleTypeMapper());
        definers.put(boolean.class, new BooleanTypeMapper());
        definers.put(Timestamp.class, new DateTypeMapper());
        definers.put(Time.class, new TimeTypeMapper());
        definers.put(java.sql.Date.class, new DateTypeMapper());
        definers.put(java.util.Date.class, new DateTypeMapper());
        definers.put(BigDecimal.class, new BigDecimalTypeMapper());

        TYPES = Collections.unmodifiableMap(definers);
    }

    private Map overridedDefaultValue = new HashMap();

    public SqlTypeConverter() {}

    // ----------------------------------------------------------------------------------
    /**
     * Indique si le type sql correspond a un scalaire.
     *
     * @param sqlType devine !
     *
     * @return idem !
     */
    public static boolean isScalar(Integer sqlType) {
        switch (sqlType.intValue()) {
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.FLOAT:
            case Types.DOUBLE:
            case Types.BIT:
            case Types.BIGINT:
                return true;
            default:
                return false;
        }
    }


    /**
     * Indique si le type sql correspond a un reel.
     *
     * @param sqlType devine !
     *
     * @return idem !
     */
    public static boolean isDouble(Integer sqlType) {
        switch (sqlType.intValue()) {
            case Types.FLOAT:
            case Types.DOUBLE:
                return true;
            default:
                return false;
        }
    }


    /**
     * Indique si le type sql correspond a une date.
     *
     * @param sqlType devine !
     *
     * @return idem !
     */
    public static boolean isDate(Integer sqlType) {
        switch (sqlType.intValue()) {
            case Types.DATE:
            case Types.TIMESTAMP:
                return true;
            default:
                return false;
        }
    }


    /**
     * Indique si le type sql correspond a un entier.
     *
     * @param sqlType devine !
     *
     * @return idem !
     */
    public static boolean isInteger(Integer sqlType) {
        switch (sqlType.intValue()) {
            case Types.SMALLINT:
            case Types.INTEGER:
                return true;
            default:
                return false;
        }
    }


    /**
     * Indique si le type sql correspond a une String.
     *
     * @param sqlType devine !
     *
     * @return idem !
     */
    public static boolean isString(Integer sqlType) {
        switch (sqlType.intValue()) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return true;
            default:
                return false;
        }
    }


    /**
     * Indique si le type SQL est de format numerique.
     *
     * @param sqlType type sql.
     *
     * @return <code>true</code> si oui.
     */
    public static boolean isNumeric(int sqlType) {
        return sqlType == Types.INTEGER || sqlType == Types.NUMERIC
        || sqlType == Types.FLOAT || sqlType == Types.DOUBLE || sqlType == Types.DECIMAL
        || sqlType == Types.SMALLINT || sqlType == Types.BIGINT;
    }


    // ----------------------------------------------------------------------------------
    // Conversion
    /**
     * Retourne la valeur par defaut attache a un type SQL.
     *
     * @param sqlType
     *
     * @return La valeur zero / ou null
     */
    public Object getDefaultSqlValue(Integer sqlType) {
        if (overridedDefaultValue.containsKey(sqlType)) {
            return overridedDefaultValue.get(sqlType);
        }
        return getTypeMapper(sqlType).getDefaultSqlValue();
    }


    public void setDefaultNullValue(int sqlTypes, Object defaultValue) {
        overridedDefaultValue.put(new Integer(sqlTypes), defaultValue);
    }


    /**
     * Convertion classe java en type SQL.
     *
     * @param cl La classe a convertir
     *
     * @return Type sql associé à la classe java.
     *
     * @throws UnsupportedConvertionException type Java non supporté
     */
    public static Integer toSqlType(Class cl) {
        TypeMapper typeMapper = (TypeMapper)TYPES.get(cl);
        if (typeMapper == null) {
            throw new UnsupportedConvertionException("Type JAVA non supporte : " + cl);
        }
        return typeMapper.getSqlType();
    }


    /**
     * Retourne le type Java associe a un type SQL.
     *
     * @param sqlType
     *
     * @return Une String contenant le type Java (ex : "String")
     */
    public static Class toJavaType(int sqlType) {
        return getTypeMapper(sqlType).getJavaType();
    }


    public void defineVariableInto(TreeInterpreter interpreter, String variableName,
        Integer sqlType) {
        getTypeMapper(sqlType).define(interpreter, variableName);
    }


    private static TypeMapper getTypeMapper(int type) {
        return getTypeMapper(new Integer(type));
    }


    private static TypeMapper getTypeMapper(Integer type) {
        TypeMapper typeMapper = (TypeMapper)TYPES.get(type);

        if (typeMapper == null) {
            throw new IllegalArgumentException("Type SQL non supporte : " + type);
        }

        return typeMapper;
    }

    // ----------------------------------------------------------------------------------
    // Inner Class
    interface TypeMapper {
        void define(TreeInterpreter interpreter, String name);


        Object getDefaultSqlValue();


        Integer getSqlType();


        Class getJavaType();
    }

    static class UnsupportedConvertionException extends IllegalArgumentException {
        UnsupportedConvertionException(String msg) {
            super(msg);
        }
    }


    private abstract static class AbstractTypeMapper implements TypeMapper {
        private final Integer sqlType;
        private final Object defaultValue;
        private final Class javaType;

        public AbstractTypeMapper(Class javaType, int sqlType, Object defaultValue) {
            this.sqlType = new Integer(sqlType);
            this.defaultValue = defaultValue;
            this.javaType = javaType;
        }

        public void define(TreeInterpreter interpreter, String name) {
            interpreter.defineVariable(name, getDefaultSqlValue(), getJavaType());
        }


        public Object getDefaultSqlValue() {
            return defaultValue;
        }


        public Integer getSqlType() {
            return sqlType;
        }


        public Class getJavaType() {
            return javaType;
        }
    }


    private static class StringTypeMapper extends AbstractTypeMapper {
        public StringTypeMapper() {
            super(String.class, Types.VARCHAR, "");
        }
    }


    private static class CharTypeMapper extends AbstractTypeMapper {
        public CharTypeMapper() {
            super(String.class, Types.CHAR, "");
        }
    }


    private static class IntegerTypeMapper extends AbstractTypeMapper {
        public IntegerTypeMapper() {
            super(int.class, Types.INTEGER, new Integer(0));
        }
    }


    private static class LongTypeMapper extends AbstractTypeMapper {
        public LongTypeMapper() {
            super(long.class, Types.BIGINT, new Long(0L));
        }
    }


    private static class FloatTypeMapper extends AbstractTypeMapper {
        public FloatTypeMapper() {
            super(float.class, Types.FLOAT, new Float(0.0f));
        }
    }


    private static class DoubleTypeMapper extends AbstractTypeMapper {
        public DoubleTypeMapper() {
            super(double.class, Types.DOUBLE, new Double(0.0));
        }
    }


    private static class BigDecimalTypeMapper extends AbstractTypeMapper {
        public BigDecimalTypeMapper() {
            super(BigDecimal.class, Types.NUMERIC, new BigDecimal(0));
        }
    }


    private static class DateTypeMapper extends AbstractTypeMapper {
        public DateTypeMapper() {
            super(Date.class, Types.DATE, new java.sql.Date(0));
        }
    }


    private static class TimeTypeMapper extends AbstractTypeMapper {
        public TimeTypeMapper() {
            super(Time.class, Types.TIME, new java.sql.Time(0));
        }
    }


    private static class BooleanTypeMapper extends AbstractTypeMapper {
        public BooleanTypeMapper() {
            super(boolean.class, Types.BIT, Boolean.FALSE);
        }
    }
}
