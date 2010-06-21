package gr.forth.ics.jbenchy.diagram.jfreechart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * A chart based on JFreeChart.
 * @author andreou
 */
public class Chart {
    private final JFreeChart chart;
        
    protected Chart(JFreeChart chart) {
        this.chart = chart;
    }
        
    /**
     * Returns the {@link JFreeChart} that supports this chart.
     */
    public JFreeChart getJFreeChart() {
        return chart;
    }

    /**
     * Creates a panel with this chart embedded, which can be used in a Swing application.
     */
    public ChartPanel newPanel() {
        return new ChartPanel(getJFreeChart());
    }
    
    /**
     * Draws this chart on a {@link Graphics2D} object. The chart will scale itself
     * to fit in the region <tt>(0, 0), (width, height)</tt>.
     * @param g the graphics object to draw with
     * @param width the width of the drawn chart
     * @param height the height of the drawn chart
     */
    public void paint(Graphics2D g, double width, double height) {
        getJFreeChart().draw(g, new Rectangle2D.Double(0, 0, width, height));
    }

    /**
     * Creates an {@link BufferedImage image} containing this chart.
     * @param width the width of the image and the chart in it
     * @param height the height of the image and the chart in it
     */
    public BufferedImage newImage(int width, int height) {
        return getJFreeChart().createBufferedImage(width, height);
    }

    /**
     * Writes the chart as an image to a file. The <tt>format</tt> of the image (e.g., "jpeg")
     * is specified as in {@link ImageIO#write(java.awt.image.RenderedImage, String, OutputStream)}.
     * @param width the width of the image written to file
     * @param height the height of the image written to file
     * @param format the format with which to encode the image (e.g., "jpeg")
     * @param file the file on which to write the image 
     */
    public void write(int width, int height, String format, File file) throws IOException {
        ImageIO.write(newImage(width, height), format, new BufferedOutputStream(
                new FileOutputStream(file)));
    }

    /**
     * Writes the chart as an image to an output stream. The <tt>format</tt> of the image (e.g., "jpeg")
     * is specified as in {@link ImageIO#write(java.awt.image.RenderedImage, String, OutputStream)}.
     * @param width the width of the image written to file
     * @param height the height of the image written to file
     * @param format the format with which to encode the image (e.g., "jpeg")
     * @param out the output stream to which to write the image
     */
    public void write(int width, int height, String format, OutputStream out) throws IOException {
        ImageIO.write(newImage(width, height), format, out);
    }
}
