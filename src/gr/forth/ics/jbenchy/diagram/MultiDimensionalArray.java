package gr.forth.ics.jbenchy.diagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author andreou
 */
class MultiDimensionalArray<T> {
    private final List<T> array;
    private final int[] dimensionSizes;
    
    public MultiDimensionalArray(int... dimensionSizes) {
        int[] copy = new int[dimensionSizes.length];
        System.arraycopy(dimensionSizes, 0, copy, 0, dimensionSizes.length);
        this.dimensionSizes = copy;
        int totalValues = 1;
        for (int dimensionSize : dimensionSizes) {
            totalValues *= dimensionSize;
        }
        array = new ArrayList<T>(totalValues);
        array.addAll(Collections.<T>nCopies(totalValues, null));
    }
    
    public void put(T value, Integer... index) {
        array.set(translate(index), value);
    }
    
    public T get(Integer... index) {
        return array.get(translate(index));
    }
    
    private int translate(Integer... index) {
        int position = index[dimensionSizes.length - 1];
        for (int i = dimensionSizes.length - 2; i >= 0; i--) {
            position *= dimensionSizes[i];
            position += index[i];
        }
        return position;
    }
    
    public T get(int... index) {
        return array.get(translate(index));
    }
    
    private int translate(int... index) {
        int position = index[dimensionSizes.length - 1];
        for (int i = dimensionSizes.length - 2; i >= 0; i--) {
            position *= dimensionSizes[i];
            position += index[i];
        }
        return position;
    }
}
