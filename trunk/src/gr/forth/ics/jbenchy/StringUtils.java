package gr.forth.ics.jbenchy;

import com.google.common.base.Preconditions;

/**
 * Few string utilities.
 *
 * @author andreou
 */
public class StringUtils {
    private StringUtils() { }

    /**
     * Invokes {@code toString()} on a variable, then {@code trim()} and {@code toUppercase()} and
     * returns the result, while ensuring that the result is not empty.
     *
     * @param variable an object representing a variable
     * @return the normalized string of the variable, as described above
     * @throws NullPointerException if variable is null
     * @throws IllegalArgumentException if the resulting string is empty
     */
    public static String normalizeVariable(Object variable) {
        return normalizeVariable(variable, "Empty variable name");
    }

    /**
     * Invokes {@code toString()} on a variable, then {@code trim()} and {@code toUppercase()} and
     * returns the result, while ensuring that the result is not empty.
     *
     * @param variable an object representing a variable
     * @param errorMessage the error message to use
     * @return the normalized string of the variable, as described above
     * @throws NullPointerException if variable is null
     * @throws IllegalArgumentException if the resulting string is empty
     */
    public static String normalizeVariable(Object variable, String errorMessage) {
        Preconditions.checkNotNull(variable, errorMessage);
        String var = variable.toString().trim();
        Preconditions.checkArgument(var.length() > 0, errorMessage);
        return var.toUpperCase();
    }

    /**
     * Checks that the argument is not null and it is not empty when trimmed.
     *
     * @param text the argument to check
     * @throws NullPointerException if the argument is null
     * @throws IllegalArgumentExcepiton if the argument is empty, when trimmed
     */
    public static void checkHasText(String text) {
        checkHasText(text, "empty text");
    }

    /**
     * Checks that the argument is not null and it is not empty when trimmed, and throws an
     * exception with the given error message otherwise.
     *
     * @param text the argument to check
     * @param errorMessage the erorr message to use upon failure
     * @throws NullPointerException if the argument is null
     * @throws IllegalArgumentExcepiton if the argument is empty, when trimmed
     */
    public static void checkHasText(String text, String errorMessage) {
        Preconditions.checkNotNull(text, errorMessage);
        Preconditions.checkArgument(text.trim().length() > 0, errorMessage);
    }
}
