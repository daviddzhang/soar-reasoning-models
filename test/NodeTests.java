import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import reasoningmodels.bayesnet.INode;
import reasoningmodels.bayesnet.NodeImpl;
import reasoningmodels.bayesnet.RandomVariableImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for methods of NodeImpl
 */
public class NodeTests {
  private INode b;
  private INode a;
  private INode j;

  @BeforeEach
  void init() {
    a = new NodeImpl("A", new ArrayList<>(Arrays.asList("B", "E")));
    b = new NodeImpl("B", new ArrayList<>());
    j = new NodeImpl("J", new ArrayList<>(Collections.singletonList("A")));
  }

  @Test
  void testNullNameConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new NodeImpl(null, new ArrayList<>());
    });
  }

  @Test
  void testNullParentsConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new NodeImpl("B", null);
    });
  }

  @Test
  void testNoParentsConstructor() {
    assertEquals("B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n", b.toString());
  }

  @Test
  void testOneParentConstructor() {
    assertEquals("J, [A]\n" +
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
            "[+A]| Frequencies: 0\n", j.toString());
  }

  @Test
  void testMultiParentConstructor() {
    assertEquals("A, [B, E]\n" +
            "CPT: \n" +
            "[+E, +B]| Probability: 0.0\n" +
            "[-E, -B]| Probability: 0.0\n" +
            "[+E, -B]| Probability: 0.0\n" +
            "[-E, +B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E, +B]| Frequencies: 0\n" +
            "[-E, -B]| Frequencies: 0\n" +
            "[+E, -B]| Frequencies: 0\n" +
            "[-E, +B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E, +B]| Frequencies: 0\n" +
            "[-E, -B]| Frequencies: 0\n" +
            "[+E, -B]| Frequencies: 0\n" +
            "[-E, +B]| Frequencies: 0\n", a.toString());
  }

  @Test
  void testUpdateCPTNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.a.updateCPT(null);
    });
  }

  @Test
  void testUpdateCPTNoParentsOccur() {
    assertEquals("B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n", b.toString());
    this.b.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("B", true))));
    assertEquals("B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 1.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 1\n", b.toString());
  }

  @Test
  void testUpdateCPTNoParentsNotOccur() {
    assertEquals("B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n", b.toString());
    this.b.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("B", false))));
    assertEquals("B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n", b.toString());
  }

  @Test
  void testUpdateCPTNoParentsBoth() {
    assertEquals("B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n", b.toString());
    this.b.updateCPT(new ArrayList<>(Collections.singletonList(new RandomVariableImpl("B", false))));
    this.b.updateCPT(new ArrayList<>(Collections.singletonList(new RandomVariableImpl("B", true))));
    assertEquals("B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.5\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 2\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 1\n", b.toString());
  }

  @Test
  void testUpdateCPTSingleParentOccurThisOccur() {
    assertEquals("J, [A]\n" +
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
            "[+A]| Frequencies: 0\n", j.toString());
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", true),
            new RandomVariableImpl("A", true))));
    assertEquals("J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.0\n" +
            "[+A]| Probability: 1.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 1\n", j.toString());
  }

  @Test
  void testUpdateCPTSingleParentOccurThisNotOccur() {
    assertEquals("J, [A]\n" +
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
            "[+A]| Frequencies: 0\n", j.toString());
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", false),
            new RandomVariableImpl("A", true))));
    assertEquals("J, [A]\n" +
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
            "[+A]| Frequencies: 0\n", j.toString());
  }

  @Test
  void testUpdateCPTSingleParentNotOccurThisNotOccur() {
    assertEquals("J, [A]\n" +
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
            "[+A]| Frequencies: 0\n", j.toString());
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", false),
            new RandomVariableImpl("A", false))));
    assertEquals("J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.0\n" +
            "[+A]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 1\n" +
            "[+A]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n", j.toString());
  }

  @Test
  void testUpdateCPTSingleParentNotOccurThisOccur() {
    assertEquals("J, [A]\n" +
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
            "[+A]| Frequencies: 0\n", j.toString());
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", true),
            new RandomVariableImpl("A", false))));
    assertEquals("J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 1.0\n" +
            "[+A]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 1\n" +
            "[+A]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 1\n" +
            "[+A]| Frequencies: 0\n", j.toString());
  }

  @Test
  void testUpdateCPTSingleParentAll() {
    assertEquals("J, [A]\n" +
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
            "[+A]| Frequencies: 0\n", j.toString());
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", true),
            new RandomVariableImpl("A", false))));
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", true),
            new RandomVariableImpl("A", true))));
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", false),
            new RandomVariableImpl("A", false))));
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", false),
            new RandomVariableImpl("A", true))));
    assertEquals("J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.5\n" +
            "[+A]| Probability: 0.5\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 2\n" +
            "[+A]| Frequencies: 2\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 1\n" +
            "[+A]| Frequencies: 1\n", j.toString());
  }

  @Test
  void testUpdateCPTSingleParentExtraVarsHasNoEffect() {
    assertEquals("J, [A]\n" +
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
            "[+A]| Frequencies: 0\n", j.toString());
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", true),
            new RandomVariableImpl("A", false), new RandomVariableImpl("B", true))));
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", true),
            new RandomVariableImpl("A", true), new RandomVariableImpl("B", true))));
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", false),
            new RandomVariableImpl("A", false), new RandomVariableImpl("B", true))));
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", false),
            new RandomVariableImpl("A", true), new RandomVariableImpl("B", true))));
    assertEquals("J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.5\n" +
            "[+A]| Probability: 0.5\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 2\n" +
            "[+A]| Frequencies: 2\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 1\n" +
            "[+A]| Frequencies: 1\n", j.toString());
  }

  @Test
  void testToInferenceCPTNoParents() {
    assertEquals("B, []\n" +
            "CPT: \n" +
            "[-B]| Probability: 1.0\n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n", this.b.convertToInferenceCPT().toString());
  }

  @Test
  void testToInferenceCPTSingleParents() {
    assertEquals("J, [A]\n" +
            "CPT: \n" +
            "[-A, +J]| Probability: 0.0\n" +
            "[-A, -J]| Probability: 1.0\n" +
            "[+A, +J]| Probability: 0.0\n" +
            "[+A, -J]| Probability: 1.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n", this.j.convertToInferenceCPT().toString());
  }

  @Test
  void testToInferenceCPTMultiParent() {
    assertEquals("A, [B, E]\n" +
            "CPT: \n" +
            "[+E, +B, -A]| Probability: 1.0\n" +
            "[-E, -B, -A]| Probability: 1.0\n" +
            "[-E, +B, +A]| Probability: 0.0\n" +
            "[-E, +B, -A]| Probability: 1.0\n" +
            "[+E, -B, +A]| Probability: 0.0\n" +
            "[+E, +B, +A]| Probability: 0.0\n" +
            "[-E, -B, +A]| Probability: 0.0\n" +
            "[+E, -B, -A]| Probability: 1.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E, +B]| Frequencies: 0\n" +
            "[-E, -B]| Frequencies: 0\n" +
            "[+E, -B]| Frequencies: 0\n" +
            "[-E, +B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E, +B]| Frequencies: 0\n" +
            "[-E, -B]| Frequencies: 0\n" +
            "[+E, -B]| Frequencies: 0\n" +
            "[-E, +B]| Frequencies: 0\n", this.a.convertToInferenceCPT().toString());
  }

  @Test
  void testToInferenceCPTSingleParentsDiffVals() {
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", true),
            new RandomVariableImpl("A", false), new RandomVariableImpl("B", true))));
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", true),
            new RandomVariableImpl("A", true), new RandomVariableImpl("B", true))));
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", false),
            new RandomVariableImpl("A", false), new RandomVariableImpl("B", true))));
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", false),
            new RandomVariableImpl("A", true), new RandomVariableImpl("B", true))));
    this.j.updateCPT(new ArrayList<>(Arrays.asList(new RandomVariableImpl("J", false),
            new RandomVariableImpl("A", false), new RandomVariableImpl("B", true))));
    assertEquals("J, [A]\n" +
            "CPT: \n" +
            "[-A, +J]| Probability: 0.3333333333333333\n" +
            "[-A, -J]| Probability: 0.6666666666666667\n" +
            "[+A, +J]| Probability: 0.5\n" +
            "[+A, -J]| Probability: 0.5\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 3\n" +
            "[+A]| Frequencies: 2\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 1\n" +
            "[+A]| Frequencies: 1\n", this.j.convertToInferenceCPT().toString());
  }

  @Test
  void testHasParentNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.a.hasParent(null);
    });
  }

  @Test
  void testHasParentNoParents() {
    assertFalse(this.b.hasParent("C"));
  }

  @Test
  void testHasParentNoParents2() {
    assertFalse(this.b.hasParent(""));
  }

  @Test
  void testHasParentTrue() {
    assertTrue(this.a.hasParent("E"));
  }

  @Test
  void testHasParentFalse() {
    assertFalse(this.a.hasParent("Z"));
  }

//  @Test
//  void testJoin
}
