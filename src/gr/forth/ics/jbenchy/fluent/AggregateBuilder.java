package gr.forth.ics.jbenchy.fluent;

import gr.forth.ics.jbenchy.*;

/**
 * A "fluent" interface that is used in {@link Aggregator} (through {@link ReportBuilder})
 * to provide a simple way for creating queries of the form "the sum of this variable, 
 * per each distinct combination of values for those variables", or "the average of
 * this variable, for all records".
 * @author andreou
 */
public interface AggregateBuilder {
    /**
     * The sum of a variable.
     * @see Aggregate#sum(Object)
     * @see Aggregator
     */
    PerClause sumOf(Object variable);
    
    /**
     * The average of a variable.
     * @see Aggregate#average(Object)
     * @see Aggregator
     */
    PerClause averageOf(Object variable);
    
    /**
     * The maximum element of a variable.
     * @see Aggregate#max(Object)
     * @see Aggregator
     */
    PerClause maxOf(Object variable);
    
    /**
     * The minimum element of a variable.
     * @see Aggregate#min(Object)
     * @see Aggregator
     */
    PerClause minOf(Object variable);
    
    /**
     * The number of elements.
     * @see Aggregate#count()
     * @see Aggregator
     */
    PerClause count();
    
    interface PerClause {
        
        /**
         * Calculates the aggregate function grouped by the given variables in the same
         * manner as an SQL "GROUP BY" clause.
         * @see Aggregator#report(Aggregate, Object[])
         */
        Records per(Object... variables);
        
        /**
         * Calculates the aggregate function for all available data records.
         * @see Aggregator#report(Aggregate, Object[])
         */
        Records perAll();
    }
}
