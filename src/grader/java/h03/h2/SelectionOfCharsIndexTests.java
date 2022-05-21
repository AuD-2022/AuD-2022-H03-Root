package h03.h2;

import h03.SelectionOfCharsIndex;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.sourcegrade.jagr.api.testing.extension.JagrExecutionCondition;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestForSubmission("h03")
public class SelectionOfCharsIndexTests {

    private static final Function<List<Character>, String> inputListToString = characterList -> "[%s]".formatted(
        characterList.stream()
            .map(c -> "'%c' (0x%s)".formatted(c, Integer.toString(c, 16)))
            .collect(Collectors.joining(", ")));

    @ParameterizedTest
    @ArgumentsSource(Provider.class)
    @ExtendWith(JagrExecutionCondition.class)
    public void testConstructor(List<Character> characterList) throws NoSuchFieldException, IllegalAccessException {
        char[] theChars = (char[]) SelectionOfCharsIndex.class
            .getDeclaredField("theChars")
            .get(new SelectionOfCharsIndex(characterList));

        assertEquals(characterList.size(), theChars.length,
            "Length of array 'theChars' differs from expected value for input list " + inputListToString.apply(characterList));
        for (int i = 0; i < characterList.size(); i++) {
            assertEquals(characterList.get(i), theChars[i],
                "Element at index %d in array 'theChars' differs from expected value for input list %s".formatted(i,
                    inputListToString.apply(characterList)));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(Provider.class)
    public void testApply(List<Character> characterList) {
        SelectionOfCharsIndex selectionOfCharsIndex = new SelectionOfCharsIndex(characterList);

        for (int i = 0; i < characterList.size(); i++) {
            assertEquals(i, selectionOfCharsIndex.apply(characterList.get(i)),
                "Value returned by apply(Character) differs from expected value for input list "
                    + inputListToString.apply(characterList));
        }
        assertThrows(Exception.class, () -> selectionOfCharsIndex.apply('a'),
            "Expected apply(Character) to throw an exception since 'a' is not in input list %s, but no exception was thrown"
                .formatted(inputListToString.apply(characterList)));
    }

    @ParameterizedTest
    @ArgumentsSource(Provider.class)
    public void testSizeOfAlphabet(List<Character> characterList) {
        assertEquals(characterList.size(), new SelectionOfCharsIndex(characterList).sizeOfAlphabet(),
            "Value returned by sizeOfAlphabet() differs from expected value for input list "
                + inputListToString.apply(characterList));
    }

    private static class Provider implements ArgumentsProvider {
        private static final long SEED = 0L;
        private static final int LIST_SIZE = 10;
        private static final int NUMBER_OF_LISTS = 5;

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            Random random = new Random(SEED);

            return Stream.generate(() ->
                    random.ints(LIST_SIZE, 'A', 'Z' + 1)
                        .mapToObj(i -> (char) i)
                        .distinct()
                        .toList())
                .limit(NUMBER_OF_LISTS)
                .map(Arguments::of);
        }
    }
}
