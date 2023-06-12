package pl.beben.computersimulation.device.booleanfunction;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.ReflectionUtils;
import pl.beben.computersimulation.device.OutputSpy;
import pl.beben.computersimulation.device.TestWorld;
import pl.beben.computersimulation.device.booleanfunction.gate.MultiInputAndGate;
import pl.beben.computersimulation.device.powersupply.VccPowerSupply;
import pl.beben.computersimulation.utils.TruthTableSanitizer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
class BooleanFunctionTest {

  // these classes will be picked up by computeParametersStream method
  public static class ThreeInputAndGate extends MultiInputAndGate {
    public ThreeInputAndGate(String id) {
      super(id, 3);
    }
  }

  public static class FourInputAndGate extends MultiInputAndGate {
    public FourInputAndGate(String id) {
      super(id, 4);
    }
  }

  public static class TwoInputDecoder extends Decoder {
    public TwoInputDecoder(String id) {
      super(id, 2);
    }
  }

  public static class ThreeInputDecoder extends Decoder {
    public ThreeInputDecoder(String id) {
      super(id, 3);
    }
  }

  @ParameterizedTest
  @MethodSource("computeParametersStream")
  public void test(BooleanFunction booleanFunction, List<Boolean> inputs, List<Boolean> expectedOutputs) {
    // given
    @Cleanup final var world = new TestWorld();

    for (int i = 0; i < inputs.size(); i++) {
      final var powerSupply = new VccPowerSupply("powerSupply[" + i + "]");
      world.registerAsTopLevelDevice(powerSupply);
      powerSupply.setValue(inputs.get(i));

      booleanFunction.getInput(i).connectTo(powerSupply);
    }

    final var outputSpies = new ArrayList<OutputSpy>();
    for (int i = 0; i < expectedOutputs.size(); i++) {
      final var outputSpy = new OutputSpy("outputSpy[" + i + "]");
      outputSpies.add(outputSpy);

      outputSpy.connectTo(booleanFunction.getOutput(i));
    }

    // when
    world.runSynchronously();

    // then
    final var actualOutputs = outputSpies.stream()
      .map(OutputSpy::getValue)
      .collect(Collectors.toList());

    Assertions.assertEquals(expectedOutputs, actualOutputs);
  }

  private static Stream<Arguments> computeParametersStream() {

    final var booleanFunctionClasses = ReflectionUtils
      .findAllClassesInPackage(
        "pl.beben",
        classObject ->
          !Modifier.isAbstract(classObject.getModifiers()) &&
          BooleanFunction.class.isAssignableFrom(classObject),
        className ->
          !className.equals(MultiInputAndGate.class.getName()) &&
          !className.equals(Decoder.class.getName())
      );

    Assertions.assertFalse(booleanFunctionClasses.isEmpty(), "No classes found, has package name been renamed?");

    return booleanFunctionClasses.stream()
      .map(BooleanFunctionTest::construct)
      .map(BooleanFunctionTest::computeTestCases).flatMap(List::stream);
  }

  @SneakyThrows
  private static BooleanFunction construct(Class classObject) {
    return (BooleanFunction) classObject.getConstructor(String.class).newInstance(classObject.getSimpleName());
  }

  private static List<Arguments> computeTestCases(BooleanFunction booleanFunction) {

    final var truthTable = TruthTableSanitizer.sanitize(booleanFunction.truthTable);

    final var testCases = new ArrayList<Arguments>();

    final var lines = truthTable.split("\n");
    for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
      final var line = lines[lineIndex].trim();
      final var characters = line.split("\s*");

      final var inputs = new ArrayList<Boolean>();
      final var expectedOutputs = new ArrayList<Boolean>();

      for (int characterIndex = 0; characterIndex < characters.length; characterIndex++) {
        final var character = characters[characterIndex].trim();
        Assertions.assertTrue(character.equals("1") || character.equals("0"), "Illegal character in the truthTable for " + booleanFunction + ": '" + character + "'");

        final var isTrue = character.equals("1");
        final var isInput = characterIndex < booleanFunction.getInputsCount();

        if (isInput)
          inputs.add(isTrue);
        else
          expectedOutputs.add(isTrue);
      }

      testCases.add(
        Arguments.of(
          booleanFunction,
          inputs,
          expectedOutputs
        )
      );
    }

    return testCases;
  }

}