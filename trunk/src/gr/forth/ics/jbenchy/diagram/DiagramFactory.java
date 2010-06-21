package gr.forth.ics.jbenchy.diagram;

import com.google.common.base.Preconditions;
import gr.forth.ics.jbenchy.Order;
import gr.forth.ics.jbenchy.Orders;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Records;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A factory of diagrams, based on {@link gr.forth.ics.jbenchy.Aggregator} reports.
 * @author andreou
 */
public class DiagramFactory {
    private DiagramFactory() {
    }

    /**
     * Creates and returns a diagram using the given records, and assuming ascending order
     * for every variable.
     * @param records the records that the diagram will contain
     */
    public static DiagramImpl newDiagram(Records records) {
        List<Order> orders = new ArrayList<Order>();
        for (String variable : records.getVariables()) {
            orders.add(Orders.asc(variable));
        }
        return newDiagram(records, orders);
    }

    /**
     * Creates and returns a diagram using the given records, and uses for each
     * variable the respective order provided.
     * <p>
     * Each Order object <b>must</b> correspond to the respective variable
     * of the Records object (i.e., <tt>orders</tt> and <tt>records.getVariables()</tt>
     * should be parallel lists, where each index would denote a variable and its desired order.
     * @param records the records that the diagram will contain
     * @param orders a list with one order per variable (of <tt>records.getVariables()</tt>)
     */
    public static DiagramImpl newDiagram(Records records, List<Order> orders) {
        Preconditions.checkNotNull(records, "records");
        Preconditions.checkNotNull(orders, "orders");
        Preconditions.checkArgument(records.getVariables().size() == orders.size(),
                "Exactly one comparator per variable must be given");
        return new DiagramImpl(records, orders);
    }

    /**
     * Creates and returns a diagram using the given records, and uses for each
     * variable the respective order provided.
     * <p>
     * Each Order object <b>must</b> correspond to the respective variable
     * of the Records object (i.e., <tt>orders</tt> and <tt>records.getVariables()</tt>
     * should be parallel lists, where each index would denote a variable and its desired order.
     * @param records the records that the diagram will contain
     * @param orders an array with one order per variable (of <tt>records.getVariables()</tt>)
     */
    public static DiagramImpl newDiagram(Records records, Order... orders) {
        return newDiagram(records, Arrays.asList(orders));
    }

    /**
     * A diagram implementation.
     */
    public static class DiagramImpl implements Diagram {
        private final Records records;
        private final List<Order> orders;
        private final List<String> variableNames;
        private final List<Variable> variables = new ArrayList<Variable>();
        private String title = "Title";
        private String rangeLabel = "Range";
        private final int[] domainSizes;
        private final MultiDimensionalArray<Record> values;

        private DiagramImpl(Records records, List<Order> orders) {
            this.records = records;
            this.orders = orders;
            this.variableNames = records.getVariables();
            this.domainSizes = new int[variableNames.size()];
            int pos = 0;
            for (String variable : variableNames) {
                Variable var = Variable.create(variable, records, orders.get(pos));
                variables.add(var);
                domainSizes[pos++] = var.domain.size();
            }
            values = createMatrix();
        }
        
        private MultiDimensionalArray<Record> createMatrix() {
            MultiDimensionalArray<Record> array =
                    new MultiDimensionalArray<Record>(domainSizes);
            Map<String, Integer> variableIndices = new HashMap<String, Integer>();
            int pos = 0;
            for (String variable : records.getVariables()) {
                variableIndices.put(variable, pos++);
            }
            
            Map<String, Map<Object, Integer>> domainIndicesPerVariable =
                    new HashMap<String, Map<Object, Integer>>();
            pos = 0;
            for (String variable : records.getVariables()) {
                Map<Object, Integer> domainIndices = new HashMap<Object, Integer>();
                int index = 0;
                for (Object value : variables.get(pos++).domain) {
                    domainIndices.put(value, index++);
                }
                domainIndicesPerVariable.put(variable, domainIndices);
            }
            
            final int vars = variables.size();
            Integer[] index = new Integer[vars];
            for (Record record : records) {
                for (String variable : records.getVariables()) {
                    Object variableValue = record.get(variable);
                    index[variableIndices.get(variable)] = domainIndicesPerVariable.get(variable).get(variableValue);
                }
                array.put(record, index);
            }
            return array;
        }

        /**
         * Sets the title of this diagram.
         * @param title the new title of this diagram
         * @return this
         */
        public DiagramImpl withTitle(String title) {
            this.title = Preconditions.checkNotNull(title);
            return this;
        }

        /**
         * Sets the new range label (label that describes the diagram's cell values) of
         * this diagram
         * @param rangeLabel the new range label of this diagram
         * @return this
         */
        public DiagramImpl withRangeLabel(String rangeLabel) {
            this.rangeLabel = Preconditions.checkNotNull(rangeLabel);
            return this;
        }

        /**
         * Sets the new label of a variable, denoted by index, of this diagram
         * @param variableIndex the index of the variable of which to set the label
         * @param label the label of the variable
         * @return this
         */
        public DiagramImpl withVariableLabel(int variableIndex, String label) {
            variables.get(variableIndex).label = Preconditions.checkNotNull(label);
            return this;
        }

        public String getRangeLabel() {
            return rangeLabel;
        }

        public String getTitle() {
            return title;
        }
        
        public List<String> getVariables() {
            return variableNames;
        }
        
        public List<Object> getDomain(int variableIndex) {
            return variables.get(variableIndex).domain;
        }

        public String getLabelOf(int variableIndex) {
            return variables.get(variableIndex).label;
        }
        
        public int getVariableCount() {
            return variables.size();
        }
        
        public int getDomainSize(int variableIndex) {
            return domainSizes[variableIndex];
        }
        
        public Record getRecordAt(int... variableIndices) {
            return values.get(variableIndices);
        }
        
        private static class Variable {
            final String name;
            String label;
            final List<Object> domain;
            final Comparator<Object> order;

            Variable(String variableName, Comparator<Object> order, List<Object> orderedDomain) {
                this.name = variableName;
                this.label = variableName;
                this.order = order;
                this.domain = Collections.unmodifiableList(orderedDomain);
            }

            public static Variable create(String variable, Records records, Order order) {
                List<Object> domain = new ArrayList<Object>(records.getDomainOf(variable));
                Collections.sort(domain, order);
                return new Variable(variable, order, domain);
            }
        }
    }
}
