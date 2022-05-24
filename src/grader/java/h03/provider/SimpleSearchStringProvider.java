package h03.provider;

import h03.Alphabet;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Random;
import java.util.stream.Stream;

public class SimpleSearchStringProvider implements ArgumentsProvider {

    private static final long SEED = 0L;
    private static final int NUMBER_OF_SEARCH_STRINGS = 5;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Random random = new Random(SEED);

        return Stream.generate(() -> Alphabet.generate(random.nextInt(1, 26 + 1)))
            .limit(NUMBER_OF_SEARCH_STRINGS)
            .map(Arguments::of);
    }
}
