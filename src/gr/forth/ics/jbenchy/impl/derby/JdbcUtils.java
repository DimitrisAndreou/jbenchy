package gr.forth.ics.jbenchy.impl.derby;

import gr.forth.ics.jbenchy.SQLRuntimeException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

class JdbcUtils {
    private JdbcUtils() { }

    static <T> T executeInConnection(DataSource dataSource, SQLAction<? super Connection, T> action) {
        Connection c = null;
        try {
            c = dataSource.getConnection();
            return action.execute(c);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    throw new SQLRuntimeException(e);
                }
            }
        }
    }

    static <T> T executeInStatement(DataSource dataSource, SQLAction<? super Statement, T> action) {
        return executeInConnection(dataSource, new StatementSQLAction<T>(action));
    }

    private static class StatementSQLAction<T> implements SQLAction<Connection, T> {
        private final SQLAction<? super Statement, T> delegate;

        StatementSQLAction(SQLAction<? super Statement, T> delegate) {
            this.delegate = delegate;
        }

        public T execute(Connection connection) throws SQLException {
            Statement st = null;
            try {
                st = connection.createStatement();
                return delegate.execute(st);
            } finally {
                if (st != null) {
                    st.close();
                }
            }

        }
    }
    
    static <T> T executeInResultSet(DataSource dataSource, String query, SQLAction<? super ResultSet, T> action) {
        return executeInStatement(dataSource, new ResultSetSQLAction<T>(query, action));
    }

    private static class ResultSetSQLAction<T> implements SQLAction<Statement, T> {
        private final String query;
        private final SQLAction<? super ResultSet, T> delegate;

        ResultSetSQLAction(String query, SQLAction<? super ResultSet, T> delegate) {
            this.query = query;
            this.delegate = delegate;
        }

        public T execute(Statement st) throws SQLException {
            ResultSet rs = st.executeQuery(query);
            try {
                return delegate.execute(rs);
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
    }
}
