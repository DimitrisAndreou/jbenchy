package gr.forth.ics.jbenchy;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A record is a measurement taken in an environment described by variables bound to specific
 * values. All variables are treated as upper-cased strings, i.e. variables
 * are case-insensitive. Null variables are not permitted.
 * <p>
 * This class implements <tt>Map<String, Object></tt>. Each key is the uppercased
 * string of a variable. {@link #get(Object) } and {@link #containsKey(Object)} 
 * also uppercases the string of the key to answer whether there is a binding
 * to the given variable; thus this class acts as a Map with case-insensitive 
 * string keys.
 * <p>
 * 
 * @author andreou
 */
public class Record implements Map<String, Object> {
    private final Map<String, Object> values;
    
    /**
     * Constructs an empty record.
     */
    public Record() {
        this.values = new LinkedHashMap<String, Object>();
    }
    
    private Record(Map<String, ? extends Object> values) {
        this.values = new LinkedHashMap<String, Object>(values);
    }
    
    /**
     * Constructs a record with a value.
     * @param value the value
     * @see #getValue()
     * @see #putValue(Object)
     */
    public Record(Object value) {
        this();
        putValue(value);
    }
    
    /**
     * Creates a copy of this record.
     */
    public Record copy() {
        return new Record(values);
    }
    
    /**
     * Binds a variable to a value, for this record.
     * @param var the variable to bind
     * @param value the value to bind the variable to
     * @return this
     */
    public Record add(Object var, Object value) {
        put(var.toString().toUpperCase(), value);
        return this;
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        return values.containsKey(key.toString().toUpperCase());
    }

    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    public Object get(Object key) {
        if (key == null) return null;
        return values.get(key.toString().toUpperCase());
    }
    
    /**
     * Returns the value associated with the <tt>null</tt> key, if one exists, or returns <tt>null</tt> otherwise.
     * Equivalent to <tt>get(null)</tt>.
     */
    public Object getValue() {
        return values.get(null);
    }
    
    /**
     * Associates the specified value with the <tt>null</tt> key. Returns the previous associated value,
     * or null if there was none.
     * @param value the value with which the <tt>null</tt> key will be associated
     * @return the previously associated value with the <tt>null</tt> key
     */
    public Object putValue(Object value) {
        return values.put(null, value);
    }
    
    public Object put(String key, Object value) {
        return values.put(key.toUpperCase(), value);
    }
    
    public Object remove(Object key) {
        if (key == null) return null;
        return values.remove(key.toString().toUpperCase());
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
        for (Entry<? extends String, ? extends Object> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        values.clear();
    }

    public Set<String> keySet() {
        return values.keySet();
    }

    public Collection<Object> values() {
        return values.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return values.entrySet();
    }
    
    @Override
    public String toString() {
        return values.toString();
    }
}
