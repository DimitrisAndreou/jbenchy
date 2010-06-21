package gr.forth.ics.jbenchy.diagram.gnuplot;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A context for gnuplot, i.e. a set of variables and respective values that can be
 * set and unset in a gnuplot command file.
 * @author andreou
 */
public class GnuPlotContext {
    private final List<Entry<String, String>> vars = new ArrayList<Entry<String, String>>();
    
    private final Map<Integer, PlotStyle> styles = new HashMap<Integer, PlotStyle>();
    private PlotStyle defaultStyle = PlotStyle.lines();
    
    /**
     * Creates an empty GnuPlotContext.
     */
    public GnuPlotContext() { }
    
    /**
     * Creates a GnuPlotContext with the specified entries.
     */
    public GnuPlotContext(List<Entry<String, String>> context) {
        vars.addAll(context);
    }
    
    /**
     * Creates a copy of the specified context.
     */
    public GnuPlotContext(GnuPlotContext context) {
        this(context.getContext());
    }
    
    /**
     * Sets the default {@link PlotStyle style} for plotting datasets. This will be used if there is
     * no explicitly specified style (through {@link #setStyle(int, PlotStyle)}) that is
     * non-null and non-empty.
     * 
     * @param defaultStyle the style to be used for datasets for which there is
     * no explicitly specified style
     * @return this
     * @see #getStyle(int)
     */
    public GnuPlotContext setDefaultStyle(PlotStyle defaultStyle) {
        if (defaultStyle == null) {
            defaultStyle = PlotStyle.lines();
        }
        this.defaultStyle = defaultStyle;
        return this;
    }
    
    /**
     * Sets the {@link PlotStyle style} to be used for a plotted column data, by its index (starting from 1).
     * If a null or empty style is provided, the style is effectively removed, and henceforth the
     * default style will be used for the column.
     * 
     * @param columnIndex the index of the plotted data column for which a style is specified
     * @param style the gnuplot style to use
     * @return this
     * @see #getStyle(int)
     * @see #setDefaultStyle(PlotStyle)
     */
    public GnuPlotContext setStyle(int columnIndex, PlotStyle style) {
        if (style == null) {
            styles.remove(columnIndex);
        } else {
            styles.put(columnIndex, style);
        }
        return this;
    }
    
    /**
     * Returns a {@link PlotStyle style} that should be used in the gnuplot "plot" command, after the description of a
     * columnIndex of data. Default value is {@link PlotStyle#lines()}. 
     * @param columnIndex the index of the plotted data column that the returned description will be used to plot
     * The first columnIndex of data has value 1, the second 2, etc.
     * @return a string that describes the style to be used for this data columnIndex
     * @see #setStyle(int, PlotStyle)
     * @see #setDefaultStyle(PlotStyle)
     */
    public PlotStyle getStyle(int columnIndex) {
        PlotStyle style = styles.get(columnIndex);
        if (style == null) {
            return defaultStyle;
        }
        return style;
    }
    
    /**
     * Removes all styles. 
     * @return this
     * @see #setStyle(int, PlotStyle)
     * @see #getStyle(int)
     */
    public GnuPlotContext clearStyles() {
        styles.clear();
        return this;
    }
    
    /**
     * Returns all entries of this context.
     */
    public List<Entry<String, String>> getContext() {
        return Collections.unmodifiableList(vars);
    }
    
    /**
     * Adds an entry with the given variable (with no argument).
     * @return this
     */
    public GnuPlotContext set(String variable) {
        return set(variable, "");
    }
    
    /**
     * Adds an entry of the given variable to a value.
     * @return this
     */
    public GnuPlotContext set(String variable, String value) {
        Preconditions.checkNotNull(variable, "variable");
        if (value == null) {
            value = "";
        }
        vars.add(new SimpleImmutableEntry<String, String>(variable, value));
        return this;
    }
    
    /**
     * Adds an entry of "logscale" with value "x".
     * @return this
     */
    public GnuPlotContext setLogscaleX() {
        return set("logscale", "x");
    }
    
    /**
     * Adds an entry of "logscale" with value "y".
     * @return this
     */
    public GnuPlotContext setLogscaleY() {
        return set("logscale", "y");
    }
    
    /**
     * Adds an entry of "xrange" with value "[<tt>from</tt> : <tt>to</tt>]".
     * @return this
     */
    public GnuPlotContext setXRange(double from, double to) {
        return set("xrange", range(from, to));
    }
    
    /**
     * Adds an entry of "yrange" with value "[<tt>from</tt> : <tt>to</tt>]".
     * @return this
     */
    public GnuPlotContext setYRange(double from, double to) {
        return set("yrange", range(from, to));
    }
    
    private String range(double from, double to) {
        return "[" + from + ":" + to + "]";
    }
    
    /**
     * Adds an entry of "mytics" with the specified value.
     * @return this
     */
    public GnuPlotContext setNumberOfIntervalTics(int minorTicsBetweenMajorTics) {
        return set("mytics", Integer.toString(minorTicsBetweenMajorTics));
    }
    
    /**
     * Adds an entry of "noxtics".
     * @return this
     */
    public GnuPlotContext setNoXTicks() {
        return set("noxticks");
    }
    
    /**
     * Adds an entry of "noytics".
     * @return this
     */
    public GnuPlotContext setNoYTicks() {
        return set("noyticks");
    }
    
    /**
     * Adds an entry of "ticscale" with value "<tt>major, minor</tt>".
     * Defaults are major=1 and minor=0.5 respectively. Negative numbers
     * put tics outside the diagram.
     * @return this
     */
    public GnuPlotContext setTickScale(double major, double minor) {
        return set("ticscale", major + ", " + minor);
    }
    
    /**
     * Adds an entry of "xtics" with value "<tt>from, step, to</tt>".
     * @return this
     */
    public GnuPlotContext setXTicsRange(double from, double step, double to) {
        return set("xtics", from + ", " + step + ", " + to);
    }
    
    /**
     * Adds an entry of "ytics" with value "<tt>from, step, to</tt>".
     * @return this
     */
    public GnuPlotContext setYTicsRange(double from, double step, double to) {
        return set("ytics", from + ", " + step + ", " + to);
    }
    
    /**
     * Adds an entry of "format" with value 'x "format"'.
     * @return this
     */
    public GnuPlotContext setXFormat(String format) {
        return set("format", "x \"" + format + "\"");
    }
    
    /**
     * Adds an entry of "format" with value 'y "format"'.
     * @return this
     */
    public GnuPlotContext setYFormat(String format) {
        return set("format", "y \"" + format + "\"");
    }
    
    /**
     * Adds an entry of "xlabel" with value '"label"'.
     * @return this
     */
    public GnuPlotContext setXLabel(String label) {
        return set("xlabel", "\"" + label + "\"");
    }
    
    /**
     * Adds an entry of "ylabel" with value '"label"'.
     * @return this
     */
    public GnuPlotContext setYLabel(String label) {
        return set("ylabel", "\"" + label + "\"");
    }
    
    /**
     * Adds an entry of "title" with value '"title"'.
     * @return this
     */
    public GnuPlotContext setTitle(String title) {
        return set("title", "\"" + title + "\"");
    }
    
    /**
     * Sets the width of boxes, in case such a style is being used. Use together with the {@link PlotStyle#boxes()} style. 
     * @return this
     * @see PlotStyle#boxes()
     */
    public GnuPlotContext setBoxWidth(double width) {
        return set("boxwidth", Double.toString(width));
    }
    
    /**
     * Adds an entry of "terminal" with the specified value.
     * @return this
     */
    public GnuPlotContext setTerminal(String terminal) {
        return set("terminal", terminal);
    }
    
    /**
     * Prints to the given <tt>Appendable</tt> the commands that set the contained entries.
     * @param out
     */
    public void doCreateContext(Appendable out) {
        dumpContext(out, "set", true);
    }
    
    /**
     * Prints to the given <tt>Appendable</tt> the commands that unset the contained entries.
     * @param out
     */
    public void doDestroyContext(Appendable out) {
        dumpContext(out, "unset", false);
    }
    
    private void dumpContext(Appendable out, String command, boolean includeValue) {
        try {
            for (Entry<String, String> entry : vars) {
                out.append(command).append(" ").
                        append(entry.getKey());
                if (includeValue) {
                    out.append(" ").append(entry.getValue());
                }
                out.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class SimpleImmutableEntry<K, V> implements Entry<K, V> {
        private final K key;
        private final V value;
        
        SimpleImmutableEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }            
}
