package gr.forth.ics.jbenchy;

import java.util.Comparator;

/**
 * An SQL-based order (sort) definition.
 * @author andreou
 */
public interface Order extends Comparator<Object> {
    String toSql();
}
