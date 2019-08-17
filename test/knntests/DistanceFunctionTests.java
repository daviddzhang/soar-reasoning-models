package knntests;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.junit.jupiter.api.Test;

import reasoningmodels.classifiers.NumericalFeature;
import reasoningmodels.knn.IDistanceFunction;
import reasoningmodels.knn.L2Distance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for methods of IDistanceFunction implementing classes.
 */
public class DistanceFunctionTests {
  private final IDistanceFunction l2 = new L2Distance();

  @Test
  void testL2DistanceZero() {
    assertEquals(0, l2.evaluate(new double[] {}, new double[] {}));
  }

  @Test
  void testL2DistanceZero2() {
    assertEquals(0, l2.evaluate(new double[] {0, 0}, new double[] {0, 0}));
  }

  @Test
  void testDifferingSizes() {
    assertThrows(DimensionMismatchException.class, () -> {
      l2.evaluate(new double[] {1, 0}, new double[] {0});
    });
  }

  @Test
  void testL2Distance() {
    assertEquals(1, l2.evaluate(new double[] {1, 0}, new double[] {0, 0}));
  }
}
