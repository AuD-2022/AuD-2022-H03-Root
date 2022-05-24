package h03.h4;

import h03.*;
import h03.provider.RepeatingSearchStringProvider;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.sourcegrade.jagr.api.testing.extension.JagrExecutionCondition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    // TODO: test other method once with each FunctionToInt

    @ParameterizedTest
    @ArgumentsSource(RepeatingSearchStringProvider.class)
    @ExtendWith(JagrExecutionCondition.class)
    public void testComputePartialMatchLengthUpdateValues(int repeatLength, String searchString) throws NoSuchMethodException,
        InvocationTargetException, IllegalAccessException {
        Method computePartialMatchLengthUpdateValues = PartialMatchLengthUpdateValues.class
            .getDeclaredMethod("computePartialMatchLengthUpdateValues", Object[].class);

        int k = (int) computePartialMatchLengthUpdateValues.invoke(PARTIAL_MATCH_LENGTH_UPDATE_VALUES,
            (Object) searchString.chars()
                .mapToObj(c -> (char) c)
                .toArray(Character[]::new));
        assertEquals(repeatLength, k, "Number of repeating characters did not match expected value for input "
            + "[%s]".formatted(searchString.chars().mapToObj("'%c'"::formatted).collect(Collectors.joining(", "))));
    }
}
