import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import reasoningmodels.bayesnet.IRandomVariable;
import reasoningmodels.bayesnet.RandomVariableImpl;
import reasoningmodels.classifiers.BooleanFeature;
import reasoningmodels.classifiers.IFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the methods of RandomVariableImpl.
 */
public class RandomVariableTests {
  private final IRandomVariable bTrue = new RandomVariableImpl("B", true);
  private final IRandomVariable bTrue2 = new RandomVariableImpl("B", true);
  private final IRandomVariable bTrue3 = new RandomVariableImpl("B", true);
  private final IRandomVariable zFalse = new RandomVariableImpl("Z", false);
  private final IRandomVariable bFalse = new RandomVariableImpl("B", false);
  private final List<IRandomVariable> listOfVar = new ArrayList<>(Arrays.asList(bTrue, zFalse,
          bFalse));
  private final List<IFeature> listOfFeature = new ArrayList<>(Arrays.asList(new BooleanFeature("B",
                  1.0),
          new BooleanFeature("Z", 0.0), new BooleanFeature("B", 0.0)));
  private final List<String> listOfNames = new ArrayList<>(Arrays.asList("B", "Z", "B"));

  @Test
  void testNullConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new RandomVariableImpl(null, true);
    });
  }

  @Test
  void testToString1() {
    assertEquals("+B", bTrue.toString());
  }

  @Test
  void testToString2() {
    assertEquals("-Z", zFalse.toString());
  }

  @Test
  void testEqualsReflexive() {
    assertEquals(bTrue, bTrue);
    assertEquals(bTrue.hashCode(), bTrue.hashCode());
  }

  @Test
  void testEqualsSymmetric() {
    assertEquals(bTrue2, bTrue);
    assertEquals(bTrue2.hashCode(), bTrue.hashCode());
  }

  @Test
  void testEqualsTransitive() {
    assertEquals(bTrue2, bTrue);
    assertEquals(bTrue2, bTrue3);
    assertEquals(bTrue, bTrue3);
    assertEquals(bTrue2.hashCode(), bTrue.hashCode());
    assertEquals(bTrue2.hashCode(), bTrue3.hashCode());
    assertEquals(bTrue3.hashCode(), bTrue.hashCode());
  }

  @Test
  void testNotEquals() {
    assertNotEquals(bTrue, bFalse);
    assertNotEquals(bTrue.hashCode(), bFalse.hashCode());
    assertNotEquals(bTrue, zFalse);
    assertNotEquals(bTrue.hashCode(), zFalse.hashCode());
  }

  @Test
  void testFeatureToVarNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      RandomVariableImpl.featureListToVarList(null);
    });
  }

  @Test
  void testFeatureToVar() {
    assertEquals(this.listOfVar, RandomVariableImpl.featureListToVarList(this.listOfFeature));
  }

  @Test
  void testVarToNameNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      RandomVariableImpl.listOfVarsToListOfNames(null);
    });
  }

  @Test
  void testVarToName() {
    assertEquals(this.listOfNames, RandomVariableImpl.listOfVarsToListOfNames(this.listOfVar));
  }
}
