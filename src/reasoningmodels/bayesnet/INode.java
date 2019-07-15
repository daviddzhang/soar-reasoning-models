package reasoningmodels.bayesnet;

import java.util.List;

public interface INode {
  String toString();

  void initializeCPT();

  void initializeFreqTable();

  void initializeRelFreqTable();

  void updateCPT();

  void updateFrequency(List<IRandomVariable> trainingEx);

  void updateRelFrequency(List<IRandomVariable> trainingEx);

  String getNodeName();

  INode convertToInferenceCPT();

  boolean hasParent(String parent);

  boolean hasNoParents();

  ICPT join(ICPT other, String joinVar);

  ICPT getCPT();

}
