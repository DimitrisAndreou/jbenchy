package gr.forth.ics.jbenchy;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Offers an easy way to create a {@link Timestamp} representing the current time instant.
 *
 * @author andreou
 */
public class Timestamps {
    private Timestamps() {
    }

    /**
     * Returns the current time instance as a {@link Timestamp}. Useful to record when an experiment
     * took place.
     * 
     * @return the current time instance as a {@code Timestamp}
     */
    public static Timestamp now() {
        return new Timestamp(new Date().getTime());
    }
}
