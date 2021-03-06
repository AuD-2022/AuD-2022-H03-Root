package h03.h4;

import h03.*;
import h03.provider.RepeatingSearchStringProvider;
import h03.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@TestForSubmission("h03")
public class PartialMatchLengthUpdateValuesTests {
    private static final PartialMatchLengthUpdateValues<Character> PARTIAL_MATCH_LENGTH_UPDATE_VALUES =
        new PartialMatchLengthUpdateValues<>(new FunctionToIntImpl()) {
            @Override
            public int getPartialMatchLengthUpdate(int state, Character letter) {
                return 0;
            }

            @Override
            public int getSearchStringLength() {
                return 0;
            }
        };
    private static final Field FCT;
    private static final Method COMPUTE_PARTIAL_MATCH_LENGTH_UPDATE_VALUES;

    static {
        try {
            FCT = PartialMatchLengthUpdateValues.class.getDeclaredField("fct");
            FCT.setAccessible(true);
            COMPUTE_PARTIAL_MATCH_LENGTH_UPDATE_VALUES = PartialMatchLengthUpdateValues.class
                .getDeclaredMethod("computePartialMatchLengthUpdateValues", Object[].class);
            COMPUTE_PARTIAL_MATCH_LENGTH_UPDATE_VALUES.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void resetInvocations() {
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods();
    }

    // TODO: test other method once with each FunctionToInt

    @Test
    public void testConstructor() throws IllegalAccessException {
        FunctionToInt<Character> function = new FunctionToIntImpl();
        PartialMatchLengthUpdateValues<Character> instance = new PartialMatchLengthUpdateValues<>(function) {
            @Override
            public int getPartialMatchLengthUpdate(int state, Character letter) {
                return 0;
            }

            @Override
            public int getSearchStringLength() {
                return 0;
            }
        };

        assertSame(function, FCT.get(instance), "Constructor did not assign parameter for field 'fct'");
    }

    @ParameterizedTest
    @ArgumentsSource(RepeatingSearchStringProvider.class)
    public void testComputePartialMatchLengthUpdateValuesWithRepeating(int repeatLength, List<Character> needle)
        throws InvocationTargetException, IllegalAccessException {
        int k = (int) COMPUTE_PARTIAL_MATCH_LENGTH_UPDATE_VALUES.invoke(PARTIAL_MATCH_LENGTH_UPDATE_VALUES,
            (Object) needle.toArray(Character[]::new));

        assertEquals(repeatLength, k, "Number of repeating characters did not match expected value for input " + needle);
    }

    @Test
    public void testComputePartialMatchLengthUpdateValuesWithSame() throws InvocationTargetException, IllegalAccessException {
        Character[] needle = new Character[] {'A', 'A', 'A', 'A', 'A'};
        int k = (int) COMPUTE_PARTIAL_MATCH_LENGTH_UPDATE_VALUES.invoke(PARTIAL_MATCH_LENGTH_UPDATE_VALUES, (Object) needle);

        assertEquals(needle.length - 1, k,
            "Number of repeating characters did not match expected value for input " + Arrays.toString(needle));
    }

    @Test
    public void testComputePartialMatchLengthUpdateValuesWithSingle() throws InvocationTargetException, IllegalAccessException {
        Character[] needle = new Character[] {'A'};
        int k = (int) COMPUTE_PARTIAL_MATCH_LENGTH_UPDATE_VALUES.invoke(PARTIAL_MATCH_LENGTH_UPDATE_VALUES, (Object) needle);

        assertEquals(0, k,
            "Number of repeating characters did not match expected value for input " + Arrays.toString(needle));
    }
}
