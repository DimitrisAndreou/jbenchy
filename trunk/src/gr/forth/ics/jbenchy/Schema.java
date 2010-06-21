package gr.forth.ics.jbenchy;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A schema describes the data types of the allowable values for
 * a list of variables.
 * 
 * @see Aggregator
 * @author andreou
 */
public class Schema {
    private final Map<String, DataType<?>> variables = new LinkedHashMap<String, DataType<?>>();
        
    public Schema() {
    }
    
    public Schema(Schema copy) {
        
    }
    
    /**
     * Returns a collection of all variable names (uppercased).
     */
    public Collection<String> getVariables() {
        return Collections.unmodifiableCollection(variables.keySet());
    }

    /**
     * Returns the data type of a variable of this schema. The name of the
     * variable is derived from <tt>variable.toString().toUppercase()</tt>,
     * so that the <tt>toString()</tt> method of parameter <tt>variable</tt> is only
     * required to return a case-insensitive string of the variable name.
     * @param variable the variable's name (case-insensitive)
     * @return the data type of the variable
     */
    public DataType<?> getTypeOf(Object variable) {
        if (variable == null) {
            return null;
        }
        String var = StringUtils.normalizeVariable(variable);
        return variables.get(var.toUpperCase());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Schema)) {
            return false;
        }
        Schema that = (Schema) o;
        if (!this.getVariables().equals(that.getVariables())) {
            return false;
        }
        for (String variable : getVariables()) {
            if (!this.getTypeOf(variable).
                    equals(that.getTypeOf(variable))) {
                return false;
            }
        }
        return getVariables().equals(that.getVariables());
    }

    @Override
    public int hashCode() {
        //ignoring dimension data types; still technically correct
        return getVariables().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Schema: \n\tVariables: [\n");
        for (String variable : getVariables()) {
            sb.append("\t\t").
                    append(variable).
                    append(" --> ").
                    append(getTypeOf(variable)).append("\n");
        }
        sb.append("\t]\n]");
        return sb.toString();
    }
    
    /**
     * Adds a variable to this schema with the specified data type.
     * 
     * @param variable the variable to add to this schema
     * @param type the type of the variable
     * @return this schema (<strong>not</strong> a copy)
     */
    public Schema add(Object variable, DataType type) {
        Preconditions.checkNotNull(type, "type");
        String var = StringUtils.normalizeVariable(Preconditions.checkNotNull(variable, "variable"));
        Preconditions.checkArgument(var.length() > 0, "Empty variable name");
        if (variables.containsKey(var)) {
            throw new IllegalArgumentException("Variable '" +
                    var + "' already exists. Existing variables: " +
                    variables.keySet());
        }
        variables.put(var, type);
        return this;
    }
}
