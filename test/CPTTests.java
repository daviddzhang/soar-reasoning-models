import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.bayesnet.CPTImpl;
import reasoningmodels.bayesnet.ICPT;
import reasoningmodels.bayesnet.IRandomVariable;
import reasoningmodels.bayesnet.RandomVariableImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the methods of the CPTImpl class.
 */
public class CPTTests {

  private List<IRandomVariable> bVarPlusList;

  private Map<List<IRandomVariable>, Double> aInitCPT;
  private List<IRandomVariable> ePlusBPlus;
  private List<IRandomVariable> ePlusBMinus;
  private List<IRandomVariable> eMinusBMinus;
  private List<IRandomVariable> eMinusBPlus;

  private ICPT bCPT;
  private ICPT aCPT;
  private ICPT emptyCPT;

  @BeforeEach
  void init() {
    IRandomVariable bVarPlus = new RandomVariableImpl("B", true);
    this.bVarPlusList = new ArrayList<>(Arrays.asList(bVarPlus));
    Map<List<IRandomVariable>, Double> bInitCPT = new HashMap<>();
    bInitCPT.put(this.bVarPlusList, 0.0);
    this.bCPT = new CPTImpl(bInitCPT);

    IRandomVariable bVarMinus = new RandomVariableImpl("B", false);
    List<IRandomVariable> bVarMinusList = new ArrayList<>(Arrays.asList(bVarMinus));
    IRandomVariable eVarPlus = new RandomVariableImpl("E", true);
    IRandomVariable eVarMinus = new RandomVariableImpl("E", false);
    this.ePlusBPlus = new ArrayList<>(Arrays.asList(eVarPlus, bVarPlus));
    this.eMinusBPlus = new ArrayList<>(Arrays.asList(eVarMinus, bVarPlus));
    this.eMinusBMinus = new ArrayList<>(Arrays.asList(eVarMinus, bVarMinus));
    this.ePlusBMinus = new ArrayList<>(Arrays.asList(eVarPlus, bVarMinus));
    aInitCPT = new HashMap<>();
    aInitCPT.put(this.ePlusBPlus, 0.0);
    aInitCPT.put(this.eMinusBPlus, 0.0);
    aInitCPT.put(this.eMinusBMinus, 0.0);
    aInitCPT.put(this.ePlusBMinus, 0.0);

    this.aCPT = new CPTImpl(aInitCPT);

    Map<List<IRandomVariable>, Double> bInfCPT = new HashMap<>();
    bInfCPT.put(bVarMinusList, 1.0);
    bInfCPT.put(this.bVarPlusList, 0.0);

    this.emptyCPT = new CPTImpl(new HashMap<>());
  }

  @Test
  void testNullConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new CPTImpl(null);
    });
  }

  @Test
  void testPrintCPTEmpty() {
    assertEquals("", this.emptyCPT.printCPT());
  }

  @Test
  void testPrintCPTSingleVar() {
    assertEquals("[+B]| Probability: 0.0\n", this.bCPT.printCPT());
  }

  @Test
  void testPrintCPTMultiVar() {
    assertEquals("[+E, +B]| Probability: 0.0\n" +
            "[-E, -B]| Probability: 0.0\n" +
            "[+E, -B]| Probability: 0.0\n" +
            "[-E, +B]| Probability: 0.0\n", this.aCPT.printCPT());
  }

  @Test
  void testReplaceNullRow() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.replace(null, 2.0);
    });
  }

  @Test
  void testReplaceNegativeVal() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.replace(bVarPlusList, -.4);
    });
  }

  @Test
  void testReplaceEmpty() {
    this.emptyCPT.replace(this.eMinusBMinus, .01);
    assertEquals(this.emptyCPT, this.emptyCPT);
  }

  @Test
  void testReplace() {
    this.bCPT.replace(this.bVarPlusList, 1);
    assertEquals(1, this.bCPT.getQueryVar(bVarPlusList));
  }

  @Test
  void testGetCPTEmptyCPT() {
    assertEquals(new HashMap<List<IRandomVariable>, Double>(), this.emptyCPT.getCPT());
  }

  @Test
  void testGetCPTWithVars() {
    assertEquals(this.aInitCPT, this.aCPT.getCPT());
  }

  @Test
  void testVariablesInEmptyCPT() {
    assertEquals(new ArrayList<>(), this.emptyCPT.variablesInCPT());
  }

  @Test
  void testVariablesInOneCPT() {
    assertEquals(new ArrayList<>(Arrays.asList("B")), this.bCPT.variablesInCPT());
  }

  @Test
  void testVariablesInTwoVarCPT() {
    assertEquals(new ArrayList<>(Arrays.asList("E", "B")), this.aCPT.variablesInCPT());
  }

  @Test
  void testEliminateExceptNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.eliminateExcept(null);
    });
  }

  @Test
  void testEliminateExceptEmpty() {
    List<String> bList = new ArrayList<>(Collections.singletonList("B"));
    assertEquals("", this.emptyCPT.eliminateExcept(bList).printCPT());
  }

  @Test
  void testEliminateExceptOnlyVar() {
    List<String> bList = new ArrayList<>(Collections.singletonList("B"));
    assertEquals("[+B]| Probability: 0.0\n", this.bCPT.eliminateExcept(bList).printCPT());
  }

  @Test
  void testEliminateExceptBVar() {
    List<String> bList = new ArrayList<>(Collections.singletonList("B"));
    this.aCPT.replace(this.eMinusBMinus, .5);
    this.aCPT.replace(this.ePlusBMinus, .5);
    assertEquals("[+E, +B]| Probability: 0.0\n" +
            "[-E, -B]| Probability: 0.5\n" +
            "[+E, -B]| Probability: 0.5\n" +
            "[-E, +B]| Probability: 0.0\n", this.aCPT.printCPT());
    assertEquals("[-B]| Probability: 1.0\n" +
            "[+B]| Probability: 0.0\n", this.aCPT.eliminateExcept(bList).printCPT());
  }

  @Test
  void testNormalizeEmpty() {
    this.emptyCPT.normalize();
    assertEquals(this.emptyCPT, this.emptyCPT);
  }

  @Test
  void testNormalizeOneVar() {
    this.bCPT.replace(this.bVarPlusList, .25);
    this.bCPT.normalize();
    assertEquals(1.0, this.bCPT.getCPT().get(this.bVarPlusList));
  }

  @Test
  void testNormalizeMultiVarsSameDistribution() {
    this.aCPT.replace(this.eMinusBMinus, 1.0);
    this.aCPT.replace(this.eMinusBPlus, 1.0);
    this.aCPT.replace(this.ePlusBMinus, 1.0);
    this.aCPT.replace(this.ePlusBPlus, 1.0);
    this.aCPT.normalize();
    assertEquals(.25, this.aCPT.getCPT().get(this.eMinusBMinus));
    assertEquals(.25, this.aCPT.getCPT().get(this.eMinusBPlus));
    assertEquals(.25, this.aCPT.getCPT().get(this.ePlusBMinus));
    assertEquals(.25, this.aCPT.getCPT().get(this.ePlusBPlus));
  }

  @Test
  void testNormalizeMultiVarsDiffDistribution() {
    this.aCPT.replace(this.eMinusBMinus, .75);
    this.aCPT.replace(this.eMinusBPlus, 1.0);
    this.aCPT.replace(this.ePlusBMinus, .25);
    this.aCPT.replace(this.ePlusBPlus, 1.0);
    this.aCPT.normalize();
    assertEquals(.25, this.aCPT.getCPT().get(this.eMinusBMinus));
    assertEquals(.333, this.aCPT.getCPT().get(this.eMinusBPlus), .001);
    assertEquals(.08333, this.aCPT.getCPT().get(this.ePlusBMinus), .001);
    assertEquals(.333, this.aCPT.getCPT().get(this.ePlusBPlus), .001);
  }

  @Test
  void testGetQueryVarEmpty() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.emptyCPT.getQueryVar(new ArrayList<>());
    });
  }

  @Test
  void testGetQueryVarSingleVar() {
    this.bCPT.replace(this.bVarPlusList, .25);
    assertEquals(.25, this.bCPT.getQueryVar(this.bVarPlusList));
  }

  @Test
  void testGetQueryVarMultiVar() {
    this.aCPT.replace(this.eMinusBMinus, 1.0);
    this.aCPT.replace(this.eMinusBPlus, 1.0);
    this.aCPT.replace(this.ePlusBMinus, 1.0);
    this.aCPT.replace(this.ePlusBPlus, 1.0);
    assertEquals(1.0, this.aCPT.getQueryVar(this.eMinusBMinus));
  }

  @Test
  void testGetQueryVarWhenInputIsNotInCPT() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.getQueryVar(this.eMinusBMinus);
    });
  }

  @Test
  void testGetQueryVarNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.getQueryVar(null);
    });
  }

  @Test
  void testToInferenceCPTEmpty() {
    assertEquals("", this.emptyCPT.toInferenceCPT("Z").printCPT());
  }

  @Test
  void testToInferenceCPTSingleVar() {
    assertEquals("[-B]| Probability: 1.0\n" +
            "[+B]| Probability: 0.0\n", this.bCPT.toInferenceCPT("B").printCPT());
  }

  @Test
  void testToInferenceCPTSingleVar2() {
    assertEquals("[-B]| Probability: 1.0\n" +
            "[+B]| Probability: 0.0\n", this.bCPT.toInferenceCPT("C").printCPT());
  }

  @Test
  void testToInferenceCPTMultiVar() {
    assertEquals("[+E, +B, -A]| Probability: 1.0\n" +
            "[-E, -B, -A]| Probability: 1.0\n" +
            "[-E, +B, +A]| Probability: 0.0\n" +
            "[-E, +B, -A]| Probability: 1.0\n" +
            "[+E, -B, +A]| Probability: 0.0\n" +
            "[+E, +B, +A]| Probability: 0.0\n" +
            "[-E, -B, +A]| Probability: 0.0\n" +
            "[+E, -B, -A]| Probability: 1.0\n", this.aCPT.toInferenceCPT("A").printCPT());
  }

  @Test
  void testToInferenceCPTNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.toInferenceCPT(null);
    });
  }

}
