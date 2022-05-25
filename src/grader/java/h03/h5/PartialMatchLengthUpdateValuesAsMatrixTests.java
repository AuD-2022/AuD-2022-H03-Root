package h03.h5;

import h03.*;
import h03.provider.SimpleSearchStringProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.sourcegrade.jagr.api.testing.extension.JagrExecutionCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link h03.PartialMatchLengthUpdateValuesAsMatrix} with the number of rows of
 * {@link h03.PartialMatchLengthUpdateValuesAsMatrix#matrix} matching the alphabet size.
 */
@TestForSubmission("h03")
public class PartialMatchLengthUpdateValuesAsMatrixTests {

    private static final BiFunction<String, Object, String> EXCEPTION_MESSAGE = (s, needle) -> """
        %s
        Alphabet: %s
        Search string: %s""".formatted(s, Alphabet.SHORT_DESCRIPTION, needle);

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    @ExtendWith(JagrExecutionCondition.class)
    public void testMatrixDimensions(List<Character> needle) throws NoSuchFieldException, IllegalAccessException {
        int[][] matrix = (int[][]) PartialMatchLengthUpdateValuesAsMatrix.class
            .getDeclaredField("matrix")
            .get(new PartialMatchLengthUpdateValuesAsMatrix<>(new FunctionToIntImpl(), needle.toArray(Character[]::new)));

        assertEquals(Alphabet.SIZE, matrix.length, EXCEPTION_MESSAGE.apply(
            "Number of rows of field 'matrix' differs from expected value (alphabet size)", needle));
        for (int i = 0; i < Alphabet.SIZE; i++) {
            assertEquals(needle.size() + 1, matrix[i].length, EXCEPTION_MESSAGE.apply(
                "Number of columns of field 'matrix' differs from expected value (length of search string + 1) at index " + i,
                needle));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    @ExtendWith(JagrExecutionCondition.class)
    public void testStatesWhenMatch(List<Character> needle) throws NoSuchFieldException, IllegalAccessException {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        int[][] matrix = (int[][]) PartialMatchLengthUpdateValuesAsMatrix.class
            .getDeclaredField("matrix")
            .get(new PartialMatchLengthUpdateValuesAsMatrix<>(function, needle.toArray(Character[]::new)));

        for (int i = 0; i < needle.size(); i++) {
            assertEquals(i + 1, matrix[function.apply(needle.get(i))][i], EXCEPTION_MESSAGE.apply("""
                    Follow-up state in field 'matrix' did not match expected value
                    Current character: '%c'
                    Current state: %d""".formatted(needle.get(i), i),
                needle));
        }
        assertEquals(1, matrix[function.apply(needle.get(0))][needle.size()], EXCEPTION_MESSAGE.apply("""
                    Follow-up state in field 'matrix' did not match expected value
                    Current character: '%c'
                    Current state: %d""".formatted(needle.get(0), needle.size()),
            needle));
    }

    /*
     * Tests for states of chars that are not part of the search string, no idea how to phrase it as a method name...
     */
    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    @ExtendWith(JagrExecutionCondition.class)
    public void testDefaultStates(List<Character> needle) throws NoSuchFieldException, IllegalAccessException {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        int[][] matrix = (int[][]) PartialMatchLengthUpdateValuesAsMatrix.class
            .getDeclaredField("matrix")
            .get(new PartialMatchLengthUpdateValuesAsMatrix<>(function, needle.toArray(Character[]::new)));
        List<Character> leftOverCharacters = new ArrayList<>(Alphabet.getAlphabet());
        leftOverCharacters.removeAll(needle);

        for (Character currentCharacter : leftOverCharacters) {
            int i = function.apply(currentCharacter);

            for (int j = 0; j < needle.size() + 1; j++) {
                assertEquals(0, matrix[i][j], EXCEPTION_MESSAGE.apply("""
                        Follow-up state for a character that does not appear in the search string does not equal 0
                        Current character: '%c'
                        Current state: %d""".formatted(currentCharacter, j),
                    needle));
            }
        }
    }

    @Test
    @ExtendWith(JagrExecutionCondition.class)
    public void testComplex() throws NoSuchFieldException, IllegalAccessException {
        FunctionToInt<Character> function = new ComplexFunctionToInt();
        int[][] expectedMatrix = {
            {0, 2, 0, 2},
            {1, 1, 3, 1},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        int[][] actualMatrix = (int[][]) PartialMatchLengthUpdateValuesAsMatrix.class
            .getDeclaredField("matrix")
            .get(new PartialMatchLengthUpdateValuesAsMatrix<>(function, new Character[] {'g', 'a', 'g'}));

        assertEquals(expectedMatrix.length, actualMatrix.length,
            "Number of rows of field 'matrix' differs from the expected value");
        for (int i = 0; i < expectedMatrix.length; i++) {
            assertEquals(expectedMatrix[i].length, actualMatrix[i].length,
                "Number of columns of field 'matrix' differs from expected value at index " + i);
            for (int j = 0; j < expectedMatrix[i].length; j++) {
                assertEquals(expectedMatrix[i][j], actualMatrix[i][j],
                    "Value at index [%d, %d] of field 'matrix' differs from expected value".formatted(i, j));
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    @ExtendWith(JagrExecutionCondition.class)
    public void testGetPartialMatchLengthUpdate(List<Character> needle) throws NoSuchFieldException, IllegalAccessException {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        PartialMatchLengthUpdateValuesAsMatrix<Character> instance = new PartialMatchLengthUpdateValuesAsMatrix<>(function,
            needle.toArray(Character[]::new));
        int[][] matrix = (int[][]) PartialMatchLengthUpdateValuesAsMatrix.class.getDeclaredField("matrix").get(instance);

        for (Character currentCharacter : Alphabet.getAlphabet()) {
            int i = function.apply(currentCharacter);

            for (int j = 0; j < needle.size() + 1; j++) {
                assertEquals(matrix[i][j], instance.getPartialMatchLengthUpdate(j, currentCharacter));
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    public void testGetSearchStringLength(List<Character> needle) {
        PartialMatchLengthUpdateValuesAsMatrix<Character> instance = new PartialMatchLengthUpdateValuesAsMatrix<>(
            new FunctionToIntImpl(),
            needle.toArray(Character[]::new)
        );

        assertEquals(needle.size(), instance.getSearchStringLength(), EXCEPTION_MESSAGE.apply(
            "Value returned by method getSearchStringLength() differs from expected value",
            needle));
    }
}
