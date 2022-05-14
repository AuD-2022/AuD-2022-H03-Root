package h03;

import org.junit.jupiter.api.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static h03.MD5Assert.assertMd5Matches;
import static org.junit.jupiter.api.Assertions.*;

public class PublicTests {

    enum Alpha {
        A, B, C, D;

        public static Alpha[] toSearchString(String s) {
            return s.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .map(Alpha::valueOf)
                .toArray(Alpha[]::new);
        }
    }

    private final EnumIndex<Alpha> index = new EnumIndex<>(Alpha.class);

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
            assertMd5Matches(
                "297ce0b3c836ae307023d7c2c3a7b1ec",
                index.sizeOfAlphabet(),
                "sizeOfAlphabet did not return the correct value: hashes don't match");
        }
    }

    @Nested
    class SelectionOfCharsIndexTest {

        private final SelectionOfCharsIndex index;

        SelectionOfCharsIndexTest() {
            var alpha = "ABCD"
                .chars()
                .mapToObj(c -> (char) c)
                .toList();
            index = new SelectionOfCharsIndex(alpha);
        }

        @Test
        void testApply() {
            assertEquals(1, index.apply('B'));
        }

        @Test
        void testSizeOfAlphabet() {
            assertEquals( 4, index.sizeOfAlphabet());
        }
    }

    @Nested
    class EnumIndexTest {

        @Test
        void testApply() {
            assertEquals(1, index.apply(Alpha.B));
        }

        @Test
        void testSizeOfAlphabet() {
            assertEquals( 4, index.sizeOfAlphabet());
        }
    }

    @Nested
    class PartialMatchLengthUpdateValuesTest extends PartialMatchLengthUpdateValues<Alpha> {

        public PartialMatchLengthUpdateValuesTest() {
            super(index);
        }

        @Test
        void testComputePartialMatchLengthUpdateValues() {
            var searchString = Alpha.toSearchString("ABBABBA");
            assertEquals(4, computePartialMatchLengthUpdateValues(searchString));
        }

        @Override
        public int getPartialMatchLengthUpdate(int state, Alpha letter) {
            return 1;
        }

        @Override
        public int getSearchStringLength() {
            return 1;
        }
    }
}

class MD5Assert {

    public static void assertMd5Matches(String expectedHash, int actual, String message) {
        try {
            var s = String.valueOf(actual);
            assertEquals(expectedHash, md5(s), message);
        } catch (NoSuchAlgorithmException e) {
            fail("Could not load md5 hash", e);
        }
    }

    private static String md5(String s) throws NoSuchAlgorithmException {
        var md5 = MessageDigest.getInstance("MD5");
        md5.update(s.getBytes());
        return toHex(md5.digest());
    }

    private static String toHex(byte[] bytes) {
        var sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append("%02x".formatted(b));
        }
        return sb.toString();
    }
}
