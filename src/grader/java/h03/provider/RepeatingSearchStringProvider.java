package h03.provider;

import h03.Alphabet;
import kotlin.Pair;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class RepeatingSearchStringProvider implements ArgumentsProvider {

    private static final long SEED = 0L;
    private static final int STREAM_SIZE = 3;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Random random = new Random(SEED);

        return Stream.generate(() -> {
                List<Character> originalSequence = Alphabet.generate(random.nextInt(1, 26 + 1));
                int repeatLength = random.nextInt(1, originalSequence.size());

                return new Pair<>(repeatLength,
                    Stream.concat(originalSequence.stream(), originalSequence.stream().limit(repeatLength)).toList());
            })
            .limit(STREAM_SIZE)
            .map(pair -> Arguments.of(pair.getFirst(), pair.getSecond()));
    }
}
