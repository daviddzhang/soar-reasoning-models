package reasoningmodels;

import reasoningmodels.bayesnet.INode;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

public interface IReasoningModel {
  void addNode(INode node);

  void addFeature(String feature, String[] enumerations);

  void train(IEntry entry);

  String query(IEntry query);

  String query(IEntry query, int k) throws UnsupportedOperationException;

  double queryProbability(IEntry query, IEntry evidence) throws UnsupportedOperationException;
}
