package h03;

import h03.h2.SelectionOfCharsIndexTests;
import h03.h2.UnicodeNumberOfCharIndexTests;
import h03.h3.EnumIndexTests;
import h03.h4.PartialMatchLengthUpdateValuesTests;
import h03.transformer.AccessTransformer;
import org.sourcegrade.jagr.api.rubric.*;
import org.sourcegrade.jagr.api.testing.RubricConfiguration;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

@RubricForSubmission("h03")
public class H03_RubricProvider implements RubricProvider {

    private static final BiFunction<String, Callable<Method>, Criterion> DEFAULT_CRITERION = (shortDescription, callable) ->
        Criterion.builder()
            .shortDescription(shortDescription)
            .grader(Grader.testAwareBuilder()
                .requirePass(JUnitTestRef.ofMethod(callable))
                .pointsFailedMin()
                .pointsPassedMax()
                .build())
            .build();

    private static final Criterion H2 = Criterion.builder()
        .shortDescription("H2 | Konkrete Alphabete mit Unicode-Zeichen")
        .addChildCriteria(
            DEFAULT_CRITERION.apply("Methode apply(Character) in Klasse UnicodeNumberOfCharIndex funktioniert wie beschrieben.",
                () -> UnicodeNumberOfCharIndexTests.class.getDeclaredMethod("testApply", int.class)),
            DEFAULT_CRITERION.apply("Methode sizeOfAlphabet() in Klasse UnicodeNumberOfCharIndex funktioniert wie beschrieben.",
                () -> UnicodeNumberOfCharIndexTests.class.getDeclaredMethod("testSizeOfAlphabet")),
            Criterion.builder()
                .shortDescription("Konstruktor und Methode sizeOfAlphabet() in Klasse SelectionOfCharsIndex " +
                    "funktionieren wie beschrieben.")
                .grader(Grader.testAwareBuilder()
                    .requirePass(JUnitTestRef.and(
                        JUnitTestRef.ofMethod(() -> SelectionOfCharsIndexTests.class.getDeclaredMethod("testConstructor",
                            List.class)),
                        JUnitTestRef.ofMethod(() -> SelectionOfCharsIndexTests.class.getDeclaredMethod("testSizeOfAlphabet",
                            List.class))))
                    .pointsFailedMin()
                    .pointsPassedMax()
                    .build())
                .build(),
            DEFAULT_CRITERION.apply("Methode apply(Character) in Klasse SelectionOfCharsIndex funktioniert wie beschrieben.",
                () -> SelectionOfCharsIndexTests.class.getDeclaredMethod("testApply", List.class))
        )
        .build();

    private static final Criterion H3 = Criterion.builder()
        .shortDescription("H3 | Konkrete Alphabete mit Enumerationen")
        .addChildCriteria(
            DEFAULT_CRITERION.apply("Konstruktor in Klasse EnumIndex funktioniert wie beschrieben.",
                () -> EnumIndexTests.class.getDeclaredMethod("testConstructor", Class.class)),
            DEFAULT_CRITERION.apply("Methode apply(T) in Klasse EnumIndex funktioniert wie beschrieben.",
                () -> EnumIndexTests.class.getDeclaredMethod("testApply", Class.class)),
            DEFAULT_CRITERION.apply("Methode sizeOfAlphabet() in Klasse EnumIndex funktioniert wie beschrieben.",
                () -> EnumIndexTests.class.getDeclaredMethod("testSizeOfAlphabet", Class.class))
        )
        .build();

    private static final Criterion H4 = Criterion.builder()
        .shortDescription("H4 | Abstrakte Tabelle für String Matching BOFA")
        .addChildCriteria(
            DEFAULT_CRITERION.apply("Methode computePartialMatchLengthUpdateValues(T[]) funktioniert wie beschrieben.",
                () -> PartialMatchLengthUpdateValuesTests.class
                    .getDeclaredMethod("testComputePartialMatchLengthUpdateValues", int.class, String.class))
        )
        .build();

    public static final Rubric RUBRIC = Rubric.builder()
        .title("H03")
        .addChildCriteria(H2, H3, H4)
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }

    @Override
    public void configure(RubricConfiguration configuration) {
        configuration.addTransformer(new AccessTransformer());
    }
}
