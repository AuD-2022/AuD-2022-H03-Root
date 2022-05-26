package h03.h3;

import h03.EnumIndex;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestForSubmission("h03")
public class EnumIndexTests {

    private static final Field ENUM_ARRAY;
    private static final Function<Class<?>, String> GET_ENUM_CONSTANTS = clazz -> Arrays.toString(clazz.getEnumConstants());

    static {
        try {
            ENUM_ARRAY = EnumIndex.class.getDeclaredField("enumArray");
            ENUM_ARRAY.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(Provider.class)
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> void testConstructor(Class<T> enumClass) throws IllegalAccessException {
        T[] expectedEnumConstants = enumClass.getEnumConstants();
        T[] enumArray = (T[]) ENUM_ARRAY.get(new EnumIndex<>(enumClass));

        assertArrayEquals(expectedEnumConstants, enumArray,
            "Contents of array in field 'enumArray' differ from the expected values");
    }

    @ParameterizedTest
    @ArgumentsSource(Provider.class)
    public <T extends Enum<T>> void testApply(Class<T> enumClass) {
        T[] enumConstants = enumClass.getEnumConstants();
        EnumIndex<T> enumIndex = new EnumIndex<>(enumClass);

        for (int i = 0; i < enumConstants.length; i++) {
            assertEquals(i, enumIndex.apply(enumConstants[i]),
                "Value returned by apply(T) with parameter '%s' differs from expected value for enum constants %s".formatted(
                    enumConstants[i], GET_ENUM_CONSTANTS.apply(enumClass)));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(Provider.class)
    public <T extends Enum<T>> void testSizeOfAlphabet(Class<T> enumClass) {
        assertEquals(enumClass.getEnumConstants().length, new EnumIndex<>(enumClass).sizeOfAlphabet(),
            "Value returned by sizeOfAlphabet() differs from expected value for enum constants "
                + GET_ENUM_CONSTANTS.apply(enumClass));
    }

    private static class Provider implements ArgumentsProvider {
        private enum ENUM_1 {ELEMENT_1, ELEMENT_2, ELEMENT_3, ELEMENT_4, ELEMENT_5}
        private enum ENUM_2 {A, B, C, D, E, F, G, H, I, J, K, L, M}
        private enum ENUM_3 {LONELIEST_CONSTANT}

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(ENUM_1.class, ENUM_2.class, ENUM_3.class).map(Arguments::of);
        }
    }
}
