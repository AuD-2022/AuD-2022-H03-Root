package h03;

import java.util.Map;

public class ComplexFunctionToInt implements FunctionToInt<Character> {

    private final Map<Character, Integer> map = Map.of(
        'a', 0,
        'g', 1,
        'i', 2,
        'n', 3
    );

    @Override
    public int sizeOfAlphabet() {
        return 4;
    }

    @Override
    public int apply(Character character) throws IllegalArgumentException {
        if (map.containsKey(character)) {
            return map.get(character);
        }
        throw new IllegalArgumentException();
    }
}
