package h03;

import kotlin.Pair;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionToIntImpl implements FunctionToInt<Character> {

    private final Map<Character, Integer> map = Alphabet.generate(Alphabet.SIZE)
        .stream()
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
}
