package gr.forth.ics.jbenchy;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.List;

/**
 * Provides various ready-made <tt>Order</tt>s.
 * @see Aggregator
 * @author andreou
 */
public class Orders {
    static final String ALIAS_FOR_COUNT_AGGREGATE = "AGGREGATED_COLUMN";
    
    private Orders() {
    }
    
    /**
     * Returns an order denoting the ascending order for the given variable.
     */
    public static Order asc(Object variable) {
        return new OrderImpl(variable, true);
    }
    
    /**
     * Returns an order denoting the ascending order for a COUNT aggregate column - it will not work for
     * other columns.
     */
    public static Order asc() {
        return asc(ALIAS_FOR_COUNT_AGGREGATE);
    }
    
    /**
     * Returns an order denoting the descending order for the given variable.
     */
    public static Order desc(Object variable) {
        return new OrderImpl(variable, false);
    }
    
    /**
     * Returns an order denoting the descending order for a COUNT aggregate column - it will not work for
     * other columns.
     */
    public static Order desc() {
        return desc(ALIAS_FOR_COUNT_AGGREGATE);
    }
    
    /**
     * Returns the SQL's ORDER BY clause for the given list of orders. If null or 
     * empty orders list is provided, the empty string is returned.
     */
    public static String toSqlOrderByClause(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ORDER BY ");
        Iterator<Order> i = orders.iterator();
        sb.append(i.next().toSql());
        while (i.hasNext()) {
            sb.append(", ").append(i.next().toSql());
        }
        return sb.toString();
    }
    
    private static class OrderImpl implements Order {
        private final String variable;
        private final String sql;
        private final boolean ascending;
        
        OrderImpl(Object variable, boolean ascending) {
            Preconditions.checkNotNull(variable, "Null variable");
            this.variable = StringUtils.normalizeVariable(variable);
            this.sql = this.variable + " " + (ascending ? "ASC" : "DESC");
            this.ascending = ascending;
        }
        
        @SuppressWarnings({"unchecked"})
        public int compare(Object o1, Object o2) {
            Comparable c1 = (Comparable)o1;
            Comparable c2 = (Comparable)o2;
            if (ascending) {
                return c1.compareTo(c2);
            } else {
                return c2.compareTo(c1);
            }
        }

        public String toSql() {
            return sql;
        }
        
        @Override
        public String toString() {
            return "[" + toSql() + "]";
        }
    }
}
