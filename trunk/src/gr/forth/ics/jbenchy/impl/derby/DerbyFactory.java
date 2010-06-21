package gr.forth.ics.jbenchy.impl.derby;

import com.google.common.base.Preconditions;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import gr.forth.ics.jbenchy.Database;
import gr.forth.ics.jbenchy.DbFactory;
import gr.forth.ics.jbenchy.SQLRuntimeException;
import gr.forth.ics.jbenchy.StringUtils;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author andreou
 */
public class DerbyFactory implements DbFactory {
    static {
        DerbyUtils.loadEmbeddedDriver();
        DerbyUtils.loadClientDriver();

        Logger.getLogger("com.mchange.v2").setLevel(Level.OFF);
    }
    
    private final ConnectionString connectionString;

    public DerbyFactory(ConnectionString connectionString) {
        this.connectionString = Preconditions.checkNotNull(connectionString);
    }

    public Database get(String dbName) {
        StringUtils.checkHasText(dbName, "Empty db name");
        if (!exists(dbName)) {
            throw new RuntimeException("Database: '" + dbName +
                    "' does not exist");
        }
        return new DerbyDatabase(dbName, createDataSource(dbName), connectionString);
    }

    public boolean exists(String dbName) {
        StringUtils.checkHasText(dbName, "Empty db name");
        try {
            DerbyUtils.openAndClose(connectionString.connect(dbName));
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Database getOrCreate(String dbName) {
        StringUtils.checkHasText(dbName, "Empty db name");
        try {
            DerbyUtils.openAndClose(connectionString.createAndConnect(dbName));
            return new DerbyDatabase(dbName, createDataSource(dbName), connectionString);
        } catch (SQLException e) { throw new SQLRuntimeException(e); }
    }

    private DataSource createDataSource(String dbName) {
        try {
            final ComboPooledDataSource cpds = new ComboPooledDataSource();
            String driver = connectionString.isEmbedded() ? 
                DerbyUtils.EMBEDDED_DRIVER :
                DerbyUtils.CLIENT_DRIVER;
            cpds.setDriverClass(driver);
            cpds.setJdbcUrl(connectionString.connect(dbName));
            cpds.setMinPoolSize(1);
            cpds.setMaxPoolSize(1);
            return cpds;
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }
}
