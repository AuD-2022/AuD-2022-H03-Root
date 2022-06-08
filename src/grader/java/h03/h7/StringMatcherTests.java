package h03.h7;

import h03.*;
import h03.provider.SimpleSearchStringProvider;
import h03.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@TestForSubmission("h03")
public class StringMatcherTests {

    private static String getExceptionMessage(String message, String alphabet, String searchString, String inputString) {
        return """
            %s
            Alphabet: %s
            Search string: %s
            Input string (source): %s""".formatted(message, alphabet, searchString, inputString);
    }
    private static final Field VALUES;

    static {
        try {
            VALUES = StringMatcher.class.getDeclaredField("VALUES");
            VALUES.setAccessible(true);
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
            "^java/util/List of\\(.*\\)Ljava/util/List;$",
            "^java/util/Arrays asList\\(\\[Ljava/lang/Object;\\)Ljava/util/List;$"
        );
    }

    @Test
    public void testConstructor() throws IllegalAccessException {
        PartialMatchLengthUpdateValues<Character> values = new PartialMatchLengthUpdateValues<>(new FunctionToIntImpl()) {
            @Override
            public int getPartialMatchLengthUpdate(int state, Character letter) {
                return 0;
            }

            @Override
            public int getSearchStringLength() {
                return 0;
            }
        };

        assertSame(values, VALUES.get(new StringMatcher<>(values)),
            "Constructor did not assign parameter to field 'VALUES'");
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    public void testSimpleWithMatrix(List<Character> needle) {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        StringMatcher<Character> stringMatcher = new StringMatcher<>(
            new PartialMatchLengthUpdateValuesAsMatrix<>(function, needle.toArray(Character[]::new)));
        List<Integer> matches = stringMatcher.findAllMatches(Alphabet.getAlphabet().toArray(Character[]::new));

        assertEquals(1, matches.size(), getExceptionMessage(
            "Method did not return the expected number of matches", Alphabet.SHORT_DESCRIPTION,
            needle.toString(), Alphabet.getAlphabet().toString()));
        assertEquals(1, matches.get(0), getExceptionMessage(
            "Method did not return the expected match", Alphabet.SHORT_DESCRIPTION,
            needle.toString(), Alphabet.getAlphabet().toString()));
    }

    @ParameterizedTest
    @ArgumentsSource(SimpleSearchStringProvider.class)
    public void testSimpleWithAutomaton(List<Character> needle) {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        StringMatcher<Character> stringMatcher = new StringMatcher<>(
            new PartialMatchLengthUpdateValuesAsAutomaton<>(function, needle.toArray(Character[]::new)));
        List<Integer> matches = stringMatcher.findAllMatches(Alphabet.getAlphabet().toArray(Character[]::new));

        assertEquals(1, matches.size(), getExceptionMessage(
            "Method did not return the expected number of matches", Alphabet.SHORT_DESCRIPTION,
            needle.toString(), Alphabet.getAlphabet().toString()));
        assertEquals(1, matches.get(0), getExceptionMessage(
            "Method did not return the expected match", Alphabet.SHORT_DESCRIPTION,
            needle.toString(), Alphabet.getAlphabet().toString()));
    }

    @Test
    public void testComplexWithMatrix() {
        List<Character> needle = List.of('g', 'a', 'g');
        List<Character> inputString = "gaggingagag".chars().mapToObj(c -> (char) c).toList();
        FunctionToInt<Character> function = new ComplexFunctionToInt();
        StringMatcher<Character> stringMatcher = new StringMatcher<>(
            new PartialMatchLengthUpdateValuesAsMatrix<>(function, needle.toArray(Character[]::new)));
        int[] expectedMatches = {1, 7, 9};
        List<Integer> actualMatches = stringMatcher.findAllMatches(inputString.toArray(Character[]::new));

        assertEquals(3, actualMatches.size(), getExceptionMessage(
            "Method did not return the expected number of matches", "a, g, i, n",
            needle.toString(), inputString.toString()));
        for (int i = 0; i < expectedMatches.length; i++) {
            assertEquals(expectedMatches[i], actualMatches.get(i), getExceptionMessage(
                "Method did not return the expected value", "a, g, i, n",
                needle.toString(), inputString.toString()));
        }
    }

    @Test
    public void testComplexWithAutomaton() {
        List<Character> needle = List.of('g', 'a', 'g');
        List<Character> inputString = "gaggingagag".chars().mapToObj(c -> (char) c).toList();
        FunctionToInt<Character> function = new ComplexFunctionToInt();
        StringMatcher<Character> stringMatcher = new StringMatcher<>(
            new PartialMatchLengthUpdateValuesAsAutomaton<>(function, needle.toArray(Character[]::new)));
        int[] expectedMatches = {1, 7, 9};
        List<Integer> actualMatches = stringMatcher.findAllMatches(inputString.toArray(Character[]::new));

        assertEquals(3, actualMatches.size(), getExceptionMessage(
            "Method did not return the expected number of matches", "a, g, i, n",
            needle.toString(), inputString.toString()));
        for (int i = 0; i < expectedMatches.length; i++) {
            assertEquals(expectedMatches[i], actualMatches.get(i), getExceptionMessage(
                "Method did not return the expected value", "a, g, i, n",
                needle.toString(), inputString.toString()));
        }
    }
}
