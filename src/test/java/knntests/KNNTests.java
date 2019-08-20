package knntests;

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
import reasoningmodels.knn.KNN;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KNNTests {
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
  private final IEntry queryEntry = new EntryImpl(Arrays.asList(numerical3, boolean2));

  private IReasoningModel testKNN;
  private IReasoningModel emptyKNN;

  private Map<String, Object> params = new HashMap<>();

  @BeforeEach
  public void init() {
    testKNN = new KNN("Category");
    Map<String, String[]> features = new HashMap<>();
    features.put("Number", null);
    features.put("Boolean", null);
    features.put("Category", new String[] {"value1", "value2", "value3"});
    testKNN.parameterizeWithFlatFeatures(features);
    emptyKNN = new KNN("Category");

    params.put("k", "1");
    params.put("distance", "euclidean");
  }

  @Test
  public void testKNNNullConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new KNN(null);
    });
  }

  @Test
  public void testHasFlatFeatures() {
    assertTrue(this.testKNN.hasFlatFeatures());
  }

  @Test
  public void testParameterizeWithNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.emptyKNN.parameterizeWithFlatFeatures(null);
    });
  }

  @Test
  public void testParameterizeWithNoTargetClass() {
    Map<String, String[]> features = new HashMap<>();
    features.put("Number", null);
    features.put("Boolean", null);
    assertThrows(IllegalArgumentException.class, () -> {
      this.emptyKNN.parameterizeWithFlatFeatures(features);
    });
  }

  @Test
  public void testParameterize() {
    assertEquals("[]\n", emptyKNN.toString());
    Map<String, String[]> features = new HashMap<>();
    features.put("Number", null);
    features.put("Boolean", null);
    features.put("Category", new String[] {"value1", "value2"});
    emptyKNN.parameterizeWithFlatFeatures(features);
    assertEquals("[Category, Number, Boolean]\n", emptyKNN.toString());
  }

  @Test
  public void testTrainNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.train(null);
    });
  }

  @Test
  public void testTrainIncompatibleSize() {
    List<IFeature> testFeatures = this.entry.getFeatures();
    testFeatures.add(numerical2);
    IEntry testEntry = new EntryImpl(testFeatures);
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.train(testEntry);
    });
  }

  @Test
  public void testTrainRandomFeature() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.train(randomEntry);
    });
  }

  @Test
  public void testTrainOnce() {
    assertEquals("[Category, Number, Boolean]\n", testKNN.toString());
    testKNN.train(entry);
    assertEquals("[Number, Boolean, Category]\n" +
            "[2.5, TRUE, value1]\n", testKNN.toString());
  }

  @Test
  public void testTrainMultiple() {
    assertEquals("[Category, Number, Boolean]\n", testKNN.toString());
    testKNN.train(entry);
    testKNN.train(entry2);
    assertEquals("[Number, Boolean, Category]\n" +
            "[2.5, TRUE, value1]\n" +
            "[5.0, FALSE, value2]\n", testKNN.toString());
  }

  @Test
  public void testQueryWithNullEntry() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(null, params);
    });
  }

  @Test
  public void testQueryWithNullParams() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(queryEntry, null);
    });
  }

  @Test
  public void testQueryOnEmptyModel() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(queryEntry, params);
    });
  }

  @Test
  public void testQueryWithEntryIncludingTargetClass() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(entry, params);
    });
  }

  @Test
  public void testQueryWithKLargerThanExamplesSize() {
    params.put("k", "5");
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(entry, params);
    });
  }

  @Test
  public void testQueryWithEvenK() {
    params.put("k", "2");
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(entry, params);
    });
  }

  @Test
  public void testQueryWithKMultipleOfTargetDimension() {
    params.put("k", "3");
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(entry, params);
    });
  }

  @Test
  public void testQueryWithNegativeK() {
    params.put("k", "-4");
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(entry, params);
    });
  }

  @Test
  public void testQueryWithNoDistanceParam() {
    params.clear();
    params.put("k", "1");
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(entry, params);
    });
  }

  @Test
  public void testQueryWithNoKParam() {
    params.clear();
    params.put("distance", "euclidean");
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(entry, params);
    });
  }

  @Test
  public void testQueryWithTargetClass() {
    List<IFeature> features = new ArrayList<>(Arrays.asList(numerical1, categorical1));
    IEntry query = new EntryImpl(features);
    assertThrows(IllegalArgumentException.class, () -> {
      this.testKNN.queryWithParams(query, params);
    });
  }

  /**
   * See KNNDemo for more in-depth query test.
   */
  @Test
  public void testQuery() {
    testKNN.train(entry);
    testKNN.train(entry2);
    assertEquals("value1", testKNN.queryWithParams(queryEntry, params));
  }
}
