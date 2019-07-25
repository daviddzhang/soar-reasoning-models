package reasoningmodels;

import reasoningmodels.classifiers.IEntry;

public interface IReasoningModel {
  /**
   * Provides an example to train the reasoning model. Specifics of how the model uses the
   * example is up to the implementation of the reasoning model.
   *
   * @param entry to train the model with
   */
  void train(IEntry entry);

  /**
   * Queries the reasoning model based on the given entry, which will most likely lack the target
   * class as a feature. Should only be supported by classifiers.
   *
   * @param query the entry to query the model with
   * @return the resulting class
   */
  String query(IEntry query);

  /**
   * Queries the reasoning model based on the given entry, which will most likely lack the target
   * class as a feature. Also takes in a double for smoothing. Should only be supported by
   * classifiers that require smoothing.
   *
   * @param query the entry to query the model with
   * @param smoothing the smoothing value
   * @return the resulting class
   */
  String query(IEntry query, double smoothing);

  /**
   * Queries the reasoning model based on the given entry, which will most likely lack the target
   * class as a feature. Also takes in an int for k, or the number of neighbors. Should only be
   * supported by classifiers that require a k value.
   * @param query the entry to query the model with
   * @param k the k value/number of neighbors
   * @return the resulting class
   */
  String query(IEntry query, int k);


  double queryProbability(IEntry query, IEntry evidence);
}
