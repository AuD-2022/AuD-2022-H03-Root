package h03;

import h03.h2.SelectionOfCharsIndexTests;
import h03.h2.UnicodeNumberOfCharIndexTests;
import h03.h3.EnumIndexTests;
import h03.h4.PartialMatchLengthUpdateValuesTests;
import h03.h5.Alt_PartialMatchLengthUpdateValuesAsMatrixTests;
import h03.h5.PartialMatchLengthUpdateValuesAsMatrixTests;
import h03.h6.PartialMatchLengthUpdateValuesAsAutomatonTests;
import h03.transformer.AccessTransformer;
import org.sourcegrade.jagr.api.rubric.*;
import org.sourcegrade.jagr.api.testing.RubricConfiguration;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.stream.Stream;

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
    private static final BiFunction<String, Stream<Callable<Method>>, Criterion> OR_CRITERION = (shortDescription, callables) ->
        Criterion.builder()
            .shortDescription(shortDescription)
            .grader(Grader.testAwareBuilder()
                .requirePass(JUnitTestRef.or(callables.map(JUnitTestRef::ofMethod).toArray(JUnitTestRef[]::new)))
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

    private static final Criterion H5 = Criterion.builder()
        .shortDescription("H5 | Tabellenimplementationen für String Matching BOFA als Matrix")
        .addChildCriteria(
            OR_CRITERION.apply("Die Dimensionen der Matrix sind korrekt.", Stream.of(
                () -> PartialMatchLengthUpdateValuesAsMatrixTests.class
                    .getDeclaredMethod("testMatrixDimensions", List.class),
                () -> Alt_PartialMatchLengthUpdateValuesAsMatrixTests.class
                    .getDeclaredMethod("testMatrixDimensions", List.class))),
            OR_CRITERION.apply("Die Zustände der Matrix sind korrekt, wenn ein Match vorliegt.", Stream.of(
                () -> PartialMatchLengthUpdateValuesAsMatrixTests.class
                    .getDeclaredMethod("testStatesWhenMatch", List.class),
                () -> Alt_PartialMatchLengthUpdateValuesAsMatrixTests.class
                    .getDeclaredMethod("testStatesWhenMatch", List.class))),
            OR_CRITERION.apply("Folgezustände für Zeichen, die nicht im Suchstring vorkommen sind immer 0.", Stream.of(
                () -> PartialMatchLengthUpdateValuesAsMatrixTests.class
                    .getDeclaredMethod("testDefaultStates", List.class),
                () -> Alt_PartialMatchLengthUpdateValuesAsMatrixTests.class
                    .getDeclaredMethod("testDefaultStates", List.class))),
            Criterion.builder()
                .shortDescription("Die Matrix ist vollständig korrekt für einen komplexeren Fall (Vorlesungsfolien).")
                .maxPoints(2)
                .grader(Grader.testAwareBuilder()
                    .requirePass(JUnitTestRef.or(
                        JUnitTestRef.ofMethod(() -> PartialMatchLengthUpdateValuesAsMatrixTests.class
                            .getDeclaredMethod("testComplex")),
                        JUnitTestRef.ofMethod(() -> Alt_PartialMatchLengthUpdateValuesAsMatrixTests.class
                            .getDeclaredMethod("testComplex"))
                    ))
                    .pointsFailedMin()
                    .pointsPassedMax()
                    .build())
                .build(),
            OR_CRITERION.apply("Methode getPartialMatchLengthUpdate(int, T) liefert den korrekten Folgezustand zurück.",
                Stream.of(
                    () -> PartialMatchLengthUpdateValuesAsMatrixTests.class
                        .getDeclaredMethod("testGetPartialMatchLengthUpdate", List.class),
                    () -> Alt_PartialMatchLengthUpdateValuesAsMatrixTests.class
                        .getDeclaredMethod("testGetPartialMatchLengthUpdate", List.class))),
            DEFAULT_CRITERION.apply("Methode getSearchStringLength() liefert den korrekten Wert zurück.",
                () -> PartialMatchLengthUpdateValuesAsMatrixTests.class
                    .getDeclaredMethod("testGetSearchStringLength", List.class))
        )
        .build();

    private static final Criterion H6 = Criterion.builder()
        .shortDescription("H6 | Tabellenimplementationen für String Matching BOFA als Automat")
        .addChildCriteria(
            DEFAULT_CRITERION.apply("Die Größe des Arrays 'theStates' entspricht der Anzahl der möglichen Zustände.",
                () -> PartialMatchLengthUpdateValuesAsAutomatonTests.class
                    .getDeclaredMethod("testTheStatesLength", List.class)),
            DEFAULT_CRITERION.apply("Alle Listen in 'theStates' haben die korrekte Anzahl von Transitionen.",
                () -> PartialMatchLengthUpdateValuesAsAutomatonTests.class
                    .getDeclaredMethod("testTheStatesListSize", List.class)),
            DEFAULT_CRITERION.apply("Die Zustände und Transitionen sind korrekt für den Fall, dass ein Match vorliegt.",
                () -> PartialMatchLengthUpdateValuesAsAutomatonTests.class
                    .getDeclaredMethod("testStatesWhenMatch", List.class)),
            DEFAULT_CRITERION.apply("Transitionen für Zeichen, die nicht im Suchstring vorkommen, verweisen immer auf Zustand 0.",
                () -> PartialMatchLengthUpdateValuesAsAutomatonTests.class
                    .getDeclaredMethod("testDefaultStates", List.class)),
            DEFAULT_CRITERION.apply("Der Automat ist vollständig korrekt für einen komplexeren Fall (Vorlesungsfolien).",
                () -> PartialMatchLengthUpdateValuesAsAutomatonTests.class
                    .getDeclaredMethod("testComplex")),
            DEFAULT_CRITERION.apply("Methode getPartialMatchLengthUpdate(int, T) liefert den korrekten Folgezustand zurück.",
                () -> PartialMatchLengthUpdateValuesAsAutomatonTests.class
                    .getDeclaredMethod("testGetPartialMatchLengthUpdate", List.class)),
            DEFAULT_CRITERION.apply("Methode getSearchStringLength() liefert den korrekten Wert zurück.",
                () -> PartialMatchLengthUpdateValuesAsAutomatonTests.class
                    .getDeclaredMethod("testGetSearchStringLength", List.class))
        )
        .build();

    public static final Rubric RUBRIC = Rubric.builder()
        .title("H03")
        .addChildCriteria(H2, H3, H4, H5, H6)
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
