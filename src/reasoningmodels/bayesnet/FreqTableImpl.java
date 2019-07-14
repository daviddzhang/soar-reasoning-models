package reasoningmodels.bayesnet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreqTableImpl implements IFreqTable {
  Map<List<IRandomVariable>, Integer> hm;

  public FreqTableImpl(Map<List<IRandomVariable>, Integer> hm) {
    this.hm = hm;
  }

  public String printFreqTable() {
    String str = "";
    for (Map.Entry<List<IRandomVariable>, Integer> entry : this.hm.entrySet()) {
      str += entry.getKey().toString() + "| Frequencies: " + entry.getValue() + "\n";
    }
    return str;
  }

  // gets the frequency associated to a value
  public int getFreq(List<IRandomVariable> currentRow) {
    int frequency = -1;
    for (Map.Entry<List<IRandomVariable>, Integer> freqEntry : this.hm.entrySet()) {
      if (freqEntry.getKey().equals(currentRow)) {
        frequency = this.hm.get(freqEntry.getKey());
      }
    }
    return frequency;
  }

  @Override
  public Map<List<IRandomVariable>, Integer> getFreqTable() {
    // although this is poor practice, for this implementation, the node and frequencies are
    // closely interconnected and thus should have produce a mutable version of the frequencies
    return this.hm;
  }
}
