package reasoningmodels;

import java.util.Map;

import reasoningmodels.classifiers.IEntry;

public interface IReasoningModel {
  /**
   * Provides an example to train the reasoning model. Specifics of how the model uses the
   * example is up to the implementation of the reasoning model.
   *
   * @param entry to train the model with
   */
  void train(IEntry entry);

  String queryWithParams(IEntry queryEntry, Map<String, Object> queryParams);
}
