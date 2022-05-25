package h03;

public class FunctionToIntImpl implements FunctionToInt<Character> {

    @Override
    public int sizeOfAlphabet() {
        return Alphabet.SIZE;
    }

    @Override
    public int apply(Character character) throws IllegalArgumentException {
        if (character >= 'A' && character <= 'Z') {
            return character - 'A';
        } else {
            throw new IllegalArgumentException();
        }
    }
}
