package reasoningmodels.knn;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.List;


public class EntryImpl implements IEntry {
  List<IFeature> features;

  @Override
  public double calcDistance(DistanceFunction distanceFunction, IEntry other) {
    switch (distanceFunction) {
      case EUCLIDEAN:
        double sum = 0.0;
        List<IFeature> otherFeatures = other.getFeatures();
        for (IFeature feature : this.features) {
          String currentFeature = feature.getFeatureName();

          // iterate through the other entry's features to check for the current feature the loop
          // is on for this entry
          for (int i = 0; i < otherFeatures.size(); i++) {
            if (otherFeatures.get(i).getFeatureName().equals(currentFeature)) {
              // use vector calculations if the feature is categorical
              if (feature.isCategorical()) {
                sum += new EuclideanDistance().compute(feature.getValueAsVector(),
                        otherFeatures.get(i).getValueAsVector());
              }
              else {
                sum += Math.pow(otherFeatures.get(i).getValue() - feature.getValue(), 2);
              }
            }
          }
        }

        double res = Math.sqrt(sum);

        return res;
      default:
        throw new IllegalArgumentException("Supplied distance function is not supported.");
    }
  }

  @Override
  public List<IFeature> getFeatures() {
    return this.features;
  }

  @Override
  public boolean containsFeature(IFeature feature) {
    for (IFeature curFeature : this.features) {
      String curFeatureName = curFeature.getFeatureName();
      String otherFeatureName = feature.getFeatureName();
      if (curFeatureName.equals(otherFeatureName)) {
        return true;
      }
    }
    return false;
  }

}
