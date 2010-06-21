package gr.forth.ics.jbenchy.diagram;

import gr.forth.ics.jbenchy.Record;
import java.util.List;

/**
 * A diagram is a tabular data set, comprising a number of named dimensions (variables), and a
 * value for each cell.
 * <p>
 * A diagram maintains the (ordered) domain of each variable. For example, if there are two variables,
 * "COLOR" and "SIZE", and color has domain: [RED, GREEN, BLUE] and size has domain: [SMALL, MEDIUM, BIG],
 * then the following indices correspond to the points appearing below:
 * <table border="1">
 * <thead>
 * <tr>
 * <th>INDEX</th>
 * <th>POINT</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>(0, 0)</td>
 * <td>(RED, SMALL)</td>
 * </tr>
 * <tr>
 * <td>(0, 1)</td>
 * <td>(RED, MEDIUM)</td>
 * </tr>
 * <tr>
 * <td>(0, 2)</td>
 * <td>(RED, BIG)</td>
 * </tr>
 * <tr>
 * <td>(1, 0)</td>
 * <td>(GREEN, SMALL)</td>
 * </tr>
 * <tr>
 * <td>(1, 1)</td>
 * <td>(GREEN, MEDIUM)</td>
 * </tr>
 * <tr>
 * <td>(1, 2)</td>
 * <td>(GREEN, BIG)</td>
 * </tr>
 * <tr>
 * <td>(2, 0)</td>
 * <td>(BLUE, SMALL)</td>
 * </tr>
 * <tr>
 * <td>(2, 1)</td>
 * <td>(BLUE, MEDIUM)</td>
 * </tr>
 * <tr>
 * <td>(2, 2)</td>
 * <td>(BLUE, BIG)</td>
 * </tr>
 * </tbody>
 * </table>
 * @author andreou
 */
public interface Diagram {
    /**
     * Returns the title of this diagram.
     */
    String getTitle();
    
    /**
     * Returns the label describing the contents of this diagram cells.
     */
    String getRangeLabel();
    
    /**
     * Returns the label of a variable (expressed by its index, counting from 0).
     */
    String getLabelOf(int variableIndex);
    
    /**
     * Returns the union of all possible values that a variable (expressed by its index, counting from 0) takes.
     */
    List<Object> getDomain(int variableIndex);
    
    /**
     * Returns the record which resides in the specified diagram cell, denoted by an offset for each variable.
     * @param variableIndices the indices that describe the position of the record. Each variable index means the offset
     * of the value in the domain of that variable's domain.
     * @return the record residing in the described cell
     */
    Record getRecordAt(int... variableIndices);
    
    /**
     * Returns the ordered list of variables, corresponding to the dimensions of this diagram.
     */
    List<String> getVariables();
    
    /**
     * Returns the number of variables (or dimensions) in this diagram.
     */
    int getVariableCount();
    
    /**
     * Returns the domain size for a variable (denoted by its index). This is the
     * upper bound (exclusively) of valid index values that can be used 
     * for the N<i>th</i> variable index in {@link #getRecordAt(int[])} method.
     * <p>
     * This is equivalent to <tt>getDomain(variableIndex).size()</tt>.
     * @param variableIndex the variable of which to return the domain size
     * @return the size of the variable domain
     */
    int getDomainSize(int variableIndex);
}