package h03.h2;

import h03.UnicodeNumberOfCharIndex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestForSubmission("h03")
public class UnicodeNumberOfCharIndexTests {

    private static final UnicodeNumberOfCharIndex INSTANCE = new UnicodeNumberOfCharIndex();

    @ParameterizedTest
    @ArgumentsSource(Provider.class)
    public void testApply(int i) {
        assertDoesNotThrow(() -> INSTANCE.apply((char) i));
        assertEquals(i, INSTANCE.apply((char) i),
            "Return value of method apply(Character) did not match the expected value for input '%c' (U+%s)".formatted(
                (char) i, Integer.toString(i, 16)));
    }

    @Test
    public void testSizeOfAlphabet() {
        assertEquals(Character.MAX_VALUE + 1, INSTANCE.sizeOfAlphabet(),
            "Return value of method sizeOfAlphabet() did not match the expected value (Character.MAX_VALUE + 1)");
    }

    private static class Provider implements ArgumentsProvider {
        private static final long SEED = 0L;
        private static final int NUMBER_OF_CHARS = 10;

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return new Random(SEED).ints(NUMBER_OF_CHARS, 0, 0x10000)
                .mapToObj(Arguments::of);
        }
    }
}
