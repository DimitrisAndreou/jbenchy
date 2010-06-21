package gr.forth.ics.jbenchy;

/**
 * A database factory.
 * @author andreou
 */
public interface DbFactory {
    /**
     * Returns an existing database with the specified name.
     * @param dbName the name of the database to return
     * @throws SQLRuntimeException if the operation fails, for instance
     * if no such database can be found
     */
    Database get(String dbName) throws SQLRuntimeException;

    /**
     * Returns whether a database exists with the specified name.
     * @return true if a database is found with the specified name
     */
    boolean exists(String dbName);

    /**
     * Returns an existing database with the specified name, or 
     * creates and returns a new database if no such database can
     * be found.
     * @param dbName the name of the database to return
     * @throws java.sql.SQLRuntimeException if the operation fails
     */
    Database getOrCreate(String dbName) throws SQLRuntimeException;
}
