package h03;

/**
 * A class that represents a function with a given enum as an alphabet.
 *
 * @param <T> The type of the given Enum.
 */
public class EnumIndex<T extends Enum<T>> implements FunctionToInt<T>{
    /**
     * The constants of the enum to be saved by this object.
     */
    private final T[] enumArray;

    /**
     * Constructs a new EnumIndex object with the constants of the given enum as an alphabet.
     *
     * @param enumClass The enum to be used.
     */
    public EnumIndex(Class<T> enumClass) {
        enumArray = enumClass.getEnumConstants();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int sizeOfAlphabet() {
        return enumArray.length;
    }

    /**
     * Returns the index of the given constant in the enum represented by the alphabet.
     *
     * @param t                             The given parameter to be used.
     * @return                              The index of the given parameter.
     * @throws IllegalArgumentException     Will never be thrown.
     */
    @Override
    public int apply(T t) throws IllegalArgumentException {
        return t.ordinal();
    }
}
