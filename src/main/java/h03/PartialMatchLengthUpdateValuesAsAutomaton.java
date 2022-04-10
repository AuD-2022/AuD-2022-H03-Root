package h03;

import java.util.LinkedList;
import java.util.List;

/**
 * A class that represents string matching BOFA using an intern array of lists of transitions -
 * basically an array containing the states and their possible transitions to other states.
 *
 * @param <T> The type of the function/letters of the used alphabet.
 */
public class PartialMatchLengthUpdateValuesAsAutomaton<T> extends PartialMatchLengthUpdateValues<T> {
    /**
     * The states of the automaton as a list of its transitions.
     */
    private List<Transition<T>>[] theStates;
    private final int SEARCH_STRING_LENGTH;

    /**
     * Constructs a PartialMatchLengthUpdateValuesAsAutomaton object with the given function and search string.
     * This is done by creating the private array of this object by creating the various lists and their possible transitions to other states.
     *
     * @param fct          The function to be used.
     * @param searchString The search string to be used.
     */
    public PartialMatchLengthUpdateValuesAsAutomaton(FunctionToInt<T> fct, T[] searchString) {
        super(fct);
        SEARCH_STRING_LENGTH = searchString.length;
        //Create states (Lists of Transitions)
        theStates = (LinkedList<Transition<T>>[]) new LinkedList[SEARCH_STRING_LENGTH + 1];
        for (int i = 0; i < theStates.length; i++) {
            theStates[i] = new LinkedList<>();
        }
        //Fill table data fields for each state
        for (int state = 0; state < theStates.length; state++) {
            //Letters in searchString that are not the perfect letter can lead to other states than 0
            for (T letter : searchString) {
                //Currently reached substring of searchString (based on state)
                T[] subString = (T[]) new Object[state + 1];
                //Fill this substring accordingly and add possible next letter
                for (int i = 0; i < subString.length - 1; i++) {
                    subString[i] = searchString[i];
                }
                subString[subString.length - 1] = letter;
                //Request next state resulting from this letter and if needed add either a new Transition-Object to the state and add the letter to its letters-List or
                //add the letter to an existing Transition-Object
                int nextState = computePartialMatchLengthUpdateValues(subString);
                //Skip the following if the next state would be 0 (due to "implicit if Transition is not included")
                if (nextState == 0) {
                    continue;
                }
                //Does a transition to this state already exist? If so - add this letter (if it is not already included in letters-List)
                boolean exists = false;
                for (Transition<T> transition : theStates[state]) {
                    if (transition.J == nextState) {
                        exists = true;
                        boolean letterIncluded = false;
                        for (T let : transition.LETTERS) {
                            if (fct.apply(letter) == fct.apply(let)) {
                                letterIncluded = true;
                                break;
                            }
                        }
                        if (!letterIncluded) {
                            transition.LETTERS.add(letter);
                        }
                        break;
                    }
                }
                //If a transition does not already exist add it and the found letter to the letters-List of the new Transition-Object
                if (!exists) {
                    List<T> list = new LinkedList<>();
                    list.add(letter);
                    Transition<T> toAdd = new Transition<>(nextState, list);
                    theStates[state].add(toAdd);
                }
            }
            //perfect letter only if current state is not end-state
            if (state != SEARCH_STRING_LENGTH) {
                boolean exists = false;
                //Delete transitions of perfect letter that could lead to a former state (e.g. "gag" in state 2 and letter is 'g' -> state 1 would be recognized first, then state 3)
                for (Transition<T> transition : theStates[state]) {
                    if (transition.J != state + 1) {
                        for (int i = 0; i < transition.LETTERS.size(); i++) {
                            if (fct.apply(transition.LETTERS.get(i)) == fct.apply(searchString[state])) {
                                transition.LETTERS.remove(i);
                                break;
                            }
                        }
                    }
                }
                //Add transition for perfect letter
                for (Transition<T> transition : theStates[state])
                    //Does the transition exist?
                {
                    if (transition.J == state + 1) {
                        exists = true;
                        boolean letterIncluded = false;
                        //Does the letter in the transition exist?
                        for (T let : transition.LETTERS) {
                            if (fct.apply(let) == fct.apply(searchString[state])) {
                                letterIncluded = true;
                                break;
                            }
                        }
                        if (!letterIncluded) {
                            transition.LETTERS.add(searchString[state]);
                        }
                        break;
                    }
                }
                if (!exists) {
                    List<T> list = new LinkedList<>();
                    list.add(searchString[state]);
                    Transition<T> perfect = new Transition<>(state + 1, list);
                    theStates[state].add(perfect);
                }
            }

            //Cleanup necessary due to an error in the algorithm above which I can not find/do not want to invest any more time into.
            //Sometimes there were Transition-objects with empty letter-attribute which are obviously not intended.
            for (int i = 0; i < theStates[state].size(); i++) {
                if (theStates[state].get(i).LETTERS != null && theStates[state].get(i).LETTERS.size() == 0) {
                    theStates[state].remove(i);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPartialMatchLengthUpdate(int state, T letter) {
        //Search through transitions of the given state
        for (Transition<T> transition : theStates[state])
            //Search through letters of the current transition
        {
            for (T let : transition.LETTERS)
                //If letter is the given letter -> return the state this transition leads to
            {
                if (fct.apply(let) == fct.apply(letter)) {
                    return transition.J;
                }
            }
        }
        //No transition with given letter found -> implicit transition to 0
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSearchStringLength() {
        return SEARCH_STRING_LENGTH;
    }
}
