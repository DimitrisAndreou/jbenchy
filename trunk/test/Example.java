
import gr.forth.ics.jbenchy.Aggregate;
import gr.forth.ics.jbenchy.Aggregator;
import gr.forth.ics.jbenchy.DataTypes;
import gr.forth.ics.jbenchy.Database;
import gr.forth.ics.jbenchy.DbFactories;
import gr.forth.ics.jbenchy.Filters;
import gr.forth.ics.jbenchy.Orders;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Records;
import gr.forth.ics.jbenchy.Schema;
import gr.forth.ics.jbenchy.Timestamps;
import gr.forth.ics.jbenchy.diagram.Diagram;
import gr.forth.ics.jbenchy.diagram.DiagramFactory;
import gr.forth.ics.jbenchy.diagram.gnuplot.GnuPlotContext;
import gr.forth.ics.jbenchy.diagram.gnuplot.GnuPlotWriter;
import gr.forth.ics.jbenchy.diagram.gnuplot.PlotStyle;
import gr.forth.ics.jbenchy.diagram.jfreechart.Chart;
import gr.forth.ics.jbenchy.diagram.jfreechart.ChartFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.jfree.chart.ChartPanel;

public class Example extends TestCase {
    private enum Variables {
        Phase, Height, Time, Value
    }

    public void test() throws Exception {
        Database db = DbFactories.localDerby().getOrCreate("mydb");
        Schema schema = new Schema().add(Variables.Value, DataTypes.DOUBLE).
                add(Variables.Phase, DataTypes.decimal(6, 2)).
                add(Variables.Height, DataTypes.DOUBLE).
                add(Variables.Time, DataTypes.TIMESTAMP);
        Aggregator aggregator = db.forceCreate(schema, "aggregator");
        for (double x = 0.0; x < 10.0; x += 0.25) {
            for (double h = 1.0; h < 5.0; h += 1.0) {
                aggregator.record(
                        new Record().add(Variables.Value, Math.sin(x) * h).add(Variables.Height,
                        h).add(Variables.Phase, x).add(Variables.Time,
                        Timestamps.now()));
            }
        }

        Records records = aggregator.filtered(Filters.ge(Variables.Phase, 1.0)).
                ordered(Orders.desc(Variables.Phase)).
                ordered(Orders.asc(Variables.Height)).
                report(Aggregate.average(Variables.Value),
                Variables.Phase, Variables.Height);

        System.out.println(records);
        Diagram diagram = DiagramFactory.newDiagram(records).
                withTitle("My Title").
                withRangeLabel("My Range").
                withVariableLabel(0, "Phase").
                withVariableLabel(1, "Height");

        Records records2 = aggregator.filtered(Filters.ge(Variables.Phase, 1.0)).
                ordered(Orders.desc(Variables.Phase)).
                report(Aggregate.average(Variables.Value),
                Variables.Phase);

        System.out.println(records2);
        Diagram diagram2 = DiagramFactory.newDiagram(records2).
                withTitle("My Title").
                withRangeLabel("My Range").
                withVariableLabel(0, "Phase");

        db.shutDown();

        File folder = new File("build/diagramsFolder");
        GnuPlotWriter gnuPlot = new GnuPlotWriter(folder);
        gnuPlot.writeDiagram(diagram, "diagram-2D", new GnuPlotContext().setDefaultStyle(
                PlotStyle.points()));
        gnuPlot.writeDiagram(diagram2, "diagram-1D");
        gnuPlot.createMasterPlotFile("master.plt");
    }

    private static void print(Records results) {
        for (Record v : results) {
            System.out.println(v);
        }
    }

    public void testJFreeChart() {
        Records records = getReport(7, 2);
        Diagram diagram = DiagramFactory.newDiagram(records).withVariableLabel(0,
                "XXX").withVariableLabel(1, "YYY");
        final Chart chart = new ChartFactory(diagram).newLineChart();
        final ChartPanel chartPanel = chart.newPanel();

        try {
            chart.write(800, 600, "jpeg", new File("build/myDiagram.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        chartPanel.setSize(new Dimension(800, 600));
//        chartPanel.setMaximumDrawWidth(800);
//        chartPanel.setMaximumDrawHeight(600);
//
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                JFrame frame = new JFrame();
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.add(chartPanel);
//                frame.pack();
//                frame.setLocationRelativeTo(null);
//                frame.setVisible(true);
//            }
//        });
    }

    private static Records getReport(double maxX, double maxY) {
        List<Record> records = new ArrayList<Record>();
        for (double i = 0; i < maxX; i += 0.1) {
            for (double j = 1; j <= maxY; j++) {
                Record record = new Record(Math.sin(i) * j).add("X", i).
                        add("Y", j);
                records.add(record);
            }
        }
        return new Records(records, Arrays.asList("X", "Y"));
    }
}
