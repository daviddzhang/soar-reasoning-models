package naivebayestests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.classifiers.BooleanFeature;
import reasoningmodels.classifiers.CategoricalFeature;
import reasoningmodels.classifiers.EntryImpl;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;
import reasoningmodels.classifiers.NumericalFeature;
import reasoningmodels.naivebayes.NaiveBayes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NaiveBayesTests {
  private final IFeature numerical1 = new NumericalFeature("Number", 2.5);
  private final IFeature numerical2 = new NumericalFeature("Number", 5.0);
  private final IFeature numerical3 = new NumericalFeature("Number", 3.0);
  private final IFeature boolean1 = new BooleanFeature("Boolean", 0.0);
  private final IFeature boolean2 = new BooleanFeature("Boolean", 1.0);
  private final IFeature categorical1 = new CategoricalFeature("Category", "value1");
  private final IFeature categorical2 = new CategoricalFeature("Category", "value2");
  private final IFeature randomFeature = new CategoricalFeature("Random", "random");

  private final IEntry entry = new EntryImpl(Arrays.asList(numerical1, boolean2,
          categorical1));
  private final IEntry entry2 = new EntryImpl(Arrays.asList(numerical2, boolean1,
          categorical2));
  private final IEntry randomEntry = new EntryImpl(Arrays.asList(numerical2, boolean1,
          randomFeature));
  private final IEntry queryEntry = new EntryImpl(Arrays.asList(numerical3, boolean1));

  private IReasoningModel emptyNB;
  private IReasoningModel testNB;

  private Map<String, Object> params = new HashMap<>();

  @BeforeEach
  void init() {
    emptyNB = new NaiveBayes("Category");
    testNB = new NaiveBayes("Category");
    Map<String, String[]> features = new HashMap<>();
    features.put("Number", null);
    features.put("Boolean", null);
    features.put("Category", new String[] {"value1", "value2", "value3"});
    testNB.parameterizeWithFlatFeatures(features);

    params.put("smoothing", "1");
  }

  @Test
  void testNBNullConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new NaiveBayes(null);
    });
  }

  @Test
  void testHasFlatFeatures() {
    assertTrue(this.testNB.hasFlatFeatures());
  }

  @Test
  void testParameterizeWithNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.emptyNB.parameterizeWithFlatFeatures(null);
    });
  }

  @Test
  void testParameterizeWithNoTargetClass() {
    Map<String, String[]> features = new HashMap<>();
    features.put("Number", null);
    features.put("Boolean", null);
    assertThrows(IllegalArgumentException.class, () -> {
      this.emptyNB.parameterizeWithFlatFeatures(features);
    });
  }

  @Test
  void testParameterize() {
    assertEquals("[]\n", emptyNB.toString());
    Map<String, String[]> features = new HashMap<>();
    features.put("Number", null);
    features.put("Boolean", null);
    features.put("Category", new String[] {"value1", "value2"});
    emptyNB.parameterizeWithFlatFeatures(features);
    assertEquals("[Category, Number, Boolean]\n", emptyNB.toString());
  }

  @Test
  void testTrainNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testNB.train(null);
    });
  }

  @Test
  void testTrainIncompatibleSize() {
    List<IFeature> testFeatures = this.entry.getFeatures();
    testFeatures.add(numerical2);
    IEntry testEntry = new EntryImpl(testFeatures);
    assertThrows(IllegalArgumentException.class, () -> {
      this.testNB.train(testEntry);
    });
  }

  @Test
  void testTrainRandomFeature() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testNB.train(randomEntry);
    });
  }

  @Test
  void testTrainOnce() {
    assertEquals("[Category, Number, Boolean]\n", testNB.toString());
    testNB.train(entry);
    assertEquals("[Number, Boolean, Category]\n" +
            "[2.5, TRUE, value1]\n", testNB.toString());
  }

  @Test
  void testTrainMultiple() {
    assertEquals("[Category, Number, Boolean]\n", testNB.toString());
    testNB.train(entry);
    testNB.train(entry2);
    assertEquals("[Number, Boolean, Category]\n" +
            "[2.5, TRUE, value1]\n" +
            "[5.0, FALSE, value2]\n", testNB.toString());
  }

  @Test
  void testQueryWithNullEntry() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testNB.queryWithParams(null, params);
    });
  }

  @Test
  void testQueryWithNullParams() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testNB.queryWithParams(queryEntry, null);
    });
  }

  @Test
  void testQueryOnEmptyModel() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testNB.queryWithParams(queryEntry, params);
    });
  }

  @Test
  void testQueryWithEntryIncludingTargetClass() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testNB.queryWithParams(entry, params);
    });
  }

  @Test
  void testQueryWithTargetClass() {
    List<IFeature> features = new ArrayList<>(Arrays.asList(numerical1, categorical1));
    IEntry query = new EntryImpl(features);
    assertThrows(IllegalArgumentException.class, () -> {
      this.testNB.queryWithParams(query, params);
    });
  }

  @Test
  void testQueryWithNoSmoothing() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testNB.queryWithParams(queryEntry, new HashMap<>());
    });
  }

  @Test
  void testQueryWithNegativeSmoothing() {
    params.put("smoothing", "-3");
    assertThrows(IllegalArgumentException.class, () -> {
      this.testNB.queryWithParams(queryEntry, params);
    });
  }

  /**
   * See NBDemo for a more in depth test for querying.
   */
  @Test
  void testQuery() {
    testNB.train(entry);
    testNB.train(entry2);
    assertEquals("value2", testNB.queryWithParams(queryEntry, params));
  }
}
