package h03;

import java.util.List;

/**
 * A class that represents a function with a given alphabet.
 */
public class SelectionOfCharsIndex implements FunctionToInt<Character> {
    /**
     * The chars of the objects' alphabet.
     */
    private final char[] theChars;

    /**
     * Constructs a new SelectionOfCharsIndex object with the given alphabet scope.
     * The given alphabet must not be null, has to contain at least one element and each element has to be unique.
     *
     * @param theAlphabet The given alphabet.
     */
    public SelectionOfCharsIndex(List<Character> theAlphabet) {
        theChars = new char[theAlphabet.size()];
        int index = 0;
        for (Character c : theAlphabet) {
            theChars[index++] = c;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int sizeOfAlphabet() {
        return theChars.length;
    }

    /**
     * Returns the index at which the given parameter is contained in the alphabet.
     *
     * @param character The given parameter to be searched for.
     * @return The index of the given parameter.
     * @throws IllegalArgumentException Iff the given parameter is not contained in the alphabet.
     */
    @Override
    public int apply(Character character) throws IllegalArgumentException {
        for (int i = 0; i < theChars.length; i++) {
            if (theChars[i] == character) {
                return i;
            }
        }
        throw new IllegalArgumentException("Character could not be found in alphabet!");
    }
}

