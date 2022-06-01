package h03;

/**
 * An interface representing a function of type T that returns an int.
 *
 * @param <T> The generic type of the function.
 */
public interface FunctionToInt<T> {

    /**
     * Returns the size of this functions' alphabet.
     *
     * @return The size of the alphabet.
     */
    int sizeOfAlphabet();

    /**
     * Applies the given function to the given parameter t. This is deterministic.
     *
     * @param t The given parameter to be used.
     * @return The result of the function applied on the given parameter t.
     * The result is always >= 0 and < sizeOfAlphabet or the Exception.
     * @throws IllegalArgumentException If the parameter given for t is invalid for the used function.
     */
    int apply(T t) throws IllegalArgumentException;
}
