package h03;

import java.util.LinkedList;
import java.util.List;

/**
 * This class realizes the algorithm of string matching BOFA using a pre-processed PartialMatchLengthUpdateValues object to search through a given source string.
 *
 * @param <T> The type of the letters, etc.
 */
public class StringMatcher<T> {
    /**
     * The update values to be used in the algorithm.
     */
    private final PartialMatchLengthUpdateValues<T> VALUES;

    /**
     * Constructs a new StringMatcher object with the given PartialMatchLengthUpdateValues object.
     *
     * @param values The update values for this object.
     */
    public StringMatcher(PartialMatchLengthUpdateValues<T> values) {
        VALUES = values;
    }

    /**
     * Finds and returns all indices at which an occurrence of the search string (pre-processed with the update values object) starts in the given source.
     *
     * @param source    The source string to search through.
     * @return          The list of calculated indices.
     */
    public List<Integer> findAllMatches(T[] source) {
        //Current state
        int state = 0;
        //Length of the searchString
        final int SEARCH_STRING_LENGTH = VALUES.getSearchStringLength();
        List<Integer> result = new LinkedList<>();
        //Iterate over all elements in source
        for(int i = 0; i < source.length; i ++) {
            //Update to next state (depends on current state and current element in source)
            state = VALUES.getPartialMatchLengthUpdate(state, source[i]);
            //Has a match been found?
            if(state == SEARCH_STRING_LENGTH)
                //If so - add the index of the beginning of found match to result
                if(SEARCH_STRING_LENGTH != 0)
                    result.add(i + 1 - SEARCH_STRING_LENGTH + 1);
        }
        return result;
    }
}

