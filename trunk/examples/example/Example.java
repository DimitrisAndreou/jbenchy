package example;

import gr.forth.ics.jbenchy.Aggregator;
import gr.forth.ics.jbenchy.DataTypes;
import gr.forth.ics.jbenchy.Database;
import gr.forth.ics.jbenchy.DbFactories;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.Records;
import gr.forth.ics.jbenchy.Schema;
import gr.forth.ics.jbenchy.diagram.Diagram;
import gr.forth.ics.jbenchy.diagram.DiagramFactory;
import gr.forth.ics.jbenchy.diagram.jfreechart.ChartFactory;
import java.io.File;
import java.io.IOException;

public class Example {
    public static void main(String[] args) {
        Database db = DbFactories.localDerby().getOrCreate("exampleDb");

        Schema schema = new Schema()
                .add("input1", DataTypes.DOUBLE)
                .add("input2", DataTypes.DOUBLE)
                .add("measurement", DataTypes.DOUBLE);

        Aggregator aggregator = db.forceCreate(schema, "data");

        for (double input1 = 0.0; input1 < 10.0; input1 += 0.5) {
            for (double input2 = 0.0; input2 < 10.0; input2 += 0.5) {
                //execute experiment for these parameters
                //repeat each experiment ten times
                for (int repeats = 0; repeats < 10; repeats++) {
                    double measurement = experiment(input1, input2);
                    //create a record representing the specific experiment and store it
                    aggregator.record(new Record()
                            .add("input1", input1)
                            .add("input2", input2)
                            .add("measurement", measurement));
                }
            }
        }

        //experiment has run. Now lets create some reports

        //first, lets see the average measurement per different values of input1
        Records records1 = aggregator.averageOf("measurement").per("input1");
        System.out.println(records1);

        //Prints the following (null is the key for the average we requested):
        //{INPUT1=0.0, null=2.4134248936280946}
        //{INPUT1=0.5, null=2.446740183102116}
        //{INPUT1=1.0, null=2.4209812849423153}
        //...
        //{INPUT1=8.5, null=2.451182446002516}
        //{INPUT1=9.0, null=2.4580809911471158}
        //{INPUT1=9.5, null=2.4079731939133824}

        //then, proceed with the average measurement per input2
        Records records2 = aggregator.averageOf("measurement").per("input2");
        System.out.println(records2);

        //Prints:
        //{INPUT2=0.0, null=0.49248966971474956}
        //{INPUT2=0.5, null=5.2688706685206865}
        //{INPUT2=1.0, null=8.900585034163608}
        //...
        //{INPUT2=8.5, null=8.489498306888802}
        //{INPUT2=9.0, null=4.617760556069815}
        //{INPUT2=9.5, null=-0.2626161345687626}

        Diagram diagram = DiagramFactory.newDiagram(records2);
        try {
            new ChartFactory(diagram).newLineChart().write(400, 300, "jpeg", new File("image.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //lets pretend that this function represents a real experimental setting,
    //and we need to discover how the measurement depends on the inputs.
    //Notice that the return value depends only on the second input.
    private static double experiment(double input1, double input2) {
        return Math.sin(input2) * 10.0 + Math.random();
    }
}
