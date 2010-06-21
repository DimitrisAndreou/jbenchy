package gr.forth.ics.jbenchy.diagram.gnuplot;

/**
 * An enumeration of patterns for drawing individual points in gnuplot.
 * @author andreou
 */
public enum Pattern {
    /**
     * Default; let GnuPlot pick a pattern.
     */
    DEFAULT(-1),
    
    /**
     * A cross.
     */
    CROSS(1),
    
    /**
     * An 'X'.
     */
    X(2),
    
    /**
     * A star.
     */
    STAR(3),
    
    /**
     * An empty square.
     */
    BOX_EMPTY(4),
    
    /**
     * A filled square.
     */
    BOX_FILLED(5),
    
    /**
     * An empty circle.
     */
    CIRCLE_EMPTY(6),
    
    /**
     * A filled circle.
     */
    CIRCLE_FILLED(7),
    
    /**
     * An empty triangle.
     */
    TRIANGLE_EMPTY(8),
    
    /**
     * A filled triangle.
     */
    TRIANGLE_FILLED(9);
    private final int id;

    Pattern(int id) {
        this.id = id;
    }

    /**
     * Returns the gnuplot specific command corresponding to this pattern, for example
     * <tt>" pointstyle 7"</tt> (which actually defines the filled circle pattern).
     */
    public String toGnuPlotCommand() {
        if (this == DEFAULT) {
            return "";
        }
        return " pointstyle " + id;
    }
}
