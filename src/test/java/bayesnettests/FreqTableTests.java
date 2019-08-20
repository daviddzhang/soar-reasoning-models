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
import reasoningmodels.bayesnet.FreqTableImpl;
import reasoningmodels.bayesnet.IFreqTable;
import reasoningmodels.bayesnet.IRandomVariable;
import reasoningmodels.bayesnet.RandomVariableImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the methods of the FreqTable class.
 */
public class FreqTableTests {
  private List<IRandomVariable> cList;
  private IFreqTable cFreq;
  private List<IRandomVariable> ePlusBPlus;
  private List<IRandomVariable> ePlusBMinus;
  private List<IRandomVariable> eMinusBMinus;
  private List<IRandomVariable> eMinusBPlus;
  private IFreqTable aFreq;
  private IFreqTable empty;

  @BeforeEach
  public void init() {
    IRandomVariable c = new RandomVariableImpl("C", true);
    this.cList = new ArrayList<>(Collections.singletonList(c));
    Map<List<IRandomVariable>, Integer> cInitFreq = new HashMap<>();
    cInitFreq.put(this.cList, 0);
    this.cFreq = new FreqTableImpl(cInitFreq);

    IRandomVariable bVarPlus = new RandomVariableImpl("B", true);
    IRandomVariable bVarMinus = new RandomVariableImpl("B", false);
    IRandomVariable eVarPlus = new RandomVariableImpl("E", true);
    IRandomVariable eVarMinus = new RandomVariableImpl("E", false);
    this.ePlusBPlus = new ArrayList<>(Arrays.asList(eVarPlus, bVarPlus));
    this.eMinusBPlus = new ArrayList<>(Arrays.asList(eVarMinus, bVarPlus));
    this.eMinusBMinus = new ArrayList<>(Arrays.asList(eVarMinus, bVarMinus));
    this.ePlusBMinus = new ArrayList<>(Arrays.asList(eVarPlus, bVarMinus));

    Map<List<IRandomVariable>, Integer> aInitFreq = new HashMap<>();
    aInitFreq.put(ePlusBPlus, 0);
    aInitFreq.put(eMinusBMinus, 0);
    aInitFreq.put(eMinusBPlus, 0);
    aInitFreq.put(ePlusBMinus, 0);

    this.aFreq = new FreqTableImpl(aInitFreq);
    this.empty = new FreqTableImpl(new HashMap<>());
  }

  @Test
  public void testNullConstructor() {
    assertThrows(IllegalArgumentException.class, () -> {
      new FreqTableImpl(null);
    });
  }

  @Test
  public void testPrintEmpty() {
    assertEquals("", this.empty.printFreqTable());
  }

  @Test
  public void testPrintSingleVar() {
    assertEquals("[+C]| Frequencies: 0\n", this.cFreq.printFreqTable());
  }

  @Test
  public void testPrintMultiVar() {
    assertEquals("[+E, +B]| Frequencies: 0\n" +
            "[-E, -B]| Frequencies: 0\n" +
            "[+E, -B]| Frequencies: 0\n" +
            "[-E, +B]| Frequencies: 0\n", this.aFreq.printFreqTable());
  }

  @Test
  public void testReplaceNullRow() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.cFreq.replace(null, 2);
    });
  }

  @Test
  public void testReplaceNegativeVal() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.cFreq.replace(eMinusBMinus, -3);
    });
  }

  @Test
  public void testReplaceEmpty() {
    this.empty.replace(this.eMinusBMinus, 20);
    assertEquals(this.empty, this.empty);
  }

  @Test
  public void testReplace() {
    this.aFreq.replace(this.eMinusBMinus, 1);
    assertEquals(1, this.aFreq.getFreq(eMinusBMinus));
  }

  @Test
  public void testGetFreqEmpty() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.empty.getFreq(this.eMinusBMinus);
    });
  }

  @Test
  public void testGetFreqNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      this.cFreq.getFreq(null);
    });
  }

  @Test
  public void testGetFreq() {
    this.aFreq.replace(this.eMinusBMinus, 20);
    assertEquals(20, this.aFreq.getFreq(this.eMinusBMinus));
  }

}
