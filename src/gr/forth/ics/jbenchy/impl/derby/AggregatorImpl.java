package gr.forth.ics.jbenchy.impl.derby;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import gr.forth.ics.jbenchy.Aggregate;
import gr.forth.ics.jbenchy.Filter;
import gr.forth.ics.jbenchy.Order;
import gr.forth.ics.jbenchy.impl.AbstractAggregator;
import gr.forth.ics.jbenchy.Orders;
import gr.forth.ics.jbenchy.Schema;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Records;
import gr.forth.ics.jbenchy.StringUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;

/**
 *
 * @author andreou
 */
class AggregatorImpl extends AbstractAggregator {
    private final Schema schema;
    private final DataSource dataSource;
    private final String tableName;
    
    private final String insertSql;
    
    private static final Joiner commaJoiner = Joiner.on(",");
    
    public AggregatorImpl(
            DataSource dataSource,
            Schema schema,
            String name) {
        super(name);
        ;
        StringUtils.checkHasText(name, "Aggregator name");
        this.schema = Preconditions.checkNotNull(schema, "schema");
        this.dataSource = Preconditions.checkNotNull(dataSource, "dataSource");
        this.tableName = name.toUpperCase();
        this.insertSql = createInsertSql(schema);
    }
    
    public Schema getSchema() {
        return schema;
    }
    
    private String createInsertSql(Schema schema) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO APP.").append(tableName).append("(")
        .append(commaJoiner.join(schema.getVariables()))
        .append(") VALUES (");
        
        sb.append(commaJoiner.join(Collections.nCopies(schema.getVariables().size(), "?")));
        sb.append(")");
        return sb.toString();
    }
    
    public void record(final Record record) {
        checkRecord(record);
        JdbcUtils.executeInConnection(dataSource, new SQLAction<Connection, Void>() {
            public Void execute(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(insertSql);
                int pos = 1;
                for (String variable : schema.getVariables()) {
                    Object value = record.get(variable);
                    if (value == null) {
                        throw new IllegalArgumentException("Record had no value for variable: '" + variable + "'. " +
                                "Record was: " + record + ", Schema was: " + schema);
                    }
                    ps.setObject(pos++, String.valueOf(value));
                }
                ps.execute();
                return null;
            }
        });
    }
    
    private void checkRecord(Record record) {
        Set<String> variables = new HashSet<String>(schema.getVariables());
        for (String variable : record.keySet()) {
            Preconditions.checkNotNull(variable, "Record with at least one null variable name detected");
            variable = StringUtils.normalizeVariable(variable, "Record with at least one empty variable name detected");
            if (!variables.contains(variable)) {
                throw new IllegalArgumentException("Unknown variable: '" + variable + "', available " +
                        "variables: " + schema.getVariables());
            }
            variables.remove(variable);
        }
        if (!variables.isEmpty()) {
            throw new IllegalArgumentException("Record has missing variable(s): " + variables + "," +
                    "it only defines these variables: " + record.keySet());
        }
    }
    
    @Override
    @SuppressWarnings({"unchecked"})
    protected Records report(final Aggregate aggr, final Filter filter,
            final List<Order> orders, final Object... variables) {
        Preconditions.checkNotNull(aggr, "aggregator");
        List<Record> records = JdbcUtils.executeInResultSet(dataSource, groupBySql(aggr, filter, orders, variables), 
                new SQLAction<ResultSet, List<Record>>() {
            final String aggregatedColumnAlias = aggr.getVariableName();
            public List<Record> execute(ResultSet rs) throws SQLException {
                List<Record> records = Lists.newArrayListWithCapacity(32);
                while (rs.next()) {
                    Object measurement = aggr.getResultType(schema).parse(
                            rs.getString(aggregatedColumnAlias));
                    Record record = new Record();
                    for (Object variable : variables) {
                        record.add(variable,
                                schema.getTypeOf(variable).parse(
                                    rs.getString(variable.toString())));
                    }
                    record.putValue(measurement);
                    records.add(record);
                }
                return records;
            }
        });
        return new Records(records, Arrays.asList(variables));
    }
    
    private String groupBySql(Aggregate aggr, Filter filter, List<Order> orders, Object... variables) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        for (Object variable : variables) {
            StringUtils.checkHasText(variable.toString(), "Empty variable name detected");
        }
        String commaDelimitedDimensions = commaJoiner.join(variables);
        if (variables.length > 0) {
            sb.append(commaDelimitedDimensions);
            sb.append(",");
        }
        sb.append(aggr.toSql())
        .append(" AS ").append(aggr.getVariableName())
        .append(" FROM APP.").append(tableName)
        .append(" WHERE ")
        .append(filter.toSql(schema));
        if (variables.length > 0) {
            sb.append(" GROUP BY ").append(commaDelimitedDimensions);
        }
        sb.append(" ").append(Orders.toSqlOrderByClause(orders));
        return sb.toString();
    }
    
    @Override
    @SuppressWarnings({"unchecked"})
    protected <T> List<T> domainOfVariable(Filter filter, List<Order> orders, final Object variable, final Class<T> expectedType) {
        return JdbcUtils.executeInResultSet(dataSource, domainSql(filter, orders, variable),
                new SQLAction<ResultSet, List<T>>() {
            public List<T> execute(ResultSet rs) throws SQLException {
                List<T> objects = Lists.newArrayListWithCapacity(64);
                while (rs.next()) {
                    objects.add(expectedType.cast(
                            schema.getTypeOf(variable).parse(
                                    rs.getString(1))));
                }
                return objects;
            }
        });
    }

    private String domainSql(Filter filter, List<Order> orders, Object variable) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT ").append(variable)
        .append(" FROM APP.").append(tableName)
        .append(" WHERE ").append(filter.toSql(schema)).append(" ")
        .append(Orders.toSqlOrderByClause(orders));
        return sb.toString();
    }

    @Override
    protected void deleteRecords(final Filter filter) {
        JdbcUtils.executeInStatement(dataSource, new SQLAction<Statement, Void>() {
            public Void execute(Statement st) throws SQLException {
                st.execute(deleteSql(filter));
                return null;
            }
        });
    }
    
    private String deleteSql(Filter filter) {
        return "DELETE FROM APP." + tableName + " WHERE " +
                filter.toSql(schema);
    }
}
