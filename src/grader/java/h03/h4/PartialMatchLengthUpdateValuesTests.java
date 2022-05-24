package h03.h4;

import h03.*;
import kotlin.Pair;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.sourcegrade.jagr.api.testing.extension.JagrExecutionCondition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestForSubmission("h03")
public class PartialMatchLengthUpdateValuesTests {

    private static final FunctionToInt<Character> FUNCTION_TO_INT = new FunctionToInt<>() {
        private final Map<Character, Integer> map = Stream.iterate('A', c -> c < 'Z', c -> (char) (c + 1))
            .map(new Function<Character, Pair<Character, Integer>>() {
                private int index = 0;

                @Override
                public Pair<Character, Integer> apply(Character character) {
                    return new Pair<>(character, index++);
                }
            })
            .collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));

        @Override
        public int sizeOfAlphabet() {
            return map.size();
        }

        @Override
        public int apply(Character character) throws IllegalArgumentException {
            if (map.containsKey(character)) {
                return map.get(character);
            } else {
                throw new IllegalArgumentException();
            }
        }
    };
    private static final PartialMatchLengthUpdateValues<Character> PARTIAL_MATCH_LENGTH_UPDATE_VALUES =
        new PartialMatchLengthUpdateValues<>(FUNCTION_TO_INT) {
            @Override
            public int getPartialMatchLengthUpdate(int state, Character letter) {
                return 0;
            }

            @Override
            public int getSearchStringLength() {
                return 0;
            }
        };

    // TODO: test other method once with each FunctionToInt

    @ParameterizedTest
    @ArgumentsSource(SearchStringProvider.class)
    @ExtendWith(JagrExecutionCondition.class)
    public void testComputePartialMatchLengthUpdateValues(int repeatLength, String searchString) throws NoSuchMethodException,
        InvocationTargetException, IllegalAccessException {
        Method computePartialMatchLengthUpdateValues = PartialMatchLengthUpdateValues.class
            .getDeclaredMethod("computePartialMatchLengthUpdateValues", Object[].class);

        int k = (int) computePartialMatchLengthUpdateValues.invoke(PARTIAL_MATCH_LENGTH_UPDATE_VALUES,
            (Object) searchString.chars()
                .mapToObj(c -> (char) c)
                .toArray(Character[]::new));
        assertEquals(repeatLength, k, "Number of repeating characters did not match expected value for input "
            + "[%s]".formatted(searchString.chars().mapToObj("'%c'"::formatted).collect(Collectors.joining(", "))));
    }

    private static class SearchStringProvider implements ArgumentsProvider {
        private static final long SEED = 0L;
        private static final int STREAM_SIZE = 5;

        private static final Function<Integer, Stream<Character>> ALPHABET_GENERATOR = i ->
            Stream.iterate('A', c -> (char) (c + 1))
                .limit(Math.min(Math.max(i, 0), 'Z' - 'A' + 1));

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            Random random = new Random(SEED);

            return Stream.generate(() -> {
                    int initialSearchStringLength = random.nextInt(1, 26 + 1);
                    int repeatLength = random.nextInt(0, initialSearchStringLength + 1);
                    String initialSearchString = ALPHABET_GENERATOR.apply(initialSearchStringLength)
                        .map(Objects::toString)
                        .collect(Collectors.joining());
                    String searchString = initialSearchString + initialSearchString.substring(0, repeatLength);

                    return new Pair<>(repeatLength, searchString);
                })
                .limit(STREAM_SIZE)
                .map(pair -> Arguments.of(pair.getFirst(), pair.getSecond()));
        }
    }
}
