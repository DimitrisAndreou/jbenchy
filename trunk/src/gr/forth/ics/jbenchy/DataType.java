package gr.forth.ics.jbenchy;

/**
 * A data type, which can be mapped to both Java and SQL.
 * @author andreou
 */
public interface DataType<T> {
    /**
     * Returns a name of this type.
     */
    String getName();
    
    /**
     * Returns the SQL definition of this type. For example, an integer type would have
     * a "INT" SQL definition.
     */
    String getSqlDefinition();
    
    /**
     * Returns the Java type that this data type denotes.
     */
    Class<T> getMappedType();
    
    /**
     * Parses a string to create a Java object, according to the semantics of this data type.
     * @param value a textual value that is to be mapped onto a Java object
     * @return a Java object that is the result of parsing the textual value, if that value is valid
     */
    T parse(String value);
    
    /**
     * Returns a string, embeddable in an SQL query, that has the same value as the provided
     * Java object. For example, a string value would have to be enclosed in "'" characters.
     * 
     * @param value the Java object to be rendered in SQL
     * @return the string representation of the value that can be safely embedded in SQL 
     */
    String toSql(Object value);
}
