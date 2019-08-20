package bayesnettests;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.bayesnet.BayesNet;
import reasoningmodels.bayesnet.INode;
import reasoningmodels.bayesnet.NodeImpl;
import reasoningmodels.classifiers.BooleanFeature;
import reasoningmodels.classifiers.EntryImpl;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for methods of the BayesNet class.
 */
public class BayesNetTest {
  private IReasoningModel emptyBayes = new BayesNet();
  private IReasoningModel testBayes;
  private INode e;
  private INode b;
  private INode a;
  private INode j;
  private List<INode> nodes;
  private IFeature eFeature = new BooleanFeature("E", 1.0);
  private IFeature bFeature = new BooleanFeature("B", 0.0);
  private IFeature aFeature = new BooleanFeature("A", 1.0);
  private IFeature jFeature = new BooleanFeature("J", 0.0);
  private IEntry varsAsEntry = new EntryImpl(new ArrayList<>(Arrays.asList(this.eFeature,
          this.bFeature,
          this.aFeature, this.jFeature)));

  private IFeature eFeature2 = new BooleanFeature("E", 0.0);
  private IFeature bFeature2 = new BooleanFeature("B", 1.0);
  private IFeature aFeature2 = new BooleanFeature("A", 0.0);
  private IFeature jFeature2 = new BooleanFeature("J", 1.0);
  private IEntry varsAsEntry2 = new EntryImpl(new ArrayList<>(Arrays.asList(this.eFeature2,
          this.bFeature2,
          this.aFeature2, this.jFeature2)));

  private IFeature zFeature = new BooleanFeature("Z", 1.0);
  private IEntry zEntry = new EntryImpl(new ArrayList<>(Collections.singletonList(this.zFeature)));

  private IEntry evidenceEntry =
          new EntryImpl(new ArrayList<>(Collections.singletonList(this.jFeature2)));
  private Map<String, Object> targetVarParam = new HashMap<>();

  @BeforeEach
  public void testInit() {
    this.testBayes = new BayesNet();
    this.e = new NodeImpl("E", new ArrayList<>());
    this.b = new NodeImpl("B", new ArrayList<>());
    this.a = new NodeImpl("A", new ArrayList<>(Arrays.asList("E", "B")));
    this.j = new NodeImpl("J", new ArrayList<>(Collections.singletonList("A")));
    this.nodes = new ArrayList<>(Arrays.asList(this.e, this.b, this.a, this.j));
    this.testBayes.parameterizeWithGraphicalFeatures(this.nodes);
    targetVarParam.put("target-vars",new ArrayList<>(Collections.singletonList(new ImmutablePair<>(
            "B",
                    "1.0"))));
  }

  @Test
  public void testHasFlatFeatures() {
    assertFalse(this.testBayes.hasFlatFeatures());
  }

  @Test
  public void testParameterizeWithFlatFeatures() {
    assertThrows(UnsupportedOperationException.class, () -> {
      this.testBayes.parameterizeWithFlatFeatures(new HashMap<>());
    });
  }

  @Test
  public void testParameterizeWithGraphicalFeaturesNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testBayes.parameterizeWithGraphicalFeatures(null);
    });
  }

  @Test
  public void testParameterizeWithGraphicalFeatures() {
    assertEquals("[]", this.emptyBayes.toString());
    this.emptyBayes.parameterizeWithGraphicalFeatures(this.nodes);
    assertEquals("[E, []\n" +
            "CPT: \n" +
            "[+E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            ", B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            ", A, [E, B]\n" +
            "CPT: \n" +
            "[+B, -E]| Probability: 0.0\n" +
            "[-B, +E]| Probability: 0.0\n" +
            "[+B, +E]| Probability: 0.0\n" +
            "[-B, -E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            ", J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.0\n" +
            "[+A]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "]", this.emptyBayes.toString());
  }

  @Test
  public void testTrainNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testBayes.train(null);
    });
  }

  @Test
  public void testTrainOnEmptyNetHasNoEffect() {
    assertEquals("[]", this.emptyBayes.toString());
    this.emptyBayes.train(this.varsAsEntry);
    assertEquals("[]", this.emptyBayes.toString());
  }

  @Test
  public void testTrainAllVars() {
    assertEquals("[E, []\n" +
            "CPT: \n" +
            "[+E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            ", B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            ", A, [E, B]\n" +
            "CPT: \n" +
            "[+B, -E]| Probability: 0.0\n" +
            "[-B, +E]| Probability: 0.0\n" +
            "[+B, +E]| Probability: 0.0\n" +
            "[-B, -E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            ", J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.0\n" +
            "[+A]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "]", this.testBayes.toString());
    this.testBayes.train(this.varsAsEntry);
    assertEquals("[E, []\n" +
            "CPT: \n" +
            "[+E]| Probability: 1.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E]| Frequencies: 1\n" +
            ", B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            ", A, [E, B]\n" +
            "CPT: \n" +
            "[+B, -E]| Probability: 0.0\n" +
            "[-B, +E]| Probability: 1.0\n" +
            "[+B, +E]| Probability: 0.0\n" +
            "[-B, -E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 1\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 1\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            ", J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.0\n" +
            "[+A]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "]", this.testBayes.toString());
  }

  @Test
  public void testTrainWithVarNotInNetHasNoEffect() {
    assertEquals("[E, []\n" +
            "CPT: \n" +
            "[+E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            ", B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            ", A, [E, B]\n" +
            "CPT: \n" +
            "[+B, -E]| Probability: 0.0\n" +
            "[-B, +E]| Probability: 0.0\n" +
            "[+B, +E]| Probability: 0.0\n" +
            "[-B, -E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            ", J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.0\n" +
            "[+A]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "]", this.testBayes.toString());
    this.testBayes.train(this.zEntry);
    assertEquals("[E, []\n" +
            "CPT: \n" +
            "[+E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            ", B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            ", A, [E, B]\n" +
            "CPT: \n" +
            "[+B, -E]| Probability: 0.0\n" +
            "[-B, +E]| Probability: 0.0\n" +
            "[+B, +E]| Probability: 0.0\n" +
            "[-B, -E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            ", J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.0\n" +
            "[+A]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "]", this.testBayes.toString());
  }

  @Test
  public void testMultiTrains() {
    assertEquals("[E, []\n" +
            "CPT: \n" +
            "[+E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            ", B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            ", A, [E, B]\n" +
            "CPT: \n" +
            "[+B, -E]| Probability: 0.0\n" +
            "[-B, +E]| Probability: 0.0\n" +
            "[+B, +E]| Probability: 0.0\n" +
            "[-B, -E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            ", J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.0\n" +
            "[+A]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "]", this.testBayes.toString());
    this.testBayes.train(this.varsAsEntry);
    this.testBayes.train(this.varsAsEntry2);
    assertEquals("[E, []\n" +
            "CPT: \n" +
            "[+E]| Probability: 0.5\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E]| Frequencies: 2\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E]| Frequencies: 1\n" +
            ", B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.5\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 2\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 1\n" +
            ", A, [E, B]\n" +
            "CPT: \n" +
            "[+B, -E]| Probability: 0.0\n" +
            "[-B, +E]| Probability: 1.0\n" +
            "[+B, +E]| Probability: 0.0\n" +
            "[-B, -E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B, -E]| Frequencies: 1\n" +
            "[-B, +E]| Frequencies: 1\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 1\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            ", J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 1.0\n" +
            "[+A]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 1\n" +
            "[+A]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 1\n" +
            "[+A]| Frequencies: 0\n" +
            "]", this.testBayes.toString());
  }

  @Test
  public void testQueryWithVarsNotInNet() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testBayes.queryWithParams(this.zEntry, this.targetVarParam);
    });
  }

  @Test
  public void testQueryWithNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.testBayes.queryWithParams(null, null);
    });
  }

  @Test
  public void testQueryNoTrain() {
    assertEquals("0.0", this.testBayes.queryWithParams(evidenceEntry, targetVarParam));
  }

  @Test
  public void testQueryAfterTrain() {
    assertEquals("0.0", this.testBayes.queryWithParams(evidenceEntry, targetVarParam));
    this.testBayes.train(varsAsEntry);
    this.testBayes.train(varsAsEntry2);
    assertEquals("0.5", this.testBayes.queryWithParams(evidenceEntry, targetVarParam));
  }
}
