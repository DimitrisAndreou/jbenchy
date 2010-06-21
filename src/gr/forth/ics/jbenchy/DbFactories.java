package gr.forth.ics.jbenchy;

import gr.forth.ics.jbenchy.impl.derby.ConnectionString;
import gr.forth.ics.jbenchy.impl.derby.DerbyFactory;

/**
 * A provider of database factories.
 * @author andreou
 */
public class DbFactories {
    private DbFactories() { }
    
    /**
     * Returns a factory that can create embedded (in-process) Derby database instances.
     */
    public static DbFactory localDerby() {
        return new DerbyFactory(new ConnectionString.EmbeddedConnectionString());
    }
    
    /**
     * Returns a factory that can connect to remote Derby databases. This
     * factory will use the port 1527 for remote connections.
     * @param host the host that the remote Derby database resides
     * @param user the user under which to connect to the database
     * @param password the password of the user
     */
    public static DbFactory remoteDerby(String host, String user, String password) {
        return remoteDerby(host, 1527, user, password);
    }
    
    /**
     * 
     * Returns a factory that can connect to remote Derby databases.
     * @param host the host that the remote Derby database resides
     * @param port the port that the remote Derby database listens to
     * @param user the user under which to connect to the database
     * @param password the password of the user
     */
    public static DbFactory remoteDerby(String host, int port, String user, String password) {
        return new DerbyFactory(
                new ConnectionString.RemoteConnectionString(host, port, user, password));
    }
}
