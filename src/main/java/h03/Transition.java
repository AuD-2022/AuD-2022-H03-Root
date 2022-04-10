package h03;

import java.util.List;

/**
 * Represents a possible transition to a state (with label j) using the letters in its list.
 *
 * @param <T> The type of the letters
 */
public class Transition<T> {
    /**
     * Where this transition leads to.
     */
    public final int J;

    /**
     * The letters that trigger this transition.
     */
    public final List<T> LETTERS;

    /**
     * Constructs a new Transition object with the given label j and given letters.
     *
     * @param j       The label of the state this transition leads to.
     * @param letters The letters that will make this transition lead to the next state (j).
     */
    public Transition(int j, List<T> letters) {
        this.J = j;
        this.LETTERS = letters;
    }
}
