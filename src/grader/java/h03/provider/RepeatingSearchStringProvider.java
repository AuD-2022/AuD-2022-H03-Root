package h03.provider;

import h03.Alphabet;
import kotlin.Pair;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RepeatingSearchStringProvider implements ArgumentsProvider {

    private static final long SEED = 0L;
    private static final int STREAM_SIZE = 5;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Random random = new Random(SEED);

        return Stream.generate(() -> {
                int initialSearchStringLength = random.nextInt(1, 26 + 1);
                int repeatLength = random.nextInt(0, initialSearchStringLength + 1);
                String initialSearchString = Alphabet.generate(initialSearchStringLength)
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining());
                String searchString = initialSearchString + initialSearchString.substring(0, repeatLength);

                return new Pair<>(repeatLength, searchString);
            })
            .limit(STREAM_SIZE)
            .map(pair -> Arguments.of(pair.getFirst(), pair.getSecond()));
    }
}
