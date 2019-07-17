package reasoningmodels;

import reasoningmodels.bayesnet.INode;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

public interface IReasoningModel {
  void addNode(INode node);

  void addFeature(String feature, String[] enumerations);

  void train(IEntry entry);

  void query(IEntry query);

  void query(IEntry query, int k) throws UnsupportedOperationException;
}
