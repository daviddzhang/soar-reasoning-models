package reasoningmodels.bayesnet;

import java.util.List;
import java.util.Map;

public interface IFreqTable {

  String printFreqTable();

  int getFreq(List<IRandomVariable> entry);

  Map<List<IRandomVariable>, Integer> getFreqTable();
}
