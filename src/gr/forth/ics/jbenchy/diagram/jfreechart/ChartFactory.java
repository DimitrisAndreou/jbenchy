package gr.forth.ics.jbenchy.diagram.jfreechart;

import gr.forth.ics.jbenchy.diagram.Diagram;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;

/**
 * A factory that can create various types of JFreeChart charts.
 * @author andreou
 */
public class ChartFactory {
    private final Diagram diagram;

    /**
     * Creates a default ChartFactory from a {@link Diagram}. The returned factory
     * can be used to create various types of JFreeChart charts.
     * @param diagram the diagram which will provide the chart's depicted data
     */
    public ChartFactory(Diagram diagram) {
        this.diagram = diagram;
    }

    /**
     * Creates a chart with bars.
     */
    public Chart newBarChart() {
        return new Chart(
                org.jfree.chart.ChartFactory.createBarChart(
                diagram.getTitle(), getLabelOfX(), getLabelOfY(),
                Datasets.newCategoryDataset(diagram), PlotOrientation.VERTICAL, true, false, true));
    }

    /**
     * Creates a chart with lines.
     */
    public Chart newLineChart() {
        return new Chart(
                org.jfree.chart.ChartFactory.createLineChart(
                diagram.getTitle(), getLabelOfX(), getLabelOfY(),
                Datasets.newCategoryDataset(diagram), PlotOrientation.VERTICAL, true, false, true));
    }

    /**
     * Creates a chart with points (scatter plot).
     */
//    public Chart newXYChart() {
//        return new Chart(
//                org.jfree.chart.ChartFactory.createXYStepChart(
//                diagram.getTitle(), getLabelOfX(), getLabelOfY(),
//                Datasets.newCategoryDataset(diagram), PlotOrientation.VERTICAL, true, false, true));
//    }
    
    private String getLabelOfX() {
        return diagram.getLabelOf(0);
    }

    private String getLabelOfY() {
        return (diagram.getVariableCount() == 2) ? diagram.getLabelOf(1) : diagram.getRangeLabel();
    }
}
