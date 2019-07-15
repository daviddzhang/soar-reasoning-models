package reasoningmodels.classifiers;

import java.util.List;

import reasoningmodels.knn.DistanceFunction;

public interface IEntry {

  /**
   * Calculates the overall distance between two examples with the given distance function. It
   * will iterate through this entry to calculate the distance with the other entry. Thus, this
   * method will most often be called on the example to be classified with training examples
   * being parameters.
   *
   * @param distanceFunction the distance function to use
   * @param other the other entry (most likely a training example)
   * @return the calculated distance between the two entries
   */
  double calcDistance(DistanceFunction distanceFunction, IEntry other);

  List<IFeature> getFeatures();

  boolean containsFeature(IFeature feature);

  String toString();

}
