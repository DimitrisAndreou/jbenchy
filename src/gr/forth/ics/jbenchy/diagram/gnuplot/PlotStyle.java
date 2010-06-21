package gr.forth.ics.jbenchy.diagram.gnuplot;

import com.google.common.base.Preconditions;

/**
 * Defines a style for drawing datasets in gnuplot. Specifically, models the gnuplot language part
 * which supports phrases like <tt>"with points" </tt>, <tt>"with linespoints pointstyle 7"</tt>, etc.
 * Use the static factory methods to create predefines styles, or subclass to make a new style not
 * supported here.
 * @author andreou
 */
public class PlotStyle {
    private static final PlotStyle DEFAULT_LINES_POINTS = lines(Pattern.DEFAULT);
    private static final PlotStyle DEFAULT_POINTS = points(Pattern.DEFAULT);
    private static final PlotStyle DEFAULT_BOXES = boxes(0.7f);
    
    private final String plotStyleCommand;

    protected PlotStyle(String plotStyleCommand) {
        this.plotStyleCommand = Preconditions.checkNotNull(plotStyleCommand);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PlotStyle)) {
            return false;
        }
        PlotStyle other = (PlotStyle)obj;
        return plotStyleCommand.equals(other.plotStyleCommand);
    }

    @Override
    public int hashCode() {
        return plotStyleCommand.hashCode();
    }

    @Override
    public String toString() {
        return "PlotStyle[" + plotStyleCommand + "]";
    }
    
    /**
     * Returns the gnuplot specific command that describes this style, for example
     * <tt>"with points pointstyle 7"</tt>.
     */
    public String toGnuPlotCommand() {
        return plotStyleCommand;
    }
    
    /**
     * A style that draws individual points using a drawing pattern chosen by gnuplot.
     */
    public static PlotStyle points() {
        return DEFAULT_POINTS;
    }
    
    /**
     * A style that draws individual points using a specific drawing pattern.
     */
    public static PlotStyle points(Pattern pattern) {
        return new PlotStyle("with points" + pattern.toGnuPlotCommand());
    }
    
    /**
     * A style that connects subsequent points with lines using a drawing pattern chosen by gnuplot.
     */
    public static PlotStyle lines() {
        return DEFAULT_LINES_POINTS;
    }
    
    /**
     * A style that connects subsequent points with lines using a specific drawing pattern.
     */
    public static PlotStyle lines(Pattern pattern) {
        return new PlotStyle("with linespoints" + pattern.toGnuPlotCommand());
    }
    
    /**
     * A style that draws rectangles (like a barchart) with thickness <tt>0.7F</tt>.
     */
    public static PlotStyle boxes() {
        return DEFAULT_BOXES;
    }
    
    /**
     * A style that draws rectangles (like a barchart) with a specified thickness
     * (should be 0 <= thickness <= 1).
     */
    public static PlotStyle boxes(float thickness) {
        Preconditions.checkArgument(thickness >= 0F && thickness <= 1F, "Invalid thickness, not in [0, 1] range");
        return new PlotStyle("with boxes fs solid " + thickness);
    }
}
