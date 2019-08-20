package bayesnettests;

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
  public void init() {
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
  public void testNullConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new CPTImpl(null);
    });
  }

  @Test
  public void testPrintCPTEmpty() {
    assertEquals("", this.emptyCPT.printCPT());
  }

  @Test
  public void testPrintCPTSingleVar() {
    assertEquals("[+B]| Probability: 0.0\n", this.bCPT.printCPT());
  }

  @Test
  public void testPrintCPTMultiVar() {
    assertEquals("[+E, +B]| Probability: 0.0\n" +
            "[-E, -B]| Probability: 0.0\n" +
            "[+E, -B]| Probability: 0.0\n" +
            "[-E, +B]| Probability: 0.0\n", this.aCPT.printCPT());
  }

  @Test
  public void testReplaceNullRow() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.replace(null, 2.0);
    });
  }

  @Test
  public void testReplaceNegativeVal() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.replace(bVarPlusList, -.4);
    });
  }

  @Test
  public void testReplaceEmpty() {
    this.emptyCPT.replace(this.eMinusBMinus, .01);
    assertEquals(this.emptyCPT, this.emptyCPT);
  }

  @Test
  public void testReplace() {
    this.bCPT.replace(this.bVarPlusList, 1);
    assertEquals(1, this.bCPT.getQueryVar(bVarPlusList));
  }

  @Test
  public void testGetCPTEmptyCPT() {
    assertEquals(new HashMap<List<IRandomVariable>, Double>(), this.emptyCPT.getCPT());
  }

  @Test
  public void testGetCPTWithVars() {
    assertEquals(this.aInitCPT, this.aCPT.getCPT());
  }

  @Test
  public void testVariablesInEmptyCPT() {
    assertEquals(new ArrayList<>(), this.emptyCPT.variablesInCPT());
  }

  @Test
  public void testVariablesInOneCPT() {
    assertEquals(new ArrayList<>(Arrays.asList("B")), this.bCPT.variablesInCPT());
  }

  @Test
  public void testVariablesInTwoVarCPT() {
    assertEquals(new ArrayList<>(Arrays.asList("E", "B")), this.aCPT.variablesInCPT());
  }

  @Test
  public void testEliminateExceptNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.eliminateExcept(null);
    });
  }

  @Test
  public void testEliminateExceptEmpty() {
    List<String> bList = new ArrayList<>(Collections.singletonList("B"));
    assertEquals("", this.emptyCPT.eliminateExcept(bList).printCPT());
  }

  @Test
  public void testEliminateExceptOnlyVar() {
    List<String> bList = new ArrayList<>(Collections.singletonList("B"));
    assertEquals("[+B]| Probability: 0.0\n", this.bCPT.eliminateExcept(bList).printCPT());
  }

  @Test
  public void testEliminateExceptBVar() {
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
  public void testNormalizeEmpty() {
    this.emptyCPT.normalize();
    assertEquals(this.emptyCPT, this.emptyCPT);
  }

  @Test
  public void testNormalizeOneVar() {
    this.bCPT.replace(this.bVarPlusList, .25);
    this.bCPT.normalize();
    assertEquals(1.0, this.bCPT.getCPT().get(this.bVarPlusList), 001);
  }

  @Test
  public void testNormalizeMultiVarsSameDistribution() {
    this.aCPT.replace(this.eMinusBMinus, 1.0);
    this.aCPT.replace(this.eMinusBPlus, 1.0);
    this.aCPT.replace(this.ePlusBMinus, 1.0);
    this.aCPT.replace(this.ePlusBPlus, 1.0);
    this.aCPT.normalize();
    assertEquals(.25, this.aCPT.getCPT().get(this.eMinusBMinus), 001);
    assertEquals(.25, this.aCPT.getCPT().get(this.eMinusBPlus), 001);
    assertEquals(.25, this.aCPT.getCPT().get(this.ePlusBMinus), 001);
    assertEquals(.25, this.aCPT.getCPT().get(this.ePlusBPlus), 001);
  }

  @Test
  public void testNormalizeMultiVarsDiffDistribution() {
    this.aCPT.replace(this.eMinusBMinus, .75);
    this.aCPT.replace(this.eMinusBPlus, 1.0);
    this.aCPT.replace(this.ePlusBMinus, .25);
    this.aCPT.replace(this.ePlusBPlus, 1.0);
    this.aCPT.normalize();
    assertEquals(.25, this.aCPT.getCPT().get(this.eMinusBMinus), 001);
    assertEquals(.333, this.aCPT.getCPT().get(this.eMinusBPlus), .001);
    assertEquals(.08333, this.aCPT.getCPT().get(this.ePlusBMinus), .001);
    assertEquals(.333, this.aCPT.getCPT().get(this.ePlusBPlus), .001);
  }

  @Test
  public void testGetQueryVarEmpty() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.emptyCPT.getQueryVar(new ArrayList<>());
    });
  }

  @Test
  public void testGetQueryVarSingleVar() {
    this.bCPT.replace(this.bVarPlusList, .25);
    assertEquals(.25, this.bCPT.getQueryVar(this.bVarPlusList));
  }

  @Test
  public void testGetQueryVarMultiVar() {
    this.aCPT.replace(this.eMinusBMinus, 1.0);
    this.aCPT.replace(this.eMinusBPlus, 1.0);
    this.aCPT.replace(this.ePlusBMinus, 1.0);
    this.aCPT.replace(this.ePlusBPlus, 1.0);
    assertEquals(1.0, this.aCPT.getQueryVar(this.eMinusBMinus));
  }

  @Test
  public void testGetQueryVarWhenInputIsNotInCPT() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.getQueryVar(this.eMinusBMinus);
    });
  }

  @Test
  public void testGetQueryVarNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.getQueryVar(null);
    });
  }

  @Test
  public void testToInferenceCPTEmpty() {
    assertEquals("", this.emptyCPT.toInferenceCPT("Z").printCPT());
  }

  @Test
  public void testToInferenceCPTSingleVar() {
    assertEquals("[-B]| Probability: 1.0\n" +
            "[+B]| Probability: 0.0\n", this.bCPT.toInferenceCPT("B").printCPT());
  }

  @Test
  public void testToInferenceCPTSingleVar2() {
    assertEquals("[-B]| Probability: 1.0\n" +
            "[+B]| Probability: 0.0\n", this.bCPT.toInferenceCPT("C").printCPT());
  }

  @Test
  public void testToInferenceCPTMultiVar() {
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
  public void testToInferenceCPTNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.bCPT.toInferenceCPT(null);
    });
  }

}
