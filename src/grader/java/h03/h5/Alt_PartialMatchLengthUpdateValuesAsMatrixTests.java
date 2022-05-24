package h03.h5;

import h03.Alphabet;
import h03.FunctionToInt;
import h03.FunctionToIntImpl;
import h03.PartialMatchLengthUpdateValuesAsMatrix;
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
 * Tests for {@link PartialMatchLengthUpdateValuesAsMatrix} with the number of rows of
 * {@link PartialMatchLengthUpdateValuesAsMatrix#matrix} matching the search string length + 1.
 */
@TestForSubmission("h03")
public class Alt_PartialMatchLengthUpdateValuesAsMatrixTests {

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

        assertEquals(needle.size() + 1, matrix.length, EXCEPTION_MESSAGE.apply(
            "Number of rows of field 'matrix' differs from expected value (alphabet size)", needle));
        for (int i = 0; i < needle.size() + 1; i++) {
            assertEquals(Alphabet.SIZE, matrix[i].length, EXCEPTION_MESSAGE.apply(
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
            assertEquals(i + 1, matrix[i][function.apply(needle.get(i))], EXCEPTION_MESSAGE.apply("""
                    Follow-up state in field 'matrix' did not match expected value
                    Current character: '%c'
                    Current state: %d""".formatted(needle.get(i), i),
                needle));
        }
        assertEquals(1, matrix[needle.size()][function.apply(needle.get(0))], EXCEPTION_MESSAGE.apply("""
                    Follow-up state in field 'matrix' did not match expected value
                    Current character: '%c'
                    Current state: %d""".formatted(needle.get(0), needle.size()),
            needle));
    }

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
                assertEquals(0, matrix[j][i], EXCEPTION_MESSAGE.apply("""
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
        FunctionToInt<Character> function = new FunctionToInt<>() {
            private final Map<Character, Integer> map = Map.of(
                'a', 0,
                'g', 1,
                'i', 2,
                'n', 3
            );

            @Override
            public int sizeOfAlphabet() {
                return 4;
            }

            @Override
            public int apply(Character character) throws IllegalArgumentException {
                if (map.containsKey(character)) {
                    return map.get(character);
                }
                throw new IllegalArgumentException();
            }
        };
        int[][] expectedMatrix = {
            {0, 1, 0, 0},
            {2, 1, 0, 0},
            {0, 3, 0, 0},
            {2, 1, 0, 0}
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

        for (int i = 0; i < needle.size() + 1; i++) {
            for (Character currentCharacter : Alphabet.getAlphabet()) {
                assertEquals(matrix[i][function.apply(currentCharacter)],
                    instance.getPartialMatchLengthUpdate(i, currentCharacter));
            }
        }
    }
}
