package gr.forth.ics.jbenchy.impl.derby;

import java.sql.SQLException;

interface SQLAction<F, T> {
    T execute(F from) throws SQLException;
}
