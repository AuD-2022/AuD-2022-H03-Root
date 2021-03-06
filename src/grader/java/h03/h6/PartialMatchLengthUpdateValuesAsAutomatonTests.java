package h03.h6;

import h03.*;
import h03.provider.SimpleSearchStringProvider;
import h03.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.opentest4j.AssertionFailedError;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

@TestForSubmission("h03")
public class PartialMatchLengthUpdateValuesAsAutomatonTests {

    private static final BiFunction<String, Object, String> EXCEPTION_MESSAGE = (s, needle) -> """
        %s
        Alphabet: %s
        Search string: %s""".formatted(s, Alphabet.SHORT_DESCRIPTION, needle);
    private static final Field THE_STATES;

    static {
        try {
            THE_STATES = PartialMatchLengthUpdateValuesAsAutomaton.class.getDeclaredField("theStates");
            THE_STATES.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void resetInvocations() {
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods(
            "^java/util/\\w*List add\\(Ljava/lang/Object;\\)Z$",
            "^java/util/\\w*List add\\(ILjava/lang/Object;\\)V$",
            "^java/util/\\w*List remove\\(I\\)Ljava/lang/Object;$",
            "^java/util/\\w*List remove\\(Ljava/lang/Object;\\)Z$",
            "^java/util/List of\\(.*\\)Ljava/util/List;$",
            "^java/util/Arrays asList\\(\\[Ljava/lang/Object;\\)Ljava/util/List;$"
        );
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    @SuppressWarnings("unchecked")
    public void testTheStatesLength(List<Character> needle) throws IllegalAccessException {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        List<Transition<Character>>[] theStates = (List<Transition<Character>>[]) THE_STATES
            .get(new PartialMatchLengthUpdateValuesAsAutomaton<>(function, needle.toArray(Character[]::new)));

        assertEquals(needle.size() + 1, theStates.length, EXCEPTION_MESSAGE.apply(
            "Length of array in field 'theStates' differs from expected value (search string size + 1)", needle));
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    @SuppressWarnings("unchecked")
    public void testTheStatesListSize(List<Character> needle) throws IllegalAccessException {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        List<Transition<Character>>[] theStates = (List<Transition<Character>>[]) THE_STATES
            .get(new PartialMatchLengthUpdateValuesAsAutomaton<>(function, needle.toArray(Character[]::new)));

        assertEquals(1, theStates[0].size(), EXCEPTION_MESSAGE.apply(
            "List of transitions of state 0 has more than 1 element", needle));
        for (int i = 1; i < needle.size(); i++) {
            assertEquals(2, theStates[i].size(), EXCEPTION_MESSAGE.apply(
                "List of transitions of state %d has more than 2 elements".formatted(i), needle));
        }
        assertEquals(1, theStates[needle.size()].size(), EXCEPTION_MESSAGE.apply(
            "List of transitions of state %d has more than 1 element".formatted(needle.size()), needle));
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    @SuppressWarnings("unchecked")
    public void testStatesWhenMatch(List<Character> needle) throws IllegalAccessException {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        List<Transition<Character>>[] theStates = (List<Transition<Character>>[]) THE_STATES
            .get(new PartialMatchLengthUpdateValuesAsAutomaton<>(function, needle.toArray(Character[]::new)));

        for (int i = 0; i < needle.size(); i++) {
            final int finalI = i;
            boolean hasCorrectFollowUpState = theStates[i].stream()
                .filter(transition -> transition.J == finalI + 1)
                .map(transition -> transition.LETTERS)
                .findFirst()
                .orElse(Collections.emptyList())
                .contains(needle.get(i));

            assertTrue(hasCorrectFollowUpState, EXCEPTION_MESSAGE.apply(
                "No transition in state %d leads to state %d for letter '%c'".formatted(i, i + 1, needle.get(i)), needle));
        }
        boolean hasCorrectTransitionInLastState = theStates[needle.size()].stream()
            .filter(transition -> transition.J == 1)
            .map(transition -> transition.LETTERS)
            .findFirst()
            .orElse(Collections.emptyList())
            .contains(needle.get(0));
        assertTrue(hasCorrectTransitionInLastState, EXCEPTION_MESSAGE.apply(
            "No transition in state %d leads to state 1 for letter '%c'".formatted(needle.size(), needle.get(0)), needle));
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    @SuppressWarnings("unchecked")
    public void testDefaultStates(List<Character> needle) throws IllegalAccessException {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        List<Transition<Character>>[] theStates = (List<Transition<Character>>[]) THE_STATES
            .get(new PartialMatchLengthUpdateValuesAsAutomaton<>(function, needle.toArray(Character[]::new)));
        List<Character> leftOverCharacters = new ArrayList<>(Alphabet.getAlphabet());
        leftOverCharacters.removeAll(needle);

        for (int i = 0; i < needle.size() + 1; i++) {
            Character illegalTransition = theStates[i].stream()
                .flatMap(transition -> transition.LETTERS.stream())
                .filter(leftOverCharacters::contains)
                .findAny()
                .orElse(null);

            assertNull(illegalTransition, EXCEPTION_MESSAGE.apply(
                "Letter '%c' has an explicit transition in state %d".formatted(illegalTransition, i), needle));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testComplex() throws IllegalAccessException {
        FunctionToInt<Character> function = new ComplexFunctionToInt();
        List<Transition<Character>>[] expectedStates = new List[] {
            List.of(new Transition<>(1, List.of('g'))),
            List.of(new Transition<>(2, List.of('a')), new Transition<>(1, List.of('g'))),
            List.of(new Transition<>(3, List.of('g'))),
            List.of(new Transition<>(2, List.of('a')), new Transition<>(1, List.of('g')))
        };
        List<Character> needle = List.of('g', 'a', 'g');
        List<Transition<Character>>[] actualStates =
            (List<Transition<Character>>[]) THE_STATES
                .get(new PartialMatchLengthUpdateValuesAsAutomaton<>(function, needle.toArray(Character[]::new)));

        assertEquals(expectedStates.length, actualStates.length, EXCEPTION_MESSAGE.apply(
            "Number of states differs from the expected value", needle));
        for (int i = 0; i < expectedStates.length; i++) {
            final int finalI = i;
            assertEquals(expectedStates[i].size(), actualStates[i].size(), EXCEPTION_MESSAGE.apply(
                "Number of transitions for state %d differs from expected value".formatted(i), needle));
            for (Transition<Character> transition : expectedStates[i]) {
                Transition<Character> transitionMatch = actualStates[i].stream()
                    .filter(t -> t.J == transition.J)
                    .findAny()
                    .orElseThrow(() -> new AssertionFailedError(EXCEPTION_MESSAGE.apply(
                        "State %d does not have a transition for follow-up state %d".formatted(finalI, transition.J), needle)));
                for (Character character : transition.LETTERS) {
                    assertTrue(transitionMatch.LETTERS.contains(character), EXCEPTION_MESSAGE.apply(
                        "Letter '%c' does not have a transition in state %d to follow-up state %d"
                            .formatted(character, i, transition.J), needle));
                }
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    @SuppressWarnings("unchecked")
    public void testGetPartialMatchLengthUpdate(List<Character> needle) throws IllegalAccessException {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        PartialMatchLengthUpdateValues<Character> instance = new PartialMatchLengthUpdateValuesAsAutomaton<>(function,
            needle.toArray(Character[]::new));
        List<Transition<Character>>[] theStates = (List<Transition<Character>>[]) THE_STATES.get(instance);

        for (int i = 0; i < needle.size() + 1; i++) {
            for (Character character : Alphabet.getAlphabet()) {
                int followUpState = theStates[i].stream()
                    .flatMap(transition -> transition.LETTERS.stream().map(c -> c.equals(character) ? transition.J : null))
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(0);

                assertEquals(followUpState, instance.getPartialMatchLengthUpdate(i, character), EXCEPTION_MESSAGE.apply(
                    "Method returned wrong follow-up state for current state %d and letter '%c'".formatted(i, character), needle));
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    public void testGetSearchStringLength(List<Character> needle) {
        PartialMatchLengthUpdateValues<Character> instance = new PartialMatchLengthUpdateValuesAsAutomaton<>(
            new FunctionToIntImpl(),
            needle.toArray(Character[]::new)
        );

        assertEquals(needle.size(), instance.getSearchStringLength(), EXCEPTION_MESSAGE.apply(
            "Value returned by method getSearchStringLength() differs from expected value", needle));
    }
}
