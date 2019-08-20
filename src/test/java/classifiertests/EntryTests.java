package classifiertests;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import reasoningmodels.classifiers.BooleanFeature;
import reasoningmodels.classifiers.CategoricalFeature;
import reasoningmodels.classifiers.EntryImpl;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;
import reasoningmodels.classifiers.NumericalFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the methods of EntryImpl.
 */
public class EntryTests {
  private final IFeature numerical1 = new NumericalFeature("Number", 2.5);
  private final IFeature boolean1 = new BooleanFeature("Boolean", 0.0);
  private final IFeature boolean2 = new BooleanFeature("Boolean", 1.0);
  private final IFeature categorical1 = new CategoricalFeature("Category", "value1");

  private final IEntry entry = new EntryImpl(Arrays.asList(numerical1, boolean1, boolean2,
          categorical1));

  @Test
  public void testNullEntryConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new EntryImpl(null);
    });
  }

  @Test
  public void testToString() {
    assertEquals("[2.5, FALSE, TRUE, value1]", entry.toString());
  }

  @Test
  public void testContainsFeatureNumerical() {
    assertTrue(this.entry.containsFeature("Number"));
  }

  @Test
  public void testContainsFeatureBoolean() {
    assertTrue(this.entry.containsFeature("Boolean"));
  }

  @Test
  public void testContainsFeatureCategorical() {
    assertTrue(this.entry.containsFeature("Category"));
  }

  @Test
  public void testContainsFeatureFalse() {
    assertFalse(this.entry.containsFeature("Feature"));
  }
}
