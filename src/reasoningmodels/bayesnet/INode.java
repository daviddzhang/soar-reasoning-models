package reasoningmodels.bayesnet;

public interface INode {
  String toString();

  void initializeCPT();

  void initializeFreqTable();

  void initializeRelFreqTable();

  void updateCPT();

  void updateFrequency();

  void updateRelFrequency();

  void getNodeName();

  INode convertToInferenceCPT();

  boolean hasParent(String parent);

  boolean hasNoParents();

  ICPT join(ICPT other, String joinVar);

}
