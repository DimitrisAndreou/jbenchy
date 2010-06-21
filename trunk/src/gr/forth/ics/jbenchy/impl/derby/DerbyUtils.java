package gr.forth.ics.jbenchy.impl.derby;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author andreou
 */
class DerbyUtils {
    static final String EMBEDDED_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    static final String CLIENT_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    private static final String ID_COLUMN = "ID";

    static String getIdColumnName() {
        return ID_COLUMN;
    }

    public static int getExitCode() {
        return 50000;
    }

    private DerbyUtils() {
    }

    static void loadEmbeddedDriver() {
        try {
            Class.forName(DerbyUtils.EMBEDDED_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static void loadClientDriver() {
        try {
            Class.forName(DerbyUtils.CLIENT_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static void openAndClose(String url) throws SQLException {
        DriverManager.getConnection(url).close();
    }
}
