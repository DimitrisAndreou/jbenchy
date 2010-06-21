package gr.forth.ics.jbenchy;

import com.google.common.base.Preconditions;
import java.sql.SQLException;

/**
 * A database can create and contain Aggregators.
 * @see Aggregator
 * @author andreou
 */
public abstract class Database {
    /**
     * Creates an aggregator that is backed up with a strictly new table (with the specified name)
     * in this database and adheres to given specified schema. In other words, 
     * if a table with the same name exists, it is dropped and recreated.
     * 
     * @param schema the schema that the created aggregator will adhere to
     * @param name the {@link Aggregator#getName() name} of the aggregator, and
     * also the name of the table that will back the returned aggregator up
     * @return a new Aggregator instance
     * @throws SQLRuntimeException when the underlying database throws an exception
     */
    public Aggregator forceCreate(Schema schema, String name) throws SQLRuntimeException {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(schema, "schema");
        delete(name);
        return create(schema, name);
    }

    /**
     * Returns an aggregator with the specified schema and name, backed up with an existing
     * table with the same name, or a new one if no such table exists. 
     * <p>
     * Note that if an existing table is found, the returned aggregator may have less specific
     * data types in comparison to the ones used to originally created it (this affects
     * types which are parameterized by custom lengths).
     * 
     * @param schema the schema that the created aggregator will adhere to
     * @param name the {@link Aggregator#getName() name} of the aggregator, and
     * also the name of the table that will back the returned aggregator up
     * @return a new Aggregator instance
     * @throws SQLRuntimeException when the underlying database throws an exception
     */
    public Aggregator getOrCreate(Schema schema, String name) throws SQLRuntimeException {
        if (schema == null || name == null) {
            throw new NullPointerException();
        }
        Aggregator aggr = get(name);

        if (aggr == null) {
            aggr = create(schema, name);
        }
        return aggr;
    }

    /**
     * Drops a table with the specified name.
     * @param name the name of the table to drop
     * @return true if a table was actually dropped
     */
    public abstract boolean delete(String name);
    
    /**
     * Returns an aggregator with the specified name, which is backed up with an
     * existing table of the same name. This operation is guaranteed to never
     * drop or create tables; an SQLException will be thrown if the specified table
     * is not found.
     * @param name the name of the aggregator to return, as well as the name of the
     * backing table
     * @return an Aggregator instance with the specified name
     * @throws SQLRuntimeException if the specified table is not found, or the database
     * throws an exception
     */
    public abstract Aggregator get(String name) throws SQLRuntimeException;

    /**
     * Returns an aggregator backed up by a created table with the given schema and name.
     * If such a table already exists, then an SQLException will be thrown.
     * @param schema the schema that the returned aggregator will adhere to
     * @param name the name of the returned aggregator, as well as the name of the backing table
     * @return an Aggregator instance with the specified name
     * @throws java.sql.SQLException when the underlying database throws an exception
     */
    public abstract Aggregator create(Schema schema, String name) throws SQLRuntimeException;

    /**
     * Attempts to cleanly shutdown the database.
     */
    public abstract void shutDown();
}
