package reasoningmodels.classifiers;

import java.util.List;

import reasoningmodels.knn.IDistanceFunction;

public interface IEntry {
  List<IFeature> getFeatures();

  boolean containsFeature(String feature);

  String toString();

}
