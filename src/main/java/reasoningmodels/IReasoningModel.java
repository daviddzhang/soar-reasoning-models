package reasoningmodels;

import java.util.List;
import java.util.Map;

import reasoningmodels.bayesnet.INode;
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
   * Returns a model query result given a set of parameters. Specific models will require
   * different parameters and should throw an exception if it cannot find the necessary
   * parameters. If the query result is a probability, it will return a string version of the
   * number.
   *
   * @param queryEntry features to query for
   * @param queryParams parameters for the query
   * @return the query result
   */
  String queryWithParams(IEntry queryEntry, Map<String, Object> queryParams)
          throws IllegalArgumentException;

  /**
   * Returns whether this reasoning model requires flat features. Some models might need
   * graphical features.
   *
   * @return true if the model requires flat features, false otherwise.
   */
  boolean hasFlatFeatures();

  /**
   * Sets the model's features as the given flat features. This should only be called on models
   * that have flat features.
   *
   * @param features features for the model
   */
  void parameterizeWithFlatFeatures(Map<String, String[]> features);

  /**
   * Sets the model's features as the given graphical features. This should only be called on
   * models that have graphical features.
   *
   * @param nodes nodes/features for the model
   */
  void parameterizeWithGraphicalFeatures(List<INode> nodes);
}
