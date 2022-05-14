package h03;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PublicTests {

    @Nested
    class UnicodeNumberOfCharIndexTest {

        private final UnicodeNumberOfCharIndex index = new UnicodeNumberOfCharIndex();

        @Test
        void testApply() {
            var c = Character.valueOf('A');
            int ord = 'A';
            assertEquals(ord, index.apply(c));
        }

        @Test
        void testSizeOfAlphabet() {
            assertEquals(1 << 16, index.sizeOfAlphabet());
        }
    }
}
