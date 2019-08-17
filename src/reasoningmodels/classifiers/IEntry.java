package reasoningmodels.classifiers;

import java.util.List;

/**
 * This interface represents an entry/training example for a reasoning model. Implementing
 * classes must be able to return its features as a list of IFeatures.
 */
public interface IEntry {
  /**
   * Gets the features that this instance of an IEntry holds.
   *
   * @return a list of features
   */
  List<IFeature> getFeatures();

  /**
   * Checks if this entry contains the given feature (feature name).
   *
   * @param feature feature to check
   * @return true if it contains the feature, false otherwise
   */
  boolean containsFeature(String feature);

  /**
   * Specific implementations of IEntry should specify how its string representation is formatted.
   */
  String toString();

}
