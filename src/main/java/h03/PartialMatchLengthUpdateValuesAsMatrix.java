package h03;

/**
 * A class that represents string matching BOFA using an intern matrix.
 *
 * @param <T> The type of the function/letters of the used alphabet.
 */
public class PartialMatchLengthUpdateValuesAsMatrix<T> extends PartialMatchLengthUpdateValues<T> {
    //length of substring + 1 (number of states) x size of alphabet (possible next letter)
    /**
     * The matrix of this object in which the lookup-table is implemented.
     */
    private int[][] matrix;
    private final int SEARCH_STRING_LENGTH;

    /**
     * Constructs a PartialMatchLengthUpdateValuesAsMatrix object with the given function and search string.
     * This is done by creating the private matrix of this object so that it may be used to look up next possible states.
     *
     * @param fct           The function to be used.
     * @param searchString  The search string to be used.
     */
    public PartialMatchLengthUpdateValuesAsMatrix(FunctionToInt<T> fct, T[] searchString) {
        super(fct);
        SEARCH_STRING_LENGTH = searchString.length;
        //Create table data fields
        matrix = new int[SEARCH_STRING_LENGTH + 1][];
        for(int i = 0; i < matrix.length; i++)
            matrix[i] = new int[fct.sizeOfAlphabet()];
        //Fill table data fields for each state
        for(int state = 0; state < matrix.length; state++) {
            //Letters in searchString that are not the perfect letter can lead to other states than 0
            for(T letter: searchString) {
                //Currently reached substring of searchString (based on state)
                T[] subString = (T[]) new Object[state + 1];
                //Fill this substring accordingly and add possible next letter
                for(int i = 0; i < subString.length - 1; i ++) {
                    subString[i] = searchString[i];
                }
                subString[subString.length - 1] = letter;
                //Request next state resulting from adding this letter and add resulting state in table (under added letter)
                matrix[state][fct.apply(letter)] = computePartialMatchLengthUpdateValues(subString);
            }
            //Perfect letter only if current state is not end-state
            if(state != SEARCH_STRING_LENGTH)
                matrix[state][fct.apply(searchString[state])] = state + 1;
            //All letters not even represented in searchString will remain as 0 (int is initialized as 0)
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPartialMatchLengthUpdate(int state, T letter) {
        return matrix[state][fct.apply(letter)];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSearchStringLength() {
        return SEARCH_STRING_LENGTH;
    }
}

