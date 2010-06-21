package gr.forth.ics.jbenchy.impl.derby;

import com.google.common.base.Preconditions;
import java.sql.SQLException;

/**
 *
 * @author andreou
 */
public abstract class ConnectionString {
    private static final String CREATE_PART = ";create=true";
    private static final String SHUTDOWN_PART = ";shutdown=true";

    public ConnectionString() {
    }

    protected abstract String dbPart(String dbName);

    protected abstract String credentialsPart();

    public abstract boolean isEmbedded();

    public String connect(String dbName) {
        return "jdbc:derby:" + dbPart(dbName) + credentialsPart();
    }

    public String createAndConnect(String dbName) {
        return "jdbc:derby:" + dbPart(dbName) + credentialsPart() + CREATE_PART;
    }

    public void shutdown() {
        shutdown("");
    }

    public void shutdown(String dbName) {
        try {
            DerbyUtils.openAndClose("jdbc:derby:" + dbPart(dbName) +
                    credentialsPart() + SHUTDOWN_PART);
        } catch (SQLException ignore) {
            //exception is thrown as part of the shutdown process
        }
    }

    public static class EmbeddedConnectionString extends ConnectionString {
        public EmbeddedConnectionString() {
        }

        @Override
        protected String credentialsPart() {
            return "";
        }

        @Override
        protected String dbPart(String dbName) {
            return dbName;
        }

        @Override
        public boolean isEmbedded() {
            return true;
        }
    }

    public static class RemoteConnectionString extends ConnectionString {
        private final String host;
        private final int port;
        private final String user;
        private final String password;

        public RemoteConnectionString(String host, int port,
                String user, String password) {
            this.host = Preconditions.checkNotNull(host, "host");
            this.port = Preconditions.checkNotNull(port, "port");
            this.user = Preconditions.checkNotNull(user, "user");
            this.password = password;
        }

        @Override
        protected String credentialsPart() {
            return ";user=" + user + ";password=" + password;
        }

        @Override
        protected String dbPart(String dbName) {
            return "//" + host + ":" + port + "/" + dbName;
        }

        @Override
        public boolean isEmbedded() {
            return false;
        }
    }
}
