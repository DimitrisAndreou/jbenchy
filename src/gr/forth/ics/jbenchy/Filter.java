package gr.forth.ics.jbenchy;

/**
 * An SQL-based filter. A filters is the direct analog of SQL's WHERE clause,
 * for example <tt>Filters.eq("VAR", "X")</tt> allows only those results
 * which <em>do</em> have the value "X" for variable "VAR", filtering out all the rest.
 * @author andreou
 * @see Filters
 */
public interface Filter {
    /**
     * Returns the SQL part that can be embedded in a WHERE clause and express the semantics of this filter.
     * @param schema the schema of the aggregator/table that the filter will apply to. This is needed to be
     * able to correctly render values in the SQL string, something for which in some cases the data type of the 
     * value is required
     */
    String toSql(Schema schema);
}
