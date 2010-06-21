package gr.forth.ics.jbenchy;

import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides various common data types.
 * 
 * @author andreou
 */
public class DataTypes {
    private DataTypes() {
    }
    /**
     * Strings with maximum length 255.
     */
    public static DataType<String> LONG_STRING =
            new StringDataType("LONG_STRING", 255);
    /**
     * Strings with maximum length 64.
     */
    public static DataType<String> MED_STRING =
            new StringDataType("MED_STRING", 64);
    /**
     * Strings with maximum length 16.
     */
    public static DataType<String> SMALL_STRING =
            new StringDataType("SMALL_STRING", 16);
    /**
     * Integers.
     */
    public static DataType<Integer> INTEGER =
            new AbstractDataType<Integer>("INTEGER", "INTEGER", Integer.class) {
                public Integer parse(String value) {
                    return Integer.parseInt(value);
                }
            };
    /**
     * Long integers.
     */
    public static DataType<Long> LONG =
            new AbstractDataType<Long>("LONG", "BIGINT", Long.class) {
                public Long parse(String value) {
                    return Long.parseLong(value);
                }
            };
    /**
     * Short integers.
     */
    public static DataType<Short> SHORT =
            new AbstractDataType<Short>("SHORT", "SMALLINT", Short.class) {
                public Short parse(String value) {
                    return Short.parseShort(value);
                }
            };
    /**
     * Floating numbers of double precision.
     */
    public static DataType<Double> DOUBLE =
            new AbstractDataType<Double>("DOUBLE", "DOUBLE", Double.class) {
                public Double parse(String value) {
                    return Double.parseDouble(value);
                }
            };
    /**
     * Floating numbers of single precision.
     */
    public static DataType<Float> FLOAT =
            new AbstractDataType<Float>("FLOAT", "REAL", Float.class) {
                public Float parse(String value) {
                    return Float.parseFloat(value);
                }
            };
    /**
     * Timestamps (time plus date).
     * @see Timestamps#now()
     */
    public static DataType<Timestamp> TIMESTAMP =
            new AbstractDataType<Timestamp>("TIMESTAMP", "TIMESTAMP",
            Timestamp.class) {
                public Timestamp parse(String value) {
                    return Timestamp.valueOf(value);
                }

                @Override
                public String toSql(Timestamp value) {
                    return "'" + value + "'";
                }
            };

    /**
     * Returns the type of strings with the specified maximum size.
     * @param size the maximum size of strings
     */
    public static DataType<String> string(int size) {
        return new StringDataType("STRING(" + size + ")", size);
    }

    /**
     * Returns the type of decimals which have a specified number of digits,
     * some of which are used for the fractional part of the number.
     * @param totalDigits the total digits that the decimals will comprise
     * @param fractionalDigits the digits, out of total digits, that are to be used for the fractional part
     */
    public static DataType<BigDecimal> decimal(int totalDigits, int fractionalDigits) {
        Preconditions.checkArgument(totalDigits > 0, "Negative or zero total digits");
        Preconditions.checkArgument(fractionalDigits >= 0, "Negative fractional digits");
        Preconditions.checkArgument(fractionalDigits <= totalDigits,
                "Fractional digits greater than total digits");
        String definition = "DECIMAL(" + totalDigits + ", " + fractionalDigits +
                ")";
        return new AbstractDataType<BigDecimal>(definition, definition,
                BigDecimal.class) {
            public BigDecimal parse(String value) {
                return new BigDecimal(value);
            }
        };
    }
    private static final Map<String, DataType<?>> knownTypes = new HashMap<String, DataType<?>>();

    static {
        knownTypes.put("INTEGER", INTEGER);
        knownTypes.put("BIGINT", LONG);
        knownTypes.put("SMALLINT", LONG);
        knownTypes.put("REAL", FLOAT);
        knownTypes.put("DOUBLE", DOUBLE);
        knownTypes.put("DECIMAL", decimal(15, 8));
        knownTypes.put("TIMESTAMP", TIMESTAMP);
        knownTypes.put("VARCHAR", DataTypes.LONG_STRING);
    }

    /**
     * Returns an appropriate data type object that describes an SQL type definition.
     * @param sqlType the SQL type definition
     * @return a data type object that denotes the same type
     */
    public static DataType<?> fromSql(String sqlType) {
        DataType<?> type = knownTypes.get(sqlType);
        if (type == null) {
            throw new RuntimeException("Unknown type: '" + sqlType +
                    "', known types: " + knownTypes.keySet());
        }
        return type;
    }

    private abstract static class AbstractDataType<T> implements DataType<T> {
        private final String name;
        private final String sqlDef;
        private final Class<T> type;

        AbstractDataType(String name, String sqlDefinition, Class<T> type) {
            this.name = name;
            this.sqlDef = sqlDefinition;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getSqlDefinition() {
            return sqlDef;
        }

        public Class<T> getMappedType() {
            return type;
        }

        @Override
        public String toString() {
            return name;
        }

        public String toSql(T value) {
            return String.valueOf(value);
        }
    }

    private static class StringDataType extends AbstractDataType<String> {
        StringDataType(String name, int length) {
            super(name, "VARCHAR(" + length + ")", String.class);
        }

        public String parse(String value) {
            return value;
        }

        @Override
        public String toSql(String value) {
            return "'" + value + "'";
        }
    }
}
