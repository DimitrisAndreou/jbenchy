package gr.forth.ics.jbenchy;

import java.sql.SQLException;

/**
 * An unchecked exception that wraps an {@linkplain SQLException}
 * 
 * @author andreou
 */
public class SQLRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -2889552715008477541L;
    
    public SQLRuntimeException(SQLException cause) {
        super(cause);
    }

    @Override public SQLException getCause() {
        return (SQLException)super.getCause();
    }
}
