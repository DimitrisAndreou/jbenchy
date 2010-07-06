package gr.forth.ics.jbenchy.impl;

import com.google.common.base.Preconditions;
import gr.forth.ics.jbenchy.Aggregate;
import gr.forth.ics.jbenchy.fluent.AggregateBuilder;
import gr.forth.ics.jbenchy.fluent.AggregateBuilder.PerClause;
import gr.forth.ics.jbenchy.Aggregator;
import gr.forth.ics.jbenchy.DataType;
import gr.forth.ics.jbenchy.Filter;
import gr.forth.ics.jbenchy.Filters;
import gr.forth.ics.jbenchy.Order;
import gr.forth.ics.jbenchy.Orders;
import gr.forth.ics.jbenchy.fluent.ReportBuilder;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Records;
import gr.forth.ics.jbenchy.Schema;
import gr.forth.ics.jbenchy.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Abstract implementation of {@link Aggregator}.
 * 
 * @author andreou
 */
public abstract class AbstractAggregator implements Aggregator {
    private final String name;
    private final AggregateBuilderImpl aggregateBuilderImpl = new AggregateBuilderImpl(this);
    
    protected AbstractAggregator(String name) {
        StringUtils.checkHasText(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public Records report(Aggregate aggr, Object... variables) {
        Preconditions.checkNotNull(aggr, "aggregation");
        Preconditions.checkNotNull(variables, "variables");
        return report(aggr, Filters.NULL_FILTER, Collections.<Order>emptyList(), variables);
    }
    
    protected abstract Records report(Aggregate aggr,
            Filter filter, List<Order> orders, Object... variables);

    public PerClause averageOf(Object variable) {
        return aggregateBuilderImpl.averageOf(variable);
    }

    public PerClause count() {
        return aggregateBuilderImpl.count();
    }

    public PerClause maxOf(Object variable) {
        return aggregateBuilderImpl.maxOf(variable);
    }

    public PerClause minOf(Object variable) {
        return aggregateBuilderImpl.minOf(variable);
    }

    public PerClause sumOf(Object variable) {
        return aggregateBuilderImpl.sumOf(variable);
    }
    
    protected abstract <T> List<T> domainOfVariable(Filter filter, List<Order> orders, Object variable, Class<T> expectedType);
    
    protected abstract void deleteRecords(Filter filter);
    
    public Aggregator with(Object variable, Object value) {
        return new BoundAggregator(this, variable.toString(), value);
    }

    public ReportBuilder ordered(Order... orders) {
        return new SelectorImpl().ordered(orders);
    }
        
    public ReportBuilder ordered(List<Order> orders) {
        return new SelectorImpl().ordered(orders);
    }

    public ReportBuilder filtered(Filter filter) {
        return new SelectorImpl().filtered(filter);
    }

    public <T> List<T> domainOf(Object variable, Class<T> expectedType) {
        Preconditions.checkNotNull(variable, "Null variable");
        return domainOfVariable(Filters.NULL_FILTER, Arrays.asList(Orders.asc(variable)), variable, expectedType);
    }
    
    public void deleteRecords() {
        deleteRecords(Filters.NULL_FILTER);
    }
        
    private class SelectorImpl implements ReportBuilder {
        private final List<Order> orders = new ArrayList<Order>();
        private final List<Filter> filterList = new ArrayList<Filter>();
        
        public PerClause averageOf(final Object variable) {
            return new PerClauseImpl(Aggregate.average(variable));
        }

        public PerClause sumOf(final Object variable) {
            return new PerClauseImpl(Aggregate.sum(variable));
        }

        public PerClause minOf(final Object variable) {
            return new PerClauseImpl(Aggregate.min(variable));
        }

        public PerClause maxOf(final Object variable) {
            return new PerClauseImpl(Aggregate.max(variable));
        }

        public PerClause count() {
            return new PerClauseImpl(Aggregate.count());
        }
        
        private class PerClauseImpl implements PerClause {
            Aggregate aggregate;
            
            PerClauseImpl(Aggregate aggregate) {
                this.aggregate = aggregate;
            }
            
            public Records per(Object... variables) {
                return report(aggregate, variables);
            }

            public Records perAll() {
                return report(aggregate);
            }
        }
    
        public ReportBuilder filtered(Filter filter) {
            Preconditions.checkNotNull(filter);
            filterList.add(filter);
            return this;
        }

        public ReportBuilder ordered(Order... orders) {
            return ordered(Arrays.asList(orders));
        }
        
        public ReportBuilder ordered(List<Order> orders) {
            Preconditions.checkNotNull(orders, "Null orders");
            this.orders.addAll(orders);
            return this;
        }

        public Records report(Aggregate aggr, Object... variables) {
            return AbstractAggregator.this.report(aggr,
                    Filters.and(filterList), orders, variables);
        }
        
        public <T> List<T> domainOf(Object variable, Class<T> expectedType) {
            Preconditions.checkNotNull(variable, "Null variable");
            return AbstractAggregator.this.domainOfVariable(Filters.and(filterList), orders, variable, expectedType);
        }

        public void deleteRecords() {
            AbstractAggregator.this.deleteRecords(Filters.and(filterList));
        }
    }
    
    private static class BoundAggregator extends AbstractAggregator {
        final AbstractAggregator parent;
        final String boundVariable;
        final Object boundValue;

        BoundAggregator(AbstractAggregator parent, String boundDimension, Object boundValue) {
            super(parent.getName());
            Preconditions.checkNotNull(boundDimension);
            this.parent = parent;
            this.boundVariable = boundDimension;
            this.boundValue = boundValue;
        }

        public Schema getSchema() {
            return new FilteredSchema(parent.getSchema(), boundVariable);
        }

        public void record(Record measurement) {
            if (measurement.containsKey(boundVariable)) {
                throw new IllegalArgumentException("Measurement: " + measurement + " " +
                        "already contains a value for the variable: " + boundVariable + ", which" +
                        "was implicitly bound to this value: " + boundValue);
            }
            parent.record(measurement.copy().add(boundVariable, boundValue));
        }

        protected Filter boundVariableFilter() {
            return Filters.eq(boundVariable, boundValue);
        }
        
        @Override
        public Records report(Aggregate aggr, Object... variables) {
            return report(aggr, Filters.NULL_FILTER, Collections.<Order>emptyList(), variables);
        }

        @Override
        protected Records report(Aggregate aggr, Filter filter,
                List<Order> orders, Object... variables) {
            variables = addBoundedVariableIfAbsent(variables);
            return parent.filtered(Filters.and(boundVariableFilter(), filter)).
                    ordered(orders).report(aggr, variables);
        }
        
        @Override
        protected <T> List<T> domainOfVariable(Filter filter, List<Order> orders, Object variable, Class<T> expectedType) {
            return parent.domainOfVariable(filter, orders, variable, expectedType);
        }
        
        protected void deleteRecords(Filter filter) {
            parent.deleteRecords(filter);
        }

        private Object[] addBoundedVariableIfAbsent(Object[] variables) {
            boolean found = false;
            for (Object var : variables) {
                Preconditions.checkNotNull(var, "Null variable");
                if (boundVariable.equalsIgnoreCase(var.toString())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                return variables;
            }
            Object[] newVariables = new Object[variables.length + 1];
            System.arraycopy(variables, 0, newVariables, 0, variables.length);
            variables = newVariables;
            variables[variables.length - 1] = boundVariable;
            return variables;
        }
    }
    
    private static class FilteredSchema extends Schema {
        private final Schema delegate;
        private final String variableToHide;
        
        FilteredSchema(Schema delegate, Object variableToHide) {
            this.delegate = delegate;
            this.variableToHide = variableToHide.toString().toUpperCase();
        }

        @Override
        public DataType getTypeOf(Object variable) {
            if (variable == null) { return null; }
            String var = variable.toString().toUpperCase();
            if (var.equals(variableToHide)) {
                return null;
            }
            return delegate.getTypeOf(variable);
        }

        @Override
        public Collection<String> getVariables() {
            Collection<String> variables = new LinkedHashSet<String>(delegate.getVariables());
            variables.remove(variableToHide);
            return Collections.unmodifiableCollection(variables);
        }
    }
    
    private static class AggregateBuilderImpl implements AggregateBuilder {
        private final Aggregator aggregator;
        
        AggregateBuilderImpl(Aggregator aggregator) {
            this.aggregator = aggregator;
        }
        
        public PerClause averageOf(final Object variable) {
            return new PerClauseImpl(Aggregate.average(variable));
        }

        public PerClause sumOf(final Object variable) {
            return new PerClauseImpl(Aggregate.sum(variable));
        }

        public PerClause minOf(final Object variable) {
            return new PerClauseImpl(Aggregate.min(variable));
        }

        public PerClause maxOf(final Object variable) {
            return new PerClauseImpl(Aggregate.max(variable));
        }

        public PerClause count() {
            return new PerClauseImpl(Aggregate.count());
        }
        
        private class PerClauseImpl implements PerClause {
            private final Aggregate aggregate;
            
            PerClauseImpl(Aggregate aggregate) {
                this.aggregate = aggregate;
            }
            
            public Records per(Object... vars) {
                return aggregator.report(aggregate, vars);
            }
                
            public Records perAll() {
                return aggregator.report(aggregate);
            }
        }
    }
}
