package gr.forth.ics.jbenchy;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A container of specific records.
 * <p>
 * This class implements <tt>Iterable<Record></tt>, so this idiom is possible:
 * <pre>
 * Records records = ...;
 * for (Record record : records) {
 *     ...
 * }
 * </pre>
 * @author andreou
 */
public class Records implements Iterable<Record> {
    private final List<Record> records;
    private final List<String> variables;

    /**
     * Constructs a Records instance with a list of records and a list of
     * variables. The provided lists are not defensively copied; changes in them
     * are reflected in this object too.
     */
    public Records(List<Record> records, List<?> variables) {
        this.records = Preconditions.checkNotNull(records, "records");
        this.variables = toStringList(Preconditions.checkNotNull(variables, "variables"));
    }
    
    
    public static Records union(Records ... multipleRecords) {
        if (multipleRecords.length == 0) {
            throw new IllegalArgumentException("No Records objects provided");
        }
        List<String> lastVars = null;
        for (Records records : multipleRecords) {
            if (lastVars != null) {
                if (!records.variables.equals(lastVars)) {
                    throw new IllegalArgumentException("Cannot create union of records. Some Records object has these variables: "
                            + lastVars + " and some other has these: " + records.variables
                            + ", they should be equal");
                }
            }
            lastVars = records.variables;
        }
        int totalSize = 0;
        for (Records records : multipleRecords) {
            totalSize += records.records.size();
        }
        List<Record> allRecords = new ArrayList<Record>(totalSize);
        for (Records records : multipleRecords) {
            allRecords.addAll(records.records);
        }
        return new Records(allRecords, lastVars);
    }
    
    private static List<String> toStringList(List<?> variables) {
        List<String> list = new ArrayList<String>(variables.size());
        for (Object var : variables) {
            list.add(var.toString().toUpperCase());
        }
        return list;
    }

    /**
     * Returns the variables that are known to this Records instance.
     */
    public List<String> getVariables() {
        return Collections.unmodifiableList(variables);
    }
    
    /**
     * Returns an unmodifiable list of the Record instances of this Records instance.
     */
    public List<Record> list() {
        return Collections.unmodifiableList(records);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Record record : this) {
            sb.append(record.toString()).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Returns an iterator over this instance's records. The mutative methods of Iterator
     * are not supported.
     */
    public Iterator<Record> iterator() {
        return list().iterator();
    }
    
    /**
     * Returns the union of all values that the specified variables takes in these records.
     */
    public Set<Object> getDomainOf(Object variable) {
        String var = convertToString(variable);
        Preconditions.checkState(variables.contains(var), "Variable not contained in these records");
        Set<Object> domain = new HashSet<Object>();
        for (Record record : this) {
            domain.add(record.get(var));
        }
        return domain;
    }
    
    private String convertToString(Object variable) {
        return variable == null ? null : variable.toString().toUpperCase().trim();
    }
}
