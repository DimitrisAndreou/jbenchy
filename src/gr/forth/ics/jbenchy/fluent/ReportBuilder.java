package gr.forth.ics.jbenchy.fluent;

import gr.forth.ics.jbenchy.*;
import java.util.List;

/**
 * A "fluent" interface that is used in {@link Aggregator} to provide declarative
 * ordering and filtering.
 * @author andreou
 */
public interface ReportBuilder extends AggregateBuilder {
    /**
     * Creates a report categorized by the specifed variables and aggregated with the given
     * function, honoring ordering and filtering imposed by this object.
     * @see Aggregator#report(gr.forth.ics.jbenchy.Aggregate, Object[])
     */
    Records report(Aggregate aggr, Object... variables);

    /**
     * Returns the union of all values that the specified variable takes,
     * honoring ordering and filtering imposed by this object. 
     */
    List<Object> domainOf(Object variable);

    /**
     * Declares that this instance will use the specified orders. Note that additional
     * calls to this method do not replace prior declared orders.
     * @return this
     * @see Orders
     */
    ReportBuilder ordered(Order... orders);

    /**
     * Declares that this instance will use the specified orders. Note that additional
     * calls to this method do not replace prior declared orders.
     * @return this
     * @see Orders
     */
    ReportBuilder ordered(List<Order> orders);

    /**
     * Declares that this ReporterBuilder is constrained to records
     * allowed by the specified filter. The
     * {@link #report(gr.forth.ics.jbenchy.Aggregate, Object[])} and {@link #deleteRecords}
     * methods will only apply to these records.
     * 
     * <p>
     * Note that subsequent calls to this method further constrain this
     * ReporterBuilder; they do not replace existing filters. For a record
     * to be reported, it is needed that it is allowed by all defined filters, i.e.
     * with conjunction ("and") semantics.
     * 
     * @return this
     * @see #report(gr.forth.ics.jbenchy.Aggregate, Object[])
     * @see #deleteRecords()
     * @see Filters
     */
    ReportBuilder filtered(Filter filter);

    /**
     * Deletes all records which are not filtered out. For example, to delete all records having
     * <tt>COLOR=="RED"</tt>, this idiom would probably be used: <BR>
     * <tt>aggregator.filtered(Filters.eq("COLOR", "RED")).deleteRecords();</tt>
     * @see #filtered(Filter)
     */
    void deleteRecords();
}
