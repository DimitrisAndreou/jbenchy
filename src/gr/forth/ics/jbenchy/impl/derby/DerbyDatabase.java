package gr.forth.ics.jbenchy.impl.derby;

import com.google.common.base.Preconditions;
import gr.forth.ics.jbenchy.Aggregator;
import gr.forth.ics.jbenchy.DataType;
import gr.forth.ics.jbenchy.DataTypes;
import gr.forth.ics.jbenchy.Database;
import gr.forth.ics.jbenchy.SQLRuntimeException;
import gr.forth.ics.jbenchy.Schema;
import gr.forth.ics.jbenchy.StringUtils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 *
 * @author andreou
 */
class DerbyDatabase extends Database {
    private final String dbName;
    private final DataSource dataSource;
    private final ConnectionString connectionString;
    
    DerbyDatabase(String dbName, DataSource dataSource, ConnectionString connectionString) {
        this.dbName = dbName;
        this.dataSource = dataSource;
        this.connectionString = connectionString;
    }
    
    public Aggregator get(final String name) {
        StringUtils.checkHasText(name);
        return JdbcUtils.executeInConnection(dataSource, new SQLAction<Connection, Aggregator>() {
            public Aggregator execute(Connection con) throws SQLException {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet table = metaData.getTables(
                        null, null, name.toUpperCase(), new String[] { "TABLE" });
                if (!table.next()) {
                    throw new SQLException("Corresponding table not found (expected '"
                            + name + "')");
                }
                Schema schema = new Schema();
                ResultSet columns = metaData.getColumns(null, null, table.getString("TABLE_NAME"), null);
                while (columns.next()) {
                    String variableName = columns.getString("COLUMN_NAME");
                    if (variableName.equalsIgnoreCase(DerbyUtils.getIdColumnName())) {
                        continue;
                    }
                    DataType dataType = DataTypes.fromSql(columns.getString("TYPE_NAME"));
                    schema.add(variableName, dataType);
                }
                return new AggregatorImpl(dataSource, schema, name);
            }
        });
    }
    
    public void shutDown() {
        connectionString.shutdown(dbName);
    }
    
    public Aggregator create(Schema schema, String name) throws SQLRuntimeException {
        Preconditions.checkNotNull(schema, "schema");
        StringUtils.checkHasText(name, "name");
        name = name.toUpperCase();
        final String createTableSql = createTableSql(schema, name);
        JdbcUtils.executeInStatement(dataSource, new SQLAction<Statement, Void>() {
            public Void execute(Statement st) throws SQLException {
                st.execute(createTableSql);
                return null;
            }
        });
        return new AggregatorImpl(dataSource, schema, name);
    }
    
    private String createTableSql(Schema schema, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE APP.")
        .append(name)
        .append("(ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, "
                + "INCREMENT BY 1)");
        for (String dimensionName : schema.getVariables()) {
            sb.append(", ")
            .append(dimensionName)
            .append(" ")
            .append(schema.getTypeOf(dimensionName).getSqlDefinition())
            .append(" NOT NULL");
        }
        sb.append(")");
        return sb.toString();
    }
    
    public boolean delete(final String name) {
        StringUtils.checkHasText(name);
        SQLAction<Statement, Boolean> dropTable = new SQLAction<Statement, Boolean>() {
            public Boolean execute(Statement st) {
                try {
                    st.execute("DROP TABLE APP." + name);
                    return true;
                } catch (SQLException ignore) {
                    //thrown when table does not exist
                    return false;
                }
            }
        };
        return JdbcUtils.executeInStatement(dataSource, dropTable);
    }
}
