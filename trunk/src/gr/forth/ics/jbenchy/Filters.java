package gr.forth.ics.jbenchy;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Provides various ready-made <tt>Filter</tt>s.
 * @see Aggregator
 * @see Filter
 * @author andreou
 */
public class Filters {
    /**
     * A filter that accepts anything.
     */
    public static final Filter NULL_FILTER = new PlainFilter("0=0");

    private Filters() {
    }

    /**
     * Returns a filter that represents the AND combination of many filters.
     */
    public static Filter and(Filter... filters) {
        return and(Arrays.asList(filters));
    }

    /**
     * Returns a filter that represents the AND combination of many filters.
     */
    public static Filter and(List<Filter> filters) {
        return combinedFilter(filters, "AND");
    }

    /**
     * Returns a filter that represents the OR combination of many filters.
     */
    public static Filter or(Filter... filters) {
        return or(Arrays.asList(filters));
    }

    /**
     * Returns a filter that represents the OR combination of many filters.
     */
    public static Filter or(List<Filter> filters) {
        return combinedFilter(filters, "OR");
    }

    /**
     * Returns a filter that only allows records which have
     * the specified value for the given variable.
     */
    public static Filter eq(Object variable, Object value) {
        return newFilter(variable, "=", value);
    }

    /**
     * Returns a filter that only allows records which do <strong>NOT</strong>
     * have the specified value for the given variable.
     */
    public static Filter notEq(Object variable, Object value) {
        return newFilter(variable, "<>", value);
    }

    /**
     * Returns a filter that only allows records which have a strictly greater
     * value for the given variable from the specified value.
     */
    public static Filter gt(Object variable, Object value) {
        return newFilter(variable, ">", value);
    }

    /**
     * Returns a filter that only allows records which have a greater or
     * equal value for the given variable to the specified value.
     */
    public static Filter ge(Object variable, Object value) {
        return newFilter(variable, ">=", value);
    }

    /**
     * Returns a filter that only allows records which have a strictly less
     * value for the given variable from the specified value.
     */
    public static Filter lt(Object variable, Object value) {
        return newFilter(variable, "<", value);
    }

    /**
     * Returns a filter that only allows records which have a less or
     * equal value for the given variable to the specified value.
     */
    public static Filter le(Object variable, Object value) {
        return newFilter(variable, "<=", value);
    }

    private static Filter newFilter(Object variable, String symbol, Object value) {
        Preconditions.checkNotNull(variable, "variable");

        //we do not allow null values anyway
        Preconditions.checkNotNull(value, "value");
        return new VariableFilter(variable, symbol, value);
    }

    private static Filter combinedFilter(final Collection<Filter> filters, final String operator) {
        if (filters.isEmpty()) {
            return NULL_FILTER;
        } else {
            return new Filter() {
                public String toSql(Schema schema) {
                    Iterator<Filter> i = filters.iterator();
                    StringBuilder sb = new StringBuilder();
                    sb.append(i.next().toSql(schema));
                    while (i.hasNext()) {
                        sb.append(" ").append(operator).append(" ").append(i.next().toSql(schema));
                    }
                    return sb.toString();
                }
                
                @Override
                public String toString() {
                    return "[" + operator + " Filter: " + filters + "]";
                }
            };
        }
    }

    private static class PlainFilter implements Filter {
        private final String sql;

        PlainFilter(String sql) {
            this.sql = Preconditions.checkNotNull(sql);
        }

        public String toSql(Schema schema) {
            return sql;
        }

        @Override
        public String toString() {
            return sql;
        }
    }

    private static class VariableFilter implements Filter {
        private final String variable;
        private final String symbol;
        private final Object value;

        VariableFilter(Object variable, String symbol, Object value) {
            this.variable = variable.toString().toUpperCase();
            this.symbol = symbol;
            this.value = value;
        }

        public String toSql(Schema schema) {
            DataType valueType = schema.getTypeOf(variable);
            if (valueType == null) {
                throw new IllegalArgumentException("Cannot filter variable: '" +
                        variable + " which does not belong to" +
                        " target schema: " + schema);
            }
            try {
                //this is unsafe. If a value that cannot be handled by the specific DataType is passed,
                //then a ClassCastException would occur in this method call.
                @SuppressWarnings("unchecked")
                String sql = valueType.toSql(value);
                return variable + symbol + sql;
            } catch (ClassCastException e) {
                throw new RuntimeException("DataType: '" + valueType + "' cannot handle the value: " +
                        "'" + value + "' of type: " + value.getClass().getName(), e);
            }
        }

        @Override
        public String toString() {
            return "[" + variable + symbol + value + "]";
        }
    }
}
