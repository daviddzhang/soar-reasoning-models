package classifiertests;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import reasoningmodels.classifiers.BooleanFeature;
import reasoningmodels.classifiers.CategoricalFeature;
import reasoningmodels.classifiers.IFeature;
import reasoningmodels.classifiers.NumericalFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the methods of classes extending IFeature.
 */
public class FeatureTests {
  private final IFeature numerical1 = new NumericalFeature("Number", 2.5);
  private final IFeature boolean1 = new BooleanFeature("Boolean", 0.0);
  private final IFeature boolean2 = new BooleanFeature("Boolean", 1.0);
  private final IFeature categorical1 = new CategoricalFeature("Category", "value1");

  @Test
  public void testNullNameNumericalConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new NumericalFeature(null, 3.0);
    });
  }

  @Test
  public void testNullNameBooleanConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new BooleanFeature(null, 1.0);
    });
  }

  @Test
  public void testNullNameCategoricalConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new BooleanFeature(null, 1.0);
    });
  }

  @Test
  public void testInvalidValueBooleanConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new BooleanFeature("Boolean", 2.0);
    });
  }

  @Test
  public void testNullValueCategoricalConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new CategoricalFeature("Categorical", null);
    });
  }

  @Test
  public void testIsCategoricalNumericalFeature() {
    assertFalse(this.numerical1.isCategorical());
  }

  @Test
  public void testIsCategoricalBooleanFeature() {
    assertFalse(this.boolean1.isCategorical());
  }

  @Test
  public void testIsCategoricalCategoricalFeature() {
    assertTrue(this.categorical1.isCategorical());
  }

  @Test
  public void testValueAsVectorEmptyEnum() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.categorical1.getValueAsVector(new String[] {});
    });
  }

  @Test
  public void testValueAsVectorNullEnum() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.categorical1.getValueAsVector(null);
    });
  }

  @Test
  public void testValueAsVectorTwoEnums() {
    assertTrue(Arrays.equals(this.categorical1.getValueAsVector(new String[] {"value1",
    "value2"}),  new double[] {1, 0}));
  }

  @Test
  public void testValueAsVectorTwoEnumsDifferentOrder() {
    assertTrue(Arrays.equals(this.categorical1.getValueAsVector(new String[] {"value2",
            "value1"}),  new double[] {0, 1}));
  }

  @Test
  public void testGetValueNumerical() {
    assertEquals(2.5, this.numerical1.getValue());
  }

  @Test
  public void testGetValueBoolean() {
    assertEquals(1.0, this.boolean2.getValue());
  }

  @Test
  public void testGetFeatureNameNumerical() {
    assertEquals("Number", this.numerical1.getFeatureName());
  }

  @Test
  public void testGetFeatureNameBoolean() {
    assertEquals("Boolean", this.boolean1.getFeatureName());
  }

  @Test
  public void testGetFeatureNameCategorical() {
    assertEquals("Category", this.categorical1.getFeatureName());
  }

  @Test
  public void testOutOfBoundsScale() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.numerical1.scaleFeature(3, 5);
    });
  }

  @Test
  public void testGetScaledFeatureNumerical() {
    this.numerical1.scaleFeature(0, 5);
    assertEquals(.5, this.numerical1.getScaledValue());
  }

  @Test
  public void testGetScaledFeatureNumericalZeroValue() {
    this.numerical1.scaleFeature(2.5, 5);
    assertEquals(0, this.numerical1.getScaledValue());
  }

  @Test
  public void testGetScaledFeatureNumericalOneValue() {
    this.numerical1.scaleFeature(.5, 2.5);
    assertEquals(1, this.numerical1.getScaledValue());
  }

  @Test
  public void testToStringNumerical() {
    assertEquals("2.5", this.numerical1.toString());
  }

  @Test
  public void testToStringBooleanTrue() {
    assertEquals("TRUE", this.boolean2.toString());
  }

  @Test
  public void testToStringBooleanFalse() {
    assertEquals("FALSE", this.boolean1.toString());
  }

  @Test
  public void testToStringCategorical() {
    assertEquals("value1", this.categorical1.toString());
  }
}
