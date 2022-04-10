package h03;

/**
 * An abstract class that contains a function (FunctionToInt) and can compute the length of partial string matches of a given string of type T.
 *
 * @param <T> The type of objects to be searched through by an object of this class.
 */
public abstract class PartialMatchLengthUpdateValues<T> {
    /**
     * The function to be used in this object.
     */
    protected FunctionToInt<T> fct;

    /**
     * Constructs a PartialMatchLengthUpdateValues object with the given function.
     *
     * @param fct The function to be used by this object.
     */
    public PartialMatchLengthUpdateValues(FunctionToInt<T> fct) {
        this.fct = fct;
    }

    /**
     * Returns the next state that will be entered when using the given letter from the given state.
     *
     * @param state     The current state.
     * @param letter    The letter to be added.
     * @return          The next state.
     */
    public abstract int getPartialMatchLengthUpdate(int state, T letter);

    /**
     * Returns the length of the search string used in this object.
     *
     * @return The length of the search string.
     */
    public abstract int getSearchStringLength();

    /**
     * Returns the amount of elements k in searchString so that the first k elements of searchString match the last k elements of searchString.
     *
     * @param searchString  The searchString to search through.
     * @return              The amount k.
     */
    protected int computePartialMatchLengthUpdateValues(T[] searchString) {
        int length = searchString.length;
        //Go through all possible k-values (0 to length of searchString -1 because full length of searchString would always be true)
        //and check, whether the given k is a valid return (start value is 0)
        int result = 0;
        for(int k = 1; k < length; k++) {
            //Check, whether last k elements match first k elements
            boolean update = true;
            for(int i = 0; i < k; i++)
                //Check for equality per given function
                if(fct.apply(searchString[i]) != fct.apply(searchString[length - k + i])) {
                    update = false;
                    break;
                }
            //If all elements matched: Update to new k (can only be higher than previous k)
            if(update)
                result = k;
        }
        return result;
    }
}
