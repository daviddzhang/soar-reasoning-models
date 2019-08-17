package reasoningmodels.bayesnet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the IFreqTable interface. It keeps track of counts as a mapping of lists of
 * random variables to integers. Specific details are documented below.
 */
public class FreqTableImpl implements IFreqTable, Serializable {
  private final Map<List<IRandomVariable>, Integer> hm;

  /**
   * Constructs an instance of FreqTableImpl. Requires a mapping of random variable entries to
   * integers. This will most likely be the initial structure of the table.
   *
   * @param hm the mapping to construct the table with
   * @throws IllegalArgumentException if the provided mapping is null
   */
  public FreqTableImpl(Map<List<IRandomVariable>, Integer> hm) {
    if (hm == null) {
      throw new IllegalArgumentException("Provided mapping cannot be null.");
    }
    this.hm = hm;
  }

  /**
   * Formats the CPT as follows for each entry in the CPT: [Random Variables] | Frequencies: x
   */
  @Override
  public String printFreqTable() {
    StringBuilder str = new StringBuilder();
    for (Map.Entry<List<IRandomVariable>, Integer> entry : this.hm.entrySet()) {
      str.append(entry.getKey().toString()).append("| Frequencies: ").append(entry.getValue()).append("\n");
    }
    return str.toString();
  }

  @Override
  public int getFreq(List<IRandomVariable> currentRow) {
    if (currentRow == null) {
      throw new IllegalArgumentException("Given row cannot be null");
    }
    for (Map.Entry<List<IRandomVariable>, Integer> freqEntry : this.hm.entrySet()) {
      if (freqEntry.getKey().equals(currentRow)) {
        return this.hm.get(freqEntry.getKey());
      }
    }
    throw new IllegalArgumentException("Given row is not in the frequency table");
  }

  @Override
  public Map<List<IRandomVariable>, Integer> getFreqTable() {
    return new HashMap<>(this.hm);
  }

  @Override
  public void replace(List<IRandomVariable> rowToReplace, int newVal) throws IllegalArgumentException {
    if (rowToReplace == null) {
      throw new IllegalArgumentException("Row to replace cannot be null.");
    }

    if (newVal < 0) {
      throw new IllegalArgumentException("Negative count cannot be in CPT.");
    }
    this.hm.replace(rowToReplace, newVal);
  }
}
