package h03;

import java.util.List;
import java.util.stream.Stream;

public class Alphabet {

    public static final int SIZE = 'Z' - 'A' + 1;
    public static final String SHORT_DESCRIPTION = "A-Z";

    private static final List<Character> ALPHABET = Stream.iterate('A', c -> c <= 'Z', c -> (char) (c + 1)).toList();

    public static List<Character> getAlphabet() {
        return ALPHABET;
    }

    public static List<Character> generate(int length) {
        return ALPHABET.subList(0, length);
    }
}
