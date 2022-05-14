package h03;

import org.junit.jupiter.api.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static h03.MD5Assert.assertMd5Matches;
import static org.junit.jupiter.api.Assertions.*;

public class PublicTests {

    enum Alpha { A, B, C, D }

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

        private final EnumIndex<Alpha> index = new EnumIndex<>(Alpha.class);

        @Test
        void testApply() {
            assertEquals(1, index.apply(Alpha.B));
        }

        @Test
        void testSizeOfAlphabet() {
            assertEquals( 4, index.sizeOfAlphabet());
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
