package gr.forth.ics.jbenchy.diagram.jfreechart;

import com.google.common.base.Preconditions;
import gr.forth.ics.jbenchy.Record;
import gr.forth.ics.jbenchy.diagram.Diagram;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Utility class that adapts {@link Diagram diagrams} to JFreeChart datasets.
 * @author andreou
 */
public class Datasets {
    private Datasets() {
    }

    /**
     * Creates a JFreeChart's {@link CategoryDataset} from a {@link Diagram}.
     */
    public static CategoryDataset newCategoryDataset(Diagram diagram) {
        Preconditions.checkArgument(diagram.getVariableCount() == 1 ||
                diagram.getVariableCount() == 2,
                "Only 1D and 2D diagrams are supported");
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        final boolean is2D = diagram.getVariableCount() == 2;

        final int domainSizeOfSecondVariable = diagram.getVariableCount() == 2 ? diagram.getDomainSize(1) : 1;
        int[] index = new int[2];
        for (index[0] = 0; index[0] < diagram.getDomainSize(0); index[0]++) {
            for (index[1] = 0; index[1] < domainSizeOfSecondVariable; index[1]++) {
                Record cell = diagram.getRecordAt(index);
                if (cell == null) {
                    continue;
                }
                Number value = null;
                Comparable x = null, y = null;
                try {
                    value = (Number) cell.getValue();
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Value: '" + cell + "' at [row:" +
                            index[0] + ", column:" + index[1] + "] is not a number");
                }
                try {
                    x = (Comparable) diagram.getDomain(0).get(index[0]);
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Domain value: '" + cell + "' of [row:" +
                            index[0] + "] is not a number");
                }
                try {
                    y = is2D ? (Comparable) diagram.getDomain(1).get(index[1])
                            : diagram.getLabelOf(0);
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Domain value: '" + cell + "' of [column:" +
                            index[1] + "] is not a number");
                }

                dataset.addValue(value, y, x);
            }
        }
        return dataset;
    }
}
