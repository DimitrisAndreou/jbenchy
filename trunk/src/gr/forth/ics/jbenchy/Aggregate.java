package gr.forth.ics.jbenchy;

import com.google.common.base.Preconditions;

/**
 * Aggregate functions that can be applied to coalesce multiple values into a single result.
 * 
 * @author andreou
 */
public abstract class Aggregate {
    private Aggregate() {
    }

    /**
     * The sum of the values of the given variable.
     */
    public static Aggregate sum(Object variable) {
        return new AggregateImpl("SUM", variable);
    }

    /**
     * The average of the values of the given variable.
     */
    public static Aggregate average(Object variable) {
        return new AggregateImpl("AVG", variable);
    }

    /**
     * The minimum of the values of the given variable.
     */
    public static Aggregate min(Object variable) {
        return new AggregateImpl("MIN", variable);
    }

    /**
     * The maximum of the values of the given variable.
     */
    public static Aggregate max(Object variable) {
        return new AggregateImpl("MAX", variable);
    }

    /**
     * The count of the values of the given variable.
     */
    public static Aggregate count() {
        return new AggregateImpl("COUNT", "*");
    }

    /**
     * Returns the SQL representation of an aggregation. Examples: <tt>"AVG(SALARY)"</tt>,
     * </tt>MIN(TIME)</tt>, <tt>COUNT(*)</tt> etc.
     */
    public abstract String toSql();
    
    /**
     * Returns name of the variable involved in this aggregate (or a dummy alias in case of a
     * "count", which takes no variable).
     */
    public abstract String getVariableName();

    /**
     * Returns the type of the expected result of this aggregation, in regards to the given schema.
     * @param schema the schema which will be used to resolve the type of a possible
     * variable that this aggregation may contain
     */
    public abstract DataType getResultType(Schema schema);

    private static class AggregateImpl extends Aggregate {
        private final String sqlAggregate;
        private final String variable;

        AggregateImpl(String sqlAggregate, Object variable) {
            this.sqlAggregate = sqlAggregate;
            this.variable = Preconditions.checkNotNull(variable, "Null variable").toString().toUpperCase();
        }

        public String toSql() {
            return sqlAggregate + "(" + variable + ")";
        }

        @Override
        public String toString() {
            return toSql();
        }

        public DataType<?> getResultType(Schema schema) {
            DataType<?> type = schema.getTypeOf(variable);
            if (type != null) {
                return type;
            }
            if (variable.equals("*")) {
                return DataTypes.INTEGER;
            }
            throw new RuntimeException("Variable: " + variable +
                    " is not contained in the following schema:\n" + schema);
        }
        
        public String getVariableName() {
            if (variable.equals("*")) {
                return Orders.ALIAS_FOR_COUNT_AGGREGATE;
            }
            return variable;
        }
    }
}
