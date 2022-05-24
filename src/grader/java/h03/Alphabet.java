package h03;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Alphabet {

    public static final int SIZE = 'Z' - 'A' + 1;
    public static final String SHORT_DESCRIPTION = "A-Z";

    private static final Function<Integer, Stream<Character>> GENERATOR = i -> Stream.iterate('A', c -> (char) (c + 1))
        .limit(Math.min(Math.max(i, 0), SIZE));

    public static List<Character> generate(int length) {
        return GENERATOR.apply(length).toList();
    }
}
